package com.dmgkz.mcjobs.playerdata;

import com.dmgkz.mcjobs.util.MatClass;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.dmgkz.mcjobs.util.PotionTypeAdv;
import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;

public class CompCache {
    private final String job;
    private final String action;
    private final Location loc;
    private final Player player;
    private Player killed;
    private MatClass block;
    private EntityType entity;
    private PotionTypeAdv potion;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private ItemStack items;
    private DyeColor color;
    
    /*
    * Cache for Break/Place
    */
    public CompCache(String sJob, Location loc, Player play, MatClass block, String action) {
        this.job = sJob;
        this.loc = loc;
        this.player = play;
        this.block = block;
        this.action = action;        
    }

    /*
    * Cache for Defeat
    */
    public CompCache(String sJob, Location loc, Player play, EntityType mob, String action) {
        this.job = sJob;
        this.loc = loc;
        this.player = play;
        this.entity = mob;
        this.action = action;        
    }
    
    /*
    * Cache for PvP
    */
    public CompCache(String sJob, Location loc, Player play, Player killed, String action) {
        this.job = sJob;
        this.loc = loc;
        this.player = play;
        this.killed = killed;
        this.action = action;        
    }

    /*
    * Cache for Brewing
    */
    public CompCache(String sJob, Location loc, Player play, PotionTypeAdv potion, String action){
        this.job = sJob;
        this.loc = loc;
        this.player = play;
        this.potion = potion;
        this.action = action;        
    }

    /*
    * Cache for Enchants
    */
    public CompCache(String sJob, Location loc, Player play, Map<Enchantment, Integer> enchantments, String action){
        this.job = sJob;
        this.loc = loc;
        this.player = play;
        this.enchantments.putAll(enchantments);
        this.action = action;        
    }

    /*
    * Cache for Craft/Repair
    */
    public CompCache(String sJob, Location loc, Player play, ItemStack items, String action) {
        this.job = sJob;
        this.loc = loc;
        this.player = play;
        this.items = items;
        this.action = action;        
    }
    
    /*
    * Cache for Shear Sheep
    */
    public CompCache(String sJob, Sheep ent, Player play, String action) {
        this.job = sJob;
        this.loc = ent.getLocation();
        this.color = ent.getColor();
        this.player = play;
        this.action = action;        
    }

    public String getJob() {
        return job;
    }
    
    public Location getLocation() {
        return loc;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Player getKilled() {
        return killed;
    }

    public MatClass getMaterial() {
        return block;
    }

    public EntityType getEntity(){
        return entity;
    }
    
    public PotionTypeAdv getPotion(){
        return potion;
    }
    
    public Map<Enchantment, Integer> getEnchants(){
        return enchantments;
    }
    
    public ItemStack getItems() {
        return items;
    }
    
    public String getAction() {
        return action;
    }
    
    public DyeColor getColor() {
        return color;
    }
}
