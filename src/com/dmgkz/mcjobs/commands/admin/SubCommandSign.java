/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.admin;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import com.dmgkz.mcjobs.util.JobSign;
import com.dmgkz.mcjobs.util.SignType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class SubCommandSign {
    //mcjadm sign {job} {type}
    public static void command(CommandSender s, String l, String[] a) {
        String str = "";
        PrettyText text = new PrettyText();
        Player p;
        if(s instanceof Player) {
            p = (Player)s;
            if(!s.hasPermission("mcjobs.admin.defaults")) {
                str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("permission", p.getUniqueId()).addVariables("", p.getName(), l);
                text.formatPlayerText(str, p);
                return;
            }
        } else {
            s.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminCommand("cmd-need-player", UUID.fromString("00000000-0000-0000-0000-000000000000")).addVariables("", "Console", l));
            return;
        }
        
        if(a.length != 3) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("args", p.getUniqueId()).addVariables("", p.getName(), l);
            text.formatPlayerText(str, p);                    
            return;
        }
        
        if(!PlayerJobs.getJobsList().containsKey(a[1])) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("missing-job", p.getUniqueId()).addVariables("", p.getName(), l);
            text.formatPlayerText(str, p);                    
            return;
        }
        
        SignType siType = SignType.getByName(a[2]);
        if(siType == SignType.NONE) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("missing-signtype", p.getUniqueId()).addVariables("", p.getName(), l);
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
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("no-sign-in-sight", p.getUniqueId()).addVariables("", p.getName(), l);
            text.formatPlayerText(str, p);                    
            return;
        }
        
        if(McJobs.getPlugin().getSignManager().isSign(tmp.getLocation())) {
            str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("sign-is-registed", p.getUniqueId()).addVariables("", p.getName(), l);
            text.formatPlayerText(str, p);                    
            return;
        }
        
        JobSign js = new JobSign(a[1], siType);
        McJobs.getPlugin().getSignManager().addSign(tmp.getLocation(), js);
        str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("sign-successfull-registed", p.getUniqueId()).addVariables("", p.getName(), l);
        text.formatPlayerText(str, p);                    
    }
}
