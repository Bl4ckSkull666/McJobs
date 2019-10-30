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
import java.util.logging.Level;
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
                    if(_maxdefaults.containsKey(group.toLowerCase())) {
                        if(i < _maxdefaults.get(group.toLowerCase()))
                            i = _maxdefaults.get(group.toLowerCase());
                    }
                }
            } catch(Exception e) {
                McJobs.getPlugin().getLogger().info("Your permission mod does not support player groups. Using default max jobs only.");
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
    
    public static void setAllowedJobs(String grp, int jobs) {
        if(_bVault && !grp.equalsIgnoreCase("default")) {
            try {
                Permission permission = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class).getProvider();
                List<String> lGroups = Arrays.asList(permission.getGroups());
                boolean isGroup = false;
                for(String grou: lGroups) {
                    if(grou.equalsIgnoreCase(grp))
                        isGroup = true;
                }
                
                if(!isGroup) {
                    McJobs.getPlugin().getLogger().warning("Can't find Group " + grp + ".");
                }
            } catch(Exception ex) {
                McJobs.getPlugin().getLogger().log(Level.INFO, "Can't verify " + grp + " for max jobs. Is your Group Managment (soft)depend with Vault?", ex);
            }
        }
        _maxdefaults.put(grp.toLowerCase(), jobs);
    }
    
    public static OfflinePlayer getOfflinePlayer(String str) {
        try {
            if(str.length() >= 32) {
                if(str.length() == 32) {
                    str = str.substring(0, 8) + "-" + str.substring(8, 12) + "-" + str.substring(12, 16) + "-" + str.substring(16, 20) + "-" + str.substring(20);
                }
                return Bukkit.getOfflinePlayer(UUID.fromString(str));
            } else {
                for(OfflinePlayer op: Bukkit.getOfflinePlayers()) {
                    if(op != null && op.getName() != null && op.getName().equalsIgnoreCase(str))
                        return op;
                }
            }
        } catch(Exception ex) {

        }
        return null;
    }
}
