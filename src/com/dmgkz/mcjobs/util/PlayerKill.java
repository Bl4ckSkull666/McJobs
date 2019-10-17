/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.util;

import java.util.Calendar;
import java.util.UUID;

/**
 *
 * @author Bl4ckSkull666
 */
public class PlayerKill {
    private int _amount = 0;
    private final UUID _uuid;
    private long _lastKill;
    
    public PlayerKill(UUID uuid) {
        _uuid = uuid;
        _lastKill = PlayerKill.getCurrentSecounds();
        _amount = 1;
    }
    
    public PlayerKill(UUID uuid, int amount, long lastKill) {
        _uuid = uuid;
        _lastKill = lastKill;
        _amount = amount;
    }
    
    public int getKills() {
        return _amount;
    }
    
    public long getLastKill() {
        return _lastKill;
    }
    
    public long getLastKillBeforeSeconds() {
        return getCurrentSecounds()-_lastKill;
    }
    
    public UUID getKilled() {
        return _uuid;
    }
    
    public void setKilled() {
        _amount++;
        _lastKill = PlayerKill.getCurrentSecounds();
    }
    
    public static long getCurrentSecounds() {
        Calendar old = Calendar.getInstance();
        old.set(1970, 0, 1, 0, 0, 0);
        Calendar now = Calendar.getInstance();
        
        return Math.round((now.getTimeInMillis() - old.getTimeInMillis())/1000);
    }
}
