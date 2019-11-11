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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class SubCommandLeave {
    public static void command(Player p, String job) {
        String jobname = McJobs.getPlugin().getLanguage().getJobName(job.toLowerCase(), p.getUniqueId());
        if(!PlayerJobs.getJobsList().containsKey(job)) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobCommand("exist", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
            return;                    
        }

        if(!PlayerData.hasJob(p.getUniqueId(), job)) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobLeave("donthave", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
            return;
        }

        if(PlayerJobs.getJobsList().get(job).getData().compJob().isDefault() && !p.hasPermission("mcjobs.admin.leavedefault")) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobLeave("leavedefault", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
            return;                
        }
        
        PlayerData.removeJob(p.getUniqueId(), job);
        p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getJobLeave("quit", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
        
        McJobsEventJobChange event = new McJobsEventJobChange(p, job, false, true);
        Bukkit.getServer().getPluginManager().callEvent(event);
        PlayerData.savePlayerCache(p.getUniqueId());
    }
}