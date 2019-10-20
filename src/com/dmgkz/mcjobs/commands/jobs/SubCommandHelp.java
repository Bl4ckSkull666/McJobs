/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.jobs;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import java.text.DecimalFormat;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class SubCommandHelp {
    public static void command(Player p, int page) {
        DecimalFormat df = new DecimalFormat("#,##0");
        if(page > McJobs.getPlugin().getLanguage().getSpaces("numhelp", p.getUniqueId()) || page < 1) {
            p.sendMessage(McJobs.getPlugin().getLanguage().getJobHelp("nohelp", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page)));
            return;            
        }
        
        p.sendMessage(ChatColor.GOLD + "---~~ " + ChatColor.DARK_AQUA + "MC Jobs " + ChatColor.GOLD + "~~---");
        p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getJobHelp("page", p.getUniqueId()).addVariables("", p.getName(), "") +" " + page);
        p.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------------------------");
        p.sendMessage("");

        int line = 1;
        while(true) {
            String now = McJobs.getPlugin().getLanguage().getJobHelp(page + "." + line, p.getUniqueId()).addVariables(df.format(PlayerData.getAllowedJobCount(p.getUniqueId())), p.getName(), String.valueOf(page));
            
            if(now.equalsIgnoreCase("--"))
                break;
            
            p.sendMessage(now);
            line++;
        }
        
        p.sendMessage("");
        if(page == McJobs.getPlugin().getLanguage().getSpaces("numhelp", p.getUniqueId()))
            p.sendMessage(McJobs.getPlugin().getLanguage().getJobHelp("finish", p.getUniqueId()).addVariables("", p.getName(), ""));
        else {
            if(Bukkit.getVersion().toLowerCase().contains("spigot")) {
                int spaces = 55;
                TextComponent tcmain = new TextComponent("");
                TextComponent tcprev = new TextComponent("");
                if(page > 1) {
                    tcprev = new TextComponent(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobHelp("prevpage", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page - 1))));
                    spaces -= ChatColor.stripColor(tcprev.getText()).length();
                    tcprev.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobHelp("command", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page - 1))))));
                }
                
                TextComponent tcnext = new TextComponent(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobHelp("nextpage", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page + 1))));
                spaces -= ChatColor.stripColor(tcnext.getText()).length();
                tcnext.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobHelp("command", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page + 1))))));
                
                tcmain.addExtra(tcprev);
                for(int i = 0; i < spaces; i++)
                    tcmain.addExtra(" ");
                tcmain.addExtra(tcnext);
                p.spigot().sendMessage(tcmain);
            } else {
                p.sendMessage(McJobs.getPlugin().getLanguage().getJobHelp("continuepage", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page + 3)) + " " + McJobs.getPlugin().getLanguage().getJobHelp("command", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page + 1)));
                p.sendMessage("");
                
                int spaces = 55;
                String tctop = "";
                String tcprev = "";
                if(page > 1) {
                    tcprev = ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobHelp("prevpage", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page - 1)));
                    spaces -= ChatColor.stripColor(tcprev).length();
                }
                
                String tcnext = ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobHelp("nextpage", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page + 1)));
                spaces -= ChatColor.stripColor(tcnext).length();
                
                tctop = tcprev;
                for(int i = 0; i < spaces; i++)
                    tctop += " ";
                tctop += tcnext;
                
                String tcbot = "";
                tcprev = "";
                if(page > 1) {
                    tcprev = ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobHelp("coomand", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page - 1)));
                    spaces -= ChatColor.stripColor(tcprev).length();
                }
                
                tcnext = ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobHelp("command", p.getUniqueId()).addVariables("", p.getName(), String.valueOf(page + 1)));
                spaces -= ChatColor.stripColor(tcnext).length();
                
                tcbot = tcprev;
                for(int i = 0; i < spaces; i++)
                    tcbot += " ";
                tcbot += tcnext;
                p.sendMessage(tctop);
                p.sendMessage(tcbot);
            }
        }
    }
}
