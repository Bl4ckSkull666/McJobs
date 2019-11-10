/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.localization;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.util.TimeFormat;
import com.dmgkz.mcjobs.util.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class SpigotBuilds {
    public static void sendTopLine(Player p, String line, OfflinePlayer op) {
        TextComponent main = new TextComponent(line);
        String hoverText = "";
        if(op.isOnline()) {
            hoverText = McJobs.getPlugin().getLanguage().getJobNotify("is-online", p.getUniqueId()).addVariables("", op.getName(), "");
        } else if(op.hasPlayedBefore() && op.getLastPlayed() > 0) {
            hoverText = McJobs.getPlugin().getLanguage().getJobNotify("last-online", p.getUniqueId()).addVariables("", op.getName(), TimeFormat.getFormatedTime(p.getUniqueId(), (System.currentTimeMillis()-op.getLastPlayed())/1000));
        }
        
        if(!hoverText.isEmpty()) {
            HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', hoverText)).create());
            main.setHoverEvent(he);
        }
        p.spigot().sendMessage(main);
    }
    
    public static void sendJobButtons(Player p, String cmd) {
        TextComponent main = new TextComponent("");
        boolean firstAdd = false;
        for(String job: PlayerJobs.getJobsList().keySet()) {
            if(firstAdd)
                main.addExtra(" ");
            String jobMe = McJobs.getPlugin().getLanguage().getJobName(job, p.getUniqueId());
            main.addExtra(getCommandButton(jobMe, p, cmd.replace("%j", jobMe)));
            firstAdd = true;
        }
        p.spigot().sendMessage(main);
    }
    
    public static void sendJobList(Player p) {
        TextComponent main = new TextComponent("");
        int i = 0;
        for(Map.Entry<String, PlayerJobs> me: PlayerJobs.getJobsList().entrySet()) {
            String jobOriginal = me.getKey();
            String jobMe = ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobName(me.getKey(), p.getUniqueId()));
            
            if(PlayerData.hasJob(p.getUniqueId(), jobOriginal) && !me.getValue().getData().compJob().isDefault())
                main.addExtra(getInfoButton(jobMe, p, ChatColor.RED));
            else if(!PlayerData.isJoinable(p.getUniqueId(), jobOriginal))
                main.addExtra(getInfoButton(jobMe, p, ChatColor.DARK_GRAY));
            else if((p.hasPermission("mcjobs.jobsavail." + jobOriginal) || p.hasPermission("mcjobs.jobsavail.all") || !(McJobs.getPlugin().getConfig().getBoolean("advanced.usePerms"))) && !me.getValue().getData().compJob().isDefault())
                main.addExtra(getInfoButton(jobMe, p, ChatColor.GOLD));
            else if(me.getValue().getData().compJob().isDefault())
                main.addExtra(getInfoButton(jobMe, p, ChatColor.DARK_AQUA));
            else
                main.addExtra(getInfoButton(jobMe, p, ChatColor.DARK_GRAY));
            
            i++;
            if(i < PlayerJobs.getJobsList().size())
                main.addExtra(new TextComponent(ChatColor.GRAY + ", "));
            else
                main.addExtra(new TextComponent(ChatColor.GRAY + "."));
        }
        p.spigot().sendMessage(main);
    }
    
    public static void sendHelpPage(Player p, int page) {
        int spaces = 55;
        TextComponent tcmain = new TextComponent("");
        TextComponent tcprev = new TextComponent("");
        if(page > 1) {
            tcprev = new TextComponent(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobHelp("prevpage", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page - 1))));
            spaces -= ChatColor.stripColor(tcprev.getText()).length();
            tcprev.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobHelp("command", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page - 1))))));
            tcmain.addExtra(tcprev);
        }
                
        TextComponent tcnext = new TextComponent(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobHelp("nextpage", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page + 1))));
        spaces -= ChatColor.stripColor(tcnext.getText()).length();
        tcnext.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobHelp("command", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page + 1))))));

        for(int i = 0; i < spaces; i++)
            tcmain.addExtra(" ");
        tcmain.addExtra(tcnext);
        p.spigot().sendMessage(tcmain);
    }
    
    public static void getLanguageList(Player p) {
        TextComponent tcmain = new TextComponent("");
        boolean isUse = false;
        for(String langOriginal: McJobs.getPlugin().getLanguage().getAvaLangs()) {
            if(isUse)
                tcmain.addExtra(ChatColor.GRAY + ", ");
            String langMe = McJobs.getPlugin().getLanguage().getLanguageName(langOriginal, p.getUniqueId());
            tcmain.addExtra(getLanguageButton(langMe, p.getPlayer()));
            isUse = true;
        }
        p.spigot().sendMessage(tcmain);
    }
    
    public static void sendMessage(Player p, ConfigurationSection section, HashMap<String, String> replaces) {
        TextComponent tcLine = new TextComponent("");
        
        List<Integer> keys = new ArrayList<>();
        for(String strKey: section.getKeys(false)) {
            try {
                keys.add(Integer.parseInt(strKey));
            } catch(Exception ex) {
                McJobs.getPlugin().getLogger().warning("Please use only integer for Spigot/WorldEdit and Multi Line Messages.");
            }
        }
        Collections.sort(keys);
        
        for(int ik: keys) {
            String k = String.valueOf(ik);
            if(tcLine == null)
                tcLine = new TextComponent("");
            
            if(section.isString(k)) {
                TextComponent tcNext = new TextComponent(Utils.colorTrans(Replace(section.getString(k), replaces)));
                tcLine.addExtra(tcNext);
                continue;
            }
            
            ConfigurationSection cs = section.getConfigurationSection(k);
            if(!cs.isString("message"))
                continue;
            
            TextComponent tcNext = new TextComponent(Utils.colorTrans(Replace(cs.getString("message"), replaces)));
            if(cs.isString("hover-msg")) {
                HoverEvent hoverev = new HoverEvent(
                    getHoverAction(cs.getString("hover-type", "text")), 
                    new ComponentBuilder(Utils.colorTrans(Replace(cs.getString("hover-msg"), replaces))).create()
                );
                tcNext.setHoverEvent(hoverev);
            }
             
            if(cs.isString("click-msg")) {
                ClickEvent clickev = new ClickEvent(
                    getClickAction(cs.getString("click-type", "open_url")), 
                    Replace(cs.getString("click-msg"), replaces)
                );
                tcNext.setClickEvent(clickev);
            }
            
            tcLine.addExtra(tcNext);
            
            // End of Message?!
            if(cs.getBoolean("break", false)) {
                p.spigot().sendMessage(tcLine);
                tcLine = null;
            }
        }
        
        if(tcLine != null) {
            p.spigot().sendMessage(tcLine);
        }
    }
    
    private static String Replace(String str, HashMap<String, String> rep) {
        if(rep == null || rep.isEmpty())
            return str;
        
        for(Map.Entry<String, String> me: rep.entrySet()) {
            str = str.replace(me.getKey(), me.getValue());
        }
        return str;
    }
    
    public static int getPlayerHasJobs(Player p) {
        int iJob = 0;
        for(Map.Entry<String, PlayerJobs> me: PlayerJobs.getJobsList().entrySet()) {
            if(PlayerData.hasJob(p.getUniqueId(), me.getKey()) && !me.getValue().getData().compJob().isDefault())
                iJob++;
        }
        return iJob;
    }
    
    public static TextComponent getCommandButton(String jobMe, Player p, String cmd) {
        jobMe = ChatColor.stripColor(jobMe);
        TextComponent tc = new TextComponent(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobDisplay("button.command", p.getUniqueId()).addVariables(jobMe, p.getName(), "")));
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
        return  tc;
    }
    
    public static TextComponent getInfoButton(String jobMe, Player p, ChatColor cc) {
        jobMe = ChatColor.stripColor(jobMe);
        TextComponent tc = new TextComponent(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobDisplay("button.info", p.getUniqueId()).addVariables(jobMe, p.getName(), "" + cc.getChar() + "")));
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mcjobs info " + jobMe));
        return  tc;
    }
    
    public static void sendJoinButton(String jobMe, Player p) {
        TextComponent tc = new TextComponent(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobDisplay("button.join", p.getUniqueId()).addVariables(jobMe, p.getName(), "")));
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mcjobs join " + jobMe));
        p.spigot().sendMessage(tc);
    }
    
    public static void sendLeaveButton(String jobMe, Player p) {
        TextComponent tc = new TextComponent(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobDisplay("button.leave", p.getUniqueId()).addVariables(jobMe, p.getName(), "")));
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mcjobs leave " + jobMe));
        p.spigot().sendMessage(tc);
    }
    
    public static TextComponent getLanguageButton(String langMe, Player p) {
        TextComponent tc = new TextComponent(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobDisplay("button.language", p.getUniqueId()).addVariables("", p.getName(), langMe)));
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mcjobs language " + langMe));
        return  tc;
    }
    
    private static HoverEvent.Action getHoverAction(String str) {
        if(HoverEvent.Action.valueOf("SHOW_" + str.toUpperCase()) != null)
            return HoverEvent.Action.valueOf("SHOW_" + str.toUpperCase());
        return HoverEvent.Action.SHOW_TEXT;
    }
    
    private static ClickEvent.Action getClickAction(String str) {
        if(ClickEvent.Action.valueOf(str.toUpperCase()) != null)
            return ClickEvent.Action.valueOf(str.toUpperCase());
        return ClickEvent.Action.RUN_COMMAND;
    }
}
