package com.example.mximc.smoney;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mximc.smoney.adapter.ListViewCompat;
import com.example.mximc.smoney.adapter.PrivateListingAdapter;
import com.example.mximc.smoney.bean.CategoryBean;
import com.example.mximc.smoney.bean.CostBean;
import com.example.mximc.smoney.util.DateUtils;
import com.example.mximc.smoney.util.DoubleUtils;
import com.example.mximc.smoney.util.IntentUtils;
import com.example.mximc.smoney.util.ToastUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends BaseActivity{
    private List<CostBean> mCostBeanList;

    @BindView(R.id.tv_current_date)
    TextView tvCurrentDate;
    @BindView(R.id.tv_month_income)
    TextView tvMonthIncome;     //月支出
    @BindView(R.id.tv_month_expense)
    TextView tvMonthExpense;    //月支出
    @BindView(R.id.tv_surplus)
    TextView tvSurplusMonth;     //节余的月份
    @BindView(R.id.tv_total_income)
    TextView tvTotalIncome;
    @BindView(R.id.tv_total_expense)
    TextView tvTotalExpense;
    @BindView(R.id.tv_day_income_expense)
    TextView tvDayIncomeExpense;
    @BindView(R.id.tv_surplus_money)
    TextView tvSurplusMoney;    // 节余的金额

    @BindView(R.id.ib_add)
    ImageButton ibAdd;
    @BindView(R.id.ib_search)
    ImageButton ibSearch;
    @BindView(R.id.ib_pie)
    ImageButton ibPie;
    @BindView(R.id.ib_lock)
    ImageButton ibResetPwd;

    @BindView(R.id.lv_day_cost)
    ListViewCompat lvCost;

    private String currentDate= DateUtils.getCurrentDate();
    private String currentYearAndMonth=DateUtils.getCurrentYearAndMonth();
    private Double totalIncome=0.0; //总收入
    private Double totalExpense=0.0;   //总支出

    private SharedPreferences sp;
    private String pwd;     //手势密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this); //绑定控件到当前视图

        sp=getSharedPreferences("GuestureLockSP",MODE_PRIVATE );   //获取手势密码
        pwd=sp.getString("pwd", null);

        initCategory(); //初始化分类数据
        setViewValue();
        getCurrentMonthIncome();    //本月收入
        getCurrentMonthExpense();   //本月支出
        getCurrentMonthSurplus();   //本月工资节余
        getCurrentDayIncomeAndExpense();    //当天收支情况

        mCostBeanList=new ArrayList<>();
        initCostData(); //初始化消费数据
        initAdapter();
    }

    /*
    * 初始化适配器
    * */
    public void initAdapter(){
        PrivateListingAdapter mAdapter=new PrivateListingAdapter(this,mCostBeanList);
        lvCost.setAdapter(mAdapter);
    }

    private void setViewValue(){
        final String month=DateUtils.getCurrentMonth();
        tvCurrentDate.setText(DateUtils.getCurrentMonthAndDate());
        tvMonthIncome.setText(month+"月收入");
        tvMonthExpense.setText(month+"月支出");
        tvSurplusMonth.setText(month+"月工资节余");
    }

    public void getCurrentMonthIncome(){
        //获取这个月的所有收入进行累加并显示
        List<CostBean> list= DataSupport.where("createDate like ? and type=?","%"+currentYearAndMonth+"%","收入").find(CostBean.class);
        for (int i = 0;i<list.size();i++){
            totalIncome+=list.get(i).getMoney();
        }
        tvTotalIncome.setText(DoubleUtils.formatDouble(totalIncome/100.0)+"");
    }
    public void  getCurrentMonthExpense(){
        //获取这个月的所有支出进行累计并显示
        List<CostBean> list=DataSupport.where("createDate like ? and type=?","%"+currentYearAndMonth+"%","支出").find(CostBean.class);
        for (int i = 0;i<list.size();i++){
            totalExpense+=list.get(i).getMoney();
        }
        tvTotalExpense.setText(DoubleUtils.formatDouble(totalExpense/100.0)+"");
    }

    /*
    * 获取当天的收入和支出
    * */
    public void  getCurrentDayIncomeAndExpense(){
        double dayOfIncome=0.0;
        double dayOfExpense=0.0;
        List<CostBean> doiList=DataSupport.where("createDate like ? and type=?","%"+currentDate+"%","收入").find(CostBean.class);
        List<CostBean> doeList=DataSupport.where("createDate like ? and type=?","%"+currentDate+"%","支出").find(CostBean.class);
        for (int i=0;i<doiList.size();i++){
            dayOfIncome+=doiList.get(i).getMoney();
        }
        for (int i=0;i<doeList.size();i++){
            dayOfExpense+=doeList.get(i).getMoney();
        }
        tvDayIncomeExpense.setText("收入："+DoubleUtils.formatDouble(dayOfIncome/100.0)+"支出："+DoubleUtils.formatDouble(dayOfExpense/100.0));

    }

    /*
    * 收入数据每一条数据的点击事件，跳转到更新页面
    * */
    @OnItemClick(R.id.lv_day_cost)
    public void onItemClick(int position){
        Intent intent=new Intent(MainActivity.this,UpdateActivity.class);
        Bundle bundle=new Bundle();
        bundle.putParcelable("costBean",lvCost.costBean );  //序列化一个对象，对数据进行传递
        intent.putExtras(bundle);   //把该对象传到另一个页面
        startActivity(intent);
    }

    //获取当前月的节余
    public void getCurrentMonthSurplus(){
        tvSurplusMoney.setText(DoubleUtils.formatDouble((totalIncome-totalExpense)/100.0)+"");
    }

    private void  initCostData(){
        //获取当天的收支情况
        mCostBeanList=DataSupport.where("createDate like ? ","%"+currentDate+"%").find(CostBean.class);
    }

    /*
    * 搜索、查询、增加按钮的点击事件
    * */
    @OnClick({R.id.ib_add,R.id.ib_search,R.id.ib_pie,R.id.ib_lock})
    public void viewOnClick(View v){
        switch ((v.getId())){
            case R.id.ib_add:
                Intent addIntent=new  Intent(MainActivity.this,AddActivity.class);
                startActivity(addIntent);
                break;
            case R.id.ib_search:
                Intent searchIntent=new  Intent(MainActivity.this, SearchActivity.class);
                startActivity(searchIntent);
                break;
            case R.id.ib_pie:
                Intent pieIntent=new  Intent(MainActivity.this,PieChartActivity.class);
                startActivity(pieIntent);
                break;
            case R.id.ib_lock:
                if (TextUtils.isEmpty(pwd)){
                    ToastUtils.toastShort(MainActivity.this,"请先设置密码" );
                }else {
                    IntentUtils.jump(MainActivity.this,ResetPwdActivity.class );
                }
                break;
        }
    }

    /*
    * 初始化分类数据
    * */
    public void initCategory(){
        List<CategoryBean> category1=DataSupport.where("clazz=?","购物").find(CategoryBean.class);
        if (category1.size()==0)
            new CategoryBean("支出","购物").save();
        List<CategoryBean> category2=DataSupport.where("clazz=?","餐饮").find(CategoryBean.class);
        if (category2.size()==0)
            new CategoryBean("支出","餐饮").save();
        List<CategoryBean> category3=DataSupport.where("clazz=?","交通").find(CategoryBean.class);
        if (category3.size()==0)
            new CategoryBean("支出","交通").save();
        List<CategoryBean> category4=DataSupport.where("clazz=?","其他").find(CategoryBean.class);
        if (category4.size()==0)
            new CategoryBean("支出","其他").save();
        List<CategoryBean> category5=DataSupport.where("clazz=?","工资").find(CategoryBean.class);
        if (category5.size()==0)
            new CategoryBean("收入","工资").save();
        List<CategoryBean> category6=DataSupport.where("clazz=?","奖金").find(CategoryBean.class);
        if (category6.size()==0)
            new CategoryBean("收入","奖金").save();
        List<CategoryBean> category7=DataSupport.where("clazz=?","兼职").find(CategoryBean.class);
        if (category7.size()==0)
            new CategoryBean("收入","兼职").save();
    }
}
