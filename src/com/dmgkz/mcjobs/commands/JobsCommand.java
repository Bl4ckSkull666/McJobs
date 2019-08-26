package com.dmgkz.mcjobs.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.commands.jobs.SubCommandHelp;
import com.dmgkz.mcjobs.commands.jobs.SubCommandHideShow;
import com.dmgkz.mcjobs.commands.jobs.SubCommandInfo;
import com.dmgkz.mcjobs.commands.jobs.SubCommandJoin;
import com.dmgkz.mcjobs.commands.jobs.SubCommandLeave;
import com.dmgkz.mcjobs.commands.jobs.SubCommandList;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.util.StringToNumber;
import java.util.UUID;

public class JobsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!c.getName().equalsIgnoreCase("mcjobs")) {
            s.sendMessage("JobsCommand failure!");
            return false;
        }

        if(!(s instanceof Player)) {
            s.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobCommand("playeronly", UUID.fromString("00000000-0000-0000-0000-000000000000")).addVariables("", "", ""));
            return true;
        }

        Player p = (Player)s;
        
        if(a.length == 0) {
            String version = McJobs.getPlugin().getDescription().getVersion();
            s.sendMessage(ChatColor.DARK_RED + "MC Jobs by " + ChatColor.GOLD + "RathelmMC till v" + ChatColor.GREEN + "3.1.2");
            s.sendMessage(ChatColor.DARK_RED + "Modified & Updated by " + ChatColor.GOLD + "Bl4ckSkull666 since v3.2.0");
            s.sendMessage(ChatColor.DARK_RED + "MC Jobs installed version " + ChatColor.GREEN + version);
            return true;
        } else if(a.length == 1) {
            switch(a[0].toLowerCase()) {
                case "list":
                    SubCommandList.command(p, l);            
                    return true;
                case "help":
                    SubCommandHelp.command(p, 1);
                    return true;
                default:
                    if(!(p.hasPermission("mcjobs.jobs.info") || p.hasPermission("mcjobs.jobs.all")) && McJobs.getPlugin().getConfig().getBoolean("advanced.usePerms", true)) { 
                        p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobCommand("permission", p.getUniqueId()).addVariables("", p.getName(), ""));
                        return true;
                    }
                    if(!PlayerJobs.getJobsList().containsKey(a[0].toLowerCase())) {
                        p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobCommand("nojob", p.getUniqueId()).addVariables("", p.getName(), ""));
                        return true;
                    }
                    PlayerJobs.getJobsList().get(a[0].toLowerCase()).getData().display().showJob(p);
                    return true;
            }
        } else if(a.length == 2) {
            switch(a[0].toLowerCase()) {
                case "help":
                    if(!StringToNumber.isPositiveNumber(a[1])){
                        p.sendMessage(McJobs.getPlugin().getLanguage().getJobHelp("nohelp", p.getUniqueId()).addVariables("", p.getName(), a[1]));
                        return true;
                    }
                    SubCommandHelp.command(p, Integer.parseInt(a[1]));
                    return true;
                case "show":
                    SubCommandHideShow.command(p, a[1], true);
                    return true;
                case "hide":
                    SubCommandHideShow.command(p, a[1], false);
                    return true;
                case "join":
                    SubCommandJoin.command(p, a);
                    return true;
                case "leave":
                    SubCommandLeave.command(p, a);
                    return true;
                case "info":
                    SubCommandInfo.command(p, a);
                    return true;
                default:
                    String version = McJobs.getPlugin().getDescription().getVersion();
                    p.sendMessage(ChatColor.DARK_RED + "MC Jobs by " + ChatColor.GOLD + "RathelmMC till v" + ChatColor.GREEN + "3.1.2");
                    p.sendMessage(ChatColor.DARK_RED + "Modified & Updated by " + ChatColor.GOLD + "Bl4ckSkull666 since v3.2.0");
                    p.sendMessage(ChatColor.DARK_RED + "MC Jobs installed version " + ChatColor.GREEN + version);
                    return true;
            }
        }
        return true;
    }
}
