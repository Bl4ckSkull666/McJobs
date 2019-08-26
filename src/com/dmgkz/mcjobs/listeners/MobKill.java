package com.dmgkz.mcjobs.listeners;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.dmgkz.mcjobs.logging.BlockSpawners;
import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PlayerCache;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.data.CompData;
import org.bukkit.GameMode;

public class MobKill implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void mobDeath(EntityDeathEvent event){
        int length = MCListeners.getSpawnDist();
        LivingEntity mob = event.getEntity();     
        Player play = null;
         
        if(mob.getKiller() instanceof Player)
             play = mob.getKiller();
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
        
        if(event.getEntity() instanceof Player) {
            Player killed = (Player)event.getEntity();
            for(Map.Entry<String, PlayerJobs> pair: PlayerJobs.getJobsList().entrySet()) {
                String sJob = pair.getKey();

                if(PlayerData.hasJob(play.getUniqueId(), sJob)){
                    CompCache comp = new CompCache(sJob, mob.getLocation(), play, killed, "pvp");
                    CompData.getCompCache().add(comp);
                }
            }
        } else {
            if(BlockSpawners.isSpawnerNearby(mob.getLocation(), length) && !MCListeners.isPaySpawner()){
                return;
            }

            for(Map.Entry<String, PlayerJobs> pair: PlayerJobs.getJobsList().entrySet()) {
                String sJob = pair.getKey();

                if(PlayerData.hasJob(play.getUniqueId(), sJob)){
                    CompCache comp = new CompCache(sJob, mob.getLocation(), play, mob.getType(), "defeat");
                    CompData.getCompCache().add(comp);
                }
            }
        }
    }
}
