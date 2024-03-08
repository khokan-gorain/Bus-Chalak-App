package com.khokan_gorain_nsubuschalak_app.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.khokan_gorain_nsubuschalak_app.ApiController.ApiController;
import com.khokan_gorain_nsubuschalak_app.ConstantData.Constant;
import com.khokan_gorain_nsubuschalak_app.Fragments.HomeFragment;
import com.khokan_gorain_nsubuschalak_app.Fragments.PrivacyPolicy;
import com.khokan_gorain_nsubuschalak_app.Fragments.ProfileFragment;
import com.khokan_gorain_nsubuschalak_app.Fragments.StatusFragment;
import com.khokan_gorain_nsubuschalak_app.ModelClass.DriverInfo;
import com.khokan_gorain_nsubuschalak_app.ModelClass.DvProfileUpdateResponse;
import com.khokan_gorain_nsubuschalak_app.R;
import com.khokan_gorain_nsubuschalak_app.ServicesClass.LocationUpdateService;
import com.khokan_gorain_nsubuschalak_app.SharedPreference.UserLoginData;
import com.khokan_gorain_nsubuschalak_app.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    String current_location;
    FusedLocationProviderClient fusedLocationProviderClient;
    UserLoginData userLoginData;
    AlertDialog dialog,checkInternet;
    EditText userName,password;
    Button loginBtn;
    String uName,uPassword;
    DriverInfo driverInfo;
    Button internetRetryBtn;
    TextInputEditText inputDvPhoneNumber;
    String dvPhoneNumber,otpVerificationCode;
    View loginDialog;
    private FirebaseAuth kAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    EditText et1,et2,et3,et4,et5,et6;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.mytoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        kAuth = FirebaseAuth.getInstance();
        //FirebaseApp.initializeApp(this);
        //kAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
        Constant.activity = MainActivity.this;

        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.mystatus:
                    replaceFragment(new StatusFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
                case R.id.privacy_policy:
                    replaceFragment(new PrivacyPolicy());
                    break;
            }
            return true;
        });

        // For Checking Location Permission
            Dexter.withContext(getApplicationContext())
                    .withPermission(ACCESS_FINE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            getLastLocation();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",getApplicationContext().getPackageName(),null);
                            intent.setData(uri);
                            startActivity(intent);
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();


            // Internet Checking Dialog Box
        AlertDialog.Builder internetCheck = new AlertDialog.Builder(MainActivity.this);
        View noInternet = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_no_internet_conncetion,null);
        internetRetryBtn = noInternet.findViewById(R.id.retryBtn);
        internetCheck.setView(noInternet);
        checkInternet = internetCheck.create();
        checkInternet.setCancelable(false);
        checkInternet.getWindow().setGravity(Gravity.CENTER);

            // Login Dialog Box
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        loginDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.login_lyt,null);
        builder.setView(loginDialog);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        userName = loginDialog.findViewById(R.id.dvName);
        password = loginDialog.findViewById(R.id.dvPassword);
        loginBtn = loginDialog.findViewById(R.id.loginBtn);
        loginDialog.findViewById(R.id.forgotPassword).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View view) {
                loginDialog.findViewById(R.id.login_lyt).setVisibility(View.GONE);
                loginDialog.findViewById(R.id.phoneNoLyt).setVisibility(View.VISIBLE);
            }
        });
        loginDialog.findViewById(R.id.loginNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginDialog.findViewById(R.id.login_lyt).setVisibility(View.VISIBLE);
                loginDialog.findViewById(R.id.phoneNoLyt).setVisibility(View.GONE);
            }
        });
        loginDialog.findViewById(R.id.getOtpBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPhoneNumberIsExist();
            }
        });
        loginDialog.findViewById(R.id.verifyOtpBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyOtp(otpVerificationCode);
            }
        });
        loginDialog.findViewById(R.id.changePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
       // edTextInput(); // OTP Box AutoFill function Call

        checkConnection(); // should be call first

        internetRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkConnection();
                replaceFragment(new HomeFragment());
            }
        });


        // Logout Dialog Box
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getApplicationContext(), "Oye", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setCancelable(false);
                alertDialog.setIcon(R.drawable.ic_report_problem);
                alertDialog.setTitle("Are you sure");
                alertDialog.setMessage("You want to logout...");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UserLoginData userLoginData = new UserLoginData(getApplicationContext());
                        userLoginData.logOutUser();
                        loginProcess();
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog logoutDialog = alertDialog.create();
                logoutDialog.show();
            }
        });


    }

    // Check Internet Connection
    public void checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile_network = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnected()) {
            checkInternet.dismiss();
            loginProcess();
        } else if(mobile_network.isConnected()) {
            checkInternet.dismiss();
            loginProcess();
        } else {
            checkInternet.show();
        }

    }
      // Fragment Replace Function
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.replaceFragment,fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

     // Get Last Location Finder Function
    private void getLastLocation(){
        if(ContextCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null) {
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            if(addresses.get(0).getLocality() != null) {
                                current_location = addresses.get(0).getSubAdminArea() + " " + addresses.get(0).getAdminArea() + " " + addresses.get(0).getPostalCode();
                                binding.dvLocation.setText(addresses.get(0).getLocality() + " " + addresses.get(0).getAdminArea() + " " + addresses.get(0).getPostalCode());
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "We are unable to fetch your location", Toast.LENGTH_LONG).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Please check your location permission and try again...", Toast.LENGTH_SHORT).show();
        }
    }

    // Check Login Credential From SharedPreference
    public void loginProcess(){
        userLoginData = new UserLoginData(MainActivity.this);
        if(userLoginData.getUserName().isEmpty() && userLoginData.getPassword().isEmpty()){

            dialog.show();
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uName = userName.getText().toString().trim();
                    uPassword = password.getText().toString().trim();
                    if(uName.isEmpty()){
                        userName.setError("Please enter user name...");

                    } else if (uPassword.isEmpty()) {
                        password.setError("Please enter password...");
                    } else {
                        getDvLoginCheck(uName,uPassword);

                    }

                }
            });

        } else {
            //dialog.dismiss();
            getDvLoginCheck(userLoginData.getUserName(),userLoginData.getPassword()); //
            binding.logoutBtn.setVisibility(View.VISIBLE);
        }
    }

    // Check Login Credentials For Bus Chalak
    public void getDvLoginCheck(String dvName, String dvPassword){

       Call<List<DriverInfo>> call = ApiController
                       .getInstance()
                       .getApi()
                       .getDriverInfo(dvName,dvPassword);

        call.enqueue(new Callback<List<DriverInfo>>() {
            @Override
            public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {
                List<DriverInfo> data = response.body();
                if(data == null){
                    Toast.makeText(MainActivity.this, "Invalid username and password.", Toast.LENGTH_LONG).show();
                } else {
                    dialog.dismiss();
                    binding.logoutBtn.setVisibility(View.VISIBLE);
                    UserLoginData userLoginData = new UserLoginData(MainActivity.this);
                    userLoginData.setUserName(data.get(0).getDriver_name());
                    userLoginData.setPassword(data.get(0).getPassword());
                     driverInfo = new DriverInfo(
                            data.get(0).getId(),data.get(0).getStatus(),data.get(0).getDriver_id(),data.get(0).getDriver_name(),
                            data.get(0).getPhone_number(),data.get(0).getEmail_id(),data.get(0).getPassword(),
                            data.get(0).getPro_img(),data.get(0).getDv_full_name(),data.get(0).getDvAddress()
                     );

                    Glide.with(MainActivity.this).load(Constant.BASE_URL+data.get(0).getPro_img()).into(binding.dvProfile);
                    Constant.dvFullName = data.get(0).getDv_full_name();
                    Constant.dvEmailId = data.get(0).getEmail_id();
                    Constant.dvPhoneNumber = data.get(0).getPhone_number();
                    Constant.dvProfileImg = data.get(0).getPro_img();
                    Constant.dvAddress = data.get(0).getDvAddress();
                    Constant.dvId = data.get(0).getId();
                }
            }

            @Override
            public void onFailure(Call<List<DriverInfo>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Check your internet connection. Please try again...", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void checkPhoneNumberIsExist(){

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Cheeking your phone number...");

        inputDvPhoneNumber = loginDialog.findViewById(R.id.dvPhone);
        dvPhoneNumber =  inputDvPhoneNumber.getText().toString().trim();
        if(dvPhoneNumber.equalsIgnoreCase("")) {
            inputDvPhoneNumber.setError("Phone number is empty...");
        } else if(dvPhoneNumber.length() != 10) {
            Toast.makeText(getApplicationContext(), "Invalid Phone Number", Toast.LENGTH_LONG).show();
        } else {
            progressDialog.show();
            Call<DvProfileUpdateResponse> call = ApiController
                    .getInstance()
                    .getApi()
                    .getDvCheckPhoneNumber(dvPhoneNumber);
            call.enqueue(new Callback<DvProfileUpdateResponse>() {
                @Override
                public void onResponse(Call<DvProfileUpdateResponse> call, Response<DvProfileUpdateResponse> response) {
                    DvProfileUpdateResponse data = response.body();
                    if(data.getMessage().equalsIgnoreCase("success")){
                        progressDialog.dismiss();
                        sendOtp();  // sendOtp function call
                        //verifyOtp(otpVerificationCode);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Sorry! Number not register...", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<DvProfileUpdateResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Something went wrong. Please try again...", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void sendOtp(){

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Verification field...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                progressDialog.dismiss();
                otpVerificationCode = verificationId;
                loginDialog.findViewById(R.id.phoneNoLyt).setVisibility(View.GONE);
                loginDialog.findViewById(R.id.otp_lyt).setVisibility(View.VISIBLE);

                et1 = loginDialog.findViewById(R.id.etC1);
                et2 = loginDialog.findViewById(R.id.etC2);
                et3 = loginDialog.findViewById(R.id.etC3);
                et4 = loginDialog.findViewById(R.id.etC4);
                et5 = loginDialog.findViewById(R.id.etC5);
                et6 = loginDialog.findViewById(R.id.etC6);

                edTextInput();

            }
        };
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(kAuth)
                        .setPhoneNumber("+91"+dvPhoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(MainActivity.this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void verifyOtp(String otpVerificationCode) {


        String c1 = et1.getText().toString().trim();
        String c2 = et2.getText().toString().trim();
        String c3 = et3.getText().toString().trim();
        String c4 = et4.getText().toString().trim();
        String c5 = et5.getText().toString().trim();
        String c6 = et6.getText().toString().trim();


        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Verifying....");

        if (c1.isEmpty() || c2.isEmpty() || c3.isEmpty() || c4.isEmpty() || c5.isEmpty() || c6.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill opt...", Toast.LENGTH_SHORT).show();
        } else if (otpVerificationCode != null) {
            progressDialog.show();
            String code = c1 + c2 + c3 + c4 + c5 + c6;
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpVerificationCode, code);
            FirebaseAuth
                    .getInstance()
                    .signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                loginDialog.findViewById(R.id.otp_lyt).setVisibility(View.GONE);
                                loginDialog.findViewById(R.id.verifyOtpBtn).setVisibility(View.GONE);
                                loginDialog.findViewById(R.id.newPasswordLyt).setVisibility(View.VISIBLE);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Please inter a valid otp", Toast.LENGTH_LONG).show();
        }
    }

    public void edTextInput() {
        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void changePassword(){

        TextInputEditText newPassword = loginDialog.findViewById(R.id.dvNewPassword);
        TextInputEditText confirmPassword = loginDialog.findViewById(R.id.dvConfirmPassword);

        String dvNewPassword = newPassword.getText().toString().trim();
        String dvConfirmPassword = confirmPassword.getText().toString().trim();

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Changing password...");
        if(dvNewPassword.equalsIgnoreCase("")){
            newPassword.setError("New password is empty...");
        } else if(dvConfirmPassword.equalsIgnoreCase("")){
            confirmPassword.setError("Confirm password is empty...");
        } else if(!dvNewPassword.equals(dvConfirmPassword)){
            Toast.makeText(getApplicationContext(), "Password not matching...", Toast.LENGTH_LONG).show();
        } else {
            progressDialog.show();
            Call<DvProfileUpdateResponse> call = ApiController
                    .getInstance()
                    .getApi()
                    .changeDvNewPassword(dvPhoneNumber,dvConfirmPassword);
            call.enqueue(new Callback<DvProfileUpdateResponse>() {
                @Override
                public void onResponse(Call<DvProfileUpdateResponse> call, Response<DvProfileUpdateResponse> response) {
                    DvProfileUpdateResponse data = response.body();
                    if(data.getMessage().equalsIgnoreCase("success")){
                        progressDialog.dismiss();
                        loginDialog.findViewById(R.id.newPasswordLyt).setVisibility(View.GONE);
                        loginDialog.findViewById(R.id.login_lyt).setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "Password update successful...", Toast.LENGTH_LONG).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Something went wrong. Please try again...", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<DvProfileUpdateResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Something went wrong please try again after some time...", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


}
