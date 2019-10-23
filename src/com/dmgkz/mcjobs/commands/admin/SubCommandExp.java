/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.admin;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.levels.Leveler;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import com.dmgkz.mcjobs.util.PlayerUtils;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class SubCommandExp {
    //mcjadm {exptype} {playername} {job} {exp amount}
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
        
        String job = McJobs.getPlugin().getLanguage().getOriginalJobName(a[2].toLowerCase(), p.getUniqueId()).toLowerCase();
        double exp = 0.0;
        
        if(isDouble(a[3]))
            exp = Double.parseDouble(a[3]);
        
        if(job.isEmpty() || !PlayerJobs.getJobsList().containsKey(job)) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("exist", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);                    
            return;
        }
        
        if(!PlayerData.hasJob(op.getUniqueId(), job)) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("nojob", p.getUniqueId()).addVariables(job, op.getName(), a[3]));
            return;
        }
        
        switch(t.toLowerCase()) {
            case "add":
                if(PlayerData.addExp(op.getUniqueId(), job, exp)) {
                    p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("add_lvl", p.getUniqueId()).addVariables(job, op.getName(), a[3]));
                    if(Bukkit.getPlayer(op.getUniqueId()) != null){
                        text = new PrettyText();
                        str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("padd_lvl", op.getUniqueId()).addVariables(job, op.getName(), a[3]);
                        text.formatPlayerText(str, Bukkit.getPlayer(op.getUniqueId()));
                    }                    
                } else
                    p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("adderror", p.getUniqueId()).addVariables(job, op.getName(), a[3]));
                return;
            case "set":
                if(exp >= Leveler.getXPtoLevel(PlayerData.getJobLevel(op.getUniqueId(), job))) {
                    p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("exp_to_high", p.getUniqueId()).addVariables(job, op.getName(), a[3]));
                    return;
                }
                
                PlayerData.setExp(op.getUniqueId(), job, exp);
                if(PlayerData.getJobExp(op.getUniqueId(), job) == exp) {
                    p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("set_exp", p.getUniqueId()).addVariables(job, op.getName(), a[3]));
                    if(Bukkit.getPlayer(op.getUniqueId()) != null){
                        text = new PrettyText();
                        str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("pset_exp", op.getUniqueId()).addVariables(job, op.getName(), a[3]);
                        text.formatPlayerText(str, Bukkit.getPlayer(op.getUniqueId()));
                    }                    
                } else
                    p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("seterror", p.getUniqueId()).addVariables(job, op.getName(), a[3]));
                return;
            case "remove":
                double xp = PlayerData.getJobExp(op.getUniqueId(), job)-exp;
                if(xp < 0.0) {
                    p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("exp_to_low", p.getUniqueId()).addVariables(job, op.getName(), a[3]));
                    return;
                }
                PlayerData.setExp(p.getUniqueId(), job, xp);
                p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("rem_exp", p.getUniqueId()).addVariables(job, op.getName(), a[3]));
                if(Bukkit.getPlayer(p.getUniqueId()) != null){
                    text = new PrettyText();
                    str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("prem_exp", op.getUniqueId()).addVariables(job, op.getName(), a[3]);
                    text.formatPlayerText(str, Bukkit.getPlayer(p.getUniqueId()));
                }
                return;
            default:
                p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("error", p.getUniqueId()).addVariables(job, op.getName(), a[3]));
        }
    }
    
    private static boolean isDouble(String str) {
        try {
            double doub = Double.parseDouble(str);
            return (doub >= 0.0);
        } catch(NumberFormatException ex) {}
        return false;
    }
}
