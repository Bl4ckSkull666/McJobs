package com.dmgkz.mcjobs.listeners;

import com.dmgkz.mcjobs.McJobs;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.data.CompData;
import java.util.ArrayList;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class Crafting implements Listener {
    private final HashMap<Location, MyCraft> _hCraft = new HashMap<>();
    
    @EventHandler(priority = EventPriority.LOW)
    public void CraftItem(CraftItemEvent e) {
        /*if(e.getCurrentItem() != null) {
            ItemStack item = e.getCurrentItem();
            McJobs.getPlugin().getLogger().info("CurrentItem " + item.getType().name() + " has " + item.getAmount());
        }
        
        if(e.getRecipe().getResult() != null) {
            ItemStack item =  e.getRecipe().getResult();
            McJobs.getPlugin().getLogger().info("Recipe Item " + item.getType().name() + " has " + item.getAmount());
        }*/

        if(_hCraft.containsKey(e.getInventory().getLocation())) {
            MyCraft mc = _hCraft.get(e.getInventory().getLocation());
            //McJobs.getPlugin().getLogger().info("MyCraft saved " + mc.getResult().name() + " with amount of " + mc.getAmount());
            
            List<HumanEntity> pList = e.getViewers();
            ArrayList<String> jobs = McJobs.getPlugin().getHolder().getJobsHolder().getJobs("craft");
            for(HumanEntity he: pList) {
                if(!(he instanceof Player))
                    continue;

                Player p = (Player)he;
                if(MCListeners.isMultiWorld()) {
                    if(!p.hasPermission("mcjobs.world.all") && !p.hasPermission("mcjobs.world." + p.getWorld().getName()))
                        continue;
                }

                if(p.getGameMode() == GameMode.CREATIVE) {
                    if(!p.hasPermission("mcjobs.paycreative"))
                        continue;
                }

                for(String sJob: jobs) {
                    if(PlayerData.hasJob(p.getUniqueId(), sJob)) {
                        CompCache comp = new CompCache(sJob, e.getInventory().getLocation(), p, mc.getResult(), "craft");
                        CompData.getCompCache().add(comp);
                    }
                }
            }
            _hCraft.remove(e.getInventory().getLocation());
        }
    }
        
    @EventHandler(priority = EventPriority.LOW)
    public void preCraftEvent(PrepareItemCraftEvent e) {
        ItemStack result = e.getInventory().getItem(0);
        if(result != null)
            _hCraft.put(e.getInventory().getLocation(), new MyCraft(result.getType(), result.getAmount()));
    }
    
    private class MyCraft {
        private final Material _result;
        private final int _amount;
        
        public MyCraft(Material mat, int amount) {
           _result = mat;
           _amount = amount;
        }
        
        public Material getResult() {
            return _result;
        }
        
        public int getAmount() {
            return _amount;
        }
    }
}
