package com.dmgkz.mcjobs.playerjobs.data;

import com.dmgkz.mcjobs.McJobs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.dmgkz.mcjobs.playerdata.CompCache;
import com.dmgkz.mcjobs.playerdata.PaymentCache;
import com.dmgkz.mcjobs.playerdata.PlayerData;
import com.dmgkz.mcjobs.util.EnchantTypeAdv;
import com.dmgkz.mcjobs.util.PlayerKills;
import com.dmgkz.mcjobs.util.PotionTypeAdv;
import java.util.List;
import org.bukkit.DyeColor;

public class CompData {
    private final JobsData _jobsdata;
    private static final ArrayList<CompCache> _compcache = new ArrayList<>();
    
    public CompData(JobsData jobsdata){
        _jobsdata = jobsdata;
    }
        
    public static ArrayList<CompCache> getCompCache() {
        return _compcache;
    }
    
    public boolean compBlock(Material block, Player play, String sAction, ArrayList<PaymentCache> aPayer){
        if(_jobsdata.getMatHash().isEmpty() || !_jobsdata.getMatHash().containsKey(sAction))
            return false;

        for(Map.Entry<Integer, List<Material>> me: _jobsdata.getMatHash().get(sAction).entrySet()) {
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

        for(Map.Entry<Integer, List<EntityType>> me: _jobsdata.getEntHash().get(sAction).entrySet()) {
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

        for(Map.Entry<Integer, List<Material>> me: _jobsdata.getMatHash().get(sAction).entrySet()) {
            if(me.getValue().isEmpty() || !me.getValue().contains(item.getType()) || !_jobsdata.getTierPays().containsKey(sAction))
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
        
        for(Map.Entry<Integer, List<PotionTypeAdv>> me: _jobsdata.getPotHash().get(sAction).entrySet()) {
            if(me.getValue().isEmpty() || !me.getValue().contains(potion) || !_jobsdata.getTierPays().containsKey(sAction))
                continue;

            PaymentCache payment = new PaymentCache(play, _jobsdata.getTierPays().get(sAction), me.getKey(), _jobsdata.getBasePay(), _jobsdata.getName().toLowerCase());
            aPayer.add(payment);
            return true;
        }
        return false;
    }

    public boolean compEnchant(Map<Enchantment, Integer> enchantments, Player play, String sAction, ArrayList<PaymentCache> aPayer) {
        if(_jobsdata.getEnchantHash().isEmpty() || !_jobsdata.getEnchantHash().containsKey(sAction))
            return false;

        boolean bEnchanted = false;
        for(Map.Entry<Integer, List<EnchantTypeAdv>> me: _jobsdata.getEnchantHash().get(sAction).entrySet()) {
            if(me.getValue().isEmpty() || !_jobsdata.getTierPays().containsKey(sAction))
                continue;

            for(Map.Entry<Enchantment, Integer> me2: enchantments.entrySet()) {
                EnchantTypeAdv enchant = McJobs.getPlugin().getHolder().getEnchants().getEnchantAdv(me2.getKey(), me2.getValue());
                if(enchant == null || !me.getValue().contains(enchant))
                    continue;

                PaymentCache payment = new PaymentCache(play, _jobsdata.getTierPays().get(sAction), me.getKey(), _jobsdata.getBasePay(), _jobsdata.getName().toLowerCase());
                aPayer.add(payment);
                bEnchanted = true;
            }                        
        }
        return bEnchanted;
    }
    
    public boolean compPvP(String job, Player killed, Player play, String sAction, ArrayList<PaymentCache> aPayer) {
        if(_jobsdata.getPvPHash().isEmpty() || !_jobsdata.getPvPHash().containsKey(sAction))
            return false;
        
        PlayerKills pk = PlayerData.getPlayerKills(play.getUniqueId());
        int killedCount = pk.getKilledCount(killed.getUniqueId(), job);
        
        if(killedCount > 0 && pk.lastKilledBeforeSeconds(killed.getUniqueId(), job) <= _jobsdata.getPvPInterval()) {
            return false;
        }
        
        int tier = 0;
        int tmpKills = 0;
        for(Map.Entry<Integer, Integer> me: _jobsdata.getPvPHash().get(sAction).entrySet()) {
            if(me.getValue() >= killedCount && (tmpKills == 0 || me.getValue() < tmpKills)) {
                tier = me.getKey();
                tmpKills = me.getValue();
            }
        }
        
        pk.setKill(killed.getUniqueId(), job);
        if(tier > 0) {
            PaymentCache payment = new PaymentCache(play, _jobsdata.getTierPays().get(sAction), tier, _jobsdata.getBasePay(), _jobsdata.getName().toLowerCase());
            aPayer.add(payment);
            return true;
        }
        return false;
    }
    
    public boolean compShear(DyeColor dc, Player play, String sAction, ArrayList<PaymentCache> aPayer) {
        if(_jobsdata.getColorHash().isEmpty() || !_jobsdata.getColorHash().containsKey(sAction))
            return false;

        for(Map.Entry<Integer, List<DyeColor>> me: _jobsdata.getColorHash().get(sAction).entrySet()) {
            if(me.getValue().isEmpty() || !me.getValue().contains(dc) || !_jobsdata.getTierPays().containsKey(sAction))
                continue;

            PaymentCache payment = new PaymentCache(play, _jobsdata.getTierPays().get(sAction), me.getKey(), _jobsdata.getBasePay(), _jobsdata.getName().toLowerCase());
            aPayer.add(payment);
            return true;                        
        }

        return false;
    }
    
    public HashMap<Integer, List<Material>> getMatTypeTiers(String type) {
        if(_jobsdata.getMatHash().containsKey(type))
            return _jobsdata.getMatHash().get(type);
        
        return new HashMap<>();
    }

    public HashMap<Integer, List<EntityType>> getEntTypeTiers(String type){
        if(_jobsdata.getEntHash().containsKey(type))
            return _jobsdata.getEntHash().get(type);
        
        return new HashMap<>();
    }

    public HashMap<Integer, List<PotionTypeAdv>> getPotTypeTiers(String type){
        if(_jobsdata.getPotHash().containsKey(type))
            return _jobsdata.getPotHash().get(type);
        
        return new HashMap<>();
    }
    
    public HashMap<Integer, List<EnchantTypeAdv>> getEnchantTypeTiers(String type){
        if(_jobsdata.getEnchantHash().containsKey(type))
            return _jobsdata.getEnchantHash().get(type);
        
        return new HashMap<>();
    }
    
    public HashMap<Integer, List<DyeColor>> getColorTiers(String type) {
        if(_jobsdata.getColorHash().containsKey(type))
            return _jobsdata.getColorHash().get(type);
        
        return new HashMap<>();
    }
    
    public HashMap<Integer, Integer> getPvpTiers(String type) {
        if(_jobsdata.getPvPHash().containsKey(type))
            return _jobsdata.getPvPHash().get(type);
        
        return new HashMap<>();
    }

    
    public Boolean isDefault(){
        return _jobsdata._bDefaultJob;
    }
}
