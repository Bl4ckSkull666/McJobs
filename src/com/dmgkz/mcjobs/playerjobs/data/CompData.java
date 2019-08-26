package com.dmgkz.mcjobs.playerjobs.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PaymentCache;
import com.dmgkz.mcjobs.util.EnchantTypeAdv;
import com.dmgkz.mcjobs.util.MatClass;
import com.dmgkz.mcjobs.util.PotionTypeAdv;
import com.dmgkz.mcjobs.util.Utils;

public class CompData {
    private final JobsData _jobsdata;

    private static final ArrayList<CompCache> _compcache = new ArrayList<>();
    
    public CompData(JobsData jobsdata){
        _jobsdata = jobsdata;
    }
        
    public static ArrayList<CompCache> getCompCache() {
        return _compcache;
    }
    
    public boolean compBlock(MatClass block, Player play, String sAction, ArrayList<PaymentCache> aPayer){
        if(_jobsdata.getMatHash().isEmpty() || !_jobsdata.getMatHash().containsKey(sAction))
            return false;

        for(Map.Entry<Integer, ArrayList<MatClass>> me: _jobsdata.getMatHash().get(sAction).entrySet()) {
            if(me.getValue().isEmpty() || !me.getValue().contains(block) || !_jobsdata.getTierPays().containsKey(sAction))
                continue;
            
            PaymentCache payment = new PaymentCache(play, _jobsdata.getTierPays().get(sAction), me.getKey(), _jobsdata.getBasePay(), _jobsdata.getName().toLowerCase());
            aPayer.add(payment);
            return true;
        }
        return false;
    }

    public boolean compEntity(EntityType entity, Player play, String sAction, ArrayList<PaymentCache> aPayer){
        if(_jobsdata.getEntHash().isEmpty() || !_jobsdata.getEntHash().containsKey(sAction))
            return false;

        for(Map.Entry<Integer, ArrayList<EntityType>> me: _jobsdata.getEntHash().get(sAction).entrySet()) {
            if(me.getValue().isEmpty() || !me.getValue().contains(entity) || !_jobsdata.getTierPays().containsKey(sAction))
                continue;

            PaymentCache payment = new PaymentCache(play, _jobsdata.getTierPays().get(sAction), me.getKey(), _jobsdata.getBasePay(), _jobsdata.getName().toLowerCase());
            aPayer.add(payment);
            return true;                        
        }

        return false;
    }

    public boolean compCraft(ItemStack item, Player play, String sAction, ArrayList<PaymentCache> aPayer) {
        if(_jobsdata.getMatHash().isEmpty() || !_jobsdata.getMatHash().containsKey(sAction))
            return false;

        for(Map.Entry<Integer, ArrayList<MatClass>> me: _jobsdata.getMatHash().get(sAction).entrySet()) {
            if(me.getValue().isEmpty() || !Utils.isInside(item.getType(), me.getValue()) || !_jobsdata.getTierPays().containsKey(sAction))
                continue;

            PaymentCache payment = new PaymentCache(play, _jobsdata.getTierPays().get(sAction), me.getKey(), _jobsdata.getBasePay(), _jobsdata.getName().toLowerCase());
            aPayer.add(payment);
            return true;
        }
        return false;
    }

    public boolean compPotions(PotionTypeAdv potion, Player play, String sAction, ArrayList<PaymentCache> aPayer){
        if(_jobsdata.getPotHash().isEmpty() || !_jobsdata.getPotHash().containsKey(sAction))
            return false;
        
        for(Map.Entry<Integer, ArrayList<PotionTypeAdv>> me: _jobsdata.getPotHash().get(sAction).entrySet()) {
            if(me.getValue().isEmpty() || !me.getValue().contains(potion) || !_jobsdata.getTierPays().containsKey(sAction))
                continue;

            PaymentCache payment = new PaymentCache(play, _jobsdata.getTierPays().get(sAction), me.getKey(), _jobsdata.getBasePay(), _jobsdata.getName().toLowerCase());
            aPayer.add(payment);
            return true;
        }
        return false;
    }

    public boolean compEnchant(Map<Enchantment, Integer> enchantments, Player play, String sAction, ArrayList<PaymentCache> aPayer){
        if(_jobsdata.getEnchantHash().isEmpty() || !_jobsdata.getEnchantHash().containsKey(sAction))
            return false;

        boolean bEnchanted = false;
        for(Map.Entry<Integer, ArrayList<EnchantTypeAdv>> me: _jobsdata.getEnchantHash().get(sAction).entrySet()) {
            if(me.getValue().isEmpty() || !_jobsdata.getTierPays().containsKey(sAction))
                continue;

            for(Map.Entry<Enchantment, Integer> me2: enchantments.entrySet()) {
                EnchantTypeAdv enchant = EnchantTypeAdv.getEnchantAdv(me2.getKey(), me2.getValue());
                
                if(enchant == null || !me.getValue().contains(enchant))
                    continue;

                PaymentCache payment = new PaymentCache(play, _jobsdata.getTierPays().get(sAction), me.getKey(), _jobsdata.getBasePay(), _jobsdata.getName().toLowerCase());
                aPayer.add(payment);
                bEnchanted = true;
            }                        
        }
        return bEnchanted;
    }
    
        
    public HashMap<Integer, ArrayList<MatClass>> getMatTypeTiers(String type) {
        if(_jobsdata.getMatHash().containsKey(type))
            return _jobsdata.getMatHash().get(type);
        
        return new HashMap<>();
    }

    public HashMap<Integer, ArrayList<EntityType>> getEntTypeTiers(String type){
        if(_jobsdata.getEntHash().containsKey(type))
            return _jobsdata.getEntHash().get(type);
        
        return new HashMap<>();
    }

    public HashMap<Integer, ArrayList<PotionTypeAdv>> getPotTypeTiers(String type){
        if(_jobsdata.getPotHash().containsKey(type))
            return _jobsdata.getPotHash().get(type);
        
        return new HashMap<>();
    }
    
    public HashMap<Integer, ArrayList<EnchantTypeAdv>> getEnchantTypeTiers(String type){
        if(_jobsdata.getEnchantHash().containsKey(type))
            return _jobsdata.getEnchantHash().get(type);
        
        return new HashMap<>();
    }

    
    public Boolean isDefault(){
        return _jobsdata._bDefaultJob;
    }
}
