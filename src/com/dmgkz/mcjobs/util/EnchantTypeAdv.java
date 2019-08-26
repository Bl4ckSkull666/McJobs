/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.util;

import com.dmgkz.mcjobs.McJobs;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

/**
 *
 * @author Bl4ckSkull666
 */
public class EnchantTypeAdv {
    private final String _name;
    private final Enchantment _ench;
    private final Integer _level;
    
    public EnchantTypeAdv(String name, Enchantment ench, int level) {
        _name = name;
        _ench = ench;
        _level = level;
    }
    
    private static final HashMap<String, EnchantTypeAdv> _enchants = new HashMap<>();
    
    public static void load() {
        File f = new File(McJobs.getPlugin().getDataFolder(), "enchantments.yml");
        FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
        if(!f.exists()) {
            for(Enchantment en: Enchantment.values()) {
                for(int i = en.getStartLevel(); i <= en.getMaxLevel(); i++) {
                    fc.set(en.getName() + "." + i, en.getName().toUpperCase() + "_" + i);
                }
            }
            try {
                fc.save(f);
            } catch(Exception ex) {
                
            }
        }
        
        for(String k: fc.getKeys(false)) {
            Enchantment ench = Enchantment.getByName(k.toUpperCase());
            if(ench == null) {
                McJobs.getPlugin().getLogger().log(Level.INFO, "Can't find Enchantment " + k);
                continue;
            }
            
            for(String strLvl: fc.getConfigurationSection(k).getKeys(false)) {
                try {
                    int lvl = Integer.parseInt(strLvl);
                    String name = fc.getString(k + "." + strLvl).toUpperCase();
                    EnchantTypeAdv eta = new EnchantTypeAdv(name, ench, lvl);
                    _enchants.put(name, eta);
                } catch(NumberFormatException ex) {
                    McJobs.getPlugin().getLogger().log(Level.INFO, "Error in Enchantment Configuration " + k, ex);
                }
            }
        }
    }
    
    public static EnchantTypeAdv getEnchantAdv(String str) {
        if(_enchants.containsKey(str.toUpperCase()))
            return _enchants.get(str.toUpperCase());
        return null;
    }
  
    public static EnchantTypeAdv getEnchantAdv(Enchantment enchant, Integer value) {
        for(Map.Entry<String, EnchantTypeAdv> e : _enchants.entrySet()) {
            if(e.getValue()._ench.equals(enchant) && e.getValue()._level.equals(value))
                return e.getValue();
        }
        return null;
    }
  
    public static Enchantment getEnchant(String str) {
        if(_enchants.containsKey(str.toLowerCase()))
            return (_enchants.get(str.toLowerCase()))._ench;
        return null;
    }

    public static Enchantment getEnchant(EnchantTypeAdv eta) {
        return eta._ench;
    }

    public Enchantment getEnchant() {
        return _ench;
    }
    
    public String getName() {
        return _name;
    }

    public static Integer getLevel(String str) {
        EnchantTypeAdv eta = getEnchantAdv(str);
        if(eta != null)
            return eta._level;
        return null;
    }

    public static Integer getLevel(EnchantTypeAdv eta) {
        return eta._level;
    }
}
