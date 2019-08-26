package com.dmgkz.mcjobs.playerdata;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import com.dmgkz.mcjobs.util.IOsaver;
import com.dmgkz.mcjobs.util.PlayerUtils;
import java.util.UUID;

public class PlayerCache implements Serializable {
/*    private static final long serialVersionUID = 2012090401L;
    private static int lastsave_timer = 15;
    private static int expired_timer = 3;

    private static HashMap<String, PlayerCache> playercache = new HashMap<>();
    private static ArrayList<String>            playerperms = new ArrayList<>();

    private ArrayList<String>playerJobs;
    private HashMap<String, Integer> rejoinJobs;
    private HashMap<String, Boolean> showEveryTime;
    private HashMap<String, Double> jobexp;
    private HashMap<String, Integer> joblevel;
    private HashMap<String, String> jobrank;
    private int lastSave;
    private double earnedIncome;
    private int jobCount;
    private int allowedJobs;
    private boolean seenPitch;
    private Date dateModified;
    
    public PlayerCache() {
        playerJobs    = new ArrayList<>();
        rejoinJobs    = new HashMap<>();
        showEveryTime = new HashMap<>();
        jobexp        = new HashMap<>();
        joblevel      = new HashMap<>();
        jobrank       = new HashMap<>();
        lastSave = 0;
        earnedIncome = 0.0D;
        jobCount = 0;
        allowedJobs = PlayerUtils.getAllowed();
        seenPitch = false;
        dateModified = new Date();
    }

    public static boolean playerExists(String player){
        if(playercache.containsKey(player))
            return true;
        
        File file = new File("./plugins/mcjobs/data/" + player + ".dat");
        if(file.exists())
            return true;
    return false;
    }
    
    public static boolean loadPlayerCache(String player) {
        try {
            PlayerCache checkPlayer = (PlayerCache) IOsaver.getFile("./plugins/mcjobs/data/" + player + ".dat");
            playercache.put(player, checkPlayer);
            return true;
        } catch (Exception e) {}
        return false;
    }
    
    public static PlayerCache getPlayerCache(String name){
        if(playercache.containsKey(name))
            return playercache.get(name);
        else {
            if(loadPlayerCache(name)) {
                return playercache.get(name);
            } else {
                return null;
            }
        }
    }
        
    public static PlayerData changePlayerCache(String name) {
        PlayerCache pc = getPlayerCache(name);
        UUID uuid = PlayerUtils.getUUIDByName(name);
        if(pc == null || uuid == null)
            return null;
        PlayerData pd = new PlayerData(name, uuid, pc.playerJobs, pc.rejoinJobs, pc.showEveryTime, pc.jobexp, pc.joblevel, pc.lastSave, pc.earnedIncome, pc.seenPitch ,pc.dateModified.getTime(), "en");
        return pd;
    }*/
}
