package com.dmgkz.mcjobs.util;


import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.localization.GetLanguage;
import java.util.UUID;

public class TimeFormat {    
    public static String getFormatedTime(UUID uuid, long seconds) {
        String timeString = "";
        boolean isBefore = false;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;

        GetLanguage modText = McJobs.getPlugin().getLanguage();
        
        if(seconds > (60*60*24*7)) {
            weeks = (int) Math.floor(seconds / (60 * 60 * 24 * 7));
            seconds -= (60 * 60 * 24 * 7) * weeks;
            if (weeks > 0) {
                if (weeks == 1)
                    timeString += "0" + weeks + modText.getJobNotify("week", uuid).addVariables("", "", "");
                else
                    timeString += (weeks < 10?"0":"") + weeks + modText.getJobNotify("weeks", uuid).addVariables("", "", "");
                isBefore = true;
            }
        }

        if(seconds > (60*60*24)) {
            days = (int)Math.floor(seconds/(60*60*24));
            seconds -= (60*60*24)*days;
            if(days > 0) {
                if(isBefore)
                    timeString += modText.getJobNotify("time-seperator", uuid).addVariables("", "", "");

                if(days == 1)
                    timeString += "0" + days + modText.getJobNotify("day", uuid).addVariables("", "", "");
                else
                    timeString += (days < 10?"0":"") + days + modText.getJobNotify("days", uuid).addVariables("", "", "");
                isBefore = true;
            }
        }

        if(seconds > (60*60)) {
            hours = (int)Math.floor(seconds/(60*60));
            seconds -= (60*60)*hours;
            if(hours > 0) {
                if(isBefore)
                    timeString += modText.getJobNotify("time-seperator", uuid).addVariables("", "", "");

                if(hours == 1)
                    timeString += "0" + hours + modText.getJobNotify("hour", uuid).addVariables("", "", "");
                else
                    timeString += (hours < 10?"0":"") + hours + modText.getJobNotify("hours", uuid).addVariables("", "", "");
                isBefore = true;
            }
        }

        if(seconds > 60) {
            minutes = (int)Math.floor(seconds/60);
            seconds -= 60*minutes;
            if(minutes > 0) {
                if(isBefore)
                    timeString += modText.getJobNotify("time-seperator", uuid).addVariables("", "", "");

                if(minutes == 1)
                    timeString += "0" + minutes + modText.getJobNotify("minute", uuid).addVariables("", "", "");
                else
                    timeString += (minutes < 10?"0":"") + minutes + modText.getJobNotify("minutes", uuid).addVariables("", "", "");
                isBefore = true;
            }
        }

        if(seconds > 0) {
            if(isBefore)
                timeString += modText.getJobNotify("time-seperator", uuid).addVariables("", "", "");

            if(seconds == 1)
                timeString += "0" + seconds + modText.getJobNotify("second", uuid).addVariables("", "", "");
            else
                timeString += (seconds < 10?"0":"") + seconds + modText.getJobNotify("seconds", uuid).addVariables("", "", "");
        }
        return timeString;
    }
}
