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
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class SubCommandJob {
    //mcjadm addjob (playername) (job)
    public static void command(CommandSender s, String l, String[] a, String t) {
        String str = "";
        PrettyText text = new PrettyText();
        String name = "Console";
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player) {
            uuid = ((Player)s).getUniqueId();
            name = ((Player)s).getName();
            if(!s.hasPermission("mcjobs.admin.addlevel")) {
                str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("permission", uuid).addVariables("", name, l);
                text.formatPlayerText(str, (Player)s);
                return;
            }
        }
        
        if(a.length != 3 || t.equalsIgnoreCase("list") && a.length != 2) {
            if(s instanceof Player){
                str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("args", uuid).addVariables("", name, l);
                text.formatPlayerText(str, (Player)s);                    
            } else
                s.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("args", uuid).addVariables("", name, l));
            return;
        }
        
        UUID playerUUID = PlayerUtils.getUUIDByName(a[1]);
        if(playerUUID == null) {
            if(s instanceof Player){
                str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("notjoined", uuid).addVariables("", name, l);
                text.formatPlayerText(str, (Player)s);                    
            } else
                s.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("notjoined", uuid).addVariables("", name, l));
            return;
        }
        
        
        if(t.equalsIgnoreCase("list")) {
            
            return;
        }
        
        String job = McJobs.getPlugin().getLanguage().getJobNameByLangName(a[2].toLowerCase(), uuid);
        
        if(job.isEmpty() || !PlayerJobs.getJobsList().containsKey(job)) {
            job = a[2].toLowerCase();
        
            if(!PlayerJobs.getJobsList().containsKey(job)) {
                if(s instanceof Player){
                    str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("exist", uuid).addVariables("", name, l);
                    text.formatPlayerText(str, (Player)s);                    
                } else
                    s.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("exist", uuid).addVariables("", name, l));
                return;
            }
        }
        
        switch(t.toLowerCase()) {
            case "join":
                if(PlayerData.addJob(playerUUID, job)) {
                    if(PlayerData.getRejoinTime(playerUUID, job) > 0)
                        PlayerData.removeRejoinTimer(playerUUID, job);
                    
                    s.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("added_job", uuid).addVariables(job, PlayerData.getName(playerUUID), a[3]));
                    if(Bukkit.getPlayer(playerUUID) != null){
                        text = new PrettyText();
                        str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("padded_job", playerUUID).addVariables(job, Bukkit.getPlayer(playerUUID).getName(), a[3]);
                        text.formatPlayerText(str, Bukkit.getPlayer(playerUUID));
                    }
                } else
                    s.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("hasjob", uuid).addVariables(job, PlayerData.getName(playerUUID), a[3]));
                return;
            case "leave":
                if(!PlayerData.hasJob(playerUUID, job)) {
                    s.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("nojob", uuid).addVariables(job, PlayerData.getName(playerUUID), a[3]));
                    return;
                }
                
                if(PlayerJobs.getJobsList().get(job).getData().compJob().isDefault()) {
                    s.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("isDefault", uuid).addVariables("", name, l));
                    return;
                }
                
                if(PlayerData.removeJob(playerUUID, job)) {
                    PlayerData.addReJoinTimer(playerUUID, job, JobChangeListener.getTimer());
                    s.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("rem_job", uuid).addVariables(job, PlayerData.getName(playerUUID), a[3]));
                    if(Bukkit.getPlayer(playerUUID) != null){
                        text = new PrettyText();
                        str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("prem_job", playerUUID).addVariables(job, Bukkit.getPlayer(playerUUID).getName(), a[3]);
                        text.formatPlayerText(str, Bukkit.getPlayer(playerUUID));
                    }
                } else
                    s.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("remerror", uuid).addVariables("", name, l));
                return;
            case "info":
                if(!PlayerData.hasJob(playerUUID, job)) {
                    s.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("nojob", uuid).addVariables(job, PlayerData.getName(playerUUID), a[3]));
                    return;
                }
                
                PlayerJobs.getJobsList().get(job).getData().display().showPlayerJob(s, playerUUID);
                return;
            default:
                return;
        }
    }
}
