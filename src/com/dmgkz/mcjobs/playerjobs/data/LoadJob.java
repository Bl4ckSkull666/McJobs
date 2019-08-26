package com.dmgkz.mcjobs.playerjobs.data;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import com.dmgkz.mcjobs.util.EnchantTypeAdv;
import com.dmgkz.mcjobs.util.PotionTypeAdv;
import com.dmgkz.mcjobs.util.StringToNumber;
import com.dmgkz.mcjobs.util.MatClass;
import com.dmgkz.mcjobs.util.RegionPositions;
import com.dmgkz.mcjobs.util.SpigotMessage;
import com.dmgkz.mcjobs.util.Utils;
import java.util.Arrays;
import java.util.HashMap;
import org.bukkit.Bukkit;

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
    public void setupJob(ConfigurationSection section){
        for(String key: section.getKeys(false)) {
            if(key.equalsIgnoreCase("basepay")){
                setBasePay(section.getDouble(key));
                continue;
            }
            
            if(key.equalsIgnoreCase("show_every_time")){
                setShowEveryTime(section.getBoolean(key));
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
                    } else if(StringToNumber.isPositiveNumber(key2)) {
                        int tier = Integer.parseInt(key2);
                        if(key.equalsIgnoreCase("break") || key.equalsIgnoreCase("place") || key.equalsIgnoreCase("craft") || key.equalsIgnoreCase("repair")) {
                            ArrayList<MatClass> temp = new ArrayList<>();
                            for(String s: section.getString(key + "." + key2).split(" ")) {
                                String[] mats = s.split(":");
                                if(!Utils.isMaterial(mats[0]))
                                    continue;
                                
                                MatClass mc = new MatClass(Material.valueOf(mats[0].toUpperCase()));
                                if(mats.length == 2 && StringToNumber.isPositiveNumber(mats[1]))
                                    mc.setWorth(Integer.parseInt(mats[1]));
                                temp.add(mc);
                            }
                            setMat(key, tier, temp);
                        } else if(key.equalsIgnoreCase("defeat") || key.equalsIgnoreCase("fishing")) {
                            ArrayList<EntityType> temp = new ArrayList<>();
                            for(String s: section.getString(key + "." + key2).split(" ")) {
                                if(!Utils.isEntity(s))
                                    continue;
                                temp.add(EntityType.valueOf(s.toUpperCase()));
                            }
                            setEntity(key, tier, temp);
                        } else if(key.equalsIgnoreCase("potion")) {
                            ArrayList<PotionTypeAdv> temp = new ArrayList<>();
                            for(String s: section.getString(key + "." + key2).split(" ")) {
                                if(!Utils.isPotionTypeAdv(s))
                                    continue;
                                temp.add(PotionTypeAdv._potions.get(s.toUpperCase()));
                            }
                            setPotions(key, tier, temp);
                        } else if(key.equalsIgnoreCase("enchant")) {
                            ArrayList<EnchantTypeAdv> temp = new ArrayList<>();
                            for(String s: section.getString(key + "." + key2).split(" ")) {
                                if(!Utils.isEnchantTypeAdv(s))
                                    continue;
                                temp.add(EnchantTypeAdv.getEnchantAdv(s.toUpperCase()));
                            }
                            setEnchants(key, tier, temp);
                        }
                    } else if(key.equalsIgnoreCase("job-info-zone")) {
                        if(section.isConfigurationSection(key + ".region"))
                            _jobsdata._jobInfoZone = RegionPositions.getRP(section.getConfigurationSection(key + ".region"));
                        else
                            _jobsdata._jobInfoZone = null;

                        _jobsdata._jobInfoZoneMessage.addAll(Arrays.asList(section.getString(key + ".message", "").split("|")));

                        if(Bukkit.getServer().getVersion().contains("spigot"))
                            _jobsdata._spigotMessage = new SpigotMessage(section.getConfigurationSection(key + ".spigot-message"));
                        else
                            _jobsdata._spigotMessage = null;
                    } else if(key.equalsIgnoreCase("entity-sign")) {
                        _jobsdata._signStringMessage.addAll(Arrays.asList(section.getString(key + ".message", "").split("|")));

                        if(Bukkit.getServer().getVersion().contains("spigot"))
                            _jobsdata._signSpigotMessage = new SpigotMessage(section.getConfigurationSection(key + ".spigot-message"));
                        else
                            _jobsdata._signSpigotMessage = null;
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
    
    private void setMat(String action, int key, ArrayList<MatClass> tier){
        if(!_jobsdata.getMatHash().containsKey(action))
            _jobsdata.getMatHash().put(action, new HashMap<>());
        _jobsdata.getMatHash().get(action).put(key, tier);
    }

    private void setEntity(String action, int key, ArrayList<EntityType> tier){
        if(!_jobsdata.getEntHash().containsKey(action))
            _jobsdata.getEntHash().put(action, new HashMap<>());
        _jobsdata.getEntHash().get(action).put(key, tier);
    }
    
    private void setPotions(String action, int key, ArrayList<PotionTypeAdv> tier){
        if(!_jobsdata.getPotHash().containsKey(action))
            _jobsdata.getPotHash().put(action, new HashMap<>());
        _jobsdata.getPotHash().get(action).put(key, tier);
    }
    
    private void setEnchants(String action, int key, ArrayList<EnchantTypeAdv> tier) {
        if(!_jobsdata.getEnchantHash().containsKey(action))
            _jobsdata.getEnchantHash().put(action, new HashMap<>());
        _jobsdata.getEnchantHash().get(action).put(key, tier);
    }
    
    private void setTierPays(String key, Boolean isPays){
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
    
    public void setHide(String block, boolean isHide){
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
    }
}