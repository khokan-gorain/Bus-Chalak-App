package com.khokan_gorain_nsubuschalak_app.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.khokan_gorain_nsubuschalak_app.Activitys.MainActivity;
import com.khokan_gorain_nsubuschalak_app.ApiController.ApiController;
import com.khokan_gorain_nsubuschalak_app.ConstantData.Constant;
import com.khokan_gorain_nsubuschalak_app.ModelClass.AreaName;
import com.khokan_gorain_nsubuschalak_app.ModelClass.BusNumber;
import com.khokan_gorain_nsubuschalak_app.ModelClass.LocationUpdateResponse;
import com.khokan_gorain_nsubuschalak_app.R;
import com.khokan_gorain_nsubuschalak_app.ServicesClass.LocationUpdateService;
import com.khokan_gorain_nsubuschalak_app.SharedPreference.BusRunningPathData;
import com.khokan_gorain_nsubuschalak_app.databinding.FragmentStatusBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.NOTIFICATION_SERVICE;


public class StatusFragment extends Fragment {

    public FragmentStatusBinding binding;
    ArrayList<String> areaNamesArray;
    ArrayList<String> busNumberArray;
    ProgressDialog progressDialog;
    FusedLocationProviderClient fusedLocationProviderClient;
    Double latitudeGmap, longitudeGmap;
    BusRunningPathData busRunningPathData;
    ArrayAdapter<String> areaNameArrayAdapter;
    ArrayAdapter<String> busNumber;
    Intent locationUpdateService;

