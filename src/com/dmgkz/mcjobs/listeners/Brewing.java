package com.dmgkz.mcjobs.listeners;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.data.CompData;
import com.dmgkz.mcjobs.util.PotionTypeAdv;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;


/*
* Slot 0 Potion Left CRAFTING
* Slot 1 Potion Middle CRAFTING
* Slot 2 Potion Right CRAFTING
* Slot 3 Zutat FUEL
* Slot 4 Burning CRAFTING
*/
public class Brewing implements Listener{
    private final HashMap<Location, Player> hBrewStands = new HashMap<>();
    private final Logger log = McJobs.getPlugin().getLogger();
    
    /*
    * Löst aus wenn fertig gestellt.
    */
    @EventHandler(priority = EventPriority.LOW)
    public void brewing(BrewEvent e) {
        if(e.isCancelled())
            return;
        
        BrewingStand bStand = e.getContents().getHolder();
        Player play = null;
        
        if(hBrewStands.containsKey(bStand.getLocation()))
            play = hBrewStands.get(bStand.getLocation());
        else
            return;
        
        if(!play.isOnline())
            return;
        
        play.sendMessage("You Potion is ready in Brewstand.");
        if(MCListeners.isMultiWorld()){
            if(!play.hasPermission("mcjobs.world.all") && !play.hasPermission("mcjobs.world." + play.getWorld().getName()))
                return;
        }
                
        if(play.getGameMode() == GameMode.CREATIVE){
            if(!play.hasPermission("mcjobs.paycreative"))
                return;
        }
        
        ItemStack ingred = e.getContents().getIngredient();
        int amount = 0;
        ItemStack item = null;
        for(int i = 0; i <= 2; i++) {
            ItemStack tmpItem = e.getContents().getItem(i);
            if(tmpItem == null || (!tmpItem.getType().equals(Material.POTION) && !tmpItem.getType().equals(Material.SPLASH_POTION) && !tmpItem.getType().equals(Material.LINGERING_POTION)))
                continue;

            item = tmpItem;
            amount++;
        }
                
        if(item == null)
            return;
                
        PotionTypeAdv potion = McJobs.getPlugin().getHolder().getPotions().getPotion(item);
        if(potion == null)
            return;
                    
        potion = potion.getResultPotion(ingred.getType());
        if(potion == null)
            return;
        
        play.sendMessage("Your Brewstand has " + amount + " of Potion ready.");
        ArrayList<String> jobs = McJobs.getPlugin().getHolder().getJobsHolder().getJobs("potion");
        for(String sJob: jobs) {
            if(PlayerData.hasJob(play.getUniqueId(), sJob)) {
                for(int i = 0; i < amount; i++) {
                    CompCache comp = new CompCache(sJob, play.getLocation(), play, potion, "potion");
                    CompData.getCompCache().add(comp);
                }
            }
        }
        hBrewStands.remove(bStand.getLocation());
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void getBrewStand(InventoryClickEvent e) {
        if(e.getInventory().getLocation() == null)
            return;
        
        Player play = (Player) e.getWhoClicked();
        if(e.isCancelled())
            return;
        
        if(e.getInventory() instanceof BrewerInventory) {
            BrewerInventory binv = (BrewerInventory)e.getInventory();
            BrewingStand bStand = binv.getHolder();
            
            play.sendMessage("Oh you interact on Brewstand.");
            Bukkit.getScheduler().runTaskLater(McJobs.getPlugin(), new checkBrewingStarted(bStand.getLocation(), play), 20);
        }
    }
    
    public class checkBrewingStarted implements Runnable {
        private final Location _bStand;
        private final Player _p;
        
        public checkBrewingStarted(Location bStand, Player p) {
            _bStand = bStand;
            _p = p;
        }
        
        @Override
        public void run() {
            if(!(_bStand.getBlock().getState() instanceof BrewingStand))
                return;
            
            BrewingStand bStand = (BrewingStand)_bStand.getBlock().getState();
            
            BrewerInventory binv = bStand.getSnapshotInventory();
            ItemStack potionL = binv.getItem(0);
            ItemStack potionM = binv.getItem(1);
            ItemStack potionR = binv.getItem(2);
            
            if((potionL != null || potionM != null || potionR != null) && binv.getIngredient() != null) {
                if(bStand.getFuelLevel() == 0 || bStand.getBrewingTime() <= 0)
                    return;
                
                if(!hBrewStands.containsKey(_bStand)) {
                    _p.sendMessage("Register your Brewstand to your's for the next brewing.");
                    hBrewStands.put(_bStand, _p);
                }
            }
        }
    }
}
