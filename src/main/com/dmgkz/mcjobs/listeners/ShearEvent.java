package com.dmgkz.mcjobs.listeners;

import com.dmgkz.mcjobs.McJobs;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.data.CompData;
import java.util.ArrayList;
import org.bukkit.GameMode;
import org.bukkit.entity.Sheep;

public class ShearEvent implements Listener {
    
    @EventHandler(priority = EventPriority.LOW)
    public void sheepShear(PlayerShearEntityEvent event){
        if(event.isCancelled())
            return;
        
        Player play = event.getPlayer();
        if(MCListeners.isMultiWorld()){
            if(!play.hasPermission("mcjobs.world.all") && !play.hasPermission("mcjobs.world." + play.getWorld().getName()))
                return;
        }
                
        if(play.getGameMode() == GameMode.CREATIVE){
            if(!play.hasPermission("mcjobs.paycreative"))
                return;
        }

        if(!(event.getEntity() instanceof Sheep)) {
            return;
        }
        
        Sheep theSheep = (Sheep)event.getEntity();
        ArrayList<String> jobs = McJobs.getPlugin().getHolder().getJobsHolder().getJobs("shear");
        for(String sJob: jobs) {            
            if(PlayerData.hasJob(play.getUniqueId(), sJob)) {
                CompCache comp = new CompCache(sJob, theSheep, play, "shear");
                CompData.getCompCache().add(comp);
            }
        }
        
    }
}
