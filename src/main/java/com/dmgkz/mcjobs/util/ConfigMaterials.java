/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.util;

import com.dmgkz.mcjobs.McJobs;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Bl4ckSkull666
 */
public class ConfigMaterials {
    private static Map<String, Material> _mats = new HashMap<>();
    
    public static void load(FileConfiguration fc) {
        _mats.clear();
        
        if(fc.isConfigurationSection("materials")) {
            for(String k: fc.getConfigurationSection("materials").getKeys(false)) {
                if(!fc.isString("materials." + k))
                    continue;
                
                String mStr = fc. getString("materials." + k);
                for(Material m: Material.values()) {
                    if(m.name().equalsIgnoreCase(mStr)) {
                        _mats.put(k.toLowerCase(), m);
                        break;
                    }
                }
            }
        }
    }
    
    public static Material getMaterial(String str) {
        if(_mats.containsKey(str.toLowerCase()))
            return _mats.get(str.toLowerCase());
        
        McJobs.getPlugin().getLogger().log(Level.WARNING, "Missing Material for {0}, PLEASE FIX IT!!!!", str.toLowerCase());
        return Material.AIR;
    }
}
