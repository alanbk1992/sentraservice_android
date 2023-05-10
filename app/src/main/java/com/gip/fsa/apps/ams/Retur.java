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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gip.fsa.MainActivity;
import com.gip.fsa.R;
import com.gip.fsa.apps.ams.Api.ApiClient;
import com.gip.fsa.apps.ams.Api.ApiInterface;
import com.gip.fsa.apps.ams.Api.JsonData;
import com.gip.fsa.apps.ams.Model.ReceiveModel;
import com.gip.fsa.apps.ams.Model.ReturOrderModel;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Retur extends AppCompatActivity {

    Retrofit retrofit;
    ApiInterface apiInterface;
    Dialog pop_loading;

    String json_response,status,message,user_id;
    LinearLayout lr_data_retur,lr_no_data_retur;

    ArrayList<ReturOrderModel> retur_arrayList = null;
    ReturAdapter retur_adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retur);

        Initialisasi();
        loadData();
        post_retur_order();

    }

    private void Initialisasi(){
        retrofit = ApiClient.retrofit();
        apiInterface = retrofit.create(ApiInterface.class);

        pop_loading = new Dialog(this);
        pop_loading.setContentView(R.layout.ams_alert_loading);
        pop_loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop_loading.setCanceledOnTouchOutside(false);

        lr_data_retur = (LinearLayout) findViewById(R.id.lrDataRetur);
        lr_no_data_retur = (LinearLayout) findViewById(R.id.lrNoDataRetur);

    }

    private void loadData(){
        Util.pref_ams = getSharedPreferences("FMS", Context.MODE_PRIVATE);
        if (Util.pref_ams.contains("user_idKey")) {
            user_id = Util.pref_ams.getString("user_idKey", "");
        }
    }

    public void btnAddRetur_click(View view) {
        SharedPreferences.Editor editor = Util.pref_ams.edit();
        editor.putString("movement_idKey", "");
        editor.putString("retur_updateKey", "0");
        editor.putString("retur_newKey", "1");
        editor.commit();
        Intent intent = new Intent(Retur.this,ReturUnit.class);
        startActivity(intent);
    }

    public void btnBack_click(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("menu_id","2");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("menu_id","2");
        startActivity(intent);
    }

    private void post_retur_order() {
        pop_loading.show();
        Call<ResponseBody> result = apiInterface.postReturOrder(ApiClient.api_key,user_id);
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
                    retur_arrayList = new ArrayList<ReturOrderModel>();
                    Integer rowcount = JsonData.get_listdata(json_response,"movement_id").size();
                    for (int i=0; i<rowcount; i++){
                        String movement_id = JsonData.get_listdata(json_response,"movement_id").get(i);
                        String order_no = JsonData.get_listdata(json_response,"order_no").get(i);
                        String retur_date = JsonData.get_listdata(json_response,"retur_date").get(i);
                        Integer qty = Integer.valueOf(JsonData.get_listdata(json_response,"qty").get(i));
                        String warehouse_id = JsonData.get_listdata(json_response,"warehouse_id").get(i);
                        String warehouse_name = JsonData.get_listdata(json_response,"warehouse_name").get(i);
                        String status = JsonData.get_listdata(json_response,"order_status").get(i);
                        retur_arrayList.add(new ReturOrderModel(movement_id,order_no,retur_date,qty,warehouse_id,warehouse_name,status));
                    }
                    if (rowcount==0){
                        lr_no_data_retur.setVisibility(View.VISIBLE);
                        lr_data_retur.setVisibility(View.GONE);
                    }else{
                        lr_no_data_retur.setVisibility(View.GONE);
                        lr_data_retur.setVisibility(View.VISIBLE);
                        display_retur(retur_arrayList);
                    }
                }
                else{
                    Toast.makeText(Retur.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(Retur.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void display_retur(final ArrayList<ReturOrderModel> returAdapters) {
        retur_adapter = new ReturAdapter(this, R.layout.ams_listview_retur,returAdapters);
        ListView listView = (ListView) findViewById(R.id.lvRetur);
        listView.setAdapter(retur_adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReturOrderModel returAdapter = (ReturOrderModel) parent.getItemAtPosition(position);
                String movement_id = returAdapter.getMovement_id();
                Util.pref_ams = getSharedPreferences("FMS", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = Util.pref_ams.edit();
                editor.putString("movement_idKey", movement_id);
                editor.putString("retur_updateKey", "0");
                editor.putString("retur_newKey", "0");
                editor.commit();
                Intent intent = new Intent(Retur.this,ReturUnit.class);
                startActivity(intent);
            }
        });
    }

    private class ReturAdapter extends ArrayAdapter<ReturOrderModel> {

        private ArrayList<ReturOrderModel> returAdapterArrayList;

        public ReturAdapter(Context context, int textViewResourceId, ArrayList<ReturOrderModel> returAdapterArrayList) {
            super(context, textViewResourceId, returAdapterArrayList);
            this.returAdapterArrayList = new ArrayList<ReturOrderModel>();
            this.returAdapterArrayList.addAll(returAdapterArrayList);
        }

        private class ViewHolder {
            TextView movement_id;
            TextView order_no;
            TextView retur_date;
            TextView qty;
            TextView warehouse_name;
            TextView status;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ReturAdapter.ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.ams_listview_retur, null);
                holder = new ReturAdapter.ViewHolder();
                holder.movement_id = (TextView) convertView.findViewById(R.id.tvMovementId);
                holder.warehouse_name = (TextView) convertView.findViewById(R.id.tvWarehouseName);
                holder.order_no = (TextView) convertView.findViewById(R.id.tvOrderNo);
                holder.retur_date = (TextView) convertView.findViewById(R.id.tvReturDate);
                holder.qty = (TextView) convertView.findViewById(R.id.tvQty);
                holder.status = (TextView) convertView.findViewById(R.id.tvReturStatus);
                convertView.setTag(holder);

            } else {
                holder = (ReturAdapter.ViewHolder) convertView.getTag();
            }

            ReturOrderModel returAdapter = returAdapterArrayList.get(position);
            holder.movement_id.setText(returAdapter.getMovement_id());
            holder.warehouse_name.setText(returAdapter.getWarehouse_name());
            holder.order_no.setText(returAdapter.getOrder_no());
            holder.retur_date.setText(returAdapter.getRetur_date());
            holder.qty.setText("Total "+String.valueOf(returAdapter.getQty())+" unit");
            holder.status.setText(returAdapter.getStatus());
            return convertView;
        }
    }

}
