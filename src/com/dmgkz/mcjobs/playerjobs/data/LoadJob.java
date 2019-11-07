package com.dmgkz.mcjobs.playerjobs.data;

import com.dmgkz.mcjobs.McJobs;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import com.dmgkz.mcjobs.util.EnchantTypeAdv;
import com.dmgkz.mcjobs.util.PotionTypeAdv;
import com.dmgkz.mcjobs.util.StringToNumber;
import com.dmgkz.mcjobs.util.RegionPositions;
import com.dmgkz.mcjobs.util.Utils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.DyeColor;

public class LoadJob {
    private final JobsData _jobsdata;
    
    public LoadJob(JobsData jobsdata){
        _jobsdata = jobsdata;
    }

    /*
    * basepay: '0.0'
    * show_every_time: false
    * exp: '0.0'
    * default: false
    * (break|place)|(craft|repair)|(defeat|pvp)|fishing|shear|potion|enchant:
    *     pays: false
    *     hide: false
    *     need-tool: diamond_axe wooden_axe
    *     1 - X: Types
    *     job-info-zone:
    *         region:
    *             world: 'worldname'
    *             pos1:
    *                 x: '0.0'
    *                 y: '0.0'
    *                 z: '0.0'
    '             pos2:
    *                 x: '0.0'
    *                 y: '0.0'
    *                 z: '0.0'
    *         message: 'My message Line 1|Line 2|Line 3|....|...'
    *         spigot-message:
    *             l1:
    *                 message: 'Hallo bitte '
    *                 break: false
    *             l2:
    *                 message: '[Klick mich]'
    *                 hover-type: 'text'
    *                 hover-msg: 'Bitte hier klicken'
    *                 click-type: 'run_command'
    *                 click-msg: '/jobs join kumpel'
    *                 break: false
    *             l3:
    *                 message: ' hart.'
    *                 break: true
    *             l4:
    *                 message: 'Das ist Line 2'
    *     entity-sign:
    *         message: 'My message Line 1|Line 2|Line 3|....|...'
    *         spigot-message:
    *             l1:
    *                 message: 'Hallo bitte '
    *                 break: false
    *             l2:
    *                 message: '[Klick mich]'
    *                 hover-type: 'text'
    *                 hover-msg: 'Bitte hier klicken'
    *                 click-type: 'run_command'
    *                 click-msg: '/jobs join kumpel'
    *                 break: false
    *             l3:
    *                 message: ' hart.'
    *                 break: true
    *             l4:
    *                 message: 'Das ist Line 2'
    */
    public void setupJob(ConfigurationSection section) {
        for(String key: section.getKeys(false)) {
            if(key.equalsIgnoreCase("basepay")){
                setBasePay(section.getDouble(key));
                continue;
            }
            
            if(key.equalsIgnoreCase("show_every_time")){
                setShowEveryTime(section.getBoolean(key));
                continue;
            }
            
            if(key.equalsIgnoreCase("hide_in_scoreboard")){
                setHideInScoreBoard(section.getBoolean(key));
                continue;
            }
            
            if(key.equalsIgnoreCase("exp")){
                setEXP(section.getDouble(key));
                continue;
            }
            
            if(key.equalsIgnoreCase("default")){
                setDefault(section.getBoolean(key));
                continue;
            }
            
            if(key.equalsIgnoreCase("job-info-zone")) {
                for(String key3: section.getConfigurationSection(key).getKeys(false)) {
                    if(key3.equalsIgnoreCase("region") && section.isConfigurationSection(key + ".region"))
                        _jobsdata._jobInfoZone = RegionPositions.getRP(section.getConfigurationSection(key + ".region"));
                    else if(key3.equalsIgnoreCase("component"))
                        _jobsdata._jobInfoZoneComponents.put("default", section.getConfigurationSection(key + "." + key3));
                    else if(key3.equalsIgnoreCase("message")) {
                        for(String key4: section.getConfigurationSection(key + "." + key3).getKeys(false)) {
                            if(McJobs.getPlugin().getLanguage().getAvaLangs().contains(key4) || key4.equalsIgnoreCase("default")) {
                                _jobsdata._jobInfoZoneMessage.put(key4, section.getString(key + "." + key3 + "." + key4));
                            }
                        }
                    } else if(McJobs.getPlugin().getLanguage().getAvaLangs().contains(key3))
                        _jobsdata._jobInfoZoneComponents.put(key3, section.getConfigurationSection(key + "." + key3 + ".component"));
                }
                continue;
            } 
            
            if(key.equalsIgnoreCase("entity-sign")) {
                for(String key3: section.getConfigurationSection(key).getKeys(false)) {
                    if(key3.equalsIgnoreCase("component"))
                        _jobsdata._entityComponents.put("default", section.getConfigurationSection(key + "." + key3));
                    else if(key3.equalsIgnoreCase("message")) {
                        for(String key4: section.getConfigurationSection(key + "." + key3).getKeys(false)) {
                            if(McJobs.getPlugin().getLanguage().getAvaLangs().contains(key4) || key4.equalsIgnoreCase("default")) {
                                _jobsdata._entityMessage.put(key4, section.getString(key + "." + key3 + "." + key4));
                            }
                        }
                    } else if(McJobs.getPlugin().getLanguage().getAvaLangs().contains(key3))
                        _jobsdata._entityComponents.put(key3, section.getConfigurationSection(key + "." + key3 + ".component"));
                }
                continue;
            }
            
            if(section.isConfigurationSection(key)) {
                for(String key2: section.getConfigurationSection(key).getKeys(false)) {
                    if(key2.equalsIgnoreCase("pays")) {
                        setCostPay(section.getBoolean(key + "." + key2));
                        setTierPays(key, section.getBoolean(key + "." + key2)); 
                    } else if(key2.equalsIgnoreCase("hide")) {
                        setHide(key, section.getBoolean(key + "." + key2));
                    } else if(key2.equalsIgnoreCase("need-tool")) {
                        for(String s: section.getString(key + "." + key2).split(" "))
                            setTools(key, s);
                    } else if(key.equalsIgnoreCase("pvp") && key2.equalsIgnoreCase("kill-interval")) {
                        _jobsdata.setPvPInterval(section.getLong(key + "." + key2, 30));
                    } else if(StringToNumber.isPositiveNumber(key2)) {
                        int tier = Integer.parseInt(key2);
                        if(key.equalsIgnoreCase("break") || key.equalsIgnoreCase("place") || key.equalsIgnoreCase("craft") || key.equalsIgnoreCase("repair") || key.equalsIgnoreCase("fishing")) {
                            McJobs.getPlugin().getHolder().getJobsHolder().addJob(key, _jobsdata.getName());
                            List<Material> temp = new ArrayList<>();
                            List<String> matList = new ArrayList<>();
                            if(section.isList(key + "." + key2)) {
                                matList.addAll((List<String>)section.getList(key + "." + key2));
                            } else {
                                matList.addAll(Arrays.asList(section.getString(key + "." + key2).split(" ")));
                            }
                            
                            for(String s: matList) {
                                if(!Utils.isMaterial(s)) {
                                    McJobs.getPlugin().getLogger().warning("Can't found Material " + s + " on " + key + " in Job " + _jobsdata.getName());
                                    continue;
                                }
                                
                                try {
                                    Material tmpMat = Material.valueOf(s.toUpperCase());
                                    if(tmpMat == null) {
                                        McJobs.getPlugin().getLogger().warning("Material " + s + " on " + key + " in Job " + _jobsdata.getName() + " is null!!!!!");
                                        continue;
                                    }

                                    if(!temp.add(tmpMat)) {
                                        McJobs.getPlugin().getLogger().warning("Duplicate Material " + s + " on " + key + " in Job " + _jobsdata.getName());
                                        continue;
                                    }
                                    tmpMat = null;
                                } catch(Exception ex) {
                                    McJobs.getPlugin().getLogger().log(Level.WARNING, "Exception in JobLoad Material Type: ", ex);
                                }
                                s = "";
                            }
                            setMat(key, tier, temp);
                        } else if(key.equalsIgnoreCase("defeat")) {
                            McJobs.getPlugin().getHolder().getJobsHolder().addJob(key, _jobsdata.getName());
                            List<EntityType> temp = new ArrayList<>();
                            List<String> entList = new ArrayList<>();
                            if(section.isList(key + "." + key2)) {
                                entList.addAll((List<String>)section.getList(key + "." + key2));
                            } else {
                                entList.addAll(Arrays.asList(section.getString(key + "." + key2).split(" ")));
                            }
                            
                            for(String s: entList) {
                                if(!Utils.isEntity(s)) {
                                    McJobs.getPlugin().getLogger().warning("Can't found EntityType " + s + " on " + key + " in Job " + _jobsdata.getName());
                                    continue;
                                }
                                
                                EntityType entTmp = EntityType.valueOf(s.toUpperCase());
                                if(!temp.add(entTmp)) {
                                    McJobs.getPlugin().getLogger().warning("Duplicate EntityType " + s + " on " + key + " in Job " + _jobsdata.getName());
                                    continue;
                                }
                                
                                s = "";
                                entTmp = null;
                            }
                            setEntity(key, tier, temp);
                        } else if(key.equalsIgnoreCase("potion")) {
                            McJobs.getPlugin().getHolder().getJobsHolder().addJob(key, _jobsdata.getName());
                            List<PotionTypeAdv> temp = new ArrayList<>();
                            List<String> potList = new ArrayList<>();
                            if(section.isList(key + "." + key2)) {
                                potList.addAll((List<String>)section.getList(key + "." + key2));
                            } else {
                                potList.addAll(Arrays.asList(section.getString(key + "." + key2).split(" ")));
                            }
                            
                            for(String s: potList) {
                                if(!Utils.isPotionTypeAdv(s)) {
                                    McJobs.getPlugin().getLogger().warning("Can't found PotionTypeAdv " + s + " on " + key + " in Job " + _jobsdata.getName());
                                    continue;
                                }
                                
                                PotionTypeAdv potTmp = McJobs.getPlugin().getHolder().getPotions().getPotion(s);
                                if(!temp.add(potTmp)) {
                                    McJobs.getPlugin().getLogger().warning("Duplicate PotionTypeAdv " + s + " on " + key + " in Job " + _jobsdata.getName());
                                    continue;
                                }
                                
                                s = "";
                                potTmp = null;
                            }
                            setPotions(key, tier, temp);
                        } else if(key.equalsIgnoreCase("enchant")) {
                            McJobs.getPlugin().getHolder().getJobsHolder().addJob(key, _jobsdata.getName());
                            List<EnchantTypeAdv> temp = new ArrayList<>();
                            List<String> enchList = new ArrayList<>();
                            if(section.isList(key + "." + key2)) {
                                enchList.addAll((List<String>)section.getList(key + "." + key2));
                            } else {
                                enchList.addAll(Arrays.asList(section.getString(key + "." + key2).split(" ")));
                            }
                            
                            for(String s: enchList) {
                                if(!Utils.isEnchantTypeAdv(s)) {
                                    McJobs.getPlugin().getLogger().warning("Can't found Enchant " + s + " on " + key + " in Job " + _jobsdata.getName());
                                    continue;
                                }
                                
                                EnchantTypeAdv enchTmp = McJobs.getPlugin().getHolder().getEnchants().getEnchantAdv(s);
                                if(!temp.add(enchTmp)) {
                                    McJobs.getPlugin().getLogger().warning("Duplicate EnchantTypeAdv " + s + " on " + key + " in Job " + _jobsdata.getName());
                                    continue;
                                }
                                
                                s = "";
                                enchTmp = null;
                            }
                            setEnchants(key, tier, temp);
                        } else if(key.equalsIgnoreCase("shear")) {
                            McJobs.getPlugin().getHolder().getJobsHolder().addJob(key, _jobsdata.getName());
                            List<DyeColor> temp = new ArrayList<>();
                            List<String> colList = new ArrayList<>();
                            if(section.isList(key + "." + key2)) {
                                colList.addAll((List<String>)section.getList(key + "." + key2));
                            } else {
                                colList.addAll(Arrays.asList(section.getString(key + "." + key2).split(" ")));
                            }
                            
                            for(String s: colList) {
                                if(DyeColor.valueOf(s) == null) {
                                    McJobs.getPlugin().getLogger().warning("Can't found DyeColor " + s + " on " + key + " in Job " + _jobsdata.getName());
                                    continue;
                                }
                                
                                if(!temp.add(DyeColor.valueOf(s))) {
                                    McJobs.getPlugin().getLogger().warning("Duplicate DyeColor " + s + " on " + key + " in Job " + _jobsdata.getName());
                                    continue;
                                }
                                
                                s = "";
                            }
                            setDyeColors(key, tier, temp);
                        } else if(key.equalsIgnoreCase("pvp")) {
                            McJobs.getPlugin().getHolder().getJobsHolder().addJob(key, _jobsdata.getName());
                            if(section.isInt(key + "." + key2))
                                setPvPs(key, tier, section.getInt(key + "." + key2));
                        }
                    }
                }
            }
        }
    }

