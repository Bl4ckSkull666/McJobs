/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.listeners;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.levels.Leveler;
import com.dmgkz.mcjobs.util.JobSign;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author Bl4ckSkull666
 */
public class OnPlayerMove implements Listener {
    private static List<JobSign> _statsSigns = new ArrayList<>();
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        for(Map.Entry<String, PlayerJobs> me: PlayerJobs.getJobsList().entrySet()) {
            if(me.getValue() != null && me.getValue().getData().getRegionPositions() != null) {
                if(me.getValue().getData().getRegionPositions().hasEntered(p.getUniqueId(), e.getTo())) {
                    me.getValue().getData().sendRegionMessage(p);
                }
            }
        }
        
        if(McJobs.getPlugin().getConfig().getBoolean("statistic-sign.use", false)) {
            for(JobSign js: _statsSigns) {
                if(!p.getLocation().getWorld().equals(js.getLocation().getWorld()))
                    continue;

                if(p.getLocation().distance(js.getLocation()) > 64)
                    continue;

                if(!(js.getLocation().getBlock().getState() instanceof Sign))
                    continue;

                ConfigurationSection cs = null;
                if(PlayerData.hasJob(p.getUniqueId(), js.getJob())) {
                    cs = McJobs.getPlugin().getConfig().getConfigurationSection("statistic-sign.template.has");
                } else {
                    cs = McJobs.getPlugin().getConfig().getConfigurationSection("statistic-sign.template.not");
                }

                if(cs == null)
                    continue;

                String job = McJobs.getPlugin().getLanguage().getJobName(js.getJob().toLowerCase(), p.getUniqueId());
                String rank = "";
                String level = "";
                if(PlayerData.hasJob(p.getUniqueId(), js.getJob())) {
                    int lv = PlayerData.getJobLevel(p.getUniqueId(), js.getJob());
                    rank = Leveler.getRank(lv);
                    level = String.valueOf(lv);
                }

                String[] newLines = new String[4];
                for(int i = 0; i < 4; i++) {
                    String tmp = cs.getString(String.valueOf(i+1), "");
                    tmp = tmp.replace("%job", job);
                    tmp = tmp.replace("%rank", rank);
                    tmp = tmp.replace("%level", level);
                    String spaces = freeSpaces(tmp);
                    tmp = tmp.replace("%free", spaces);
                    newLines[i] = ChatColor.translateAlternateColorCodes('&', tmp);
                }
                p.sendSignChange(js.getLocation(), newLines);
            }
        }
    }
    
    private String freeSpaces(String str) {
        String spac = "";
        str = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', str.replace("%free", "")));
        for(int i = str.length();i <= 16; i++)
            spac += " ";
        return spac;
    }
    
    public static void addSign(JobSign js) {
        _statsSigns.add(js);
    }
    
    public static void removeSign(JobSign js) {
        _statsSigns.remove(js);
    }
}
