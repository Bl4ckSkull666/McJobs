package com.dmgkz.mcjobs.scheduler;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerjobs.data.CompData;

public class McJobsPreComp implements Runnable{

    @Override
    public void run() {
        ArrayList<CompCache> copycomp = new ArrayList<>();
        
        copycomp.addAll(CompData.getCompCache());
        if(copycomp.isEmpty())
            return;
        
        CompData.getCompCache().clear();
        Bukkit.getScheduler().runTaskAsynchronously(McJobs.getPlugin(), new McJobsComp(copycomp));
    }
}
