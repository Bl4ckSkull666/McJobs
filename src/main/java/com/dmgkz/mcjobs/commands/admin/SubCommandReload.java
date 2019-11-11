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
import com.dmgkz.mcjobs.scheduler.McJobsPreComp;
import com.dmgkz.mcjobs.scheduler.McJobsRemovePerm;
import com.dmgkz.mcjobs.util.ConfigMaterials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class SubCommandReload {
    public static void command(Player p) {
        String str = "";
        PrettyText text = new PrettyText();
        if(!p.hasPermission("mcjobs.admin.reload")) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("permission", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }
        
        McJobs.getPlugin().reloadConfig();

        PlayerJobs.getJobsList().clear();
        Leveler.getRanks().clear();
        McJobs.getPlugin().getServer().getScheduler().cancelTasks(McJobs.getPlugin());

        try {
            McJobs.getPlugin().mcloadconf(McJobs.getPlugin());
        } catch(Exception e) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("failedreload", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);
        }
        
        McJobs.getPlugin().loadClasses();
        ConfigMaterials.load(McJobs.getPlugin().getConfig());
        PlayerData.loadPlayerPerms();
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(McJobs.getPlugin(), new McJobsRemovePerm(), 1200L, 1200L);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(McJobs.getPlugin(), new McJobsPreComp(), 200L, 200L);
        str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("succeedreload", p.getUniqueId()).addVariables("", p.getName(), "");
        text.formatPlayerText(str, p);
    }
}
