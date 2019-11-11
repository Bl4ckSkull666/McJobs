/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.jobs;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class SubCommandInfo {
    public static void command(Player p, String job) {
        String jobname = McJobs.getPlugin().getLanguage().getJobName(job.toLowerCase(), p.getUniqueId());
        if(!(p.hasPermission("mcjobs.jobs.info") || p.hasPermission("mcjobs.jobs.all")) && McJobs.getPlugin().getConfig().getBoolean("advanced.usePerms", true)) { 
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobCommand("permission", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
            return;
        }
        
        if(!PlayerJobs.getJobsList().containsKey(job)) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobCommand("nojob", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
            return;
        }
        
        PlayerJobs.getJobsList().get(job).getData().display().showJob(p);
    }
}
