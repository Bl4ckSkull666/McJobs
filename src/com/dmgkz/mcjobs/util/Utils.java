/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.util;

import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author PapaHarni
 */
public class Utils {
    public static boolean isMaterial(String strMat) {
        try {
            Material mat = Material.valueOf(strMat.toUpperCase());
            return true;
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
        return PotionTypeAdv._potions.containsKey(strPta.toUpperCase());
    }
    
    public static boolean isEnchantTypeAdv(String strEta) {
        EnchantTypeAdv eta = EnchantTypeAdv.getEnchantAdv(strEta.toUpperCase());
        return (eta != null);
    }
    
    public static boolean hasNeededTool(Player p, String job, String action) {
        if(PlayerJobs.getJobsList().get(job).getData().getTools().containsKey(action) && !PlayerJobs.getJobsList().get(job).getData().getTools().get(action).isEmpty()) {
            if(p.getInventory().getItemInMainHand() == null)
                return false;
                        
            for(String tool: PlayerJobs.getJobsList().get(job).getData().getTools().get(action)) {
                if(p.getInventory().getItemInMainHand().getType().name().toLowerCase().contains(tool.toLowerCase()))
                    return true;
            }
            return false;
        } else
            return true;
    }
    
    public static MatClass getMatClassFromBlock(Block b, ArrayList<MatClass> amc) {
        MatClass tmp = null;
        for(MatClass mc: amc) {
            if(mc.getMaterial().equals(b.getType())) {
                if(tmp == null || tmp.getWorth() == -1 && mc.getWorth() == b.getState().getData().toItemStack().getDurability())
                    tmp = mc;
                else if(tmp == null)
                    tmp = mc;
            }
        }
        return tmp;
    }
    
    public static MatClass getMatClassFromItemStack(ItemStack item, ArrayList<MatClass> amc) {
        MatClass tmp = null;
        for(MatClass mc: amc) {
            if(mc.getMaterial().equals(item.getType())) {
                if(tmp == null || tmp.getWorth() == -1 && mc.getWorth() == item.getDurability())
                    tmp = mc;
                else if(tmp == null)
                    tmp = mc;
            }
        }
        return tmp;
    }
    
    public static boolean isBlockIn(Block b, ArrayList<MatClass> amc) {
        for(MatClass mc: amc) {
            if(mc.getMaterial().equals(b.getType())) {
                if(mc.getWorth() == -1 || mc.getWorth() == b.getState().getData().toItemStack().getDurability())
                    return true;
            }
        }
        return false;
    }
    
    public static boolean isInside(Material mat, ArrayList<MatClass> lmc) {
        for(MatClass tmp: lmc) {
            if(tmp.getMaterial().equals(mat))
                return true;
        }
        return false;
    }
}
