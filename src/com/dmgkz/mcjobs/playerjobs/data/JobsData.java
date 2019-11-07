package com.dmgkz.mcjobs.playerjobs.data;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import com.dmgkz.mcjobs.playerjobs.display.JobsDisplay;
import com.dmgkz.mcjobs.util.EnchantTypeAdv;
import com.dmgkz.mcjobs.util.PotionTypeAdv;
import com.dmgkz.mcjobs.util.RegionPositions;
import com.dmgkz.mcjobs.util.Utils;
import java.util.List;
import java.util.UUID;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;


public class JobsData{
    private final CompData _compare;
    private final LoadJob _loadjob;
    private final JobsDisplay _display;
    
    private final HashMap<String, HashMap<Integer, List<Material>>> _hBlocksBPC = new HashMap<>();
    private final HashMap<String, HashMap<Integer, List<EntityType>>> _hBlocksK = new HashMap<>();
    private final HashMap<String, HashMap<Integer, List<PotionTypeAdv>>> _hPotions = new HashMap<>();
    private final HashMap<String, HashMap<Integer, List<EnchantTypeAdv>>> _hEnchants = new HashMap<>();
    private final HashMap<String, HashMap<Integer, List<DyeColor>>> _hColors = new HashMap<>();
    private final HashMap<String, HashMap<Integer, Integer>> _hPvPs = new HashMap<>();
    private long _hPvPInterval = Long.MAX_VALUE;
    private final HashMap<String, Boolean> _bTierPays = new HashMap<>();
    
    //Type like Break, Place, Craft... , ArrayList<Tool like pickaxt, sword....>
    private final HashMap<String, ArrayList<String>> _tools = new HashMap<>();
    
    protected boolean[] _bShow;
    protected boolean[] _bCP;
    protected boolean   _bShowEveryTime;
    protected boolean   _bDefaultJob;
    protected boolean   _bScoreboardHide;
    
    protected String _sJobName;
    
    protected double _dBasepay;
    protected double _exp_modifier;
    
    protected RegionPositions _jobInfoZone = null;
    protected HashMap<String, String> _jobInfoZoneMessage = new HashMap<>();
    protected HashMap<String, ConfigurationSection> _jobInfoZoneComponents = new HashMap<>();
    protected HashMap<String, String> _entityMessage = new HashMap<>();
    protected HashMap<String, ConfigurationSection> _entityComponents = new HashMap<>();

    public JobsData() {
        _compare = new CompData(this);
        _loadjob = new LoadJob(this);
        _display = new JobsDisplay(this);
        
        _bShow = new boolean[10];
        _bCP   = new boolean[2];
        Arrays.fill(_bShow, false);
        Arrays.fill(_bCP, false);
        
        _bShowEveryTime = false;
        _bDefaultJob    = false;
        _bScoreboardHide = false;
        
        _sJobName     = "no_name";
        
        _dBasepay = 0.25D;
        _exp_modifier = 1D;
    }

    public boolean getShowEveryTime() {
        return _bShowEveryTime;
    }
    
    public Double getBasePay() {
        return _dBasepay;
    }
    
    public double getEXP() {
        return _exp_modifier;
    }
    
    public LoadJob loadJob() {
        return _loadjob;
    }
    
    public CompData compJob() {
        return _compare;
    }
        
    public JobsDisplay display() {
        return _display;
    }
    
    public HashMap<String, HashMap<Integer, List<Material>>> getMatHash() {
        return _hBlocksBPC;
    }

    public HashMap<String, HashMap<Integer, List<EntityType>>> getEntHash() {
        return _hBlocksK;
    }
    
    public HashMap<String, HashMap<Integer, List<PotionTypeAdv>>> getPotHash() {
        return _hPotions;
    }
    
    public HashMap<String, HashMap<Integer, List<EnchantTypeAdv>>> getEnchantHash() {
        return _hEnchants;
    }
    
    public HashMap<String, HashMap<Integer, List<DyeColor>>> getColorHash() {
        return _hColors;
    }
    
    public HashMap<String, HashMap<Integer, Integer>> getPvPHash() {
        return _hPvPs;
    }
    
    public void setPvPInterval(long i) {
        _hPvPInterval = i;
    }
    
    public long getPvPInterval() {
        return _hPvPInterval;
    }
    
    public HashMap<String, ArrayList<String>> getTools() {
        return _tools;
    }
    
    public String getName() {
        return _sJobName;
    }
    
