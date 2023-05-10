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
import android.widget.ImageView;
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

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Installation extends AppCompatActivity {

    Retrofit retrofit;
    ApiInterface apiInterface;
    Dialog pop_loading;

    String json_response,status,message,user_id;
    LinearLayout lr_data_installation,lr_no_data_installation;

    ArrayList<ReceiveModel> installation_arrayList = null;
    InstallationAdapter installation_adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installation);

        Initialisasi();
        loadData();
        post_installation_order();

    }

    private void Initialisasi(){
        retrofit = ApiClient.retrofit();
        apiInterface = retrofit.create(ApiInterface.class);

        pop_loading = new Dialog(this);
        pop_loading.setContentView(R.layout.ams_alert_loading);
        pop_loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop_loading.setCanceledOnTouchOutside(false);

        lr_data_installation = (LinearLayout) findViewById(R.id.lrDataInstallation);
        lr_no_data_installation = (LinearLayout) findViewById(R.id.lrNoDataInstallation);

    }

    private void loadData(){
        Util.pref_ams = getSharedPreferences("FMS", Context.MODE_PRIVATE);
        if (Util.pref_ams.contains("user_idKey")) {
            user_id = Util.pref_ams.getString("user_idKey", "");
        }
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

    private void post_installation_order() {
        pop_loading.show();
        Call<ResponseBody> result = apiInterface.postInstallationOrder(ApiClient.api_key,user_id);
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
                    installation_arrayList = new ArrayList<ReceiveModel>();
                    Integer rowcount = JsonData.get_listdata(json_response,"type").size();
                    for (int i=0; i<rowcount; i++){
                        String type = JsonData.get_listdata(json_response,"type").get(i);
                        String total_qty = JsonData.get_listdata(json_response,"total_qty").get(i);
                        String total_installed = JsonData.get_listdata(json_response,"total_installed").get(i);
                        String order_status = JsonData.get_listdata(json_response,"order_status").get(i);
                        installation_arrayList.add(new ReceiveModel(type,total_qty,total_installed,order_status));
                    }
                    if (rowcount==0){
                        lr_no_data_installation.setVisibility(View.VISIBLE);
                        lr_data_installation.setVisibility(View.GONE);
                    }else{
                        lr_no_data_installation.setVisibility(View.GONE);
                        lr_data_installation.setVisibility(View.VISIBLE);
                        display_installation(installation_arrayList);
                    }
                }
                else{
                    Toast.makeText(Installation.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pop_loading.dismiss();
                Toast.makeText(Installation.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void display_installation(final ArrayList<ReceiveModel> installationAdapters) {
        installation_adapter = new InstallationAdapter(this, R.layout.ams_listview_installation,installationAdapters);
        ListView listView = (ListView) findViewById(R.id.lvInstallation);
        listView.setAdapter(installation_adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReceiveModel receiveAdapter = (ReceiveModel) parent.getItemAtPosition(position);
                String type = receiveAdapter.getType();
                Util.pref_ams = getSharedPreferences("FMS", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = Util.pref_ams.edit();
                editor.putString("typeKey", type);
                editor.putString("install_updateKey", "0");
                editor.commit();
                Intent intent = new Intent(Installation.this,InstallationUnit.class);
                startActivity(intent);
            }
        });
    }

    private class InstallationAdapter extends ArrayAdapter<ReceiveModel> {

        private ArrayList<ReceiveModel> receiveAdapterArrayList;

        public InstallationAdapter(Context context, int textViewResourceId, ArrayList<ReceiveModel> receiveAdapterArrayList) {
            super(context, textViewResourceId, receiveAdapterArrayList);
            this.receiveAdapterArrayList = new ArrayList<ReceiveModel>();
            this.receiveAdapterArrayList.addAll(receiveAdapterArrayList);
        }

        private class ViewHolder {
            ImageView img_type;
            TextView type;
            TextView total_qty;
            TextView total_installed;
            TextView order_status;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            InstallationAdapter.ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.ams_listview_installation, null);
                holder = new InstallationAdapter.ViewHolder();
                holder.img_type = (ImageView) convertView.findViewById(R.id.imgType);
                holder.type = (TextView) convertView.findViewById(R.id.tvType);
                holder.total_qty = (TextView) convertView.findViewById(R.id.tvTotalQty);
                holder.total_installed = (TextView) convertView.findViewById(R.id.tvTotalInstalled);
                holder.order_status = (TextView) convertView.findViewById(R.id.tvOrderStatus);
                convertView.setTag(holder);

            } else {
                holder = (InstallationAdapter.ViewHolder) convertView.getTag();
            }

            ReceiveModel receiveAdapter = receiveAdapterArrayList.get(position);
            String type = receiveAdapter.getType();
            holder.type.setText(type);
            if (type.equals("EDC")){
                holder.img_type.setImageResource(R.drawable.edc);
            }
            else if (type.equals("SIMCARD")){
                holder.img_type.setImageResource(R.drawable.simcard);
            }
            else if (type.equals("PERIPHERAL")){
                holder.img_type.setImageResource(R.drawable.peripheral);
            }
            else if (type.equals("THERMAL")){
                holder.img_type.setImageResource(R.drawable.thermal);
            }
            holder.total_qty.setText(receiveAdapter.getTotal_qty());
            holder.total_installed.setText(receiveAdapter.getTotal_received());
            holder.order_status.setText(receiveAdapter.getOrder_status());
            return convertView;
        }
    }

}
