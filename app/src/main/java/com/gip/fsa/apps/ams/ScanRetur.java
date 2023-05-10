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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gip.fsa.R;
import com.gip.fsa.apps.ams.Api.ApiClient;
import com.gip.fsa.apps.ams.Api.ApiInterface;
import com.gip.fsa.apps.ams.Api.JsonData;
import com.gip.fsa.apps.ams.Model.ReturReasonModel;
import com.gip.fsa.apps.ams.Model.ReturUnitModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScanRetur extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    Retrofit retrofit;
    ApiInterface apiInterface;

    Dialog pop_alert_scan,popup_item,pop_loading;
    ListView lv_popup;
    Spinner sp_retur_type;
    TextView txt_sn,txt_scan_status,txt_merchant,txt_header_popup,txt_retur_type,txt_retur_dependence,txt_retur_reason;
    ArrayList<String> list_retur_type = new ArrayList<String>();
    ArrayList<String> list_retur_dependence = new ArrayList<String>();
    ArrayList<String> list_retur_reason = new ArrayList<String>();

    ArrayList<ReturUnitModel> retur_edc_arrayList = null;
    ArrayList<ReturReasonModel> retur_reason_arrayList = null;
    int iscan,bt_scan;
    int row_update = 0;
    String user_id,sn,pn,merchant,data_sn,json_response,unit_status;

    Button btn_scanOk;
    private ZXingScannerView mScannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ams_activity_scan_retur);

        Initialisasi();
        loadData();
        post_retur_reason("type","all","all");

    }


    private void Initialisasi(){
        retrofit = ApiClient.retrofit();
        apiInterface = retrofit.create(ApiInterface.class);

        row_update = 0;
        pop_alert_scan = new Dialog(this);
        pop_alert_scan.setContentView(R.layout.ams_popup_retur_unit);
        pop_alert_scan.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop_alert_scan.setCanceledOnTouchOutside(false);
        txt_retur_type = pop_alert_scan.findViewById(R.id.tvReturType);
        txt_retur_dependence = pop_alert_scan.findViewById(R.id.tvReturDependence);
        txt_retur_reason = pop_alert_scan.findViewById(R.id.tvReturReason);
        btn_scanOk = pop_alert_scan.findViewById(R.id.btnScanOk);

        popup_item = new Dialog(this);;
        popup_item.setContentView(R.layout.ams_popup_listview);
        popup_item.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup_item.setCanceledOnTouchOutside(false);
        txt_header_popup = popup_item.findViewById(R.id.tvHeaderPopup);
        lv_popup = popup_item.findViewById(R.id.lvPopup);

        txt_sn = pop_alert_scan.findViewById(R.id.tvScanSN);
        txt_merchant = pop_alert_scan.findViewById(R.id.tvScanMerchant);
        txt_scan_status = pop_alert_scan.findViewById(R.id.tvScanStatus);

        pop_loading = new Dialog(this);
        pop_loading.setContentView(R.layout.ams_alert_loading);
        pop_loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop_loading.setCanceledOnTouchOutside(false);

        checkCameraPermission();
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

    }

    private void loadData(){
        retur_edc_arrayList = new ArrayList<ReturUnitModel>();
        Util.pref_ams = getSharedPreferences("FMS", Context.MODE_PRIVATE);
        if (Util.pref_ams.contains("user_idKey")) {
            user_id = Util.pref_ams.getString("user_idKey", "");
        }
        if (Util.pref_ams.contains("retur_unitKey")) {
            retur_edc_arrayList = getArrayList("retur_unitKey");
        }
        SharedPreferences.Editor editor = Util.pref_ams.edit();
        editor.putString("row_updateKey", "0");
        editor.commit();
    }

    private void checkCameraPermission(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mScannerView = new ZXingScannerView(ScanRetur.this);
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

    private void post_retur_check_unit() {
        pop_loading.show();
        Call<ResponseBody> result = apiInterface.postReturCheckUnit(ApiClient.api_key,user_id,"edc",sn);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pop_loading.dismiss();
                try {
                    json_response = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String status = JsonData.get_data(json_response,"status");
                String message = JsonData.get_data(json_response,"message");
                if (status.equals("200")) {
                    unit_status = JsonData.get_data(json_response,"unit_status");
                    pn = JsonData.get_data(json_response,"pn");
                    merchant = JsonData.get_data(json_response,"merchant");
                    txt_merchant.setText(JsonData.get_data(json_response,"merchant"));
                    txt_retur_type.setText("");
                    txt_retur_reason.setText("");
                    if (unit_status.equals("valid")){
                        txt_scan_status.setText("Valid S/N");
                        txt_scan_status.setTextColor(getResources().getColor(R.color.color_green));
                        btn_scanOk.setText("OK");
                    }else if (unit_status.equals("invalid")){
                        txt_scan_status.setText("Invalid S/N");
                        txt_scan_status.setTextColor(getResources().getColor(R.color.color_red));
                        btn_scanOk.setText("RESCAN");
                    }
                    pop_alert_scan.show();
                }
                else{
                    Toast.makeText(ScanRetur.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(ScanRetur.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post_retur_reason(final String name, String type, String dependence) {
        pop_loading.show();
        Call<ResponseBody> result = apiInterface.postReturReason(ApiClient.api_key,user_id,name,type,dependence);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pop_loading.dismiss();
                try {
                    json_response = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String status = JsonData.get_data(json_response,"status");
                String message = JsonData.get_data(json_response,"message");
                if (status.equals("200")) {
                    if(name.equals("type")){
                        list_retur_type = new ArrayList<String>();
                        Integer rowcount = JsonData.get_listdata(json_response,"name").size();
                        for (int i=0; i<rowcount; i++){
                            String name = JsonData.get_listdata(json_response,"name").get(i);
                            list_retur_type.add(name);
                        }
                    }else if(name.equals("dependence")){
                        list_retur_dependence = new ArrayList<String>();
                        Integer rowcount = JsonData.get_listdata(json_response,"name").size();
                        for (int i=0; i<rowcount; i++){
                            String name = JsonData.get_listdata(json_response,"name").get(i);
                            list_retur_dependence.add(name);
                        }
                    }else if(name.equals("reason")){
                        list_retur_reason = new ArrayList<String>();
                        Integer rowcount = JsonData.get_listdata(json_response,"name").size();
                        for (int i=0; i<rowcount; i++){
                            String name = JsonData.get_listdata(json_response,"name").get(i);
                            list_retur_reason.add(name);
                        }
                    }
                }
                else{
                    Toast.makeText(ScanRetur.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(ScanRetur.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnScanOk_click(View view) {
        if(txt_retur_type.getText().equals("")){
            Toast.makeText(this, "Please Choose Type", Toast.LENGTH_SHORT).show();
        }else if(txt_retur_dependence.getText().equals("")){
            Toast.makeText(this, "Please Choose Dependence", Toast.LENGTH_SHORT).show();
        }else if(txt_retur_reason.getText().equals("")){
            Toast.makeText(this, "Please Choose Reason", Toast.LENGTH_SHORT).show();
        }else{
            int cek_sn = search_sn(sn);
            if(cek_sn==0){
                String retur_type = txt_retur_type.getText().toString();
                String retur_reason = txt_retur_type.getText().toString() + " " + txt_retur_dependence.getText().toString() + " " + txt_retur_reason.getText().toString();
                retur_edc_arrayList.add(new ReturUnitModel(merchant,"edc",sn,pn,"retur",retur_type,retur_reason,"","",true));
                saveArrayList(retur_edc_arrayList,"retur_unitKey");
                SharedPreferences.Editor editor = Util.pref_ams.edit();
                editor.putString("retur_updateKey", "1");
                editor.commit();
            }
            pop_alert_scan.hide();
            mScannerView.resumeCameraPreview(this);
        }
    }

    private int search_sn(String sn_scan){
        int result = 0;
        for(int i=0; i<retur_edc_arrayList.size(); i++) {
            String retur_sn = retur_edc_arrayList.get(i).getDesc_1().toString();
            if (sn_scan.equals(retur_sn)){
                result=1;
            }
        }
        return result;
    }

    public void btnScanCancel_click(View view) {
        pop_alert_scan.hide();
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ScanRetur.this,ReturUnit.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        sn = rawResult.getText();
        mScannerView.stopCameraPreview();
        txt_sn.setText(sn);
        post_retur_check_unit();
    }

    public ArrayList<ReturUnitModel> getArrayList(String key){
        Gson gson = new Gson();
        String json = Util.pref_ams.getString(key, "");
        Type type = new TypeToken<ArrayList<ReturUnitModel>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveArrayList(ArrayList<ReturUnitModel> list, String key){
        SharedPreferences.Editor editor = Util.pref_ams.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.commit();
    }

    public void tvReturType_Click(View view) {
        txt_header_popup.setText("Choose Type");
        ListView lv = (ListView) popup_item.findViewById(R.id.lvPopup);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.ams_listview_item,R.id.tvItem,list_retur_type);
        lv.setAdapter(adapter);
        popup_item.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                txt_retur_type.setText(selectedItem);
                txt_retur_dependence.setText("");
                txt_retur_reason.setText("");
                post_retur_reason("dependence",selectedItem,"all");
                popup_item.dismiss();
            }
        });
    }

    public void tvReturDependence_Click(View view) {
        txt_header_popup.setText("Choose Dependence");
        ListView lv = (ListView) popup_item.findViewById(R.id.lvPopup);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.ams_listview_item,R.id.tvItem,list_retur_dependence);
        lv.setAdapter(adapter);
        popup_item.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                txt_retur_dependence.setText(selectedItem);
                txt_retur_reason.setText("");
                String type = txt_retur_type.getText().toString();
                post_retur_reason("reason",type,selectedItem);
                popup_item.dismiss();
            }
        });
    }

    public void tvReturReason_Click(View view) {
        txt_header_popup.setText("Choose Reason");
        ListView lv = (ListView) popup_item.findViewById(R.id.lvPopup);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.ams_listview_item,R.id.tvItem,list_retur_reason);
        lv.setAdapter(adapter);
        popup_item.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                txt_retur_reason.setText(selectedItem);
                popup_item.dismiss();
            }
        });
    }

    public void btnCloseItem_click(View view) {
        popup_item.dismiss();
    }

}
