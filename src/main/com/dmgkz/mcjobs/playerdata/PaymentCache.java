package com.dmgkz.mcjobs.playerdata;

import org.bukkit.entity.Player;

public class PaymentCache {
    private final Player _play;
    private final boolean _isPayed;
    private final int _paymentTier;
    private final double _basepay;
    private final String _jobName;
    
    public PaymentCache(Player play, boolean isPayed, int paymentTier, double basepay, String jobName){
        _play = play;
        _isPayed = isPayed;
        _paymentTier = paymentTier;
        _basepay = basepay;
        _jobName = jobName;
    }

    public Player getPlayer() {
        return _play;
    }
    
    public boolean getPayed() {
        return _isPayed;
    }

    public int getPaymentTier() {
        return _paymentTier;
    }

    public double getBasePay() {
        return _basepay;
    }

    public String getJobName() {
        return _jobName;
    }
}
