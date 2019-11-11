package com.dmgkz.mcjobs.logging;

import com.dmgkz.mcjobs.McJobs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class BlockLoggers {
    private final ArrayList<String> noLogging;
    private final HashMap<Location, ArrayList<BPlayer>> hPlayerBreakBlock;
    private final HashMap<Location, ArrayList<BPlayer>> hPlayerPlaceBlock;
    private final HashMap<World, Boolean> hBuiltInWorld;
    private static long _timer;
    private static BlockLoggers _logger;
 
    public BlockLoggers() {
        noLogging         = new ArrayList<>();
        hPlayerBreakBlock = new HashMap<>();
        hPlayerPlaceBlock = new HashMap<>();
        hBuiltInWorld     = new HashMap<>();
        _logger = this;
    }
    
    private void start() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(McJobs.getPlugin(), new Clearer(), _timer, _timer);
    }
    
    public static void setTimer(long time) {
        _timer = time;
        if(_logger != null)
            _logger.start();
    }
    
    /*
    + Return true has player build on the location the same material in time_interval
    */
    public Boolean checkBuiltIn(Location loc, Player p, Material mat, Boolean isBreak) {
        if(!getBuiltIn().containsKey(p.getWorld()))
            getBuiltIn().put(p.getWorld(), true);

        if(isBreak) {
            if(this.hPlayerBreakBlock.containsKey(loc)) {
                for(BPlayer bp: hPlayerBreakBlock.get(loc)) {
                    if(bp.isUUID(p.getUniqueId()) && bp.isMaterial(mat) && !bp.isTimeLater(System.currentTimeMillis()))
                        return true;
                }
            }
        } else {
            if(this.hPlayerPlaceBlock.containsKey(loc)) {
                for(BPlayer bp: hPlayerPlaceBlock.get(loc)) {
                    if(bp.isUUID(p.getUniqueId()) && bp.isMaterial(mat) && !bp.isTimeLater(System.currentTimeMillis()))
                        return true;
                }
            }
        }
        return false;
    }

    public void addPlayer(Location loc, Player p, Material mat, Boolean isBreak) {
        ArrayList<BPlayer> aPlayers = new ArrayList<>();
        aPlayers.add(new BPlayer(p.getUniqueId(), mat));
        if(isBreak) {
            if(hPlayerBreakBlock.containsKey(loc))
                aPlayers.addAll(hPlayerBreakBlock.get(loc));
            hPlayerBreakBlock.put(loc, aPlayers);
        } else {
            if(hPlayerPlaceBlock.containsKey(loc))
                aPlayers.addAll(hPlayerPlaceBlock.get(loc));
            hPlayerPlaceBlock.put(loc, aPlayers);
        }
    }

    public HashMap<World, Boolean> getBuiltIn() {
        return this.hBuiltInWorld;
    }
    
    public class Clearer implements Runnable {
        @Override
        public void run() {
            HashMap<Location, List<BPlayer>> breaker = new HashMap<>();
            HashMap<Location, List<BPlayer>> placer = new HashMap<>();
            breaker.putAll(hPlayerBreakBlock);
            placer.putAll(hPlayerPlaceBlock);
            
            for(Map.Entry<Location, List<BPlayer>> me: breaker.entrySet()) {
                for(BPlayer bp: me.getValue()) {
                    if(bp.isTimeLater(System.currentTimeMillis()))
                        hPlayerBreakBlock.get(me.getKey()).remove(bp);
                }
            }
            
            for(Map.Entry<Location, List<BPlayer>> me: placer.entrySet()) {
                for(BPlayer bp: me.getValue()) {
                    if(bp.isTimeLater(System.currentTimeMillis()))
                        hPlayerPlaceBlock.get(me.getKey()).remove(bp);
                }
            }
        }
    }
    
    public class BPlayer {
        private final UUID _uuid;
        private final long _time;
        private final Material _mat;
        
        public BPlayer(UUID uuid, Material mat) {
            _uuid = uuid;
            _time = System.currentTimeMillis();
            _mat = mat;
        }
        
        public boolean isUUID(UUID uuid) {
            return _uuid.equals(uuid);
        }
        
        public boolean isTimeLater(long t) {
            long diff = _time - t;
            return (diff >= BlockLoggers._timer);
        }
        
        public long getTime() {
            return _time;
        }
        
        public boolean isMaterial(Material mat) {
            return _mat.equals(mat);
        }
    }
}
