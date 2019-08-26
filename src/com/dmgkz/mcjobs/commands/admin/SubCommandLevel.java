/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.admin;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import com.dmgkz.mcjobs.util.PlayerUtils;
import com.dmgkz.mcjobs.util.StringToNumber;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class SubCommandLevel {
    //mcjadm setlevel {playername} {job} {level}
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
        
        if(a.length != 4) {
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
        
        String job = McJobs.getPlugin().getLanguage().getJobNameByLangName(a[2].toLowerCase(), uuid);
        int levels = 1;
        
        if(StringToNumber.isPositiveNumber(a[3]))
            levels = Integer.parseInt(a[3]);
        
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
        
        if(!PlayerData.hasJob(playerUUID, job)) {
            s.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("nojob", uuid).addVariables(job, PlayerData.getName(playerUUID), a[3]));
            return;
        }
        
        switch(t.toLowerCase()) {
            case "add":
                if(PlayerData.addLevels(playerUUID, job, levels)) {
                    s.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("add_lvl", uuid).addVariables(job, PlayerData.getName(playerUUID), a[3]));
                    if(Bukkit.getPlayer(playerUUID) != null){
                        text = new PrettyText();
                        str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("padd_lvl", playerUUID).addVariables(job, Bukkit.getPlayer(playerUUID).getName(), a[3]);
                        text.formatPlayerText(str, Bukkit.getPlayer(playerUUID));
                    }                    
                } else
                    s.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("adderror", uuid).addVariables(job, PlayerData.getName(playerUUID), a[3]));
                return;
            case "set":
                PlayerData.setLevel(playerUUID, job, levels);
                if(PlayerData.getJobLevel(playerUUID, job) == levels) {
                    s.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("set_lvl", uuid).addVariables(job, PlayerData.getName(playerUUID), a[3]));
                    if(Bukkit.getPlayer(playerUUID) != null){
                        text = new PrettyText();
                        str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("pset_lvl", playerUUID).addVariables(job, Bukkit.getPlayer(playerUUID).getName(), a[3]);
                        text.formatPlayerText(str, Bukkit.getPlayer(playerUUID));
                    }                    
                } else
                    s.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("seterror", uuid).addVariables(job, PlayerData.getName(playerUUID), a[3]));
                return;
            case "remove":
                int lvl = PlayerData.getJobLevel(playerUUID, job)-levels;
                if(lvl <= 0) {
                    s.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("lvl_to_low", uuid).addVariables(job, PlayerData.getName(playerUUID), a[3]));
                    return;
                }
                PlayerData.setLevel(playerUUID, job, lvl);
                PlayerData.setExp(playerUUID, job, 0.0);
                s.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("rem_lvl", uuid).addVariables(job, PlayerData.getName(playerUUID), a[3]));
                if(Bukkit.getPlayer(playerUUID) != null){
                    text = new PrettyText();
                    str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("prem_lvl", playerUUID).addVariables(job, Bukkit.getPlayer(playerUUID).getName(), a[3]);
                    text.formatPlayerText(str, Bukkit.getPlayer(playerUUID));
                }
                return;
            default:
                s.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("error", uuid).addVariables(job, PlayerData.getName(playerUUID), a[3]));
        }
    }
}
