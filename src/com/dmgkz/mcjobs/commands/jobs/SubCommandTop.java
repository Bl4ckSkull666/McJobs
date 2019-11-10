/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.jobs;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.localization.GetLanguage;
import com.dmgkz.mcjobs.localization.SpigotBuilds;
import com.dmgkz.mcjobs.localization.WorldEditBuilds;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.levels.Leveler;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import com.dmgkz.mcjobs.scheduler.McTopSigns;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class SubCommandTop {
    //mcjadm top {job}
    public static void command(Player p, String[] a) {
        String str = "";
        PrettyText text = new PrettyText();
        if(!p.hasPermission("mcjobs.toplist")) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("permission", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }
        
        if(!McJobs.getPlugin().getConfig().getBoolean("toplist.allow-top", true)) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("no-top", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);                    
            return;
        }
        
        if(a.length < 2) {
            GetLanguage.sendMessage(p, "admincommand.top-list", "&cPlease add the wished Job to the command.", null);
            if(GetLanguage.isSpigot()) {
                SpigotBuilds.sendJobButtons(p, "/mcjobs top %j");
            } else if(GetLanguage.isWorldEdit()) {
                WorldEditBuilds.sendJobButtons(p, "/mcjobs top %j");
            }
            return;
        }
        
        String jobOriginal = McJobs.getPlugin().getLanguage().getOriginalJobName(a[1], p.getUniqueId()).toLowerCase();        
        if(!PlayerJobs.getJobsList().containsKey(jobOriginal)) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("missing-job", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);                    
            return;
        }
        
        HashMap<Double, List<OfflinePlayer>> topPlayers = McTopSigns.getTopOfJob(jobOriginal);
        if(topPlayers.isEmpty()) {
            GetLanguage.sendMessage(p, "admincommand.no-players-in-job", "&cThere is no Player with this Job.", null);
            return;
        }
        
        String header = "&f[&l &3Top 10 in &r&b" + McJobs.getPlugin().getLanguage().getJobName(jobOriginal, p.getUniqueId()) + "&l &r&f]";
        int hSpaces = (52 - ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', header)).length()) / 2;
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', spaceBuilder(hSpaces, "&b=") + header + spaceBuilder(hSpaces, "&b=")));
        
        List<Double> tmp = new ArrayList<>();
        tmp.addAll(topPlayers.keySet());
        Collections.sort(tmp);
        Collections.reverse(tmp);
        
        int pos = 1;
        int lines = 1;
        for(double doub: tmp) {
            if(lines == 11)
                break;
            
            int level = Leveler.getLevelByExp(doub);
            for(OfflinePlayer op: topPlayers.get(doub)) {                
                String line1 = (pos > 10?"&l ":"") + "&r&a" + String.valueOf(pos) + "&f.&l &r&6" + op.getName();
                String rank = McJobs.getPlugin().getLanguage().getJobRank(Leveler.getRank(level), p.getUniqueId());
                String line2 = "&e" + rank + "&l &l &l &l &l &bLv.&l &r" + (level > 1000?"&l ":"") + (level > 100?"&l ":"") + (level > 10?"&l ":"") + "&r&b" + String.valueOf(level);
                int spaces = 52 - ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', line1)).length();
                spaces -= ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', line2)).length();
                
                sendLine(p, op, ChatColor.translateAlternateColorCodes('&', line1 + spaceBuilder(spaces, "&l ") + line2));
                lines++;
                if(lines == 11)
                    break;
            }
            pos++;
        }
        
        String footer = "&f[ &elast update " + McTopSigns.getLastUpdated(p.getUniqueId()) + " &f]";
        int fSpaces = (52 - ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', footer)).length()) / 2;
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', spaceBuilder(fSpaces, "&b=") + footer + spaceBuilder(fSpaces, "&b=")));
    }
    
    private static String spaceBuilder(int spaces, String symb) {
        String str = "";
        for(int i = 0; i < spaces; i++)
            str += symb;
        return str;
    }
    
    private static void sendLine(Player p, OfflinePlayer op, String line) {
        if(GetLanguage.isSpigot()) {
            SpigotBuilds.sendTopLine(p, line, op);
        } else if(GetLanguage.isWorldEdit()) {
            WorldEditBuilds.sendTopLine(p, line, op);
        } else {
            p.sendMessage(line);
        }
    }
}
