/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.util;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Bl4ckSkull666
 */
public final class SignManager {
    private final Map<Location, JobSign> _signs = new HashMap<>();
    public SignManager() {
        load();
    }
    
    public boolean isSign(Location loc) {
        return _signs.containsKey(loc);
    }
    
    public SignType getSignType(Location loc) {
        if(isSign(loc))
            return _signs.get(loc).getSignType();
        return SignType.NONE;
    }
    
    public JobSign getJobSign(Location loc) {
        if(isSign(loc))
            return _signs.get(loc);
        return null;
    }
    
    public void addSign(Location loc, JobSign js) {
        _signs.put(loc, js);
        save();
    }
    
    public void removeSign(Location loc) {
        if(!_signs.containsKey(loc))
            return;
        
        _signs.remove(loc);
        save();
    }
    
    public void load() {
        File f = new File(McJobs.getPlugin().getDataFolder(), "signs.yml");
        if(!f.exists())
            return;
        
        FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
        for(String k: conf.getKeys(false)) {
            if(!conf.isString(k + ".world") || !conf.isString(k + ".type") || !conf.isString(k + ".job") || !conf.isInt(k + ".x") || !conf.isInt(k + ".y") || !conf.isInt(k + ".z"))
                continue;
            
            if(!PlayerJobs.getJobsList().containsKey(conf.getString(k + ".job")))
                continue;
            
            SignType siType = SignType.getByName(conf.getString(k + ".type"));
            if(siType == null)
                continue;
            
            JobSign js = new JobSign(conf.getString(k + ".job"), siType);
            if(Bukkit.getWorld(conf.getString(k + ".world")) == null)
                continue;
            
            World w = Bukkit.getWorld(conf.getString(k + ".world"));
            
            Location loc = new Location(w, conf.getInt(k + ".x"), conf.getInt(k + ".y"), conf.getInt(k + ".z"));
            Block b = loc.getBlock();
            if(!(b instanceof Sign)) {
                McJobs.getPlugin().getLogger().log(Level.WARNING, "Sign on position X:{0}, Y:{1}, Z:{2} on World {3} can''t found.", new Object[] {loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), w.getName()});
                continue;
            }
            _signs.put(loc, js);
        }
    }
    
    public void save() {
        File f = new File(McJobs.getPlugin().getDataFolder(), "signs.yml");
        if(f.exists())
            f.delete();
        
        if(_signs.isEmpty())
            return;
        
        int i = 1;
        FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
        for(Map.Entry<Location, JobSign> me: _signs.entrySet()) {
            conf.set(i + ".world", me.getKey().getWorld().getName());
            conf.set(i + ".x", me.getKey().getBlockX());
            conf.set(i + ".y", me.getKey().getBlockY());
            conf.set(i + ".z", me.getKey().getBlockZ());
            conf.set(i + ".type", me.getValue().getSignType().getName());
            conf.set(i + ".job", me.getValue().getJob());
            i++;
        }
        
        try {
            conf.save(f);
        } catch (IOException ex) {
            Logger.getLogger(SignManager.class.getName()).log(Level.WARNING, "Can't save Signs.", ex);
        } 
    }
}

