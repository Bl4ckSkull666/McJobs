package com.dmgkz.mcjobs.util;


import com.dmgkz.mcjobs.McJobs;
import com.dmgkz.mcjobs.localization.GetLanguage;
import java.util.UUID;

public class TimeFormat {
    public static String getFormatedTime(UUID uuid, Integer time) {
        GetLanguage modText = McJobs.getPlugin().getLanguage();
        String str = "";

        if(time < 1) {
            str = "1 " + modText.getJobNotify("minute", uuid).addVariables("", "", "");
            return str;
        }
        
        while(time > 0){
            if(time < 60) {
                if(time == 1)
                    str = str.concat(time.toString() + " " + modText.getJobNotify("minute", uuid).addVariables("", "", ""));
                else
                    str = str.concat(time.toString() + " " + modText.getJobNotify("minutes", uuid).addVariables("", "", ""));
                time = 0;
            } else if(time >= 60 && time < 1440) {
                Integer hour = 0;
                while(time >= 60){
                    hour++;
                    time = time - 60;
                }
                if(hour == 1)
                    str = str.concat(hour.toString() + " " + modText.getJobNotify("hour", uuid).addVariables("", "", ""));
                else
                    str = str.concat(hour.toString() + " " + modText.getJobNotify("hours", uuid).addVariables("", "", ""));
                
                if(time > 0)
                    str = str.concat(" " + modText.getJobNotify("and", uuid).addVariables("", "", "") + " ");
            } else if(time >= 1440 && time <= 10080 ){
                Integer day = 0;
                while(time >= 1440) {
                    day++;
                    time = time - 1440;
                }
                if(day == 1)
                    str = str.concat(day.toString() + " " + modText.getJobNotify("day", uuid).addVariables("", "", ""));
                else
                    str = str.concat(day.toString() + " " + modText.getJobNotify("days", uuid).addVariables("", "", ""));
                if(time >= 60)
                    str = str.concat(" " + modText.getJobNotify("and", uuid).addVariables("", "", "") + " ");
            } else if(time >= 10080 && time <= 302400) {
                Integer week = 0;
                while(time >= 10080){
                    week++;
                    time = time - 10080;
                }
                if(week == 1)
                    str = str.concat(week.toString() + " " + modText.getJobNotify("week", uuid).addVariables("", "", ""));
        else
                    str = str.concat(week.toString() + " " + modText.getJobNotify("weeks", uuid).addVariables("", "", ""));
                if(time >= 1440)
                    str = str.concat(" " + modText.getJobNotify("and", uuid).addVariables("", "", "") + " ");
            } else if(time >= 302400) {
                Integer month = 0;
                while(time >= 302400) {
                    month++;
                    time = time - 302400;
                }
                if(month == 1)
                    str = month.toString() + " " + modText.getJobNotify("month", uuid).addVariables("", "", "");
                else
                    str = month.toString() + " " + modText.getJobNotify("months", uuid).addVariables("", "", "");
                if(time >= 1440)
                    str = str.concat(" " + modText.getJobNotify("and", uuid).addVariables("", "", "") + " ");
            }
        }
        return str;
    }
}
