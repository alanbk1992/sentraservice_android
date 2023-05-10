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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gip.fsa.R;
import com.gip.fsa.apps.ams.Api.ApiClient;
import com.gip.fsa.apps.ams.Api.ApiInterface;
import com.gip.fsa.apps.ams.Api.JsonData;
import com.gip.fsa.apps.ams.Model.ReceiveUnitModel;
import com.gip.fsa.apps.ams.Model.UnitCheckModel;
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

public class InstallationUnit extends AppCompatActivity {

    Retrofit retrofit;
    ApiInterface apiInterface;
    int install_update = 0;

    Dialog pop_alert,pop_loading,popup_item,popup_tid;
    TextView txt_alert_header,txt_alert_message,txt_header_popup,txt_customer,txt_unit_status,txt_unit_qty;
    ImageView img_alert;
    Button btn_alert_ok,btn_scan,btn_installed_all;

    EditText edt_unit_qty;

    String json_response,status,message,user_id,type,lat,lng,data_unit,alert_type;

    ArrayList<ReceiveUnitModel> installation_unit_arrayList = null;
    InstallationUnitAdapter installation_unit_adapter = null;
    ListView lv_installation_unit,lv_popup;
    ArrayList<UnitCheckModel> data_array = null;
    ArrayList<String> list_status = new ArrayList<String>();

