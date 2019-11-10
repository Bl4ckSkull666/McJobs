package com.dmgkz.mcjobs.scheduler;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import com.dmgkz.mcjobs.util.TimeFormat;


public class McJobsNotify implements Runnable {
    private static int _timer;
    private static boolean _bShow = false;
    
    public static void setTime(int time){
        _timer = time;
    }
    
    public static void setShow(boolean b){
        _bShow = b;
    }
    
    @Override
    public void run() {
        for(Player play: Bukkit.getOnlinePlayers()) {
            String time = TimeFormat.getFormatedTime(play.getUniqueId(), _timer*60);
            double earned = PlayerData.getEarnedIncome(play.getUniqueId());            
            if(earned != 0D){
                DecimalFormat df = new DecimalFormat("###,###,###.##");
                String sEarned = "";

                sEarned = df.format(earned);

                String str = ChatColor.GREEN + McJobs.getPlugin().getLanguage().getJobNotify("message", play.getUniqueId()).addVariables(sEarned, play.getName(), time);
                PrettyText text = new PrettyText();

                if(_bShow)
                    text.formatPlayerText(str, play);            
                PlayerData.setEarnedIncome(play.getUniqueId(), 0D);
                PlayerData.savePlayerCache(play.getUniqueId());
            }
        }
    }
}
