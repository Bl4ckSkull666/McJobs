package com.dmgkz.mcjobs.listeners;

import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.data.CompData;
import com.dmgkz.mcjobs.util.MatClass;

import de.diddiz.LogBlock.QueryParams.BlockChangeType;

public class BlockPlace implements Listener {
    
    @EventHandler(priority = EventPriority.LOW)
    public void blockPlace(BlockPlaceEvent event) {
        if(event.isCancelled())
            return;
        if(!event.canBuild())
            return;
        if(event.getBlock().getType() == null)
            return;
        
        Player play = event.getPlayer();
        Material block = event.getBlock().getType();
        Material replaced = event.getBlockReplacedState().getType();
        Location loc = event.getBlock().getLocation();
        Integer timer = MCListeners.getTimeInMins();
                

        if((block == Material.DIODE_BLOCK_OFF || 
                block == Material.REDSTONE_TORCH_OFF || 
                block == Material.RAILS || 
                block == Material.DETECTOR_RAIL || 
                block == Material.POWERED_RAIL) && 
                (replaced == Material.WATER || 
                replaced == Material.STATIONARY_WATER || 
                replaced == Material.LAVA || 
                replaced == Material.STATIONARY_LAVA))
            return;
        
        if(MCListeners.isWorldGuard()){
            if(!McJobs.getWorldGuard().canBuild(play, loc))
                return;
        }

        if(MCListeners.isMultiWorld()){
            if(!play.hasPermission("mcjobs.world.all") && !play.hasPermission("mcjobs.world." + play.getWorld().getName()))
                return;
        }

        if(play.getGameMode() == GameMode.CREATIVE){
            if(!play.hasPermission("mcjobs.paycreative"))
                return;
        }
        
        MatClass MatBlock = new MatClass(event.getBlock().getType());
        if(event.getBlock().getState().getData().toItemStack().getDurability() > 0) {
            MatBlock.setWorth(event.getBlock().getState().getData().toItemStack().getDurability());
        }
        
        for(Map.Entry<String, PlayerJobs> pair: PlayerJobs.getJobsList().entrySet()) {
            String sJob = pair.getKey();
            if(PlayerData.hasJob(play.getUniqueId(), sJob)){

                if(McJobs.getPlugin().isLogBlock()){
                    if(McJobs.getPlugin().getBlockLogging().checkLogBlock(play.getWorld(), play, event.getBlock().getLocation(), BlockChangeType.CREATED, timer))
                        return;
                }
                else{
                    if(McJobs.getPlugin().getBlockLogging().checkBuiltIn(loc, play, false))
                        return;
                }

                CompCache comp = new CompCache(sJob, loc, play, MatBlock, "place");
                CompData.getCompCache().add(comp);
            }
        }
    }
}
