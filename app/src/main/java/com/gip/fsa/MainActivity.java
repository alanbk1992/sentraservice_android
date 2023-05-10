package com.gip.fsa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gip.fsa.fragment.AmsFragment;
import com.gip.fsa.fragment.HomeFragment;
import com.gip.fsa.fragment.InboxFragment;
import com.gip.fsa.fragment.OrderFragment;
import com.gip.fsa.fragment.ViewProfileFragment;
import com.gip.fsa.service.RetrofitService;
import com.gip.fsa.service.common.CommonInterface;
import com.gip.fsa.service.common.CommonModel;
import com.gip.fsa.utility.ConstantUtility;
import com.gip.fsa.utility.SignatureUtility;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static boolean PERMISSION_APPROVE = false;

    Dialog pop_logout;

    private View view;
    private TextView tvMonth, tvWeek, tvDay;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean exitPress = false;

    private Fragment fragment;
    private BottomNavigationView bottomNavigationView;
    private Intent intent;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ConstantUtility constantUtility;
    private SignatureUtility signatureUtility;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        try{
            _initialize();

            String menu_id = getIntent().getStringExtra("menu_id");
            if (menu_id == null){
                menu_id = "0";
            }
            cek_menu(menu_id);
            ReactiveNetwork.observeInternetConnectivity()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean isConnectedToInternet) {
                            // do something with isConnectedToInternet value
                            View parentLayout = MainActivity.this.findViewById(R.id.main_layout);
                            if (isConnectedToInternet) {
                                editor.putBoolean("_OFFLINE_MODE", false);
                                editor.commit();
                                Snackbar.make(parentLayout, "You're online", Snackbar.LENGTH_SHORT)
                                        .setBackgroundTint(getResources().getColor(R.color.color_green))
                                        .show();
                            }
                            else {
                                editor.putBoolean("_OFFLINE_MODE", true);
                                editor.commit();
                                Snackbar.make(parentLayout, "You're offline", Snackbar.LENGTH_INDEFINITE)
                                        .show();
                                Log.d("MainActivity","You're offline");
                            }
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void _initialize() {
        fragment          = new HomeFragment();
        constantUtility   = new ConstantUtility();
        signatureUtility  = new SignatureUtility();
        sharedPreferences = getSharedPreferences("FMS", MODE_PRIVATE);
        editor            = sharedPreferences.edit();

        _fragment(fragment);
        _navigation();

        pop_logout = new Dialog(this);
        pop_logout.setContentView(R.layout.popup_logout);
        pop_logout.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop_logout.setCanceledOnTouchOutside(false);

        //Make directories to store pictures
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "FMS/Pictures").mkdirs();
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "FMS/Logos").mkdirs();
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "FMS/Profile Pictures").mkdirs();

        location_permission();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    if (PERMISSION_APPROVE == true) {
                        _location();
                    }
                    handler.postDelayed(this, 300000);
                } catch (Exception exception) {}
            }
        }, 300000);
    }

    void cek_menu(String menu_id){
        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.main_navigation);
        if (menu_id.equals("0")){
            bottomNavigationView.setSelectedItemId(R.id._navigation_home);
            fragment = new HomeFragment();
            _fragment(fragment);
        }
        else if (menu_id.equals("1")){
            bottomNavigationView.setSelectedItemId(R.id._navigation_order);
            fragment = new OrderFragment();
            _fragment(fragment);
        }
        else if (menu_id.equals("2")){
            bottomNavigationView.setSelectedItemId(R.id._navigation_ams);
            fragment = new AmsFragment();
            _fragment(fragment);
        }
    }

    void _navigation() {
        fragment = null;
        intent   = null;

        bottomNavigationView = findViewById(R.id.main_navigation);
        bottomNavigationView.setSelectedItemId(R.id._navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id._navigation_home :
                        fragment = new HomeFragment();
                        break;
                    case R.id._navigation_order :
                        fragment = new OrderFragment();
                        break;
                    case R.id._navigation_ams :
                        fragment = new AmsFragment();
                        break;
                    case R.id._navigation_inbox :
                        fragment = new InboxFragment();
                        break;
                    case R.id._navigation_profile :
                        fragment = new ViewProfileFragment();
                        break;
                }
                return _fragment(fragment);
            }
        });
    }

    public void btnMenu_click(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        //showMenu(view);
    }

    private boolean _fragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        pop_logout.show();
    }

    public void btnLogoutYa_click(View view) {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
        return;
    }

    public void btnLogoutTidak_click(View view) {
        pop_logout.hide();
    }

    @Override
    public void onLocationChanged(Location location) { }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) { }

    @Override
    public void onProviderDisabled(String s) { }

    @SuppressLint("MissingPermission")
    void _location() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, this);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                _geolocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
            }
        }
        catch(Exception ex) {

        }
    }

    void _geolocation(String latitude, String longitude) {
        String _accountId     = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _datetime      = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        String _signature     = signatureUtility.doSignature(_datetime, _accountId);

        CommonInterface _interface     = RetrofitService.getRetrofitService().create(CommonInterface.class);
        final Call<CommonModel> _model = _interface._doLocation(_accountId,latitude, longitude, _datetime, _signature);

        _model.enqueue(new Callback<CommonModel>() {
            @Override
            public void onResponse(Call<CommonModel> call, final Response<CommonModel> response) { }
            @Override
            public void onFailure(Call<CommonModel> call, Throwable throwable) { }
        });
    }

    void location_permission(){
        Dexter.withActivity(MainActivity.this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            PERMISSION_APPROVE = true;
                            _location();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(MainActivity.this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
}
