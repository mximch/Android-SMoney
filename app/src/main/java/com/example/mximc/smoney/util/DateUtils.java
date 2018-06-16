package com.example.mximc.smoney.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static Date date =new Date();
    public static String getDateTimeInstance(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("y-M-d HH:mm:ss");
        return  sdf.format(date);
    }
    public static String getDateIntance(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("y-M-d");
        return sdf.format(date);
    }
    public static String getMonthInstance(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("M");
        return sdf.format(date);
    }
    public static String getMonthAndDateIntance(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("M月d日");
        return sdf.format(date);
    }
    public static String getCurrentYearAndMonthIntance(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("y-M");
        return sdf.format(date);
    }
    public static String getTimeIntance(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }

    public static String getCurrentDate(){

        return getDateIntance(date);
    }

    public static String getCurrentMonth(){

        return getMonthInstance(date);
    }

    public static String getCurrentMonthAndDate(){

        return getMonthAndDateIntance(date);
    }

    public static String getCurrentYearAndMonth(){

        return getCurrentYearAndMonthIntance(date);
    }
}
