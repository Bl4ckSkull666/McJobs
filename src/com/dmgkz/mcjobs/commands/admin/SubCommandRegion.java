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
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("permission", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }
        
        if(!Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("missing-worldedit", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }
        
        if(a.length < 2) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("missing-worldedit", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }

        Region sel = getSelectedRegion(p);
        if(sel == null || sel.getMaximumPoint() == null || sel.getMinimumPoint() == null) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("no-selection", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }
        
        String jobOriginal = McJobs.getPlugin().getLanguage().getOriginalJobName(a[1].toLowerCase(), p.getUniqueId()).toLowerCase();
        String jobMe = McJobs.getPlugin().getLanguage().getOriginalJobName(a[1].toLowerCase(), p.getUniqueId()).toLowerCase();
        if(!PlayerJobs.getJobsList().containsKey(jobOriginal)) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("exist", p.getUniqueId()).addVariables(jobMe, p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }
        
        PlayerJobs.getJobsList().get(jobOriginal).getData().setRegionPosition(BukkitAdapter.adapt(BukkitAdapter.adapt(sel.getWorld()), sel.getMinimumPoint()));
        PlayerJobs.getJobsList().get(jobOriginal).getData().setRegionPosition(BukkitAdapter.adapt(BukkitAdapter.adapt(sel.getWorld()), sel.getMaximumPoint()));
        if(SaveJob.saveRegion(jobOriginal)) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("set", p.getUniqueId()).addVariables(jobMe, p.getName(), "");
            text.formatPlayerText(str, p);
        } else {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("error", p.getUniqueId()).addVariables(jobMe, p.getName(), "");
            text.formatPlayerText(str, p);
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
