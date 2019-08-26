/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.listeners;

import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 *
 * @author Bl4ckSkull666
 */
public class OnPlayerInteractEntity implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        Sign s = null;
        World w = e.getRightClicked().getWorld();
        int x = e.getRightClicked().getLocation().getBlockX();
        int z = e.getRightClicked().getLocation().getBlockZ();
        int yCheck = Math.max(e.getRightClicked().getLocation().getBlockY()-5, 1);
        for(int y = e.getRightClicked().getLocation().getBlockY()-1; y >= yCheck; y--) {
            Block b = w.getBlockAt(x, y, z);
            if(b instanceof Sign) {
                s = (Sign)b;
                break;
            }
        }
        
        if(s != null) {
            if(s.getLine(0).toLowerCase().equals("[mcjobs]")) {
                String job = s.getLine(2);
                if(PlayerJobs.getJobsList().containsKey(job)) {
                    PlayerJobs.getJobsList().get(job).getData().sensSignMessage(e.getPlayer());
                }
            }
        }
    }
}
