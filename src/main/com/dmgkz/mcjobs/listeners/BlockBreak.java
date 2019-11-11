package com.dmgkz.mcjobs.listeners;


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
import com.dmgkz.mcjobs.playerjobs.data.CompData;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import com.dmgkz.mcjobs.util.Utils;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;


import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;

public class BlockBreak implements Listener{
    private static boolean _noPitch = false; 
    
    @EventHandler(priority = EventPriority.LOW)
    public void blockBreak(BlockBreakEvent event) {
        if(event.isCancelled())
            return; 
        
        if(event.getBlock().getType().equals(Material.AIR))
            return;
        
        PrettyText text = new PrettyText();
        Player play = event.getPlayer();
        if(event.getBlock().getState() instanceof Sign && McJobs.getPlugin().getSignManager().isSign(event.getBlock().getLocation())) {
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
        
        Location loc = event.getBlock().getLocation();

        if(MCListeners.isWorldGuard()) {
            RegionQuery rq = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            if(!rq.testState(BukkitAdapter.adapt(loc), WorldGuardPlugin.inst().wrapPlayer(play), Flags.BUILD))
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

        ArrayList<String> jobs = McJobs.getPlugin().getHolder().getJobsHolder().getJobs("break");
        for(String sJob: jobs) {
            if(PlayerData.hasJob(play.getUniqueId(), sJob)) {
                if(!Utils.hasNeededTool(play, sJob, "break"))
                    continue;
                
                CompCache comp = new CompCache(sJob, loc, play, event.getBlock().getType(), "break");
                CompData.getCompCache().add(comp);
            }
        }
    }

    public static void setNoPitch(boolean b) {
        _noPitch = b;
    }
}
