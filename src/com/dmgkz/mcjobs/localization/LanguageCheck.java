/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.localization;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class LanguageCheck implements Runnable {
    private FileConfiguration _tmp = null;
    private Player _p;
    
    public LanguageCheck(Player p) {
        _p = p;
    }
    
    @Override
    public void run() {
        File fold = new File(McJobs.getPlugin().getDataFolder(), "languages");
        HashMap<String, FileConfiguration> tmp = new HashMap<>();
        tmp.putAll(McJobs.getPlugin().getLanguage().getLanguages());
        boolean changes = false;
        for(Map.Entry<String, FileConfiguration> me: tmp.entrySet()) {
            File f = new File(fold, me.getKey() + ".yml");
            if(!f.exists())
                return;
                
            _tmp = me.getValue();
            if(checkJobs("jobs")) {
                changes = true;
                save(f);
            }
            
            if(checkEnchants("enchant")) {
                changes = true;
                save(f);
            }
            
            if(checkPotions("potion")) {
                changes = true;
                save(f);
            }
            
            if(checkEntityTypes("entities")) {
                changes = true;
                save(f);
            }
            
            if(checkMaterials("materials")) {
                changes = true;
                save(f);
            }
            
            if(checkColors("color")) {
                changes = true;
                save(f);
            }
            
            if(checkLanguages("languages")) {
                changes = true;
                save(f);
            }
            
            if(checkOtherMessages()) {
                changes = true;
                save(f);
            }
            _tmp = null;
        }
        
        if(changes) {
            Bukkit.getScheduler().runTask(McJobs.getPlugin(), new reloadLanguage(_p));
            if(_p != null)
                _p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("language.reload", _p.getUniqueId()).addVariables("", _p.getName(), ""));
        } else {
            if(_p != null)
                _p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("language.finish", _p.getUniqueId()).addVariables("", _p.getName(), ""));
        }
    }
    
    private class reloadLanguage implements Runnable {
        private final Player _p;
    
        public reloadLanguage(Player p) {
            _p = p;
        }
        
        @Override
        public void run() {
            McJobs.getPlugin().reloadLanguages();
            if(_p != null)
                _p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminCommand("language.reloaded", _p.getUniqueId()).addVariables("", _p.getName(), ""));
        }
    }
    
    private void save(File f) {
        try {
            _tmp.save(f);
        } catch(IOException ex) {
            McJobs.getPlugin().getLogger().warning("Error on save Language File " + f.getName() + " after add missing components.");
        }
    }
    
    private boolean checkJobs(String section) {
       boolean hasChanged = false;
        for(String job: PlayerJobs.getJobsList().keySet()) {
            if(!_tmp.isString(section + ".name." + job)) {
                _tmp.set(section + ".name." + job, job);
                hasChanged = true;
            }
            if(!_tmp.isString(section + ".description." + job.toLowerCase())) {
                _tmp.set(section + ".description." + job.toLowerCase(), "Missing description for Job " + job);
                hasChanged = true;
            }
        }
        
        for(String rank: McJobs.getPlugin().getConfig().getConfigurationSection("ranks").getKeys(false)) {
            if(!_tmp.isString(section + ".rank." + rank.toLowerCase())) {
                _tmp.set(section + ".rank." + rank.toLowerCase(), rank);
                hasChanged = true;
            }
        }
        return hasChanged;
    }
    
    private boolean checkEnchants(String section) {
        boolean hasChanged = false;
        for(String ench: McJobs.getPlugin().getHolder().getEnchants().getEnchants().keySet()) {
            if(!_tmp.isString(section + "." + ench.toLowerCase())) {
                _tmp.set(section + "." + ench.toLowerCase(), ench.replace("_", " "));
                hasChanged = true;
            }
        }
        return hasChanged;
    }
    
    private boolean checkPotions(String section) {
        boolean hasChanged = false;
        for(String pta: McJobs.getPlugin().getHolder().getPotions().getPotions().keySet()) {
            if(!_tmp.isString(section + "." + pta.toLowerCase())) {
                _tmp.set(section + "." + pta.toLowerCase(), pta.replace("_", " "));
                hasChanged = true;
            }
        }
        return hasChanged;
    }
    
    private boolean checkEntityTypes(String section) {
        boolean hasChanged = false;
        for(EntityType et: EntityType.values()) {
            if(!_tmp.isString(section + "." + et.name().toLowerCase())) {
                _tmp.set(section + "." + et.name().toLowerCase(), et.name().replace("_", " "));
                hasChanged = true;
            }
        }
        return hasChanged;
    }
    
    private boolean checkMaterials(String section) {
        boolean hasChanged = false;
        for(Material mat: Material.values()) {
            if(!_tmp.isString(section + "." + mat.name().toLowerCase())) {
                _tmp.set(section + "." + mat.name().toLowerCase(), mat.name().replace("_", " "));
                hasChanged = true;
            }
        }
        return hasChanged;
    }
    
    private boolean checkColors(String section) {
        boolean hasChanged = false;
        for(DyeColor dc: DyeColor.values()) {
            if(!_tmp.isString(section + "." + dc.name().toLowerCase())) {
                _tmp.set(section + "." + dc.name().toLowerCase(), dc.name().replace("_", " "));
                hasChanged = true;
            }
        }
        return hasChanged;
    }
    
    private boolean checkLanguages(String section) {
        boolean hasChanged = false;
        for(String lang: McJobs.getPlugin().getLanguage().getAvaLangs()) {
            if(!_tmp.isString(section + "." + lang.toLowerCase())) {
                _tmp.set(section + "." + lang.toLowerCase(), lang);
                hasChanged = true;
            }
        }
        return hasChanged;
    }
    
    private boolean issetString(String str, String msg) {
        if(!_tmp.isString(str)) {
            _tmp.set(str, msg);
            return false;
        }
        return true;
    }
    
    private boolean issetInt(String str, int i) {
        if(!_tmp.isInt(str)) {
            _tmp.set(str, i);
            return false;
        }
        return true;
    }
    
    private boolean issetBoolean(String str, boolean b) {
        if(!_tmp.isBoolean(str)) {
            _tmp.set(str, b);
            return false;
        }
        return true;
    }
    
    private boolean checkOtherMessages() {
        HashMap<String, String> tmp = new HashMap<>();
        HashMap<String, Integer> tmpInt = new HashMap<>();
        HashMap<String, Boolean> tmpBol = new HashMap<>();
        
        tmp.put("jobscommand.playeronly", "Can only be done by a player!");
        tmp.put("jobscommand.permission", "&cYou do not have permission to do this!");
        tmp.put("jobscommand.args", "&cYou gave too many arguments!");
        tmp.put("jobscommand.exist", "&cThe job &6%j&c does not exist!");
        tmp.put("jobscommand.rejoin", "&7You can now rejoin the &6%j&7 job.");
        tmp.put("jobscommand.show", "&7Now &ashowing&7 pay messages for the &6%j&7 job.");
        tmp.put("jobscommand.hide", "&7Now &chiding&7 pay messages for the &6%j&7 job.");
        tmp.put("jobscommand.join", "&6You have learned the job %j successful.");
        tmp.put("jobscommand.nojob", "&cThe wished Job don''t exist.");
        tmp.put("jobscommand.donthave", "&cYou don''t have the job %j.");
        tmp.put("jobscommand.have", "&cYou have already the wished job.");
        tmp.put("jobscommand.language-header", "&ePress on the wished Language to change:");
        tmp.put("jobscommand.no-language", "&cCan't find the wished language &e%g&c.");
        tmp.put("jobscommand.language-changed", "&6Changed language successfull.");
        tmp.put("jobscommand.language-footer", "&eChange language with use /mcjobs language (language)");
        
        tmp.put("jobsnotify.and", "and");
        tmp.put("jobsnotify.hours", "hours");
        tmp.put("jobsnotify.hour", "hour");
        tmp.put("jobsnotify.minutes", "minutes");
        tmp.put("jobsnotify.minute", "minute");
        tmp.put("jobsnotify.days", "days");
        tmp.put("jobsnotify.day", "day");
        tmp.put("jobsnotify.weeks", "weeks");
        tmp.put("jobsnotify.week", "week");
        tmp.put("jobsnotify.months", "months");
        tmp.put("jobsnotify.month", "month");
        tmp.put("jobsnotify.message", "&aYour &6MC Jobs&a have earned you &6%j&a in the past &c%g&a.");
        tmp.put("jobsnotify.overpay", "&e%p&a, you are exhausted and cannot earn more from &6MC Jobs&a for awhile.");
        
        tmp.put("jobsdisplay.employed", "--   &cEmployed");
        tmp.put("jobsdisplay.unemployed", "-- &9Unemployed");
        tmp.put("jobsdisplay.default", "--    &3Default");
        tmp.put("jobsdisplay.basepay", "Base Pay");
        tmp.put("jobsdisplay.charge", "CHARGE");
        tmp.put("jobsdisplay.pay", "PAY");
        tmp.put("jobsdisplay.level", "LEVEL");
        tmp.put("jobsdisplay.rank", "RANK");
        tmp.put("jobsdisplay.exp", "XP");
        tmp.put("jobsdisplay.break", "BREAK");
        tmp.put("jobsdisplay.place", "PLACE");
        tmp.put("jobsdisplay.defeat", "DEFEAT");
        tmp.put("jobsdisplay.fishing", "FISHING");
        tmp.put("jobsdisplay.craft", "CRAFT");
        tmp.put("jobsdisplay.repair", "REPAIR");
        tmp.put("jobsdisplay.enchant", "ENCHANT");
        tmp.put("jobsdisplay.potion", "POTIONS");
        tmp.put("jobsdisplay.tier", "TIER");
        tmp.put("jobsdisplay.pvp", "PvP");
        tmp.put("jobsdisplay.shear", "SHEAR");
        tmp.put("jobsdisplay.pvptier", "Requires more than %g kills from same player");
        tmp.put("jobsdisplay.button.join", "&f[&eJoin &6%j&f]");
        tmp.put("jobsdisplay.button.leave", "&f[&cLeave &6%j&f]");
        tmp.put("jobsdisplay.button.info", "&f[%g%j&f]");
        tmp.put("jobsdisplay.button.language", "&f[&e%g&f]");
        
        tmp.put("jobsjoin.have", "You already have &6%j&c!");
        tmp.put("jobsjoin.join", "%p has joined &9%j&7.");
        tmp.put("jobsjoin.toomany", "&6%p&c you have too many jobs.  To join a new job:");
        tmp.put("jobsjoin.command", "/jobs leave [job]");
        tmp.put("jobsjoin.jobperm", "&6%p&7 You don''t have permission to join &9%j&7");
        tmp.put("jobsjoin.timer", "&7You cannot join &6%j&7 for another &c%g&7.");
        
        tmp.put("jobsleave.quit", "You have quit &6%j&7.");
        tmp.put("jobsleave.donthave", "You do not have the &6%j&c job.");
        tmp.put("jobsleave.leavedefault", "You cannot leave &6%j&c because it is a default job.");
        
        tmp.put("jobslist.available", "&e%p&2, available jobs are:");
        tmp.put("jobslist.jobsin", "Jobs in &cred&2 you already have.");
        tmp.put("jobslist.nojob", "Jobs in &8dark grey&2 are unavailable to you.");
        tmp.put("jobslist.defaultjob", "Jobs in &3dark aqua&2 are default jobs.");
        tmp.put("jobslist.specific", "To learn about a specific job type: &e/jobs [job name]");
        tmp.put("jobslist.jobs", "JOBS:");
        
        tmp.put("jobshelp.page", "PAGE");
        tmp.put("jobshelp.1.1", "&7   Welcome to &3MC Jobs&7.  &3MC Jobs&7 is a mod designed to allow you");
        tmp.put("jobshelp.1.2", "&7to earn money doing various activities on the server based");
        tmp.put("jobshelp.1.3", "&7upon the jobs you choose.");
        tmp.put("jobshelp.1.4", "");
        tmp.put("jobshelp.1.5", "&2   To get started you need to choose a job.  You can find");
        tmp.put("jobshelp.1.6", "&2what jobs are available by typing &e/jobs list &2into the chat box.");
        tmp.put("jobshelp.1.7", "");
        tmp.put("jobshelp.1.8", "&2Learn more about each job by typing into the chat box:");
        tmp.put("jobshelp.1.9", "&e/jobs [job_name]");
        tmp.put("jobshelp.1.10", "");
        tmp.put("jobshelp.1.11", "&e[job name] &2can be any job name, for example &6digger&2.");
        tmp.put("jobshelp.1.12", "&e/jobs join &2and&e leave &2are explained on the next page.");
        tmp.put("jobshelp.1.13", "--");
        tmp.put("jobshelp.2.1", "&2You can join a job by typing the command:");
        tmp.put("jobshelp.2.2", "&e/jobs join [job name]");
        tmp.put("jobshelp.2.3", "");
        tmp.put("jobshelp.2.4", "&2Joining a job will allow you to earn and lose money based upon");
        tmp.put("jobshelp.2.5", "&2the pay tables of each job.  You are only allowed to have a");
        tmp.put("jobshelp.2.6", "&2maximum of &c%j&2 jobs on this server.");
        tmp.put("jobshelp.2.7", "");
        tmp.put("jobshelp.2.8", "&2You can leave a job by entering &e/jobs leave [job name]");
        tmp.put("jobshelp.2.9", "&2Once you have the maximum number of jobs the server allows,");
        tmp.put("jobshelp.2.10", "&2the only way to choose new ones is to leave an existing job.");
        tmp.put("jobshelp.2.11", "");
        tmp.put("jobshelp.2.12", "&2To learn more about &e/jobs list&2 go to the next page.");
        tmp.put("jobshelp.2.13", "--");
        tmp.put("jobshelp.3.1", "&2   The list command at a quick glance gives useful");
        tmp.put("jobshelp.3.2", "&2information about what jobs are available to you and what");
        tmp.put("jobshelp.3.3", "&2jobs you cannot take.");
        tmp.put("jobshelp.3.4", "");
        tmp.put("jobshelp.3.5", "&2Jobs that you have are in &cred&2.  Jobs that you can take are in");
        tmp.put("jobshelp.3.6", "&6gold&2.  Jobs that are unavailable to you are in &8dark grey&2.");
        tmp.put("jobshelp.3.7", "");
        tmp.put("jobshelp.3.8", "&2Default jobs are in &3dark aqua&2.  They are jobs that are");
        tmp.put("jobshelp.3.9", "&2given to everyone logging into the server.  Only players with");
        tmp.put("jobshelp.3.10", "&2the &cmcjobs.admin.leavedefault&2 perm can quit &3default&2 jobs.");
        tmp.put("jobshelp.3.11", "");
        tmp.put("jobshelp.3.12", "&2Continue the &elist&2 command on the next page.");
        tmp.put("jobshelp.3.13", "--");
        tmp.put("jobshelp.4.1", "&2Lastly it tells you how many jobs you have versus the max");
        tmp.put("jobshelp.4.2", "&2jobs the server allows, which is &c%j&2 on this server.");
        tmp.put("jobshelp.4.3", "");
        tmp.put("jobshelp.4.4", "&2  The &e/jobs leave&2 command works the same as the &ejoin");
        tmp.put("jobshelp.4.5", "&2command.  You can only leave jobs that you have already");
        tmp.put("jobshelp.4.6", "&2joined.  This will allow you to pick up new jobs in case you");
        tmp.put("jobshelp.4.7", "&2change your mind on what you want to be.");
        tmp.put("jobshelp.4.8", "");
        tmp.put("jobshelp.4.9", "&2Once you leave a job you cannot rejoin it for a server");
        tmp.put("jobshelp.4.10", "&2specified time frame.  The default is 1 hour.");
        tmp.put("jobshelp.4.11", "");
        tmp.put("jobshelp.4.12", "&2To learn about &e/jobs [job name] continue to next page");
        tmp.put("jobshelp.4.13", "--");
        tmp.put("jobshelp.5.1", "&2  The &e/jobs [job name]&2 command gives the player all the");
        tmp.put("jobshelp.5.2", "&2information they need to know about each job.");
        tmp.put("jobshelp.5.3", "");
        tmp.put("jobshelp.5.4", "&2The first line starts with the &3jobs name&2 and is followed by");
        tmp.put("jobshelp.5.5", "&2whether you are &cemployed&2 by the job or whether you are");
        tmp.put("jobshelp.5.6", "&2not part of the job.  In which case it will say &9unemployed&2.");
        tmp.put("jobshelp.5.7", "&2Next it tells you the &abase pay&2 of the job.  This figure is how");
        tmp.put("jobshelp.5.8", "&2much money or xp &6TIER1&2 will pay you upon doing the &eaction&2.");
        tmp.put("jobshelp.5.9", "&2The last bit of information this line tells the player is whether");
        tmp.put("jobshelp.5.10", "&2the job &apays&2 or &ccharges&2 the player for completing &eactions&2.");
        tmp.put("jobshelp.5.11", "");
        tmp.put("jobshelp.5.12", "&2The next page will explain the subsequent lines of &e/jobs [job]");
        tmp.put("jobshelp.5.13", "--");
        tmp.put("jobshelp.6.1", "&2As said a job can both &apay&2 and &ccharge&2 the player.  As an");
        tmp.put("jobshelp.6.2", "&2example, the &6miner&2 job &apays&2 the player for breaking &7iron ore&2.");
        tmp.put("jobshelp.6.3", "&2However if you place &7iron ore&2 it will &ccharge&2 you money instead.");
        tmp.put("jobshelp.6.4", "");
        tmp.put("jobshelp.6.5", "&2If you are currently &cemployed&2 with the job it will show you");
        tmp.put("jobshelp.6.6", "&2your current &6level&2 and &6rank&2 in the job.");
        tmp.put("jobshelp.6.7", "&2The second line is the jobs &6description&2.");
        tmp.put("jobshelp.6.8", "");
        tmp.put("jobshelp.6.9", "&2After the dashes is what the jobs &eactions&2 are and what blocks");
        tmp.put("jobshelp.6.10", "&2or entities are used by the job.");
        tmp.put("jobshelp.6.11", "");
        tmp.put("jobshelp.6.12", "&2The next page will explain the job &eactions&2.");
        tmp.put("jobshelp.6.13", "--");
        tmp.put("jobshelp.7.1", "&2The actions are: &6BREAK&2, &6PLACE&2, &6DEFEAT&2, &6CRAFT&2, &6REPAIR&2,");
        tmp.put("jobshelp.7.2", "&6FISHING&2, &6POTIONS&2, &6ENCHANT&2, &6SHEAR and &6PVP&2.");
        tmp.put("jobshelp.7.3", "");
        tmp.put("jobshelp.7.4", "&6BREAK&2: Is the action of breaking blocks like &7dirt&2.");
        tmp.put("jobshelp.7.5", "&6PLACE&2: Is the action of placing blocks like &7stone stairs&2.");
        tmp.put("jobshelp.7.6", "&6DEFEAT&2: Is the action of killing monsters like &7spiders&2.");
        tmp.put("jobshelp.7.7", "&6CRAFT&2: Is the action of crafting items like &7wood sword&2.");
        tmp.put("jobshelp.7.8", "&6REPAIR&2: Is the action of repairing items like &7stone shovel&2.");
        tmp.put("jobshelp.7.9", "&2This covers both the &6mcMMO&2 repair and the vanilla repair.");
        tmp.put("jobshelp.7.10", "&6FISHING&2: &aPays&2 or &ccharges&2 whenever you use a fishing rod to");
        tmp.put("jobshelp.7.11", "&2catch an entity or fish.");
        tmp.put("jobshelp.7.12", "&6Potion&2 and &6Enchants&2 are covered on the next page.");
        tmp.put("jobshelp.7.13", "--");
        tmp.put("jobshelp.8.1", "&6POTIONS&2: &aPays&2 or &ccharges&2 for using a brew stand to make");
        tmp.put("jobshelp.8.2", "&2potions.");
        tmp.put("jobshelp.8.3", "&6ENCHANT&2: &aPays&2 or &ccharges&2 when the player enchants an item.");
        tmp.put("jobshelp.8.4", "&6SHEAR&2: &aPays&2 or &ccharges&2 when the player shear a sheep.");
        tmp.put("jobshelp.8.5", "&6PVP&2: &aPays&2 or &ccharges&2 when the player kills other player.");
        tmp.put("jobshelp.8.6", "");
        tmp.put("jobshelp.8.7", "");
        tmp.put("jobshelp.8.8", "");
        tmp.put("jobshelp.8.9", "&eEnglish&c localization done by: &6RathelmMC");
        tmp.put("jobshelp.8.10", "");
        tmp.put("jobshelp.8.11", "");
        tmp.put("jobshelp.8.12", "&7More info at: &6 https://dev.bukkit.org/projects/mcjobs");
        tmp.put("jobshelp.8.13", "--");
        
        tmp.put("jobshelp.continuepage", "&7Continue on Page %g");
        tmp.put("jobshelp.prevpage", "&f[&6Previos page&f]");
        tmp.put("jobshelp.nextpage", "&f[&6Next page&f]");
        tmp.put("jobshelp.command", "&6/jobs help %g");
        tmp.put("jobshelp.finish", "&7END OF HELP FILE");
        tmp.put("jobshelp.nohelp", "&7%g&c is not a help page!");
        
        tmp.put("admincommand.permission", "&6%p&c you do not have permission to do this!");
        tmp.put("admincommand.failedreload", "&cFailed to reload the plugin!");
        tmp.put("admincommand.succeedreload", "&6MC Jobs&c has been reloaded!");
        tmp.put("admincommand.defaults", "&7Defaults have been added to the config.yml file.");
        tmp.put("admincommand.args", "&cToo many arguments for &e/jadm&c.  So this is the end, we''re going to test a ridiculously long string.");
        tmp.put("admincommand.exist", "&6%j&c does not exist!");
        tmp.put("admincommand.missing-worldedit", "&cYou need WorldEdit for this command. Without WorldEdit you must set it manual to job file.");
        
        tmp.put("admincommand.language.start", "&eChecking async all language files. Please wait a moment.");
        tmp.put("admincommand.language.reload", "&6Language check has been finished. Reload languages files now sync.");
        tmp.put("admincommand.language.reloaded", "&6All languages files has been reloaded.");
        tmp.put("admincommand.language.finish", "&eFinished language file check. All is fine!");
        
        tmp.put("adminadd.args", "&cWrong arguments: &e/jadm add [player/group] [job_name]");
        tmp.put("adminadd.offline", "&6%p&c group doesn''t exist or isn''t a player that has been seen!");
        tmp.put("adminadd.novault", "&6%p&c is not a known player!");
        tmp.put("adminadd.hasjob", "&6%p&7 already has the &9%j&7 job.");
        tmp.put("adminadd.empty", "&7There are no &eplayers&7 online in the &6%p&7 group.");
        tmp.put("adminadd.added", "&6%p&7 has been added to &9%j&7.");
        tmp.put("adminadd.padded", "&6%p&2 you have been added to the &9%j&2 job by a server admin.");
        
        tmp.put("adminremove.args", "&cWrong arguments: &e/jadm remove [player/group] [job_name]");
        tmp.put("adminremove.nojob", "&6%p&7 does not have the &9%j&7 job.");
        tmp.put("adminremove.nodefault", "&6%p&c you do not have permission to remove &3default&c jobs!");
        tmp.put("adminremove.removed", "&6%p&7 has been removed from &9%j&7.");
        tmp.put("adminremove.premoved", "&6%p&2 you have been removed from the &9%j&2 job by a server admin.");
        
        tmp.put("adminlist.args", "&cWrong arguments: &e/jadm list [player/group]");
        tmp.put("adminlist.playerlist:", "&2%p&7: %g");
        tmp.put("adminlist.nojobs", "&2%p&c has no jobs.");
        tmp.put("adminlist.wrongpage", "&e%g is not a proper page number.  Using page 1 instead.");
        
        tmp.put("adminregion.args", "&cMissing arguments. Please use &e/mcjobs (set/remove) (jobname)");
        tmp.put("adminregion.no-worldedit", "&cCan't find WorldEdit.");
        tmp.put("adminregion.no-selection", "&cYou must select a region with toe WorldEdit wand tool.");
        tmp.put("adminregion.set", "&6Region set for Job &e%j&6.");
        tmp.put("adminregion.error", "&cCan't save &e%j&6 after Region set.");
        tmp.put("adminregion.removed", "&6Cleared the %j region.");
        
        tmp.put("adminregion.no-permission", "&cYou don't have the rihght permission.");
        tmp.put("adminregion.missing-args", "&cPlease check the wiki to how work this command.");
        tmp.put("adminregion.already-begun", "&cYou have already a open builder.");
        tmp.put("adminregion.missing-job", "&cMissing job as argument.");
        tmp.put("adminregion.wrong-language", "&cCan't find the language.");
        tmp.put("adminregion.job-not-found", "&cCan't find the job.");
        tmp.put("adminregion.non-begun", "&cNo open Build found. Please open frist one.");
        tmp.put("adminregion.missing-text", "&cOh noo a crocodile has eat the Text.");
        tmp.put("adminregion.no-open", "&cI'm really sorry, but there isn't a open builder for you.");
        tmp.put("adminregion.success", "&e%g &6was successful.");
        tmp.put("adminregion.first-message", "&cPlease add first a message before you do this.");
        tmp.put("adminregion.missing-hover", "&cHoly, where is the displayed text for the Hover?!");
        tmp.put("adminregion.missing-click", "&cWhat you want to run on click the last message?");
        tmp.put("adminregion.wrong-click-type", "&cPlease use one of the follow types: &e%g");
        tmp.put("adminregion.save-error", "&cWArning - It's happend an error on save the build.");
        tmp.put("adminregion.cleared", "&eBuilder is cleared. You can begin it new now.");
        tmp.put("adminregion.not-forget", "&cPlease don't forget to save on end.");
       
        tmp.put("adminentity.no-permission", "&cYou don't have the rihght permission.");
        tmp.put("adminentity.missing-args", "&cPlease check the wiki to how work this command.");
        tmp.put("adminentity.already-begun", "&cYou have already a open builder.");
        tmp.put("adminentity.missing-job", "&cMissing job as argument.");
        tmp.put("adminentity.wrong-language", "&cCan't find the language.");
        tmp.put("adminentity.job-not-found", "&cCan't find the job.");
        tmp.put("adminentity.non-begun", "&cNo open Build found. Please open frist one.");
        tmp.put("adminentity.missing-text", "&cOh noo a crocodile has eat the Text.");
        tmp.put("adminentity.no-open", "&cI'm really sorry, but there isn't a open builder for you.");
        tmp.put("adminentity.success", "&e%g &6was successful.");
        tmp.put("adminentity.first-message", "&cPlease add first a message before you do this.");
        tmp.put("adminentity.missing-hover", "&cHoly, where is the displayed text for the Hover?!");
        tmp.put("adminentity.missing-click", "&cWhat you want to run on click the last message?");
        tmp.put("adminentity.wrong-click-type", "&cPlease use one of the follow types: &e%g");
        tmp.put("adminentity.save-error", "&cWArning - It's happend an error on save the build.");
        tmp.put("adminentity.cleared", "&eBuilder is cleared. You can begin it new now.");
        tmp.put("adminentity.not-forget", "&cPlease don't forget to save on end.");
        
        tmp.put("pitch.line0", "This server runs:");
        tmp.put("pitch.line1", "&aTo see what jobs are available type. &e/jobs list");
        tmp.put("pitch.line2", "&aTo join a job type. &e/jobs join [job_name]");
        tmp.put("pitch.line3", "&aPlay, &2earn money&a, and have fun!");
        
        tmp.put("onadminlogin.toolow", "&eYour pay scale is too low for an &6XP&e based economy.  Consider switching &2pay_scale&e to high in the config.yml file.");
        tmp.put("onadminlogin.outofdate", "&eConfig.yml is out of date.  &6MC Jobs&e may not work properly without reloading the config file.");
        
        tmp.put("experience.level", "&eYou are now level &6%g&e in &6%j&e.");
        tmp.put("experience.reset", "&eYour level in &6%j&e has been reset to &c0&e.");
        tmp.put("experience.rank", "&eYou are now rank &6%g&e in &6%j&e.");
        tmp.put("experience.added_lvl", "&7You have given &6%p &a%g&7 levels in &6%j&7.");
        tmp.put("experience.padded_lvl", "&7You have been given &a%g&7 levels in &6%j&7 by a system admin.");
        tmp.put("experience.added_xp", "&7You have given &6%p &a%g&7 experience in &6%j&7.");
        tmp.put("experience.padded_xp", "&7You have been given &a%g&7 experience in &6%j&7 by a system admin.");
        tmp.put("experience.nojob", "&6%p&c doesn''t have the &6%j&c job.");
        
        tmp.put("payment.pay", "&aThe &6%j&a job has paid you &6%g&a %p.");
        tmp.put("payment.payxp", "&aThe &6%j&a job has earned you &6%g&a experience.");
        tmp.put("payment.charge", "&aThe &6%j&a job has cost you &c%g&a %p.");
        tmp.put("payment.chargexp", "&aThe &6%j&a job has taken &c%g&a experience.");
        tmp.put("payment.currency_single", "dollar");
        tmp.put("payment.currency_plural", "dollars");
        
        tmp.put("languages.en", "English");
        
        tmp.put("scoreboard.header", "&b&r| &aMcJobs &bStats &b&r|");
        tmp.put("scoreboard.order", "&aSet the new order successfull.");
        tmp.put("scoreboard.sort", "&aSet the new sort successfull.");
        tmp.put("scoreboard.none", "&aThe scoreboard will be hide now.");
        tmp.put("scoreboard.info.1.message", "&cThe follow parameters are available:");
        tmpBol.put("scoreboard.info.1.break", true);
        tmp.put("scoreboard.info.2.message","For the order:");
        tmpBol.put("scoreboard.info.2.break", true);
        tmp.put("scoreboard.info.3.message", "&ejob");
        tmp.put("scoreboard.info.3.hover-msg", "&aOrder the McJobs Scoreboard by the Jobname.");
        tmp.put("scoreboard.info.3.click-type", "run_command");
        tmp.put("scoreboard.info.3.click-msg", "/mcjobs scoreboard job");
        tmp.put("scoreboard.info.4.message", "&f, ");
        tmp.put("scoreboard.info.5.message", "&erank");
        tmp.put("scoreboard.info.5.hover-msg", "&aOrder the McJobs Scoreboard by the Job rank.");
        tmp.put("scoreboard.info.5.click-type", "run_command");
        tmp.put("scoreboard.info.5.click-msg", "/mcjobs scoreboard rank");
        tmp.put("scoreboard.info.6.message", "&f, ");
        tmp.put("scoreboard.info.7.message", "&elevel");
        tmp.put("scoreboard.info.7.hover-msg", "&aOrder the McJobs Scoreboard by the Job level.");
        tmp.put("scoreboard.info.7.click-type", "run_command");
        tmp.put("scoreboard.info.7.click-msg", "/mcjobs scoreboard level");
        tmp.put("scoreboard.info.8.message", "&f, ");
        tmp.put("scoreboard.info.9.message", "&ehasexp");
        tmp.put("scoreboard.info.9.hover-msg", "&aOrder the McJobs Scoreboard by the Exp of the job.");
        tmp.put("scoreboard.info.9.click-type", "run_command");
        tmp.put("scoreboard.info.9.click-msg", "/mcjobs scoreboard hasexp");
        tmp.put("scoreboard.info.10.message", "&f, ");
        tmp.put("scoreboard.info.11.message", "&eneedexp");
        tmp.put("scoreboard.info.11.hover-msg", "&aOrder the McJobs Scoreboard by the missing Exp to the next level.");
        tmp.put("scoreboard.info.11.click-type", "run_command");
        tmp.put("scoreboard.info.11.click-msg", "/mcjobs scoreboard needexp");
        tmp.put("scoreboard.info.12.message", "&f, ");
        tmp.put("scoreboard.info.13.message", "&enextexp");
        tmp.put("scoreboard.info.13.hover-msg", "&aOrder the McJobs Scoreboard by the Exp that need the next level.");
        tmp.put("scoreboard.info.13.click-type", "run_command");
        tmp.put("scoreboard.info.13.click-msg", "/mcjobs scoreboard nextexp");
        tmpBol.put("scoreboard.info.13.break", true);
        tmp.put("scoreboard.info.14.message","For the sort:");
        tmpBol.put("scoreboard.info.14.break", true);
        tmp.put("scoreboard.info.15.message", "&easc");
        tmp.put("scoreboard.info.15.hover-msg", "&aSort the McJobs Scoreboard from 0 to 9/A to Z.");
        tmp.put("scoreboard.info.15.click-type", "run_command");
        tmp.put("scoreboard.info.15.click-msg", "/mcjobs scoreboard asc");
        tmp.put("scoreboard.info.16.message", "&f, ");
        tmp.put("scoreboard.info.17.message", "&edesc");
        tmp.put("scoreboard.info.17.hover-msg", "&aSort the McJobs Scoreboard from 9 to 0/Z to A.");
        tmp.put("scoreboard.info.17.click-type", "run_command");
        tmp.put("scoreboard.info.17.click-msg", "/mcjobs scoreboard desc");
        tmpBol.put("scoreboard.info.17.break", true);
        tmp.put("scoreboard.info.18.message", "&bTo Hide the Scoreboard use &enone &f, &bto reactivate set a order again.");
        tmp.put("scoreboard.info.18.hover-msg", "&aDeactivate the Scoreboard");
        tmp.put("scoreboard.info.18.click-type", "run_command");
        tmp.put("scoreboard.info.18.click-msg", "/mcjobs scoreboard none");
        
        tmpInt.put("spaces.jobslist", 16);
        tmpInt.put("spaces.display", 4);
        tmpInt.put("spaces.displaytwo", 8);
        tmpInt.put("spaces.displaythree", 4);
        tmpInt.put("spaces.chargelen", 7);
        tmpInt.put("spaces.numhelp" , 8);
        
        boolean hasChanges = false;
        for(Map.Entry<String, String> me: tmp.entrySet()) {
            if(!issetString(me.getKey(), me.getValue())) 
                hasChanges = true;
        }
        
        for(Map.Entry<String, Integer> me: tmpInt.entrySet()) {
            if(!issetInt(me.getKey(), me.getValue())) 
                hasChanges = true;
        }
        
        for(Map.Entry<String, Boolean> me: tmpBol.entrySet()) {
            if(!issetBoolean(me.getKey(), me.getValue())) 
                hasChanges = true;
        }
        
        return hasChanges;
    }
}
