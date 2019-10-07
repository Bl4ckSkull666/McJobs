package com.dmgkz.mcjobs.listeners;

import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 *
 * @author Bl4ckSkull666
 */
public class OnPlayerInteractEntity implements Listener {
    private final HashMap<UUID, Calendar> _clicked = new HashMap<>();
    private final HashMap<UUID, Sign> _signs = new HashMap<>();
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        World w = e.getRightClicked().getWorld();
        int x = e.getRightClicked().getLocation().getBlockX();
        int z = e.getRightClicked().getLocation().getBlockZ();
        int y = Math.max(e.getRightClicked().getLocation().getBlockY()-7, 1);
        
        int r = 0;
        while(r <= 10) {
            Block b = w.getBlockAt(x, (y+r), z);
            if(b.getState() instanceof Sign) {
                if(isSign((Sign)b.getState(), e.getPlayer()))
                    break;
            }
            r++;
        }
    }
    
    private boolean selfSign(UUID uuid, Sign s) {
        if(_signs.containsKey(uuid)) {
            if(_signs.get(uuid).equals(s))
                return true;
            else
                _signs.remove(uuid);
        }
        _signs.put(uuid, s);
        return false;
    }
    
    private boolean hasClicked(UUID uuid, Sign s) {
        boolean selfSign = selfSign(uuid, s);
        if(_clicked.containsKey(uuid)) {
            Calendar lastClick = _clicked.get(uuid);
            Calendar now = Calendar.getInstance();
            if(!selfSign || (now.getTimeInMillis()-lastClick.getTimeInMillis()) > 5000) {
                _clicked.remove(uuid);
            } else
                return true;
        }
        _clicked.put(uuid, Calendar.getInstance());
        return false;
    }
    
    private boolean isSign(Sign s, Player p) {
        if(s.getLine(0).equalsIgnoreCase("[mcjobs]")) {
            if(hasClicked(p.getUniqueId(), s))
                return true;
            
            String job = s.getLine(2);
            if(PlayerJobs.getJobsList().containsKey(job)) {
                PlayerJobs.getJobsList().get(job).getData().sendSignMessage(p);
                return true;
            }
        }
        return false;
    }
}
