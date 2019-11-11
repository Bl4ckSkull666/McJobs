package com.dmgkz.mcjobs.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class McJobsEventJobChange extends Event {
    private static final HandlerList _handlers = new HandlerList();
    private final Boolean _join;
    private final Boolean _leave;
    private final Player _play;
    private final String _jobName;
    
    public McJobsEventJobChange(Player play, String jobName, Boolean join, Boolean leave){
        _join = join;
        _leave = leave;
        _play = play;
        _jobName = jobName;
    }
    
    public Player getPlayer(){
        return _play;
    }
    
    public String getJob(){
        return _jobName;
    }
    
    public Boolean getJoin(){
        return _join;
    }
    
    public Boolean getLeave(){
        return _leave;
    }
    
    @Override
    public HandlerList getHandlers() {
        return _handlers;
    }

    public static HandlerList getHandlerList(){
        return _handlers;
    }
}
