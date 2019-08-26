/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.util;

import org.bukkit.Material;

public final class MatClass {
    private final Material _mat;
    private int _worth = -1;
    public MatClass(Material mat) {
        _mat = mat;
    }
    
    public Material getMaterial() {
        return _mat;
    }
    
    public void setWorth(int worth) {
        _worth = worth;
    }
    
    public int getWorth() {
        return _worth;
    }
}
