package com.khokan_gorain_nsubuschalak_app.SharedPreference;

import android.content.Context;
import android.content.SharedPreferences;

public class BusRunningPathData {

    private String startingPoint,endingPoint,busNumber;
    Context context;
    SharedPreferences sharedPreferences;

    public BusRunningPathData( Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("BusRunningPath",context.MODE_PRIVATE);
    }

    public String getStartingPoint() {
        startingPoint = sharedPreferences.getString("startingPoint","");
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
        sharedPreferences.edit().putString("startingPoint",startingPoint).commit();
    }

    public String getEndingPoint() {
        endingPoint = sharedPreferences.getString("endingPoint","");
        return endingPoint;
    }

    public void setEndingPoint(String endingPoint) {
        this.endingPoint = endingPoint;
        sharedPreferences.edit().putString("endingPoint",endingPoint).commit();
    }

    public String getBusNumber() {
        busNumber = sharedPreferences.getString("busNumber","");
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
        sharedPreferences.edit().putString("busNumber",busNumber).commit();
    }

    public void completeJourney() {
        sharedPreferences.edit().clear().commit();
    }
}
