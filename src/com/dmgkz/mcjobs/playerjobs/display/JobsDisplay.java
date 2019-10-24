package com.dmgkz.mcjobs.playerjobs.display;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.commands.jobs.SpigotBuilds;
import com.dmgkz.mcjobs.commands.jobs.WorldEditBuilds;
import com.dmgkz.mcjobs.localization.GetLanguage;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.data.JobsData;
import com.dmgkz.mcjobs.playerjobs.levels.Leveler;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import com.dmgkz.mcjobs.util.EnchantTypeAdv;
import com.dmgkz.mcjobs.util.PotionTypeAdv;
import java.util.HashMap;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class JobsDisplay {
    private final JobsData _jobsdata;
    
    public JobsDisplay(JobsData jobsdata) {
        _jobsdata = jobsdata;
    }
    
    public void showPlayerJob(Player p, UUID uuid) {
        showPlayerJob((CommandSender)p, uuid);
    }
    
    public void showPlayerJob(CommandSender s, UUID uuid) {
        UUID pUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player)
            pUUID = ((Player)s).getUniqueId();
        
        GetLanguage modText = McJobs.getPlugin().getLanguage();
        String sJob = null;
        String sCost = "";
        String sPays = "";
        String sJobName = _jobsdata.getName(uuid);
        String payscale = _jobsdata.getBasePay().toString();
        PrettyText text = new PrettyText();
        String pName = "N/A";
        if(Bukkit.getOfflinePlayer(uuid) != null)
            pName = Bukkit.getOfflinePlayer(uuid).getName();
        else if(Bukkit.getPlayer(uuid) != null)
            pName = Bukkit.getPlayer(uuid).getName();
        
        if(PlayerData.hasJob(uuid, sJobName.toLowerCase())){
            DecimalFormat df = new DecimalFormat("###,###.####");
            Double i = _jobsdata.getBasePay() * Leveler.getMultiplier(PlayerData.getJobLevel(uuid, sJobName.toLowerCase()));
            payscale = df.format(i);
        }
        
        sJob = sJobName.toUpperCase();

        if(sJob.length() < 12) {
            while(sJob.length() < 12) {
                sJob = sJob.concat(" ");
            }
        } else if(sJob.length() > 12)
            sJob = sJob.substring(0, 12);

        if(PlayerData.hasJob(uuid, sJobName.toLowerCase()))
            sJob = sJob.concat(" " + modText.getJobDisplay("employed", pUUID).addVariables(_jobsdata.getName(pUUID), pName, ""));
        else if(_jobsdata.compJob().isDefault())
            sJob = sJob.concat(" " + modText.getJobDisplay("default", pUUID).addVariables(_jobsdata.getName(pUUID), pName, ""));
        else
            sJob = sJob.concat(" " + modText.getJobDisplay("unemployed", pUUID).addVariables(_jobsdata.getName(pUUID), pName, ""));
        
        sJob = sJob.concat(PrettyText.addSpaces(modText.getSpaces("display", pUUID)) + ChatColor.GREEN + modText.getJobDisplay("basepay", pUUID).addVariables(_jobsdata.getName(pUUID), pName, "") + " " + payscale);
        
        if(_jobsdata.getCostPay(false))
            sCost = ChatColor.RED + modText.getJobDisplay("charge", pUUID).addVariables(_jobsdata.getName(pUUID), pName, "");
        else
            sCost = PrettyText.addSpaces(modText.getSpaces("chargelen", pUUID));
        
        if(_jobsdata.getCostPay(true))
            sPays = ChatColor.GREEN + modText.getJobDisplay("pay", pUUID).addVariables(_jobsdata.getName(pUUID), pName, "");
        else
            sPays = "";
        
        if(_jobsdata.getCostPay(true) && _jobsdata.getCostPay(false))
            sJob = sJob.concat(" - " + ChatColor.RED + modText.getJobDisplay("charge", pUUID).addVariables(_jobsdata.getName(pUUID), pName, "") + ChatColor.DARK_GRAY + "/" + ChatColor.GREEN + modText.getJobDisplay("pay", pUUID).addVariables(_jobsdata.getName(pUUID), pName, ""));
        else
            sJob = sJob.concat(" - " + sCost + sPays);

        s.sendMessage(ChatColor.DARK_AQUA + sJob);

        if(PlayerData.hasJob(uuid, sJobName.toLowerCase())) {
            String level = PlayerData.getJobLevel(uuid, sJobName.toLowerCase()).toString();
            String xp = PlayerData.getJobExpDisplay(uuid, sJobName.toLowerCase());
            String xpNeeded = Leveler.getXPtoLevelDisplay(PlayerData.getJobLevel(uuid, sJobName.toLowerCase()));
            String rank = PlayerData.getJobRank(uuid, sJobName.toLowerCase());
            
            s.sendMessage(ChatColor.DARK_AQUA + modText.getJobDisplay("level", pUUID).addVariables(_jobsdata.getName(pUUID), pName, "") + ChatColor.DARK_GREEN + ": " + level + PrettyText.addSpaces(modText.getSpaces("displaytwo", pUUID)) + 
                              ChatColor.DARK_AQUA + modText.getJobDisplay("rank", pUUID).addVariables(_jobsdata.getName(pUUID), pName, "") + ChatColor.DARK_GREEN + ": " + rank + PrettyText.addSpaces(modText.getSpaces("displaythree", pUUID)) +
                             ChatColor.DARK_AQUA + modText.getJobDisplay("exp", pUUID).addVariables(_jobsdata.getName(pUUID), pName, "") + ChatColor.DARK_GREEN + ": " + xp + "/" + xpNeeded);
        }
        
        sJob = ChatColor.GRAY + _jobsdata.getDesc(pUUID);     
        text.formatPlayerText(sJob, s);
    }
    
    public void showJob(Player p) {
        GetLanguage modText = McJobs.getPlugin().getLanguage();
        showPlayerJob(p, p.getUniqueId());

        if(!_jobsdata.compJob().getMatTypeTiers("break").isEmpty() && !getHide("break")) {
            p.sendMessage(ChatColor.DARK_GRAY + "----------------------------------------------------");
            p.sendMessage(ChatColor.YELLOW + modText.getJobDisplay("break", p.getUniqueId()).addVariables(_jobsdata.getName(p.getUniqueId()), p.getName(), ""));
            p.sendMessage("");

            this.buildMatTiers(_jobsdata.compJob().getMatTypeTiers("break"), p);
        }

        if(!_jobsdata.compJob().getMatTypeTiers("place").isEmpty() && !getHide("place")) {
            p.sendMessage(ChatColor.DARK_GRAY + "----------------------------------------------------");
            p.sendMessage(ChatColor.YELLOW + modText.getJobDisplay("place", p.getUniqueId()).addVariables(_jobsdata.getName(p.getUniqueId()), p.getName(), ""));
            p.sendMessage("");
            
            buildMatTiers(_jobsdata.compJob().getMatTypeTiers("place"), p);
        }
        
        if(!_jobsdata.compJob().getEntTypeTiers("defeat").isEmpty() && !getHide("defeat")) {
            p.sendMessage(ChatColor.DARK_GRAY + "----------------------------------------------------");
            p.sendMessage(ChatColor.YELLOW + modText.getJobDisplay("defeat", p.getUniqueId()).addVariables(_jobsdata.getName(p.getUniqueId()), p.getName(), ""));
            p.sendMessage("");
            
            buildEntTiers(_jobsdata.compJob().getEntTypeTiers("defeat"), p);

        }
        if(!_jobsdata.compJob().getMatTypeTiers("fishing").isEmpty() && !getHide("fishing")) {
            p.sendMessage(ChatColor.DARK_GRAY + "----------------------------------------------------");
            p.sendMessage(ChatColor.YELLOW + modText.getJobDisplay("fishing", p.getUniqueId()).addVariables(_jobsdata.getName(p.getUniqueId()), p.getName(), ""));
            p.sendMessage("");
            
            buildMatTiers(_jobsdata.compJob().getMatTypeTiers("fishing"), p);

        }
        if(!_jobsdata.compJob().getMatTypeTiers("craft").isEmpty() && !getHide("craft")) {
            p.sendMessage(ChatColor.DARK_GRAY + "----------------------------------------------------");
            p.sendMessage(ChatColor.YELLOW + modText.getJobDisplay("craft", p.getUniqueId()).addVariables(_jobsdata.getName(p.getUniqueId()), p.getName(), ""));
            p.sendMessage("");
            
            buildMatTiers(_jobsdata.compJob().getMatTypeTiers("craft"), p);
        }
        if(!_jobsdata.compJob().getMatTypeTiers("repair").isEmpty() && !getHide("repair")) {
            p.sendMessage(ChatColor.DARK_GRAY + "----------------------------------------------------");
            p.sendMessage(ChatColor.YELLOW + modText.getJobDisplay("repair", p.getUniqueId()).addVariables(_jobsdata.getName(p.getUniqueId()), p.getName(), ""));
            p.sendMessage("");
            
            buildMatTiers(_jobsdata.compJob().getMatTypeTiers("repair"), p);
        }
        if(!_jobsdata.compJob().getPotTypeTiers("potion").isEmpty() && !getHide("potion")) {
            p.sendMessage(ChatColor.DARK_GRAY + "----------------------------------------------------");
            p.sendMessage(ChatColor.YELLOW + modText.getJobDisplay("potion", p.getUniqueId()).addVariables(_jobsdata.getName(p.getUniqueId()), p.getName(), ""));
            p.sendMessage("");
            
            buildPotTiers(_jobsdata.compJob().getPotTypeTiers("potion"), p);
        }
        if(!_jobsdata.compJob().getEnchantTypeTiers("enchant").isEmpty() && !getHide("enchant")) {
            p.sendMessage(ChatColor.DARK_GRAY + "----------------------------------------------------");
            p.sendMessage(ChatColor.YELLOW + modText.getJobDisplay("enchant", p.getUniqueId()).addVariables(_jobsdata.getName(p.getUniqueId()), p.getName(), ""));
            p.sendMessage("");
            
            buildEnchantTiers(_jobsdata.compJob().getEnchantTypeTiers("enchant"), p);
        }
        
        if(!_jobsdata.compJob().getColorTiers("shear").isEmpty() && !getHide("shear")) {
            p.sendMessage(ChatColor.DARK_GRAY + "----------------------------------------------------");
            p.sendMessage(ChatColor.YELLOW + modText.getJobDisplay("shear", p.getUniqueId()).addVariables(_jobsdata.getName(p.getUniqueId()), p.getName(), ""));
            p.sendMessage("");
            
            buildColorTiers(_jobsdata.compJob().getColorTiers("shear"), p);
        }
        
        if(!_jobsdata.compJob().getPvpTiers("pvp").isEmpty() && !getHide("pvp")) {
            p.sendMessage(ChatColor.DARK_GRAY + "----------------------------------------------------");
            p.sendMessage(ChatColor.YELLOW + modText.getJobDisplay("pvp", p.getUniqueId()).addVariables(_jobsdata.getName(p.getUniqueId()), p.getName(), ""));
            p.sendMessage("");
            
            buildPvPTiers(_jobsdata.compJob().getPvpTiers("pvp"), p);
        }
        
        p.sendMessage("");
        if(PlayerData.hasJob(p.getUniqueId(), _jobsdata.getName().toLowerCase())) {
            if(Bukkit.getVersion().toLowerCase().contains("spigot"))
                SpigotBuilds.sendLeaveButton(_jobsdata.getName(p.getUniqueId()), p);
            else if(Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
                WorldEditBuilds.sendLeaveButton(_jobsdata.getName(p.getUniqueId()), p);
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobDisplay("button.leave", p.getUniqueId()).addVariables(_jobsdata.getName(p.getUniqueId()), p.getName(), "")));
            }
        } else if(PlayerData.isJoinable(p.getUniqueId(), _jobsdata.getName().toLowerCase())) {
            if(Bukkit.getVersion().toLowerCase().contains("spigot"))
                SpigotBuilds.sendJoinButton(_jobsdata.getName(p.getUniqueId()), p);
            else if(Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
                WorldEditBuilds.sendJoinButton(_jobsdata.getName(p.getUniqueId()), p);
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', McJobs.getPlugin().getLanguage().getJobDisplay("button.join", p.getUniqueId()).addVariables(_jobsdata.getName(p.getUniqueId()), p.getName(), "")));
            }
        }
    }
    
    private void buildMatTiers(HashMap<Integer, List<Material>> it, Player p){
        PrettyText text = new PrettyText();
        
        for(Map.Entry<Integer, List<Material>> me: it.entrySet()) {
            String data = null;
            String str = null;
            
            data = text.formatMaterialTiers(me.getValue(), ChatColor.DARK_GREEN, p.getUniqueId());
            str = ChatColor.GOLD + McJobs.getPlugin().getLanguage().getJobDisplay("tier", p.getUniqueId()).addVariables("", p.getName(), "") + me.getKey().toString() + ": " + ChatColor.DARK_GREEN + data + ChatColor.GRAY + "." + ChatColor.DARK_GREEN;
            
            text.formatPlayerText(str, p);
        }

    }

    private void buildEntTiers(HashMap<Integer, List<EntityType>> it, Player p) {
        PrettyText text = new PrettyText();
        
        for(Map.Entry<Integer, List<EntityType>> me: it.entrySet()) {
            String data = null;
            String str = null;
            
            data = text.formatEntityTiers(me.getValue(), ChatColor.DARK_GREEN, p.getUniqueId());
            str = ChatColor.GOLD + McJobs.getPlugin().getLanguage().getJobDisplay("tier", p.getUniqueId()).addVariables("", p.getName(), "") + me.getKey().toString() + ": " + ChatColor.DARK_GREEN + data + ChatColor.GRAY + "." + ChatColor.DARK_GREEN;
            
            text.formatPlayerText(str, p);
        }

    }

    private void buildPotTiers(HashMap<Integer, List<PotionTypeAdv>> it, Player p) {
        PrettyText text = new PrettyText();
        
        for(Map.Entry<Integer, List<PotionTypeAdv>> me: it.entrySet()) {
            String data = null;
            String str = null;
            
            data = text.formatPotionTiers(me.getValue(), ChatColor.DARK_GREEN, p.getUniqueId());
            str = ChatColor.GOLD + McJobs.getPlugin().getLanguage().getJobDisplay("tier", p.getUniqueId()).addVariables("", p.getName(), "") + me.getKey().toString() + ": " + ChatColor.DARK_GREEN + data + ChatColor.GRAY + "." + ChatColor.DARK_GREEN;
            
            text.formatPlayerText(str, p);
        }
    }

    private void buildEnchantTiers(HashMap<Integer, List<EnchantTypeAdv>> it, Player p) {
        PrettyText text = new PrettyText();
        
        for(Map.Entry<Integer, List<EnchantTypeAdv>> me: it.entrySet()) {
            String data = null;
            String str = null;
            
            data = text.formatEnchantTiers(me.getValue(), ChatColor.DARK_GREEN, p.getUniqueId());
            str = ChatColor.GOLD + McJobs.getPlugin().getLanguage().getJobDisplay("tier", p.getUniqueId()).addVariables("", p.getName(), "") + me.getKey().toString() + ": " + ChatColor.DARK_GREEN + data + ChatColor.GRAY + "." + ChatColor.DARK_GREEN;
            
            text.formatPlayerText(str, p);
        }

    }
    
    
    private void buildColorTiers(HashMap<Integer, List<DyeColor>> it, Player p) {
        PrettyText text = new PrettyText();
        
        for(Map.Entry<Integer, List<DyeColor>> me: it.entrySet()) {
            String data = null;
            String str = null;
            
            data = text.formatColorTiers(me.getValue(), ChatColor.DARK_GREEN, p.getUniqueId());
            str = ChatColor.GOLD + McJobs.getPlugin().getLanguage().getJobDisplay("tier", p.getUniqueId()).addVariables("", p.getName(), "") + me.getKey().toString() + ": " + ChatColor.DARK_GREEN + data + ChatColor.GRAY + "." + ChatColor.DARK_GREEN;
            
            text.formatPlayerText(str, p);
        }

    }
    
    
    private void buildPvPTiers(HashMap<Integer, Integer> it, Player p) {
        PrettyText text = new PrettyText();
        
        for(Map.Entry<Integer, Integer> me: it.entrySet()) {
            String data = null;
            String str = null;
            
            data = McJobs.getPlugin().getLanguage().getJobDisplay("pvptier", p.getUniqueId()).addVariables("", p.getName(), me.getValue().toString());  //ttext.formatEnchantTiers(me.getValue(), ChatColor.DARK_GREEN, p.getUniqueId());
            str = ChatColor.GOLD + McJobs.getPlugin().getLanguage().getJobDisplay("tier", p.getUniqueId()).addVariables("", p.getName(), "") + me.getKey().toString() + ": " + ChatColor.DARK_GREEN + data + ChatColor.GRAY + "." + ChatColor.DARK_GREEN;
            
            text.formatPlayerText(str, p);
        }

    }

    public Boolean getHide(String block) {
        if(block.equalsIgnoreCase("break"))
            return _jobsdata.getShow(0);
        else if(block.equalsIgnoreCase("place"))
            return _jobsdata.getShow(1);
        else if(block.equalsIgnoreCase("defeat"))
            return _jobsdata.getShow(2);
        else if(block.equalsIgnoreCase("craft"))
            return _jobsdata.getShow(3);
        else if(block.equalsIgnoreCase("repair"))
            return _jobsdata.getShow(4);
        else if(block.equalsIgnoreCase("fishing"))
            return _jobsdata.getShow(5);
        else if(block.equalsIgnoreCase("enchant"))
            return _jobsdata.getShow(6);
        else if(block.equalsIgnoreCase("potion"))
            return _jobsdata.getShow(7);
        else if(block.equalsIgnoreCase("shear"))
            return _jobsdata.getShow(8);
        else if(block.equalsIgnoreCase("pvp"))
            return _jobsdata.getShow(9);
        else return false;
    }
}
