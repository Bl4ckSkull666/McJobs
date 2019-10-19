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
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

/**
 *
 * @author Bl4ckSkull666
 */
public class LanguageCheck implements Runnable {
    private FileConfiguration _tmp = null;
    
    @Override
    public void run() {
        File fold = new File(McJobs.getPlugin().getDataFolder(), "languages");
        HashMap<String, FileConfiguration> tmp = new HashMap<>();
        tmp.putAll(McJobs.getPlugin().getLanguage().getLanguages());
        boolean changes = false;
        for(Map.Entry<String, FileConfiguration> me: tmp.entrySet()) {
            File f = new File(fold, me.getKey() + ".yml");
            if(!f.exists())
                return;
                
            _tmp = me.getValue();
            if(checkJobs("jobs")) {
                changes = true;
                save(f);
            }
            
            if(checkEnchants("enchant")) {
                changes = true;
                save(f);
            }
            
            if(checkPotions("potion")) {
                changes = true;
                save(f);
            }
            
            if(checkEntityTypes("entities")) {
                changes = true;
                save(f);
            }
            
            if(checkMaterials("materials")) {
                changes = true;
                save(f);
            }
            
            if(checkColors("color")) {
                changes = true;
                save(f);
            }
            _tmp = null;
        }
        
        if(changes) {
            McJobs.getPlugin().reloadLanguages();
        }
    }
    
    private void save(File f) {
        try {
            _tmp.save(f);
        } catch(IOException ex) {
            McJobs.getPlugin().getLogger().warning("Error on save Language File " + f.getName() + " after add missing components.");
        }
    }
    
    private boolean checkJobs(String section) {
       boolean hasChanged = false;
        for(String job: PlayerJobs.getJobsList().keySet()) {
            if(!_tmp.isString(section + ".name." + job.toLowerCase())) {
                _tmp.set(section + ".name." + job.toLowerCase(), job);
                hasChanged = true;
            }
            if(!_tmp.isString(section + ".description." + job.toLowerCase())) {
                _tmp.set(section + ".description." + job.toLowerCase(), "Missing description for Job " + job);
                hasChanged = true;
            }
        }
        return hasChanged;
    }
    
    private boolean checkEnchants(String section) {
        boolean hasChanged = false;
        for(String ench: McJobs.getPlugin().getHolder().getEnchants().getEnchants().keySet()) {
            if(!_tmp.isString(section + "." + ench.toLowerCase())) {
                _tmp.set(section + "." + ench.toLowerCase(), ench.replace("_", " "));
                hasChanged = true;
            }
        }
        return hasChanged;
    }
    
    private boolean checkPotions(String section) {
        boolean hasChanged = false;
        for(String pta: McJobs.getPlugin().getHolder().getPotions().getPotions().keySet()) {
            if(!_tmp.isString(section + "." + pta.toLowerCase())) {
                _tmp.set(section + "." + pta.toLowerCase(), pta.replace("_", " "));
                hasChanged = true;
            }
        }
        return hasChanged;
    }
    
    private boolean checkEntityTypes(String section) {
        boolean hasChanged = false;
        for(EntityType et: EntityType.values()) {
            if(!_tmp.isString(section + "." + et.name().toLowerCase())) {
                _tmp.set(section + "." + et.name().toLowerCase(), et.name().replace("_", " "));
                hasChanged = true;
            }
        }
        return hasChanged;
    }
    
    private boolean checkMaterials(String section) {
        boolean hasChanged = false;
        for(Material mat: Material.values()) {
            if(!_tmp.isString(section + "." + mat.name().toLowerCase())) {
                _tmp.set(section + "." + mat.name().toLowerCase(), mat.name().replace("_", " "));
                hasChanged = true;
            }
        }
        return hasChanged;
    }
    
    private boolean checkColors(String section) {
        boolean hasChanged = false;
        for(DyeColor dc: DyeColor.values()) {
            if(!_tmp.isString(section + "." + dc.name().toLowerCase())) {
                _tmp.set(section + "." + dc.name().toLowerCase(), dc.name().replace("_", " "));
                hasChanged = true;
            }
        }
        return hasChanged;
    }
}
