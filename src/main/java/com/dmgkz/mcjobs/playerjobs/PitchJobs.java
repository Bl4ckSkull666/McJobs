package com.dmgkz.mcjobs.playerjobs;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;

public class PitchJobs {
    public static void pitchJobs(Player play){
        play.sendMessage(ChatColor.DARK_GREEN + McJobs.getPlugin().getLanguage().getPitch("line0", play.getUniqueId()).addVariables("", play.getName(), "") + ChatColor.DARK_AQUA + " MC Jobs.");
        play.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");
        play.sendMessage(ChatColor.GREEN + McJobs.getPlugin().getLanguage().getPitch("line1", play.getUniqueId()).addVariables("", play.getName(), ""));
        play.sendMessage(ChatColor.GREEN + McJobs.getPlugin().getLanguage().getPitch("line2", play.getUniqueId()).addVariables("", play.getName(), ""));
        play.sendMessage(ChatColor.GREEN + McJobs.getPlugin().getLanguage().getPitch("line3", play.getUniqueId()).addVariables("", play.getName(), ""));

        PlayerData.setSeenPitch(play.getUniqueId(), true);
        PlayerData.savePlayerCache(play.getUniqueId());
    }
}