    public StatusFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStatusBinding.inflate(inflater,container,false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

         getAreaName();
         getBusNumber();

         binding.busNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                     Constant.busNumber = String.valueOf(adapterView.getItemAtPosition(i).toString());
             }
         });

         binding.startingPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                     Constant.startingPoint = String.valueOf(adapterView.getItemAtPosition(i).toString());
             }
         });

         binding.endingPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 Constant.endingPoint = String.valueOf(adapterView.getItemAtPosition(i).toString());
             }
         });

         binding.statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                 if(compoundButton.isChecked()){
                   Constant.busRunningStatus = "1";
                     Toast.makeText(getContext(), "Bus is Running...", Toast.LENGTH_LONG).show();
                 } else {
                     Constant.busRunningStatus = "0";
                     Toast.makeText(getContext(), "Bus is Stop.", Toast.LENGTH_LONG).show();
                 }
             }
         });


         binding.dvLocationUpdateBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 updateLocationBtn();
             }
         });

         binding.completeJourneyBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 getActivity().stopService(new Intent(getActivity(),LocationUpdateService.class));
                // Toast.makeText(getContext(), "Stop Location Update", Toast.LENGTH_LONG).show();
             }
         });

         locationUpdateService = new Intent(getActivity(),LocationUpdateService.class);
         return binding.getRoot();
    }


    public void getAreaName(){
        Call<List<AreaName>> call = ApiController
                .getInstance()
                .getApi()
                .getAreaName();

        call.enqueue(new Callback<List<AreaName>>() {
            @Override
            public void onResponse(Call<List<AreaName>> call, Response<List<AreaName>> response) {
                List<AreaName> data = response.body();
                if(data != null){
                    areaNamesArray = new ArrayList<>();
                    for(int i=0;i<data.size();i++)
                    {
                        areaNamesArray.add(data.get(i).getAreaName());
                    }
                    areaNameArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_activated_1,areaNamesArray);
                    binding.startingPoint.setThreshold(2);
                    binding.startingPoint.setAdapter(areaNameArrayAdapter);
                    binding.endingPoint.setThreshold(2);
                    binding.endingPoint.setAdapter(areaNameArrayAdapter);

                } else {
                    Toast.makeText(getContext(), "Unable to fetch location. Please tyr again...", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<AreaName>> call, Throwable t) {
                Toast.makeText(getContext(), "Please check your internet connection and try again...", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getBusNumber(){
        Call<List<BusNumber>> call = ApiController
                .getInstance()
                .getApi()
                .getBusNumber();

        call.enqueue(new Callback<List<BusNumber>>() {
            @Override
            public void onResponse(Call<List<BusNumber>> call, Response<List<BusNumber>> response) {
                List<BusNumber> data = response.body();
                if(data.isEmpty())
                {
                    Toast.makeText(getContext(), "Unable is fetch bus number. Please try again...", Toast.LENGTH_LONG).show();
                } else {
                    busNumberArray = new ArrayList<>();
                    for(int i=0;i<data.size();i++){
                        busNumberArray.add(data.get(i).getBus_number());
                    }
                    busNumber = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,busNumberArray);
                    binding.busNumber.setAdapter(busNumber);
                }
            }

            @Override
            public void onFailure(Call<List<BusNumber>> call, Throwable t) {
                Toast.makeText(getContext(),"Please check your internet connection and please try again...", Toast.LENGTH_LONG).show();
            }
        });
    }



    public void updateLocationBtn(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating...");
        if(Constant.busNumber.isEmpty()){
            Toast.makeText(getContext(), "Please select bus number", Toast.LENGTH_LONG).show();
        } else if(Constant.startingPoint.isEmpty()) {
            Toast.makeText(getContext(), "Please select starting area", Toast.LENGTH_LONG).show();
        } else if(Constant.endingPoint.isEmpty()) {
            Toast.makeText(getContext(), "Please select ending area", Toast.LENGTH_LONG).show();
        } else if(Constant.busRunningStatus.equalsIgnoreCase("1")){
            checkLocationPermission();
        } else {
            progressDialog.show();
            getLastLocation();
        }
    }


    // One time update current location
    public void getLastLocation(){
        if(ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null)
                    {
                        latitudeGmap = location.getLatitude();
                        longitudeGmap = location.getLongitude();
                        if(latitudeGmap.isNaN()  && latitudeGmap.isNaN()){
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Sorry we are unable to fetch your location. Please try after some time...", Toast.LENGTH_LONG).show();
                        } else {
                            currentLocationUpdate();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Sorry we are unable to fetch your location. Please try after some time...", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Please check your location permission and try again...", Toast.LENGTH_LONG).show();
        }
    }

    public void currentLocationUpdate(){
        Call<LocationUpdateResponse> call = ApiController
                .getInstance()
                .getApi()
                .BusLocationUpdate(Constant.dvFullName,Constant.busNumber,Constant.startingPoint,Constant.endingPoint,
                        Constant.busRunningStatus,Constant.dvPhoneNumber,Constant.dvId,Constant.dvProfileImg,latitudeGmap,longitudeGmap);

        call.enqueue(new Callback<LocationUpdateResponse>() {
            @Override
            public void onResponse(Call<LocationUpdateResponse> call, Response<LocationUpdateResponse> response) {
                LocationUpdateResponse data = response.body();
                if(data != null){
                    if(data.getMessage().equalsIgnoreCase("success")){
                        progressDialog.dismiss();
                        busRunningPathData = new BusRunningPathData(getContext());
                        busRunningPathData.setBusNumber(Constant.busNumber);
                        busRunningPathData.setStartingPoint(Constant.startingPoint);
                        busRunningPathData.setEndingPoint(Constant.endingPoint);
                        Toast.makeText(getContext(), "Your location update successful...", Toast.LENGTH_LONG).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Something went wrong. Your location not updated. Please try after some time...", Toast.LENGTH_LONG).show();
                    }

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Something went wrong. Please try again...", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<LocationUpdateResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Something went wrong. Please try again...", Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean gpsIsCheck(){
        Boolean result;
        result = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED;
        return result;
    }

    private void checkLocationPermission(){
        if(ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
         ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                if(ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getActivity().startService(locationUpdateService);
                } else {
                    askForBackgroundLocation();
                    //Toast.makeText(getContext(), "Please Allow All The Time Permission", Toast.LENGTH_LONG).show();

                }
            } else {
                getActivity().startService(locationUpdateService);
            }
        } else {
            askForLocationPermission();
           // Toast.makeText(getContext(), "Please Allow Location Permission", Toast.LENGTH_LONG).show();
        }
    }

    private void askForLocationPermission(){
        Dexter.withContext(getContext())
                .withPermission(ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getActivity().startService(locationUpdateService);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",getContext().getPackageName(),null);
                        intent.setData(uri);
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void askForBackgroundLocation(){
        Dexter.withContext(getContext())
                .withPermission(ACCESS_BACKGROUND_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getActivity().startService(locationUpdateService);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",getContext().getPackageName(),null);
                        intent.setData(uri);
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }


}