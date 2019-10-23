/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.jobs;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.events.McJobsEventJobChange;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import com.dmgkz.mcjobs.util.TimeFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class SubCommandJoin {
    public static void command(Player p, String job) {
        String jobname = McJobs.getPlugin().getLanguage().getJobName(job.toLowerCase(), p.getUniqueId());
        if(!PlayerJobs.getJobsList().containsKey(job)) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobCommand("exist", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
            return;                    
        }
        
        if(PlayerData.hasJob(p.getUniqueId(), job)) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobJoin("have", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
            return;
        }

        if(!(p.hasPermission("mcjobs.jobsavail." + job) || p.hasPermission("mcjobs.jobsavail.all")) && McJobs.getPlugin().getConfig().getBoolean("advanced.usePerms")) {
            p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getJobJoin("jobperm", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
            return;
        }
        
        if(!PlayerData.isJoinable(p.getUniqueId(), job)) {
            p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getJobJoin("jobperm", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
            Integer time = PlayerData.getRejoinTime(p.getUniqueId(), job);
            
            if(time == null) {
                p.sendMessage("ERROR: time is null!");
                return;
            } else if(time < 0)
                time = 1;
            
            String timed = TimeFormat.getFormatedTime(p.getUniqueId(), time);
            PrettyText text = new PrettyText();
            String str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getJobJoin("timer", p.getUniqueId()).addVariables(jobname, p.getName(), timed);
                    
            text.formatPlayerText(str, p);
            return;
        }
        
        if(PlayerData.getJobCount(p.getUniqueId()) < PlayerData.getAllowedJobCount(p.getUniqueId())) {
            PlayerData.addJob(p.getUniqueId(), job);
            p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getJobJoin("join", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
            McJobsEventJobChange event = new McJobsEventJobChange(p, job, true, false);
            Bukkit.getServer().getPluginManager().callEvent(event);
        } else {
            p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getJobJoin("toomany", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
            p.sendMessage(ChatColor.YELLOW + McJobs.getPlugin().getLanguage().getJobJoin("command", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
            return;
        }
        PlayerData.savePlayerCache(p.getUniqueId());
    }
}
