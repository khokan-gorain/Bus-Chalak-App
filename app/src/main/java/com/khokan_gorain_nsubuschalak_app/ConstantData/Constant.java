package com.khokan_gorain_nsubuschalak_app.ConstantData;

import android.app.Activity;
import android.util.Log;

import retrofit2.http.PUT;

public class Constant {

    // ------------------------------APIs-----------------------------------//
    public static final String MAIN_URL = "http://stdroom.in/nsu/";  // Main Url
    public static final String BASE_URL = MAIN_URL + "all_api/nsu_bus_chalak/"; // BaseUrl


    //-------------------------------VARIABLE-------------------------//
    public static Activity activity;
    public static String startingPoint = "";
    public static String endingPoint = "";
    public static String busNumber = "";
    public static String dvFullName = "";
    public static String dvEmailId = "";
    public static String dvPhoneNumber = "";
    public static String dvProfileImg = "";
    public static String dvAddress = "";
    public static String  busRunningStatus = "";
    public static int dvId = 0;





}
