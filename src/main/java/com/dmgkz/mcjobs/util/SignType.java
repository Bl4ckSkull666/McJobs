/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.util;

/**
 *
 * @author Bl4ckSkull666
 */
public enum SignType {
    JOIN("join"),
    LEAVE("leave"),
    INFO("info"),
    NPC("npc"),
    REGION("region"),
    TOP("top"),
    STATS("stats"),
    REMOVE("remove"),
    NONE("none");
        
    private final String _name;
    private SignType(String name) {
        _name = name;
    }
        
    public String getName() {
        return _name;
    }
        
    public static SignType getByName(String str) {
        for(SignType st: SignType.values()) {
            if(st.getName().equalsIgnoreCase(str))
                return st;
        }
        return null;
    }
}
