package com.khokan_gorain_nsubuschalak_app.ServicesClass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.khokan_gorain_nsubuschalak_app.Activitys.MainActivity;
import com.khokan_gorain_nsubuschalak_app.ApiController.ApiController;
import com.khokan_gorain_nsubuschalak_app.ConstantData.Constant;
import com.khokan_gorain_nsubuschalak_app.ModelClass.LocationUpdateResponse;
import com.khokan_gorain_nsubuschalak_app.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationUpdateService extends Service {

    FusedLocationProviderClient fusedLocationProviderClient;
    Double latitudeGmap, longitudeGmap;
    String locationUpdateResponse = "";
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    Notification notification;
    Bitmap notificationImg;
    PendingIntent pendingIntent;
    NotificationManager notificationManager;
    private final int REQUEST_CHECK_CODE = 1001;
    private int NOTIFY_ONCE = 0;
    private static final String NOTIFICATION_CHANNEL_ID = "Location Update";
    private static final int UPDATE_NOTIFICATION_ID = 100;
    


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        updateLocation();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        locationRequest = new LocationRequest.Builder(5000)
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

         locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                continueLocationUpdateDb(locationResult);
            }
        };

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.bus,null);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        notificationImg = bitmapDrawable.getBitmap();

        Intent iNotify = new Intent(getApplicationContext(), MainActivity.class);
        iNotify.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivities(getApplicationContext(),REQUEST_CHECK_CODE,
                new Intent[]{iNotify},PendingIntent.FLAG_MUTABLE);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Location Update",
                    NotificationManager.IMPORTANCE_HIGH));
        }


    }

    
    public void updateLocation() {

        LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .build();


        SettingsClient settingsClient = LocationServices.getSettingsClient(getApplicationContext());
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                if (task.isSuccessful()) {
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                } else {
                    if(task.getException() instanceof ResolvableApiException){
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) task.getException();
                            resolvableApiException.startResolutionForResult(Constant.activity,REQUEST_CHECK_CODE);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(LocationUpdateService.this, "Something went wrong. Please try again...", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    public void continueLocationUpdateDb(LocationResult locationResult) {
       Double latitude = locationResult.getLastLocation().getLatitude();
       Double longitude = locationResult.getLastLocation().getLongitude();

       // Toast.makeText(this, latitude.toString()+" "+longitude.toString(), Toast.LENGTH_SHORT).show();
        Call<LocationUpdateResponse> call = ApiController
                .getInstance()
                .getApi()
                .BusLocationUpdate(Constant.dvFullName,Constant.busNumber,Constant.startingPoint,Constant.endingPoint,
                        Constant.busRunningStatus,Constant.dvPhoneNumber,Constant.dvId, Constant.dvProfileImg,latitude,longitude);

        call.enqueue(new Callback<LocationUpdateResponse>() {
            @Override
            public void onResponse(Call<LocationUpdateResponse> call, Response<LocationUpdateResponse> response) {
                LocationUpdateResponse data = response.body();
                if(data != null){
                    locationUpdateResponse = data.getMessage();
                   // Toast.makeText(LocationUpdateService.this, latitudeGmap.toString(), Toast.LENGTH_SHORT).show();
                    if(NOTIFY_ONCE == 0){
                        startForeground(UPDATE_NOTIFICATION_ID,locationUpdateNotification(locationUpdateResponse));
                    }

                } else {
                    Toast.makeText(LocationUpdateService.this, "Something went wrong. Please try again...", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<LocationUpdateResponse> call, Throwable t) {
                Toast.makeText(LocationUpdateService.this, "Something went wrong. Please try again...", Toast.LENGTH_LONG).show();
            }
        });
    }

    public Notification locationUpdateNotification(String locationUpdateResponse){

        NOTIFY_ONCE = 1; // for one time notification

        if(locationUpdateResponse.equalsIgnoreCase("success")){
            Toast.makeText(getApplicationContext(), "Start Location Update", Toast.LENGTH_LONG).show();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notification = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_logo)
                        .setLargeIcon(notificationImg)
                        .setContentTitle("Bus is running...")
                        .setContentText(Constant.startingPoint+" To "+Constant.endingPoint)
                        .setOngoing(true)
                        .setContentIntent(pendingIntent)
                        .setChannelId(NOTIFICATION_CHANNEL_ID)
                        .build();

            } else {
                notification = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_logo)
                        .setLargeIcon(notificationImg)
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Bus is running...")
                        .setContentText(Constant.startingPoint+" To "+Constant.endingPoint)
                        .setOngoing(true)
                        .build();
            }
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Toast.makeText(getApplicationContext(), "Location Not Updating...", Toast.LENGTH_LONG).show();
                notification = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_logo)
                        .setLargeIcon(notificationImg)
                        .setContentTitle("Something went wrong. Location Not Updating...")
                        .setContentText(Constant.startingPoint+" To "+Constant.endingPoint)
                        .setOngoing(true)
                        .setContentIntent(pendingIntent)
                        .setChannelId(NOTIFICATION_CHANNEL_ID)
                        .build();
            } else {
                notification = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_logo)
                        .setLargeIcon(notificationImg)
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Something went wrong. Location Not Updating...")
                        .setContentText(Constant.startingPoint+" To "+Constant.endingPoint)
                        .setOngoing(true)
                        .build();
            }
        }
        return notification;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(NOTIFY_ONCE == 1){
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            notificationManager.cancel(UPDATE_NOTIFICATION_ID);
            NOTIFY_ONCE = 0;
            Toast.makeText(getApplicationContext(), "Stop Location Update", Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(this, "Already stop the journey. Please start journey first...", Toast.LENGTH_LONG).show();
        }

    }

}
