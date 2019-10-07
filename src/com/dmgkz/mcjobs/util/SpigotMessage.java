package com.dmgkz.mcjobs.util;

import com.dmgkz.mcjobs.McJobs;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
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
            
            TextComponent tmp = new TextComponent(colorTrans(cs.getString("message")));
            if(cs.isString("hover-msg")) {
                HoverEvent hoverev = new HoverEvent(
                    getHoverAction(cs.getString("hover-type", "text")), 
                    new ComponentBuilder(colorTrans(cs.getString("hover-msg"))).create()
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
    
    private String colorTrans(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    } 
    
    public List<TextComponent> getMessage() {
        return _messages;
    }
    
    public void saveMessage(ConfigurationSection conf, String basePath) {
        McJobs.getPlugin().getLogger().info("============= START TC ==============");
        for(TextComponent tc: _messages) {
            McJobs.getPlugin().getLogger().info("============= NEW TC ==============");
            if(tc.getExtra() != null && !tc.getExtra().isEmpty()) {
                moreMessageDetails(tc.getExtra());
            }
            McJobs.getPlugin().getLogger().info("============= END TC ==============");
        }
        McJobs.getPlugin().getLogger().info("============= STOP TC ==============");
    }
    
    private void moreMessageDetails(List<BaseComponent> list) {
        for(BaseComponent bc: list) {
            McJobs.getPlugin().getLogger().info(bc.toLegacyText());
            McJobs.getPlugin().getLogger().log(Level.INFO, "MoreExtras - Has Hover? {0}", (bc.getHoverEvent() != null));
            McJobs.getPlugin().getLogger().log(Level.INFO, "MoreExtras - Has Click? {0}", (bc.getClickEvent() != null));
            if(bc.getExtra() != null && !bc.getExtra().isEmpty()) {
                McJobs.getPlugin().getLogger().log(Level.INFO, "Has More Extras? {0}", true);
                moreMessageDetails(bc.getExtra());
            }
            
            if(bc.getHoverEvent() != null)
                HoverDetails(bc.getHoverEvent());
            
            if(bc.getClickEvent() != null)
                ClickDetails(bc.getClickEvent());
        }
    }
    
    private void HoverDetails(HoverEvent e) {
        McJobs.getPlugin().getLogger().log(Level.INFO, "HoverEvent Action: {0}", e.getAction().name());
        for(BaseComponent bc: e.getValue()) {
            McJobs.getPlugin().getLogger().log(Level.INFO, "HoverEvent Text: {0}", bc.toLegacyText());
        }
    }
    
    private void ClickDetails(ClickEvent e) {
        McJobs.getPlugin().getLogger().log(Level.INFO, "ClickEvent Action: {0}", e.getAction().name());
        McJobs.getPlugin().getLogger().log(Level.INFO, "ClickEvent Text: {0}", e.getValue());
    }
}
