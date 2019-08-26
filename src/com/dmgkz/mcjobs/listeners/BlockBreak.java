package com.dmgkz.mcjobs.listeners;

import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PitchJobs;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.data.CompData;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import com.dmgkz.mcjobs.util.MatClass;
import com.dmgkz.mcjobs.util.Utils;


import de.diddiz.LogBlock.QueryParams.BlockChangeType;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

public class BlockBreak implements Listener{
    private static boolean _noPitch = false; 
    
    @EventHandler(priority = EventPriority.LOW)
    public void blockBreak(BlockBreakEvent event) {
        if(event.isCancelled())
            return; 
        
        if(event.getBlock().getType() == null)
            return;
        
        PrettyText text = new PrettyText();
        Player play = event.getPlayer();
        if(event.getBlock() instanceof Sign && McJobs.getPlugin().getSignManager().isSign(event.getBlock().getLocation())) {
            if(!play.hasPermission("mcjobs.admin")) {
                String str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("permission", play.getUniqueId()).addVariables("", play.getName(), "");
                text.formatPlayerText(str, play);
                event.setCancelled(true);
                return;
            }
            
            McJobs.getPlugin().getSignManager().removeSign(event.getBlock().getLocation());
            String str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("sign-removed", play.getUniqueId()).addVariables("", play.getName(), "");
            text.formatPlayerText(str, play);
            return;
        } 
        MatClass block = new MatClass(event.getBlock().getType());
        if(event.getBlock().getState().getData().toItemStack(1).getDurability() > 0) {
            block.setWorth(event.getBlock().getState().getData().toItemStack(1).getDurability());
        }
        
        Location loc = event.getBlock().getLocation();
        Integer timer = MCListeners.getTimeInMins();

        if(MCListeners.isWorldGuard()) {
            if(!McJobs.getWorldGuard().canBuild(play, loc))
                return;
        }
        
        if(MCListeners.isMultiWorld()) {
            if(!play.hasPermission("mcjobs.world.all") && !play.hasPermission("mcjobs.world." + play.getWorld().getName()))
                return;
        }

        if(play.getGameMode() == GameMode.CREATIVE) {
            if(!play.hasPermission("mcjobs.paycreative"))
                return;
        }

        if(PlayerData.getJobCount(play.getUniqueId()) == 0 && !_noPitch) {
            if((play.hasPermission("mcjobs.jobs.join") ||
                    play.hasPermission("mcjobs.jobs.*") ||
                    play.hasPermission("mcjobs.jobs.all") ||
                    !MCListeners.isPerms()))
                return;
            
            if(!PlayerData.getSeenPitch(play.getUniqueId()))
                PitchJobs.pitchJobs(play);
        }
        
        for(Map.Entry<String, PlayerJobs> me: PlayerJobs.getJobsList().entrySet()) {
            String sJob = me.getKey();
            if(PlayerData.hasJob(play.getUniqueId(), sJob)) {
                if(!Utils.hasNeededTool(play, sJob, "break"))
                    continue;
                    
                if(McJobs.getPlugin().isLogBlock()) {
                    if(McJobs.getPlugin().getBlockLogging().checkLogBlock(play.getWorld(), play, event.getBlock().getLocation(), BlockChangeType.DESTROYED, timer))
                        return;
                } else {
                    if(McJobs.getPlugin().getBlockLogging().checkBuiltIn(loc, play, true))
                        return;
                }
                
                CompCache comp = new CompCache(sJob, loc, play, block, "break");
                CompData.getCompCache().add(comp);
            }
        }
    }

    public static void setNoPitch(boolean b) {
        _noPitch = b;
    }
}
