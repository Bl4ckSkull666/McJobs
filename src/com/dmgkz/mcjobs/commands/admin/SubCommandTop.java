/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.admin;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class SubCommandTop {
    //jadm region set/remove {jobname} - Need WorldEdit
    public static void command(Player p, String[] a) {
        String str = "";
        PrettyText text = new PrettyText();
        if(!p.hasPermission("mcjobs.admin.region")) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("permission", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }
        
        if(!Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("missing-worldedit", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }
    }
}
