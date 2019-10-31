/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.admin;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.data.SaveJob;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class SubCommandRegion {
    //jadm region set/remove {jobname} - Need WorldEdit
    public static void command(Player p, String[] a) {
        String str = "";
        PrettyText text = new PrettyText();
        if(!p.hasPermission("mcjobs.admin.region")) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("no-permission", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }
        
        if(!Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("missing-worldedit", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }
        
        if(a.length < 3) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("missing-args", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }
        
        String jobOriginal = McJobs.getPlugin().getLanguage().getOriginalJobName(a[2].toLowerCase(), p.getUniqueId()).toLowerCase();
        String jobMe = McJobs.getPlugin().getLanguage().getOriginalJobName(a[2].toLowerCase(), p.getUniqueId()).toLowerCase();
        if(!PlayerJobs.getJobsList().containsKey(jobOriginal)) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("job-not-found", p.getUniqueId()).addVariables(jobMe, p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }
        
        switch(a[1].toLowerCase()) {
            case "set":
                setRegion(p, jobOriginal, jobMe);
                return;
            case "remove":
                clearRegion(p, jobOriginal, jobMe);
                return;
            default:
                p.sendMessage(ChatColor.RED + "Wrong Subsection in job region. Only set and remove available.");               
        }
    }
    
    private static void setRegion(Player p, String job, String jobMe) {
        Region sel = getSelectedRegion(p);
        if(sel == null || sel.getMaximumPoint() == null || sel.getMinimumPoint() == null) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("no-selection", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        PlayerJobs.getJobsList().get(job).getData().setRegionPosition(BukkitAdapter.adapt(BukkitAdapter.adapt(sel.getWorld()), sel.getMinimumPoint()));
        PlayerJobs.getJobsList().get(job).getData().setRegionPosition(BukkitAdapter.adapt(BukkitAdapter.adapt(sel.getWorld()), sel.getMaximumPoint()));
        if(SaveJob.saveRegion(job)) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("set", p.getUniqueId()).addVariables(jobMe, p.getName(), ""));
        } else {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("error", p.getUniqueId()).addVariables(jobMe, p.getName(), ""));
        }
    }
    
    private static void clearRegion(Player p, String job, String jobMe) {
        PlayerJobs.getJobsList().get(job).getData().getRegionPositions().removePositions();
        if(SaveJob.saveRegion(job)) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("removed", p.getUniqueId()).addVariables(jobMe, p.getName(), ""));
        } else {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("error", p.getUniqueId()).addVariables(jobMe, p.getName(), ""));
        }
    }
    
    public static Region getSelectedRegion(Player p) {
        try {
            return WorldEdit.getInstance().getSessionManager().findByName(p.getName()).getSelection(BukkitAdapter.adapt(p.getWorld()));
        } catch (Exception ex) {
            p.sendMessage("Can't find WorldEdit selection.");
        }
        return null;
    }
}
