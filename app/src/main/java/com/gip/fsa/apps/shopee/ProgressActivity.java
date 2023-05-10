package com.gip.fsa.apps.shopee;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gip.fsa.R;
import com.gip.fsa.apps.shopee.adapter.ProgressShopeeAdapter;
import com.gip.fsa.apps.shopee.service.ShopeeInterface;
import com.gip.fsa.apps.shopee.service.ShopeeModel;
import com.gip.fsa.service.RetrofitService;
import com.gip.fsa.utility.ConstantUtility;
import com.gip.fsa.utility.SignatureUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgressActivity extends AppCompatActivity {

    private boolean isLoading = false;
    private boolean isEnd     = false;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Handler handler;

    private ConstantUtility constantUtility;
    private SignatureUtility signatureUtility;
    private ShopeeModel shopeeModel;
    private ProgressShopeeAdapter progressShopeeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopee_progress);
        _initialize();
    }
    protected void onResume() {
        super.onResume();
        try {
            constantUtility   = new ConstantUtility();
            sharedPreferences = getSharedPreferences("FMS", MODE_PRIVATE);
            _gridNoLoading();
        } catch (Exception e) {

        }
    }

    void _initialize() {
        toolbar             = findViewById(R.id.shopee_progress_toolbar);
        recyclerView        = findViewById(R.id.shopee_progress_recycle);
        swipeRefreshLayout  = findViewById(R.id.shopee_progress_swipe);
        constantUtility     = new ConstantUtility();
        signatureUtility    = new SignatureUtility();
        sharedPreferences   = getSharedPreferences("FMS", MODE_PRIVATE);
        editor              = sharedPreferences.edit();

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        isEnd = false;
                        _gridList();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == shopeeModel.getDatas().size() - 1) {
                        if(isEnd == false) {
                            _more();
                            isLoading = true;
                        }
                    }
                }
            }
        });

        toolbar.setTitle("In Progress (Shopee)");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        _gridList();
    }
    void _gridList() {
        progressDialog = new ProgressDialog(ProgressActivity.this);
        progressDialog.setMessage(constantUtility.BASE_LOADING);
        progressDialog.setCancelable(false);
        progressDialog.show();

        String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        String _signature = signatureUtility.doSignature(_datetime, _accountId);

        ShopeeInterface _interface     = RetrofitService.getRetrofitService().create(ShopeeInterface.class);
        final Call<ShopeeModel> _model = _interface._goInProgress(_accountId, "0", _datetime, _signature);

        _model.enqueue(new Callback<ShopeeModel>() {
            @Override
            public void onResponse(Call<ShopeeModel> call, final Response<ShopeeModel> response) {
                progressDialog.dismiss();

                if(response.body() != null) {
                    if(response.body().getSuccess().matches("false")) {
                        Toast.makeText(ProgressActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        shopeeModel   = response.body();
                        progressShopeeAdapter = new ProgressShopeeAdapter(shopeeModel);
                        recyclerView.setAdapter(progressShopeeAdapter);

                        if (response.body().getDatas().size() < 10) {
                            isLoading = true;
                        }
                    }
                }
                else
                {
                    Toast.makeText(ProgressActivity.this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShopeeModel> call, Throwable throwable) {
                progressDialog.dismiss();
                Toast.makeText(ProgressActivity.this, constantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
            }
        });
    }
    void _gridNoLoading() {
        String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        String _signature = signatureUtility.doSignature(_datetime, _accountId);

        ShopeeInterface _interface     = RetrofitService.getRetrofitService().create(ShopeeInterface.class);
        final Call<ShopeeModel> _model = _interface._goInProgress(_accountId, "0", _datetime, _signature);

        _model.enqueue(new Callback<ShopeeModel>() {
            @Override
            public void onResponse(Call<ShopeeModel> call, final Response<ShopeeModel> response) {
                if(response.body() != null) {
                    if(response.body().getSuccess().matches("false")) {
                        Toast.makeText(ProgressActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        shopeeModel   = response.body();
                        progressShopeeAdapter = new ProgressShopeeAdapter(shopeeModel);
                        recyclerView.setAdapter(progressShopeeAdapter);

                        if (response.body().getDatas().size() < 10) {
                            isLoading = true;
                        }
                    }
                }
                else
                {
                    Toast.makeText(ProgressActivity.this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShopeeModel> call, Throwable throwable) {
                Toast.makeText(ProgressActivity.this, constantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
            }
        });
    }
    void _more() {
        shopeeModel.getDatas().add(null);
        progressShopeeAdapter.notifyItemInserted(shopeeModel.getDatas().size() - 1);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                shopeeModel.getDatas().remove(shopeeModel.getDatas().size() - 1);
                int modelSize = shopeeModel.getDatas().size();
                progressShopeeAdapter.notifyItemRemoved(modelSize);

                String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
                String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
                String _signature = signatureUtility.doSignature(_datetime, _accountId);

                ShopeeInterface _interface     = RetrofitService.getRetrofitService().create(ShopeeInterface.class);
                final Call<ShopeeModel> _model = _interface._goInProgress(_accountId, String.valueOf(modelSize), _datetime, _signature);

                _model.enqueue(new Callback<ShopeeModel>() {
                    @Override
                    public void onResponse(Call<ShopeeModel> call, final Response<ShopeeModel> response) {
                        if(response.body() != null) {
                            for(int i = 0; i < response.body().getDatas().size(); i++) {
                                shopeeModel.getDatas().add(response.body().getDatas().get(i));
                            }
                            if (response.body().getDatas().size() < 10) {
                                isEnd = true;
                            }
                            progressShopeeAdapter.notifyDataSetChanged();
                            isLoading = false;
                        }
                        else
                        {
                            Toast.makeText(ProgressActivity.this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ShopeeModel> call, Throwable t) {
                        Toast.makeText(ProgressActivity.this, constantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 2000);
    }

}
