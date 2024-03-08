package com.khokan_gorain_nsubuschalak_app.ApiInterface;

import com.khokan_gorain_nsubuschalak_app.ModelClass.AreaName;
import com.khokan_gorain_nsubuschalak_app.ModelClass.BusNumber;
import com.khokan_gorain_nsubuschalak_app.ModelClass.DriverInfo;
import com.khokan_gorain_nsubuschalak_app.ModelClass.DriverNoticeMessage;
import com.khokan_gorain_nsubuschalak_app.ModelClass.DvProfileUpdateResponse;
import com.khokan_gorain_nsubuschalak_app.ModelClass.ImageSlider;
import com.khokan_gorain_nsubuschalak_app.ModelClass.LocationUpdateResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface ApiSet {

    @GET("image_slider.php")
    Call<List<ImageSlider>> getSliderImage();

    @GET("area_name.php")
    Call<List<AreaName>> getAreaName();

   @GET("bus_number.php")
    Call<List<BusNumber>> getBusNumber();

   @GET("dv_login_info.php")
    Call<List<DriverInfo>> getDriverInfo(@Query("dvName") String dvName, @Query("dvPassword") String dvPassword );

   @GET("update_driver_data.php")
    Call<DvProfileUpdateResponse> updateDriverData(@Query("dvName") String dvName, @Query("dvEmailId") String dvEmailId,
                                                   @Query("dvAddress") String dvAddress, @Query("dvProfileImg") String dvProfileImg);

   @GET("location_update.php")
    Call<LocationUpdateResponse> BusLocationUpdate(@Query("dvName") String dvName, @Query("busNumber") String busNumber,
                                                   @Query("fromArea") String fromArea, @Query("toArea") String toArea,
                                                   @Query("status") String status, @Query("dvPhoneNumber") String  dvPhoneNumber,
                                                   @Query("dvId") int dvId, @Query("dvProfileImg") String dvProfileImg, @Query("latitudeGmap") Double latitudeGmap,
                                                   @Query("longitudeGmap") double longitudeGmap);

   @GET("driver_notice_message.php")
    Call<List<DriverNoticeMessage>> getDriverNoticeMessage();

   @GET("dv_phone_no_verify.php")
    Call<DvProfileUpdateResponse> getDvCheckPhoneNumber(@Query("dvPhone") String dvPhone);

   @GET("dv_change_password.php")
    Call<DvProfileUpdateResponse> changeDvNewPassword(@Query("dvPhone") String dvPhone, @Query("dvNewPassword") String dvNewPassword);

}