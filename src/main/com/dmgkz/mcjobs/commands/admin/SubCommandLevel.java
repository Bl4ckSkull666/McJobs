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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class SubCommandLevel {
    //mcjadm setlevel {playername} {job} {level}
    public static void command(Player p, String[] a, String t) {
        String str = "";
        PrettyText text = new PrettyText();
        if(!p.hasPermission("mcjobs.admin.addlevel")) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("permission", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }
        
        
        if(a.length != 4) {
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
        
        String jobOriginal = McJobs.getPlugin().getLanguage().getOriginalJobName(a[2].toLowerCase(), p.getUniqueId()).toLowerCase();
        String jobPlayer = McJobs.getPlugin().getLanguage().getJobName(jobOriginal, op.getUniqueId());
        String jobMe = McJobs.getPlugin().getLanguage().getJobName(jobOriginal, p.getUniqueId());
        
        int levels = 1;
        
        if(StringToNumber.isPositiveNumber(a[3]))
            levels = Integer.parseInt(a[3]);
        
        if(jobOriginal.isEmpty() || !PlayerJobs.getJobsList().containsKey(jobOriginal)) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("exist", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);                    
            return;
        }
        
        if(!PlayerData.hasJob(op.getUniqueId(), jobOriginal)) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("nojob", p.getUniqueId()).addVariables(jobMe, op.getName(), a[3]));
            return;
        }
        
        switch(t.toLowerCase()) {
            case "add":
                if(PlayerData.addLevels(op.getUniqueId(), jobOriginal, levels)) {
                    p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("add_lvl", p.getUniqueId()).addVariables(jobMe, op.getName(), ""));
                    if(Bukkit.getPlayer(op.getUniqueId()) != null){
                        text = new PrettyText();
                        str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("padd_lvl", op.getUniqueId()).addVariables(jobPlayer, op.getName(), "");
                        text.formatPlayerText(str, Bukkit.getPlayer(op.getUniqueId()));
                    }                    
                } else
                    p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("adderror", p.getUniqueId()).addVariables(jobMe, op.getName(), ""));
                return;
            case "set":
                PlayerData.setLevel(op.getUniqueId(), jobOriginal, levels);
                if(PlayerData.getJobLevel(op.getUniqueId(), jobOriginal) == levels) {
                    p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("set_lvl", p.getUniqueId()).addVariables(jobMe, op.getName(), ""));
                    if(Bukkit.getPlayer(op.getUniqueId()) != null){
                        str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("pset_lvl", op.getUniqueId()).addVariables(jobPlayer, op.getName(), "");
                        text.formatPlayerText(str, Bukkit.getPlayer(op.getUniqueId()));
                    }                    
                } else
                    p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("seterror", p.getUniqueId()).addVariables(jobMe, op.getName(), ""));
                return;
            case "remove":
                int lvl = PlayerData.getJobLevel(op.getUniqueId(), jobOriginal)-levels;
                if(lvl <= 0) {
                    p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("lvl_to_low", p.getUniqueId()).addVariables(jobMe, op.getName(), ""));
                    return;
                }
                PlayerData.setLevel(op.getUniqueId(), jobOriginal, lvl);
                PlayerData.setExp(op.getUniqueId(), jobOriginal, 0.0);
                p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("rem_lvl", p.getUniqueId()).addVariables(jobMe, op.getName(), ""));
                if(Bukkit.getPlayer(op.getUniqueId()) != null){
                    text = new PrettyText();
                    str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("prem_lvl", op.getUniqueId()).addVariables(jobPlayer, op.getName(), "");
                    text.formatPlayerText(str, Bukkit.getPlayer(op.getUniqueId()));
                }
                return;
            default:
                p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("error", p.getUniqueId()).addVariables(jobMe, op.getName(), ""));
        }
    }
}
