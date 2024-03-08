package com.khokan_gorain_nsubuschalak_app.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.khokan_gorain_nsubuschalak_app.Activitys.MainActivity;
import com.khokan_gorain_nsubuschalak_app.Activitys.UcropperActivity;
import com.khokan_gorain_nsubuschalak_app.ApiController.ApiController;
import com.khokan_gorain_nsubuschalak_app.ConstantData.Constant;
import com.khokan_gorain_nsubuschalak_app.ModelClass.BusNumber;
import com.khokan_gorain_nsubuschalak_app.ModelClass.DriverInfo;
import com.khokan_gorain_nsubuschalak_app.ModelClass.DvProfileUpdateResponse;
import com.khokan_gorain_nsubuschalak_app.R;
import com.khokan_gorain_nsubuschalak_app.SharedPreference.UserLoginData;
import com.khokan_gorain_nsubuschalak_app.databinding.FragmentProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    ActivityResultLauncher<String> cropImage;
    Bitmap bitmap;
    Uri uri;
    String encodedDvProfileImg;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false);

        binding.dvPhoneNo.setText(Constant.dvPhoneNumber);
        binding.dvName.setText(Constant.dvFullName);
        binding.dvEmailId.setText(Constant.dvEmailId);
        binding.dvAddress.setText(Constant.dvAddress);
        Glide.with(getContext()).load(Constant.BASE_URL+Constant.dvProfileImg).into(binding.dvProfileImg);

        cropImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            Intent intent = new Intent(getContext(), UcropperActivity.class);
            intent.putExtra("SendImageData",result.toString());
            startActivityForResult(intent,100);
        });

        binding.dvProfileUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDriverProfile();
            }
        });

        binding.fbAddImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                cropImage.launch("image/*");

                            }
                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == 101 && data != null){
            String result = data.getStringExtra("CROP");
             uri = data.getData();
            if(result != null){
                uri = Uri.parse(result);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    encodedImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "Something went wrong. Please try again...", Toast.LENGTH_SHORT).show();
            }
            binding.dvProfileImg.setImageURI(uri);
        } else {
            Toast.makeText(getContext(), "Image not selected. Please try again...", Toast.LENGTH_LONG).show();
        }
    }

    private void encodedImageBitmap(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        encodedDvProfileImg = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
    }
    public void setDriverProfile(){

        UserLoginData userLoginData = new UserLoginData(getContext());
        String dvName = userLoginData.getUserName();
        String dvFullName = binding.dvName.getText().toString().trim();
        String dvEmailId = binding.dvEmailId.getText().toString().trim();
        String dvPhoneNumber = binding.dvPhoneNo.getText().toString().trim();
        String dvAddress = binding.dvAddress.getText().toString().trim();

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);


       if(dvName.isEmpty()){
           Toast.makeText(getContext(), "Something went wrong. Please try again...", Toast.LENGTH_SHORT).show();
        } else if(dvFullName.isEmpty()) {
           binding.dvName.setError("Name is empty...");
       } else if(dvEmailId.isEmpty()) {
           binding.dvEmailId.setError("Email id is empty...");
       } else if(dvPhoneNumber.isEmpty()) {
           binding.dvPhoneNo.setError("Phone number is empty...");
       } else if(dvAddress.isEmpty()) {
           binding.dvAddress.setError("Address is empty...");
       } else {

           progressDialog.show();
           Call<DvProfileUpdateResponse> call = ApiController
                           .getInstance()
                           .getApi()
                           .updateDriverData(dvName,dvEmailId,dvAddress,encodedDvProfileImg);

           call.enqueue(new Callback<DvProfileUpdateResponse>() {
               @Override
               public void onResponse(Call<DvProfileUpdateResponse> call, Response<DvProfileUpdateResponse> response) {
                   DvProfileUpdateResponse data = response.body();
                   if(data != null){
                       String result = data.getMessage();
                       if(result.equals("success")){
                           progressDialog.dismiss();
                           Toast.makeText(getContext(), "Update Successful...", Toast.LENGTH_LONG).show();
                       } else {
                           progressDialog.dismiss();
                           Toast.makeText(getContext(), "Something went wrong. Please try again...", Toast.LENGTH_LONG).show();
                       }

                   } else {
                       progressDialog.dismiss();
                       Toast.makeText(getContext(), "Image size should be less than 1 MB ", Toast.LENGTH_LONG).show();
                   }

               }

               @Override
               public void onFailure(Call<DvProfileUpdateResponse> call, Throwable t) {
                   progressDialog.dismiss();
                   Toast.makeText(getContext(), t.toString(), Toast.LENGTH_LONG).show();

               }
           });

        }


    }
}