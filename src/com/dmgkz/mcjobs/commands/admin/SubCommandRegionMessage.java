/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.commands.admin;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public class SubCommandRegionMessage {
    private static final Map<UUID, FileConfiguration> _users = new HashMap<>();
    private static final Map<UUID, String> _jobs = new HashMap<>();
    private static final Map<UUID, String> _language = new HashMap<>();
    /*
    *        0        1       2      3
    * /jadm rmsg    begin   jobname language
    * /jadm rmsg    add     Text
    * /jadm rmsg    break                   - Set New Line
    * /jadm rmsg    hover   type    Text    - Set Hover text to last add
    * /jadm rmsg    click   type    doing   - Set Click action to last add
    * /jadm rmsg    save                    - Save the Msg and clear the open message
    * /jadm rmsg    remove  jobname
    */
    public static void command(Player p, String[] a) {
        if(!p.hasPermission("mcjobs.admin.region.message")) {
            p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminRegion("no-permission", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        if(a.length < 2) {
            p.sendMessage(ChatColor.GRAY + McJobs.getPlugin().getLanguage().getAdminRegion("missing-args", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        String subCmd = a[1];
        List<String> tmp = new ArrayList<>();
        tmp.addAll(Arrays.asList(a));
        tmp.remove(0);
        tmp.remove(0);
        String[] arg = new String[tmp.size()];
        arg = tmp.toArray(arg);
        
        switch(subCmd.toLowerCase()) {
            case "begin":
                useCommandBegin(arg, p);
                break;
            case "add":
                useCommandAdd(arg, p);
                break;
            case "break":
                useCommandBreak(arg, p);
                break;
            case "hover":
                useCommandHover(arg, p);
                break;
            case "click":
                useCommandClick(arg, p);
                break;
            case "save":
                useCommandSave(arg, p);
                break;
            case "remove":
                useCommandRemove(arg, p);
                break;
            case "clear":
                useCommandClear(arg, p);
                break;
            case "set":
                // /jadm rmsg set (jobname) (language) Text
                //Add Text to ArrayList Message
                useCommandSet(arg, p);
                break;
            case "delete":
                // /jadm rmsg delete (jobname) (language)
                useCommandDelete(arg, p);
                break;
            default:
                
                return;
        }
    }
    
    private static void useCommandBegin(String[] a, Player p) {
        //jadm rmsg begin jobname
        if(_users.containsKey(p.getUniqueId()) || _jobs.containsKey(p.getUniqueId())) {
            //Already one open
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("already-begun", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        if(a.length < 1) {
            //No job given
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("missing-job", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        String language = "";
        if(a.length >= 2) {
            String langOriginal = McJobs.getPlugin().getLanguage().getOriginalLanguageName(a[1], p.getUniqueId());
            if(!McJobs.getPlugin().getLanguage().getAvaLangs().contains(langOriginal)) {
                p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("wrong-language", p.getUniqueId()).addVariables("", p.getName(), ""));
                return;
            }
            
            language = langOriginal;
        }
        
        String jobOriginal = McJobs.getPlugin().getLanguage().getOriginalJobName(a[0].toLowerCase(), p.getUniqueId()).toLowerCase();
        if(!PlayerJobs.getJobsList().containsKey(jobOriginal)) {
            //Job not exist,
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("job-not-found", p.getUniqueId()).addVariables(jobOriginal, p.getName(), ""));
            return;
        }
        
        File f = getJobFile(jobOriginal);
        if(f == null) {
            //No job file found
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("job-file-not-found", p.getUniqueId()).addVariables(jobOriginal, p.getName(), ""));
            return;
        }

        _users.put(p.getUniqueId(), YamlConfiguration.loadConfiguration(f));
        _jobs.put(p.getUniqueId(), jobOriginal);
        if(!language.isEmpty())
            _language.put(p.getUniqueId(), language);
        p.sendMessage(ChatColor.GREEN + McJobs.getPlugin().getLanguage().getAdminRegion("success", p.getUniqueId()).addVariables(jobOriginal, p.getName(), "begin"));
    }
    
    /*
    * Status OK
    */
    private static void useCommandAdd(String[] a, Player p) {
        //jadm rmsg add Text here
        if(!_users.containsKey(p.getUniqueId()) || !_jobs.containsKey(p.getUniqueId())) {
            //&cCan't find an open build
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("non-begun", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        if(a.length < 1) {
            //&cMissing a message for the next line.
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("missing-text", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        int lastLine = getLastLine(p);
        if(lastLine == -1) {
            //&cCheck if you have start a build.
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("no-open", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        String strBuild = "";
        for(String str: a) {
            if(!strBuild.isEmpty())
                strBuild += " ";
            strBuild += str;
        }
        
        strBuild = strBuild.replace("[space]", " ").replace("[empty]", "");
        if(setAndSave(p, "job-info-zone." + (_language.containsKey(p.getUniqueId())?_language.get(p.getUniqueId()) + ".":"") + "component." + (lastLine+1) + ".message", strBuild)) {
            p.sendMessage(ChatColor.GREEN + McJobs.getPlugin().getLanguage().getAdminRegion("success", p.getUniqueId()).addVariables("", p.getName(), "add"));
        }
    }
    
    /*
    * Status OK
    */
    private static void useCommandBreak(String[] a, Player p) {
        //jadm rmsg break - Set New Line
        if(!_users.containsKey(p.getUniqueId()) || !_jobs.containsKey(p.getUniqueId())) {
            //&cCan't find an open build
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("non-begun", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        int lastLine = getLastLine(p);
        if(lastLine == -1) {
            //&cCheck if you have start a build.
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("no-open", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        if(lastLine == 0) {
            //&cPlease add frist a message.
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("first-message", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        if(setAndSave(p, "job-info-zone." + (_language.containsKey(p.getUniqueId())?_language.get(p.getUniqueId()) + ".":"") + "component." + lastLine + ".break", true)) {
            p.sendMessage(ChatColor.GREEN + McJobs.getPlugin().getLanguage().getAdminRegion("success", p.getUniqueId()).addVariables("", p.getName(), "break"));
        }
    }
    
    /*
    * Status OK
    */
    private static void useCommandHover(String[] a, Player p) {
        //jadm rmsg hover Text - Set Hover text to last add
        if(!_users.containsKey(p.getUniqueId()) || !_jobs.containsKey(p.getUniqueId())) {
            //&cCan't find an open build
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("non-begun", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        if(a.length == 0) {
            //No text
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("missing-hover", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        int lastLine = getLastLine(p);
        if(lastLine == -1) {
            //&cCheck if you have start a build.
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("no-open", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        if(lastLine == 0) {
            //&cPlease add frist a message.
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("first-message", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        String strBuild = "";
        for(String str: a) {
            if(!strBuild.isEmpty())
                strBuild += " ";
            strBuild += str;
        }
        
        strBuild = strBuild.replace("[space]", " ").replace("[empty]", "");
        if(setAndSave(p, "job-info-zone." + (_language.containsKey(p.getUniqueId())?_language.get(p.getUniqueId()) + ".":"") + "component." + lastLine + ".hover-msg", strBuild)) {
            p.sendMessage(ChatColor.GREEN + McJobs.getPlugin().getLanguage().getAdminRegion("success", p.getUniqueId()).addVariables("", p.getName(), "hover"));
        }
    }
    
    /*
    * Status OK
    */
    private static void useCommandClick(String[] a, Player p) {
        //jadm rmsg click type doing - Set Click action to last add
        if(!_users.containsKey(p.getUniqueId()) || !_jobs.containsKey(p.getUniqueId())) {
            //&cCan't find an open build
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("non-begun", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        if(a.length < 2) {
            //No text
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("missing-click", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        if(!a[0].equalsIgnoreCase("change_page") && !a[0].equalsIgnoreCase("open_file") && !a[0].equalsIgnoreCase("open_url") && !a[0].equalsIgnoreCase("run_command") && !a[0].equalsIgnoreCase("suggest_command")) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("wrong-click-type", p.getUniqueId()).addVariables("", p.getName(), "change_page, open_file, open_url, run_command, suggest_command"));
            return;
        }
        
        int lastLine = getLastLine(p);
        if(lastLine == -1) {
            //&cCheck if you have start a build.
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("no-open", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        if(lastLine == 0) {
            //&cPlease add frist a message.
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("first-message", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        String strBuild = "";
        for(int i = 1; i < a.length; i++) {
            if(!strBuild.isEmpty())
                strBuild += " ";
            strBuild += a[i];
        }
        
        strBuild = strBuild.replace("[space]", " ").replace("[empty]", "");
        boolean s1 = setAndSave(p, "job-info-zone." + (_language.containsKey(p.getUniqueId())?_language.get(p.getUniqueId()) + ".":"") + "component." + lastLine + ".click-msg", strBuild);
        boolean s2 = setAndSave(p, "job-info-zone." + (_language.containsKey(p.getUniqueId())?_language.get(p.getUniqueId()) + ".":"") + "component." + lastLine + ".click-type", a[0].toLowerCase());
        if(s1 && s2)
            p.sendMessage(ChatColor.GREEN + McJobs.getPlugin().getLanguage().getAdminRegion("success", p.getUniqueId()).addVariables("", p.getName(), "click"));
    }
    
    /*
    * Status OK
    */
    private static void useCommandSave(String[] a, Player p) {
        //jadm rmsg save - Save the Msg and clear the open message
        if(!_users.containsKey(p.getUniqueId()) || !_jobs.containsKey(p.getUniqueId())) {
            //&cCan't find an open build
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("non-begun", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        if(setAndSave(p, null)) {
            File f = getJobFile(_jobs.get(p.getUniqueId()));
            if(f == null) {
                //No job file found
                p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("job-file-not-found", p.getUniqueId()).addVariables(_jobs.get(p.getUniqueId()), p.getName(), ""));
                return;
            }

            FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
            String lang = _language.containsKey(p.getUniqueId())?_language.get(p.getUniqueId()):"default";
            PlayerJobs.getJobsList().get(_jobs.get(p.getUniqueId())).getData().setRegionMessage(lang, fc.getConfigurationSection("job-info-zone." + lang + ".component"));
            p.sendMessage(ChatColor.GREEN + McJobs.getPlugin().getLanguage().getAdminRegion("success", p.getUniqueId()).addVariables("", p.getName(), "save"));
        } else {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("save-error", p.getUniqueId()).addVariables("", p.getName(), ""));
        }
    }
    
    /*
    * Status OK
    */
    private static void useCommandRemove(String[] a, Player p) {
        //jadm rmsg remove jobname (language)
        if(a.length < 1) {
            //No job given
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("missing-job", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        String language = "";
        if(a.length >= 2) {
            String langOriginal = McJobs.getPlugin().getLanguage().getOriginalLanguageName(a[1], p.getUniqueId());
            if(!McJobs.getPlugin().getLanguage().getAvaLangs().contains(langOriginal)) {
                p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("wrong-language", p.getUniqueId()).addVariables("", p.getName(), ""));
                return;
            }
            
            language = langOriginal;
        }
        
        String jobOriginal = McJobs.getPlugin().getLanguage().getOriginalJobName(a[0].toLowerCase(), p.getUniqueId()).toLowerCase();
        if(!PlayerJobs.getJobsList().containsKey(jobOriginal)) {
            //Job not exist,
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("job-not-found", p.getUniqueId()).addVariables(jobOriginal, p.getName(), ""));
            return;
        }
        
        File f = getJobFile(jobOriginal);
        if(f == null) {
            //No job file found
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("job-file-not-found", p.getUniqueId()).addVariables(jobOriginal, p.getName(), ""));
            return;
        }
        
        FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
        try {
            fc.set("job-info-zone." + (!language.isEmpty()?language:"component"), null);
            fc.save(f);
            PlayerJobs.getJobsList().get(jobOriginal).getData().removeRegionMessage(!language.isEmpty()?language:"default");
            p.sendMessage(ChatColor.GREEN + McJobs.getPlugin().getLanguage().getAdminRegion("success", p.getUniqueId()).addVariables("", p.getName(), "remove"));
        } catch(Exception ex) {
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("ohoh", p.getUniqueId()).addVariables("", p.getName(), ""));
        }
    }
    
    /*
    * Status OK
    */
    private static void useCommandClear(String[] a, Player p) {
        //jadm rmsg clear
        if(_users.remove(p.getUniqueId()) != null && !_jobs.remove(p.getUniqueId()).isEmpty()) {
           //&cCan't find an open build
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("cleared", p.getUniqueId()).addVariables("", p.getName(), "clear"));
        } else {
            //&cCan't find an open build
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("non-begun", p.getUniqueId()).addVariables("", p.getName(), ""));
        }
    }
    
    private static void useCommandSet(String[] a, Player p) {
        // /jadm rmsg set (jobname) (language) Text
        if(a.length < 2) {
            //&cMissing a message for the next line.
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("missing-text", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        int txtStart = 1;
        String lang = "default";
        String langOriginal = McJobs.getPlugin().getLanguage().getOriginalLanguageName(a[1], p.getUniqueId());
        if(McJobs.getPlugin().getLanguage().isLang(langOriginal)) {
            lang = langOriginal;
            txtStart++;
        }
        
        String strBuild = "";
        for(int i = txtStart; i < a.length; i++) {
            if(!strBuild.isEmpty())
                strBuild += " ";
            strBuild += a[i];
        }
        
        strBuild = strBuild.replace("[space]", " ").replace("[empty]", "");
        if(addAndSave(p, "job-info-zone.message." + lang, strBuild)) {
            p.sendMessage(ChatColor.GREEN + McJobs.getPlugin().getLanguage().getAdminRegion("success", p.getUniqueId()).addVariables(lang, p.getName(), "set"));
        }
    }
    
    private static void useCommandDelete(String[] a, Player p) {
        // /jadm rmsg set (jobname) (language)
        if(a.length < 2) {
            //&cMissing a message for the next line.
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("missing-text", p.getUniqueId()).addVariables("", p.getName(), ""));
            return;
        }
        
        String lang = "default";
        String langOriginal = McJobs.getPlugin().getLanguage().getOriginalLanguageName(a[1], p.getUniqueId());
        if(McJobs.getPlugin().getLanguage().isLang(langOriginal)) {
            lang = langOriginal;
        }
        
        if(setAndSave(p, "job-info-zone.message." + lang, null)) {
            p.sendMessage(ChatColor.GREEN + McJobs.getPlugin().getLanguage().getAdminRegion("success", p.getUniqueId()).addVariables(lang, p.getName(), "delete"));
        }
    }
    
    private static int getLastLine(Player p) {
         if(!_users.containsKey(p.getUniqueId()) || !_jobs.containsKey(p.getUniqueId())) {
            //No open builder
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("already-begun", p.getUniqueId()).addVariables("", p.getName(), ""));
            return -1;
        }
        
        FileConfiguration fc = _users.get(p.getUniqueId());
        if(!fc.isConfigurationSection("job-info-zone." + (_language.containsKey(p.getUniqueId())?_language.get(p.getUniqueId()) + ".":"") + "component")) {
            return 0;
        }
        
        int last = 0;
        for(String k: fc.getConfigurationSection("job-info-zone." + (_language.containsKey(p.getUniqueId())?_language.get(p.getUniqueId()) + ".":"") + "component").getKeys(false)) {
            try {
                int i = Integer.parseInt(k);
                if(i > last)
                    last = i;
            } catch(Exception ex) {}
        }
        return last;
    }
    
    private static File getJobFile(String jobName) {
        File fold = new File(McJobs.getPlugin().getDataFolder(), "jobs");
        if(!fold.isDirectory()) {
            //No job folder exist oO
            return null;
        }
        
        File f = new File(fold, jobName + ".yml");
        if(!f.exists()) {
            //Job file not found.
            return null;
        }
        return f;
    }
    
    private static boolean setAndSave(Player p, String path, Object var) {
        File f = getJobFile(_jobs.get(p.getUniqueId()));
        if(f == null) {
            //Internal Error
            return false;
        }
        
        FileConfiguration fc = _users.get(p.getUniqueId());
        if(var != null)
            fc.set(path, var);
        
        try {
            fc.save(f);
            return true;
        } catch(Exception ex) {
            //&cDon't forget to run /mcjobs rmsg save on end.
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("not-forget", p.getUniqueId()).addVariables("", p.getName(), ""));
        }
        return false;
    }
    
    private static boolean setAndSave(Player p, HashMap<String, Object> obj) {
        File f = getJobFile(_jobs.get(p.getUniqueId()));
        if(f == null) {
            //Internal Error
            return false;
        }
        
        FileConfiguration fc = _users.get(p.getUniqueId());
        if(obj != null) {
            for(Map.Entry<String, Object> me: obj.entrySet())
                fc.set(me.getKey(), me.getValue());
        }
    
        try {
            fc.save(f);
            return true;
        } catch(Exception ex) {
            //&cDon't forget to run /mcjobs rmsg save on end.
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("not-forget", p.getUniqueId()).addVariables("", p.getName(), ""));
        }
        return false;
    }
    
    private static boolean addAndSave(Player p, String path, String msg) {
        File f = getJobFile(_jobs.get(p.getUniqueId()));
        if(f == null) {
            //Internal Error
            return false;
        }
        
        FileConfiguration fc = _users.get(p.getUniqueId());
        if(fc.isString(path))
            msg = fc.getString(path) + msg;
        fc.set(path, msg);
        
        try {
            fc.save(f);
            return true;
        } catch(Exception ex) {
            //&cDon't forget to run /mcjobs rmsg save on end.
            p.sendMessage(ChatColor.RED + McJobs.getPlugin().getLanguage().getAdminRegion("not-forget", p.getUniqueId()).addVariables("", p.getName(), ""));
        }
        return false;
    }
}
