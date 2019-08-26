package com.dmgkz.mcjobs.listeners;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import java.util.Map;

public class OnPlayerLogins implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event){
        Player play = event.getPlayer();
        Integer version = McJobs.getPlugin().getVersion();
        
        PlayerData.verifyPlayerCache(play.getUniqueId());
        
        if((play.hasPermission("mcjobs.admin") || play.isOp())){
/*            if(McJobs.getPlugin().isPayXP() && (McJobs.getPlugin().getPayScale().equalsIgnoreCase("low") || McJobs.getPlugin().getPayScale().equalsIgnoreCase("normal"))){
                PrettyText text = new PrettyText();
                String str = McJobs.getPlugin().getLanguage().getAdminLogin("toolow").addVariables("", play.getName(), "");
                text.formatPlayerText(str, play);                
            } */
            
            if(version != McJobs._VERSION || version == null){
                PrettyText text = new PrettyText();
                String str = McJobs.getPlugin().getLanguage().getAdminLogin("outofdate", play.getUniqueId()).addVariables("", play.getName(), "");
                text.formatPlayerText(str, play);
            }
        }
        
        if(!play.hasPermission("mcjobs.admin.leavedefault")){
            for(Map.Entry<String, PlayerJobs> pair: PlayerJobs.getJobsList().entrySet()){
                if(pair.getValue().getData().compJob().isDefault() && !PlayerData.hasJob(play.getUniqueId(), pair.getKey())){
                    PlayerData.addJob(play.getUniqueId(), pair.getKey());
                }
            }
        }            
    }
}
