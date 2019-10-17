/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.util;

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
    
    public Enchantment getEnchant() {
        return _ench;
    }
    
    public String getName() {
        return _name;
    }

    public Integer getLevel() {
        return _level;
    }
}
