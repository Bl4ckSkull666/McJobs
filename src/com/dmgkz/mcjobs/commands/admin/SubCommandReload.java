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
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class SubCommandReload {
    public static void command(CommandSender s, String l, String[] a) {
        String str = "";
        PrettyText text = new PrettyText();
        String name = "Console";
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player) {
            uuid = ((Player)s).getUniqueId();
            name = ((Player)s).getName();
            if(!s.hasPermission("mcjobs.admin.reload")) {
                str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("permission", uuid).addVariables("", name, l);
                text.formatPlayerText(str, (Player)s);
                return;
            }
        }
        
        McJobs.getPlugin().reloadConfig();

        PlayerJobs.getJobsList().clear();
        Leveler.getRanks().clear();
            
        McJobs.getPlugin().getServer().getScheduler().cancelTasks(McJobs.getPlugin());

        try {
            McJobs.getPlugin().mcloadconf(McJobs.getPlugin());
        } catch(Exception e){
            if(s instanceof Player){
                str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("failedreload", uuid).addVariables("", name, l);
                text.formatPlayerText(str, (Player)s);
            } else
                s.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("failedreload", uuid).addVariables("", name, l));
        }
            
        PlayerData.loadPlayerPerms();
            
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(McJobs.getPlugin(), new McJobsRemovePerm(), 1200L, 1200L);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(McJobs.getPlugin(), new McJobsPreComp(), 200L, 200L);
            
        if(s instanceof Player){
            str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("succeedreload", uuid).addVariables("", name, l);
            text.formatPlayerText(str, (Player)s);
        } else
            s.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("succeedreload", uuid).addVariables("", name, l));
    }
}
