/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.jobs;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import java.text.DecimalFormat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class SubCommandHelp {
    public static void command(Player p, int page) {
        DecimalFormat df = new DecimalFormat("#,##0");
        if(page > McJobs.getPlugin().getLanguage().getSpaces("numhelp", p.getUniqueId()) || page < 1) {
            p.sendMessage(McJobs.getPlugin().getLanguage().getJobHelp("nohelp", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page)));
            return;            
        }
        
        p.sendMessage(ChatColor.GOLD + "---~~ " + ChatColor.DARK_AQUA + "MC Jobs " + ChatColor.GOLD + "~~---");
        p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getJobHelp("page", p.getUniqueId()).addVariables("", p.getName(), "") +" " + page);
        p.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------------------------");
        p.sendMessage("");

        int line = 1;
        while(true) {
            String now = McJobs.getPlugin().getLanguage().getJobHelp(page + ".line" + String.valueOf(line), p.getUniqueId()).addVariables(df.format(PlayerData.getAllowedJobCount(p.getUniqueId())), p.getName(), String.valueOf(page));
            String next = McJobs.getPlugin().getLanguage().getJobHelp(page + ".line" + String.valueOf((line+1)), p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page));
            if(!(now.isEmpty() || now.equals("") || now == null) && !(next.isEmpty() || next.equals("") || next == null) ||
                    !(now.isEmpty() || now.equals("") || now == null) && (next.isEmpty() || next.equals("") || next == null) ||
                    (now.isEmpty() || now.equals("") || now == null) && !(next.isEmpty() || next.equals("") || next == null)) {
                p.sendMessage(now);
                line++;
            } else if((now.isEmpty() || now.equals("") || now == null) && (next.isEmpty() || next.equals("") || next == null))
                break;
        }
        
        p.sendMessage("");
        if(page == McJobs.getPlugin().getLanguage().getSpaces("numhelp", p.getUniqueId()))
            p.sendMessage(McJobs.getPlugin().getLanguage().getJobHelp("finish", p.getUniqueId()).addVariables("", p.getName(), ""));
        else
            p.sendMessage(McJobs.getPlugin().getLanguage().getJobHelp("endofpage", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page)) + " " + McJobs.getPlugin().getLanguage().getJobHelp("command", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page)));
    }
}
