/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.admin;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class SubCommandRegionMessage {
    private Map<UUID, TextComponent> _users = new HashMap<>();
    private Map<UUID, String> _jobs = new HashMap<>();
    
    /*
    *        0        1       2      3
    * /jadm rmsg    begin   jobname
    * /jadm rmsg    add     Text
    * /jadm rmsg    break                   - Set New Line
    * /jadm rmsg    hover   type    Text    - Set Hover text to last add
    * /jadm rmsg    click   type    doing   - Set Click action to last add
    * /jadm rmsg    save                    - Save the Msg and clear the open message
    * /jadm rmsg    remove  jobname
    */
    public static void command(CommandSender s, String l, String[] a) {
        String str = "";
        PrettyText text = new PrettyText();
        String name = "Console";
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player) {
            uuid = ((Player)s).getUniqueId();
            name = ((Player)s).getName();
            if(!s.hasPermission("mcjobs.admin.region.message")) {
                str = ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("permission", uuid).addVariables("", name, l);
                text.formatPlayerText(str, (Player)s);
                return;
            }
        }
        
        if(a.length < 2) {
            
            return;
        }
        
        String subCmd = a[1];
        List<String> tmp = new ArrayList<>();
        tmp.addAll(Arrays.asList(a));
        tmp.remove(0);
        tmp.remove(0);
        
        switch(subCmd.toLowerCase()) {
            case "begin":
                useCommandBegin((String[])tmp.toArray(), uuid);
                break;
            case "add":
                useCommandAdd((String[])tmp.toArray(), uuid);
                break;
            case "break":
                useCommandBreak((String[])tmp.toArray(), uuid);
                break;
            case "hover":
                useCommandHover((String[])tmp.toArray(), uuid);
                break;
            case "click":
                useCommandClick((String[])tmp.toArray(), uuid);
                break;
            case "save":
                useCommandSave((String[])tmp.toArray(), uuid);
                break;
            case "remove":
                useCommandRemove((String[])tmp.toArray(), uuid);
                break;
            case "clear":
                useCommandClear((String[])tmp.toArray(), uuid);
                break;
            default:
                
                return;
        }
    }
    
    private static void useCommandBegin(String[] a, UUID uuid) {
        //jadm rmsg begin jobname
        
    }
    
    private static void useCommandAdd(String[] a, UUID uuid) {
        //jadm rmsg add Text here
    }
    
    private static void useCommandBreak(String[] a, UUID uuid) {
        //jadm rmsg break - Set New Line
    }
    
    private static void useCommandHover(String[] a, UUID uuid) {
        //jadm rmsg hover type Text - Set Hover text to last add
    }
    
    private static void useCommandClick(String[] a, UUID uuid) {
        //jadm rmsg click type doing - Set Click action to last add
    }
    
    private static void useCommandSave(String[] a, UUID uuid) {
        //jadm rmsg save - Save the Msg and clear the open message
    }
    
    private static void useCommandRemove(String[] a, UUID uuid) {
        //jadm rmsg remove jobname
    }
    
    private static void useCommandClear(String[] a, UUID uuid) {
        //jadm rmsg clear
       
    }
}
