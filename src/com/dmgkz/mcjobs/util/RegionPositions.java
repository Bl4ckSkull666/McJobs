/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    private List<UUID> _inside = new ArrayList<>();
    
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
            double x1 = Math.min(_Pos1.getX(), _Pos2.getX());
            double x2 = Math.max(_Pos1.getX(), _Pos2.getX());
            
            double y1 = Math.min(_Pos1.getY(), _Pos2.getY());
            double y2 = Math.max(_Pos1.getY(), _Pos2.getY());
            
            double z1 = Math.min(_Pos1.getZ(), _Pos2.getZ());
            double z2 = Math.max(_Pos1.getZ(), _Pos2.getZ());
            
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
        if(_Pos1 != null && _Pos2 != null && !_Pos1.getWorld().equals(loc.getWorld()))
            return false;
        
        if(_inside.contains(uuid)) {
            if(loc.getX() < _Pos1.getX() || loc.getX() > _Pos2.getX()) {
                _inside.remove(uuid);
                return false;
            }
            
            if(loc.getY() < _Pos1.getY() || loc.getY() > _Pos2.getY()) {
                _inside.remove(uuid);
                return false;
            }
            
            if(loc.getZ() < _Pos1.getZ() || loc.getZ() > _Pos2.getZ()) {
                _inside.remove(uuid);
                return false;
            }
        } else {
            if(loc.getX() >= _Pos1.getX() && loc.getX() <= _Pos2.getX()) {
                if(loc.getY() >= _Pos1.getY() && loc.getY() <= _Pos2.getY()) {
                    if(loc.getZ() >= _Pos1.getZ() && loc.getZ() <= _Pos2.getZ()) {
                        _inside.add(uuid);
                        return true;
                    }
                }
            }
        }
        
        if(_inside.equals(uuid))
            _inside.remove(uuid);
        return false;
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
            if(!cs.isDouble("pos1.x") || !cs.isDouble("pos1.y") || !cs.isDouble("pos1.z"))
                return null;
        }  
        Location pos1 = new Location(w, cs.getDouble("pos1.x"), cs.getDouble("pos1.y"), cs.getDouble("pos1.z"));

         
        if(cs.isConfigurationSection("pos2")) {
            if(!cs.isDouble("pos2.x") || !cs.isDouble("pos2.y") || !cs.isDouble("pos2.z"))
                return null;
        }
        Location pos2 = new Location(w, cs.getDouble("pos2.x"), cs.getDouble("pos2.y"), cs.getDouble("pos2.z"));
        
        RegionPositions rp = new RegionPositions();
        rp.setPosition(pos1);
        rp.setPosition(pos2);
        return rp;
    }
}
