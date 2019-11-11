/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.listeners;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.data.CompData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Bl4ckSkull666
 */
public class Repair implements Listener {
    private final HashMap<Location, MyAnvil> _hRepair = new HashMap<>();
    
    @EventHandler(priority = EventPriority.LOW)
    public void preAnvilEvent(PrepareAnvilEvent e) {
        McJobs.getPlugin().getLogger().info("Starting New PrepareAnvil Event");
        if(e.getResult() == null) {
            McJobs.getPlugin().getLogger().info("PrepareAnvil - Result is null");
            return;
        }
        
        ItemStack item = e.getInventory().getItem(0);
        ItemStack ingredient = e.getInventory().getItem(1);
        ItemStack result = e.getInventory().getItem(2);
        if(item != null && ingredient != null && result != null) {
           _hRepair.put(e.getInventory().getLocation(), new MyAnvil(result.getType(), ingredient.getType(), ingredient.getAmount()));
        } else if(_hRepair.containsKey(e.getInventory().getLocation()) && item == null && result == null) {
            int used = 0;
            MyAnvil ma = _hRepair.get(e.getInventory().getLocation());
            if(ingredient == null)
                used = ma.getIngredientAmount();
            else
                used = ma.getIngredientAmount() - ingredient.getAmount();
            
            if(used == 0)
                return;

            List<HumanEntity> pList = e.getViewers();
            ArrayList<String> jobs = McJobs.getPlugin().getHolder().getJobsHolder().getJobs("repair");
            for(HumanEntity he: pList) {
                if(!(he instanceof Player))
                    continue;
                
                Player p = (Player)he;
                if(MCListeners.isMultiWorld()){
                    if(!p.hasPermission("mcjobs.world.all") && !p.hasPermission("mcjobs.world." + p.getWorld().getName()))
                        continue;
                }

                if(p.getGameMode() == GameMode.CREATIVE){
                    if(!p.hasPermission("mcjobs.paycreative"))
                        continue;
                }
                
                for(String sJob: jobs) {
                    if(PlayerData.hasJob(p.getUniqueId(), sJob)) {
                        for(int a = 0; a < used; a++) {
                            CompCache comp = new CompCache(sJob, e.getInventory().getLocation(), p, ma.getResult(), "repair");
                            CompData.getCompCache().add(comp);
                        }
                    }
                }
            }
            _hRepair.remove(e.getInventory().getLocation());
        }
    }

    private class MyAnvil {
        private final Material _result;
        private final Material _ingredient;
        private final int _ingredientAmount;
        
        public MyAnvil(Material result, Material ingredient, int amount) {
            _result = result;
            _ingredient = ingredient;
            _ingredientAmount = amount;
        }
        
        public Material getResult() {
            return _result;
        }
        
        public Material getIngredient() {
            return _ingredient;
        }
        
        public int getIngredientAmount() {
            return _ingredientAmount;
        }
    } 
}
