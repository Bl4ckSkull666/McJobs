package com.dmgkz.mcjobs.listeners;

import com.dmgkz.mcjobs.McJobs;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.data.CompData;
import java.util.ArrayList;

public class Crafting implements Listener{

    private static final HashMap<Player, Material> hCraft = new HashMap<>();
    private static final HashMap<Player, Material> hRepair = new HashMap<>();
    
    @EventHandler(priority = EventPriority.LOW)
    public void craftEvent(CraftItemEvent event) {
        if(event.isCancelled())
            return;
        
        HumanEntity crafter = event.getWhoClicked();
        Player play = (Player) crafter;
        Material item = event.getCurrentItem().getType();
        
        if(MCListeners.isMultiWorld()){
            if(!play.hasPermission("mcjobs.world.all") && !play.hasPermission("mcjobs.world." + play.getWorld().getName()))
                return;
        }

        
        ArrayList<String> jobs = McJobs.getPlugin().getHolder().getJobsHolder().getJobs("craft");
        for(String sJob: jobs) {
            if(PlayerData.hasJob(play.getUniqueId(), sJob)){
                if(hRepair.containsKey(play)){
                    if(hRepair.get(play).equals(item)){
                        CompCache comp = new CompCache(sJob, play.getLocation(), play, event.getCurrentItem(), "repair");
                        CompData.getCompCache().add(comp);                   
                    }
                }
                if(hCraft.containsKey(play)){
                    if(hCraft.get(play).equals(item)){
                        CompCache comp = new CompCache(sJob, play.getLocation(), play, event.getCurrentItem(), "craft");
                        CompData.getCompCache().add(comp);
                    }
                }
            }
        }
        hRepair.remove(play);
        hCraft.remove(play);
    }
        
    @EventHandler(priority = EventPriority.LOW)
    public void preCraftEvent(PrepareItemCraftEvent event){
        if(event.getViewers() == null)
            return;
        if(event.getRecipe() == null)
            return;
        
        List<HumanEntity> playlist = event.getViewers();
        Iterator<HumanEntity> it = playlist.iterator();
        Material mat = event.getRecipe().getResult().getType();
        while(it.hasNext()) {
            HumanEntity he = it.next();
            if(he instanceof Player) {
                Player play = (Player)he;
                if(event.isRepair())
                    hRepair.put(play, mat);
                else
                    hCraft.put(play, mat);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void invCloseEvent(InventoryCloseEvent event) {
        Player play = (Player) event.getPlayer();
        if(hRepair.containsKey(play))
            hRepair.remove(play);
        if(hCraft.containsKey(play))
            hCraft.remove(play);
    }

    public static HashMap<Player, Material> getRepair() {
        return hRepair;
    }

    public static HashMap<Player, Material> getCraft() {
        return hCraft;
    }
}
