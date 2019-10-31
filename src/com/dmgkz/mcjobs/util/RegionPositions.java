package com.dmgkz.mcjobs.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author Bl4ckSkull666
 */
public class RegionPositions {
    private Location _Pos1 = null;
    private Location _Pos2 = null;
    
    private boolean _setNextPos = true;
    private final List<UUID> _inside = new ArrayList<>();
    
    public void setPosition(Location loc) {
        if(_setNextPos) {
            _Pos1 = loc;
            _setNextPos = false;
        } else {
            _Pos2 = loc;
            _setNextPos = true;
        }
        
        if(_Pos1 != null && _Pos2 != null) {
            World w = _Pos1.getWorld();
            int x1 = Math.min(_Pos1.getBlockX(), _Pos2.getBlockX());
            int x2 = Math.max(_Pos1.getBlockX(), _Pos2.getBlockX());
            
            int y1 = Math.min(_Pos1.getBlockY(), _Pos2.getBlockY());
            int y2 = Math.max(_Pos1.getBlockY(), _Pos2.getBlockY());
            
            int z1 = Math.min(_Pos1.getBlockZ(), _Pos2.getBlockZ());
            int z2 = Math.max(_Pos1.getBlockZ(), _Pos2.getBlockZ());
            
            _Pos1 = new Location(w, x1, y1, z1);
            _Pos2 = new Location(w, x2, y2, z2);
        }
    }
    
    public Location getPos1() {
        return _Pos1;
    }
    
    public Location getPos2() {
        return _Pos2;
    }
    
    public boolean hasEntered(UUID uuid, Location loc) {
        if(_Pos1 == null || _Pos2 == null || !_Pos1.getWorld().equals(loc.getWorld()))
            return false;
        
        if(_inside.contains(uuid)) {
            if(loc.getBlockX() < _Pos1.getBlockX() || loc.getBlockX() > _Pos2.getBlockX()) {
                _inside.remove(uuid);
                return false;
            }
            
            if(loc.getBlockY() < _Pos1.getBlockY() || loc.getBlockY() > _Pos2.getBlockY()) {
                _inside.remove(uuid);
                return false;
            }
            
            if(loc.getBlockZ() < _Pos1.getBlockZ() || loc.getBlockZ() > _Pos2.getBlockZ()) {
                _inside.remove(uuid);
                return false;
            }
            /* Is already in Region and is in Region, message was send, don't send again */
            return false;
        } else {
            if(loc.getBlockX() >= _Pos1.getBlockX() && loc.getBlockX() <= _Pos2.getBlockX()) {
                if(loc.getBlockY() >= _Pos1.getBlockY() && loc.getBlockY() <= _Pos2.getBlockY()) {
                    if(loc.getBlockZ() >= _Pos1.getBlockZ() && loc.getBlockZ() <= _Pos2.getBlockZ()) {
                        /* Player is new in Region, send Message */
                        _inside.add(uuid);
                        return true;
                    }
                }
            }
        }
        
        if(_inside.contains(uuid))
            _inside.remove(uuid);
        return false;
    }
    
    public void removePositions() {
        _Pos1 = null;
        _Pos2 = null;
    }
    
    /*
    * world: 'name'
    * pos1:
    *     x: 0.0
    *     y: 0.0
    *     z: 0.0
    * pos2:
    *     x: 0.0
    *     y: 0.0
    *     z: 0.0
    */
    public static RegionPositions getRP(ConfigurationSection cs) {
        World w = null;
        if(cs.isString("world"))
            w = Bukkit.getWorld(cs.getString("world"));
        
        if(w == null)
            return null;
        
        if(cs.isConfigurationSection("pos1")) {
            if(!cs.isInt("pos1.x") || !cs.isInt("pos1.y") || !cs.isInt("pos1.z"))
                return null;
        }  
        Location pos1 = new Location(w, cs.getInt("pos1.x"), cs.getInt("pos1.y"), cs.getInt("pos1.z"));
        
        if(cs.isConfigurationSection("pos2")) {
            if(!cs.isInt("pos2.x") || !cs.isInt("pos2.y") || !cs.isInt("pos2.z"))
                return null;
        }
        Location pos2 = new Location(w, cs.getInt("pos2.x"), cs.getInt("pos2.y"), cs.getInt("pos2.z"));
        
        RegionPositions rp = new RegionPositions();
        rp.setPosition(pos1);
        rp.setPosition(pos2);
        return rp;
    }
}
