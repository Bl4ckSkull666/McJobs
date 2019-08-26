package com.dmgkz.mcjobs.util;

import com.dmgkz.mcjobs.McJobs;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class PotionTypeAdv {
    public static HashMap<String, PotionTypeAdv> _potions = new HashMap<>();
    
    public static void load() {
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
    
    
    private final String _name;
    private final PotionType _type;
    private final boolean _extended;
    private final boolean _upgraded;
    private final int _order;
    private boolean _status = true;
    private final HashMap<Material, String> _results = new HashMap<>();
    
    public PotionTypeAdv(ConfigurationSection cs, String name) {
        _type = PotionType.valueOf(cs.getString("type", "").toUpperCase());
        if(_type == null)
            _status = false;
        _name = name;
        _extended = cs.getBoolean("extended", false);
        _upgraded = cs.getBoolean("upgraded", false);
        _order = cs.getInt("order", 0);
        if(cs.isConfigurationSection("result")) {
            for(String strMat: cs.getConfigurationSection("result").getKeys(false)) {
                Material mat = Material.getMaterial(strMat.toUpperCase());
                if(mat == null) {
                    McJobs.getPlugin().getLogger().log(Level.INFO, "Material " + strMat + " in result of " + name + " not found.");
                    continue;
                }
                
                _results.put(mat, cs.getString("result." + strMat));
            }
        }
    }
    
    public boolean getStatus() {
        return _status;
    }
    
    public boolean isExtended() {
        return _extended;
    }
    
    public boolean isUpgraded() {
        return _upgraded;
    }
    
    public int getOrder() {
        return _order;
    }
    
    public String getName() {
        return _name;
    }
    
    public PotionType getType() {
        return _type;
    }
    
    public boolean hasResult(Material mat) {
        return _results.containsKey(mat);
    } 
    
    public PotionTypeAdv getResultPotion(Material mat) {
        if(!hasResult(mat))
            return null;
        
        String str = _results.get(mat);
        
        if(_potions.containsKey(str.toLowerCase()))
            return _potions.get(str.toLowerCase());
        return null;
    }
  
    public static PotionTypeAdv getPotion(String str) {
        if(!_potions.containsKey(str.toLowerCase()))
            return null;
        
        return _potions.get(str.toLowerCase());
    }
    
    public static PotionTypeAdv getPotion(ItemStack item) {
        if(!item.hasItemMeta() && !(item.getItemMeta() instanceof PotionMeta))
            return null;
        
        PotionMeta pm = (PotionMeta)item.getItemMeta();
        for(Map.Entry<String, PotionTypeAdv> me: _potions.entrySet()) {
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
    
    public static PotionTypeAdv getNewPotion(ItemStack item, Material mat) {
        PotionTypeAdv pta = getPotion(item);
        if(pta == null)
            return null;
        
        PotionTypeAdv result = pta.getResultPotion(mat);
        if(result == null)
            return null;
        return result;
    }
  
    public static Boolean isHigherTier(PotionTypeAdv potOne, PotionTypeAdv potTwo) {
        if((potOne == null) || (potTwo == null))
            return false;
        return (potOne.getOrder() > potTwo.getOrder());
    }
}