package com.example.carpooltest1.Constants;

import android.Manifest;

import com.google.android.gms.maps.model.LatLng;

import java.net.URL;

public class Error {
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    public static final float DEFAULT_ZOOM = 15f;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    public static final int STATUS_UPDATE_FAILED = 4041;
    public static final int REVIEW_INSERTION_FAILED = 4042;
    public static final int NO_ENTRIES_FOUND = 4043;
    public static final int NO_VEHICLE_ENTRIES_FOUND = 4045;
    public static final int VEHICLE_NOT_VERIFIED = 4044;

    public static final int VEHICLE_VERIFIED = 102;
    public static final int INSERTION_SUCCESSFULL = 103;
    public static final int INSERTION_FAILED = 403;
    public static final int DUPLICATE_ENTRY = 404;

    public static final String ID_KEY = "ID";
    public static final String ID_STATUS = "Status";


    public static final int REVIEW_INSERTION_SUCCESSFULL = 101;

    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final LatLng NED_LOCATION_MAIN_GATE = new LatLng(24.939522, 67.114246);
    public static final  String URL_MAIN = "https://introjected-knots.000webhostapp.com/android/v1/";
    public static final  String URL_BROADCAST = URL_MAIN + "Driverbroadcast.php";
    public static final  String URL_USERREQUESTGET = URL_MAIN + "getUserRequests.php";
    public static final  String URL_PROFILEGET = URL_MAIN + "Profiles.php";
    public static final  String URL_STATUS_UPDATE = URL_MAIN + "StatusUpdate.php";
    public static final  String URL_GETREVIEWS = URL_MAIN + "getReviews.php";
    public static final  String URL_END_RIDE = URL_MAIN + "EndRide.php";
    public static final  String URL_INSREVIEWS = URL_MAIN + "InsertReviews.php";
    public static final  String URL_GETREVIEWS_PENDING =URL_MAIN + "getReviewspending.php";
    public static final  String URL_LOGIN = URL_MAIN + "userLogin.php";
    public static final  String URL_REGISTER = URL_MAIN + "registerUser.php";
    public static final  String URL_REQUEST = URL_MAIN + "USRREQNEW.php";
    public static final  String URL_VEHICLE_STATUS_CHECK = URL_MAIN + "VehicleStChk.php";

    public static final boolean Ride_Started_bool                                = true;
    public static final boolean Ride_not_Started_bool                            = false;


    public static final int Ride_not_Started                                     = 0;
    public static final int Ride_Started                                         = 1;
    public static final int User_Canceled_ride                                   = 2;
    public static final int Driver_Canceled_ride                                 = 3;
    public static final int Ride_Ended                                           = 4;
    public static final int User_Canceled_ride_User_driver_reviews_not_given     = 5;
    public static final int Driver_Canceled_ride_User_driver_reviews_not_given   = 6;
    public static final int Driver_Ended_ride_User_driver_reviews_not_given      = 7;
    public static final int User_Canceled_ride_User_reviews_not_given            = 8;
    public static final int Driver_Canceled_ride_User_reviews_not_given          = 9;
    public static final int Driver_Ended_ride_User_reviews_not_given             = 10;
    public static final int User_Canceled_ride_driver_reviews_not_given          = 11;
    public static final int Driver_Canceled_ride_driver_reviews_not_given        = 12;
    public static final int Driver_Ended_ride_driver_reviews_not_given           = 13;
    public static final int User_Canceled_ride_reviews_given                     = 14;
    public static final int Driver_Canceled_ride_reviews_given                   = 15;
    public static final int Driver_Ended_ride_reviews_given                      = 16;





}
