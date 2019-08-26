/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.listeners;

import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import java.util.Map;
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
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        for(Map.Entry<String, PlayerJobs> me: PlayerJobs.getJobsList().entrySet()) {
            if(me.getValue() != null && me.getValue().getData().getRegionPositions() != null) {
                if(me.getValue().getData().getRegionPositions().hasEntered(p.getUniqueId(), e.getTo()))
                    me.getValue().getData().sendRegionMessage(p);
            }
        }
    }
}
