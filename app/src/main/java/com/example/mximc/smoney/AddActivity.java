package com.example.mximc.smoney;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.*;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.app.AlertDialog;

import com.example.mximc.smoney.bean.CategoryBean;
import com.example.mximc.smoney.bean.CostBean;
import com.example.mximc.smoney.util.DateUtils;
import com.example.mximc.smoney.util.IntentUtils;
import com.example.mximc.smoney.util.ToastUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddActivity extends BaseActivity {
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.sp_type)
    Spinner spType;
    @BindView(R.id.sp_clazz)
    Spinner spClazz;
    @BindView(R.id.et_money)
    EditText etMoney;
    @BindView(R.id.et_remark)
    EditText etRemark;
    @BindView(R.id.bt_confirm)
    Button btConfirm;
    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;

    private CostBean costBean ;
    CategoryBean category ;

    List<CategoryBean> categoryExpenseList = DataSupport.where("type = ?","支出").find(CategoryBean.class); // 支出数据
    List<CategoryBean> categoryIncomeList = DataSupport.where("type = ?","收入").find(CategoryBean.class); // 收入数据
    List<CategoryBean> saveCategoryList = null;

    List<String> typeList = null;

    List<String> clazzList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);     // 绑定控件到当前视图
        setTypeList();  // 设置类型 List 数据

        costBean = new CostBean();

        String nowDate = DateUtils.getDateTimeInstance(new Date());
        tvDate.setText(nowDate);   // 设置当前日期
        costBean.setCreateDate(nowDate);

        setTypeSpinner(spType);     // 选择分类
        setClazzSpinner(spClazz);

        costBean.setRemark(etRemark.getText().toString());

    }


    /**
     * 点击确定按钮保存数据
     */
    public void save(){
        //判断数据是否合法
        boolean isLegal = isMoney(etMoney.getText().toString());//通过isMoney方法判断数据是否合法返回值
        if(isLegal){
            costBean.save(); //保存数据
            IntentUtils.jump(AddActivity.this,MainActivity.class);  // 返回到 MainActivity
            ToastUtils.toastShort(AddActivity.this,"保存成功");
        }else{
            ToastUtils.toastShort(AddActivity.this,"数据不合法");
        }
    }

    /**
     * 按钮点击事件
     */
    @OnClick({R.id.tv_date, R.id.bt_confirm, R.id.ib_back,R.id.fab_add})//分别对日期、确定、返回、添加按钮添加点击事件
    public void viewOnClick(View v) {
        switch (v.getId()) {
            case R.id.tv_date:
                showDatePickDialog();
                break;
            case R.id.bt_confirm:
                save();
                break;
            case R.id.ib_back:
                IntentUtils.jump(AddActivity.this,MainActivity.class);  // 返回到 MainActivity
                break;
            case R.id.fab_add:
                addCategoryView();
                break;
        }
    }

    /**
     * 增加分类视图
     */
    public void addCategoryView() {
        category = new CategoryBean();  // 初始化分类对象
        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this); //弹窗控件
        LayoutInflater inflater = LayoutInflater.from(AddActivity.this);    //加载布局管理器
        View viewDialog = inflater.inflate(R.layout.activity_category, null);
        final Spinner spNewType = (Spinner) viewDialog.findViewById(R.id.sp_new_type);
        final EditText etNewClazz = (EditText) viewDialog.findViewById(R.id.et_new_clazz);
        setAdapter(typeList, spNewType);

        spNewType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category.setType(typeList.get(position));   // 设置类型
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        builder.setView(viewDialog);
        builder.setTitle("新增分类");
        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("取消", null);
        builder.create();
        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String newClazz = etNewClazz.getText().toString();

                if (newClazz != null && !"".equals(newClazz)) {
                    category.setClazz(newClazz);    // 设置用户输入的类型(一定要放在点击事件里才能取到值)
                    saveCategoryList = DataSupport.where("type = ? and clazz = ?", category.getType(), category.getClazz()).find(CategoryBean.class);
                    if (saveCategoryList.size() > 0) {
                        ToastUtils.toastShort(AddActivity.this, "该记录已存在！");
                    } else {
                        ToastUtils.toastShort(AddActivity.this, "保存成功！");
                        category.save();    // 保存分类对象
                        dialog.dismiss(); // 手动控制 dialog 的关闭
                        IntentUtils.jump(AddActivity.this, AddActivity.class);    // 重新跳转到增加页面，相当于刷新一遍数据
                    }
                }else{
                    ToastUtils.toastShort(AddActivity.this,"分类不能为空！");
                }
            }
        });


    }

    /**
     * 判断用户输入的金额是否合法
     */
    public boolean isMoney(String str){
        Pattern pattern=Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"); // 判断小数点后2位的数字的正则表达式
        Matcher match=pattern.matcher(str);
        if(match.matches() == false){
            return false;
        }else{
            double d = Double.valueOf(str)*100.0;// 扩大一百倍（int 的范围 "-2147483648"-"2147483647"）
            costBean.setMoney((int)d);
            return true;
        }
    }

    /**
     * 设置适配器
     */
    public void setAdapter(List<String> list,Spinner spinner){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner.setAdapter(adapter);
    }

    /**
     * 设置类型 List
     */
    public void setTypeList(){
        typeList  = new ArrayList<String>();
        typeList.add("收入");
        typeList.add("支出");
    }

    //设置类型下拉框
    public void setTypeSpinner(final Spinner spinner){
        setAdapter(typeList,spinner);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                costBean.setType(typeList.get(position));  // 设置收入、支出，endswith指定的后缀
                String type = costBean.getType();
                if(type.endsWith("收入")){
                    clazzList = new ArrayList<String>();
                    for(int i=0;i<categoryIncomeList.size();i++)
                        clazzList.add(categoryIncomeList.get(i).getClazz());
                    setAdapter(clazzList,spClazz);
                }else{
                    clazzList = new ArrayList<String>();
                    for(int i=0;i<categoryExpenseList.size();i++)
                        clazzList.add(categoryExpenseList.get(i).getClazz());
                    setAdapter(clazzList,spClazz);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setClazzSpinner(final Spinner spinner) {
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                costBean.setClazz(clazzList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 显示日期选择器
     */
    public void showDatePickDialog() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(AddActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker dp, int year, int month, int day) {
                        String date = year + "-" + (month+1) + "-" + day + " " + DateUtils.getTimeIntance(new Date());
                        tvDate.setText(date);
                        costBean.setCreateDate(date);
                    }
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)).show();
    }
}