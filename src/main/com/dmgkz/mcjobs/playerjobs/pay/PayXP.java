package com.dmgkz.mcjobs.playerjobs.pay;


import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.levels.Leveler;
import com.dmgkz.mcjobs.prettytext.PrettyText;

public class PayXP {
    private static double _maxPay = -1;
    private static final DecimalFormat _df = new DecimalFormat("#,##0.0#");

    public static String payXP(Player play, int tier, double basepay, String job){
        double payAmount = 0.0;
        double iTimes = tier;
        double totalPay = PlayerData.getEarnedIncome(play.getUniqueId());
        String str = "";

        job = job.toLowerCase();
        
        int xp;
                
        payAmount = basepay * iTimes * Leveler.getMultiplier(PlayerData.getJobLevel(play.getUniqueId(), job));
         
        if(overLimit(payAmount, totalPay, play))
            return str;

        if(totalPay + payAmount >= _maxPay && _maxPay > 0)
            payAmount = _maxPay - totalPay + 1;

        totalPay = totalPay + payAmount;
        PlayerData.setEarnedIncome(play.getUniqueId(), totalPay);

        if(payAmount < 1 && payAmount >= 0.5)
            xp = 1;
        else
            xp = (int) payAmount;

        manipXP(play, xp);
        str = ChatColor.GREEN + McJobs.getPlugin().getLanguage().getPayment("payxp", play.getUniqueId()).addVariables(job, "", _df.format(payAmount));
        return str;
    }
    
    public static String chargeXP(Player play, int tier, double basepay, String job){
        double payAmount = 0.0;
        double iTimes = tier;
        double totalPay = PlayerData.getEarnedIncome(play.getUniqueId());
        String str = "";

        job = job.toLowerCase();
        
        int xp;
                
        payAmount = basepay * iTimes * Leveler.getMultiplier(PlayerData.getJobLevel(play.getUniqueId(), job));
        payAmount = payAmount * PlayerJobs.getPercent() / 100.0;
            
        totalPay = totalPay + payAmount;
        PlayerData.setEarnedIncome(play.getUniqueId(), totalPay);
            
        payAmount = -1D * payAmount;
            
        if(payAmount <= 0.5 && payAmount > -1)
            xp = -1;
        else
            xp = (int) payAmount;

        manipXP(play, xp);

        str = ChatColor.GREEN + McJobs.getPlugin().getLanguage().getPayment("chargexp", play.getUniqueId()).addVariables(job, "", _df.format(payAmount));
        return str;
    }
    
    private static void manipXP(Player play, Integer xp){
        Float currentXP;
        Integer currentLVL = play.getLevel();

        play.giveExp(xp);
        currentXP = play.getExp();
                
        if(currentLVL + currentXP < 0){
            play.setLevel(0);
            play.setExp(0F);
            return;
        }
        
        while(currentXP < 0F) {
            currentLVL = currentLVL - 1;
            play.setLevel(currentLVL);

            currentXP = currentXP + 1F;
            play.setExp(currentXP);            
        }    
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
}
