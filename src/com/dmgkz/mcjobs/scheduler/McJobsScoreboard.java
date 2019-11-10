/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.scheduler;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.localization.GetLanguage;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.levels.Leveler;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

/**
 *
 * @author Bl4ckSkull666
 */
public class McJobsScoreboard implements Runnable {
    //User UUID <Jobname, String>
    private static final HashMap<UUID, Scoreboard> _userScoreboards = new HashMap<>();
    private static final DecimalFormat _df = new DecimalFormat("#,###,###,##0.##");
    private static ConfigurationSection _conf;
    private static GetLanguage _lang; 
    
    public McJobsScoreboard(McJobs plugin) {
        _conf = plugin.getConfig();
        _lang = plugin.getLanguage();
    }
    
    @Override
    public void run() {
        for(Player p: Bukkit.getOnlinePlayers()) {
            McJobsScoreboard.setScoreboard(p);
        }
    }
    
    private static Scoreboard getScoreboard(Player p) {
        if(!_userScoreboards.containsKey(p.getUniqueId()))
            _userScoreboards.put(p.getUniqueId(), Bukkit.getScoreboardManager().getNewScoreboard());
        return _userScoreboards.get(p.getUniqueId());
    }
    
    public static void setScoreboard(Player p) {
        Objective obj = null;
        Scoreboard sb = getScoreboard(p);
        obj = sb.getObjective("mcjobs");
        if(obj != null)
            obj.unregister();
        
        if(PlayerData.getPlayerJobs(p.getUniqueId()).isEmpty() || !PlayerData.showScoreboard(p.getUniqueId())) {
            p.setScoreboard(sb);
            return;
        }
                
        obj = sb.registerNewObjective("mcjobs", "mcjobs", ChatColor.translateAlternateColorCodes('&', _lang.getScoreboard("header", p.getUniqueId())), RenderType.INTEGER);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        String userOrder = PlayerData.getScoreboardOrder(p.getUniqueId());
        String userSort = PlayerData.getScoreboardSort(p.getUniqueId());

        HashMap<String, ArrayList<JobMe>> jobsMap = new HashMap<>();
        
        for(String job: PlayerData.getPlayerJobs(p.getUniqueId())) {
            if(PlayerJobs.getJobsList().get(job).getData().hideInScoreboard())
                continue;

            JobMe jm = new JobMe(job, p.getUniqueId());
            String order = "";
            switch(userOrder.toLowerCase()) {
                case "job":
                    order = _lang.getJobName(job, p.getUniqueId());
                    break;
                case "rank":
                    order = _lang.getJobRank(PlayerData.getJobRank(p.getUniqueId(), job), p.getUniqueId());
                    break;
                case "level":
                    order = String.valueOf(PlayerData.getJobLevel(p.getUniqueId(), job));
                    break;
                case "hasexp":
                    order = String.valueOf(PlayerData.getJobExp(p.getUniqueId(), job));
                    break;
                case "nextexp":
                    order = String.valueOf(Leveler.getXPtoLevel(PlayerData.getJobLevel(p.getUniqueId(), job)+1));
                    break;
                case "needexp":
                    order = String.valueOf(Leveler.getXPtoLevel(PlayerData.getJobLevel(p.getUniqueId(), job)+1)-PlayerData.getJobExp(p.getUniqueId(), job));
                    break;
                default:
                    continue;
            }
            
            if(!jobsMap.containsKey(order))
                jobsMap.put(order, new ArrayList<>());
            jobsMap.get(order).add(jm);
        }
        
        List<String> keyList = new ArrayList<>();
        keyList.addAll(jobsMap.keySet());
        
        Collections.sort(keyList);
        if(userSort.equalsIgnoreCase("desc"))
            Collections.reverse(keyList);
        
        boolean hasUpdated = false;
        int i = 16;
        while(i >= 1) {
            if(i == 0)
                break;
            
            for(String key1: keyList) {
                if(i == 0)
                    break;
                
                for(JobMe jMe: jobsMap.get(key1)) {
                    if(i == 0)
                        break;
                
                    for(String k: _conf.getStringList("scoreboard.template")) {
                        k = k.replace("%jobname", jMe._name);
                        k = k.replace("%rank", jMe._rank);
                        k = k.replace("%level", String.valueOf(jMe._level));
                        k = k.replace("%hasexp", jMe._hasExp);
                        k = k.replace("%nextexp", jMe._nextExp);
                        k = k.replace("%needexp", jMe._needExp);

                        if(k.contains("%placeholder")) {
                            int needHolder = 16 - (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', k)).length() - 12);
                            String holders = getPlaceHolders(needHolder);
                            k = k.replace("%placeholder", holders);
                        }

                        String cutted = cutString(k);
                        obj.getScore(ChatColor.translateAlternateColorCodes('&', cutted)).setScore(i);
                        i--;

                        hasUpdated = true;
                        if(i == 0)
                            break;
                    }
                }
            }
            break;
        }
        
        if(hasUpdated) {
            p.setScoreboard(sb);
        }
    }

    private static String cutString(String var) {
        String str = "";
        String clearString = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', var));
        if(clearString.length() > 16) {
            char[] cChar = clearString.toCharArray();
            char[] aChar = var.toCharArray();
            int c = 0;
            int a = 0;
            for(int i = 0; i < 13; i++) {
                if(aChar[a] != cChar[c])
                    i--;
                else
                    c++;
                str += String.valueOf(aChar[a]);
                a++;
            }
            return str + "...";
        }
        return var;
    }
    
    private static String getPlaceHolders(int i) {
        String str = "";
        while(i > 0) {
            str += " ";
            i--;
        }
        return str;
    }
    
    private static class JobMe {
        public final String _name;
        public final String _rank;
        public final int _level;
        public final String _hasExp;
        public final String _needExp;
        public final String _nextExp;
        
        public JobMe(String jobname, UUID uuid) {
            _name = _lang.getJobName(jobname, uuid);
            _rank = _lang.getJobRank(PlayerData.getJobRank(uuid, jobname), uuid);
            _level  = PlayerData.getJobLevel(uuid, jobname);
            _hasExp = _df.format(PlayerData.getJobExp(uuid, jobname));
            _nextExp = _df.format(Leveler.getXPtoLevel(_level+1));
            _needExp = _df.format(Leveler.getXPtoLevel(_level+1)-PlayerData.getJobExp(uuid, jobname));
        }
    }
}
