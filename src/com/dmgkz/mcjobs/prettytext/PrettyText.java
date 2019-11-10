package com.dmgkz.mcjobs.prettytext;

//import java.util.logging.Logger;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.util.EnchantTypeAdv;
import com.dmgkz.mcjobs.util.PotionTypeAdv;
import java.util.List;
import java.util.UUID;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;

public class PrettyText {
    
    public void formatPlayerText(String str, Player play) {
        formatPlayerText(McJobs.getPlugin().getLanguage().checkForPlaceholderAPI(play.getUniqueId(), str), (CommandSender)play);
    }

    public void formatPlayerText(String str, CommandSender play) {
        // 64 is the length of a line.
        int LENGTH = 59;
        
        ArrayList<String> sFormat = new ArrayList<>();
        String sTempHolder = "";
        String sLine = "";
        String pCC = "";
        
        char cc = ChatColor.COLOR_CHAR;

        int num = 0;
        int stopper = 0;
        int ulet = 0;
        int unew = 0;
        int maxlen = LENGTH;

        while(num < str.length()) {
            while(str.charAt(num) != ' ') {
                if(str.charAt(num) == cc){
                    maxlen = maxlen + 2;
                    pCC = Character.toString(cc) + Character.toString(str.charAt(num + 1));
                }
                if(str.charAt(num) == 'i' || str.charAt(num) == 'l' || str.charAt(num) == 't' || str.charAt(num) == ',' || str.charAt(num) == '.' || str.charAt(num) == ':')
                    unew++;
                sTempHolder = sTempHolder.concat(Character.toString(str.charAt(num)));
                num++;
                if(!(num < str.length()))
                    break;
            }
            ulet = unew + ulet;
            while(ulet >= 6 && stopper < 2) {
                if(ulet >= 6){
                    maxlen = maxlen + 2;
                    ulet = ulet - 6;
                }
                stopper++;
            }
            stopper = 0;

            if(sTempHolder.length() + sLine.length() < maxlen - 1) {
                sLine = sLine.concat(sTempHolder + " ");
                sTempHolder = "";
            } else if(sTempHolder.length() + sLine.length() == maxlen || sTempHolder.length() + sLine.length() == maxlen - 1){
                sLine = sLine.concat(sTempHolder);
                if(sLine.endsWith(" "))
                    sLine = sLine.substring(0, sLine.length() - 1);
                sFormat.add(sLine);
                sTempHolder = pCC + "";
                sLine = "";
                maxlen = LENGTH;
                ulet = 0;
            } else {
                if(sLine.endsWith(" "))
                    sLine = sLine.substring(0, sLine.length() - 1);
                sFormat.add(sLine);
                sLine = "";
                sLine = sLine.concat(pCC + sTempHolder + " ");
                sTempHolder = pCC + "";
                maxlen = LENGTH;
                ulet = unew;
            }
            num++;
            unew = 0;
        }
        
        if(!sLine.isEmpty())
            sFormat.add(sLine);

        this.PrintText(sFormat, play);
    }

    
    public String formatMaterialTiers(List<Material> material, ChatColor cc, UUID uuid) {
        String str = "";
        String end =  ChatColor.GRAY + "," + cc + " ";
        for(Material mc: material) {
            if(!str.isEmpty())
                str += end;
            
            str += McJobs.getPlugin().getLanguage().getMaterial(mc.getKey().getKey(), uuid);
        }
        
        str += cc;
        return str;
    }
    
    public String formatEntityTiers(List<EntityType> entity, ChatColor cc, UUID uuid) {
        String str = "";
        String end =  ChatColor.GRAY + "," + cc + " ";
        for(EntityType test: entity) {
            if(!str.isEmpty())
                str += end;
            
            str += McJobs.getPlugin().getLanguage().getEntity(test.name(), uuid);
        }

        str += cc;
        return str;
    }

    public String formatPotionTiers(List<PotionTypeAdv> potion, ChatColor cc, UUID uuid){
        String str = "";
        String end =  ChatColor.GRAY + "," + cc + " ";
        for(PotionTypeAdv test: potion) {
            if(!str.isEmpty())
                str += end;
            
            str += McJobs.getPlugin().getLanguage().getPotion(test.getName(), uuid);
        }
        
        str += cc;
        return str;
    }
    
    public String formatEnchantTiers(List<EnchantTypeAdv> enchant, ChatColor cc, UUID uuid) {
        String str = "";
        String end =  ChatColor.GRAY + ","+ cc + " ";
        for(EnchantTypeAdv test: enchant) {
            if(!str.isEmpty())
                str += end;
            
            str += McJobs.getPlugin().getLanguage().getEnchant(test.getName(), uuid);
        }

        str += cc;
        return str;
    }
    
    public String formatColorTiers(List<DyeColor> colors, ChatColor cc, UUID uuid) {
        String str = "";
        String end =  ChatColor.GRAY + ","+ cc + " ";
        for(DyeColor test: colors) {
            if(!str.isEmpty())
                str += end;

            str += McJobs.getPlugin().getLanguage().getColor(test.name(), uuid);
        }

        str += cc;
        return str;
    }
    
    private void PrintText(ArrayList<String> str, CommandSender p) {
        for(String st: str)
            p.sendMessage(st);
    }

    public static String colorText(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
    
    public static String addSpaces(int i) {
        String str = "";
        
        for(int it = 0; it < i; it++) {
            str += " ";
        }
        
        return str;
    }
}

