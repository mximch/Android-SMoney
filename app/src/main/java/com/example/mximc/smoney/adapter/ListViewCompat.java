package com.example.mximc.smoney.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.example.mximc.smoney.bean.CostBean;
import com.example.mximc.smoney.view.SlideView;

public class ListViewCompat extends ListView{
    private  static  final String TAG="ListViewCompat";
    public static CostBean costBean;

    private SlideView mFocusedItemView;

    public ListViewCompat(Context context){
        super(context);
    }

    public ListViewCompat(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    public ListViewCompat(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
    }

    public void shrinkListItem(int position){
        View item=getChildAt(position);

        if (item!=null){
            try {
                ((SlideView)item).shrink();
            }catch (ClassCastException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                int x= (int) event.getX();
                int y= (int) event.getY();
                int position=pointToPosition(x,y );
                Log.e(TAG, "postion"+position);
                if (position!=INVALID_POSITION){
                    costBean=(CostBean) getItemAtPosition(position);

                    mFocusedItemView=costBean.slideView;
                    Log.e(TAG,"FocusedItemView=" +mFocusedItemView);
                }
            }
            default:
                break;
        }
        if (mFocusedItemView!=null){
            mFocusedItemView.onRequireTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }
}
