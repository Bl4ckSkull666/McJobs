/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.util;

import com.dmgkz.mcjobs.McJobs;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

/**
 *
 * @author Bl4ckSkull666
 */
public class Holder {
    private final PotionHolder _potions;
    private final EnchantHolder _enchants;
    private final JobsHolder _jobs;
    
    public Holder() {
        _potions = new PotionHolder();
        _enchants = new EnchantHolder();
        _jobs = new JobsHolder();
        McJobs.getPlugin().getLogger().info("Loaded " + _potions.getPotions().size() + " Potions and " + _enchants.getEnchants().size() + " Enchants.");
    }
    
    public PotionHolder getPotions() {
        return _potions;
    }
    
    public EnchantHolder getEnchants() {
        return _enchants;
    }
    
    public JobsHolder getJobsHolder() {
        return _jobs;
    }
    
    public class PotionHolder {
        private final HashMap<String, PotionTypeAdv> _potions = new HashMap<>();
        
        public PotionHolder() {
            File f = new File(McJobs.getPlugin().getDataFolder(), "potions.yml");
            FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
            if(!f.exists()) {
                try {
                    InputStream in = McJobs.getPlugin().getResource("potions.yml");
                    String msg = "";
                    int c = -1;
                    while((c = in.read()) != -1)
                        msg += String.valueOf((char)c);
                    fc.loadFromString(msg);
                    fc.save(f);
                } catch(IOException | InvalidConfigurationException ex) {
                    McJobs.getPlugin().getLogger().log(Level.INFO, "Error on load default potions.yml, please copy it manually from mcjobs.jar and restart your server. Thank you");
                    return;
                }
            }
            
            for(String name: fc.getKeys(false)) {
                PotionTypeAdv pta = new PotionTypeAdv(fc.getConfigurationSection(name), name);
                if(pta.getStatus())
                    _potions.put(name.toLowerCase(), pta);
            }
        }
        
        public HashMap<String, PotionTypeAdv> getPotions() {
            HashMap<String, PotionTypeAdv> tmp = new HashMap<>();
            tmp.putAll(_potions);
            return tmp;
        }

        public PotionTypeAdv getPotion(String str) {
            if(!_potions.containsKey(str.toLowerCase()))
                return null;

            return _potions.get(str.toLowerCase());
        }

        public PotionTypeAdv getPotion(ItemStack item) {
            if(!item.hasItemMeta() && !(item.getItemMeta() instanceof PotionMeta))
                return null;
            
            PotionMeta pm = (PotionMeta)item.getItemMeta();
            for(Map.Entry<String, PotionTypeAdv> me: _potions.entrySet()) {
                if(!item.getType().equals(me.getValue().getMaterial()))
                    continue;
                
                if(!pm.getBasePotionData().getType().equals(me.getValue().getType()))
                    continue;

                if(pm.getBasePotionData().isExtended() != me.getValue().isExtended())
                    continue;

                if(pm.getBasePotionData().isUpgraded() != me.getValue().isUpgraded())
                    continue;

                return me.getValue();
            }
            return null;
        }
    }
    
    public class EnchantHolder {
        private final HashMap<String, EnchantTypeAdv> _enchants = new HashMap<>();
        
        public EnchantHolder() {
            File f = new File(McJobs.getPlugin().getDataFolder(), "enchantments.yml");
            FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
            if(!f.exists()) {
                for(Enchantment en: EnchantmentWrapper.values()) {
                    for(int i = en.getStartLevel(); i <= en.getMaxLevel(); i++) {
                        fc.set(en.getKey().getKey() + "." + i, en.getKey().getKey().toUpperCase() + "_" + i);
                    }
                }
                try {
                    fc.save(f);
                } catch(Exception ex) {

                }
            }

            for(String k: fc.getKeys(false)) {
                Enchantment ench = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(k));
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
    
        public HashMap<String, EnchantTypeAdv> getEnchants() {
            HashMap<String, EnchantTypeAdv> tmp = new HashMap<>();
            tmp.putAll(_enchants);
            return tmp;
        }

        public EnchantTypeAdv getEnchantAdv(String str) {
            if(_enchants.containsKey(str.toUpperCase()))
                return _enchants.get(str.toUpperCase());
            return null;
        }

        public EnchantTypeAdv getEnchantAdv(Enchantment enchant, Integer value) {
            for(Map.Entry<String, EnchantTypeAdv> e : _enchants.entrySet()) {
                if(e.getValue().getEnchant().equals(enchant) && Objects.equals(e.getValue().getLevel(), value))
                    return e.getValue();
            }
            return null;
        }

        public Enchantment getEnchant(String str) {
            if(_enchants.containsKey(str.toLowerCase()))
                return (_enchants.get(str.toLowerCase())).getEnchant();
            return null;
        }

        public Integer getEnchantAdvLevel(String str) {
            EnchantTypeAdv eta = getEnchantAdv(str);
            if(eta != null)
                return eta.getLevel();
            return null;
        }
    }
    
    public class JobsHolder {
        private TreeMap<String, ArrayList<String>> _jobs = new TreeMap<>();
        
        public void addJob(String action, String job) {
            if(!_jobs.containsKey(action))
                _jobs.put(action, new ArrayList<>());
            
            if(!_jobs.get(action).contains(job))
                _jobs.get(action).add(job);
        }
        
        public ArrayList<String> getJobs(String action) {
            if(!_jobs.containsKey(action))
                return new ArrayList<>();
            return _jobs.get(action);
        }
    }
}
