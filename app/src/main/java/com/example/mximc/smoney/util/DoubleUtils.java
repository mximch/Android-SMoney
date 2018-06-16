package com.example.mximc.smoney.util;

import java.text.DecimalFormat;

public class DoubleUtils {
    /*
    * 保留两位小数
    */
    public static  String formatDouble(double d){
        return new DecimalFormat("#.00").format(d);
    }
}