    public String getName(UUID uuid) {
        return McJobs.getPlugin().getLanguage().getJobName(_sJobName, uuid);
    }
    
    public String getDesc(UUID uuid) {
        return McJobs.getPlugin().getLanguage().getJobDesc(_sJobName, uuid);
    }
    
    public boolean getCostPay(boolean isPay) {
        if(isPay)
            return _bCP[0];
        else
            return _bCP[1];
    }
    
    public boolean getShow(int i) {
        return _bShow[i];
    }
    
    public boolean getShow(String block) {
        if(block.equalsIgnoreCase("break"))
            return _bShow[0];
        else if(block.equalsIgnoreCase("place"))
            return _bShow[1];
        else if(block.equalsIgnoreCase("defeat"))
            return _bShow[2];
        else if(block.equalsIgnoreCase("craft"))
            return _bShow[3];
        else if(block.equalsIgnoreCase("repair"))
            return _bShow[4];
        else if(block.equalsIgnoreCase("fishing"))
            return _bShow[5];
        else if(block.equalsIgnoreCase("enchant"))
            return _bShow[6];
        else if(block.equalsIgnoreCase("potion"))
            return _bShow[7];
        else if(block.equalsIgnoreCase("shear"))
            return _bShow[8];
        else if(block.equalsIgnoreCase("pvp"))
            return _bShow[9];
        return false;
    }

    public HashMap<String, Boolean> getTierPays() {
        return _bTierPays;
    }
    
    public boolean hideInScoreboard() {
        return _bScoreboardHide;
    }
    
    public RegionPositions getRegionPositions() {
        return _jobInfoZone;
    }
    
    public void setRegionPosition(Location loc) {
        if(_jobInfoZone == null)
            _jobInfoZone = new RegionPositions();
        
        _jobInfoZone.setPosition(loc);
    }
    
    public void sendRegionMessage(Player p) {
        String lang = PlayerData.getLang(p.getUniqueId());
        HashMap<String, String> replaces = new HashMap<>();
        replaces.put("%j", getName(p.getUniqueId()));
        replaces.put("%p", p.getName());
        replaces.put("%g", "");
        
        if(_jobInfoZoneComponents.containsKey(lang)) {
            if(Utils.sendMessage(p, _jobInfoZoneComponents.get(lang), replaces))
                return;
        } else if(_jobInfoZoneComponents.containsKey("default")) {
            if(Utils.sendMessage(p, _jobInfoZoneComponents.get("default"), replaces))
                return;
        }
        
        if(!_jobInfoZoneMessage.containsKey(lang))
            lang = "default";
        
        String[] tmpMsg = new String[0];
        if(_jobInfoZoneMessage.containsKey(lang))
            tmpMsg = _jobInfoZoneMessage.get(lang).split("|");

        for(String msg: tmpMsg)
            p.sendMessage(Utils.ReplaceAll(msg, replaces));
        
    }
    
    public void sendEntityMessage(Player p) {
        String lang = PlayerData.getLang(p.getUniqueId());
        HashMap<String, String> replaces = new HashMap<>();
        replaces.put("%j", getName(p.getUniqueId()));
        replaces.put("%p", p.getName());
        replaces.put("%g", "");
        
        if(_entityComponents.containsKey(lang)) {
            if(Utils.sendMessage(p, _entityComponents.get(lang), replaces))
                return;
        } else if(_entityComponents.containsKey("default")) {
            if(Utils.sendMessage(p, _entityComponents.get("default"), replaces))
                return;
        }
        
        if(!_entityMessage.containsKey(lang))
            lang = "default";
        
        String[] tmpMsg = new String[0];
        if(_entityMessage.containsKey(lang))
            tmpMsg = _entityMessage.get(lang).split("|");

        for(String msg: tmpMsg)
            p.sendMessage(Utils.ReplaceAll(msg, replaces));
    }
    
    public void setRegionMessage(String lang, ConfigurationSection cs) {
        if(_jobInfoZoneComponents.containsKey(lang))
            _jobInfoZoneComponents.remove(lang);
        _jobInfoZoneComponents.put(lang, cs);
    }
    
    public void removeRegionMessage(String lang) {
        _jobInfoZoneComponents.remove(lang);
    }
    
    public void setEntityMessage(String lang, ConfigurationSection cs) {
        if(_entityComponents.containsKey(lang))
            _entityComponents.remove(lang);
        _entityComponents.put(lang, cs);
    }
    
    public void removeEntityMessage(String lang) {
        _entityComponents.remove(lang);
    }
}
