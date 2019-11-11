/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dmgkz.mcjobs.database;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.levels.Leveler;
import com.dmgkz.mcjobs.scheduler.McTopSigns;
import com.dmgkz.mcjobs.util.PlayerUtils;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Pappi
 */
public final class Database {
    public static boolean hasPlayerData(UUID uuid) {
        if(McJobs.getPlugin().getConfig().getString("database.type", "yaml").equalsIgnoreCase("mysql")) {
            return isPlayerDataInMySQL(uuid);
        } else {
            return isPlayerDataInYAML(uuid);
        }
    }
    
    private static boolean isPlayerDataInYAML(UUID uuid) {
        File file = new File("plugins/mcjobs/users", uuid.toString() + ".yml");
        return file.exists();
    }
    
    private static boolean isPlayerDataInMySQL(UUID uuid) {
        Connection con;
        boolean isExist = false;
        try {
            con = getConnect();
            PreparedStatement statement;
            statement = con.prepareStatement("SELECT * FROM `mcjobs_data` WHERE `uuid` = ?");
            statement.setString(1, uuid.toString());
            ResultSet rset;
            rset = statement.executeQuery();
            if(rset.next()) {
                isExist = true;
            }
            rset.close();
            statement.close();
            close(con);
        } catch(SQLException ex) {
            McJobs.getPlugin().getLogger().log(Level.WARNING, "Error on Select User data to create the yaml file(s)", ex);
        }
        return isExist;
    }
    
    private static void removePlayerDataFromMySQL(UUID uuid) {
        Connection con;
        try {
            con = getConnect();
            PreparedStatement statement;
            statement = con.prepareStatement("DELETE FROM `mcjobs_data` WHERE `uuid` = ?");
            statement.setString(1, uuid.toString());
            statement.execute();
            statement.close();
            
            statement = con.prepareStatement("DELETE FROM `mcjobs_jobs` WHERE `uuid` = ?");
            statement.setString(1, uuid.toString());
            statement.execute();
            statement.close();
            close(con);
        } catch(SQLException ex) {
            McJobs.getPlugin().getLogger().log(Level.WARNING, "Error on Delete UserData in MySQL of UUID " + uuid.toString(), ex);
        }
    }
    
    public static PlayerData loadPlayer(UUID uuid) {
        PlayerData pc;
        if(McJobs.getPlugin().getConfig().getString("database.type", "yaml").equalsIgnoreCase("mysql")) {
            pc = loadPlayerDataFromMySQL(uuid);
        } else {
            pc = loadPlayerDataFromYAML(uuid);
        }
        //pc._allowedJobs = PlayerUtils.getAllowed(uuid);
        return pc;
    }
    
    public static PlayerData loadPlayer(String name) {
        OfflinePlayer op = PlayerUtils.getOfflinePlayer(name);
        if(op != null)
            return loadPlayer(op.getUniqueId());
        else 
            return null;
    }

    public static void savePlayer(UUID uuid) {
        if(McJobs.getPlugin().getConfig().getString("database.type", "yaml").equalsIgnoreCase("mysql")) {
            savePlayerDataToMySQL(uuid);
        } else {
            savePlayerDataToYAML(uuid);
        }
    }
    
    private static PlayerData loadPlayerDataFromYAML(UUID uuid) {
        File dir = new File("plugins/mcjobs", "users");
        if(!dir.exists())
            dir.mkdir();

        File f = new File(dir, uuid.toString() + ".yml");
        YamlConfiguration user = YamlConfiguration.loadConfiguration(f);
        ArrayList<String> jobs = new ArrayList<>();
        HashMap<String, Integer> rejoin = new HashMap<>();
        HashMap<String, Boolean> show = new HashMap<>();
        HashMap<String, Double> exp = new HashMap<>();
        HashMap<String, Integer> level = new HashMap<>();
        
        if(f.exists()) {
            if(user.isConfigurationSection("jobs")) {
                for(String key : user.getConfigurationSection("jobs").getKeys(false)) {
                    jobs.add(key);
                    exp.put(key, user.getDouble("jobs." + key + ".exp", 0.0D));
                    level.put(key, user.getInt("jobs." + key + ".level", 1));
                    show.put(key, user.getBoolean("jobs." + key + ".showEveryTime", false));
                }
            }
            if(user.isConfigurationSection("rejoinJobs")) {
                for(String key : user.getConfigurationSection("rejoinJobs").getKeys(false)) {
                    rejoin.put(key, user.getInt("rejoinJobs." + key, 0));
                }
            }
        }
        return new PlayerData(Bukkit.getOfflinePlayer(uuid).getName(), uuid, jobs, rejoin, show, exp, level, user.getInt("lastSave", 0), user.getDouble("earnedIncome", 0.0D), user.getBoolean("seenPitch", false), user.getLong("dateModified", System.currentTimeMillis()), user.getString("language", McJobs.getPlugin().getLanguage().getDefaultLang((Bukkit.getPlayer(uuid) != null?Bukkit.getPlayer(uuid).getLocale():""))), user.getString("scoreboard.order", "job"), user.getString("scoreboard.sort", "asc"));
    }

