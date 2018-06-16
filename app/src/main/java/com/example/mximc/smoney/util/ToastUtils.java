package com.example.mximc.smoney.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    public  static void toastLong(Context context,String msg){
        Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();
    }

    public static void toastShort(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
