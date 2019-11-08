package com.dmgkz.mcjobs.playerjobs.pay;


import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.levels.Leveler;
import com.dmgkz.mcjobs.prettytext.PrettyText;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PayMoney {
    private static double _maxPay = -1;
    private static final DecimalFormat _df = new DecimalFormat("#,##0.0#");
    private static final HashMap<UUID, Double> _payCache = new HashMap<>();
    
    public static double getPayCache(UUID uuid) {
        if(!_payCache.containsKey(uuid))
            return 0.0d;
        return _payCache.get(uuid);
    }
    
    public static String getPayCacheDisplay(UUID uuid) {
        return _df.format(getPayCache(uuid));
    }
    
    public static String payVault(Player play, int tier, double basepay, String job){
        double payAmount = 0.0;
        double iTimes = tier;
        double totalPay = PlayerData.getEarnedIncome(play.getUniqueId());
        
        job = job.toLowerCase();

        String sSingCur = "";
        String sPlurCur = "";
        String str = "";
        
        payAmount = basepay * iTimes * Leveler.getMultiplier(PlayerData.getJobLevel(play.getUniqueId(), job));
         
        if(overLimit(payAmount, totalPay, play))
            return str;

        if(totalPay + payAmount >= _maxPay && _maxPay > 0)
            payAmount = _maxPay - totalPay + 1;

        totalPay = totalPay + payAmount;
        PlayerData.setEarnedIncome(play.getUniqueId(), totalPay);

        if(_payCache.containsKey(play.getUniqueId())) {
            double temp = _payCache.get(play.getUniqueId());
            temp = temp + payAmount;
            _payCache.put(play.getUniqueId(), temp);
        } else
            _payCache.put(play.getUniqueId(), payAmount);
        
//        McJobs.getEconomy().depositPlayer(play.getName(), payAmount);
        
        if(!McJobs.getEconomy().currencyNameSingular().equals(""))
            sSingCur = McJobs.getEconomy().currencyNameSingular();
        else
            sSingCur = McJobs.getPlugin().getLanguage().getPayment("currency_single", play.getUniqueId()).addVariables("", "", "");

        if(!McJobs.getEconomy().currencyNamePlural().equals(""))
            sPlurCur = McJobs.getEconomy().currencyNamePlural();
        else
            sPlurCur = McJobs.getPlugin().getLanguage().getPayment("currency_plural", play.getUniqueId()).addVariables("", "", "");

        if(payAmount == 1){
                str = ChatColor.GREEN + McJobs.getPlugin().getLanguage().getPayment("pay", play.getUniqueId()).addVariables(job, sSingCur, _df.format(payAmount));
        }
        else if(payAmount != 1){
                str = ChatColor.GREEN + McJobs.getPlugin().getLanguage().getPayment("pay", play.getUniqueId()).addVariables(job, sPlurCur, _df.format(payAmount));
        }
        
        return str;
    }

    public static String payRegister(Player play, int tier, double basepay, String job){
        double payAmount = 0.0;
        double iTimes = tier;
        double totalPay = PlayerData.getEarnedIncome(play.getUniqueId());

        job = job.toLowerCase();
        
        String sSingCur = McJobs.getPlugin().getLanguage().getPayment("currency_single", play.getUniqueId()).addVariables("", "", "");
        String sPlurCur = McJobs.getPlugin().getLanguage().getPayment("currency_plural", play.getUniqueId()).addVariables("", "", "");
        String str = "";

        payAmount = basepay * iTimes * Leveler.getMultiplier(PlayerData.getJobLevel(play.getUniqueId(), job));
         
        if(overLimit(payAmount, totalPay, play))
            return str;
        
        if(totalPay + payAmount >= _maxPay && _maxPay > 0)
            payAmount = _maxPay - totalPay + 1;

        totalPay = totalPay + payAmount;        
        PlayerData.setEarnedIncome(play.getUniqueId(), totalPay);
            
//        if(Methods.getMethod().getAccount(play.getName()) == null){
//            Methods.getMethod().createAccount(play.getName());
//        }

        if(_payCache.containsKey(play.getUniqueId())){
            double temp = _payCache.get(play.getUniqueId());
            temp = temp + payAmount;
            _payCache.put(play.getUniqueId(), temp);
        } else
            _payCache.put(play.getUniqueId(), payAmount);
 
//        Methods.getMethod().getAccount(play.getName()).add(payAmount);
        if(payAmount == 1){
            str = ChatColor.GREEN + McJobs.getPlugin().getLanguage().getPayment("pay", play.getUniqueId()).addVariables(job, sSingCur, _df.format(payAmount));
        }
        else if(payAmount != 1){
            str = ChatColor.GREEN + McJobs.getPlugin().getLanguage().getPayment("pay", play.getUniqueId()).addVariables(job, sPlurCur, _df.format(payAmount));
        }        
        return str;
    }
    
    public static String chargeVault(Player play, int tier, double basepay, String job){
        double payAmount = 0.0;
        double iTimes = tier;
        double totalPay = PlayerData.getEarnedIncome(play.getUniqueId());
        
        job = job.toLowerCase();
        
        String sSingCur = "";
        String sPlurCur = "";
        String str = "";

        payAmount = basepay * iTimes * Leveler.getMultiplier(PlayerData.getJobLevel(play.getUniqueId(), job));
        payAmount = payAmount * PlayerJobs.getPercent() / 100.0;

        
        totalPay = totalPay - payAmount;
        PlayerData.setEarnedIncome(play.getUniqueId(), totalPay);
        
//        if(!McJobs.getEconomy().hasAccount(play.getName())){
//            McJobs.getEconomy().createPlayerAccount(play.getName());
//        }

        if(_payCache.containsKey(play.getUniqueId())){
            double temp = _payCache.get(play.getUniqueId());
            temp = temp - payAmount;
            _payCache.put(play.getUniqueId(), temp);
        } else {
            double temp = -1 * payAmount;
            _payCache.put(play.getUniqueId(), temp);
        }

//        McJobs.getEconomy().withdrawPlayer(play.getName(), payAmount);

        if(!McJobs.getEconomy().currencyNameSingular().equals(""))
            sSingCur = McJobs.getEconomy().currencyNameSingular();
        else
            sSingCur = McJobs.getPlugin().getLanguage().getPayment("currency_single", play.getUniqueId()).addVariables("", "", "");

        if(!McJobs.getEconomy().currencyNamePlural().equals(""))
            sPlurCur = McJobs.getEconomy().currencyNamePlural();
        else
            sPlurCur = McJobs.getPlugin().getLanguage().getPayment("currency_plural", play.getUniqueId()).addVariables("", "", "");

        if(payAmount == 1){
            str = ChatColor.GREEN + McJobs.getPlugin().getLanguage().getPayment("charge", play.getUniqueId()).addVariables(job, sSingCur, _df.format(payAmount));
        } else if(payAmount != 1){
            str = ChatColor.GREEN + McJobs.getPlugin().getLanguage().getPayment("charge", play.getUniqueId()).addVariables(job, sPlurCur, _df.format(payAmount));
        }
        
        return str;
    }

    public static String chargeRegister(Player play, int tier, double basepay, String job){
        double payAmount = 0.0;
        double iTimes = tier;
        double totalPay = PlayerData.getEarnedIncome(play.getUniqueId());

        job = job.toLowerCase();
        
        String sSingCur = McJobs.getPlugin().getLanguage().getPayment("currency_single", play.getUniqueId()).addVariables("", "", "");
        String sPlurCur = McJobs.getPlugin().getLanguage().getPayment("currency_plural", play.getUniqueId()).addVariables("", "", "");
        String str = "";
        
        payAmount = basepay * iTimes * Leveler.getMultiplier(PlayerData.getJobLevel(play.getUniqueId(), job));
        payAmount = payAmount * PlayerJobs.getPercent() / 100.0;         

        
        totalPay = totalPay - payAmount;
        PlayerData.setEarnedIncome(play.getUniqueId(), totalPay);
                        
//        if(Methods.getMethod().getAccount(play.getName()) == null){
//            Methods.getMethod().createAccount(play.getName());
//        }

        if(_payCache.containsKey(play.getUniqueId())){
            double temp = _payCache.get(play.getUniqueId());
            temp = temp - payAmount;
            _payCache.put(play.getUniqueId(), temp);
        } else
            _payCache.put(play.getUniqueId(), -1 * payAmount);

        
//        Methods.getMethod().getAccount(play.getName()).subtract(payAmount);

        if(payAmount == 1) {
            str = ChatColor.GREEN + McJobs.getPlugin().getLanguage().getPayment("charge", play.getUniqueId()).addVariables(job, sSingCur, _df.format(payAmount));
        } else if(payAmount != 1) {
            str = ChatColor.GREEN + McJobs.getPlugin().getLanguage().getPayment("charge", play.getUniqueId()).addVariables(job, sPlurCur, _df.format(payAmount));
        }
        
        return str;
    }
    
    public static void setMaxPay(double d){
        _maxPay = d;
    }
    
    private static boolean overLimit(double payamount, double playertotal, Player play){
        if(_maxPay <= 0)
            return false;
        
        if(playertotal > _maxPay)
            return true;
        
        if(payamount + playertotal > _maxPay){
            PrettyText text = new PrettyText();
            String str = McJobs.getPlugin().getLanguage().getJobNotify("overpay", play.getUniqueId()).addVariables("", play.getName(), "");

            text.formatPlayerText(str, play);
            return false;
        }        
        
        return false;
    }

    public static void makeEconomyCall(boolean bVault) {
        if(bVault) {
            HashMap<UUID, Double> temp1 = new HashMap<>();
            temp1.putAll(_payCache);
 
            for(Map.Entry<UUID, Double> me: temp1.entrySet()) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(me.getKey());
                if(op != null) {
                    if(me.getValue() > 0.0)
                        McJobs.getEconomy().depositPlayer(op, me.getValue());
                    else if(me.getValue() < 0) {
                        //Make negative to positic
                        double temp = me.getValue() * -1;
                        McJobs.getEconomy().withdrawPlayer(op, temp);
                    }
                    _payCache.remove(me.getKey());
                }
            }
        }    
    }
}
