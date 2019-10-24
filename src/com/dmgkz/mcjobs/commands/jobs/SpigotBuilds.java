/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.jobs;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import java.util.Map;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class SpigotBuilds {
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
    
    public static int getPlayerHasJobs(Player p) {
        int iJob = 0;
        for(Map.Entry<String, PlayerJobs> me: PlayerJobs.getJobsList().entrySet()) {
            if(PlayerData.hasJob(p.getUniqueId(), me.getKey()) && !me.getValue().getData().compJob().isDefault())
                iJob++;
        }
        return iJob;
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
}
