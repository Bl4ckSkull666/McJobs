/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.util;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.localization.SpigotBuilds;
import com.dmgkz.mcjobs.localization.WorldEditBuilds;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class Utils {
    public static int getRandomNumber() {
        Random rnd = new Random();
        return rnd.nextInt(Integer.MAX_VALUE);
    }
    
    public static String getListToString(List<String> arr, String seperator) {
        String tmp = "";
        for(String a: arr) {
            tmp += seperator + a;
        }
        return tmp.substring(seperator.length());
    }
    
    public static boolean isMaterial(String strMat) {
        try {
            Material mat = Material.valueOf(strMat.toUpperCase());
            return mat != null && !mat.equals(Material.AIR);
        } catch(Exception ex) {
            return false;
        }
    }
    
    public static boolean isEntity(String strEnt) {
        try {
            EntityType ent = EntityType.valueOf(strEnt.toUpperCase());
            return true;
        } catch(Exception ex) {
            return false;
        }
    }
    
    public static boolean isPotionTypeAdv(String strPta) {
        return McJobs.getPlugin().getHolder().getPotions().getPotion(strPta) != null;
    }
    
    public static boolean isEnchantTypeAdv(String strEta) {
        return McJobs.getPlugin().getHolder().getEnchants().getEnchantAdv(strEta) != null;
    }
    
    public static boolean hasNeededTool(Player p, String job, String action) {
        if(PlayerJobs.getJobsList().get(job).getData().getTools().containsKey(action) && !PlayerJobs.getJobsList().get(job).getData().getTools().get(action).isEmpty()) {
            for(String tool: PlayerJobs.getJobsList().get(job).getData().getTools().get(action)) {
                if(p.getInventory().getItemInMainHand().getType().name().toLowerCase().contains(tool.toLowerCase()))
                    return true;
            }
            return false;
        } else
            return true;
    }
    
    public static String colorTrans(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    
    public static boolean sendMessage(Player p, ConfigurationSection cs, HashMap<String, String> replaces) {
        if(Bukkit.getVersion().toLowerCase().contains("spigot")) {
            SpigotBuilds.sendMessage(p, cs, replaces);
            return true;
        } else if(Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
            WorldEditBuilds.sendMessage(p, cs, replaces);
            return true;
        }
        return false;
    }
    
    public static String ReplaceAll(String msg, HashMap<String, String> replaces) {
        for(Map.Entry<String, String> me: replaces.entrySet())
            msg.replaceAll(me.getKey(), me.getValue());
        
        return msg;
    }
}
