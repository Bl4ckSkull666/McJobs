/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.jobs;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.localization.GetLanguage;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.scheduler.McJobsScoreboard;
import java.util.HashMap;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class SubCommandScoreboard {
    public static void command(Player p, String[] a) {
        HashMap<String, String> replaces = new HashMap<>();
        replaces.put("%p", p.getName());
        if(!p.hasPermission("mcjobs.scoreboard") && McJobs.getPlugin().getConfig().getBoolean("advanced.usePerms", true)) {
            GetLanguage.sendMessage(p, "permission", "&cYou don't have the needed permission to do this.", replaces);
            return;
        }
        
        if(a.length < 2) {
            GetLanguage.sendMessage(p, "scoreboard.info", "&cthe follow parameters are available, for the order: job,rank,level,hasexp,needexp,nextexp. For sort: asc,desc. To hide Scoreboard use none.", null);
            return;
        }
        
        switch(a[1].toLowerCase()) {
            case "job":
            case "level":
            case "rank":
            case "hasexp":
            case "needexp":
            case "nextexp":
                PlayerData.setScoreboardOrder(p.getUniqueId(), a[1]);
                GetLanguage.sendMessage(p, "scoreboard.order", "&aSet the new order successfull.", replaces);
                McJobsScoreboard.setScoreboard(p);
                return;
            case "asc":
            case "desc":
                PlayerData.setScoreboardSort(p.getUniqueId(), a[1]);
                GetLanguage.sendMessage(p, "scoreboard.sort", "&aSet the new sort successfull.", replaces);
                McJobsScoreboard.setScoreboard(p);
                return;
            case "none":
                PlayerData.setScoreboardOrder(p.getUniqueId(), a[1]);
                GetLanguage.sendMessage(p, "scoreboard.none", "&aThe Scoreboard will be hide now.", replaces);
                McJobsScoreboard.setScoreboard(p);
                return;
            default:
                GetLanguage.sendMessage(p, "scoreboard.info", "&cthe follow parameters are available, for the order: job,rank,level,hasexp,needexp,nextexp. For sort: asc,desc. To hide Scoreboard use none.", null);
        }
    }    
}
