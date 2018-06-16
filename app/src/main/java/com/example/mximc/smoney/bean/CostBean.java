package com.example.mximc.smoney.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.mximc.smoney.view.SlideView;

import org.litepal.crud.DataSupport;
//消费实体
public class CostBean extends DataSupport implements Parcelable {
    private Integer id;
    private int money; //金额
    private String remark;  //备注
    private String type;    //类型：收入、支出
    private String clazz;   //类别：购物、日常
    private String createDate;  //支出
    public SlideView slideView;

    public CostBean(){}

    protected CostBean(Parcel in){
        money =in.readInt();
        remark = in.readString();
        type = in.readString();
        clazz = in.readString();
        createDate = in.readString();
    }

    public static final Creator<CostBean> CREATOR = new Creator<CostBean>() {
        @Override
        public CostBean createFromParcel(Parcel in) {
            return new CostBean(in);
        }

        @Override
        public CostBean[] newArray(int size) {
            return new CostBean[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @Override
    public  int describeContents(){
        return  0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(money);
        dest.writeString(remark);
        dest.writeString(type);
        dest.writeString(clazz);
        dest.writeString(createDate);
    }
}
