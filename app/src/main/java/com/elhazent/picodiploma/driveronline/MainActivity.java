package com.elhazent.picodiploma.driveronline;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.elhazent.picodiploma.driveronline.helper.HeroHelper;
import com.elhazent.picodiploma.driveronline.helper.LocationMonitoringService;
import com.elhazent.picodiploma.driveronline.helper.MyContants;
import com.elhazent.picodiploma.driveronline.helper.SessionManager;
import com.elhazent.picodiploma.driveronline.model.ResponseHistoryReq;
import com.elhazent.picodiploma.driveronline.model.ResponseLoginRegis;
import com.elhazent.picodiploma.driveronline.network.InitRetrofit;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "testingservice";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 2;
    private SessionManager manager;
    private Timer timer;
    private GoogleApiClient googleApiClient;
    private boolean mAlreadyStartedService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama);
        FirebaseApp.initializeApp(this);
        manager =  new SessionManager(this);
        if (manager.getGcm().isEmpty()){
            String tokenfcm = FirebaseInstanceId.getInstance().getToken();
            manager.setGcm(tokenfcm);
            Toast.makeText(this, "token  anda:"+tokenfcm , Toast.LENGTH_SHORT).show();
            insertTokenToDb(tokenfcm);
        }
        cekstatusgps();
        permissionGPS();
        timer = new Timer();
        AsyncTaskTimer();
        setLocationDriver();
    }

    private void insertTokenToDb(String tokenfcm) {
        String id =manager.getIdUser();
        InitRetrofit.getInstance().insertFcm(id, tokenfcm).enqueue(new Callback<ResponseLoginRegis>() {
            @Override
            public void onResponse(Call<ResponseLoginRegis> call, Response<ResponseLoginRegis> response) {
                if (response.isSuccessful()){
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseLoginRegis> call, Throwable t) {

            }
        });
    }

    public void onHistory(View view) {
        startActivity(new Intent(this, HistoryActivity.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu );
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i= item.getItemId();
        if (i==R.id.logout){
            manager.logout();
        }
        return super.onOptionsItemSelected(item);
    }
    private void setLocationDriver() {

        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);
                sendLocation(latitude, longitude);
                Log.d("myLatlong: ", latitude + "," + longitude);
            }
        }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST));
    }

    private void sendLocation(String latitude, String longitude) {
        String iddriver = manager.getIdUser();
        String token = manager.getToken();
        String device = HeroHelper.getDeviceUUID(this);
        InitRetrofit.getInstance().insertPosisiDriver(iddriver, latitude, longitude, device, token).enqueue(new Callback<ResponseHistoryReq>() {
            @Override
            public void onResponse(Call<ResponseHistoryReq> call, Response<ResponseHistoryReq> response) {
                String result = response.body().getResult();
                String msg = response.body().getMsg();
                if (result.equals("true")) {
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseHistoryReq> call, Throwable t) {

            }
        });
    }

    private void cekstatusgps() {
        // cek sttus gps aktif atau tidak
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Gps already enabled", Toast.LENGTH_SHORT).show();
            //     finish();
        }
        // Todo Location Already on  ... end
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Gps not enabled", Toast.LENGTH_SHORT).show();
            //menampilkan popup untuk mengaktifkan gps
            enableLoc();
        }
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MainActivity.this, MyContants.REQUEST_LOCATION);

                                finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }


    private void permissionGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        110);
            }
            return;
        }

    }

    private void AsyncTaskTimer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TimerTask task =  new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            cekplayservice();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                timer.schedule(task,0,10000);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        AsyncTaskTimer();

    }

    private void cekplayservice() {
        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable()) {
            DialogInterface dialogInterface = null;
            //Passing null to indicate that it is executing for the first time.
            cekkoneksidevice(dialogInterface);

        } else {
            Toast.makeText(getApplicationContext(), "playserc", Toast.LENGTH_LONG).show();
        }
    }

    //check koneksi internet
    private boolean cekkoneksidevice(DialogInterface dialogInterface) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }


        if (dialogInterface != null) {
            dialogInterface.dismiss();
        }

        //Yes there is active internet connection. Next check Location is granted by user or not.

        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            aktifkanservice();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }

    private void aktifkanservice() {
        if (!mAlreadyStartedService ) {

            Log.d(TAG, "service start");
            //Start location sharing service to app server.........
            Intent intent = new Intent(this, LocationMonitoringService.class);
            startService(intent);

            mAlreadyStartedService = true;
            //Ends................................................
        }
    }

    //ketika tidak ada koneksi internet
    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("no internet");
        builder.setMessage("no internet");

        String positiveText = "refresh";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        //Block the Application Execution until user grants the permissions
                        if (cekkoneksidevice(dialog)) {

                            //Now make sure about location permission.
                            if (checkPermissions()) {

                                //Step 2: Start the Location Monitor Service
                                //Everything is there to start the service.
                                aktifkanservice();
                            } else if (!checkPermissions()) {
                                requestPermissions();
                            }

                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.resionable,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void showSnackbar(int resionable, int ok, View.OnClickListener onClickListener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(resionable),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(ok), onClickListener).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //
        AsyncTaskTimer();
    }



    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;


    }
    private boolean isGooglePlayServicesAvailable() {
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


    @Override
    protected void onStop() {
        super.onStop();
//        stopService(new Intent(this, LocationMonitoringService.class));
//        mAlreadyStartedService = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

}

