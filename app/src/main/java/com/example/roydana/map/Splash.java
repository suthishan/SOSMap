package com.example.roydana.map;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roydana.map.broadCastReceivers.GpsLocationReceiver;
import com.example.roydana.map.constant.AppConstants;
import com.example.roydana.map.utility.LocationMonitoringService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import net.alexandroid.gps.GpsStatusDetector;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Splash extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GpsStatusDetector.GpsStatusDetectorCallBack{

    private static final String TAG = Splash.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private boolean mAlreadyStartedService = false;

    private static int SPLASH_TIME_OUT = 3000;



    protected LocationManager mLocationManager;
    private static final int GPS_ENABLE_REQUEST = 0x1001;
    private static final int WIFI_ENABLE_REQUEST = 0x1006;
    private AlertDialog mInternetDialog;
    private AlertDialog mGPSDialog;

    PrefManager preferenceData;
    Calendar mCurrentDate;
    int day, month, year, hour, minute, sec;
    TextView Create;

    String strAddress="", strTime;
    GpsLocationReceiver gpsReceiver;
    IntentFilter intentFilter;
    private GpsStatusDetector mGpsStatusDetector;
    boolean mISGpsStatusDetector;
    int deviceApi = Build.VERSION.SDK_INT;
    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        databaseHelper = new DatabaseHelper(this);
        preferenceData = new PrefManager(this);
        gpsReceiver = new GpsLocationReceiver();

        mGpsStatusDetector = new GpsStatusDetector(this);
        mGpsStatusDetector.checkGpsStatus();
        startStep1();

        if (mAlreadyStartedService) {
            if (!preferenceData.getLogin()) {
                stopService(new Intent(this, LocationMonitoringService.class));
                mAlreadyStartedService = false;
            }
        }

            if (preferenceData.getLogin()) {
                startService(new Intent(this, LocationMonitoringService.class));
                LocalBroadcastManager.getInstance(this).registerReceiver(
                        new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                String latitude = intent.getStringExtra(AppConstants.EXTRA_LATITUDE);
                                String longitude = intent.getStringExtra(AppConstants.EXTRA_LONGITUDE);
                                String mylocaton = latitude + "\t" + longitude;
                                    Log.d(Splash.class.getSimpleName(),"my location "+mylocaton);
                                if (latitude != null && longitude != null) {
//                            serverUpload.sendlocationtServer(mylocaton,latitude,longitude,LocationUpdateActivity.this);
                                    strAddress = getCompleteAddressString(latitude, longitude);
                                    preferenceData.setCurentAdress(strAddress);
                                        Log.d(Splash.class.getSimpleName(),"my latitude "+latitude);
                                        Log.d(Splash.class.getSimpleName(),"my longitude "+longitude);

                                    preferenceData.setCurentlatitude(latitude);
                                    preferenceData.setCurentlongitude(longitude);
//if (preferenceData.getPicmeId().equalsIgnoreCase("") && preferenceData.getVhnId().equalsIgnoreCase("")&& preferenceData.getMId().equalsIgnoreCase(""))
//                                        Log.d(LocationUpdateActivity.class.getSimpleName(),"strAddress "+strAddress);

                                    AppConstants.NEAR_LATITUDE = latitude;
                                    AppConstants.NEAR_LONGITUDE = longitude;
//                                        Log.d(LocationUpdateActivity.class.getSimpleName(),"call location update api ");

//                                    locationUpdatePresenter.uploadLocationToServer(preferenceData.getPicmeId(), preferenceData.getVhnId(), preferenceData.getMId(), latitude, longitude, strAddress);

                                }
                            }
                        }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
                );
            }
        }

    private void startStep1() {

        //Check whether this user has installed Google play service which is being used by Location updates.
        if (mISGpsStatusDetector) {
            if (isGooglePlayServicesAvailable()) {

                //Passing null to indicate that it is executing for the first time.
                startStep2(null);

            } else {
                Toast.makeText(getApplicationContext(), "No Google Play service avail", Toast.LENGTH_LONG).show();
            }
        }else{
            startActivity(new Intent(getApplicationContext(), TurnOnGpsLocation.class));
        }
    }

    private Boolean startStep2(DialogInterface dialog) {
        if (preferenceData.getLogin()) {
            preferenceData.setMainScreenOpen(0);
            Intent i = new Intent(Splash.this, MapsActivity.class);
            startActivity(i);
        } else {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                if(preferenceData.getLogin()){
                    startStep3();
                    return false;

                }else{
                    checkInternetConnection();
                    return true;
                }
            }
            if (dialog != null) {
                dialog.dismiss();
            }

            if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
                startStep3();
            } else {  //No user has not granted the permissions yet. Request now.
                requestPermissions();
            }
            return true;
        }
