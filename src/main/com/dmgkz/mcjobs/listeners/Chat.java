/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.listeners;

import com.dmgkz.mcjobs.McJobs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author Bl4ckSkull666
 */
public class Chat implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setMessage("[McJobs]" + McJobs.getPlugin().getLanguage().checkForPlaceholderAPI(e.getPlayer().getUniqueId(), e.getMessage()));
    }
}
