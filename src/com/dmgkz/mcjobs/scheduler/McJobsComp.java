package com.dmgkz.mcjobs.scheduler;

import java.util.ArrayList;

import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PaymentCache;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.playerjobs.PlayerJobs;
import com.dmgkz.mcjobs.playerjobs.pay.PayMoney;
import com.dmgkz.mcjobs.playerjobs.pay.PayXP;

public class McJobsComp implements Runnable {
    private final ArrayList<CompCache> _aComp;
    private final ArrayList<PaymentCache> _aPayer;
    private static boolean _bVault;
    private static boolean _bRegister;
    private static boolean _bXP;
    
    public McJobsComp(ArrayList<CompCache> aComp) {
        _aComp = aComp;
        _aPayer = new ArrayList<>();
    }
    
    @Override
    public void run() {
        for(CompCache cc: _aComp) {
            if(cc.getAction().equalsIgnoreCase("break") || cc.getAction().equalsIgnoreCase("place")) {
                if(!McJobs.getPlugin().getBlockLogging().checkBuiltIn(cc.getLocation(), cc.getPlayer(), cc.getMaterial(), cc.getAction().equalsIgnoreCase("break"))) {
                    if(PlayerJobs.getJobsList().get(cc.getJob()).getData().compJob().compBlock(cc.getMaterial(), cc.getPlayer(), cc.getAction(), _aPayer)) {
                        McJobs.getPlugin().getBlockLogging().addPlayer(cc.getLocation(), cc.getPlayer(), cc.getMaterial(), cc.getAction().equalsIgnoreCase("break"));
                    }
                }
            } else if(cc.getAction().equalsIgnoreCase("defeat"))
                PlayerJobs.getJobsList().get(cc.getJob()).getData().compJob().compEntity(cc.getEntity(), cc.getPlayer(), cc.getAction(), _aPayer);
            else if(cc.getAction().equalsIgnoreCase("potion"))
                PlayerJobs.getJobsList().get(cc.getJob()).getData().compJob().compPotions(cc.getPotion(), cc.getPlayer(), cc.getAction(), _aPayer);
            else if(cc.getAction().equalsIgnoreCase("enchant"))
                PlayerJobs.getJobsList().get(cc.getJob()).getData().compJob().compEnchant(cc.getEnchants(), cc.getPlayer(), cc.getAction(), _aPayer);
            else if(cc.getAction().equalsIgnoreCase("craft") || cc.getAction().equalsIgnoreCase("repair") || cc.getAction().equalsIgnoreCase("fishing"))
                PlayerJobs.getJobsList().get(cc.getJob()).getData().compJob().compBlock(cc.getMaterial(), cc.getPlayer(), cc.getAction(), _aPayer);
            else if(cc.getAction().equalsIgnoreCase("shear"))
                PlayerJobs.getJobsList().get(cc.getJob()).getData().compJob().compShear(cc.getColor(), cc.getPlayer(), cc.getAction(), _aPayer);
            else if(cc.getAction().equalsIgnoreCase("pvp"))
                PlayerJobs.getJobsList().get(cc.getJob()).getData().compJob().compPvP(cc.getJob(), cc.getKilled(), cc.getPlayer(), cc.getAction(), _aPayer);
        }
        
        for(PaymentCache pc: _aPayer) {
            String str = "";
            if(pc.getPayed()){
                if(_bVault)
                    str = PayMoney.payVault(pc.getPlayer(), pc.getPaymentTier(), pc.getBasePay(), pc.getJobName());
                if(_bRegister)
                    str = PayMoney.payRegister(pc.getPlayer(), pc.getPaymentTier(), pc.getBasePay(), pc.getJobName());
                if(_bXP)
                    str = PayXP.payXP(pc.getPlayer(), pc.getPaymentTier(), pc.getBasePay(), pc.getJobName());
            } else {
                if(_bVault)
                    str = PayMoney.chargeVault(pc.getPlayer(), pc.getPaymentTier(), pc.getBasePay(), pc.getJobName());
                if(_bRegister)
                    str = PayMoney.chargeRegister(pc.getPlayer(), pc.getPaymentTier(), pc.getBasePay(), pc.getJobName());
                if(_bXP)
                    str = PayXP.chargeXP(pc.getPlayer(), pc.getPaymentTier(), pc.getBasePay(), pc.getJobName());                
            }
            
            double xpPayment = pc.getPaymentTier() * PlayerJobs.getJobsList().get(pc.getJobName()).getData().getEXP();
            PlayerData.addExp(pc.getPlayer().getUniqueId(), pc.getJobName().toLowerCase(), xpPayment);

            if(!str.isEmpty()) {
                if(PlayerData.getShowEveryTime(pc.getPlayer().getUniqueId(), pc.getJobName()))
                    pc.getPlayer().sendMessage(str);
            }
        }

        _aPayer.clear();

        if(_bVault)
            PayMoney.makeEconomyCall(true);
        if(_bRegister)
            PayMoney.makeEconomyCall(false);
    }

    public static void setVault(boolean b){
        _bVault = b;
    }

    public static void setRegister(boolean b){
        _bRegister = b;
    }
    
    public static void setXP(boolean b){
        _bXP = b;
    }
}
