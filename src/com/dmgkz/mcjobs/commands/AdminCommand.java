package com.dmgkz.mcjobs.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.commands.admin.SubCommandExp;
import com.dmgkz.mcjobs.commands.admin.SubCommandJob;
import com.dmgkz.mcjobs.commands.admin.SubCommandDefaults;
import com.dmgkz.mcjobs.commands.admin.SubCommandLevel;
import com.dmgkz.mcjobs.commands.admin.SubCommandReload;
import com.dmgkz.mcjobs.commands.admin.SubCommandSign;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.TabCompleter;

public class AdminCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        PrettyText text = new PrettyText();
        String str = "";
        if(!c.getName().equalsIgnoreCase("mcjobsadmin")) {
            s.sendMessage("Critical failure!");
            return true;
        }
        Player playsend = null;

        if(!(s instanceof Player)) {
            s.sendMessage("This command can be run only ingame.");
            return true;
        }
        
        Player p = (Player)s;
        if(!playsend.hasPermission("mcjobs.admin")){
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("permission", playsend.getUniqueId()).addVariables("", playsend.getName(), l);
            text.formatPlayerText(str, playsend);
            return true;
        }
        
        if(a.length == 0) {
            JobsCommand.sendDefaultMessage(p);
            return true;
        }
        
        switch(a[0].toLowerCase()) {
            case "reload":
                SubCommandReload.command(p);
                return true;
            case "defaults":
                SubCommandDefaults.command(p);
                return true;
            case "addlevel":
                SubCommandLevel.command(p, a, "add");
                return true;
            case "setlevel":
                SubCommandLevel.command(p, a, "set");
                return true;
            case "remlevel":
                SubCommandLevel.command(p, a, "remove");
                return true;
            case "addexp":
                SubCommandExp.command(p, a, "add");
                return true;
            case "setexp":
                SubCommandExp.command(p, a, "set");
                return true;
            case "remexp":
                SubCommandExp.command(p, a, "remove");
                return true;
            case "add":
                SubCommandJob.command(p, a, "join"); 
                return true;
            case "remove":
                SubCommandJob.command(p, a, "leave");
                return true;
            case "list":
                SubCommandJob.command(p, a, "list");
                return true;
            case "info":
                SubCommandJob.command(p, a, "info");
                return true;
            case "sign":
                SubCommandSign.command(p, a);
                return true;
            case "rmsg":
                p.sendMessage("Comming soon");
                //SubCommandSign.command(p, a);
                return true;
            case "emsg":
                p.sendMessage("Comming soon");
                //SubCommandSign.command(p, a);
            case "region":
                p.sendMessage("Comming soon");
                return true;
        }
        return false;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String l, String[] a) {
        List<String> list = new ArrayList<>();
        if(s instanceof Player) {
            Player p = (Player)s;
            if(a.length == 0)
                return list;
            
            if(a.length == 1) {
                list.add("reload");
                list.add("defaults");
                list.add("addlevel");
                list.add("setlevel");
                list.add("remlevel");
                list.add("addexp");
                list.add("setexp");
                list.add("remexp");
                list.add("add");
                list.add("remove");
                list.add("list");
                list.add("info");
                list.add("sign");
                list.add("rmsg");
                list.add("emsg");
                list.add("region");
            } else if(a[0].equalsIgnoreCase("sign")) {
                if(a.length == 2) {
                    for(String job: PlayerJobs.getJobsList().keySet())
                        list.add(McJobs.getPlugin().getLanguage().getJobName(job, p.getUniqueId()));
                } else if(a.length == 3) {
                    list.add("join");
                    list.add("leave");
                    list.add("info");
                    list.add("npc");
                    list.add("region");
                    list.add("top");
                }
            } else if(a[0].equalsIgnoreCase("region")) {
                if(a.length == 2) {
                    list.add("set");
                    list.add("remove");
                } else if(a.length == 3) {
                    for(String job: PlayerJobs.getJobsList().keySet())
                        list.add(McJobs.getPlugin().getLanguage().getJobName(job, p.getUniqueId()));
                }
            } else if(a[0].equalsIgnoreCase("rmsg") || a[0].equalsIgnoreCase("emsg")) {
                if(a.length == 2) {
                    list.add("begin");
                    list.add("add");
                    list.add("break");
                    list.add("hover");
                    list.add("click");
                    list.add("save");
                    list.add("remove");
                } else if(a.length == 3) {
                    if(a[1].equalsIgnoreCase("begin") || a[1].equalsIgnoreCase("remove")) {
                        for(String job: PlayerJobs.getJobsList().keySet())
                            list.add(McJobs.getPlugin().getLanguage().getJobName(job, p.getUniqueId()));
                    } else if(a[1].equalsIgnoreCase("hover")) {
                        list.add("achievement");
                        list.add("entity");
                        list.add("item");
                        list.add("text");
                    } else if(a[1].equalsIgnoreCase("click")) {
                        list.add("change_page");
                        list.add("open_file");
                        list.add("open_url");
                        list.add("run_command");
                        list.add("suggest_command");
                    }
                }
            } else if(a[0].equalsIgnoreCase("add") || a[0].equalsIgnoreCase("remove")) {
                if(a.length == 2) {
                    for(String job: PlayerJobs.getJobsList().keySet())
                        list.add(McJobs.getPlugin().getLanguage().getJobName(job, p.getUniqueId()));
                }
            }
            Collections.sort(list);
        }
        return list;
    }
}
