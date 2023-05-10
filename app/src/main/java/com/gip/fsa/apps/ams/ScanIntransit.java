package com.gip.fsa.apps.ams;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gip.fsa.R;
import com.gip.fsa.apps.ams.Model.ReceiveUnitModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.lang.reflect.Type;
import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanIntransit extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    Dialog pop_alert_scan;
    TextView txt_sn,txt_scan_status;
    ArrayList<ReceiveUnitModel> receive_unit_arrayList = null;
    int iscan,bt_scan;
    int receive_update = 0;

    Button btn_scanOk;
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ams_activity_scan_intransit);

        Initialisasi();
        loadData();

    }


    private void Initialisasi(){
        receive_update = 0;
        bt_scan = 0;
        pop_alert_scan = new Dialog(this);
        pop_alert_scan.setContentView(R.layout.ams_popup_scan_unit);
        pop_alert_scan.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btn_scanOk = pop_alert_scan.findViewById(R.id.btnScanOk);

        txt_sn = pop_alert_scan.findViewById(R.id.tvScanSN);
        txt_scan_status = pop_alert_scan.findViewById(R.id.tvScanStatus);

        checkCameraPermission();
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

    }

    private void loadData(){
        receive_unit_arrayList = new ArrayList<ReceiveUnitModel>();
        Util.pref_ams = getSharedPreferences("FMS", Context.MODE_PRIVATE);
        if (Util.pref_ams.contains("receive_unitKey")) {
            receive_unit_arrayList = getArrayList("receive_unitKey");
        }
        SharedPreferences.Editor editor = Util.pref_ams.edit();
        editor.putString("receive_updateKey", "0");
        editor.commit();
    }

    private void checkCameraPermission(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mScannerView = new ZXingScannerView(ScanIntransit.this);
                        setContentView(mScannerView);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    public void btnScanOk_click(View view) {
        if(bt_scan==1){
            receive_unit_arrayList.get(iscan).setStatus("Received");
            check_update();
            saveArrayList(receive_unit_arrayList,"receive_unitKey");
        }
        pop_alert_scan.hide();
        mScannerView.resumeCameraPreview(this);
    }

    public void btnScanCancel_click(View view) {
        pop_alert_scan.hide();
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, IntransitUnit.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(ScanIntransit.this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        String sn = rawResult.getText();
        mScannerView.stopCameraPreview();
        txt_sn.setText(sn);
        bt_scan = search_sn(sn);
        if (bt_scan==1){
            txt_scan_status.setText("Valid S/N");
            txt_scan_status.setTextColor(getResources().getColor(R.color.color_green));
            btn_scanOk.setText("OK");
        }else if (bt_scan==0){
            txt_scan_status.setText("Invalid S/N");
            txt_scan_status.setTextColor(getResources().getColor(R.color.snack_red));
            btn_scanOk.setText("RESCAN");
        }else if (bt_scan==2){
            txt_scan_status.setText("S/N is already Received");
            txt_scan_status.setTextColor(getResources().getColor(R.color.blue_01));
            btn_scanOk.setText("RESCAN");
        }
        pop_alert_scan.show();
    }

    private int search_sn(String sn_scan){
        int result = 0;
        for(int i=0; i<receive_unit_arrayList.size(); i++) {
            String sn = receive_unit_arrayList.get(i).getDesc_1().toString();
            String status = receive_unit_arrayList.get(i).getStatus().toLowerCase();
            if (sn_scan.equals(sn) && status.equals("intransit")){
                receive_unit_arrayList.get(i).setUpdate(true);
                iscan = i;
                receive_update = receive_update+1;
                result=1;
            }else if (sn_scan.equals(sn) && status.equals("received")){
                result=2;
            }
        }
        return result;
    }

    public ArrayList<ReceiveUnitModel> getArrayList(String key){
        Gson gson = new Gson();
        String json = Util.pref_ams.getString(key, null);
        Type type = new TypeToken<ArrayList<ReceiveUnitModel>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveArrayList(ArrayList<ReceiveUnitModel> list, String key){
        SharedPreferences.Editor editor = Util.pref_ams.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.commit();
    }

    private void check_update(){
        if (receive_update>0){
            SharedPreferences.Editor editor = Util.pref_ams.edit();
            editor.putString("receive_updateKey", String.valueOf(receive_update));
            editor.commit();
        }
    }

}
