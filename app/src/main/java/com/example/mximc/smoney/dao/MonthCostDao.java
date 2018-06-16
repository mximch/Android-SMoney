package com.example.mximc.smoney.dao;

import com.example.mximc.smoney.bean.CategoryBean;
import com.example.mximc.smoney.bean.CostBean;
import com.example.mximc.smoney.bean.MonthCostVO;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MonthCostDao {
    public static List<MonthCostVO> getMonthExpense(String date){
        List<CostBean> costBeansList = DataSupport.where("createDate like ? and type = ?","%" + date + "%","支出").find(CostBean.class);
        List<CategoryBean> categoryBeanList = DataSupport.where("type = ?","支出").find(CategoryBean.class);
        List<MonthCostVO> monthCostVOList = new ArrayList<MonthCostVO>();
        MonthCostVO monthCostVO=null;
        int sum;
        //计算每一类的总金额
        for (int j=0;j<categoryBeanList.size();j++){
            sum=0;
            monthCostVO=new MonthCostVO();
            for (int i=0;i<costBeansList.size();i++){
                if (costBeansList.get(i).getClazz().equals(categoryBeanList.get(j).getClazz())){
                    sum +=costBeansList.get(i).getMoney();
                }

            }
            monthCostVO.setName(categoryBeanList.get(j).getClazz());
            monthCostVO.setValue(sum);
            monthCostVOList.add(monthCostVO);
        }
        return monthCostVOList;
    }
}
