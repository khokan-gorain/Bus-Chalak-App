package com.khokan_gorain_nsubuschalak_app.ModelClass;

import com.khokan_gorain_nsubuschalak_app.ConstantData.Constant;

public class DriverInfo {

     int  id,status,driver_id;
     String driver_name,phone_number,email_id,password,pro_img,dv_full_name,dvAddress;

    public DriverInfo() {
    }

    public  DriverInfo(int id, int status, int driver_id, String driver_name, String phone_number, String email_id, String password, String pro_img,String dv_full_name,String  dvAddress) {
        this.id = id;
        this.status = status;
        this.driver_id = driver_id;
        this.driver_name = driver_name;
        this.phone_number = phone_number;
        this.email_id = email_id;
        this.password = password;
        this.pro_img = pro_img;
        this.dv_full_name = dv_full_name;
        this.dvAddress = dvAddress;
    }

    public int getId() {
        return id;
    }

    public  void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPro_img() {
        return pro_img;
    }

    public void setPro_img(String pro_img) {
        this.pro_img = pro_img;
    }

    public String getDv_full_name() {
        return dv_full_name;
    }

    public void setDv_full_name(String dv_full_name) {
        this.dv_full_name = dv_full_name;
    }

    public String getDvAddress() {
        return dvAddress;
    }

    public void setDvAddress(String dvAddress) {
        this.dvAddress = dvAddress;
    }
}
