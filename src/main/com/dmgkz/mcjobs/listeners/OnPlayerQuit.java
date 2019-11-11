package com.dmgkz.mcjobs.listeners;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;


import com.dmgkz.mcjobs.playerdata.PlayerData;

public class OnPlayerQuit implements Listener{

    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuitting(PlayerQuitEvent event){
        Player play = event.getPlayer();
        if(PlayerData.isCacheOld(play.getUniqueId())){
            PlayerData.removePlayerCache(play.getUniqueId());
        } else {
            PlayerData.savePlayerCache(play.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onKicked(PlayerKickEvent event){
        Player play = event.getPlayer();
        if(PlayerData.isCacheOld(play.getUniqueId())){
            PlayerData.removePlayerCache(play.getUniqueId());
        } else {
            PlayerData.savePlayerCache(play.getUniqueId());
        }
    }
}
