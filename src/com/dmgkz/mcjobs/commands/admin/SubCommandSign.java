/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.admin;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.localization.GetLanguage;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import com.dmgkz.mcjobs.util.JobSign;
import com.dmgkz.mcjobs.util.SignType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class SubCommandSign {
    //mcjadm sign {job} {type}
    public static void command(Player p, String[] a) {
        String str = "";
        PrettyText text = new PrettyText();
        if(!p.hasPermission("mcjobs.admin.sign")) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("permission", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);
            return;
        }
        
        if(a.length != 3 || a.length == 2 && a[1].equalsIgnoreCase("remove")) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("args", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);                    
            return;
        }
        
        SignType siType = SignType.getByName(a.length == 2?a[1]:a[2]);
        if(siType == SignType.NONE) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("missing-signtype", p.getUniqueId()).addVariables("", p.getName(), a[2]);
            text.formatPlayerText(str, p);                    
            return;
        }
        
        Set<Material> search = new HashSet<>();
        search.addAll(Arrays.asList(Material.values()));
        List<Block> bList = p.getLineOfSight(search, 7);
        
        Sign tmp = null;
        double dist = 10.0;
        for(Block b: bList) {
            if(!(b.getState() instanceof Sign))
                continue;
            
            Sign si = (Sign)b.getState();
            if(tmp == null || dist > p.getLocation().distance(b.getLocation())) {
                tmp = si;
                dist = p.getLocation().distance(b.getLocation());
            }
        }
        
        if(tmp == null) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("no-sign-in-sight", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);                    
            return;
        }
        
        if(siType.equals(SignType.REMOVE)) {
            McJobs.getPlugin().getSignManager().removeSign(tmp.getLocation());
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("sign-removed", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);                    
            return;
        }
        
        if(McJobs.getPlugin().getSignManager().isSign(tmp.getLocation())) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("sign-is-registed", p.getUniqueId()).addVariables("", p.getName(), "");
            text.formatPlayerText(str, p);                    
            return;
        }
        
        String jobOriginal = McJobs.getPlugin().getLanguage().getOriginalJobName(a[1].toLowerCase(), p.getUniqueId()).toLowerCase();
        if(!PlayerJobs.getJobsList().containsKey(jobOriginal)) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("missing-job", p.getUniqueId()).addVariables(a[1], p.getName(), "");
            text.formatPlayerText(str, p);                    
            return;
        }
        
        int startLine = -1;
        if(siType.equals(SignType.TOP)) {
            Sign si = (Sign)tmp.getBlock().getState();
            for(int i = 0; i < 4; i++) {
                if(si.getLine(i).isEmpty()) {
                    startLine = i;
                    break;
                }
            }
            
            if(startLine == -1) {
                GetLanguage.sendMessage(p, "admincommand.empty-line", "&cA Top Sign needs minimum the last line empty", null);
                return;
            }
        }
        
        JobSign js = new JobSign(jobOriginal, siType, tmp.getLocation(), startLine);
        McJobs.getPlugin().getSignManager().addSign(tmp.getLocation(), js, true);
        str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("sign-successfull-registed", p.getUniqueId()).addVariables("", p.getName(), "");
        text.formatPlayerText(str, p); 
    }
}
