package com.gip.fsa.apps.ams;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gip.fsa.R;
import com.gip.fsa.apps.ams.Api.ApiClient;
import com.gip.fsa.apps.ams.Api.ApiInterface;
import com.gip.fsa.apps.ams.Api.JsonData;
import com.gip.fsa.apps.ams.Model.ReceiveUnitModel;
import com.gip.fsa.utility.SignatureUtility;
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

public class IntransitUnit extends AppCompatActivity {

    Retrofit retrofit;
    ApiInterface apiInterface;
    int receive_update = 0;

    Dialog pop_alert,pop_loading,popup_item;
    TextView txt_alert_header,txt_alert_message,txt_header_popup;
    ImageView img_alert;
    Button btn_alert_ok,btn_scan,btn_receive_all;

    String json_response,status,message,user_id,type,lat,lng,data_unit,alert_type;

    ArrayList<ReceiveUnitModel> receive_unit_arrayList = null;
    ReceiveUnitAdapter receive_unit_adapter = null;
    ListView lv_receive_unit,lv_popup;
    ArrayList<String> list_status = new ArrayList<String>();

    private SharedPreferences sharedPreferences;
    private SignatureUtility signatureUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ams_activity_intransit_unit);

      Initialisasi();
      loadData();
        btn_receive_all.setVisibility(View.GONE);
        btn_scan.setVisibility(View.GONE);
        if (type.equals("EDC")){
            btn_scan.setVisibility(View.VISIBLE);
        }
        if (type.equals("THERMAL")){
            btn_receive_all.setVisibility(View.VISIBLE);
        }
        if (receive_update==0){
            post_receive_unit();
        }else{
            Util.pref_ams = getSharedPreferences("FMS", Context.MODE_PRIVATE);
            if (Util.pref_ams.contains("receive_unitKey")) {
                receive_unit_arrayList = getArrayList("receive_unitKey");
                SharedPreferences.Editor editor = Util.pref_ams.edit();
                editor.putString("receive_updateKey", "0");
                editor.commit();
            }
            display_receive_unit(receive_unit_arrayList);
        }

    }

    private void Initialisasi(){
        retrofit = ApiClient.retrofit();
        apiInterface = retrofit.create(ApiInterface.class);
        sharedPreferences   = getSharedPreferences("FMS", MODE_PRIVATE);
        signatureUtility    = new SignatureUtility();

        lv_receive_unit = (ListView) findViewById(R.id.lvReceiveUnit);
        receive_unit_arrayList = new ArrayList<ReceiveUnitModel>();
        btn_scan = (Button) findViewById(R.id.btnScan);
        btn_receive_all = (Button) findViewById(R.id.btnReceiveAll);

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

        popup_item = new Dialog(this);
        popup_item.setContentView(R.layout.ams_popup_listview);
        popup_item.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup_item.setCanceledOnTouchOutside(false);
        txt_header_popup = popup_item.findViewById(R.id.tvHeaderPopup);
        lv_popup = popup_item.findViewById(R.id.lvPopup);

    }

    private void loadData(){
        Util.pref_ams = getSharedPreferences("FMS", Context.MODE_PRIVATE);
        if (Util.pref_ams.contains("receive_updateKey")) {
            receive_update = Integer.valueOf(Util.pref_ams.getString("receive_updateKey", ""));
        }
        if (Util.pref_ams.contains("typeKey")) {
            type = Util.pref_ams.getString("typeKey", "");
        }
        if (Util.pref_ams.contains("user_idKey")) {
            user_id = Util.pref_ams.getString("user_idKey", "");
        }
    }

    private void post_receive_unit() {
        pop_loading.show();
        Call<ResponseBody> result = apiInterface.postReceiveUnit(ApiClient.api_key,user_id,type);
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
                    receive_unit_arrayList = new ArrayList<ReceiveUnitModel>();
                    Integer rowcount = JsonData.get_listdata(json_response,"unit_id").size();
                    for (int i=0; i<rowcount; i++){
                        String unit_id = JsonData.get_listdata(json_response,"unit_id").get(i);
                        String merchant = JsonData.get_listdata(json_response,"merchant").get(i);
                        String type = JsonData.get_listdata(json_response,"type").get(i);
                        String desc_1 = JsonData.get_listdata(json_response,"desc_1").get(i);
                        String desc_2 = JsonData.get_listdata(json_response,"desc_2").get(i);
                        String status = JsonData.get_listdata(json_response,"status").get(i);
                        Integer qr_scan = Integer.valueOf(JsonData.get_listdata(json_response,"qr_scan").get(i));
                        receive_unit_arrayList.add(new ReceiveUnitModel(unit_id,merchant,type,desc_1,desc_2,status,qr_scan,"","",false));
                    }
                    display_receive_unit(receive_unit_arrayList);
                }
                else{
                    Toast.makeText(IntransitUnit.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(IntransitUnit.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post_submit_receive_unit() {
        pop_loading.show();
        Call<ResponseBody> result = apiInterface.postSubmitReceiveUnit(ApiClient.api_key,user_id,data_unit);
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
                    txt_alert_header.setText("Success");
                    img_alert.setImageResource(R.drawable.ic_ok);
                    txt_alert_message.setText("Update Receive Unit !");
                    alert_type = "";
                    pop_alert.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(IntransitUnit.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post_submit_receive_unit_v22() {
        pop_loading.show();
        String account_id = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String datetime = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        String signature = signatureUtility.doSignature(datetime, account_id);
        Call<ResponseBody> result = apiInterface.postSubmitReceiveUnit_v22(ApiClient.api_key,user_id,account_id,datetime,signature,data_unit);
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
                    txt_alert_header.setText("Success");
                    img_alert.setImageResource(R.drawable.ic_ok);
                    txt_alert_message.setText("Update Receive Unit !");
                    alert_type = "";
                    pop_alert.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(IntransitUnit.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnAlertOk_click(View view) {
        if (alert_type.equals("received")){
            for(int i=0; i<receive_unit_arrayList.size(); i++) {
                receive_unit_arrayList.get(i).setStatus("Received");
                receive_unit_arrayList.get(i).setUpdate(true);
            }
            display_receive_unit(receive_unit_arrayList);
            pop_alert.dismiss();
        }else{
            pop_alert.dismiss();
            Intent intent = new Intent(IntransitUnit.this,Intransit.class);
            startActivity(intent);
        }
    }

    private int get_data_unit(){
        int istatus = 0;
        data_unit = "";
        for(int i=0; i<receive_unit_arrayList.size(); i++) {
            String unit_id = receive_unit_arrayList.get(i).getUnit_id().toString();
            String status = receive_unit_arrayList.get(i).getStatus().toString();
            String lat = receive_unit_arrayList.get(i).getLat().toString();
            String lng = receive_unit_arrayList.get(i).getLng().toString();
            boolean update = receive_unit_arrayList.get(i).getUpdate();
            if (update==true){
                data_unit = data_unit+",{\"unit_id\":\""+ unit_id + "\""
                        +",\"status\":\""+ status.toLowerCase() +"\""
                        +",\"lat\":\""+ lat +"\""
                        +",\"lng\":\""+ lng +"\"}";
                istatus += 1;
            }
        }
        data_unit = "["+data_unit+"]";
        data_unit = data_unit.replace("[,","[");
        return istatus;
    }

    public void btnReceiveAll_click(View view) {
        alert_type = "received";
        String total = String.valueOf(receive_unit_arrayList.size());
        txt_alert_header.setText("Receive All Unit ?");
        txt_alert_message.setText("Total "+total+" unit");
        pop_alert.show();
    }

    public void btnSubmitReceive_click(View view) {
        alert_type = "";
        int istatus = get_data_unit();
        if (istatus>0){
            post_submit_receive_unit();
        }else{
            Toast.makeText(this, "No " + type + " Submit", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnScanReceive_click(View view) {
        saveArrayList(receive_unit_arrayList,"receive_unitKey");
        Intent intent = new Intent(this, ScanIntransit.class);
        startActivity(intent);
    }

    public void btnClose_click(View view) {
        Intent intent = new Intent(this, Intransit.class);
        startActivity(intent);
    }

    public void btnCloseItem_click(View view) {
        popup_item.dismiss();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Intransit.class);
        startActivity(intent);
    }

    public void saveArrayList(ArrayList<ReceiveUnitModel> list, String key){
        SharedPreferences.Editor editor = Util.pref_ams.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.commit();
    }

    public ArrayList<ReceiveUnitModel> getArrayList(String key){
        Gson gson = new Gson();
        String json = Util.pref_ams.getString(key, null);
        Type type = new TypeToken<ArrayList<ReceiveUnitModel>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void display_receive_unit(final ArrayList<ReceiveUnitModel> receive_unitAdapters) {
        receive_unit_adapter = new ReceiveUnitAdapter(this, R.layout.ams_listview_receive_unit,receive_unitAdapters);
        ListView listView = (ListView) findViewById(R.id.lvReceiveUnit);
        listView.setAdapter(receive_unit_adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ReceiveUnitModel receive_unitAdapter = (ReceiveUnitModel) parent.getItemAtPosition(position);
                final Integer i = position;
                Integer qr_scan = receive_unitAdapter.getQr_scan();
                if (qr_scan==1){
                    Toast.makeText(IntransitUnit.this, "Silahkan Scan QR Barcode !", Toast.LENGTH_SHORT).show();
                }else{
                    list_status = new ArrayList<String>();
                    list_status.add("Received");
                    txt_header_popup.setText("Choose Status");
                    ListView lv = (ListView) popup_item.findViewById(R.id.lvPopup);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(IntransitUnit.this,R.layout.ams_listview_item,R.id.tvItem,list_status);
                    lv.setAdapter(adapter);
                    popup_item.show();
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItem = (String) parent.getItemAtPosition(position);
                            receive_unit_arrayList.get(i).setStatus(selectedItem);
                            receive_unit_arrayList.get(i).setUpdate(true);
                            display_receive_unit(receive_unit_arrayList);
                            popup_item.dismiss();
                        }
                    });
                }
            }
        });
    }

    private class ReceiveUnitAdapter extends ArrayAdapter<ReceiveUnitModel> {

        private ArrayList<ReceiveUnitModel> receive_unitAdapterArrayList;

        public ReceiveUnitAdapter(Context context, int textViewResourceId, ArrayList<ReceiveUnitModel> receive_unitAdapterArrayList) {
            super(context, textViewResourceId, receive_unitAdapterArrayList);
            this.receive_unitAdapterArrayList = new ArrayList<ReceiveUnitModel>();
            this.receive_unitAdapterArrayList.addAll(receive_unitAdapterArrayList);
        }

        private class ViewHolder {
            ImageView img_unit;
            TextView merchant;
            TextView type;
            TextView desc_1_name;
            TextView desc_1;
            TextView desc_2_name;
            TextView desc_2;
            TextView status;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ReceiveUnitAdapter.ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.ams_listview_receive_unit, null);
                holder = new ReceiveUnitAdapter.ViewHolder();
                holder.img_unit = (ImageView) convertView.findViewById(R.id.imgUnit);
                holder.merchant = (TextView) convertView.findViewById(R.id.tvMerchant);
                holder.type = (TextView) convertView.findViewById(R.id.tvType);
                holder.desc_1_name = (TextView) convertView.findViewById(R.id.tvDesc1Name);
                holder.desc_1 = (TextView) convertView.findViewById(R.id.tvDesc1);
                holder.desc_2_name = (TextView) convertView.findViewById(R.id.tvDesc2Name);
                holder.desc_2 = (TextView) convertView.findViewById(R.id.tvDesc2);
                holder.status = (TextView) convertView.findViewById(R.id.tvStatus);
                convertView.setTag(holder);

            } else {
                holder = (ReceiveUnitAdapter.ViewHolder) convertView.getTag();
            }

            ReceiveUnitModel receive_unitAdapter = receive_unitAdapterArrayList.get(position);
            holder.merchant.setText(receive_unitAdapter.getMerchant());
            String type = receive_unitAdapter.getType();
            if(type.equals("EDC")){
                holder.desc_1_name.setText("S/N");
                holder.desc_2_name.setText("P/N");
                holder.img_unit.setImageResource(R.drawable.edc);
            }
            else if(type.equals("SIMCARD")){
                holder.desc_1_name.setText("ICCID");
                holder.desc_2_name.setText("Operator");
                holder.img_unit.setImageResource(R.drawable.simcard);
            }
            else if(type.equals("PERIPHERAL")){
                holder.desc_1_name.setText("Name");
                holder.desc_2_name.setText("Customer");
                holder.img_unit.setImageResource(R.drawable.peripheral);
            }
            else if(type.equals("THERMAL")){
                holder.desc_1_name.setText("TID");
                holder.desc_2_name.setText("Customer");
                holder.img_unit.setImageResource(R.drawable.thermal);
            }
            holder.type.setText(type);
            holder.desc_1.setText(receive_unitAdapter.getDesc_1());
            holder.desc_2.setText(receive_unitAdapter.getDesc_2());
            String unit_status = receive_unitAdapter.getStatus();
            holder.status.setText(unit_status);
            if (unit_status.equals("Received")){
                holder.status.setTextColor(getResources().getColor(R.color.color_green));
            }else{
                holder.status.setTextColor(getResources().getColor(R.color.snack_red));
            }
            return convertView;
        }
    }

}