    private static void savePlayerDataToYAML(UUID uuid) {
        if(PlayerData.playerExists(uuid)) {
            File dir = new File("plugins/mcjobs", "users");
            if(!dir.exists()) {
                dir.mkdir();
            }

            File file = new File(dir, uuid.toString() + ".yml");
            YamlConfiguration c = YamlConfiguration.loadConfiguration(file);
            c.set("lastName", PlayerData.getName(uuid));
            c.set("lastSave", PlayerData.getLastSave(uuid));
            c.set("earnedIncome", PlayerData.getEarnedIncome(uuid));
            c.set("seenPitch", PlayerData.getSeenPitch(uuid));
            c.set("dateModified", System.currentTimeMillis());
            c.set("language", PlayerData.getLang(uuid));
            c.set("scoreboard.order", PlayerData.getScoreboardOrder(uuid));
            c.set("scoreboard.sort", PlayerData.getScoreboardSort(uuid));
            
            c.set("rejoinJobs", null);
            c.set("jobs", null);
            
            for(String job: PlayerData.getPlayerJobs(uuid)) {
                if(PlayerData.getRejoinTime(uuid, job) > 0)
                    c.set("rejoinJobs." + job, PlayerData.getRejoinTime(uuid, job));
                else {
                    c.set("jobs." + job + ".showEveryTime", PlayerData.getShowEveryTime(uuid, job));
                    c.set("jobs." + job + ".exp", PlayerData.getJobExp(uuid, job));
                    c.set("jobs." + job + ".level", PlayerData.getJobLevel(uuid, job));
                }
            }

            try {
                c.save(file);
            } catch(IOException ex) {
                McJobs.getPlugin().getLogger().log(Level.WARNING,"Error on saving Player file " + uuid.toString(), ex);
            }
        }
    }
    
    private static PlayerData loadPlayerDataFromMySQL(UUID uuid) {
        Connection con;
        ArrayList<String> jobs = new ArrayList<>();
        HashMap<String, Integer> rejoin = new HashMap<>();
        HashMap<String, Boolean> show = new HashMap<>();
        HashMap<String, Double> exp = new HashMap<>();
        HashMap<String, Integer> level = new HashMap<>();
        int lastSave = 0;
        double earnedIncome = 0.0D;
        boolean seenPitch = false;
        long dateModified = 0;
        String lang = McJobs.getPlugin().getLanguage().getDefaultLang((Bukkit.getPlayer(uuid) != null?Bukkit.getPlayer(uuid).getLocale():""));
        String sbOrder = "job";
        String sbSort = "asc";
        
        try {
            con = getConnect();
            PreparedStatement statement;
            ResultSet rset;
            statement = con.prepareStatement("SELECT `lastSave`,`earnedIncome`,`seenPitch`,`dateModified`,`language`,`sborder`,`sbsort` FROM `mcjobs_data` WHERE `uuid` = ?");
            statement.setString(1, uuid.toString());
            rset = statement.executeQuery();

            if(rset.next()) {
                //McJobs.getPlugin().getLogger().log(Level.WARNING, "Loading PlayerData of UUID {0}", uuid);
                lastSave = rset.getInt("lastSave")*1000;
                earnedIncome = rset.getDouble("earnedIncome");
                seenPitch = rset.getBoolean("seenPitch");
                dateModified = rset.getLong("dateModified");
                lang = rset.getString("language");
                sbOrder = rset.getString("sborder");
                sbSort = rset.getString("sbsort");
                
                PreparedStatement statement2;
                statement2 = con.prepareStatement("SELECT `jobname`,`level`,`exp`,`rejoin`,`show` FROM `mcjobs_jobs` WHERE `uuid` = ?");
                statement2.setString(1, uuid.toString());
                ResultSet rset2;
                rset2 = statement2.executeQuery();
                while(rset2.next()) {
                    jobs.add(rset2.getString("jobname"));
                    if(rset2.getInt("rejoin") == 0) {
                        exp.put(rset2.getString("jobname"), rset2.getDouble("exp"));
                        level.put(rset2.getString("jobname"), rset2.getInt("level"));
                        show.put(rset2.getString("jobname"), rset2.getBoolean("show"));
                    } else
                        rejoin.put(rset2.getString("jobname"), rset2.getInt("rejoin"));
                }
                rset2.close();
                statement2.close();
            }
            rset.close();
            statement.close();
            close(con);
        } catch(SQLException e) {
            McJobs.getPlugin().getLogger().log(Level.WARNING, "Error on load PlayerData on MySQL of " + uuid.toString(), e);
        }
        return new PlayerData(Bukkit.getOfflinePlayer(uuid).getName(), uuid, jobs, rejoin, show, exp, level, lastSave, earnedIncome, seenPitch, dateModified, lang, sbOrder, sbSort);
    }

