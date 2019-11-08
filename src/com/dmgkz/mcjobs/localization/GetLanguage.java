package com.dmgkz.mcjobs.localization;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.hooks.HookPlaceHolderAPI;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.prettytext.AddTextVariables;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import com.dmgkz.mcjobs.util.ResourceList;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public final class GetLanguage {
    private static boolean _isPlaceHolderAPI = false;
    private static boolean _isSpigot = false;
    private static boolean _isWorldEdit = false;
    private final HashMap<String, FileConfiguration> _languages = new HashMap<>();
    
    private List<String> _avaLangs = new ArrayList<>();
    private String _defaultLang = "";

    public GetLanguage() {
        try {
            if(Bukkit.getVersion().toLowerCase().contains("spigot"))
                _isSpigot = true;
            
            if(Bukkit.getPluginManager().isPluginEnabled("WorldEdit"))
                _isWorldEdit = true;
            
            _isPlaceHolderAPI = McJobs.isPlaceholderAPI();
            loadLanguage();
        } catch (InvalidConfigurationException ex) {
            McJobs.getPlugin().getLogger().log(Level.WARNING, "Error on loading language.", ex);
        }
    }
    
    public Boolean isLang(String l) {
        return _languages.containsKey(l);
    }
    
    public HashMap<String, FileConfiguration> getLanguages() {
        return _languages;
    }
  
    public List<String> getAvaLangs() {
        if(_avaLangs.isEmpty())
            _avaLangs.addAll(_languages.keySet());
        return _avaLangs;
    }
    
    public void setDefaultLang(String lang) {
        _defaultLang = lang;
    }
    
    public String getDefaultLang(String lang) {
        if(_languages.containsKey(lang))
            return lang;
        return _defaultLang;
    }
    
    private FileConfiguration getLangFile(String lang) {
        if(_languages.containsKey(lang))
            return _languages.get(lang);
        
        if(_languages.containsKey(_defaultLang))
            return _languages.get(_defaultLang);

        return null;
    }
    
    public String getEntity(String n, UUID uuid) {
        return checkForPlaceholderAPI(uuid, getSection("entities", n, uuid));
    }
    
    public String getMaterial(String n, UUID uuid) {
        return checkForPlaceholderAPI(uuid, getSection("materials", n, uuid));
    }
  
    public String getPotion(String n, UUID uuid) {
        return checkForPlaceholderAPI(uuid, getSection("potion", n, uuid));
    }
  
    public String getEnchant(String n, UUID uuid) {
        return checkForPlaceholderAPI(uuid, getSection("enchant", n, uuid));
    }
    
    public String getColor(String n, UUID uuid) {
        return checkForPlaceholderAPI(uuid, getSection("color", n, uuid));
    }
    
    public String getScoreboard(String n, UUID uuid) {
        return checkForPlaceholderAPI(uuid, getSection("scoreboard", n, uuid));
    }
    
    public String getPlaceholderAPI(String n, UUID uuid) {
        return checkForPlaceholderAPI(uuid, getSection("hooks.placeholderapi", n, uuid));
    }
  
    public AddTextVariables getJobCommand(String subSection, UUID uuid) {
        return getATVSection("jobscommand", subSection, uuid);
    }
  
    public AddTextVariables getJobDisplay(String subSection, UUID uuid) {
        return getATVSection("jobsdisplay", subSection, uuid);
    }
  
    public AddTextVariables getJobNotify(String subSection, UUID uuid) {
        return getATVSection("jobsnotify", subSection, uuid);
    }
  
    public AddTextVariables getJobJoin(String subSection, UUID uuid) {
        return getATVSection("jobsjoin", subSection, uuid);
    }
  
    public AddTextVariables getJobLeave(String subSection, UUID uuid) {
        return getATVSection("jobsleave", subSection, uuid);
    }
  
    public AddTextVariables getJobList(String subSection, UUID uuid) {
        return getATVSection("jobslist", subSection, uuid);
    }
  
    public AddTextVariables getJobHelp(String subSection, UUID uuid) {
        return getATVSection("jobshelp", subSection, uuid);
    }
  
    public AddTextVariables getAdminCommand(String subSection, UUID uuid) {
        return getATVSection("admincommand", subSection, uuid);
    }
  
    public AddTextVariables getAdminAdd(String subSection, UUID uuid) {
        return getATVSection("adminadd", subSection, uuid);
    }
  
    public AddTextVariables getAdminRemove(String subSection, UUID uuid) {
        return getATVSection("adminremove", subSection, uuid);
    }
  
    public AddTextVariables getAdminList(String subSection, UUID uuid) {
        return getATVSection("adminlist", subSection, uuid);
    }
    
    public AddTextVariables getAdminRegion(String subSection, UUID uuid) {
        return getATVSection("adminregion", subSection, uuid);
    }
    
    public AddTextVariables getAdminEntity(String subSection, UUID uuid) {
        return getATVSection("adminentity", subSection, uuid);
    }
  
    public AddTextVariables getAdminLogin(String subSection, UUID uuid) {
        return getATVSection("onadminlogin", subSection, uuid);
    }
  
    public AddTextVariables getPitch(String subSection, UUID uuid) {
        return getATVSection("pitch", subSection, uuid);
    }
    
    public AddTextVariables getPayment(String subSection, UUID uuid) {
        return getATVSection("payment", subSection, uuid);
    }
  
    public AddTextVariables getExperience(String subSection, UUID uuid) {
        return getATVSection("experience", subSection, uuid);
    }
  
    public Integer getSpaces(String subSection, UUID uuid) {
        return getIntegerSection("spaces", subSection, uuid);
    }
    
    public String getJobName(String jobname, UUID uuid) {
        return getSection("jobs.name", jobname, uuid);
    }
    
    public String getOriginalJobName(String jobname, UUID uuid) {
        String lang = PlayerData.getLang(uuid);
        if(!getLangFile(lang).isConfigurationSection("jobs.name"))
            return jobname;
        
        for(String k: getLangFile(lang).getConfigurationSection("jobs.name").getKeys(false)) {
            if(getLangFile(lang).isString("jobs.name." + k) && getLangFile(lang).getString("jobs.name." + k).equalsIgnoreCase(jobname.toLowerCase()))
                return k;
        }
        return jobname;
    }
    
    public String getLanguageName(String name, UUID uuid) {
        return getSection("languages", name, uuid);
    }
    
    public String getOriginalLanguageName(String langname, UUID uuid) {
        String lang = PlayerData.getLang(uuid);
        if(!getLangFile(lang).isConfigurationSection("languages"))
            return langname;
        
        for(String k: getLangFile(lang).getConfigurationSection("languages").getKeys(false)) {
            if(getLangFile(lang).isString("languages." + k) && getLangFile(lang).getString("languages." + k).equalsIgnoreCase(langname.toLowerCase()))
                return k;
        }
        return langname;
    } 
    
    public String getJobRank(String subSection, UUID uuid) {
        return getSection("jobs.rank", subSection, uuid);
    }
    
    public String getJobDesc(String subSection, UUID uuid) {
        return getSection("jobs.description", subSection, uuid);
    }
    
    private Integer getIntegerSection(String s, String n, UUID uuid) {
        String str;
        String lang = PlayerData.getLang(uuid);
        if(!getLangFile(lang).isConfigurationSection(s))
            return 0;
        ConfigurationSection section = getLangFile(lang).getConfigurationSection(s);
        str = getValue(section, n);
        
        try {
            return Integer.parseInt(str);
        } catch(Exception ex) {
            return 0;
        }
    }
        
    private String getSection(String s, String n, UUID uuid) {
        String lang = PlayerData.getLang(uuid);
        if(!getLangFile(lang).isConfigurationSection(s))
            return "Unknown Section: " + n;
        ConfigurationSection section = getLangFile(lang).getConfigurationSection(s);
        return getValue(section, n);
    }
    
    private AddTextVariables getATVSection(String s, String n, UUID uuid) {
        String lang = PlayerData.getLang(uuid);
        if(!getLangFile(lang).isConfigurationSection(s))
            return new AddTextVariables("Unknow Section: " + s  + " in " + lang);
        ConfigurationSection section = getLangFile(lang).getConfigurationSection(s);
        return new AddTextVariables(checkForPlaceholderAPI(uuid, getSubString(section, n)));
    }
    
    private String getValue(ConfigurationSection cs, String ss) {
        String name = cs.getString(ss.toLowerCase(), "Unknown Value: " + ss);
        return PrettyText.colorText(name);
    }
    
    private String getSubString(ConfigurationSection cs, String ss) {
        String s = cs.getString(ss.toLowerCase(), "&cUnknown String: " + ss);
        return PrettyText.colorText(s);
    }

    public void loadLanguage() throws InvalidConfigurationException {
        if(!McJobs.getPlugin().getDataFolder().exists())
            McJobs.getPlugin().getDataFolder().mkdir();
        
        File lFold = new File(McJobs.getPlugin().getDataFolder(), "languages");
        if(!lFold.exists())
            lFold.mkdir();
        
        if(McJobs.hasYAMLFiles(lFold.listFiles()) == 0) {
            loadDefaultFiles("languages", lFold);
        }
        
        for(File lang: lFold.listFiles()) {
            if(!lang.getName().endsWith(".yml"))
                continue;
                
            String name = lang.getName();
            int pos = name.lastIndexOf(".");
            if (pos > 0)
                name = name.substring(0, pos);
                        
            FileConfiguration fcLang = YamlConfiguration.loadConfiguration(lang);
            _languages.put(name, fcLang);
            McJobs.getPlugin().getLogger().log(Level.INFO, "Language {0} has been loaded.", name);
        }
        
        //loadSpigotMessages();
    }
    
    private void loadDefaultFiles(String resPath, File safeDir) {
        for(String srcFile: ResourceList.getResources(resPath)) {
            String name = "";
            try {
                //Load text from Resource File in resPath
                InputStream in = McJobs.getPlugin().getResource(srcFile);
                String msg = "";
                int c = -1;
                while((c = in.read()) != -1)
                    msg += String.valueOf((char)c);
                
                //Create File to write text from Resouce file.
                File spLang = new File(safeDir, new File(srcFile).getName());
                    
                //Create language name from Filname
                name = spLang.getName();
                int pos = name.lastIndexOf(".");
                if (pos > 0)
                    name = name.substring(0, pos);
                
                //Let us save the text local now.
                FileConfiguration fcLang = YamlConfiguration.loadConfiguration(spLang);
                fcLang.loadFromString(msg);
                fcLang.save(spLang);
                McJobs.getPlugin().getLogger().log(Level.INFO, "Loaded {0} files from resource and save it local.", name);
            } catch (IOException | InvalidConfigurationException ex) {
                McJobs.getPlugin().getLogger().log(Level.SEVERE, "Error on loading default file " + name, ex);
            }
        }
    }
    
    public static void sendMessage(Player p, String path, String def, HashMap<String, String> sr) {
        String lang = PlayerData.getLang(p.getUniqueId());
        FileConfiguration fc = McJobs.getPlugin().getLanguage().getLangFile(lang);
        if(fc == null) {
            p.sendMessage(replaceAll(def, sr));
            return;
        }
        
        if(fc.isString(path)) {
            p.sendMessage(replaceAll(fc.getString(path), sr));
            return;
        } else if(fc.isConfigurationSection(path)) {
            ConfigurationSection cs = fc.getConfigurationSection(path);
            if(_isSpigot) {
                SpigotBuilds.sendMessage(p, cs, sr);
                return;
            } else if(_isWorldEdit) {
                WorldEditBuilds.sendMessage(p, cs, sr);
                return;
            }
            
            String msg = "";
            List<Integer> keys = new ArrayList<>();
            for(String strKey: cs.getKeys(false)) {
                try {
                    keys.add(Integer.parseInt(strKey));
                } catch(Exception ex) {
                    McJobs.getPlugin().getLogger().warning("Please use only integer for Spigot/WorldEdit and Multi Line Messages.");
                }
            }
            Collections.sort(keys);

            for(int ik: keys) {
                String k = String.valueOf(ik);
                if(cs.isString(k)) {
                    msg += cs.getString(k);
                } else if(cs.isString(k + ".message")) {
                    msg += cs.getString(k + ".message");
                    
                    if(cs.getBoolean(k + ".break", false)) {
                        p.sendMessage(replaceAll(msg, sr));
                        msg = "";
                    }
                }
            }
            
            if(!msg.isEmpty()) {
                p.sendMessage(replaceAll(msg, sr));
            }
        } else {
            p.sendMessage(replaceAll(def, sr));
        }
    }
    
    private static String replaceAll(String str, HashMap<String, String> sr) {
        if(sr == null) 
            return ChatColor.translateAlternateColorCodes('&', str);
        
        for(Map.Entry<String, String> me: sr.entrySet()) {
            str = str.replace(me.getKey(), me.getValue());
        }
        return ChatColor.translateAlternateColorCodes('&', str);
    }
    
    private String checkForPlaceholderAPI(UUID uuid, String str) {
        if(_isPlaceHolderAPI)
            return HookPlaceHolderAPI.checkPlaceholders(str, uuid);
        return str;
    }
}