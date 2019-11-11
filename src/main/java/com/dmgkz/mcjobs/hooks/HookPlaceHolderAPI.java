/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.hooks;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.levels.Leveler;
import com.dmgkz.mcjobs.playerjobs.pay.PayMoney;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class HookPlaceHolderAPI extends PlaceholderExpansion {
    private final McJobs _plugin;
    
    public HookPlaceHolderAPI(McJobs plugin) {
        _plugin = plugin;
    }
    
    @Override
    public String getIdentifier() {
        return "mcjobs";
    }

    @Override
    public String getAuthor() {
        String str = "";
        for(String author: McJobs.getPlugin().getDescription().getAuthors()) {
            if(!str.isEmpty())
                str += ", ";
            str += author;
        }
        return str;
    }

    @Override
    public String getVersion() {
        return McJobs.getPlugin().getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public boolean canRegister() {
        return true;
    }
    
    @Override
    public String onRequest(OfflinePlayer p, String identifier) {
        if(p == null) {
            return "Player can't null";
        }

        ArrayList<String> jobs = PlayerData.getPlayerJobs(p.getUniqueId());
        Collections.sort(jobs);
        
        switch(identifier) {
            case "joblist":
                String joblists = "";
                for(String job: PlayerJobs.getJobsList().keySet()) {
                    if(!joblists.isEmpty())
                        joblists += _plugin.getConfig().getString("hook.placeholderapi.list-seperator", ", ");
                    joblists += _plugin.getLanguage().getJobName(job, p.getUniqueId());
                }
                return joblists;
            case "playerjobs":
                String myjobs = "";
                for(String job: PlayerData.getPlayerJobs(p.getUniqueId())) {
                    if(!myjobs.isEmpty())
                        myjobs += _plugin.getConfig().getString("hook.placeholderapi.list-seperator", ", ");
                    myjobs += _plugin.getLanguage().getJobName(job, p.getUniqueId());
                }
                return myjobs;
            case "ranks":
                String myranks = "";
                for(String job: PlayerData.getPlayerJobs(p.getUniqueId())) {
                    if(!myranks.isEmpty())
                        myranks += _plugin.getConfig().getString("hook.placeholderapi.list-seperator", ", ");
                    myranks += _plugin.getLanguage().getJobRank(PlayerData.getJobRank(p.getUniqueId(), job), p.getUniqueId());
                }
                return myranks;
            case "levels":
                String mylevels = "";
                for(String job: PlayerData.getPlayerJobs(p.getUniqueId())) {
                    if(!mylevels.isEmpty())
                        mylevels += _plugin.getConfig().getString("hook.placeholderapi.list-seperator", ", ");
                    mylevels += PlayerData.getJobLevel(p.getUniqueId(), job);
                }
                return mylevels;
            case "paycache":
                return PayMoney.getPayCacheDisplay(p.getUniqueId());
            default:
                String[] paths = identifier.split("_");
                if(paths.length <= 1)
                    return "Wrong PlaceHolder used";
                
                if(!PlayerJobs.getJobsList().containsKey(paths[0].toLowerCase()))
                    return "Job not found";
                
                String jobname = paths[0].toLowerCase();
                switch(paths[1].toLowerCase()) {
                    case "name":
                        return _plugin.getLanguage().getJobName(jobname, p.getUniqueId());
                    case "rank":
                        if(!PlayerData.hasJob(p.getUniqueId(), jobname))
                            return _plugin.getLanguage().getPlaceholderAPI("no-job", p.getUniqueId());
                        
                        return _plugin.getLanguage().getJobRank(PlayerData.getJobRank(p.getUniqueId(), jobname), p.getUniqueId());
                    case "level":
                        if(!PlayerData.hasJob(p.getUniqueId(), jobname))
                            return _plugin.getLanguage().getPlaceholderAPI("no-job", p.getUniqueId());
                        
                        return PlayerData.getJobLevel(p.getUniqueId(), jobname).toString();
                    case "exp":
                        if(!PlayerData.hasJob(p.getUniqueId(), jobname))
                            return _plugin.getLanguage().getPlaceholderAPI("no-job", p.getUniqueId());
                        
                        return PlayerData.getJobExpDisplay(p.getUniqueId(), jobname);
                    case "exptolvlup":
                        if(!PlayerData.hasJob(p.getUniqueId(), jobname))
                            return _plugin.getLanguage().getPlaceholderAPI("no-job", p.getUniqueId());
                        
                        double needExp = Leveler.getXPtoLevel(PlayerData.getJobLevel(p.getUniqueId(), jobname)+1) - PlayerData.getJobExp(p.getUniqueId(), jobname);
                        return Leveler.getXPDisplay(needExp);
                    case "nextlvlexp":
                        if(!PlayerData.hasJob(p.getUniqueId(), jobname))
                            return _plugin.getLanguage().getPlaceholderAPI("no-job", p.getUniqueId());
                        
                        return Leveler.getXPtoLevelDisplay(PlayerData.getJobLevel(p.getUniqueId(), jobname)+1);
                    case "language":
                        return PlayerData.getLang(p.getUniqueId());
                    case "has":
                        if(!PlayerData.hasJob(p.getUniqueId(), jobname))
                            return _plugin.getLanguage().getPlaceholderAPI("no-job", p.getUniqueId());
                        
                        return _plugin.getLanguage().getPlaceholderAPI("has-job", p.getUniqueId());
                    default:
                        return "Wrong PlaceHolder used";
                }
        }
    }
    
    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        return onRequest(Bukkit.getOfflinePlayer(p.getUniqueId()), identifier);
    }
    
    public static String checkPlaceholders(String str, UUID uuid) {
        if(PlaceholderAPI.containsPlaceholders(str)) {
            return PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(uuid), str);
        }
        return str;
    }
}