    private static void savePlayerDataToMySQL(UUID uuid) {
        if(PlayerData.playerExists(uuid)) {
            Connection con;
            try {
                con = getConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("INSERT INTO `mcjobs_data` (`uuid`,`lastName`,`lastSave`,`earnedIncome`,`seenPitch`,`dateModified`,`language`,`sborder`,`sbsort`) VALUES (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE `lastName` = ?,`lastSave` = ?,`earnedIncome` = ?,`seenPitch` = ?,`dateModified` = ?,`language` = ?,`sborder` = ?,`sbsort` = ?");
                statement.setString(1, uuid.toString());
                statement.setString(2, Bukkit.getOfflinePlayer(uuid).getName());
                statement.setInt(3, (int)(System.currentTimeMillis()/1000));
                statement.setDouble(4, PlayerData.getEarnedIncome(uuid));
                statement.setString(5, String.valueOf(PlayerData.getSeenPitch(uuid)));
                statement.setLong(6, System.currentTimeMillis());
                statement.setString(7, PlayerData.getLang(uuid));
                statement.setString(8, PlayerData.getScoreboardOrder(uuid));
                statement.setString(9, PlayerData.getScoreboardSort(uuid));
                statement.setString(10, Bukkit.getOfflinePlayer(uuid).getName());
                statement.setInt(11, PlayerData.getLastSave(uuid));
                statement.setDouble(12, PlayerData.getEarnedIncome(uuid));
                statement.setString(13, String.valueOf(PlayerData.getSeenPitch(uuid)));
                statement.setLong(14, System.currentTimeMillis());
                statement.setString(15, PlayerData.getLang(uuid));
                statement.setString(16, PlayerData.getScoreboardOrder(uuid));
                statement.setString(17, PlayerData.getScoreboardSort(uuid));
                statement.execute();
                statement.close();

                //Delete Player Jobs before save
                statement = con.prepareStatement("DELETE FROM `mcjobs_jobs` WHERE `uuid` = ?");
                statement.setString(1, uuid.toString());
                statement.execute();
                statement.close();

                //Save Player Jobs and ReJoin Jobs
                if(PlayerData.getPlayerJobs(uuid).size() > 0) {
                    statement = con.prepareStatement("INSERT INTO `mcjobs_jobs` (`uuid`,`jobname`,`level`,`exp`,`needexp`,`rank`,`rejoin`,`show`) VALUES (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE `level`=?,`exp`=?,`needexp`=?,`rank`=?,`rejoin`=?,`show`=?");
                    statement.setString(1, uuid.toString());
                    //Player Jobs
                    for(String job: PlayerData.getPlayerJobs(uuid)) {
                        if(PlayerData.getRejoinTime(uuid, job) > 0) {
                            statement.setString(2, job);
                            statement.setInt(3, 0);
                            statement.setDouble(4, 0.0D);
                            statement.setDouble(5, 0);
                            statement.setString(6, "none");
                            statement.setInt(7, PlayerData.getRejoinTime(uuid, job));
                            statement.setString(8, "false");
                            statement.setInt(9, 0);
                            statement.setDouble(10, 0.0D);
                            statement.setDouble(11, 0);
                            statement.setString(12, "none");
                            statement.setInt(13, PlayerData.getRejoinTime(uuid, job));
                            statement.setString(14, "false");
                        } else {
                            statement.setString(2, job);
                            statement.setInt(3, PlayerData.getJobLevel(uuid, job));
                            statement.setDouble(4, PlayerData.getJobExp(uuid, job));
                            statement.setDouble(5, Leveler.getXPtoLevel(PlayerData.getJobLevel(uuid, job)));
                            statement.setString(6, Leveler.getRank(PlayerData.getJobLevel(uuid, job)));
                            statement.setInt(7, 0);
                            statement.setString(8, String.valueOf(PlayerData.getShowEveryTime(uuid, job)));
                            statement.setInt(9, PlayerData.getJobLevel(uuid, job));
                            statement.setDouble(10, PlayerData.getJobExp(uuid, job));
                            statement.setDouble(11, Leveler.getXPtoLevel(PlayerData.getJobLevel(uuid, job)));
                            statement.setString(12, Leveler.getRank(PlayerData.getJobLevel(uuid, job)));
                            statement.setInt(13, 0);
                            statement.setString(14, String.valueOf(PlayerData.getShowEveryTime(uuid, job)));
                        }
                        statement.execute();
                    }

                    if(!statement.isClosed())
                        statement.close();
                }
                close(con);
            } catch(SQLException e) {
                McJobs.getPlugin().getLogger().log(Level.WARNING,"Error on saving Player data from PlayerData to MySQL", e);
            }
        }
    }
    
