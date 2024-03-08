package com.khokan_gorain_nsubuschalak_app.ServicesClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class LocationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

//        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
//            Intent locationUpdate = new Intent(context,LocationUpdateService.class);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(locationUpdate);
//            } else {
//                context.startActivity(locationUpdate);
//            }
//
//        }
    }
}
