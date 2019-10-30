/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.localization;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.util.Utils;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TextComponent.Builder;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class WorldEditBuilds {
    public static void sendJobList(Player p) {
        Builder b = TextComponent.builder();
        int i = 0;
        for(Map.Entry<String, PlayerJobs> me: PlayerJobs.getJobsList().entrySet()) {
            String jobOriginal = me.getKey();
            String jobMe = McJobs.getPlugin().getLanguage().getJobName(me.getKey(), p.getUniqueId());
            
            if(PlayerData.hasJob(p.getUniqueId(), jobOriginal) && !me.getValue().getData().compJob().isDefault())
                b.append(getInfoButton(jobMe, p, ChatColor.RED));
            else if(!PlayerData.isJoinable(p.getUniqueId(), jobOriginal))
                b.append(getInfoButton(jobMe, p, ChatColor.DARK_GRAY));
            else if((p.hasPermission("mcjobs.jobsavail." + jobOriginal) || p.hasPermission("mcjobs.jobsavail.all") || !(McJobs.getPlugin().getConfig().getBoolean("advanced.usePerms"))) && !me.getValue().getData().compJob().isDefault())
                b.append(getInfoButton(jobMe, p, ChatColor.GOLD));
            else if(me.getValue().getData().compJob().isDefault())
                b.append(getInfoButton(jobMe, p, ChatColor.DARK_AQUA));
            else
                b.append(getInfoButton(jobMe, p, ChatColor.DARK_GRAY));
            
            i++;
            if(i < PlayerJobs.getJobsList().size())
                b.append(ChatColor.GRAY + ", ");
            else
                b.append(ChatColor.GRAY + ".");
        }
        BukkitPlayer bp = BukkitAdapter.adapt(p);
        bp.print(b.build());
    }
    
    public static void sendHelpPage(Player p, int page) {
        int spaces = 55;
        Builder b = TextComponent.builder();
        Builder bprev = TextComponent.builder();
        Builder bnext = TextComponent.builder();
        if(page > 1) {
            String msg = ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobHelp("prevpage", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page - 1)));
            spaces -= ChatColor.stripColor(msg).length();
            bprev.append(msg).clickEvent(ClickEvent.runCommand(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobHelp("command", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page - 1))))));
            b.append(bprev.build());
        }
                
        String nmsg = ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobHelp("nextpage", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page + 1)));
        spaces -= ChatColor.stripColor(nmsg).length();
        bnext.append(nmsg).clickEvent(ClickEvent.runCommand(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobHelp("command", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page + 1))))));
                
        for(int i = 0; i < spaces; i++)
            b.append(" ");
        b.append(bnext.build());
        BukkitPlayer bp = BukkitAdapter.adapt(p);
        bp.print(b.build());
    }
    
    public static void getLanguageList(Player p) {
        Builder b = TextComponent.builder();
        boolean isUse = false;
        for(String langOriginal: McJobs.getPlugin().getLanguage().getAvaLangs()) {
            if(isUse)
                b.append(ChatColor.GRAY + ", ");
            String langMe = McJobs.getPlugin().getLanguage().getLanguageName(langOriginal, p.getUniqueId());
            b.append(getLanguageButton(langMe, p.getPlayer()));
            isUse = true;
        }
        BukkitPlayer bp = BukkitAdapter.adapt(p);
        bp.print(b.build());
    }
    
    public static int getPlayerHasJobs(Player p) {
        int iJob = 0;
        for(Map.Entry<String, PlayerJobs> me: PlayerJobs.getJobsList().entrySet()) {
            if(PlayerData.hasJob(p.getUniqueId(), me.getKey()) && !me.getValue().getData().compJob().isDefault())
                iJob++;
        }
        return iJob;
    }
    
    public static void sendMessage(Player p, ConfigurationSection section, HashMap<String, String> replaces) {
        HashMap<Integer, TextComponent> tcMain = new HashMap<>();
        int iLine = 1;
        Builder bLine = TextComponent.builder();
        for(String k: section.getKeys(false)) {
            if(bLine == null)
                bLine = TextComponent.builder();
            ConfigurationSection cs = section.getConfigurationSection(k);
            if(!cs.isString("message"))
                continue;
            
            Builder bNext = TextComponent.builder(Utils.colorTrans(Replace(cs.getString("message"), replaces)));
            if(cs.isString("hover-msg")) {
                bNext.hoverEvent(getHoverAction(cs.getString("hover-type", "text"), cs.getString("hover-msg"), replaces));
            }
             
            if(cs.isString("click-msg")) {
                bNext.clickEvent(getClickAction(cs.getString("click-type", "open_url"), cs.getString("click-msg"), replaces));
            }
            
            bLine.append(bNext);
            // End of Message?!
            if(cs.getBoolean("break", false)) {
                tcMain.put(iLine, bLine.build());
                bLine = null;
                iLine++;
            }
        }
        
        if(bLine != null) {
            tcMain.put(iLine, bLine.build());
            iLine++;
        }
        BukkitPlayer bp = BukkitAdapter.adapt(p);
        
        List<Integer> it = new ArrayList<>();
        it.addAll(tcMain.keySet());
        Collections.sort(it);
        
        for(Integer i: it)
            bp.print(tcMain.get(i));
    }
    
    private static String Replace(String str, HashMap<String, String> rep) {
        if(rep.isEmpty())
            return str;
        
        for(Map.Entry<String, String> me: rep.entrySet()) {
            str = str.replace(me.getKey(), me.getValue());
        }
        return str;
    }
    
    public static TextComponent getInfoButton(String jobMe, Player p, ChatColor cc) {
        jobMe = ChatColor.stripColor(jobMe);
        Builder b = TextComponent.builder();
        b.append(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobDisplay("button.info", p.getUniqueId()).addVariables(jobMe, p.getName(), "" + cc + "")));
        b.clickEvent(ClickEvent.runCommand("/mcjobs info " + jobMe));
        return b.build();
    }
    
    public static void sendJoinButton(String jobMe, Player p) {
        Builder b = TextComponent.builder();
        b.append(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobDisplay("button.join", p.getUniqueId()).addVariables(jobMe, p.getName(), "")));
        b.clickEvent(ClickEvent.runCommand("/mcjobs join " + jobMe));
        BukkitPlayer bp = BukkitAdapter.adapt(p);
        bp.print(b.build());
    }
    
    public static void sendLeaveButton(String jobMe, Player p) {
        Builder b = TextComponent.builder();
        b.append(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobDisplay("button.leave", p.getUniqueId()).addVariables(jobMe, p.getName(), "")));
        b.clickEvent(ClickEvent.runCommand("/mcjobs leave " + jobMe));
        BukkitPlayer bp = BukkitAdapter.adapt(p);
        bp.print(b.build());
    }
    
    public static TextComponent getLanguageButton(String langMe, Player p) {
        Builder b = TextComponent.builder();
        b.append(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobDisplay("button.language", p.getUniqueId()).addVariables("", p.getName(), langMe)));
        b.clickEvent(ClickEvent.runCommand("/mcjobs language " + langMe));
        return b.build();
    }
    
    private static HoverEvent getHoverAction(String str, String msg, HashMap<String, String> replaces) {
        //achievement|entity|item|text
        TextComponent tc = TextComponent.builder(Utils.colorTrans(Replace(msg, replaces))).build();
        return HoverEvent.showText(tc);
    }
    
    private static ClickEvent getClickAction(String str, String msg, HashMap<String, String> replaces) {
        TextComponent tc = TextComponent.builder(Utils.colorTrans(Replace(msg, replaces))).build();
        switch(str.toLowerCase()) {
            case "change_page":
                return ClickEvent.changePage(Utils.colorTrans(Replace(msg, replaces)));
            case "open_file":
                return ClickEvent.openFile(Utils.colorTrans(Replace(msg, replaces)));
            case "open_url":
                return ClickEvent.openUrl(Utils.colorTrans(Replace(msg, replaces)));
            case "suggest_command":
                return ClickEvent.suggestCommand(Utils.colorTrans(Replace(msg, replaces)));
            default:
                return ClickEvent.runCommand(Utils.colorTrans(Replace(msg, replaces)));
        }
    }
}
