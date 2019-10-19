package com.dmgkz.mcjobs.listeners;

import com.dmgkz.mcjobs.McJobs;
import java.util.HashMap;
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
import com.dmgkz.mcjobs.playerjobs.data.CompData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Campfire;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

public class Baking implements Listener {
    private final List<MyCampCook> _hBlockCook = new ArrayList<>();
    
    @EventHandler(priority = EventPriority.LOW)
    public void getFurnaceExtract(FurnaceExtractEvent e) {
        Player p = e.getPlayer();
        if(MCListeners.isMultiWorld()) {
            if(!p.hasPermission("mcjobs.world.all") && !p.hasPermission("mcjobs.world." + p.getWorld().getName()))
                return;
        }

        if(p.getGameMode() == GameMode.CREATIVE) {
            if(!p.hasPermission("mcjobs.paycreative"))
                return;
        }

        ArrayList<String> jobs = McJobs.getPlugin().getHolder().getJobsHolder().getJobs("craft");
        for(String sJob: jobs) {
            if(PlayerData.hasJob(p.getUniqueId(), sJob)) {
                for(int i = 0; i < e.getItemAmount(); i++) {
                    CompCache comp = new CompCache(sJob, p.getLocation(), p, e.getItemType(), "craft");
                    CompData.getCompCache().add(comp);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void getBlockCook(BlockCookEvent e) {
        if(!(e.getBlock().getState() instanceof Campfire))
            return;
        
        Campfire cf = (Campfire)e.getBlock().getState();
        HashMap<Integer, Material> usedSlots = new HashMap<>();
        for(int i = 0; i < cf.getSize(); i++) {
            ItemStack item = cf.getItem(i);
            if(item != null) {
                usedSlots.put(i, item.getType());
            }
        }
        
        Bukkit.getScheduler().runTaskLater(McJobs.getPlugin(), new checkCampFire(cf.getLocation(), usedSlots, e.getResult().getType()), 10);
    }
    
    private class checkCampFire implements Runnable {
        private final Location _loc;
        private final HashMap<Integer, Material> _used;
        private final Material _mat;
        
        public checkCampFire(Location loc, HashMap<Integer, Material> used, Material mat) {
            _loc = loc;
            _used = used;
            _mat = mat;
        }
        
        @Override
        public void run() {
            if(!(_loc.getBlock().getState() instanceof Campfire))
                return;
            
            Campfire cf = (Campfire)_loc.getBlock().getState();
            for(Map.Entry<Integer,  Material> me: _used.entrySet()) {
                ItemStack item = cf.getItem(me.getKey());
                if(item == null) {
                    MyCampCook mcc = getMyCampCookByLocationAndSlot(cf.getLocation(), me.getKey());
                    if(mcc == null)
                        continue;
                    
                    if(!mcc.getMaterial().equals(me.getValue()))
                        continue;
                    
                    Player p = mcc.getPlayer();
                    ArrayList<String> jobs = McJobs.getPlugin().getHolder().getJobsHolder().getJobs("craft");
                    for(String sJob: jobs) {
                        if(PlayerData.hasJob(p.getUniqueId(), sJob)){
                            CompCache comp = new CompCache(sJob, p.getLocation(), p, _mat, "craft");
                            CompData.getCompCache().add(comp);
                        }
                    }
                    _hBlockCook.remove(mcc);
                    return;
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void getPlayerInteract(PlayerInteractEvent e) {
        if(!e.hasItem())
            return;
        
        if(e.getClickedBlock() == null)
            return;
        
        if(!(e.getClickedBlock().getState() instanceof Campfire))
            return;
        
        Campfire cf = (Campfire)e.getClickedBlock().getState();
        int nextFreeSlot = -1;
        for(int i = 0; i < cf.getSize(); i++) {
            ItemStack item = cf.getItem(i);
            if(item == null || item.getType().equals(Material.AIR)) {
                nextFreeSlot = i;
                break;
            }
        }
        
        if(nextFreeSlot == -1)
            return;
        
        Player p = e.getPlayer();
        if(MCListeners.isMultiWorld()){
            if(!p.hasPermission("mcjobs.world.all") && !p.hasPermission("mcjobs.world." + p.getWorld().getName()))
                return;
        }
                
        if(p.getGameMode() == GameMode.CREATIVE){
            if(!e.getPlayer().hasPermission("mcjobs.paycreative"))
                return;
        }

        _hBlockCook.add(new MyCampCook(p, nextFreeSlot, e.getItem().getType(), cf.getLocation()));
        
    }
    
    private MyCampCook getMyCampCookByLocationAndSlot(Location loc, int slot) {
        for(MyCampCook mcc: _hBlockCook) {
            if(mcc.getLocation().equals(loc) && mcc.getSlot() == slot)
                return mcc;
        }
        return null;
    }
    
    private class MyCampCook {
        private final Player _p;
        private final int _slot;
        private final Material _material;
        private final Location _location;
        
        public MyCampCook(Player p, int s, Material m, Location loc) {
            _p = p;
            _slot = s;
            _material = m;
            _location = loc;
        }
        
        public Player getPlayer() {
            return _p;
        }
        
        public int getSlot() {
            return _slot;
        }
        
        public Material getMaterial() {
            return _material;
        }
        
        public Location getLocation() {
            return _location;
        }
    }
}
