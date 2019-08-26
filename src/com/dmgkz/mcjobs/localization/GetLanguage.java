package com.dmgkz.mcjobs.localization;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.prettytext.AddTextVariables;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import com.dmgkz.mcjobs.util.ResourceList;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.mu1ti1ingu41.Mu1ti1ingu41;
import de.diddiz.util.Utils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class GetLanguage {
    private boolean _useMultilingual = false;
    private final HashMap<String, FileConfiguration> _languages = new HashMap<>();
    private String _avaLangs = "";
    private String _defaultLang = "";

    public GetLanguage() {
        try {
            _useMultilingual = McJobs.getPlugin().getServer().getPluginManager().isPluginEnabled("Mu1ti1ingu41");
            if(!_useMultilingual)
                loadLanguage();
            else
                loadMu1ti1ingu41DefaultFiles();
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
  
    public String getAvaLangs() {
        if(_avaLangs.isEmpty())
            fillAvaLangs();
        return _avaLangs;
    }
    
    public void setDefaultLang(String lang) {
        _defaultLang = lang;
    }
    
    public String getDefaultLang() {
        return _defaultLang;
    }
    
    public void fillAvaLangs() {
        _avaLangs = "";
        for(Map.Entry<String, FileConfiguration> e: _languages.entrySet())
            _avaLangs += _avaLangs.isEmpty()?"§e" + e.getKey():"§9, §e" + e.getKey();
    }
    
    private FileConfiguration getLangFile(String lang) {
        if(_languages.containsKey(lang))
            return _languages.get(lang);
        
        if(_languages.containsKey(_defaultLang))
            return _languages.get(_defaultLang);

        return null;
    }
    
    public String getEntity(String n, UUID uuid) {
        return getSection("entities", n, uuid);
    }
    
    public String getMaterial(String n, UUID uuid) {
        return getSection("materials", n, uuid);
    }
  
    public String getPotion(String n, UUID uuid) {
        return getSection("potions", n, uuid);
    }
  
    public String getEnchant(String n, UUID uuid) {
        return getSection("enchant", n, uuid);
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
        return getIntegerSection("spaces", "0", uuid);
    }
    
    public String getJobNameInLang(String subSection, UUID uuid) {
        return getSection("jobs.name", subSection, uuid);
    }
    
    public String getJobNameByLangName(String subSection, UUID uuid) {
        ConfigurationSection section = null;
        if(_useMultilingual) {
            FileConfiguration f = Language.getMessageFile(McJobs.getPlugin(), uuid);
            if(f.isConfigurationSection("jobs.reversename"))
                section = f.getConfigurationSection("jobs.reversename");
            else
                return "Unknown Section : jobs.reversename";
        } else {
            String lang = PlayerData.getLang(uuid);
            if(getLangFile(lang).isConfigurationSection("jobs.reversename"))
                section = getLangFile(lang).getConfigurationSection("jobs.reversename");
            else
                return "Unknown Section : jobs.reversename";
        }
        
        for(String key: section.getKeys(false)) {
            if(section.getString(key).equalsIgnoreCase(subSection))
                return key;
        }
        return "Unknown Value " + subSection + " in Section jobs.reversename";
    }
    
    public String getJobRank(String subSection, UUID uuid) {
        return getSection("jobs.rank", subSection, uuid);
    }
    
    public String getJobDesc(String subSection, UUID uuid) {
        return getSection("jobs.description", subSection, uuid);
    }
    
    private Integer getIntegerSection(String s, String n, UUID uuid) {
        if(_useMultilingual) {
            String str = Language.getMessage(McJobs.getPlugin(), uuid, s + "." + n, n);
            if(Utils.isInt(str))
                return Integer.parseInt(str);
            return Integer.parseInt(n);
        }
        
        String lang = PlayerData.getLang(uuid);
        if(!getLangFile(lang).isConfigurationSection(s))
            return Integer.parseInt(n);
        ConfigurationSection section = getLangFile(lang).getConfigurationSection(s);
        String str = getValue(section, n);
        if(Utils.isInt(str))
            return Integer.parseInt(str);
        return Integer.parseInt(n);
    }
        
    private String getSection(String s, String n, UUID uuid) {
        if(_useMultilingual)
            return Language.getMessage(McJobs.getPlugin(), uuid, s + "." + n, "Unknow Section: " + n);
        
        String lang = PlayerData.getLang(uuid);
        if(!getLangFile(lang).isConfigurationSection(s))
            return "Unknow Section: " + n;
        ConfigurationSection section = getLangFile(lang).getConfigurationSection(s);
        return getValue(section, n);
    }
    
    private AddTextVariables getATVSection(String s, String n, UUID uuid) {
        if(_useMultilingual)
            return new AddTextVariables(Language.getMessage(McJobs.getPlugin(), uuid, s + "." + n, "Unknow Section: " + n));
        
        String lang = PlayerData.getLang(uuid);
        if(!getLangFile(lang).isConfigurationSection(s))
            return new AddTextVariables("Unknow Section: " + s  + " in " + lang);
        ConfigurationSection section = getLangFile(lang).getConfigurationSection(s);
        return new AddTextVariables(getSubString(section, n));
    }

    public void loadLanguage() throws InvalidConfigurationException {
        if(!McJobs.getPlugin().getDataFolder().exists())
            McJobs.getPlugin().getDataFolder().mkdir();
        
        File lFold = new File(McJobs.getPlugin().getDataFolder(), "languages");
        if(!lFold.exists())
            lFold.mkdir();
        
        if(McJobs.hasYAMLFiles(lFold.listFiles()) == 0) {
            for(String srcFile: ResourceList.getResources("languages")) {
                String name = "";
                try {
                    InputStream in = McJobs.getPlugin().getResource(srcFile);
                    String msg = "";
                    int c = -1;
                    while((c = in.read()) != -1)
                        msg += String.valueOf((char)c);
                    
                    File spLang = new File(McJobs.getPlugin().getDataFolder(), srcFile);
                    
                    name = spLang.getName();
                    int pos = name.lastIndexOf(".");
                    if (pos > 0)
                        name = name.substring(0, pos);
                    
                    FileConfiguration fcLang = YamlConfiguration.loadConfiguration(spLang);
                    fcLang.loadFromString(msg);
                    fcLang.save(spLang);
                    
                    _languages.put(name, fcLang);
                    McJobs.getPlugin().getLogger().log(Level.INFO, "Language {0} has been loaded and saved.", name);
                } catch (IOException | InvalidConfigurationException ex) {
                    McJobs.getPlugin().getLogger().log(Level.SEVERE, "Error on loading default language " + name, ex);
                }
            }
        } else {
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
        }
    }
    
    private String getValue(ConfigurationSection cs, String ss) {
        String name = cs.getString(ss.toLowerCase(), "Unknown value: " + ss);
        return PrettyText.colorText(name);
    }
    
    private String getSubString(ConfigurationSection cs, String ss) {
        String s = cs.getString(ss.toLowerCase(), "&cUnknown String: " + ss);
        return PrettyText.colorText(s);
    }
    
    private void loadMu1ti1ingu41DefaultFiles() {
        Mu1ti1ingu41.loadExternalDefaultLanguage(McJobs.getPlugin(), "languages");
    }
}