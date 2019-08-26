package com.dmgkz.mcjobs.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.data.CompData;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;
import java.util.Map;

public class McmmoRepairListener implements Listener {

    @EventHandler
    public void mcMMOrepair(McMMOPlayerRepairCheckEvent event) {
        Player play = event.getPlayer();
        
        if(MCListeners.isMultiWorld()){
            if(!play.hasPermission("mcjobs.world.all") && !play.hasPermission("mcjobs.world." + play.getWorld().getName()))
                return;
        }

        for(Map.Entry<String, PlayerJobs> pair: PlayerJobs.getJobsList().entrySet()) {
            String sJob = pair.getKey();
            
            if(PlayerData.hasJob(play.getUniqueId(), sJob)){                
                CompCache comp = new CompCache(sJob, play.getLocation(), play, event.getRepairedObject(), "repair");
                CompData.getCompCache().add(comp);
            }
        }

    }
}
