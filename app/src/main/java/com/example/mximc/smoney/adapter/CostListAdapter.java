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

import java.util.List;

public class CostListAdapter extends BaseAdapter{

    private List<CostBean> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public CostListAdapter(Context context,List<CostBean> list){
        mContext=context;
        mList=list;
        mLayoutInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder viewHolder;
        if (convertView==null) {
            viewHolder=new ViewHolder();
            convertView=mLayoutInflater.inflate(R.layout.current_date_list_item,null );
            viewHolder.mTvCostClazz=(TextView)convertView.findViewById(R.id.tv_clazz);
            viewHolder.mTvCostDate=(TextView)convertView.findViewById(R.id.tv_date);
            viewHolder.mTvCostMoney=(TextView)convertView.findViewById(R.id.tv_money);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        CostBean costBean=mList.get(position);
        viewHolder.mTvCostClazz.setText(costBean.getClazz());
        viewHolder.mTvCostDate.setText(costBean.getCreateDate());
        viewHolder.mTvCostMoney.setText(DoubleUtils.formatDouble(costBean.getMoney()));
        return convertView;
    }
    private  static class ViewHolder{
        public TextView mTvCostClazz;
        public TextView mTvCostDate;
        public TextView mTvCostMoney;
    }
}
