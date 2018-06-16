package com.example.mximc.smoney;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Matrix;
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
import android.widget.Toast;

import com.example.mximc.smoney.bean.CategoryBean;
import com.example.mximc.smoney.bean.CostBean;
import com.example.mximc.smoney.util.DateUtils;
import com.example.mximc.smoney.util.DoubleUtils;
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

public class UpdateActivity extends BaseActivity {
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.sp_type)
    Spinner spType; //下拉
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
    FloatingActionButton fabAdd;//浮动按钮

    private CostBean costBean;
    CategoryBean category;
    String nowDate = null;

    List<CategoryBean> categoryExpenseList = DataSupport.where("type=?", "支出").find(CategoryBean.class); // 支出数据
    List<CategoryBean> categoryIncomeList = DataSupport.where("type=?", "收入").find(CategoryBean.class); // 收入数据
    List<CategoryBean> saveCategoryList = null;

    List<String> typeList = null;

    private List<String> clazzList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this); //绑定控件到当前视图
        setTypeList();  //设置类型List数据

        initCostBean();

        nowDate = costBean.getCreateDate();

        //根据传来的costBean对象设置数据
        tvDate.setText(costBean.getCreateDate());
        etMoney.setText(DoubleUtils.formatDouble(costBean.getMoney()/100.0));
        etRemark.setText(costBean.getRemark());

        setTypeSpinner(spType); //选择分类
        setClazzSpinner(spClazz);
    }

    /*获取主页传来的costBean对象*/
    private void initCostBean() {
        Intent intent = this.getIntent();
        costBean = (CostBean) intent.getParcelableExtra("costBean");
    }

    /*
     * 点击确定按钮保存数据
     * */

    public void save() {
        //判断数据是否合法
        boolean isLegal = isMoney(etMoney.getText().toString());
        if (isLegal) {
            costBean.setRemark(etRemark.getText().toString());

            ContentValues values = new ContentValues();
            values.put("money", costBean.getMoney());
            values.put("remark", costBean.getRemark());
            values.put("type", costBean.getType());
            values.put("clazz", costBean.getClazz());
            values.put("money", costBean.getMoney());
            values.put("createDate", costBean.getCreateDate());
            DataSupport.updateAll(CostBean.class, values, "createDate=?", nowDate);   //更新数据
            IntentUtils.jump(UpdateActivity.this, MainActivity.class); //返回到MainActivity
            Toast.makeText(UpdateActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(UpdateActivity.this, "数据不合法", Toast.LENGTH_LONG);
        }
    }

    /*
     * 按钮点击事件
     * */
    @OnClick({R.id.tv_date, R.id.bt_confirm, R.id.ib_back, R.id.fab_add})
    public void viewOnClick(View v) {
        switch (v.getId()) {
            case R.id.tv_date:
                showDatePickDialog();
                break;
            case R.id.bt_confirm:
                save();
                break;
            case R.id.ib_back:
                IntentUtils.jump(UpdateActivity.this, MainActivity.class);
                break;
            case R.id.fab_add:
                addCategoryView();
                break;
        }
    }

    /*
     * 增加分类视图
     * */
    private void addCategoryView() {
        category = new CategoryBean();    //初始化分类对象
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
        LayoutInflater inflater = LayoutInflater.from(UpdateActivity.this);
        View viewDialog = inflater.inflate(R.layout.activity_category, null);
        final Spinner spNewTypes = (Spinner) viewDialog.findViewById(R.id.sp_new_type);
        final EditText etNewClazz = (EditText) viewDialog.findViewById(R.id.et_new_clazz);
        setAdapter(typeList, spNewTypes);
        spNewTypes.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category.setType(typeList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                    category.setClazz(newClazz);    //设置用户输入的类型（一定要在点击事件里才能取到值）
                    saveCategoryList = DataSupport.where("type=? and clazz = ?", category.getClazz()).find(CategoryBean.class);
                    if (saveCategoryList.size() > 0) {
                        ToastUtils.toastLong(UpdateActivity.this, "该记录已经存在！");
                    } else {
                        ToastUtils.toastLong(UpdateActivity.this, "保存成功");
                        category.save();    //保存分类对象
                        dialog.dismiss();   //手动控制dialog的关闭
                        IntentUtils.jump(UpdateActivity.this, UpdateActivity.class);  //重新跳转到增加的页面，相当于刷新一遍数据
                    }
                } else {
                    ToastUtils.toastLong(UpdateActivity.this, "分类不能为空！");
                }
            }
        });
    }

    /*
     * 判断用户输入的金额是否合法
     * */
    private boolean isMoney(String str) {
        Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"); //判断小数点后两位的数字的正则表达式
        Matcher match = pattern.matcher(str);
        if (match.matches() == false) {
            return false;
        } else {
            double d = Double.valueOf(str) * 100.0; //扩大一百倍（int 的范围 "-2147483648"-"2147483647"）
            costBean.setMoney((int) d);
            return true;
        }
    }

    /*
     * 设置适配器*/
    private void setAdapter(List<String> list, Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner.setAdapter(adapter);
    }

    /*
     * 设置类型
     * */
    private void setTypeList() {
        typeList = new ArrayList<String>();
        typeList.add("收入");
        typeList.add("支出");
    }

    private void setTypeSpinner(final Spinner spinner) {
        setAdapter(typeList, spinner);
        if (costBean.getType().equals("支出")) {
            spinner.setSelection(1, true);
            setExpenseAdapter();
        } else {
            spinner.setSelection(0, true);
            setIncomeAdapter();
        }
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                costBean.setType(typeList.get(position));   //设置收入、支出
                String type = costBean.getType();
                if (type.endsWith("收入")) {
                    setIncomeAdapter();
                } else {
                    setExpenseAdapter();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setIncomeAdapter() {
        clazzList = new ArrayList<String>();
        for (int i = 0; i < categoryIncomeList.size(); i++)
            clazzList.add(categoryIncomeList.get(i).getClazz());
        setAdapter(clazzList, spClazz);
    }
    private void setExpenseAdapter() {
        clazzList = new ArrayList<String>();
        for (int i = 0; i < categoryExpenseList.size(); i++)
            clazzList.add(categoryExpenseList.get(i).getClazz());
        setAdapter(clazzList, spClazz);
    }

    private void setClazzSpinner(final Spinner spinner) {
        for (int i = 0; i < clazzList.size(); i++) {
            if (clazzList.get(i).equals(costBean.getClazz())) {
                spinner.setSelection(i, true);
            }
        }
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                costBean.setClazz(clazzList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /*
     * 显示日期选择器
     * */
    private void showDatePickDialog() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(UpdateActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker dp, int year, int mounth, int day) {
                String date=year+"-"+(mounth+1)+"-"+day+""+ DateUtils.getTimeIntance(new Date());
                tvDate.setText(date);
                costBean.setCreateDate(date);
            }
        },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)).show();
    }
}
