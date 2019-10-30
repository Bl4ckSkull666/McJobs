/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.admin;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.localization.LanguageCheck;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class SubCommandLanguage {
    public static void command(Player p) {
        p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("language.start", p.getUniqueId()).addVariables("", p.getName(), ""));
        Bukkit.getServer().getScheduler().runTaskAsynchronously(McJobs.getPlugin(), new LanguageCheck(p));
    }
}
