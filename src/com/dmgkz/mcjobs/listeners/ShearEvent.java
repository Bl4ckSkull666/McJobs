package com.dmgkz.mcjobs.listeners;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.data.CompData;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
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
        for(Map.Entry<String, PlayerJobs> pair: PlayerJobs.getJobsList().entrySet()) {
            String sJob = pair.getKey();
            
            if(PlayerData.hasJob(play.getUniqueId(), sJob)) {
                CompCache comp = new CompCache(sJob, theSheep, play, "shear");
                CompData.getCompCache().add(comp);
                
//                if(PlayerJobs.joblist.get(sJob).getData().compJob().compEntity(mob, play, "defeat")){
//                    play.sendMessage("You killed him!");
//                }
            }
        }
        
    }
}