    AutoCompleteTextView txt_tid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ams_activity_installation_unit);

        Initialisasi();
        loadData();
        btn_installed_all.setVisibility(View.GONE);
        btn_scan.setVisibility(View.GONE);
        if (type.equals("EDC")){
            btn_scan.setVisibility(View.VISIBLE);
        }
        if (type.equals("THERMAL")){
            btn_installed_all.setVisibility(View.VISIBLE);
        }
        if (install_update==0){
            post_installation_unit();
        }else{
            Util.pref_ams = getSharedPreferences("FMS", Context.MODE_PRIVATE);
            if (Util.pref_ams.contains("installation_unitKey")) {
                installation_unit_arrayList = getArrayList("installation_unitKey");
                SharedPreferences.Editor editor = Util.pref_ams.edit();
                editor.putString("install_updateKey", "0");
                editor.commit();
            }
            get_data_unit();
            display_installation_unit(installation_unit_arrayList);
        }

    }

    private void Initialisasi(){
        retrofit = ApiClient.retrofit();
        apiInterface = retrofit.create(ApiInterface.class);

        lv_installation_unit = (ListView) findViewById(R.id.lvInstallationUnit);
        installation_unit_arrayList = new ArrayList<ReceiveUnitModel>();
        btn_scan = (Button) findViewById(R.id.btnScan);
        btn_installed_all = (Button) findViewById(R.id.btnInstalledAll);

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

        popup_tid = new Dialog(this);
        popup_tid.setContentView(R.layout.ams_popup_tid);
        popup_tid.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup_tid.setCanceledOnTouchOutside(false);
        txt_tid = popup_tid.findViewById(R.id.txtTID);
        txt_customer = popup_tid.findViewById(R.id.txtCustomer);
        txt_unit_status = popup_tid.findViewById(R.id.txtUnitStatus);
        txt_unit_qty = popup_tid.findViewById(R.id.txtUnitQty);
        edt_unit_qty = popup_tid.findViewById(R.id.edtUnitQty);

    }

    private void loadData(){
        Util.pref_ams = getSharedPreferences("FMS", Context.MODE_PRIVATE);
        if (Util.pref_ams.contains("install_updateKey")) {
            install_update = Integer.valueOf(Util.pref_ams.getString("install_updateKey", ""));
        }
        if (Util.pref_ams.contains("typeKey")) {
            type = Util.pref_ams.getString("typeKey", "");
        }
        if (Util.pref_ams.contains("user_idKey")) {
            user_id = Util.pref_ams.getString("user_idKey", "");
        }
    }

    private void post_installation_unit() {
        pop_loading.show();
        Call<ResponseBody> result = apiInterface.postInstallationUnit(ApiClient.api_key,user_id,type);
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
                    installation_unit_arrayList = new ArrayList<ReceiveUnitModel>();
                    Integer rowcount = JsonData.get_listdata(json_response,"unit_id").size();
                    for (int i=0; i<rowcount; i++){
                        String unit_id = JsonData.get_listdata(json_response,"unit_id").get(i);
                        String merchant = JsonData.get_listdata(json_response,"merchant").get(i);
                        String type = JsonData.get_listdata(json_response,"type").get(i);
                        String desc_1 = JsonData.get_listdata(json_response,"desc_1").get(i);
                        String desc_2 = JsonData.get_listdata(json_response,"desc_2").get(i);
                        String status = JsonData.get_listdata(json_response,"status").get(i);
                        Integer qr_scan = Integer.valueOf(JsonData.get_listdata(json_response,"qr_scan").get(i));
                        installation_unit_arrayList.add(new ReceiveUnitModel(unit_id,merchant,type,desc_1,desc_2,status,qr_scan,"","",false));
                    }
                    display_installation_unit(installation_unit_arrayList);
                }
                else{
                    Toast.makeText(InstallationUnit.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(InstallationUnit.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    String customer = txt_customer.getText().toString().toLowerCase();
                    int iunit = 0;
                    int total_unit = Integer.valueOf(edt_unit_qty.getText().toString());
                    for(int i=0; i<installation_unit_arrayList.size() ; i++) {
                        String customer_ = installation_unit_arrayList.get(i).getDesc_2().toString().toLowerCase();
                        if (customer.equals(customer_)){
                            installation_unit_arrayList.get(i).setMerchant(merchant);
                            installation_unit_arrayList.get(i).setDesc_1(txt_tid.getText().toString());
                            installation_unit_arrayList.get(i).setStatus(txt_unit_status.getText().toString());
                            installation_unit_arrayList.get(i).setUpdate(true);
                            iunit = iunit+1;
                        }
                        if (iunit==total_unit){
                            break;
                        }
                    }
                    display_installation_unit(installation_unit_arrayList);
                    popup_tid.dismiss();
                }
                else{
                    Toast.makeText(InstallationUnit.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(InstallationUnit.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post_submit_installation_unit() {
        pop_loading.show();
        Call<ResponseBody> result = apiInterface.postSubmitInstallationUnit(ApiClient.api_key,user_id,data_unit);
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
                    txt_alert_message.setText("Update Installed Unit !");
                    pop_alert.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(InstallationUnit.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnAlertOk_click(View view) {
        if (alert_type.equals("installed")){
            for(int i=0; i<installation_unit_arrayList.size(); i++) {
                String tid = installation_unit_arrayList.get(i).getDesc_1();
                if (!tid.equals("-")){
                    installation_unit_arrayList.get(i).setStatus("Installed");
                    installation_unit_arrayList.get(i).setUpdate(true);
                }
            }
            display_installation_unit(installation_unit_arrayList);
            pop_alert.dismiss();
        }else{
            pop_alert.dismiss();
            Intent intent = new Intent(InstallationUnit.this,Installation.class);
            startActivity(intent);
        }
    }

    public void btnClose_click(View view) {
        Intent intent = new Intent(InstallationUnit.this,Installation.class);
        startActivity(intent);
    }

    public void btnCloseItem_click(View view) {
        popup_item.dismiss();
    }

    private int get_data_unit(){
        int istatus = 0;
        data_unit = "";
        for(int i=0; i<installation_unit_arrayList.size(); i++) {
            String unit_id = installation_unit_arrayList.get(i).getUnit_id().toString();
            String status = installation_unit_arrayList.get(i).getStatus().toString();
            String lat = installation_unit_arrayList.get(i).getLat().toString();
            String lng = installation_unit_arrayList.get(i).getLng().toString();
            boolean update = installation_unit_arrayList.get(i).getUpdate();
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

    public void btnSubmitInstallation_click(View view) {
        alert_type = "";
        int istatus = get_data_unit();
        if (istatus>0){
            post_submit_installation_unit();
        }else{
            Toast.makeText(this, "No Installed Unit Submit", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnScanInstallation_click(View view) {
        saveArrayList(installation_unit_arrayList,"installation_unitKey");
        Intent intent = new Intent(this, ScanInstallation.class);
        startActivity(intent);
    }

    public void btnTIDCancel_click(View view) {
        popup_tid.dismiss();
    }

    public void tvUnitStatus_Click(View view) {
        list_status = new ArrayList<String>();
        list_status.add("Installed");
        txt_header_popup.setText("Choose Status");
        ListView lv = (ListView) popup_item.findViewById(R.id.lvPopup);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(InstallationUnit.this,R.layout.ams_listview_item,R.id.tvItem,list_status);
        lv.setAdapter(adapter);
        popup_item.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                txt_unit_status.setText(selectedItem);
                popup_item.dismiss();
            }
        });
    }

    public void btnTIDOk_click(View view) {
        if (txt_tid.getText().toString().equals("")){
            Toast.makeText(this, "Silahkan lengkapi TID !", Toast.LENGTH_SHORT).show();
        }
        else if (txt_customer.getText().toString().equals("")){
            Toast.makeText(this, "Silahkan lengkapi Customer !", Toast.LENGTH_SHORT).show();
        }
        else if (txt_unit_status.getText().toString().equals("")){
            Toast.makeText(this, "Silahkan lengkapi Status !", Toast.LENGTH_SHORT).show();
        }
        else if (edt_unit_qty.getText().toString().equals("")||edt_unit_qty.getText().equals("0")){
            Toast.makeText(this, "Silahkan lengkapi Qty !", Toast.LENGTH_SHORT).show();
        }
        else if (Integer.valueOf(edt_unit_qty.getText().toString()) > Integer.valueOf(txt_unit_qty.getText().toString())){
            Toast.makeText(this, "Qty maksimal "+txt_unit_qty.getText().toString()+" !", Toast.LENGTH_SHORT).show();
        }
        else{
            post_check_tid(txt_tid.getText().toString());
        }
    }

    public void btnInstalledAll_click(View view) {
        alert_type = "installed";
        int total = 0;
        for(int i=0; i<installation_unit_arrayList.size(); i++) {
            String tid = installation_unit_arrayList.get(i).getDesc_1();
            if (!tid.equals("-")){
                total = total +1;
            }
        }
        txt_alert_header.setText("Installed All Pairing Unit ?");
        txt_alert_message.setText("Total "+String.valueOf(total)+" unit");
        pop_alert.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(InstallationUnit.this,Installation.class);
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

    private void display_installation_unit(final ArrayList<ReceiveUnitModel> installation_unitAdapters) {
        installation_unit_adapter = new InstallationUnitAdapter(this, R.layout.ams_listview_receive_unit,installation_unitAdapters);
        ListView listView = (ListView) findViewById(R.id.lvInstallationUnit);
        listView.setAdapter(installation_unit_adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReceiveUnitModel installation_unitAdapter = (ReceiveUnitModel) parent.getItemAtPosition(position);
                final Integer i = position;
                Integer qr_scan = installation_unitAdapter.getQr_scan();
                String type = installation_unitAdapter.getType();
                String tid = installation_unitAdapter.getDesc_1();
                String customer = installation_unitAdapter.getDesc_2();
                if (qr_scan==1){
                    Toast.makeText(InstallationUnit.this, "Silahkan Scan QR Barcode !", Toast.LENGTH_SHORT).show();
                }else if (type.equals("THERMAL") && tid.equals("-")){
                    txt_tid.setText("");
                    txt_customer.setText(customer);
                    txt_unit_status.setText("");
                    int qty = 0;
                    for(int j=0; j<installation_unit_arrayList.size(); j++) {
                        String customer_ = installation_unit_arrayList.get(j).getDesc_2();
                        String tid_ = installation_unit_arrayList.get(j).getDesc_1();
                        if (customer.equals(customer_) && (tid.equals(tid_))){
                            qty = qty+1;
                        }
                    }
                    txt_unit_qty.setText(String.valueOf(qty));
                    edt_unit_qty.setText(String.valueOf(qty));
                    popup_tid.show();
                }
                else{
                    list_status = new ArrayList<String>();
                    list_status.add("Installed");
                    txt_header_popup.setText("Choose Status");
                    ListView lv = (ListView) popup_item.findViewById(R.id.lvPopup);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(InstallationUnit.this,R.layout.ams_listview_item,R.id.tvItem,list_status);
                    lv.setAdapter(adapter);
                    popup_item.show();
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItem = (String) parent.getItemAtPosition(position);
                            installation_unit_arrayList.get(i).setStatus(selectedItem);
                            installation_unit_arrayList.get(i).setUpdate(true);
                            display_installation_unit(installation_unit_arrayList);
                            popup_item.dismiss();
                        }
                    });
                }
            }
        });
    }

    private class InstallationUnitAdapter extends ArrayAdapter<ReceiveUnitModel> {

        private ArrayList<ReceiveUnitModel> receive_unitAdapterArrayList;

        public InstallationUnitAdapter(Context context, int textViewResourceId, ArrayList<ReceiveUnitModel> receive_unitAdapterArrayList) {
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
            InstallationUnitAdapter.ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.ams_listview_receive_unit, null);
                holder = new InstallationUnitAdapter.ViewHolder();
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
                holder = (InstallationUnitAdapter.ViewHolder) convertView.getTag();
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
            if (unit_status.equals("Installed")){
                holder.status.setTextColor(getResources().getColor(R.color.color_green));
            }else if (unit_status.equals("Retur")){
                holder.status.setTextColor(getResources().getColor(R.color.color_yellow));
            }else{
                holder.status.setTextColor(getResources().getColor(R.color.snack_red));
            }
            return convertView;
        }
    }

}
