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
import com.dmgkz.mcjobs.prettytext.PrettyText;

public class AdminCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        PrettyText text = new PrettyText();
        String str = "";
        if(!c.getName().equalsIgnoreCase("mcjobsadmin")) {
            s.sendMessage("Critical failure!");
            return true;
        }
        Player playsend = null;

        if(s instanceof Player){
            playsend = (Player)s;
            if(!playsend.hasPermission("mcjobs.admin")){
                str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("permission", playsend.getUniqueId()).addVariables("", playsend.getName(), l);
                text.formatPlayerText(str, playsend);
                return true;
            }
        }
        
        if(a.length == 0) {
            String version = McJobs.getPlugin().getDescription().getVersion();
            s.sendMessage(ChatColor.DARK_RED + "MC Jobs by " + ChatColor.GOLD + "RathelmMC till v" + ChatColor.GREEN + "3.1.2");
            s.sendMessage(ChatColor.DARK_RED + "Modified & Updated by " + ChatColor.GOLD + "Bl4ckSkull666 since v3.2.0");
            s.sendMessage(ChatColor.DARK_RED + "MC Jobs installed version " + ChatColor.GREEN + version);
            return true;
        }
        
        switch(a[0].toLowerCase()) {
            case "reload":
                SubCommandReload.command(s, l, a);
                return true;
            case "defaults":
                SubCommandDefaults.command(s, l, a);
                return true;
            case "addlevel":
                SubCommandLevel.command(s, l, a, "add");
                return true;
            case "setlevel":
                SubCommandLevel.command(s, l, a, "set");
                return true;
            case "remlevel":
                SubCommandLevel.command(s, l, a, "remove");
                return true;
            case "addexp":
                SubCommandExp.command(s, l, a, "add");
                return true;
            case "setexp":
                SubCommandExp.command(s, l, a, "set");
                return true;
            case "remexp":
                SubCommandExp.command(s, l, a, "remove");
                return true;
            case "add":
                SubCommandJob.command(s, l, a, "join"); 
                return true;
            case "remove":
                SubCommandJob.command(s, l, a, "leave");
                return true;
            case "list":
                SubCommandJob.command(s, l, a, "list");
                return true;
            case "info":
                SubCommandJob.command(s, l, a, "info");
                return true;
            case "sign":
                SubCommandSign.command(s, l, a);
                return true;
        }
        return false;
    }
}
