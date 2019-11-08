package com.dmgkz.mcjobs.playerjobs.levels;

import java.text.DecimalFormat;
import java.util.HashMap;

public class Leveler {
    private static HashMap<Integer, String> hRanks = new HashMap<>();
    private static double xpmod;
    private static double paymod;
    
    
    public static HashMap<Integer, String> getRanks(){
        return hRanks;
    }
    
    public static void setXPMod(double i) {
        if(i > 0)
            xpmod = i;
        else
            xpmod = 1;
    }
    
    public static void setPayMod(double i) {
        if(i > 0)
            paymod = i;
        else
            paymod = 1;
    }
    
    public static String getRank(int level) {
        int i = level;

        while(i > 0){
            if(Leveler.hRanks.containsKey(i))
                return Leveler.hRanks.get(i);
            i--;
        }

        return "novice";
    }
    
    public static double getXPtoLevel(int level) {
        double xpNeeded;
        
        xpNeeded = (2 * (level * level) + 10 * level - 3) * xpmod;
        
        return xpNeeded;
    }
    
    public static String getXPtoLevelDisplay(int level) {
        return getXPDisplay(getXPtoLevel(level));
    }
    
    public static String getXPDisplay(double exp) {
        DecimalFormat df = new DecimalFormat("#,###,###,##0");
        return df.format(exp);
    }
    
    public static double getMultiplier(int level) {
        double multi = 1;
        
        if(level <= 20)
            multi = 0.75 * 0.1 * level * paymod;
        
        if(level > 20)
            multi = 0.75 * ((0.0085 * level + 2) * paymod);
        
        return multi;
    }
}
