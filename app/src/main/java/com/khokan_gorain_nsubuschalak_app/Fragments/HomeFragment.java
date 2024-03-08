package com.khokan_gorain_nsubuschalak_app.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.transition.Transition;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.khokan_gorain_nsubuschalak_app.AdapterClass.SliderMessageAd;
import com.khokan_gorain_nsubuschalak_app.ApiController.ApiController;
import com.khokan_gorain_nsubuschalak_app.ConstantData.Constant;
import com.khokan_gorain_nsubuschalak_app.ModelClass.DriverNoticeMessage;
import com.khokan_gorain_nsubuschalak_app.ModelClass.ImageSlider;
import com.khokan_gorain_nsubuschalak_app.R;
import com.khokan_gorain_nsubuschalak_app.ServicesClass.LocationUpdateService;
import com.khokan_gorain_nsubuschalak_app.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    ArrayList<SlideModel> sliderList;
    SliderMessageAd sliderMessageAd;
    ArrayList driverNoticeMessage;
    private static final int BACKGROUND_LOCATION_PERMISSION_CODE = 4567;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = FragmentHomeBinding.inflate(inflater,container,false);

        processData(); // Process Data Function
        getDriverNoticeMessage(); // Notice Message Function
       // askPermissionForBackgroundUsage();

        return binding.getRoot();

    }

    public void processData() {
        Call<List<ImageSlider>> call = ApiController
                .getInstance()
                .getApi()
                .getSliderImage();

        call.enqueue(new Callback<List<ImageSlider>>() {
            @Override
            public void onResponse(Call<List<ImageSlider>> call, Response<List<ImageSlider>> response) {
                List<ImageSlider> list = response.body();
                if(list != null){
                    binding.homeFragmentLyt.setVisibility(View.VISIBLE);
                    binding.shimmerLyt.setVisibility(View.GONE);
                    sliderList = new ArrayList<>();
                    for(int i=0;i<list.size();i++) {
                        sliderList.add(new SlideModel(Constant.BASE_URL+list.get(i).getImg(),ScaleTypes.FIT));
                    }

                    binding.imageSlider.setImageList(sliderList);
                } else {
                    binding.homeFragmentLyt.setVisibility(View.GONE);
                    binding.shimmerLyt.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onFailure(Call<List<ImageSlider>> call, Throwable t) {
                Toast.makeText(getContext(), "Please check your internet connection...", Toast.LENGTH_LONG).show();
            }
        });

    }


    public void getDriverNoticeMessage() {
        Call<List<DriverNoticeMessage>> call = ApiController
                .getInstance()
                .getApi()
                .getDriverNoticeMessage();

        call.enqueue(new Callback<List<DriverNoticeMessage>>() {
            @Override
            public void onResponse(Call<List<DriverNoticeMessage>> call, Response<List<DriverNoticeMessage>> response) {
                List<DriverNoticeMessage> data = response.body();
                if(data.isEmpty()){
                    Toast.makeText(getContext(), "Unable to fetch data. Please try again...", Toast.LENGTH_LONG).show();
                } else {
                    driverNoticeMessage = new ArrayList();
                    for(int i=0;i<data.size();i++){
                        driverNoticeMessage.add(data.get(i).getMessage());
                    }
                    sliderMessageAd = new SliderMessageAd(getContext(),driverNoticeMessage);
                    binding.viewPager.setAdapter(sliderMessageAd);
                    binding.dot.setViewPager(binding.viewPager);

                }
            }
            @Override
            public void onFailure(Call<List<DriverNoticeMessage>> call, Throwable t) {
                Toast.makeText(getContext(), "Please check your internet connection...", Toast.LENGTH_LONG).show();
            }
        });
    }

}