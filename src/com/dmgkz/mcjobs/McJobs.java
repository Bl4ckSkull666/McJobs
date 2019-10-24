package com.dmgkz.mcjobs;

import java.io.File;
import java.io.InputStream;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.dmgkz.mcjobs.commands.AdminCommand;
import com.dmgkz.mcjobs.commands.JobsCommand;
import com.dmgkz.mcjobs.database.Database;
import com.dmgkz.mcjobs.listeners.BlockBreak;
import com.dmgkz.mcjobs.listeners.MCListeners;
import com.dmgkz.mcjobs.listeners.initListener;
import com.dmgkz.mcjobs.listeners.mcjobs.JobChangeListener;
import com.dmgkz.mcjobs.localization.GetLanguage;
import com.dmgkz.mcjobs.logging.BlockLoggers;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.levels.Leveler;
import com.dmgkz.mcjobs.playerjobs.pay.PayMoney;
import com.dmgkz.mcjobs.playerjobs.pay.PayXP;
import com.dmgkz.mcjobs.scheduler.McJobsNotify;
import com.dmgkz.mcjobs.scheduler.McJobsComp;
import com.dmgkz.mcjobs.scheduler.McJobsPreComp;
import com.dmgkz.mcjobs.scheduler.McJobsRemovePerm;
import com.dmgkz.mcjobs.util.ConfigMaterials;
import com.dmgkz.mcjobs.util.Holder;
import com.dmgkz.mcjobs.util.LanguageCheck;
import com.dmgkz.mcjobs.util.PlayerUtils;
import com.dmgkz.mcjobs.util.ResourceList;
import com.dmgkz.mcjobs.util.SignManager;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import com.dmgkz.mcjobs.util.Metrics;
import org.bukkit.configuration.InvalidConfigurationException;


public class McJobs extends JavaPlugin {
    private static McJobs _mcJobs;
    private Long _time = 72000L;
    private Long _notify = 72000L;
    private Integer _version = 0;
    private String _localization = "en";

    private boolean _bPrune = false;
    private boolean _bQuit = false;
    
    private static WorldGuardPlugin _wgp = null;
    private static Economy _economy = null;
    
    public static final int _VERSION = 3800; 
    
    private GetLanguage _language;
    private BlockLoggers _blocklogger;
    private SignManager _signManager;
    private Holder _holder;
    
    //private MetricsLite _metric;
    
    @Override
    public void onEnable() {        
        _mcJobs = this;
        loadClasses();
        
        getCommand("mcjobs").setExecutor(new JobsCommand());
        getCommand("mcjobsadmin").setExecutor(new AdminCommand());

        initListener.RegisterListeners(this);

        if(getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            MCListeners.setWorldGuard(true);
            _wgp = (WorldGuardPlugin)getServer().getPluginManager().getPlugin("WorldGuard");
            getLogger().info("WorldGuard found.  Enabling WorldGuard protections.");
        }

        try {        
            loadEconomyBridges();
        } catch(Exception e) {
            getLogger().info("Unable to load Economy mods.");
            getLogger().info("Using XP economy.");
            McJobsComp.setXP(true);
        }

        try {
            mcloadconf(this);
        } catch(Exception e) {
            getLogger().severe("mcloadconf failure.  Your config.yml file is corrupted delete it and let it rebuild.");  
            getLogger().info("Shutting down MC Jobs.");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            _bQuit = true;
        }

        if(!_bQuit) {
            ConfigMaterials.load(this.getConfig());
            PlayerData.loadPlayerPerms();
            try {
                Metrics metric = new Metrics(this);
                metric.addCustomChart(new Metrics.SimplePie("jobservers", () -> String.valueOf(PlayerJobs.getJobsList().size())));
            } catch(Exception ex) {
                getLogger().fine("Error in Metrics. Shit happend.");
            }
            getServer().getScheduler().runTask(this, new LanguageCheck());
            getServer().getScheduler().scheduleSyncRepeatingTask(this, new McJobsRemovePerm(), 1200L, 1200L);
            getServer().getScheduler().scheduleSyncRepeatingTask(this, new McJobsPreComp(), 200L, 200L);
            getLogger().info("MC Jobs has been enabled!");
        }
    }
    

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        
        PlayerData.savePlayerPerms();
        PlayerData.saveAllPlayerCaches();
        
