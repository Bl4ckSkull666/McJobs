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
    public static void command(Player p, String[] a) {
        if(!(p.hasPermission("mcjobs.jobs.info") || p.hasPermission("mcjobs.jobs.all")) && McJobs.getPlugin().getConfig().getBoolean("advanced.usePerms", true)) { 
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobCommand("permission", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        if(!PlayerJobs.getJobsList().containsKey(a[1].toLowerCase())) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobCommand("nojob", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        PlayerJobs.getJobsList().get(a[1].toLowerCase()).getData().display().showPlayerJob(p, p.getUniqueId());
        PlayerJobs.getJobsList().get(a[1].toLowerCase()).getData().display().showJob(p);
        
        /*if(PlayerJobs.getJobsList().get(a[1].toLowerCase()).getData().getRegionSpigotMessage() != null)
            PlayerJobs.getJobsList().get(a[1].toLowerCase()).getData().getRegionSpigotMessage().saveMessage(null, "");
        
        if(PlayerJobs.getJobsList().get(a[1].toLowerCase()).getData().getSignSpigotMessage() != null)
            PlayerJobs.getJobsList().get(a[1].toLowerCase()).getData().getSignSpigotMessage().saveMessage(null, "");*/
    }
}
