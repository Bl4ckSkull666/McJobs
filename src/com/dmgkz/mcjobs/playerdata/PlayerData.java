package com.dmgkz.mcjobs.playerdata;

import com.dmgkz.mcjobs.database.Database;
import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.localization.GetLanguage;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.levels.Leveler;
import com.dmgkz.mcjobs.util.PlayerKills;
import com.dmgkz.mcjobs.util.PlayerUtils;
import com.dmgkz.mcjobs.util.UpperCaseFirst;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerData {
    private final String _lastName;
    private final UUID _uuid;
    private static int _lastsave_timer = 15;
    private static int _expired_timer = 3;
    public final ArrayList<String> _playerJobs = new ArrayList();
    public final HashMap<String, Integer> _rejoinJobs = new HashMap();
    public final HashMap<String, Boolean> _showEveryTime = new HashMap();
    public final HashMap<String, Double> _jobexp = new HashMap();
    public final HashMap<String, Integer> _joblevel = new HashMap();
    public final HashMap<String, String> _jobrank = new HashMap();
    public int _lastSave;
    public double _earnedIncome;
    public int _jobCount;
    public int _allowedJobs;
    public boolean _seenPitch;
    public Date _dateModified;
    public String _playerLang;
    public PlayerKills _killManager;
    
    public PlayerData(String pName, UUID uuid, ArrayList<String> jobs, HashMap<String, Integer> rejoin, HashMap<String, Boolean> show, HashMap<String, Double> exp, HashMap<String, Integer> level, int lastSave, double earnedIncome, boolean seenPitch, long dateModified, String lang) {
        _lastName = pName;
        _uuid = uuid;
        _playerJobs.addAll(jobs);
        _rejoinJobs.putAll(rejoin);
        _showEveryTime.putAll(show);
        _jobexp.putAll(exp);
        _joblevel.putAll(level);
        for(String job: _playerJobs)
            _jobrank.put(job, Leveler.getRank(_joblevel.get(job)));
        _jobCount = 0;
        for(String job: jobs) {
            _jobrank.put(job, Leveler.getRank((level.containsKey(job)?level.get(job):1)));
            if(PlayerJobs.getJobsList().containsKey(job)) {
                if(!PlayerJobs.getJobsList().get(job).getData().compJob().isDefault())
                    _jobCount++;
            }
        }
        _lastSave = lastSave;
        _earnedIncome = earnedIncome;
        _allowedJobs = PlayerUtils.getAllowed();
        _seenPitch = seenPitch;
        _dateModified = new Date(dateModified);
        _playerLang = lang;
        _killManager = new PlayerKills(_uuid);
    }
    
    
    //Statics
    private final static HashMap<UUID, PlayerData> _playerdatas = new HashMap<>();
    private final static ArrayList<UUID> _playerperms = new ArrayList<>();
    
    public static PlayerData getPlayerData(UUID uuid) {
        if(_playerdatas.containsKey(uuid))
            return _playerdatas.get(uuid);
       
        _playerdatas.put(uuid, Database.loadPlayer(uuid));
        return _playerdatas.get(uuid);
    }
    
    public static String getName(UUID uuid) {
        PlayerData checkPlayer = getPlayerData(uuid);
        return checkPlayer._lastName;
    }
  
    public static String getLang(UUID uuid) {
        PlayerData checkPlayer = getPlayerData(uuid);
        return checkPlayer._playerLang;
    }

    public static void setLang(UUID uuid, String l) {
        PlayerData cP = getPlayerData(uuid);
        if(McJobs.getPlugin().getLanguage().isLang(l))
            cP._playerLang = l;
        else
            cP._playerLang = McJobs.getPlugin().getLanguage().getDefaultLang();
    }
  
    public static boolean hasJob(UUID uuid, String job) {
        PlayerData checkPlayer = getPlayerData(uuid);
        return checkPlayer._playerJobs.contains(job);
    }

    public static int getJobCount(UUID uuid) {
        PlayerData checkPlayer = getPlayerData(uuid);
        return checkPlayer._jobCount;
    }
  
    public static int getAllowedJobCount(UUID uuid) {
        PlayerData checkPlayer = getPlayerData(uuid);
        return checkPlayer._allowedJobs;
    }
  
    public static ArrayList<String> getPlayerJobs(UUID uuid) {
        PlayerData checkPlayer = getPlayerData(uuid);
        return checkPlayer._playerJobs;
    }
  
    public static boolean addJob(UUID uuid, String job) {
        PlayerData checkPlayer = getPlayerData(uuid);
        if(checkPlayer._playerJobs.contains(job))
            return false;
        
        checkPlayer._playerJobs.add(job);
        if (!PlayerJobs.getJobsList().get(job).getData().compJob().isDefault())
            checkPlayer._jobCount += 1;
        
        checkPlayer._showEveryTime.put(job, PlayerJobs.getJobsList().get(job).getData().getShowEveryTime());
        checkPlayer._joblevel.put(job, 1);
        checkPlayer._jobexp.put(job, 0.0D);
        checkPlayer._jobrank.put(job, Leveler.getRank(1));
        return true;
    }
  
    public static boolean removeJob(UUID uuid, String job) {
        PlayerData checkPlayer = getPlayerData(uuid);
        String jobName = UpperCaseFirst.toUpperFirst(McJobs.getPlugin().getLanguage().getJobNameInLang(job, uuid).toLowerCase());
        if (checkPlayer._playerJobs.contains(job)) {
            checkPlayer._playerJobs.remove(job);
            if (!PlayerJobs.getJobsList().get(job).getData().compJob().isDefault())
                checkPlayer._jobCount -= 1;
            
            checkPlayer._showEveryTime.remove(job);
            checkPlayer._jobexp.remove(job);
            checkPlayer._joblevel.remove(job);
            checkPlayer._jobrank.remove(job);
            if(Bukkit.getPlayer(uuid) != null)
                Bukkit.getPlayer(uuid).sendMessage(McJobs.getPlugin().getLanguage().getExperience("reset", uuid).addVariables(jobName, Bukkit.getPlayer(uuid).getName(), ""));

            //savePlayerCache(player);
            return true;
        }
        return false;
    }
  
    public static void verifyPlayerCache(UUID uuid) {
        PlayerData cP = getPlayerData(uuid);
        int temp = 0;
        for(String job : cP._playerJobs) {
            if(PlayerJobs.getJobsList().containsKey(job)) {
                if(!PlayerJobs.getJobsList().get(job).getData().compJob().isDefault())
                    temp++;
            }
        }
        cP._jobCount = temp;
        cP._allowedJobs = PlayerUtils.getAllowed(uuid);
        if(!cP._rejoinJobs.isEmpty() && !_playerperms.contains(uuid)) {
            _playerperms.add(uuid);
            savePlayerPerms();
        }
    }
  
    public static String getJobRank(UUID uuid, String job) {
        PlayerData checkPlayer = getPlayerData(uuid);
        return checkPlayer._jobrank.containsKey(job)?checkPlayer._jobrank.get(job):"";
    }
    
    public static void resetJobRank(UUID uuid, String job) {
        PlayerData checkPlayer = getPlayerData(uuid);
        if(checkPlayer._playerJobs.contains(job.toLowerCase())) {
            checkPlayer._jobrank.put(job, Leveler.getRank(checkPlayer._joblevel.get(job)));
        }
    }
  
    public static boolean addLevels(UUID uuid, String job, int levels) {
        PlayerData checkPlayer = getPlayerData(uuid);
        if (checkPlayer._playerJobs.contains(job.toLowerCase())) {
            int job_level = checkPlayer._joblevel.get(job.toLowerCase());
            job_level += levels;
      
            checkPlayer._joblevel.put(job.toLowerCase(), job_level);
            return true;
        }
        return false;
    }
    
    public static void setLevel(UUID uuid, String job, int lvl) {
        PlayerData checkPlayer = getPlayerData(uuid);
        if(checkPlayer._playerJobs.contains(job.toLowerCase())) {
            checkPlayer._joblevel.put(job.toLowerCase(), lvl);
        }
    }
  
    public static Integer getJobLevel(UUID uuid, String job) {
        PlayerData checkPlayer = getPlayerData(uuid);
        return checkPlayer._joblevel.containsKey(job)?checkPlayer._joblevel.get(job):0;
    }
  
    public static double getJobExp(UUID uuid, String job) {
        PlayerData checkPlayer = getPlayerData(uuid);
        return checkPlayer._jobexp.containsKey(job)?checkPlayer._jobexp.get(job):0.0D;
    }
  
    public static String getJobExpDisplay(UUID uuid, String job) {
        PlayerData checkPlayer = getPlayerData(uuid);
        if (checkPlayer._jobexp.containsKey(job)) {
            DecimalFormat df = new DecimalFormat("#,###,###,##0.##");
            return df.format(checkPlayer._jobexp.get(job));
        }
        return "0";
    }
    
    public static void setExp(UUID uuid, String job, double amount) {
        PlayerData checkPlayer = getPlayerData(uuid);
        if(checkPlayer._playerJobs.contains(job.toLowerCase())) {
            if(amount < Leveler.getXPtoLevel(checkPlayer._joblevel.get(job.toLowerCase()))) {
                checkPlayer._jobexp.put(job.toLowerCase(), amount);
            }
        }
    }
  
    public static boolean addExp(UUID uuid, String job, double amount) {
        PlayerData checkPlayer = getPlayerData(uuid);
        if(checkPlayer._jobexp.containsKey(job)) {
            double value = checkPlayer._jobexp.get(job) + amount;
            Integer level = checkPlayer._joblevel.get(job);
            if(Leveler.getXPtoLevel(level) <= value) {
                while (Leveler.getXPtoLevel(level) <= value) {
                    value -= Leveler.getXPtoLevel(level);
          
                    level = level + 1;
                    String rank = Leveler.getRank(level);
          
                    checkPlayer._jobexp.put(job, value);
                    checkPlayer._joblevel.put(job, level);
                    if(!checkPlayer._jobrank.get(job).equalsIgnoreCase(rank)) {
                        checkPlayer._jobrank.put(job, rank);
                        if(Bukkit.getPlayer(uuid) != null)
                            Bukkit.getPlayer(uuid).sendMessage(McJobs.getPlugin().getLanguage().getExperience("rank", uuid).addVariables(McJobs.getPlugin().getLanguage().getJobNameInLang(job, uuid), Bukkit.getPlayer(uuid).getName(), McJobs.getPlugin().getLanguage().getJobRank(rank, uuid)));
                    }
                    if(Bukkit.getPlayer(uuid) != null)
                        Bukkit.getPlayer(uuid).sendMessage(McJobs.getPlugin().getLanguage().getExperience("level", uuid).addVariables(McJobs.getPlugin().getLanguage().getJobNameInLang(job, uuid), Bukkit.getPlayer(uuid).getName(), level.toString()));
                }
                //savePlayerCache(player);
            } else {
                checkPlayer._jobexp.put(job, value);
            }
            return true;
        }
        return false;
    }
  
    public static boolean addReJoinTimer(UUID uuid, String job, int iTimer) {
        PlayerData checkPlayer = getPlayerData(uuid);
        if (!checkPlayer._rejoinJobs.containsKey(job)) {
            checkPlayer._rejoinJobs.put(job, iTimer);
            if (!_playerperms.contains(uuid)) {
                _playerperms.add(uuid);
                savePlayerPerms();
            }
        } else
            return false;
        return true;
    }
  
    public static boolean decrementTimer(UUID uuid) {
        GetLanguage modText = McJobs.getPlugin().getLanguage();

        PlayerData checkPlayer = getPlayerData(uuid);
        boolean removePlayer = false;

        Iterator<Map.Entry<String, Integer>> it = checkPlayer._rejoinJobs.entrySet().iterator();
        if (it.hasNext()) {
            checkPlayer._lastSave += 1;
        }
        
        while (it.hasNext()) {
            Map.Entry<String, Integer> pair = (Map.Entry)it.next();
            int i = pair.getValue();
            i--;
            if(i < 1) {
                if(Bukkit.getPlayer(uuid) != null) {
                    Player play = Bukkit.getPlayer(uuid);
                    play.sendMessage(modText.getJobCommand("rejoin", uuid).addVariables((String)pair.getKey(), play.getName(), ""));
                }
                it.remove();
                //savePlayerCache(player);
            } else
                pair.setValue(i);
            
        }
        
        if (checkPlayer._rejoinJobs.isEmpty())
            removePlayer = true;
        
        if(checkPlayer._lastSave > _lastsave_timer) {
            checkPlayer._lastSave = 0;
            //savePlayerCache(player);
        }
        return removePlayer;
    }
  
    public static boolean removeRejoinTimer(UUID uuid, String job) {
        PlayerData checkPlayer = getPlayerData(uuid);
        if (checkPlayer._rejoinJobs.containsKey(job)) {
            checkPlayer._rejoinJobs.remove(job);
            if(checkPlayer._rejoinJobs.isEmpty()) {
                _playerperms.remove(uuid);
            }
            return true;
        }
        return false;
    }
  
    public static ArrayList<UUID> getPlayerPerms() {
        return _playerperms;
    }
  
    public static int getRejoinTime(UUID uuid, String job) {
        PlayerData checkPlayer = getPlayerData(uuid);
        if (checkPlayer._rejoinJobs.containsKey(job))
            return checkPlayer._rejoinJobs.get(job);

        return 0;
    }
  
    public static boolean isJoinable(UUID uuid, String job) {
        PlayerData checkPlayer = getPlayerData(uuid);       
        return !checkPlayer._rejoinJobs.containsKey(job);
    }
  
    public static void setShowEveryTime(UUID uuid, String job, boolean value) {
        PlayerData checkPlayer = getPlayerData(uuid);
        checkPlayer._showEveryTime.put(job, value);
    }
  
    public static boolean getShowEveryTime(UUID uuid, String job) {
        PlayerData checkPlayer = getPlayerData(uuid);
        if (checkPlayer._showEveryTime.containsKey(job))
            return checkPlayer._showEveryTime.get(job);
        return false;
    }

    public static void setEarnedIncome(UUID uuid, double value) {
        PlayerData checkPlayer = getPlayerData(uuid);
        checkPlayer._earnedIncome = value;
    }

    public static double getEarnedIncome(UUID uuid) {
        PlayerData checkPlayer = getPlayerData(uuid);
        return checkPlayer._earnedIncome;
    }

    public static boolean getSeenPitch(UUID uuid) {
        PlayerData checkPlayer = getPlayerData(uuid);
        return checkPlayer._seenPitch;
    }

    public static void setSeenPitch(UUID uuid, boolean b) {
        PlayerData checkPlayer = getPlayerData(uuid);
        checkPlayer._seenPitch = b;
    }

    public static int getLastSave(UUID uuid) {
      PlayerData checkPlayer = getPlayerData(uuid);
      return checkPlayer._lastSave;
    }

    public static void setLastSave(UUID uuid, int time) {
        PlayerData checkPlayer = getPlayerData(uuid);
        checkPlayer._lastSave = time;
    }

    public static boolean savePlayerCache(UUID uuid) {
        PlayerData checkPlayer = getPlayerData(uuid);

        checkPlayer._dateModified = new Date();
        checkPlayer._killManager.save();
        Database.savePlayer(uuid);
        return true;
    }

    public static boolean playerExists(UUID uuid) {
        if(_playerdatas.containsKey(uuid))
            return true;

        return Database.hasPlayerData(uuid);
    }

    public static void loadPlayerPerms() {
        if(!McJobs.getPlugin().getDataFolder().exists())
            McJobs.getPlugin().getDataFolder().mkdir();
        File file = new File(McJobs.getPlugin().getDataFolder(), "perms.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
                savePlayerPerms();
            } catch (IOException e) {
                McJobs.getPlugin().getLogger().info("Unable to create perms.yml!");
            }
        } else {
            FileConfiguration perm = YamlConfiguration.loadConfiguration(file);
            if(perm.isList("perms")) {
                for(String str: perm.getStringList("perms"))
                    _playerperms.add(UUID.fromString(str));
            }
        }
    }

    public static void savePlayerPerms() {
        try {
            if(!McJobs.getPlugin().getDataFolder().exists())
                McJobs.getPlugin().getDataFolder().mkdir();
            File file = new File(McJobs.getPlugin().getDataFolder(), "perms.yml");
            FileConfiguration perm = YamlConfiguration.loadConfiguration(file);
            perm.set("perms", null);
            List<String> temp = new ArrayList<>();
            for(UUID uuid: _playerperms)
                temp.add(uuid.toString());
            perm.set("perms", temp);
            perm.save(file);
        } catch (Exception e) {
            McJobs.getPlugin().getLogger().info("Unable to save perms.yml!");
        }
    }

    public static PlayerKills getPlayerKills(UUID uuid) {
        if(_playerdatas.containsKey(uuid))
            return _playerdatas.get(uuid)._killManager;
        return null;
    }
  
    public static void removePlayerCache(UUID uuid) {
        if(_playerdatas.containsKey(uuid))
            _playerdatas.remove(uuid);
    }
  
    public static boolean isCacheOld(UUID uuid) {
        PlayerData checkPlayer = getPlayerData(uuid);
        if(checkPlayer._playerJobs.isEmpty())
            return true;
        
        if (Bukkit.getPlayer(uuid) != null)
            return false;

        boolean deletecache = false;

        for(String job: checkPlayer._playerJobs) {
            if(checkPlayer._joblevel.get(job) == 1)
                deletecache = true;

            if(checkPlayer._jobexp.get(job) > 0.0D) {
                deletecache = false;
                break;
            }
        }
        
        Date now = new Date();
        Calendar cal = Calendar.getInstance();

        cal.setTime(checkPlayer._dateModified);
        cal.add(5, _expired_timer);

        Date modified = cal.getTime();
        if (modified.before(now))
            deletecache = true;
        return deletecache;
    }
  
    public static void setLastSaveTimer(int i) {
        if (i > 0)
            _lastsave_timer = i;
        else
            _lastsave_timer = 15;
    }
  
    public static void setExpired(int i) {
        if (i > 0)
            _expired_timer = i;
        else
            _expired_timer = 90;
    }
    
    public static void saveAllPlayerCaches() {
        for(Map.Entry<UUID, PlayerData> me : _playerdatas.entrySet()) {
            Database.savePlayer(me.getKey());
        }
    }
}

