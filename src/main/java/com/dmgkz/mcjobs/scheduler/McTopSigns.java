/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.scheduler;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.database.Database;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.levels.Leveler;
import com.dmgkz.mcjobs.util.JobSign;
import com.dmgkz.mcjobs.util.TimeFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 *
 * @author Bl4ckSkull666
 */
public class McTopSigns implements Runnable {
    private static final List<JobSign> _signs = new ArrayList<>();
    private static final HashMap<String, HashMap<Double, List<OfflinePlayer>>> _tops = new HashMap<>();
    private static long _lastUpdate = 0L;
    
    @Override
    public void run() {
        _tops.clear();
        
        Database.getTopPlayerJobs();
        _lastUpdate = System.currentTimeMillis();
        if(McJobs.getPlugin().getConfig().getBoolean("toplist.use-sign", false))
            Bukkit.getScheduler().runTask(McJobs.getPlugin(), new UpdateSigns());
    }
    
    public static class UpdateSigns implements Runnable {
        @Override
        public void run() {
            for(JobSign js : _signs) {
                Sign s = getSign(js.getLocation());
                if(s == null) {
                    McJobs.getPlugin().getLogger().info("Missing Top Sign at " + js.getLocation().getWorld() + " X:" + js.getLocation().getBlockX() + " Y:" + js.getLocation().getBlockY() + " Z:" + js.getLocation().getBlockZ() + " for Job " + js.getJob());
                    continue;
                }

                int line = js.getStartLine();
                //No one has Job? Continue; 
                if(!_tops.containsKey(js.getJob().toLowerCase())) {
                    clearSigns(js);
                    setHead(js.getLocation().clone().add(0.0d, 1.0d, 0.0d), null);
                    continue;
                }

                
                boolean isHeader = false;
                int pos = 1;

                boolean breakLoop = false;
                HashMap<Double, List<OfflinePlayer>> topPlayers = _tops.get(js.getJob().toLowerCase());
                List<Double> tmp = new ArrayList<>();
                tmp.addAll(topPlayers.keySet());
                Collections.sort(tmp);
                Collections.reverse(tmp);

                for(Double doub: tmp) {
                    if(breakLoop) 
                        break;
                    
                    int level = Leveler.getLevelByExp(doub);
                    for(OfflinePlayer op: topPlayers.get(doub)) {
                        if(breakLoop) 
                            break;
                        
                        if(!isHeader) {
                            setHead(js.getLocation().clone().add(0.0d, 1.0d, 0.0d), op);
                            isHeader = true;
                        }
                        
                        s.setLine(line, getPlayerLine(op, pos, level));
                        line++;

                        if(line == 4) {
                            s.update(true);
                            line = 0;
                            s = getSign(s.getLocation().clone().add(0.0d, -1.0d, 0.0d));
                            if(s == null)
                                breakLoop = true;
                        }
                    }
                    pos++;
                }
                if(s != null)
                    s.update(true);
            }
        }
    }
    
    private static String getPlayerLine(OfflinePlayer op, int pos, int level) {
        String str = McJobs.getPlugin().getConfig().getString("toplist.template", "&a%pos.&3%name &4Lv.%level");
        String clear = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', str));
        clear = replace(clear, pos, level, op.getName());
        String name = op.getName();
        if(clear.length() > 16) {
            name = cutPlayerName(name, clear.length()-16);
        }
                        
        return ChatColor.translateAlternateColorCodes('&',replace(str, pos, level, name));
    }
    
    private static void clearSigns(JobSign js) {
        Sign s = getSign(js.getLocation());
        int line = js.getStartLine();
        if(s != null) {
            s.setLine(line, ChatColor.translateAlternateColorCodes('&', "&b&lBe the first"));
            for(int i = line+1; i < 4; i++)
                s.setLine(i, "");
            s.update(true);

            s = getSign(s.getLocation().clone().add(0, -1.0d, 0));
            while(s != null) {
                for(int i = 0; i < 4; i++)
                    s.setLine(i, "");
                s.update(true);
                s = getSign(s.getLocation().clone().add(0, -1.0d, 0));
            }
        }
    }
    
    private static String replace(String str, int pos, int level, String name) {
        str = str.replace("%pos", String.valueOf(pos));
        str = str.replace("%level", String.valueOf(level));
        str = str.replace("%name", name);
        return str;
    }
    
    private static String cutPlayerName(String pName, int cut) {
        cut += 2;
        return pName.substring(0, pName.length()-cut) + "..";
    }
    
    private static void setHead(Location loc, OfflinePlayer op) {
        ItemFrame frame = getItemFrame(loc);
        if(frame != null) {
            if(op != null) {
                ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta sm = (SkullMeta)Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
                sm.setOwningPlayer(op);
                sm.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b" + op.getName()));
                head.setItemMeta(sm);
                if(head != frame.getItem()) {
                    frame.setItem(head);
                    frame.setCustomName(ChatColor.translateAlternateColorCodes('&', "&b" + op.getName()));
                    frame.setCustomNameVisible(true);
                }
            } else {
                try {
                    frame.setItem(null);
                } catch(Exception ex) {
                    McJobs.getPlugin().getLogger().info("Can't clear ItemFrame on Toplist");
                }
            }
        } else if(loc.getBlock().getState() instanceof Skull) {
            Skull skull = (Skull)loc.getBlock().getState();
            skull.setOwningPlayer(op);
            skull.update(true);
        }
    }
    
    private static Sign getSign(Location loc) {
        if(loc.getBlock().getState() instanceof Sign)
            return (Sign)loc.getBlock().getState();
        return null;
    }
    
    private static ItemFrame getItemFrame(Location loc) {
        for(Entity e: loc.getChunk().getEntities()) {
            if(e.getLocation().getBlockX() == loc.getBlockX() && e.getLocation().getBlockY() == loc.getBlockY() && e.getLocation().getBlockZ() == loc.getBlockZ()) {
                if(e.getType().equals(EntityType.ITEM_FRAME))
                    return (ItemFrame)e;
            }
        }
        return null;
    }
    
    public static HashMap<Double, List<OfflinePlayer>> getTopOfJob(String job) {
        HashMap<Double, List<OfflinePlayer>> tmp = new HashMap<>();
        if(_tops.containsKey(job.toLowerCase()))
            tmp.putAll(_tops.get(job.toLowerCase()));
        return tmp;
    } 
    
    public static void addPlyerToTop(OfflinePlayer op, String job, double exp) {
        if(!_tops.containsKey(job))
            _tops.put(job, new HashMap<>());
        
        if(!_tops.get(job).containsKey(exp))
            _tops.get(job).put(exp, new ArrayList<>());
        
        _tops.get(job).get(exp).add(op);
    }
    
    public static String getLastUpdated(UUID uuid) {
        if(_lastUpdate == 0L)
            return "No data collected until now.";
        
        int rest = (int)((System.currentTimeMillis() - _lastUpdate) / 1000);
        return TimeFormat.getFormatedTime(uuid, rest);
    }
    
    public static void addSign(JobSign js, boolean upd) {
        _signs.add(js);
        if(upd && McJobs.getPlugin().getConfig().getBoolean("toplist.use-sign", false))
            Bukkit.getScheduler().runTask(McJobs.getPlugin(), new UpdateSigns());
    }
    
    public static void removeSign(JobSign js) {
        _signs.remove(js);
    }
}