    public static void getTopPlayerJobs() {
        if(McJobs.getPlugin().getConfig().getString("database.type", "yaml").equalsIgnoreCase("mysql")) {
            getTopPlayerJobsFromMySQL();
        } else {
            getTopPlayerJobsFromYAML();
        }
    }
    
    private static void getTopPlayerJobsFromYAML() {
        File dir = new File("plugins/mcjobs", "users");
        if(!dir.exists())
            return;
        
        for(File f: dir.listFiles()) {
            String name = f.getName();
            int pos = name.lastIndexOf(".");
            if(pos > 0)
                name = name.substring(0, pos);
            
            OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(name));
            if(op == null || op.getName() == null || op.getUniqueId() == null || op.getName().isEmpty() || op.getName().equalsIgnoreCase("null"))
                continue;
            
            YamlConfiguration user = YamlConfiguration.loadConfiguration(f);
            if(user.isConfigurationSection("jobs")) {
                for(String key : user.getConfigurationSection("jobs").getKeys(false)) {
                    McTopSigns.addPlyerToTop(op, key, user.getDouble("jobs." + key + ".exp", 0.0D));
                }
            }
            user = null;
            f = null;
        }
    }
    
    private static void getTopPlayerJobsFromMySQL() {
        Connection con;

        try {
            con = getConnect();
            PreparedStatement statement;
            for(String job: PlayerJobs.getJobsList().keySet()) {
                statement = con.prepareStatement("SELECT `uuid`,`exp` FROM `mcjobs_jobs` WHERE `jobname` = ? AND `exp` > ? LIMIT 0,50");
                statement.setString(1, job);
                statement.setDouble(2, 0.0d);
                ResultSet rset = statement.executeQuery();
                while(rset.next()) {
                    OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(rset.getString("uuid")));
                    if(op == null || op.getName() == null || op.getUniqueId() == null || op.getName().isEmpty() || op.getName().equalsIgnoreCase("null"))
                        continue;
                    
                    McTopSigns.addPlyerToTop(op, job, rset.getDouble("exp"));
                }
                rset.close();
                statement.close();
            }
            close(con);
        } catch(SQLException e) {
            McJobs.getPlugin().getLogger().log(Level.WARNING, "Error on load Top Jobs frpm MySQL", e);
        }
    }
    
    public static boolean checkConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch(ClassNotFoundException t) {
            McJobs.getPlugin().getLogger().log(Level.WARNING,"Where is the MySQL driver????", t);
            return false;
        }

        Connection con = getConnect();
        if(con == null) {
            McJobs.getPlugin().getLogger().log(Level.WARNING,"Whats wrong? I cant connect to the Database!!!");
            return false;
        }

        if(!structure2(con)) {
            McJobs.getPlugin().getLogger().log(Level.WARNING,"Oh no cant create mcjobs_data table.");
            close(con);
            return false;
        }

        if(!structure1(con)) {
            McJobs.getPlugin().getLogger().log(Level.WARNING,"What are you do? Cant create mcjobs_jobs table.");
            close(con);
            return false;
        }
        
        if(!updateOldTable(con)) {
            McJobs.getPlugin().getLogger().log(Level.WARNING,"Can''t update Tables.");
            close(con);
            return false;
        }
        close(con);
        return true;
    }

    //JobDaten
    private static boolean structure1(Connection con) {
        try {
            PreparedStatement statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `mcjobs_jobs` ("
                    + "`uuid` varchar(64) NOT NULL, "
                    + "`jobname` varchar(32) NOT NULL, "
                    + "`level` int(11) NOT NULL, "
                    + "`exp` double(8,0) NOT NULL, "
                    + "`needexp` double(8,0) NOT NULL, "
                    + "`rank` varchar(32) DEFAULT NULL, "
                    + "`rejoin` bigint(13) DEFAULT '0',"
                    + "`show` enum('true','false') NOT NULL DEFAULT 'false', "
                    + "PRIMARY KEY (`uuid`,`jobname`)) "
                    + "ENGINE=MyISAM DEFAULT CHARSET=latin1"
            );
            statement.execute();
            statement.close();
            return true;
        } catch(SQLException ex) {
            McJobs.getPlugin().getLogger().log(Level.WARNING,"Cant create Table mcjobs_jobs", ex);
        }
        return false;
    }

    //Userdaten
    private static boolean structure2(Connection con) {
        try {
            PreparedStatement statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `mcjobs_data` ("
                    + "`uuid` varchar(64) NOT NULL, "
                    + "`lastName` varchar(32) NOT NULL, "
                    + "`lastSave` int(11) NOT NULL, "
                    + "`earnedIncome` double NOT NULL, "
                    + "`seenPitch` enum('true','false') NOT NULL DEFAULT 'false', "
                    + "`dateModified` bigint(13) NOT NULL, "
                    + "`language` varchar(32) NOT NULL, "
                    + "`sborder` varchar(32) NOT NULL DEFAULT 'job', "
                    + "`sbsort` varchar(32) NOT NULL DEFAULT 'asc', "
                    + "PRIMARY KEY (`uuid`) "
                    + ") ENGINE=MyISAM DEFAULT CHARSET=latin1;"
            );
            statement.execute();
            statement.close();
            return true;
        } catch(SQLException ex) {
            McJobs.getPlugin().getLogger().log(Level.WARNING,"Cant create Table mcjobs_data", ex);
        }
        return false;
    }
    
    private static boolean updateOldTable(Connection con) {
        try {
            DatabaseMetaData md;
            PreparedStatement statement;
            ResultSet rs;
            
            md = con.getMetaData();
            rs = md.getColumns(null, null, "mcjobs_data", "username");
            if(rs.next()) {
                statement = con.prepareStatement("ALTER TABLE `mcjobs_data` CHANGE COLUMN username uuid varchar(64) NOT NULL, ADD lastName varchar(32) NOT NULL AFTER uuid");
                statement.execute();
                statement.close();
            }
            rs.close();
            
            md = con.getMetaData();
            rs = md.getColumns(null, null, "mcjobs_jobs", "username");
            if(rs.next()) {
                statement = con.prepareStatement("ALTER TABLE `mcjobs_jobs` CHANGE COLUMN username uuid varchar(64) NOT NULL");
                statement.execute();
                statement.close();
            }
            rs.close();
            
            md = con.getMetaData();
            rs = md.getColumns(null, null, "mcjobs_data", "sborder");
            if(!rs.next()) {
                statement = con.prepareStatement("ALTER TABLE `mcjobs_data` ADD COLUMN sborder varchar(32) NOT NULL DEFAULT 'job'");
                statement.execute();
                statement.close();
            }
            rs.close();
            
            md = con.getMetaData();
            rs = md.getColumns(null, null, "mcjobs_data", "sbsort");
            if(!rs.next()) {
                statement = con.prepareStatement("ALTER TABLE `mcjobs_data` ADD COLUMN sbsort varchar(32) NOT NULL DEFAULT 'job'");
                statement.execute();
                statement.close();
            }
            rs.close();
            return true;
        } catch(SQLException ex) {
            McJobs.getPlugin().getLogger().log(Level.WARNING,"Error on update Tables mcjobs_data and mcjobs_jobs", ex);
        }
        return false;
    }

    public static Connection getConnect() {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + McJobs.getPlugin().getConfig().getString("database.host", "127.0.0.1")
                            + ":" + McJobs.getPlugin().getConfig().getString("database.port", "3306")
                            + "/" + McJobs.getPlugin().getConfig().getString("database.name", "mc_server"),
                            McJobs.getPlugin().getConfig().getString("database.username", "root"),
                            McJobs.getPlugin().getConfig().getString("database.password", "root")
            );
        } catch(SQLException ex) {
            McJobs.getPlugin().getLogger().log(Level.WARNING,"Error on connect to Database Service!!!", ex);
        }
        return null;
    }

    private static void close(Connection con) {
        if(con == null)
            return;

        try {
            con.close();
        } catch(SQLException e) {
            McJobs.getPlugin().getLogger().log(Level.WARNING,"Error on close connection", e);
        }
    }
}
