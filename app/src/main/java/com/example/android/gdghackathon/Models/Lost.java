package com.example.android.gdghackathon.Models;

/**
 * Created by sharaddadhich on 12/11/17.
 */

public class Lost {
    String name,mobileNo,clothes;

    public Lost(){}

    public Lost(String name, String mobileNo, String clothes) {
        this.name = name;
        this.mobileNo = mobileNo;
        this.clothes = clothes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getClothes() {
        return clothes;
    }

    public void setClothes(String clothes) {
        this.clothes = clothes;
    }
}
