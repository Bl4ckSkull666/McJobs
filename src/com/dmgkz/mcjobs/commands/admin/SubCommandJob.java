/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.admin;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.listeners.mcjobs.JobChangeListener;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import com.dmgkz.mcjobs.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class SubCommandJob {
    //mcjadm addjob (playername) (job)
    public static void command(Player p, String[] a, String t) {
        String str = "";
        PrettyText text = new PrettyText();
        
        if(!p.hasPermission("mcjobs.admin.addlevel")) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("permission", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }
        
        if(a.length != 3 || t.equalsIgnoreCase("list") && a.length != 2) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("args", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);                    
            return;
        }
        
        OfflinePlayer op = PlayerUtils.getOfflinePlayer(a[1]);
        if(op == null) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("notjoined", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);                    
            return;
        }
        
        
        if(t.equalsIgnoreCase("list")) {
            
            return;
        }
        
        String jobOriginal = McJobs.getPlugin().getLanguage().getOriginalJobName(a[2].toLowerCase(), p.getUniqueId()).toLowerCase();
        String jobPlayer = McJobs.getPlugin().getLanguage().getJobName(jobOriginal, op.getUniqueId());
        String jobMe = McJobs.getPlugin().getLanguage().getJobName(jobOriginal, p.getUniqueId());
        if(a[2].isEmpty() || !PlayerJobs.getJobsList().containsKey(jobOriginal)) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("exist", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);                    
            return;
        }
        
        switch(t.toLowerCase()) {
            case "join":
                if(PlayerData.addJob(op.getUniqueId(), jobOriginal)) {
                    if(PlayerData.getRejoinTime(op.getUniqueId(), jobOriginal) > 0)
                        PlayerData.removeRejoinTimer(op.getUniqueId(), jobOriginal);
                    
                    p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("added_job", p.getUniqueId()).addVariables(jobMe, op.getName(), ""));
                    if(Bukkit.getPlayer(op.getUniqueId()) != null){
                        text = new PrettyText();
                        str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("padded_job", op.getUniqueId()).addVariables(jobPlayer, op.getName(), "");
                        text.formatPlayerText(str, Bukkit.getPlayer(op.getUniqueId()));
                    }
                } else
                    p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("hasjob", p.getUniqueId()).addVariables(jobMe, op.getName(), ""));
                return;
            case "leave":
                if(!PlayerData.hasJob(op.getUniqueId(), jobOriginal)) {
                    p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("nojob", p.getUniqueId()).addVariables(jobMe, op.getName(), ""));
                    return;
                }
                
                if(PlayerJobs.getJobsList().get(jobOriginal).getData().compJob().isDefault()) {
                    p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("isDefault", p.getUniqueId()).addVariables(jobMe, op.getName(), ""));
                    return;
                }
                
                if(PlayerData.removeJob(op.getUniqueId(), jobOriginal)) {
                    PlayerData.addReJoinTimer(op.getUniqueId(), jobOriginal, JobChangeListener.getTimer());
                    p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("rem_job", p.getUniqueId()).addVariables(jobMe, op.getName(), ""));
                    if(Bukkit.getPlayer(op.getUniqueId()) != null){
                        text = new PrettyText();
                        str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("prem_job", op.getUniqueId()).addVariables(jobPlayer, op.getName(), "");
                        text.formatPlayerText(str, Bukkit.getPlayer(op.getUniqueId()));
                    }
                } else
                    p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("remerror", p.getUniqueId()).addVariables(jobMe, op.getName(), ""));
                return;
            case "info":
                if(!PlayerData.hasJob(op.getUniqueId(), jobOriginal)) {
                    p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("nojob", p.getUniqueId()).addVariables(jobMe, op.getName(), ""));
                    return;
                }
                
                PlayerJobs.getJobsList().get(jobOriginal).getData().display().showPlayerJob(p, op.getUniqueId());
                return;
            default:
                return;
        }
    }
}
