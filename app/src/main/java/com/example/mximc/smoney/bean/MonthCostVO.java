package com.example.mximc.smoney.bean;
/*
* 月收入视图对象
* */
public class MonthCostVO {
    String name;    //支出类型
    int value;  //金额

    public MonthCostVO(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
