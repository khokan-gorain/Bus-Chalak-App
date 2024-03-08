package com.khokan_gorain_nsubuschalak_app.SharedPreference;

import android.content.Context;
import android.content.SharedPreferences;

public class UserLoginData {

    private String userName,password;
    Context context;
    SharedPreferences sharedPreferences;


    public UserLoginData(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("userLoginDetails",context.MODE_PRIVATE);
    }

    public String getUserName() {
        userName = sharedPreferences.getString("name","");
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        sharedPreferences.edit().putString("name",userName).commit();
    }

    public String getPassword() {
        password = sharedPreferences.getString("password","");
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        sharedPreferences.edit().putString("password",password).commit();
    }

    public void logOutUser()
    {
        sharedPreferences.edit().clear().commit();
    }
}
