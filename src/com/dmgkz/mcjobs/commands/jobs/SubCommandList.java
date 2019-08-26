/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.jobs;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class SubCommandList {
    public static void command(Player p, String l) {
        String str = "";
        PrettyText text = new PrettyText();
        
        if(!(p.hasPermission("mcjobs.jobs.list") || p.hasPermission("mcjobs.jobs.all")) && McJobs.getPlugin().getConfig().getBoolean("advanced.usePerms", true)) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getJobCommand("permission", p.getUniqueId()).addVariables("", p.getName(), l);
            text.formatPlayerText(str, p);
            return;
        }
    
        int iJob = 0;
        int iMax = PlayerData.getAllowedJobCount(p.getUniqueId());
    
        str = ChatColor.DARK_GREEN + McJobs.getPlugin().getLanguage().getJobList("available", p.getUniqueId()).addVariables("", p.getName(), "") + " ";
        
        int i = 0;
        for(Map.Entry<String, PlayerJobs> me: PlayerJobs.getJobsList().entrySet()) {
            String job = me.getValue().getData().getName(p.getUniqueId());
            
            if(PlayerData.hasJob(p.getUniqueId(), me.getKey()) && !me.getValue().getData().compJob().isDefault()) {
                str = str.concat(ChatColor.RED + job);
                iJob++;
            } else if(!PlayerData.isJoinable(p.getUniqueId(), me.getKey())){
                str = str.concat(ChatColor.DARK_GRAY + job);
            } else if((p.hasPermission("mcjobs.jobsavail." + me.getKey()) || p.hasPermission("mcjobs.jobsavail.all") || !(McJobs.getPlugin().getConfig().getBoolean("advanced.usePerms"))) && !me.getValue().getData().compJob().isDefault())
                str = str.concat(ChatColor.GOLD + job);
            else if(me.getValue().getData().compJob().isDefault())
                str = str.concat(ChatColor.DARK_AQUA + job);
            else
                str = str.concat(ChatColor.DARK_GRAY + job);
            
            i++;
            if(i < PlayerJobs.getJobsList().size())
                str = str.concat(ChatColor.GRAY + ", ");
            else
                str = str.concat(ChatColor.GRAY + ".");
        }
        p.sendMessage(ChatColor.DARK_GREEN + McJobs.getPlugin().getLanguage().getJobList("jobsin", p.getUniqueId()).addVariables("", p.getName(), "") + PrettyText.addSpaces(McJobs.getPlugin().getLanguage().getSpaces("jobslist", p.getUniqueId())) + ChatColor.RED + McJobs.getPlugin().getLanguage().getJobList("jobs", p.getUniqueId()).addVariables("", p.getName(), "") + " "
                                              + String.valueOf(iJob) + ChatColor.DARK_GRAY + "/" + ChatColor.RED + String.valueOf(iMax));
        p.sendMessage(ChatColor.DARK_GREEN + McJobs.getPlugin().getLanguage().getJobList("nojob", p.getUniqueId()).addVariables("", p.getName(), ""));
        p.sendMessage(ChatColor.DARK_GREEN + McJobs.getPlugin().getLanguage().getJobList("defaultjob", p.getUniqueId()).addVariables("", p.getName(), ""));
        p.sendMessage(ChatColor.DARK_GREEN + McJobs.getPlugin().getLanguage().getJobList("specific", p.getUniqueId()).addVariables("", p.getName(), ""));
        p.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------------------------");
        
        text.formatPlayerText(str, p);
    }
}
