package com.dmgkz.mcjobs.listeners.mcjobs;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.dmgkz.mcjobs.events.McJobsEventJobChange;
import com.dmgkz.mcjobs.playerdata.PlayerData;


public class JobChangeListener implements Listener {
    static private Integer _timer;
    
    @EventHandler(priority = EventPriority.LOW)
    public void mcLeaveJob(McJobsEventJobChange event){
        if(event.getLeave()) {
            String job = event.getJob();
            PlayerData.addReJoinTimer(event.getPlayer().getUniqueId(), job, _timer);
        }
    }

    static public void setTimer(int i){
        if(i > 0)
            _timer = i;
        else
            _timer = 1;
    }
    
    public static int getTimer() {
        return _timer;
    }
}