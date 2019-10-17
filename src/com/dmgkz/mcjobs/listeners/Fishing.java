package com.dmgkz.mcjobs.listeners;

import com.dmgkz.mcjobs.McJobs;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.data.CompData;
import java.util.ArrayList;
import org.bukkit.GameMode;

public class Fishing implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void fishEvent(PlayerFishEvent event){
        Player play = event.getPlayer();
        EntityType fish;

        if(event.getCaught() != null)
            fish = event.getCaught().getType();
        else
            return;

        if(MCListeners.isMultiWorld()){
            if(!play.hasPermission("mcjobs.world.all") && !play.hasPermission("mcjobs.world." + play.getWorld().getName()))
                return;
        }
                
        if(play.getGameMode() == GameMode.CREATIVE){
            if(!play.hasPermission("mcjobs.paycreative"))
                return;
        }
        
        ArrayList<String> jobs = McJobs.getPlugin().getHolder().getJobsHolder().getJobs("fishing");
        for(String sJob: jobs) {
            if(PlayerData.hasJob(play.getUniqueId(), sJob)){
                CompCache comp = new CompCache(sJob, play.getLocation(), play, fish, "fishing");
                CompData.getCompCache().add(comp);

//                if(PlayerJobs.joblist.get(sJob).getData().compJob().compEntity(fish, play, "fishing")){
//                    play.sendMessage("You killed him!");
//                } 
            } 
        }
    }
}
