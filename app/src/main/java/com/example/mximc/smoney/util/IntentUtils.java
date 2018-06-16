package com.example.mximc.smoney.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class IntentUtils {
    /*
    *跳转到不同Activity
    * */

    public static void  jump(Context context,Class<?> cs){
        Intent intent=new Intent(context,cs);
        context.startActivity(intent);
    }

    /*传递数据*/

    public static void passData(Context context, Class<?> cs, Bundle bundle){
        Intent intent=new Intent(context,cs);
        intent.putExtras(bundle);//把该对象传送到另一个页面
        context.startActivity(intent);
    }
}
