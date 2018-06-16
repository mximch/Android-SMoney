package com.example.mximc.smoney.bean;


import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

//分类实体
public class CategoryBean extends DataSupport implements  Parcelable {
    private Integer id;
    private String type;        //类型:收入、支出
    private String clazz;       //类别:购物、生活

    public CategoryBean(){};

    public CategoryBean(String type, String clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    protected  CategoryBean(Parcel in){
        type = in.readString();
        clazz = in.readString();
    }

    public  static final Creator<CategoryBean> CREATOR = new Creator<CategoryBean>() {
        @Override
        public CategoryBean createFromParcel(Parcel in) {
            return new CategoryBean(in);
        }

        @Override
        public CategoryBean[] newArray(int size) {
            return new CategoryBean[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(clazz);
    }
}
