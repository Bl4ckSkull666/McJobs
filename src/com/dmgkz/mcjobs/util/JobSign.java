/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.util;

import org.bukkit.Location;

/**
 *
 * @author Bl4ckSkull666
 */
public class JobSign {
    private final SignType _siType;
    private final String _job;
    private final Location _loc;
    private final int _startLine;
    
    public JobSign(String job, SignType siType, Location loc) {
        _job = job;
        _siType = siType;
        _loc = loc;
        _startLine = -1;
    }
    
    public JobSign(String job, SignType siType, Location loc, int startLine) {
        _job = job;
        _siType = siType;
        _loc = loc;
        _startLine = startLine;
    }
       
    public SignType getSignType() {
        return _siType;
    }
      
    public String getJob() {
        return _job;
    }
    
    public int getStartLine() {
        return _startLine;
    }
    
    public Location getLocation() {
        return _loc;
    }
}