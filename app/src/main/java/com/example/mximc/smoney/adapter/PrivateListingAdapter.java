package com.example.mximc.smoney.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mximc.smoney.R;
import com.example.mximc.smoney.bean.CostBean;
import com.example.mximc.smoney.util.DoubleUtils;
import com.example.mximc.smoney.util.IntentUtils;
import com.example.mximc.smoney.util.ToastUtils;
import com.example.mximc.smoney.view.SlideView;

import java.util.ArrayList;
import java.util.List;

public class PrivateListingAdapter extends BaseAdapter implements SlideView.OnSlideListener{
    private  static  final  String TAG="SlideAdapter";
    private CostBean item;

    private Context mContext;
    private LayoutInflater mInflater;
    private List<CostBean> mCostBeanList;

    private SlideView mLastSlideViewWithStatusOn;

    public PrivateListingAdapter(Context context,List<CostBean>costBeanList){
        mContext=context;
        mCostBeanList=costBeanList;
        mInflater=LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        if (mCostBeanList==null){
            mCostBeanList=new ArrayList<CostBean>();
        }
        return mCostBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCostBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        SlideView slideView = (SlideView) convertView;
        if (slideView == null) {
            View itemView = mInflater.inflate(R.layout.current_date_list_item, null);

            slideView = new SlideView(mContext);
            slideView.setContentView(itemView);

            holder = new ViewHolder(slideView);
            slideView.setOnSlideListener(this);
            slideView.setTag(holder);
        } else {
            holder = (ViewHolder) slideView.getTag();
        }
        item = mCostBeanList.get(position);

        //处理删除按钮的点击事件
        holder.deleteHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListViewCompat.costBean.delete();    // 删除
                mCostBeanList.remove(position);
                notifyDataSetChanged();
                // 删除之后刷新整个activity
                IntentUtils.jump(mContext, mContext.getClass());
                ToastUtils.toastShort(mContext, "删除成功");
            }
        });
        item.slideView = slideView;
        item.slideView.shrink();

        holder.clazz.setText(item.getClazz());
        holder.money.setText(DoubleUtils.formatDouble(item.getMoney()/100.0));
        holder.date.setText(item.getCreateDate());

        return slideView;
    }
    public static  class ViewHolder{
        public TextView clazz;
        public TextView money;
        public TextView date;
        public ViewGroup deleteHolder;

        ViewHolder(View view){
            clazz=(TextView)view.findViewById(R.id.tv_clazz);
            money=(TextView)view.findViewById(R.id.tv_money);
            date=(TextView)view.findViewById(R.id.tv_date);
            deleteHolder= (ViewGroup) view.findViewById(R.id.holder);
        }
    }

    @Override
    public void onSlider(View view, int status) {
        if (mLastSlideViewWithStatusOn!=null&&mLastSlideViewWithStatusOn!=view){
            mLastSlideViewWithStatusOn.shrink();
        }
        if (status==SLIDE_STATUS_ON){
            mLastSlideViewWithStatusOn=(SlideView) view;
        }
    }
}
