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
import com.dmgkz.mcjobs.commands.admin.SubCommandEntityMessage;
import com.dmgkz.mcjobs.commands.admin.SubCommandLanguage;
import com.dmgkz.mcjobs.commands.admin.SubCommandLevel;
import com.dmgkz.mcjobs.commands.admin.SubCommandRegion;
import com.dmgkz.mcjobs.commands.admin.SubCommandRegionMessage;
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

        if(!(s instanceof Player)) {
            s.sendMessage("This command can be run only ingame.");
            return true;
        }
        
        Player p = (Player)s;
        if(!p.hasPermission("mcjobs.admin")){
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("permission", p.getUniqueId()).addVariables("", p.getName(), l);
            text.formatPlayerText(str, p);
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
                SubCommandRegionMessage.command(p, a);
                return true;
            case "emsg":
                SubCommandEntityMessage.command(p, a);
                return true;
            case "region":
                SubCommandRegion.command(p, a);
                return true;
            case "language":
                SubCommandLanguage.command(p);
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
                list.add("language");
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
                    list.add("set");
                    list.add("delete");
                } else if(a.length == 3) {
                    if(a[1].equalsIgnoreCase("begin") || a[1].equalsIgnoreCase("remove") || a[1].equalsIgnoreCase("set") || a[1].equalsIgnoreCase("delete")) {
                        for(String job: PlayerJobs.getJobsList().keySet())
                            list.add(McJobs.getPlugin().getLanguage().getJobName(job, p.getUniqueId()));
                    } else if(a[1].equalsIgnoreCase("click")) {
                        list.add("change_page");
                        list.add("open_file");
                        list.add("open_url");
                        list.add("run_command");
                        list.add("suggest_command");
                    }
                } else if(a.length == 4) {
                    if(a[1].equalsIgnoreCase("begin") || a[1].equalsIgnoreCase("remove") || a[1].equalsIgnoreCase("set") || a[1].equalsIgnoreCase("delete")) {
                        for(String lang: McJobs.getPlugin().getLanguage().getAvaLangs())
                            list.add(McJobs.getPlugin().getLanguage().getLanguageName(lang, p.getUniqueId()));
                    }
                }
                
                list.add("[space]");
                list.add("[empty]");
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
