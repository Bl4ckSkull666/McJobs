package com.dmgkz.mcjobs.listeners;

import com.dmgkz.mcjobs.McJobs;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.data.CompData;
import java.util.ArrayList;
import org.bukkit.GameMode;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerFishEvent.State;

public class Fishing implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void fishEvent(PlayerFishEvent e) {
        Player play = e.getPlayer();
        if(!e.getState().equals(State.CAUGHT_ENTITY) && !e.getState().equals(State.CAUGHT_FISH))
            return;
        
        if(!(e.getCaught() instanceof Item))
            return;
        
        if(MCListeners.isMultiWorld()){
            if(!play.hasPermission("mcjobs.world.all") && !play.hasPermission("mcjobs.world." + play.getWorld().getName()))
                return;
        }
                
        if(play.getGameMode() == GameMode.CREATIVE){
            if(!play.hasPermission("mcjobs.paycreative"))
                return;
        }
        
        Item item = (Item)e.getCaught();
        ArrayList<String> jobs = McJobs.getPlugin().getHolder().getJobsHolder().getJobs("fishing");
        for(String sJob: jobs) {
            if(PlayerData.hasJob(play.getUniqueId(), sJob)) {
                CompCache comp = new CompCache(sJob, play.getLocation(), play, item.getItemStack().getType(), "fishing");
                CompData.getCompCache().add(comp);
            } 
        }
    }
}
