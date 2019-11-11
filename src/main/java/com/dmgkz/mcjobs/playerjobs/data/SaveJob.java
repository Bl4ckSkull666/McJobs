/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.playerjobs.data;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.util.EnchantTypeAdv;
import com.dmgkz.mcjobs.util.PotionTypeAdv;
import com.dmgkz.mcjobs.util.RegionPositions;
import com.dmgkz.mcjobs.util.Utils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

/**
 *
 * @author Bl4ckSkull666
 */
public class SaveJob {
    public static boolean saveRegion(String job) {
        if(!PlayerJobs.getJobsList().containsKey(job))
            return false;
        
        File fo = new File(McJobs.getPlugin().getDataFolder(), "jobs");
        File f = new File(fo, job + ".yml");
        
        FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
        RegionPositions rp = PlayerJobs.getJobsList().get(job).getData().getRegionPositions();
        if(rp == null || (rp.getPos1() == null && rp.getPos2() == null))
            conf.set("job-info-zone.region", null);
        else {
            conf.set("job-info-zone.region.world", rp.getPos1().getWorld().getName());
            conf.set("job-info-zone.region.pos1.x", rp.getPos1().getBlockX());
            conf.set("job-info-zone.region.pos1.y", rp.getPos1().getBlockY());
            conf.set("job-info-zone.region.pos1.z", rp.getPos1().getBlockZ());
            conf.set("job-info-zone.region.pos2.x", rp.getPos2().getBlockX());
            conf.set("job-info-zone.region.pos2.y", rp.getPos2().getBlockY());
            conf.set("job-info-zone.region.pos2.z", rp.getPos2().getBlockZ());
        }
        
        try {
            conf.save(f);
        } catch(IOException ex) {
            McJobs.getPlugin().getLogger().log(Level.INFO, "Error on save RegionPositions." , ex);
            return false;
        }
        return true;
    }
    
    public static boolean saveJob(String job) {
        if(!PlayerJobs.getJobsList().containsKey(job))
            return false;
        
        File fo = new File(McJobs.getPlugin().getDataFolder(), "jobs");
        File f = new File(fo, job + ".yml");
        
        if(f.exists())
            f.delete();
        
        FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
        JobsData jd = PlayerJobs.getJobsList().get(job).getData();
        
        conf.set("basepay", jd.getBasePay());
        conf.set("show_every_time", jd.getShowEveryTime());
        conf.set("exp", jd.getEXP());
        conf.set("default", jd._bDefaultJob);
        
        saveMatClass(jd, conf);
        saveEntityTypes(jd, conf);
        saveEnchantTypes(jd, conf);
        savePotionTypes(jd, conf);
    
        if(jd._jobInfoZone != null && jd._jobInfoZone.getPos1() != null && jd._jobInfoZone.getPos2() != null) {
            conf.set("job-info-zone.region.world", jd._jobInfoZone.getPos1().getWorld().getName());
            conf.set("job-info-zone.region.pos1.x", jd._jobInfoZone.getPos1().getBlockX());
            conf.set("job-info-zone.region.pos1.y", jd._jobInfoZone.getPos1().getBlockY());
            conf.set("job-info-zone.region.pos1.z", jd._jobInfoZone.getPos1().getBlockZ());
            conf.set("job-info-zone.region.pos2.x", jd._jobInfoZone.getPos2().getBlockX());
            conf.set("job-info-zone.region.pos2.y", jd._jobInfoZone.getPos2().getBlockY());
            conf.set("job-info-zone.region.pos2.z", jd._jobInfoZone.getPos2().getBlockZ());
        }
        
        if(!jd._jobInfoZoneMessage.isEmpty()) {
            for(Map.Entry<String, String> me: jd._jobInfoZoneMessage.entrySet()) {
                conf.set("job-info-zone.message." + me.getKey(), me.getValue());
            }
        }
        
        return false;
    }
    
    private static void saveMatClass(JobsData jd, ConfigurationSection conf) {
        for(Map.Entry<String, HashMap<Integer, List<Material>>> me: jd.getMatHash().entrySet()) {
            if(jd.getTierPays().containsKey(me.getKey()))
                conf.set(me.getKey() + ".pays", jd.getTierPays().get(me.getKey()));
            conf.set(me.getKey() + ".hide", jd.getShow(me.getKey()));
            if(jd.getTools().containsKey(me.getKey()) && !jd.getTools().get(me.getKey()).isEmpty())
                conf.set(me.getKey() + ".need-tool", Utils.getListToString(jd.getTools().get(me.getKey()), " "));
            
            for(Map.Entry<Integer, List<Material>> me2: me.getValue().entrySet()) {
                String tmp = "";
                for(Material mc: me2.getValue()) {
                    tmp += " " + mc.name();
                }
                tmp = tmp.substring(1);
                conf.set(me.getKey() + "." + me2.getKey(), tmp);
            }
        }
    }
    
    private static void saveEntityTypes(JobsData jd, ConfigurationSection conf) {
        for(Map.Entry<String, HashMap<Integer, List<EntityType>>> me: jd.getEntHash().entrySet()) {
            conf.set(me.getKey() + ".pays", jd.getShow(me.getKey()));
            for(Map.Entry<Integer, List<EntityType>> me2: me.getValue().entrySet()) {
                String tmp = "";
                for(EntityType mc: me2.getValue()) {
                    tmp += " " + mc.name();
                }
                tmp = tmp.substring(1);
                conf.set(me.getKey() + "." + me2.getKey(), tmp);
            }
        }
    }
    
    private static void saveEnchantTypes(JobsData jd, ConfigurationSection conf) {
        for(Map.Entry<String, HashMap<Integer, List<EnchantTypeAdv>>> me: jd.getEnchantHash().entrySet()) {
            conf.set(me.getKey() + ".pays", jd.getShow(me.getKey()));
            for(Map.Entry<Integer, List<EnchantTypeAdv>> me2: me.getValue().entrySet()) {
                String tmp = "";
                for(EnchantTypeAdv mc: me2.getValue()) {
                    tmp += " " + mc.getName();
                }
                tmp = tmp.substring(1);
                conf.set(me.getKey() + "." + me2.getKey(), tmp);
            }
        }
    }
    
    private static void savePotionTypes(JobsData jd, ConfigurationSection conf) {
        for(Map.Entry<String, HashMap<Integer, List<PotionTypeAdv>>> me: jd.getPotHash().entrySet()) {
            conf.set(me.getKey() + ".pays", jd.getShow(me.getKey()));
            for(Map.Entry<Integer, List<PotionTypeAdv>> me2: me.getValue().entrySet()) {
                String tmp = "";
                for(PotionTypeAdv mc: me2.getValue()) {
                    tmp += " " + mc.getName();
                }
                tmp = tmp.substring(1);
                conf.set(me.getKey() + "." + me2.getKey(), tmp);
            }
        }
    }
}
