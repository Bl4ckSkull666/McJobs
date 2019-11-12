/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.admin;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class SubCommandDefaults {
    public static void command(Player p) {
        String str = "";
        PrettyText text = new PrettyText();
        if(!p.hasPermission("mcjobs.admin.defaults")) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("permission", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }                   
        
        McJobs.getPlugin().getConfig().options().copyDefaults(true);
        McJobs.getPlugin().saveConfig();
        
        str = ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("defaults", p.getUniqueId()).addVariables("", p.getName(), "");
        text.formatPlayerText(str, p);
    }
}
