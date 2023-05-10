package com.gip.fsa.apps.ams;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gip.fsa.R;
import com.gip.fsa.apps.ams.Api.ApiClient;
import com.gip.fsa.apps.ams.Api.ApiInterface;
import com.gip.fsa.apps.ams.Api.JsonData;
import com.gip.fsa.apps.ams.Model.ReturOrderModel;
import com.gip.fsa.apps.ams.Model.ReturUnitModel;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReturUnit extends AppCompatActivity {

    Retrofit retrofit;
    ApiInterface apiInterface;
    int retur_update = 0;

    Dialog pop_alert,pop_loading,pop_simcard,pop_peripheral,pop_thermal,popup_item;
    TextView txt_alert_header,txt_alert_message,txt_header_popup;
    ImageView img_alert;
    Button btn_alert_ok,btn_scan,btn_ok_simcard;

    String json_response,status,message,user_id,movement_id,order_no,warehouse_id,data_unit,unit_type,type,customer,edc_sn;
    Integer qty;

    TextView txt_order_no,txt_retur_date,txt_qty,txt_warehouse_name
            ,txt_peripheral_type,txt_peripheral_customer
            ,txt_thermal_type,txt_thermal_customer;

    EditText edt_thermal_qty;

    Boolean update_simcard = false;

    AutoCompleteTextView txt_simcard,txt_sn,txt_tid;
    String[] list_simcard,peripheral_type,list_customer,list_sn;

    ArrayList<ReturOrderModel> retur_order_arrayList = null;
    ArrayList<ReturUnitModel> retur_edc_arrayList = null;
    ArrayList<ReturUnitModel> retur_simcard_arrayList = null;
    ArrayList<ReturUnitModel> retur_peripheral_arrayList = null;
    ArrayList<ReturUnitModel> retur_thermal_arrayList = null;
    ListView lv_retur_edc,lv_retur_simcard,lv_peripheral,lv_popup;
    RelativeLayout rl_retur_scan;

    TabLayout tab_unit_type;
    Integer tab_layer;
    RelativeLayout rl_edc,rl_simcard,rl_peripheral,rl_thermal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ams_activity_retur_unit);

        Initialisasi();
        loadData();
        if(retur_update==0){
            retur_edc_arrayList = new ArrayList<ReturUnitModel>();
            retur_simcard_arrayList = new ArrayList<ReturUnitModel>();
            retur_peripheral_arrayList = new ArrayList<ReturUnitModel>();
            retur_thermal_arrayList = new ArrayList<ReturUnitModel>();
            saveArrayUnit(retur_edc_arrayList,"retur_unitKey");
            saveArrayUnit(retur_simcard_arrayList,"retur_simcardKey");
            saveArrayUnit(retur_peripheral_arrayList,"retur_peripheralKey");
            saveArrayUnit(retur_thermal_arrayList,"retur_thermalKey");
            post_retur_unit();
        }else{
            load_retur_order();
            display_retur_edc(retur_edc_arrayList);
            display_retur_simcard(retur_simcard_arrayList);
            display_retur_peripheral(retur_peripheral_arrayList);
            display_retur_thermal(retur_thermal_arrayList);
        }

    }

    private void Initialisasi(){
        retrofit = ApiClient.retrofit();
        apiInterface = retrofit.create(ApiInterface.class);

        txt_order_no = findViewById(R.id.tvOrderNo);
        txt_retur_date = findViewById(R.id.tvReturDate);
        txt_qty = findViewById(R.id.tvQty);
        txt_warehouse_name = findViewById(R.id.tvWarehouse);

        lv_retur_edc = (ListView) findViewById(R.id.lvReturEDC);
        lv_retur_simcard = (ListView) findViewById(R.id.lvReturSimCard);
        retur_edc_arrayList = new ArrayList<ReturUnitModel>();

        rl_retur_scan = findViewById(R.id.rlReturScan);
        btn_scan = findViewById(R.id.btnScan);

        pop_alert = new Dialog(this);
        pop_alert.setContentView(R.layout.ams_popup_alert);
        pop_alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop_alert.setCanceledOnTouchOutside(false);
        img_alert = pop_alert.findViewById(R.id.imgAlert);
        txt_alert_header = pop_alert.findViewById(R.id.tvAlertHeader);
        txt_alert_message = pop_alert.findViewById(R.id.tvAlertMessage);
        btn_alert_ok = pop_alert.findViewById(R.id.btnAlertOk);

        pop_loading = new Dialog(this);
        pop_loading.setContentView(R.layout.ams_alert_loading);
        pop_loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop_loading.setCanceledOnTouchOutside(false);

        pop_simcard = new Dialog(this);
        pop_simcard.setContentView(R.layout.ams_popup_simcard);
        pop_simcard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop_simcard.setCanceledOnTouchOutside(false);
        txt_simcard = pop_simcard.findViewById(R.id.txtSimCard);
        btn_ok_simcard = pop_simcard.findViewById(R.id.btnSubmitSimCard);

        pop_peripheral = new Dialog(this);
        pop_peripheral.setContentView(R.layout.ams_popup_retur_peripheral);
        pop_peripheral.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop_peripheral.setCanceledOnTouchOutside(false);
        txt_peripheral_type = pop_peripheral.findViewById(R.id.txtPeripheralType);
        txt_peripheral_customer = pop_peripheral.findViewById(R.id.txtPeripheralCustomer);
        txt_sn = pop_peripheral.findViewById(R.id.txtSN);

        pop_thermal = new Dialog(this);
        pop_thermal.setContentView(R.layout.ams_popup_retur_thermal);
        pop_thermal.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop_thermal.setCanceledOnTouchOutside(false);
        txt_thermal_type = pop_thermal.findViewById(R.id.txtThermalType);
        txt_thermal_customer = pop_thermal.findViewById(R.id.txtThermalCustomer);
        txt_tid = pop_thermal.findViewById(R.id.txtTID);
        edt_thermal_qty = pop_thermal.findViewById(R.id.edtThermalQty);

        popup_item = new Dialog(this);
        popup_item.setContentView(R.layout.ams_popup_listview);
        popup_item.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup_item.setCanceledOnTouchOutside(false);
        txt_header_popup = popup_item.findViewById(R.id.tvHeaderPopup);
        lv_popup = popup_item.findViewById(R.id.lvPopup);

        rl_edc = findViewById(R.id.rlEDC);
        rl_simcard = findViewById(R.id.rlSimCard);
        rl_peripheral = findViewById(R.id.rlPeripheral);
        rl_thermal = findViewById(R.id.rlThermal);

        rl_edc.setVisibility(View.VISIBLE);
        rl_simcard.setVisibility(View.GONE);
        rl_peripheral.setVisibility(View.GONE);
        rl_thermal.setVisibility(View.GONE);

        tab_unit_type = (TabLayout) findViewById(R.id.tabType);
        tab_unit_type.addTab(tab_unit_type.newTab().setText("EDC"));
        tab_unit_type.addTab(tab_unit_type.newTab().setText("SIM Card"));
        tab_unit_type.addTab(tab_unit_type.newTab().setText("Peripheral"));
        tab_unit_type.addTab(tab_unit_type.newTab().setText("Thermal"));
        unit_type = "edc";

        tab_unit_type.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                String tab_name = tab.getText().toString();
                if (position==0){
                    unit_type = "edc";
                    tab_layer = 1;
                    btn_scan.setText("SCAN");
                    rl_edc.setVisibility(View.VISIBLE);
                    rl_simcard.setVisibility(View.GONE);
                    rl_peripheral.setVisibility(View.GONE);
                    rl_thermal.setVisibility(View.GONE);
                }
                else if (position==1){
                    unit_type = "simcard";
                    tab_layer = 2;
                    btn_scan.setText("ADD");
                    rl_edc.setVisibility(View.GONE);
                    rl_simcard.setVisibility(View.VISIBLE);
                    rl_peripheral.setVisibility(View.GONE);
                    rl_thermal.setVisibility(View.GONE);
                }
                else if (position==2){
                    unit_type = "peripheral";
                    tab_layer = 3;
                    btn_scan.setText("ADD");
                    rl_edc.setVisibility(View.GONE);
                    rl_simcard.setVisibility(View.GONE);
                    rl_peripheral.setVisibility(View.VISIBLE);
                    rl_thermal.setVisibility(View.GONE);
                }
                else if (position==3){
                    unit_type = "thermal";
                    tab_layer = 4;
                    btn_scan.setText("ADD");
                    rl_edc.setVisibility(View.GONE);
                    rl_simcard.setVisibility(View.GONE);
                    rl_peripheral.setVisibility(View.GONE);
                    rl_thermal.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void loadData(){
        Util.pref_ams = getSharedPreferences("FMS", Context.MODE_PRIVATE);
        if (Util.pref_ams.contains("user_idKey")) {
            user_id = Util.pref_ams.getString("user_idKey", "");
        }
        if (Util.pref_ams.contains("movement_idKey")) {
            movement_id = Util.pref_ams.getString("movement_idKey", "");
        }
        if (Util.pref_ams.contains("retur_updateKey")) {
            retur_update = Integer.valueOf(Util.pref_ams.getString("retur_updateKey", ""));
        }
        if (Util.pref_ams.contains("retur_orderKey")) {
            retur_order_arrayList = getArrayOrder("retur_orderKey");
        }
        if (Util.pref_ams.contains("retur_unitKey")) {
            retur_edc_arrayList = getArrayUnit("retur_unitKey");
        }
        if (Util.pref_ams.contains("retur_simcardKey")) {
            retur_simcard_arrayList = getArrayUnit("retur_simcardKey");
        }
        if (Util.pref_ams.contains("retur_peripheralKey")) {
            retur_peripheral_arrayList = getArrayUnit("retur_peripheralKey");
        }
        if (Util.pref_ams.contains("retur_thermalKey")) {
            retur_thermal_arrayList = getArrayUnit("retur_thermalKey");
        }
        String retur_new = "0";
        if (Util.pref_ams.contains("retur_newKey")) {
            retur_new = Util.pref_ams.getString("retur_newKey", "");
        }
        if(retur_new.equals("1")){
            rl_retur_scan.setVisibility(View.VISIBLE);
        }else{
            rl_retur_scan.setVisibility(View.GONE);
        }
    }

    private void load_retur_order(){
        int total = retur_edc_arrayList.size()+retur_simcard_arrayList.size()+retur_peripheral_arrayList.size()+retur_thermal_arrayList.size();
        retur_order_arrayList.get(0).setQty(total);
        txt_order_no.setText(retur_order_arrayList.get(0).getOrder_no());
        txt_retur_date.setText(retur_order_arrayList.get(0).getRetur_date());
        txt_qty.setText(String.valueOf(retur_order_arrayList.get(0).getQty()));
        txt_warehouse_name.setText(retur_order_arrayList.get(0).getWarehouse_name());
        movement_id = retur_order_arrayList.get(0).getMovement_id();
        order_no = retur_order_arrayList.get(0).getOrder_no();
        qty = retur_order_arrayList.get(0).getQty();
        warehouse_id = retur_order_arrayList.get(0).getWarehouse_id();
    }

    private void post_retur_unit() {
        pop_loading.show();
        Call<ResponseBody> result = apiInterface.postReturUnit(ApiClient.api_key,user_id,movement_id);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pop_loading.dismiss();
                try {
                    json_response = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                status = JsonData.get_data(json_response,"status");
                message = JsonData.get_data(json_response,"message");
                if (status.equals("200")) {
                    retur_edc_arrayList = new ArrayList<>();
                    retur_simcard_arrayList = new ArrayList<>();
                    int rowcount = JsonData.get_listdata(json_response,"type").size();
                    for (int i=0; i<rowcount; i++){
                        String merchant = JsonData.get_listdata(json_response,"merchant").get(i);
                        String type = JsonData.get_listdata(json_response,"type").get(i);
                        String sn = JsonData.get_listdata(json_response,"desc_1").get(i);
                        String pn = JsonData.get_listdata(json_response,"desc_2").get(i);
                        String unit_status = JsonData.get_listdata(json_response,"status").get(i);
                        String job_type = JsonData.get_listdata(json_response,"job_type").get(i);
                        String reason = JsonData.get_listdata(json_response,"remarks").get(i);
                        if (type.equals("edc")){
                            retur_edc_arrayList.add(new ReturUnitModel(merchant,type,sn,pn,unit_status,job_type,reason,"","",false));
                        }
                        else if (type.equals("simcard")){
                            retur_simcard_arrayList.add(new ReturUnitModel(merchant,type,sn,pn,unit_status,job_type,reason,"","",false));
                        }
                        else if (type.equals("peripheral")){
                            retur_peripheral_arrayList.add(new ReturUnitModel(merchant,type,sn,pn,unit_status,job_type,reason,"","",false));
                        }
                        else if (type.equals("thermal")){
                            retur_thermal_arrayList.add(new ReturUnitModel(merchant,type,sn,pn,unit_status,job_type,reason,"","",false));
                        }
                    }
                    retur_order_arrayList = new ArrayList<ReturOrderModel>();
                    String movement_id = JsonData.get_data(json_response,"movement_id");
                    String order_no = JsonData.get_data(json_response,"order_no");
                    String retur_date = JsonData.get_data(json_response,"retur_date");
                    int qty = Integer.valueOf(JsonData.get_data(json_response,"qty"));
                    String warehouse_id = JsonData.get_data(json_response,"warehouse_id");
                    String warehouse_name = JsonData.get_data(json_response,"warehouse_name");
                    String order_status = JsonData.get_data(json_response,"order_status");
                    retur_order_arrayList.add(new ReturOrderModel(movement_id,order_no,retur_date,qty,warehouse_id,warehouse_name,order_status));
                    load_retur_order();
                    display_retur_edc(retur_edc_arrayList);
                    display_retur_simcard(retur_simcard_arrayList);
                    display_retur_peripheral(retur_peripheral_arrayList);
                    display_retur_thermal(retur_thermal_arrayList);
                }
                else{
                    Toast.makeText(ReturUnit.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(ReturUnit.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post_retur_check_unit(final String unit_type,final String id) {
        pop_loading.show();
        Call<ResponseBody> result = apiInterface.postReturCheckUnit(ApiClient.api_key,user_id,unit_type,id);
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
                    String unit_status = JsonData.get_data(json_response,"unit_status");
                    String pn = JsonData.get_data(json_response,"pn");
                    String merchant = JsonData.get_data(json_response,"merchant");
                    if (unit_type.equals("simcard")){
                        retur_simcard_arrayList.add(new ReturUnitModel(merchant,unit_type,id,pn,"retur","","","","",true));
                        saveArrayUnit(retur_simcard_arrayList,"retur_simcardKey");
                        SharedPreferences.Editor editor = Util.pref_ams.edit();
                        editor.putString("retur_updateKey", "1");
                        editor.commit();
                        pop_simcard.dismiss();
                        display_retur_simcard(retur_simcard_arrayList);
                    }else if (unit_type.equals("peripheral")){
                        retur_peripheral_arrayList.add(new ReturUnitModel(merchant,unit_type,type,customer,"retur",edc_sn,"","","",true));
                        saveArrayUnit(retur_peripheral_arrayList,"retur_peripheralKey");
                        SharedPreferences.Editor editor = Util.pref_ams.edit();
                        editor.putString("retur_updateKey", "1");
                        editor.commit();
                        display_retur_peripheral(retur_peripheral_arrayList);
                    }
                    int total = retur_edc_arrayList.size()+retur_simcard_arrayList.size()+retur_peripheral_arrayList.size()+retur_thermal_arrayList.size();
                    retur_order_arrayList.get(0).setQty(total);
                    txt_qty.setText(String.valueOf(retur_order_arrayList.get(0).getQty()));
                }
                else{
                    Toast.makeText(ReturUnit.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(ReturUnit.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post_list_simcard() {
        pop_loading.show();
        Call<ResponseBody> result = apiInterface.postListSimCard(ApiClient.api_key,warehouse_id);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pop_loading.dismiss();
                try {
                    json_response = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                status = JsonData.get_data(json_response,"status");
                message = JsonData.get_data(json_response,"message");
                if (status.equals("200")) {
                    Integer rowcount = JsonData.get_listdata(json_response,"iccid").size();
                    list_simcard = new String[rowcount];
                    for (int i=0; i<rowcount; i++){
                        String iccid = JsonData.get_listdata(json_response,"iccid").get(i);
                        list_simcard[i] = iccid;
                    }
                    txt_simcard.setAdapter(new ArrayAdapter<>(ReturUnit.this, android.R.layout.simple_list_item_1, list_simcard));
                }
                else{
                    Toast.makeText(ReturUnit.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(ReturUnit.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post_list_peripheral() {
        pop_loading.show();
        Call<ResponseBody> result = apiInterface.postListItemType(ApiClient.api_key,user_id,"peripheral");
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pop_loading.dismiss();
                try {
                    json_response = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                status = JsonData.get_data(json_response,"status");
                message = JsonData.get_data(json_response,"message");
                if (status.equals("200")) {
                    Integer rowcount = JsonData.get_listdata(json_response,"type").size();
                    peripheral_type = new String[rowcount];
                    for (int i=0; i<rowcount; i++){
                        peripheral_type[i] = JsonData.get_listdata(json_response,"type").get(i);
                    }
                    pop_loading.dismiss();
                    txt_header_popup.setText("Choose Type");
                    ListView lv = (ListView) popup_item.findViewById(R.id.lvPopup);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReturUnit.this,R.layout.ams_listview_item,R.id.tvItem,peripheral_type);
                    lv.setAdapter(adapter);
                    popup_item.show();
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItem = (String) parent.getItemAtPosition(position);
                            txt_peripheral_type.setText(selectedItem);
                            popup_item.dismiss();
                        }
                    });
                }
                else{
                    Toast.makeText(ReturUnit.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(ReturUnit.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post_list_customer() {
        pop_loading.show();
        Call<ResponseBody> result = apiInterface.postListCustomer(ApiClient.api_key,user_id);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pop_loading.dismiss();
                try {
                    json_response = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                status = JsonData.get_data(json_response,"status");
                message = JsonData.get_data(json_response,"message");
                if (status.equals("200")) {
                    Integer rowcount = JsonData.get_listdata(json_response,"code").size();
                    list_customer = new String[rowcount];
                    for (int i=0; i<rowcount; i++){
                        list_customer[i] = JsonData.get_listdata(json_response,"code").get(i);
                    }
                    pop_loading.dismiss();
                    txt_header_popup.setText("Choose Customer");
                    ListView lv = (ListView) popup_item.findViewById(R.id.lvPopup);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReturUnit.this,R.layout.ams_listview_item,R.id.tvItem,list_customer);
                    lv.setAdapter(adapter);
                    popup_item.show();
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItem = (String) parent.getItemAtPosition(position);
                            String customer = selectedItem;
                            if (unit_type.equals("peripheral")){
                                txt_peripheral_customer.setText(customer);
                                post_list_sn(customer);
                            }
                            else if (unit_type.equals("thermal")){
                                txt_thermal_customer.setText(customer);
                            }
                            popup_item.dismiss();
                        }
                    });
                }
                else{
                    Toast.makeText(ReturUnit.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(ReturUnit.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post_list_sn(String customer) {
        pop_loading.show();
        Call<ResponseBody> result = apiInterface.postListSN(ApiClient.api_key,warehouse_id,customer);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pop_loading.dismiss();
                try {
                    json_response = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                status = JsonData.get_data(json_response,"status");
                message = JsonData.get_data(json_response,"message");
                if (status.equals("200")) {
                    Integer rowcount = JsonData.get_listdata(json_response,"sn").size();
                    list_sn = new String[rowcount];
                    for (int i=0; i<rowcount; i++){
                        String sn = JsonData.get_listdata(json_response,"sn").get(i);
                        list_sn[i] = sn;
                    }
                    txt_sn.setAdapter(new ArrayAdapter<>(ReturUnit.this, android.R.layout.simple_list_item_1, list_sn));
                }
                else{
                    Toast.makeText(ReturUnit.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(ReturUnit.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post_check_tid(String tid) {
        pop_loading.show();
        Call<ResponseBody> result = apiInterface.postCheckTID(ApiClient.api_key,tid);
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
                    String merchant = JsonData.get_data(json_response,"merchant");
                    int qty = Integer.valueOf(edt_thermal_qty.getText().toString());
                    for (int i=0; i<qty; i++){
                        String tid = txt_tid.getText().toString();
                        String type = txt_thermal_type.getText().toString();
                        String customer = txt_thermal_customer.getText().toString();
                        retur_thermal_arrayList.add(new ReturUnitModel(merchant,"thermal",type,customer,"retur",tid,"","","",true));
                    }
                    saveArrayUnit(retur_thermal_arrayList,"retur_thermalKey");
                    SharedPreferences.Editor editor = Util.pref_ams.edit();
                    editor.putString("retur_updateKey", "1");
                    editor.commit();
                    display_retur_thermal(retur_thermal_arrayList);
                    int total = retur_edc_arrayList.size()+retur_simcard_arrayList.size()+retur_peripheral_arrayList.size()+retur_thermal_arrayList.size();
                    retur_order_arrayList.get(0).setQty(total);
                    txt_qty.setText(String.valueOf(retur_order_arrayList.get(0).getQty()));
                    pop_thermal.dismiss();
                }
                else{
                    Toast.makeText(ReturUnit.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(ReturUnit.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post_submit_retur_order() {
        pop_loading.show();
        Call<ResponseBody> result = apiInterface.postSubmitReturUnit(ApiClient.api_key,user_id,movement_id,order_no,qty,warehouse_id,data_unit);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pop_loading.dismiss();
                try {
                    json_response = response.body().string();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                status = JsonData.get_data(json_response,"status");
                message = JsonData.get_data(json_response,"message");
                if (status.equals("200")){
                    SharedPreferences.Editor editor = Util.pref_ams.edit();
                    editor.putString("retur_updateKey", "0");
                    editor.commit();
                    retur_edc_arrayList = new ArrayList<ReturUnitModel>();
                    saveArrayUnit(retur_edc_arrayList,"retur_unitKey");
                    txt_alert_header.setText("Success");
                    img_alert.setImageResource(R.drawable.ic_ok);
                    txt_alert_message.setText("Update Retur Unit !");
                    pop_alert.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(ReturUnit.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnScanRetur_click(View view) {
        if (unit_type.equals("edc")){
            saveArrayOrder(retur_order_arrayList,"retur_orderKey");
            saveArrayUnit(retur_edc_arrayList,"retur_unitKey");
            Intent intent = new Intent(this, ScanRetur.class);
            startActivity(intent);
        }else if (unit_type.equals("simcard")){
            txt_simcard.setText("");
            btn_ok_simcard.setText("OK");
            post_list_simcard();
            pop_simcard.show();
        }else if (unit_type.equals("peripheral")){
            txt_peripheral_type.setText("");
            txt_peripheral_customer.setText("");
            txt_sn.setText("");
            pop_peripheral.show();
        }else if (unit_type.equals("thermal")){
            txt_thermal_type.setText("THERMAL");
            txt_thermal_type.setEnabled(false);
            txt_thermal_customer.setText("");
            txt_tid.setText("");
            edt_thermal_qty.setText("0");
            pop_thermal.show();
        }
    }

    public void btnSubmitRetur_click(View view) {
        int istatus = get_data_unit();
        if (istatus>0){
            post_submit_retur_order();
        }else{
            Toast.makeText(this, "No Retur Unit !", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnSimCardCancel_click(View view) {
        pop_simcard.dismiss();
    }

    public void btnSubmitSimCard_click(View view) {
        String iccid = txt_simcard.getText().toString();
        if (iccid.equals("")){
            Toast.makeText(this, "Silahkan lengkapi ICCID !", Toast.LENGTH_SHORT).show();
        }else{
            if (search_simcard(iccid)==0){
                post_retur_check_unit("simcard",iccid);
            }else{
                Toast.makeText(this, "ICCID sudah ditambahkan !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void tvPeripheralType_Click(View view) {
        if (unit_type.equals("peripheral")){
            post_list_peripheral();
        }
    }

    public void tvPeripheralCustomer_Click(View view) {
        post_list_customer();
    }

    public void tvThermalCustomer_Click(View view) {
        post_list_customer();
    }

    public void btnCloseItem_click(View view) {
        popup_item.dismiss();
    }

    public void btnPeripheralCancel_click(View view) {
        pop_peripheral.dismiss();
    }

    public void btnPeripheralOk_click(View view) {
        if (txt_peripheral_type.getText().toString().equals("")){
            Toast.makeText(this, "Silahkan lengkapi Type !", Toast.LENGTH_SHORT).show();
        }
        else if (txt_peripheral_customer.getText().toString().equals("")){
            Toast.makeText(this, "Silahkan lengkapi Customer !", Toast.LENGTH_SHORT).show();
        }
        else if (txt_sn.getText().toString().equals("")){
            Toast.makeText(this, "Silahkan lengkapi S/N !", Toast.LENGTH_SHORT).show();
        }
        else
        {
            type = txt_peripheral_type.getText().toString();
            customer = txt_peripheral_customer.getText().toString();
            edc_sn = txt_sn.getText().toString();
            post_retur_check_unit("peripheral",edc_sn);
            pop_peripheral.dismiss();
        }
    }

    public void btnThermalCancel_click(View view) {
        pop_thermal.dismiss();
    }

    public void btnThermalOk_click(View view) {
        if (txt_thermal_type.getText().toString().equals("")){
            Toast.makeText(this, "Silahkan lengkapi Type !", Toast.LENGTH_SHORT).show();
        }
        else if (txt_thermal_customer.getText().toString().equals("")){
            Toast.makeText(this, "Silahkan lengkapi Customer !", Toast.LENGTH_SHORT).show();
        }
        else if (txt_tid.getText().toString().equals("")){
            Toast.makeText(this, "Silahkan lengkapi TID !", Toast.LENGTH_SHORT).show();
        }
        else if (edt_thermal_qty.getText().toString().equals("")||edt_thermal_qty.getText().toString().equals("0")){
            Toast.makeText(this, "Silahkan lengkapi Qty !", Toast.LENGTH_SHORT).show();
        }
        else{
            post_check_tid(txt_tid.getText().toString());
        }
    }

    private int search_simcard(String simcard_scan){
        int result = 0;
        for(int i=0; i<retur_simcard_arrayList.size(); i++) {
            String retur_simcard = retur_simcard_arrayList.get(i).getDesc_1().toString();
            if (simcard_scan.equals(retur_simcard)){
                result=1;
            }
        }
        return result;
    }

    private int get_data_unit(){
        int istatus = 0;
        data_unit = "";
        for(int i=0; i<retur_edc_arrayList.size(); i++) {
            String unit_type = "edc";
            String sn = retur_edc_arrayList.get(i).getDesc_1().toString();
            String status = retur_edc_arrayList.get(i).getStatus().toString();
            String lat = retur_edc_arrayList.get(i).getLat().toString();
            String lng = retur_edc_arrayList.get(i).getLng().toString();
            String job_type = retur_edc_arrayList.get(i).getJob_type().toString();
            String reason = retur_edc_arrayList.get(i).getReason().toString();
            boolean update = retur_edc_arrayList.get(i).isUpdate();
            if (update==true){
                data_unit = data_unit+",{\"unit_type\":\""+ unit_type + "\""
                        +",\"sn\":\""+ sn +"\""
                        +",\"type\":\"\""
                        +",\"warehouse_id\":\""+ warehouse_id +"\""
                        +",\"customer\":\"\""
                        +",\"status\":\""+ status.toLowerCase() +"\""
                        +",\"lat\":\""+ lat +"\""
                        +",\"lng\":\""+ lng +"\""
                        +",\"job_type\":\""+ job_type +"\""
                        +",\"reason\":\""+ reason +"\""
                        +"}";
                istatus += 1;
            }
        }
        for(int i=0; i<retur_simcard_arrayList.size(); i++) {
            String unit_type = "simcard";
            String iccid = retur_simcard_arrayList.get(i).getDesc_1().toString();
            String status = retur_simcard_arrayList.get(i).getStatus().toString();
            String lat = retur_simcard_arrayList.get(i).getLat().toString();
            String lng = retur_simcard_arrayList.get(i).getLng().toString();
            String job_type = retur_simcard_arrayList.get(i).getJob_type().toString();
            String reason = retur_simcard_arrayList.get(i).getReason().toString();
            boolean update = retur_simcard_arrayList.get(i).isUpdate();
            if (update==true){
                data_unit = data_unit+",{\"unit_type\":\""+ unit_type + "\""
                        +",\"sn\":\""+ iccid +"\""
                        +",\"type\":\"\""
                        +",\"warehouse_id\":\""+ warehouse_id +"\""
                        +",\"customer\":\"\""
                        +",\"status\":\""+ status.toLowerCase() +"\""
                        +",\"lat\":\""+ lat +"\""
                        +",\"lng\":\""+ lng +"\""
                        +",\"job_type\":\""+ job_type +"\""
                        +",\"reason\":\""+ reason +"\""
                        +"}";
                istatus += 1;
            }
        }
        for(int i=0; i<retur_peripheral_arrayList.size(); i++) {
            String unit_type = "peripheral";
            String sn = retur_peripheral_arrayList.get(i).getJob_type().toString();
            String type = retur_peripheral_arrayList.get(i).getDesc_1().toString();
            String customer = retur_peripheral_arrayList.get(i).getDesc_2().toString();
            String status = retur_peripheral_arrayList.get(i).getStatus().toString();
            String lat = retur_peripheral_arrayList.get(i).getLat().toString();
            String lng = retur_peripheral_arrayList.get(i).getLng().toString();
            String job_type = retur_peripheral_arrayList.get(i).getJob_type().toString();
            String reason = retur_peripheral_arrayList.get(i).getReason().toString();
            boolean update = retur_peripheral_arrayList.get(i).isUpdate();
            if (update==true){
                data_unit = data_unit+",{\"unit_type\":\""+ unit_type + "\""
                        +",\"sn\":\""+ sn +"\""
                        +",\"type\":\""+ type +"\""
                        +",\"warehouse_id\":\""+ warehouse_id +"\""
                        +",\"customer\":\""+ customer +"\""
                        +",\"status\":\""+ status.toLowerCase() +"\""
                        +",\"lat\":\""+ lat +"\""
                        +",\"lng\":\""+ lng +"\""
                        +",\"job_type\":\""+ job_type +"\""
                        +",\"reason\":\""+ reason +"\""
                        +"}";
                istatus += 1;
            }
        }
        for(int i=0; i<retur_thermal_arrayList.size(); i++) {
            String unit_type = "thermal";
            String type = retur_thermal_arrayList.get(i).getDesc_1().toString();
            String customer = retur_thermal_arrayList.get(i).getDesc_2().toString();
            String status = retur_thermal_arrayList.get(i).getStatus().toString();
            String lat = retur_thermal_arrayList.get(i).getLat().toString();
            String lng = retur_thermal_arrayList.get(i).getLng().toString();
            String job_type = retur_thermal_arrayList.get(i).getJob_type().toString();
            String reason = retur_thermal_arrayList.get(i).getReason().toString();
            boolean update = retur_thermal_arrayList.get(i).isUpdate();
            if (update==true){
                data_unit = data_unit+",{\"unit_type\":\""+ unit_type + "\""
                        +",\"sn\":\""+ job_type +"\""
                        +",\"type\":\""+ type +"\""
                        +",\"warehouse_id\":\""+ warehouse_id +"\""
                        +",\"customer\":\""+ customer +"\""
                        +",\"status\":\""+ status.toLowerCase() +"\""
                        +",\"lat\":\""+ lat +"\""
                        +",\"lng\":\""+ lng +"\""
                        +",\"job_type\":\""+ job_type +"\""
                        +",\"reason\":\""+ reason +"\""
                        +"}";
                istatus += 1;
            }
        }
        data_unit = "["+data_unit+"]";
        data_unit = data_unit.replace("[,","[");
        return istatus;
    }

    public void btnAlertOk_click(View view) {
        pop_alert.dismiss();
        Intent intent = new Intent(ReturUnit.this,Retur.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ReturUnit.this,Retur.class);
        startActivity(intent);
    }

    public void btnClose_click(View view) {
        Intent intent = new Intent(ReturUnit.this,Retur.class);
        startActivity(intent);
    }

    public void saveArrayOrder(ArrayList<ReturOrderModel> list, String key){
        SharedPreferences.Editor editor = Util.pref_ams.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.commit();
    }

    public ArrayList<ReturOrderModel> getArrayOrder(String key){
        Gson gson = new Gson();
        String json = Util.pref_ams.getString(key, null);
        Type type = new TypeToken<ArrayList<ReturOrderModel>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveArrayUnit(ArrayList<ReturUnitModel> list, String key){
        SharedPreferences.Editor editor = Util.pref_ams.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.commit();
    }

    public ArrayList<ReturUnitModel> getArrayUnit(String key){
        Gson gson = new Gson();
        String json = Util.pref_ams.getString(key, null);
        Type type = new TypeToken<ArrayList<ReturUnitModel>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void display_retur_edc(final ArrayList<ReturUnitModel> retur_unitAdapters) {
        ReturUnitAdapter retur_unit_adapter = new ReturUnitAdapter(this, R.layout.ams_listview_retur_unit,retur_unitAdapters);
        ListView listView = (ListView) findViewById(R.id.lvReturEDC);
        listView.setAdapter(retur_unit_adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReturUnitModel retur_unitAdapter = (ReturUnitModel) parent.getItemAtPosition(position);
                String sn = retur_unitAdapter.getDesc_1();
            }
        });
    }

    private void display_retur_simcard(final ArrayList<ReturUnitModel> retur_unitAdapters) {
        ReturUnitAdapter retur_unit_adapter = new ReturUnitAdapter(this, R.layout.ams_listview_retur_unit,retur_unitAdapters);
        ListView listView = (ListView) findViewById(R.id.lvReturSimCard);
        listView.setAdapter(retur_unit_adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReturUnitModel retur_unitAdapter = (ReturUnitModel) parent.getItemAtPosition(position);
                String sn = retur_unitAdapter.getDesc_1();
            }
        });
    }

    private void display_retur_peripheral(final ArrayList<ReturUnitModel> retur_unitAdapters) {
        ReturUnitAdapter retur_unit_adapter = new ReturUnitAdapter(this, R.layout.ams_listview_retur_unit,retur_unitAdapters);
        ListView listView = (ListView) findViewById(R.id.lvReturPeripheral);
        listView.setAdapter(retur_unit_adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReturUnitModel retur_unitAdapter = (ReturUnitModel) parent.getItemAtPosition(position);
                String sn = retur_unitAdapter.getDesc_1();
            }
        });
    }

    private void display_retur_thermal(final ArrayList<ReturUnitModel> retur_unitAdapters) {
        ReturUnitAdapter retur_unit_adapter = new ReturUnitAdapter(this, R.layout.ams_listview_retur_unit,retur_unitAdapters);
        ListView listView = (ListView) findViewById(R.id.lvReturThermal);
        listView.setAdapter(retur_unit_adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReturUnitModel retur_unitAdapter = (ReturUnitModel) parent.getItemAtPosition(position);
                String sn = retur_unitAdapter.getDesc_1();
            }
        });
    }

    private class ReturUnitAdapter extends ArrayAdapter<ReturUnitModel> {

        private ArrayList<ReturUnitModel> retur_unitAdapterArrayList;

        public ReturUnitAdapter(Context context, int textViewResourceId, ArrayList<ReturUnitModel> retur_unitAdapterArrayList) {
            super(context, textViewResourceId, retur_unitAdapterArrayList);
            this.retur_unitAdapterArrayList = new ArrayList<ReturUnitModel>();
            this.retur_unitAdapterArrayList.addAll(retur_unitAdapterArrayList);
        }

        private class ViewHolder {
            ImageView img_unit;
            TextView merchant;
            TextView type;
            TextView desc_1_name;
            TextView desc_1;
            TextView desc_2_name;
            TextView desc_2;
            TextView job_type;
            TextView reason;
            LinearLayout lr_jobtype;
            LinearLayout lr_reason;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ReturUnitAdapter.ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.ams_listview_retur_unit, null);
                holder = new ReturUnitAdapter.ViewHolder();
                holder.img_unit = (ImageView) convertView.findViewById(R.id.imgUnit);
                holder.merchant = (TextView) convertView.findViewById(R.id.tvMerchant);
                holder.desc_1_name = (TextView) convertView.findViewById(R.id.tvDesc1Name);
                holder.desc_1 = (TextView) convertView.findViewById(R.id.tvDesc1);
                holder.desc_2_name = (TextView) convertView.findViewById(R.id.tvDesc2Name);
                holder.desc_2 = (TextView) convertView.findViewById(R.id.tvDesc2);
                holder.job_type = (TextView) convertView.findViewById(R.id.tvJobType);
                holder.reason = (TextView) convertView.findViewById(R.id.tvReason);
                holder.lr_jobtype = (LinearLayout) convertView.findViewById(R.id.lrJobType);
                holder.lr_reason = (LinearLayout) convertView.findViewById(R.id.lrReason);
                convertView.setTag(holder);

            } else {
                holder = (ReturUnitAdapter.ViewHolder) convertView.getTag();
            }

            ReturUnitModel retur_unitAdapter = retur_unitAdapterArrayList.get(position);
            holder.merchant.setText(retur_unitAdapter.getMerchant());
            String type = retur_unitAdapter.getType();
            holder.lr_jobtype.setVisibility(View.GONE);
            holder.lr_reason.setVisibility(View.GONE);
            if (type.equals("edc")){
                holder.img_unit.setImageResource(R.drawable.edc);
                holder.desc_1_name.setText("S/N");
                holder.desc_2_name.setText("P/N");
                holder.lr_jobtype.setVisibility(View.VISIBLE);
                holder.lr_reason.setVisibility(View.VISIBLE);
            }
            else if (type.equals("simcard")){
                holder.img_unit.setImageResource(R.drawable.simcard);
                holder.desc_1_name.setText("ICCID");
                holder.desc_2_name.setText("Operator");
            }
            else if (type.equals("peripheral")){
                holder.img_unit.setImageResource(R.drawable.peripheral);
                holder.desc_1_name.setText("Type");
                holder.desc_2_name.setText("Customer");
            }
            else if (type.equals("thermal")){
                holder.img_unit.setImageResource(R.drawable.thermal);
                holder.desc_1_name.setText("Type");
                holder.desc_2_name.setText("Customer");
            }
            holder.desc_1.setText(retur_unitAdapter.getDesc_1());
            holder.desc_2.setText(retur_unitAdapter.getDesc_2());
            holder.job_type.setText(retur_unitAdapter.getJob_type());
            holder.reason.setText(retur_unitAdapter.getReason());
            String unit_status = retur_unitAdapter.getStatus();
            return convertView;
        }
    }

}
