package com.dmgkz.mcjobs.listeners;

import com.dmgkz.mcjobs.McJobs;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.data.CompData;
import java.util.ArrayList;
import org.bukkit.GameMode;
import org.bukkit.event.inventory.InventoryType;

public class Baking implements Listener {
    private final HashMap<InventoryHolder, Player> _hFurnaces = new HashMap<>();
    
    @EventHandler(priority = EventPriority.LOW)
    public void getFurnaceCook(InventoryClickEvent event){
        SlotType entFurn = event.getSlotType();
        Integer slotID = event.getSlot();
        InventoryHolder furnace;
        //if(entFurn == SlotType.CONTAINER && slotID == 0 && event.getInventory().getName().equalsIgnoreCase("container.furnace")){
        if(entFurn == SlotType.CONTAINER && slotID == 0 && event.getInventory().getType().equals(InventoryType.FURNACE)) {
            Player play = (Player) event.getWhoClicked();
            ItemStack itemPlaced = event.getCursor();

            furnace = event.getInventory().getHolder();

            if(itemPlaced != null && !itemPlaced.getType().equals(Material.AIR)) {
                _hFurnaces.put(furnace, play);                 
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void furnaceBurn(FurnaceSmeltEvent event){
        Block bfurnace = event.getBlock();
        Furnace furnace = (Furnace) bfurnace.getState();

        InventoryHolder key = furnace.getInventory().getHolder();

        if(_hFurnaces.containsKey(key)){
            Player play = _hFurnaces.get(key);
            if(!play.isOnline())
                return;

            if(MCListeners.isMultiWorld()) {
                if(!play.hasPermission("mcjobs.world.all") && !play.hasPermission("mcjobs.world." + play.getWorld().getName()))
                    return;
            }

            if(play.getGameMode() == GameMode.CREATIVE){
                if(!play.hasPermission("mcjobs.paycreative"))
                    return;
            }

            ArrayList<String> jobs = McJobs.getPlugin().getHolder().getJobsHolder().getJobs("craft");
            for(String sJob: jobs) {
                if(PlayerData.hasJob(play.getUniqueId(), sJob)){
                    CompCache comp = new CompCache(sJob, play.getLocation(), play, event.getResult(), "craft");
                    CompData.getCompCache().add(comp);                
                }
            }
        }    
    }
}
