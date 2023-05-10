package com.gip.fsa.apps.shopee;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gip.fsa.R;
import com.gip.fsa.apps.shopee.service.ShopeeInterface;
import com.gip.fsa.apps.shopee.service.StatusModel;
import com.gip.fsa.service.RetrofitService;
import com.gip.fsa.utility.ConstantUtility;
import com.gip.fsa.utility.SignatureUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout linProgress, linRevisit;
    private TextView tvMonth, tvWeek, tvToday;

    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ConstantUtility constantUtility;
    private SignatureUtility signatureUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopee_dashboard);
        _initialize();
    }

    protected void onResume() {
        super.onResume();
        try {
            constantUtility   = new ConstantUtility();
            sharedPreferences = getSharedPreferences("FMS", MODE_PRIVATE);
            _status();
        } catch (Exception e) {

        }
    }

    void _initialize() {
        toolbar             = findViewById(R.id.shopee_dashboard_toolbar);
        linProgress         = findViewById(R.id.shopee_dashboard_inprogress);
        linRevisit          = findViewById(R.id.shopee_dashboard_revisit);
        tvMonth             = findViewById(R.id.shopee_dashboard_month);
        tvWeek              = findViewById(R.id.shopee_dashboard_week);
        tvToday             = findViewById(R.id.shopee_dashboard_today);
        constantUtility     = new ConstantUtility();
        signatureUtility    = new SignatureUtility();
        sharedPreferences   = getSharedPreferences("FMS", MODE_PRIVATE);
        editor              = sharedPreferences.edit();

        toolbar.setTitle("Shopee");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        linProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(DashboardActivity.this, ProgressActivity.class);
                startActivity(intent);
            }
        });
        linRevisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(DashboardActivity.this, RevisitActivity.class);
                startActivity(intent);
            }
        });

        _status();
    }
    void _status() {
        String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        String _signature = signatureUtility.doSignature(_datetime, _accountId);

        ShopeeInterface _interface     = RetrofitService.getRetrofitService().create(ShopeeInterface.class);
        final Call<StatusModel> _model = _interface._goStatus(_accountId, _datetime, _signature);

        _model.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, final Response<StatusModel> response) {
                if(response.body() != null) {
                    if(response.body().getSuccess().matches("false")) {
                        Toast.makeText(DashboardActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        String _month = response.body().getDatas().get(0).getSum_Month();
                        String _week  = response.body().getDatas().get(0).getSum_Week();
                        String _today = response.body().getDatas().get(0).getSum_Today();

                        tvMonth.setText(String.valueOf(_month));
                        tvWeek.setText(String.valueOf(_week));
                        tvToday.setText(String.valueOf(_today));
                    }
                }
                else
                {
                    Toast.makeText(DashboardActivity.this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable throwable) {
                Toast.makeText(DashboardActivity.this, constantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
