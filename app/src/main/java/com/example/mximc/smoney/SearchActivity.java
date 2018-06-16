package com.example.mximc.smoney;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mximc.smoney.adapter.ListViewCompat;
import com.example.mximc.smoney.adapter.PrivateListingAdapter;
import com.example.mximc.smoney.bean.CostBean;
import com.example.mximc.smoney.util.IntentUtils;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class SearchActivity extends BaseActivity{

    @BindView(R.id.backBtn)
    ImageButton ibBack; //和增加页面和饼图页面的不同
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.lv_search)
    ListViewCompat lvSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this); //绑定控件到当前视图
        showCostData(DataSupport.findAll(CostBean.class));
    }

    /*
    * 按钮点击事件
    * */
    @OnClick({R.id.tv_search,R.id.backBtn})
    public void viewOnCLick(View v){
        switch (v.getId()){
            case R.id.tv_search:
                showDatePickDialog();
                break;
            case R.id.backBtn:
                IntentUtils.jump(SearchActivity.this, MainActivity.class);
                break;
        }
    }

    /*
    * 收支数据每一条数据的数据的点击事件，跳转到更新页面
    * */

    @OnItemClick(R.id.lv_search)
    public void onItemClick(int position){
        Intent intent=new Intent(SearchActivity.this,UpdateActivity.class);
        Bundle bundle=new Bundle();
        bundle.putParcelable("costBean", lvSearch.costBean);    //序列化一个对象
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /*
    * 显示消费数据
    * */

    private void showCostData(List<CostBean> costBeanList) {
        PrivateListingAdapter mAdapter=new PrivateListingAdapter(this,costBeanList);
        lvSearch.setAdapter(mAdapter);
    }
    /*
    * 显示日期选择器
    * */
    private void showDatePickDialog() {
        Calendar c=Calendar.getInstance();
        new  DatePickerDialog(SearchActivity.this,
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker dp, int year, int month, int day) {
                String date = year + "-" + (month + 1) + "-" + day;
                tvSearch.setText(date);
                showCostData(DataSupport.where("createDate like ?", "%" + date + "%").find(CostBean.class));
            }
        },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)).show();//向DatePickerDialog传入日期
    }
}
