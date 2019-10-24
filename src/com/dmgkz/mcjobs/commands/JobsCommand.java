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
import com.dmgkz.mcjobs.commands.jobs.SubCommandLanguage;
import com.dmgkz.mcjobs.commands.jobs.SubCommandLeave;
import com.dmgkz.mcjobs.commands.jobs.SubCommandList;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.util.StringToNumber;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.command.TabCompleter;

public class JobsCommand implements CommandExecutor, TabCompleter {

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
            sendDefaultMessage(p);
            return true;
        } else if(a.length == 1) {
            switch(a[0].toLowerCase()) {
                case "list":
                    SubCommandList.command(p);            
                    return true;
                case "help":
                    SubCommandHelp.command(p, 1);
                    return true;
                case "language":
                    SubCommandLanguage.command(p, "");
                    return true;
                default:
                    String job = McJobs.getPlugin().getLanguage().getOriginalJobName(a[0].toLowerCase(), p.getUniqueId());
                    if(!(p.hasPermission("mcjobs.jobs.info") || p.hasPermission("mcjobs.jobs.all")) && McJobs.getPlugin().getConfig().getBoolean("advanced.usePerms", true)) { 
                        p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobCommand("permission", p.getUniqueId()).addVariables("", p.getName(), ""));
                        return true;
                    }
                    if(!PlayerJobs.getJobsList().containsKey(job.toLowerCase())) {
                        p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobCommand("nojob", p.getUniqueId()).addVariables("", p.getName(), ""));
                        return true;
                    }
                    PlayerJobs.getJobsList().get(job.toLowerCase()).getData().display().showPlayerJob(p, p.getUniqueId());
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
                    SubCommandHideShow.command(p, McJobs.getPlugin().getLanguage().getOriginalJobName(a[1].toLowerCase(), p.getUniqueId()).toLowerCase(), true);
                    return true;
                case "hide":
                    SubCommandHideShow.command(p, McJobs.getPlugin().getLanguage().getOriginalJobName(a[1].toLowerCase(), p.getUniqueId()).toLowerCase(), false);
                    return true;
                case "join":
                    SubCommandJoin.command(p, McJobs.getPlugin().getLanguage().getOriginalJobName(a[1].toLowerCase(), p.getUniqueId()).toLowerCase());
                    return true;
                case "leave":
                    SubCommandLeave.command(p, McJobs.getPlugin().getLanguage().getOriginalJobName(a[1].toLowerCase(), p.getUniqueId()).toLowerCase());
                    return true;
                case "info":
                    SubCommandInfo.command(p, McJobs.getPlugin().getLanguage().getOriginalJobName(a[1].toLowerCase(), p.getUniqueId()).toLowerCase());
                    return true;
                case "language":
                    SubCommandLanguage.command(p, a[1].toLowerCase());
                    return true;
                default:
                    sendDefaultMessage(p);
                    return true;
            }
        }
        return true;
    }
    
    public static void sendDefaultMessage(Player p) {
        String version = McJobs.getPlugin().getDescription().getVersion();
        p.sendMessage(ChatColor.DARK_RED + "MC Jobs by " + ChatColor.GOLD + "RathelmMC till v" + ChatColor.GREEN + "3.1.2");
        p.sendMessage(ChatColor.DARK_RED + "Modified & Updated by " + ChatColor.GOLD + "Bl4ckSkull666 since v3.2.0");
        p.sendMessage(ChatColor.DARK_RED + "MC Jobs installed version " + ChatColor.GREEN + version);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String l, String[] a) {
        List<String> list = new ArrayList<>();
        if(s instanceof Player) {
            Player p = (Player)s;
            if(a.length == 1) {
                list.add("list");
                list.add("help");
                list.add("show");
                list.add("hide");
                list.add("join");
                list.add("leave");
                list.add("info");
                list.add("language");
                for(String job: PlayerJobs.getJobsList().keySet())
                    list.add(unColor(McJobs.getPlugin().getLanguage().getJobName(job, p.getUniqueId())));
            } else if(a.length == 2) {
                if(a[0].equalsIgnoreCase("help")) {
                    for(int i = 1; i <= McJobs.getPlugin().getLanguage().getSpaces("numhelp", p.getUniqueId()); i++) {
                        list.add(String.valueOf(i));
                    }
                } else if(a[0].equalsIgnoreCase("language")) {
                    for(String lang: McJobs.getPlugin().getLanguage().getAvaLangs())
                        list.add(unColor(McJobs.getPlugin().getLanguage().getLanguageName(lang, p.getUniqueId())));
                } else if(a[0].equalsIgnoreCase("join")) {
                    for(String job: PlayerJobs.getJobsList().keySet()) {
                        if(PlayerData.isJoinable(p.getUniqueId(), job))
                            list.add(unColor(McJobs.getPlugin().getLanguage().getJobName(job, p.getUniqueId())));
                    }
                } else if(a[0].equalsIgnoreCase("leave")) {
                    for(String job: PlayerJobs.getJobsList().keySet()) {
                        if(PlayerData.hasJob(p.getUniqueId(), job))
                            list.add(unColor(McJobs.getPlugin().getLanguage().getJobName(job, p.getUniqueId())));
                    }
                } else {
                    for(String job: PlayerJobs.getJobsList().keySet())
                        list.add(unColor(McJobs.getPlugin().getLanguage().getJobName(job, p.getUniqueId())));
                }
                Collections.sort(list);
            }
        }
        return list;
    }
    
    private String unColor(String str) {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', str));
    }
}
