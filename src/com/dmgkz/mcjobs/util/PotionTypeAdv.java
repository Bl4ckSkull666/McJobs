package com.dmgkz.mcjobs.util;

import com.dmgkz.mcjobs.McJobs;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionType;

public class PotionTypeAdv {
    private final String _name;
    private final PotionType _type;
    private final Material _material;
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
        if(!cs.isString("material") || !Utils.isMaterial(cs.getString("material"))) {
            McJobs.getPlugin().getLogger().warning("Missing Material in " + name + " PotionTypeAdv");
            _material = Material.AIR;
        } else {
            _material = Material.valueOf(cs.getString("material").toUpperCase());
        }
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
    
    public Material getMaterial() {
        return _material;
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
        return McJobs.getPlugin().getHolder().getPotions().getPotion(str);
    }
}