    public void setBasePay(double pay){
        _jobsdata._dBasepay = pay;
    }

    public void setEXP(double exp){
        _jobsdata._exp_modifier = exp;
    }
    
    private void setMat(String action, int key, List<Material> tier){
        if(!_jobsdata.getMatHash().containsKey(action))
            _jobsdata.getMatHash().put(action, new HashMap<>());
        _jobsdata.getMatHash().get(action).put(key, tier);
    }

    private void setEntity(String action, int key, List<EntityType> tier){
        if(!_jobsdata.getEntHash().containsKey(action))
            _jobsdata.getEntHash().put(action, new HashMap<>());
        _jobsdata.getEntHash().get(action).put(key, tier);
    }
    
    private void setPotions(String action, int key, List<PotionTypeAdv> tier){
        if(!_jobsdata.getPotHash().containsKey(action))
            _jobsdata.getPotHash().put(action, new HashMap<>());
        _jobsdata.getPotHash().get(action).put(key, tier);
    }
    
    private void setEnchants(String action, int key, List<EnchantTypeAdv> tier) {
        if(!_jobsdata.getEnchantHash().containsKey(action))
            _jobsdata.getEnchantHash().put(action, new HashMap<>());
        _jobsdata.getEnchantHash().get(action).put(key, tier);
    }
    
