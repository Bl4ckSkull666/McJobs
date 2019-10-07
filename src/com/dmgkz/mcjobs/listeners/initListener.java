/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.listeners;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.listeners.mcjobs.JobChangeListener;
import static org.bukkit.Bukkit.getServer;

/**
 *
 * @author Bl4ckSkull666
 */
public class initListener {
    public static void RegisterListeners(McJobs plugin) {
        getServer().getPluginManager().registerEvents(new Baking(), plugin);
        getServer().getPluginManager().registerEvents(new BlockBreak(), plugin);
        getServer().getPluginManager().registerEvents(new BlockPlace(), plugin);
        getServer().getPluginManager().registerEvents(new Brewing(), plugin);
        getServer().getPluginManager().registerEvents(new Crafting(), plugin);
        getServer().getPluginManager().registerEvents(new Enchanting(), plugin);
        getServer().getPluginManager().registerEvents(new Fishing(), plugin);

        getServer().getPluginManager().registerEvents(new MobKill(), plugin);
        getServer().getPluginManager().registerEvents(new OnPlayerInteract(), plugin);
        getServer().getPluginManager().registerEvents(new OnPlayerLogins(), plugin);
        getServer().getPluginManager().registerEvents(new OnPlayerQuit(), plugin);
        getServer().getPluginManager().registerEvents(new ShearEvent(), plugin);
        
        getServer().getPluginManager().registerEvents(new JobChangeListener(), plugin);
        
        getServer().getPluginManager().registerEvents(new OnPlayerInteractEntity(), plugin);
        getServer().getPluginManager().registerEvents(new OnPlayerMove(), plugin);
    }
}
