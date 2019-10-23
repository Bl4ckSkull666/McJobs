/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.jobs;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class SubCommandLanguage {
    public static void command(Player p, String lang) {
        if(lang.isEmpty()) {
            p.sendMessage(McJobs.getPlugin().getLanguage().getJobCommand("language-header", p.getUniqueId()).addVariables("", p.getName(), lang));
            if(Bukkit.getVersion().toLowerCase().contains("spigot")) {
                SpigotBuilds.getLanguageList(p);
            } else if(Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
                WorldEditBuilds.getLanguageList(p);
            } else {
                String languages = "";
                for(String langOriginal: McJobs.getPlugin().getLanguage().getAvaLangs()) {
                    if(!languages.isEmpty())
                        languages += ChatColor.GRAY + ", ";
                    languages += McJobs.getPlugin().getLanguage().getLanguageName(langOriginal, p.getUniqueId());
                }
                p.sendMessage(languages);
                p.sendMessage(McJobs.getPlugin().getLanguage().getJobCommand("language-footer", p.getUniqueId()).addVariables("", p.getName(), lang));
            }
            return;
        }
        
        List<String> avaLang = McJobs.getPlugin().getLanguage().getAvaLangs();
        String langOriginal = McJobs.getPlugin().getLanguage().getOriginalLanguageName(lang, p.getUniqueId());
        if(!avaLang.contains(langOriginal)) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getJobCommand("no-language", p.getUniqueId()).addVariables("", p.getName(), lang));
            return;
        }
        
        PlayerData.setLang(p.getUniqueId(), langOriginal);
        p.sendMessage(ChatColor.GREEN + McJobs.getPlugin().getLanguage().getJobCommand("language-changed", p.getUniqueId()).addVariables("", p.getName(), lang));
    }
}