        getLogger().info("Canceling Tasks...");
        getLogger().info("MC Jobs has been disabled!");
    }

    public void mcloadconf(Plugin plugin) throws Exception {
        FileConfiguration config = plugin.getConfig();
        File file = new File("./plugins/mcjobs/config.yml");
        
        if(!file.exists()){
            config.options().copyDefaults(true);
            saveConfig();            
        }            

        ConfigurationSection section = config.getConfigurationSection("advanced");
        if(section.isInt("version"))
            _version = section.getInt("version");

        if(!section.isInt("version") || _version != _VERSION) {
            getLogger().severe("IF YOU'RE UPGRADING FROM MC JOBS 2.8.X or 3.0.X THIS WILL BREAK YOUR DATA'S USER FILES!!!");
            getLogger().severe("THEY WILL HAVE TO START OVER!");
            getLogger().info("Config.yml is out of date.  Delete config.yml to build a new one.");
            getLogger().info("Disabling MC Jobs.");
            
            getServer().getPluginManager().disablePlugin(this);
            _bQuit = true;
            return;
        }
        
        _localization = config.getString("advanced.language");
        if(_localization.isEmpty() || !_language.getLanguages().containsKey(_localization)) {
            getLogger().log(Level.INFO, "Cant find default language in config.yml!! Stop Plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLanguage().setDefaultLang(config.getString("advanced.language"));
        
        _bPrune = config.getBoolean("advanced.prune");
        
        PlayerJobs.setPercent(config.getInt("percent_cost"));

        MCListeners.setPaySpawner(config.getBoolean("advanced.pay_spawners"));
        MCListeners.setMultiWorld(config.getBoolean("advanced.multiWorld"));

        BlockBreak.setNoPitch(config.getBoolean("advanced.nopitch"));
        McJobsNotify.setShow(config.getBoolean("show_pay"));
        PayMoney.setMaxPay(config.getDouble("max_pay"));
        PayXP.setMaxPay(config.getDouble("max_pay"));

        Leveler.setXPMod(config.getDouble("advanced.xp_modifier"));
        Leveler.setPayMod(config.getDouble("advanced.pay_scale"));

        JobChangeListener.setTimer(config.getInt("timers.rejoin_interval"));
        PlayerData.setLastSaveTimer(config.getInt("timers.player_save"));
        PlayerData.setExpired(config.getInt("timers.delete_cache"));

        if(config.getInt("advanced.spawn_distance") > 0)
            MCListeners.setSpawnDist(config.getInt("advanced.spawn_distance"));

        if(config.getLong("timers.time_interval", 1200) < 1) {
            _time = 1200L;
            BlockLoggers.setTimer(_time);
        } else {
            _time = config.getLong("timers.time_interval") * 1000L;
            BlockLoggers.setTimer(_time);
        }

        if(config.getLong("timers.show_interval") < 1) {
            _notify = 1200L;
            McJobsNotify.setTime(1);
        } else {
            _notify = config.getLong("timers.show_interval") * 20L * 60L;
            McJobsNotify.setTime(config.getInt("timers.show_interval"));
        }

        //Load all available Jobs
        loadJobs();
        
        //Load Job Ranks
        if(config.isConfigurationSection("ranks")) {
            for(String rank: config.getConfigurationSection("ranks").getKeys(false))
                Leveler.getRanks().put(config.getInt("ranks." + rank), rank);
        }
        
        //Load Max Jobs per Group
        if(config.isConfigurationSection("max_jobs")) {
            for(String group: config.getConfigurationSection("max_jobs").getKeys(false)) {
                PlayerUtils.getMaxDefaults().put(group.toLowerCase(), config.getInt("max_jobs." + group));
            }
        }
        if(!PlayerUtils.getMaxDefaults().containsKey("default")){
            getLogger().info("max_jobs corrupted.  No default value found.  Setting default to 3!");
            PlayerUtils.getMaxDefaults().put("default", 3);
        }
        
        for(Map.Entry<String, Integer> me: PlayerUtils.getMaxDefaults().entrySet()) {
            getLogger().info("Group " + me.getKey() + " can learn " + me.getValue().intValue() + " Jobs.");
        }
        
        if(config.getString("database.type", "yaml").equalsIgnoreCase("mysql")) {
            if(!Database.checkConnection()) {
                this.getLogger().warning("ATTENTION!!!! CAN'T CONNECT TO MYSQL SERVER !!!! Using Yaml!!!!!");
            }
        }
        
        //Load SignManager
        _signManager = new SignManager();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new McJobsNotify(), _notify, _notify);
    }
    
    private void loadJobs() {
        File jobsFolder = new File(getDataFolder(), "jobs");
        if(!jobsFolder.exists()) {
            jobsFolder.mkdirs();
            for(String path:  ResourceList.getResources("jobs")) {
                String name = path;
                int pos = name.lastIndexOf("/");
                if(pos > 0)
                    name = name.substring(pos+1);

                try {
                    InputStream in = getResource(path);
                    String msg = "";
                    int c = -1;
                    while((c = in.read()) != -1)
                        msg += String.valueOf((char)c);

                    File spLang = new File(jobsFolder, name);

                    FileConfiguration fcLang = YamlConfiguration.loadConfiguration(spLang);
                    fcLang.loadFromString(msg);
                    fcLang.save(spLang);

                    getLogger().log(Level.INFO, "Default Job {0} has been loaded and saved.", name);
                } catch (IOException | InvalidConfigurationException ex) {
                    getLogger().log(Level.SEVERE, "Error on loading default Job " + name, ex);
                }
            }
        }
        
        jobsFolder = new File(getDataFolder(), "jobs");
        String joblist = "";
        for(File jobFile: jobsFolder.listFiles()) {
            String name = jobFile.getName();
            int pos = name.lastIndexOf(".");
            if(pos > 0)
                name = name.substring(0, pos).toLowerCase();
            
            FileConfiguration jobConfig = YamlConfiguration.loadConfiguration(jobFile);
            PlayerJobs job = new PlayerJobs();
            try {
                job.getData().loadJob().setName(name);
                job.getData().loadJob().setupJob(jobConfig);
        
                PlayerJobs.getJobsList().put(name, job);
                joblist += name + " ";
            } catch(Exception e) {
                getLogger().log(Level.INFO, "Error inside /jobs/{0}.yml!  Job {0} failed to load properly!", name);
            }
        }
        getLogger().log(Level.INFO, "LOADED JOBS: {0}", joblist);
    }
    
    private void loadEconomyBridges(){
        Plugin pVault = getServer().getPluginManager().getPlugin("Vault");
        
        String bridge = this.getConfig().getString("advanced.payment_mod");
        String type = this.getConfig().getString("advanced.payment_type");
        
        if(bridge == null) {
            getLogger().info("Bridge value is null.  Setting to none.");
            bridge = "none";
        }
        
        if(type == null) {
            getLogger().info("Type value is null.  Setting to xp.");
            type = "xp";
        }
        
        if(pVault != null){
            PlayerUtils.setVault(true);
        }

        if(type.equalsIgnoreCase("money") || type.equalsIgnoreCase("both")) {
            if(bridge.equalsIgnoreCase("vault") && pVault != null){
                getLogger().log(Level.INFO, "Found {0} {1}", new Object[]{pVault.getName(), pVault.getDescription().getVersion()});
                _economy = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
                McJobsComp.setVault(true);
            } else if(pVault != null) {
                getLogger().log(Level.INFO, "Preferred bridge not found.  Using  {0} {1}", new Object[]{pVault.getName(), pVault.getDescription().getVersion()});
                McJobsComp.setVault(true);
            } else {
                getLogger().info("No economy bridge found!  Going to XP economy.");
                McJobsComp.setXP(true);
            }
            
            if(type.equalsIgnoreCase("both")) {
                McJobsComp.setXP(true);
            }
        } else {
            getLogger().info("Using XP economy.");
            McJobsComp.setXP(true);
        }
    }
    
    public static int hasYAMLFiles(File[] files) {
        int i = 0;
        for(File f: files) {
            if(f.getName().endsWith(".yml"))
                i++;
        }
        return i;
    }
    
    public static McJobs getPlugin() {
        return _mcJobs;
    }

    public static Economy getEconomy() {
        return _economy;
    }
  
    public static WorldGuardPlugin getWorldGuard() {
        return _wgp;
    }
        
    public GetLanguage getLanguage() {
        return _language;
    }

    public BlockLoggers getBlockLogging() {
        return _blocklogger;
    }
    
    public SignManager getSignManager() {
        return _signManager;
    }

    public boolean isPrune() {
        return _bPrune;
    }

    public Integer getVersion(){
        return _version;
    }
    
    public Holder getHolder() {
        return _holder;
    }
    
    public void reloadLanguages() {
        _language = new GetLanguage();
        if(_localization.isEmpty() || !_language.getLanguages().containsKey(_localization)) {
            getLogger().log(Level.INFO, "Cant find default language in config.yml!! Stop Plugin.");
        }
    }
    
    public void loadClasses() {
        _language = new GetLanguage();
        _blocklogger = new BlockLoggers();
        _holder = new Holder();
    }
}