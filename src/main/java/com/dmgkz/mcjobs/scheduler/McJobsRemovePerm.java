package com.dmgkz.mcjobs.scheduler;

import com.dmgkz.mcjobs.playerdata.PlayerData;
import java.util.ArrayList;
import java.util.UUID;


public class McJobsRemovePerm implements Runnable {
    @Override
    public void run() {
        ArrayList<UUID> temp = new ArrayList<>();
        temp.addAll(PlayerData.getPlayerPerms());
        
        for(UUID uuid: temp) {
            if(PlayerData.decrementTimer(uuid))
                PlayerData.getPlayerPerms().remove(uuid);
        }
        
        PlayerData.savePlayerPerms();
    }
}
