package com.dmgkz.mcjobs.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.dmgkz.mcjobs.McJobs;
import java.util.UUID;
import org.bukkit.OfflinePlayer;


public class PlayerUtils {
    private static boolean _bVault = false;
    private static boolean _bFailed = false;
    private static final HashMap<String, Integer> _maxdefaults = new HashMap<>();
    
    public static int getAllowed(UUID uuid) {
        int i = _maxdefaults.get("default");
        if(_bVault) {
            if(_bFailed)
                return i;
            try {
                Permission permission = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class).getProvider();
                List<String> lGroups = new ArrayList<>();
                Player play;
                if(Bukkit.getPlayer(uuid) != null)
                    play = Bukkit.getPlayer(uuid);
                else
                    return i;
                
                if(permission.getPlayerGroups(play) != null)
                    lGroups = Arrays.asList(permission.getPlayerGroups(play));
                else 
                    return i;

                for(String group: lGroups) {
                    if(_maxdefaults.containsKey(group)) {
                        if(i < _maxdefaults.get(group))
                            i = _maxdefaults.get(group);
                    }
                }
            } catch(Exception e) {
                McJobs.getPlugin().getLogger().info("Your permission mod does not support player groups.  Using default max jobs only.");
                _bFailed = true;
            }
        }
        return i;
    }

    public static int getAllowed() {
        return _maxdefaults.get("default");
    }
    
    public static void setVault(boolean b) {
        _bVault = b;
    }

    public static HashMap<String, Integer> getMaxDefaults() {
        return _maxdefaults;
    }
    
    public static UUID getUUIDByName(String name) {
        for(OfflinePlayer op: Bukkit.getOfflinePlayers()) {
            if(op.getName().equalsIgnoreCase(name))
                return op.getUniqueId();
        }
        return null;
    }
}