//        Log.e(LocationUpdateActivity.class.getSimpleName(),"startStep"+ true);
        return true;
    }

    private void checkInternetConnection() {
        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();

        finish();
    }

    /**
     * Show A Dialog with button to refresh the internet state.
     */
    private void promptInternetConnect() {

        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();

        finish();
    }

    private void startStep3() {

        if (!mAlreadyStartedService) {


            //Start location sharing service to app server.........
            Intent intent = new Intent(this, LocationMonitoringService.class);
            startService(intent);

            mAlreadyStartedService = true;
        }
        if (mISGpsStatusDetector) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
//                if (preferenceData.getPicmeId().equalsIgnoreCase("") && preferenceData.getVhnId().equalsIgnoreCase("") && preferenceData.getMId().equalsIgnoreCase("")) {
                    if (preferenceData.getLogin()) {
//                            Log.d("LOG LOGIN", preferenceData.getPicmeId() + "," + preferenceData.getVhnId() + "," + preferenceData.getMId());
//                    AppConstants.POP_UP_COUNT= Integer.parseInt(preferenceData.getMainScreenOpen());
                        preferenceData.setMainScreenOpen(0);
                        Intent i = new Intent(Splash.this, MapsActivity.class);
                        startActivity(i);
                    } else {
                        preferenceData.setMainScreenOpen(0);
                        Intent i = new Intent(Splash.this, MainActivity.class);
                        startActivity(i);
                    }
                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }else{
//            startActivity(new Intent(getApplicationContext(), TurnOnGpsLocation.class));
        }
    }


    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }


    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionState3 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);

        int permissionState5 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionState8 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE);
        int permissionState9 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED &&
                permissionState3 == PackageManager.PERMISSION_GRANTED && permissionState5 == PackageManager.PERMISSION_GRANTED
                && permissionState8 == PackageManager.PERMISSION_GRANTED
                && permissionState9 == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Start permissions requests.
     */
    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);
        boolean shouldProvideRationale3 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.INTERNET);

        boolean shouldProvideRationale5 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.SEND_SMS);

        boolean shouldProvideRationale8 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CALL_PHONE);
        boolean shouldProvideRationale9 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_PHONE_STATE);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2 || shouldProvideRationale3
                || shouldProvideRationale5 || shouldProvideRationale8 || shouldProvideRationale9) {
//            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(Splash.this,
                                    new String[]
                                            {
                                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                                    Manifest.permission.INTERNET,
                                                    Manifest.permission.CALL_PHONE,
                                                    Manifest.permission.SEND_SMS,
                                                    Manifest.permission.READ_PHONE_STATE

                                            },
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
//            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(this,
                    new String[]
                            {
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.INTERNET,
                                    Manifest.permission.SEND_SMS,
                                    Manifest.permission.CALL_PHONE,
                                    Manifest.permission.READ_PHONE_STATE
                            },
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
//        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If img_user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
//                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                startStep3();

            } else {
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }


    @Override
    public void onDestroy() {


        //Stop location sharing service to app server.........

        stopService(new Intent(this, LocationMonitoringService.class));
        mAlreadyStartedService = false;
        //Ends................................................


        super.onDestroy();
    }


    private String getCompleteAddressString(String latitude, String longitude) {
        String strAdd = "";
        double dlatitude= Double.parseDouble(latitude);
        double dlongitude= Double.parseDouble(longitude);
//        Log.w("dlatitude",dlatitude+"");
//        Log.w("dlongitude",dlongitude+"");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {

                List<Address> addresses = geocoder.getFromLocation(dlatitude, dlongitude, 1);

                if (addresses != null) {
//                Address returnedAddress = addresses.get(0);
                    String maddress = addresses.get(0).getAddressLine(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");

                /*for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }*/
                    strAdd = String.valueOf(maddress);
//                strAdd = strReturnedAddress.toString();
//                Log.w(TAG, "My Current loction address"+strReturnedAddress.toString());
//                Log.w(TAG, "My Current loction address--->"+returnedAddress.getSubAdminArea().toString());
                } else {
                Log.w(TAG,"My Current loction address--->"+"No Address returned!");
                }

        } catch (Exception e) {
            e.printStackTrace();
//            Log.w(TAG,"My Current loction address--->"+ "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGpsStatusDetector.checkOnActivityResult(requestCode, resultCode);
    }

    @Override
    public void onGpsSettingStatus(boolean enabled) {
//        Log.d("TAG", "onGpsSettingStatus: " + enabled);
        mISGpsStatusDetector = enabled;
        if(!enabled){
            mGpsStatusDetector.checkGpsStatus();
        }
    }

    @Override
    public void onGpsAlertCanceledByUser() {
//        Log.d("TAG", "onGpsAlertCanceledByUser");
//        startActivity(new Intent(getApplicationContext(),TurnOnGpsLocation.class));
    }
}
