/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.util;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class SpigotMessage {
    private final List<TextComponent> _messages = new ArrayList<>();
    
    /*
    * cs = ConfigurationSection("spigot-messages");
    * spigot-messages:
    *     1:
    *         message: 'Hallo ich bin ein Test.'
    *         hover-type: 'achievement|entity|item|text'
    *         hover-message: ''
    *         click-type: 'change_page|open_file|open_url|run_command|suggest_command'
    *         click-message: ''
    *         break: true|false
    */
    public SpigotMessage(ConfigurationSection confSec) {
        TextComponent tc = new TextComponent("");
        for(String k: confSec.getKeys(false)) {
            ConfigurationSection cs = confSec.getConfigurationSection(k);
            if(!cs.isString("message"))
                continue;
            
            if(tc == null)
                tc = new TextComponent("");
            
            TextComponent tmp = new TextComponent(cs.getString("message"));
            if(cs.isString("hover-msg")) {
                HoverEvent hoverev = new HoverEvent(
                    getHoverAction(cs.getString("hover-type", "text")), 
                    new ComponentBuilder(cs.getString("hover-msg")).create()
                );
                tmp.setHoverEvent(hoverev);
            }
             
            if(cs.isString("click-msg")) {
                ClickEvent clickev = new ClickEvent(
                    getClickAction(cs.getString("click-type", "open_url")), 
                    cs.getString("click-msg")
                );
                tmp.setClickEvent(clickev);
            }
            
            tc.addExtra(tmp);
            
            // End of Message?!
            if(cs.getBoolean("break", true)) {
                _messages.add(tc);
                tc = null;
            }
        }
        
        if(tc != null)
            _messages.add(tc);
    }
    
    public void sendMessage(Player p) {
        for(TextComponent tc: _messages) {
            p.spigot().sendMessage(tc);
        }
    }
    
    private net.md_5.bungee.api.chat.HoverEvent.Action getHoverAction(String str) {
        if(HoverEvent.Action.valueOf("SHOW_" + str.toUpperCase()) != null)
            return HoverEvent.Action.valueOf("SHOW_" + str.toUpperCase());
        return HoverEvent.Action.SHOW_TEXT;
    }
    
    private net.md_5.bungee.api.chat.ClickEvent.Action getClickAction(String str) {
        if(ClickEvent.Action.valueOf(str.toUpperCase()) != null)
            return ClickEvent.Action.valueOf(str.toUpperCase());
        return ClickEvent.Action.RUN_COMMAND;
    }
}
