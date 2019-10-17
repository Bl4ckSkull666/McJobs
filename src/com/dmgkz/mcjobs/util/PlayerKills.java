/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.util;

import com.dmgkz.mcjobs.McJobs;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Bl4ckSkull666
 */
public class PlayerKills {
    private final HashMap<UUID, PlayerKill> _kills = new HashMap<>();
    private final UUID _myKills;
    public PlayerKills(UUID uuid) {
        _myKills = uuid;
        load();
    }
    
    public void setKill(UUID uuid) {
        if(!_kills.containsKey(uuid))
            _kills.put(uuid, new PlayerKill(uuid));
        else
            _kills.get(uuid).setKilled();
    }
    
    public long lastKilledBeforeSeconds(UUID uuid) {
        if(_kills.containsKey(uuid))
            return _kills.get(uuid).getLastKillBeforeSeconds();
        return -1;
    }
    
    public int getKilledCount(UUID uuid) {
        if(_kills.containsKey(uuid))
            return _kills.get(uuid).getKills();
        return 0;
    }
    
    private void load() {
        _kills.clear();
        File killFile = new File(McJobs.getPlugin().getDataFolder(), "playerkills.yml");
        FileConfiguration killConfig = YamlConfiguration.loadConfiguration(killFile);
        if(!killConfig.isConfigurationSection(_myKills.toString()))
            return;
        
        for(String player: killConfig.getConfigurationSection(_myKills.toString()).getKeys(false)) {
            if(UUID.fromString(player) == null || !killConfig.getConfigurationSection(_myKills.toString()).isConfigurationSection(player))
                continue;
            
            if(!killConfig.isInt(_myKills.toString() + "." + player + ".amount") || !killConfig.isLong(_myKills.toString() + "." + player + ".lastKill"))
                continue;
            
            UUID pUUID = UUID.fromString(player);
            int amount = killConfig.getInt(_myKills.toString() + "." + player + ".amount");
            long lastKill = killConfig.getLong(_myKills.toString() + "." + player + ".lastKill");
            
            if((60*60*24*14) > (PlayerKill.getCurrentSecounds() - lastKill))
                continue;
            _kills.put(pUUID, new PlayerKill(pUUID, amount, lastKill));
        }
    }
    
    public void save() {
        File killFile = new File(McJobs.getPlugin().getDataFolder(), "playerkills.yml");
        FileConfiguration killConfig = YamlConfiguration.loadConfiguration(killFile);
        if(killConfig.isConfigurationSection(_myKills.toString()))
           killConfig.set(_myKills.toString(), null);
        
        for(Map.Entry<UUID, PlayerKill> me: _kills.entrySet()) {
            killConfig.set(_myKills.toString() + "." + me.getKey().toString() + ".amount", me.getValue().getKills());
            killConfig.set(_myKills.toString() + "." + me.getKey().toString() + ".lastKill", me.getValue().getLastKill());
        }
        
        try {
            killConfig.save(killFile);
        } catch(Exception ex) {
            McJobs.getPlugin().getLogger().warning("Error on save PlayerKills for UUID " + _myKills.toString());
        }
    }
}