    private void setDyeColors(String action, int key, List<DyeColor> tier) {
        if(!_jobsdata.getColorHash().containsKey(action))
            _jobsdata.getColorHash().put(action, new HashMap<>());
        _jobsdata.getColorHash().get(action).put(key, tier);
    }
    
    private void setPvPs(String action, int key, int tier) {
        if(!_jobsdata.getPvPHash().containsKey(action))
            _jobsdata.getPvPHash().put(action, new HashMap<>());
        _jobsdata.getPvPHash().get(action).put(key, tier);
    }
    
    private void setTierPays(String key, Boolean isPays) {
        _jobsdata.getTierPays().put(key, isPays);
    }
    
    private void setTools(String key, String tool) {
        if(!_jobsdata.getTools().containsKey(key))
            _jobsdata.getTools().put(key, new ArrayList());
        _jobsdata.getTools().get(key).add(tool);
    }
    
    public void setName(String name){
        _jobsdata._sJobName = name;
    }

    public void setCostPay(boolean isPays) {
        if(isPays)
            _jobsdata._bCP[0] = true;
        else
            _jobsdata._bCP[1] = true;
    }
    
    public void setShowEveryTime(boolean bShowEveryTime) {
        _jobsdata._bShowEveryTime = bShowEveryTime;
    }
    
