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
 * @author Bl4ckSkull666
 */
public class SubCommandRegion {
    //jadm region set/remove {jobname} - Need WorldEdit
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
    }
}
