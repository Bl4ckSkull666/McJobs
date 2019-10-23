/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.jobs;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class SubCommandHideShow {
    public static void command(Player p, String job, boolean show) {
        String jobname = McJobs.getPlugin().getLanguage().getJobName(job.toLowerCase(), p.getUniqueId());
        if(!PlayerJobs.getJobsList().containsKey(job)) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobCommand("nojob", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
            return;
        }
        
        if(!PlayerData.hasJob(p.getUniqueId(), job)) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobCommand("donthave", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
            return;
        }
        
        PlayerData.setShowEveryTime(p.getUniqueId(), job, show);
        if(show)
            p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getJobCommand("show", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
        else
            p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getJobCommand("hide", p.getUniqueId()).addVariables(jobname, p.getName(), ""));
    }
}