    public void setDefault(boolean bDefaultJob) {
        _jobsdata._bDefaultJob = bDefaultJob;
    }
    
    public void setHideInScoreBoard(boolean bHide) {
        _jobsdata._bScoreboardHide = bHide;
    }
    
    public void setHide(String block, boolean isHide) {
        if(block.equalsIgnoreCase("break"))
            _jobsdata._bShow[0] = isHide;
        else if(block.equalsIgnoreCase("place"))
            _jobsdata._bShow[1] = isHide;
        else if(block.equalsIgnoreCase("defeat"))
            _jobsdata._bShow[2] = isHide;
        else if(block.equalsIgnoreCase("craft"))
            _jobsdata._bShow[3] = isHide;
        else if(block.equalsIgnoreCase("repair"))
            _jobsdata._bShow[4] = isHide;
        else if(block.equalsIgnoreCase("fishing"))
            _jobsdata._bShow[5] = isHide;
        else if(block.equalsIgnoreCase("enchant"))
            _jobsdata._bShow[6] = isHide;
        else if(block.equalsIgnoreCase("potion"))
            _jobsdata._bShow[7] = isHide;
        else if(block.equalsIgnoreCase("shear"))
            _jobsdata._bShow[8] = isHide;
        else if(block.equalsIgnoreCase("pvp"))
            _jobsdata._bShow[9] = isHide;
    }
}
