package com.dmgkz.mcjobs.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.data.CompData;
import com.dmgkz.mcjobs.util.PotionTypeAdv;
import org.bukkit.GameMode;
import org.bukkit.Material;

public class Brewing implements Listener{
    private final HashMap<InventoryHolder, Player> hBrewStands = new HashMap<>();
    private final Logger log = McJobs.getPlugin().getLogger();
    
    @EventHandler(priority = EventPriority.LOW)
    public void brewing(BrewEvent event) {
        if(event.isCancelled())
            return;
        
        InventoryHolder brewStand = event.getContents().getHolder();
        Player play = null;
        
        if(hBrewStands.containsKey(brewStand))
            play = hBrewStands.get(brewStand);
        else
            return;
        
        if(!play.isOnline())
            return;
        
        if(MCListeners.isMultiWorld()){
            if(!play.hasPermission("mcjobs.world.all") && !play.hasPermission("mcjobs.world." + play.getWorld().getName()))
                return;
        }
                
        if(play.getGameMode() == GameMode.CREATIVE){
            if(!play.hasPermission("mcjobs.paycreative"))
                return;
        }
        
        for(Map.Entry<String, PlayerJobs> pair: PlayerJobs.getJobsList().entrySet()) {
            String sJob = pair.getKey();
            ItemStack ingred = event.getContents().getIngredient();
            ItemStack item = null;
            
            if(PlayerData.hasJob(play.getUniqueId(), sJob)) {
                while((item = event.getContents().iterator().next()) != null) {
                    if(item != null && item.getData().getItemType().equals(Material.POTION)){
                        Short sPotion = item.getDurability();
                        PotionTypeAdv potion = PotionTypeAdv.getNewPotion(item, ingred.getType());

                        if(McJobs.getPlugin().getConfig().getBoolean("advanced.debug")){
                            if(potion == null){
                                play.sendMessage("This potion does not exist: " + sPotion.toString() + " " + ingred.getType().toString()); 
                            } else
                                play.sendMessage("PotionType = " + sPotion.toString() + " ingredient = " + ingred.getType().toString() + " Result = " + potion.toString());
                        }

                        CompCache comp = new CompCache(sJob, play.getLocation(), play, potion, "potions");
                        CompData.getCompCache().add(comp);
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void getBrewStand(InventoryClickEvent event){
        Player play = (Player) event.getWhoClicked();
        
        if(event.isCancelled())
            return;
                
        if(event.getSlotType() == SlotType.CRAFTING && event.getSlot() == 3 && event.getInventory().getName().equalsIgnoreCase("container.brewing")){
            InventoryHolder brewStand = event.getInventory().getHolder();
            hBrewStands.put(brewStand, play);
        }
    }
}
