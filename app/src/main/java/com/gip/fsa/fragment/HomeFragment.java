package com.gip.fsa.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gip.fsa.BuildConfig;
import com.gip.fsa.LoginActivity;
import com.gip.fsa.R;
import com.gip.fsa.apps.ams.Api.ApiClient;
import com.gip.fsa.apps.ams.Api.ApiInterface;
import com.gip.fsa.apps.ams.Api.Result;
import com.gip.fsa.apps.ams.Installation;
import com.gip.fsa.apps.ams.Intransit;
import com.gip.fsa.apps.ams.Retur;
import com.gip.fsa.service.RetrofitService;
import com.gip.fsa.service.common.CommonInterface;
import com.gip.fsa.service.common.CommonModel;
import com.gip.fsa.service.statistic.StatisticInterface;
import com.gip.fsa.service.statistic.StatusModel;
import com.gip.fsa.service.statistic.UserProfileModel;
import com.gip.fsa.utility.ConstantUtility;
import com.gip.fsa.utility.SignatureUtility;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {

    private static boolean PERMISSION_APPROVE = false;

    Retrofit retrofit;
    ApiInterface apiInterface;

    private View view;
    TextView tv_total_before,tv_process_before,tv_revisit_before
            ,tv_total_current,tv_process_current,tv_revisit_current
            ,tv_sum,tv_process_sum,tv_revisit_sum,tv_done,tv_close
            ,tv_fullname_dashboard,tv_position_dashboard
            ,tv_intransit_edc,tv_intransit_simcard,tv_intransit_peripheral,tv_intransit_thermal
            ,tv_installation_edc,tv_installation_simcard,tv_installation_peripheral,tv_installation_thermal
            ,tv_retur_edc,tv_retur_simcard,tv_retur_peripheral,tv_retur_thermal;
    String is_audit = "0";
    boolean isOffline = false;

    LinearLayout lr_job_order,lr_intransit,lr_installation,lr_retur;
    Fragment fragment;
    FragmentActivity fragmentActivity;

    private SwipeRefreshLayout swipeRefreshLayout;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ConstantUtility constantUtility;
    private SignatureUtility signatureUtility;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        view = layoutInflater.inflate(R.layout.fragment_home, viewGroup, false);
        _initialize();
        return view;
    }

    void _initialize() {
        retrofit = ApiClient.retrofit();
        apiInterface = retrofit.create(ApiInterface.class);

        tv_fullname_dashboard             = view.findViewById(R.id.tvFullNameDashboard);
        tv_position_dashboard             = view.findViewById(R.id.tvPositionDashboard);

        tv_total_before           = view.findViewById(R.id.fragment_total_before);
        tv_process_before         = view.findViewById(R.id.fragment_process_before);
        tv_revisit_before         = view.findViewById(R.id.fragment_revisit_before);

        tv_total_current          = view.findViewById(R.id.fragment_total_current);
        tv_process_current        = view.findViewById(R.id.fragment_current_process);
        tv_revisit_current        = view.findViewById(R.id.fragment_current_revisit);

        tv_sum                = view.findViewById(R.id.fragment_sum);
        tv_process_sum        = view.findViewById(R.id.fragment_sum_process);
        tv_revisit_sum        = view.findViewById(R.id.fragment_sum_revisit);
        tv_done               = view.findViewById(R.id.fragment_done);
        tv_close              = view.findViewById(R.id.fragment_close);

        tv_intransit_edc         = view.findViewById(R.id.tvIntransitEDC);
        tv_intransit_simcard     = view.findViewById(R.id.tvIntransitSimCard);
        tv_intransit_peripheral  = view.findViewById(R.id.tvIntransitPeripheral);
        tv_intransit_thermal     = view.findViewById(R.id.tvIntransitThermal);

        tv_installation_edc         = view.findViewById(R.id.tvInstallationEDC);
        tv_installation_simcard     = view.findViewById(R.id.tvInstallationSimCard);
        tv_installation_peripheral  = view.findViewById(R.id.tvInstallationPeripheral);
        tv_installation_thermal     = view.findViewById(R.id.tvInstallationThermal);

        tv_retur_edc         = view.findViewById(R.id.tvReturEDC);
        tv_retur_simcard     = view.findViewById(R.id.tvReturSimCard);
        tv_retur_peripheral  = view.findViewById(R.id.tvReturPeripheral);
        tv_retur_thermal     = view.findViewById(R.id.tvReturThermal);

        swipeRefreshLayout  = view.findViewById(R.id.fragment_home_swipe);
        constantUtility     = new ConstantUtility();
        signatureUtility    = new SignatureUtility();
        sharedPreferences   = getActivity().getSharedPreferences("FMS", MODE_PRIVATE);
        editor              = sharedPreferences.edit();
        isOffline           = sharedPreferences.getBoolean("_OFFLINE_MODE", false);

        try{
            initialization();
            _common();
            total_jo();
            ams_info();
            _version();
            _device();
            loadUserProfile();
            getUserProfileFromServer();
        }
        catch(Exception ex) {
            Toast.makeText(fragmentActivity, "Timeout " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        fragmentActivity=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    void initialization(){
        /*lr_job_order = view.findViewById(R.id.lrJobOrder);
        lr_job_order.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                fragment = new OrderFragment();
                fragmentActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_frame, fragment)
                        .commit();
            }
        });*/
        lr_intransit = view.findViewById(R.id.lrIntransit);
        lr_intransit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Intransit.class);
                startActivity(intent);
            }
        });
        lr_installation = view.findViewById(R.id.lrInstallation);
        lr_installation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Installation.class);
                startActivity(intent);
            }
        });
        lr_retur = view.findViewById(R.id.lrRetur);
        lr_retur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Retur.class);
                startActivity(intent);
            }
        });
    }

    void _common() {
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        total_jo();
                        tv_total_before.setText("0");
                        tv_process_before.setText("0");
                        tv_revisit_before.setText("0");

                        tv_total_current.setText("0");
                        tv_process_current.setText("0");
                        tv_revisit_current.setText("0");

                        tv_sum.setText("0");
                        tv_process_sum.setText("0");
                        tv_revisit_sum.setText("0");
                        tv_done.setText("0");
                        tv_close.setText("0");

                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }

    private void getUserProfileFromServer() { //Get user's profile from server, then save it to SharedPreferences
        String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        String _signature = SignatureUtility.doSignature(_datetime, _accountId);

        StatisticInterface _interface     = RetrofitService.getRetrofitService().create(StatisticInterface.class);
        final Call<UserProfileModel> _model = _interface.user_profile(_accountId, _datetime, _signature);

        _model.enqueue(new Callback<UserProfileModel>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileModel> call, @NonNull final Response<UserProfileModel> response) {
                if(response.body() != null) {
                    if(response.body().getSuccess().matches("false")) {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        String fullName = response.body().getDatas().get(0).getFullname();
                        String position = response.body().getDatas().get(0).getPositionName();
                        tv_fullname_dashboard.setText(String.valueOf(fullName));
                        tv_position_dashboard.setText(String.valueOf(position));

                        saveUserToLocal(response.body().getDatas().get(0));
                    }
                } else {
                    Toast.makeText(fragmentActivity, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileModel> call, @NonNull Throwable throwable) {
                Toast.makeText(fragmentActivity, ConstantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToLocal(UserProfileModel.Datas userProfile) {
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userProfile);
        editor.putString("UserProfile", json);
        editor.commit();
    }

    void loadUserProfile() {
        try {
            if (!isEmptyOrBlank(sharedPreferences.getString("UserProfile", ""))) {
                Gson gson = new Gson();
                String json = sharedPreferences.getString("UserProfile", "");
                UserProfileModel.Datas userProfile = gson.fromJson(json, UserProfileModel.Datas.class);

                tv_fullname_dashboard.setText(String.valueOf(userProfile.getFullname()));
                tv_position_dashboard.setText(String.valueOf(userProfile.getPositionName()));
            }
        } catch (Exception e) {
            Toast.makeText(fragmentActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void total_jo() {
        String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        String _signature = signatureUtility.doSignature(_datetime, _accountId);

        StatisticInterface _interface     = RetrofitService.getRetrofitService().create(StatisticInterface.class);
        final Call<StatusModel> _model = _interface.total_status(_accountId, _datetime, _signature);

        _model.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, final Response<StatusModel> response) {
                if(response.body() != null) {
                    if(response.body().getSuccess().matches("false")) {
                        Toast.makeText(fragmentActivity, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        String total_before = response.body().getDatas().get(0).getTotal_before();
                        String process_before  = response.body().getDatas().get(0).getProgress_before();
                        String revisit_before = response.body().getDatas().get(0).getRevisit_before();

                        String total_current = response.body().getDatas().get(0).getTotal();
                        String process_current  = response.body().getDatas().get(0).getProgress();
                        String revisit_current = response.body().getDatas().get(0).getRevisit();

                        String sum = response.body().getDatas().get(0).getSum();
                        String sum_process  = response.body().getDatas().get(0).getSum_progress();
                        String sum_revisit = response.body().getDatas().get(0).getSum_revisit();
                        String done  = response.body().getDatas().get(0).getDone();
                        String close = response.body().getDatas().get(0).getClose();

                        tv_total_before.setText(String.valueOf(total_before));
                        tv_process_before.setText(String.valueOf(process_before));
                        tv_revisit_before.setText(String.valueOf(revisit_before));

                        tv_total_current.setText(String.valueOf(total_current));
                        tv_process_current.setText(String.valueOf(process_current));
                        tv_revisit_current.setText(String.valueOf(revisit_current));

                        tv_sum.setText(String.valueOf(sum));
                        tv_process_sum.setText(String.valueOf(sum_process));
                        tv_revisit_sum.setText(String.valueOf(sum_revisit));
                        tv_done.setText(String.valueOf(done));
                        tv_close.setText(String.valueOf(close));
                    }
                }
                else
                {
                    Toast.makeText(fragmentActivity, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable throwable) {
                if (! isOffline) Toast.makeText(fragmentActivity, constantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void _version() {
        String _accountId     = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _versionCode   = String.valueOf(BuildConfig.VERSION_CODE);
        String _versionName   = String.valueOf(BuildConfig.VERSION_NAME);
        String _datetime      = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        String _signature     = signatureUtility.doSignature(_datetime, _accountId);

        CommonInterface _interface     = RetrofitService.getRetrofitService().create(CommonInterface.class);
        final Call<CommonModel> _model = _interface._doVersion(_accountId,_versionCode, _versionName, _datetime, _signature);

        _model.enqueue(new Callback<CommonModel>() {
            @Override
            public void onResponse(Call<CommonModel> call, final Response<CommonModel> response) { }
            @Override
            public void onFailure(Call<CommonModel> call, Throwable throwable) { }
        });
    }

    void _device() {
        String _accountId   = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _serial      = Build.SERIAL;
        String _models      = Build.MODEL;
        String _id          = Build.ID;
        String _manufacture = Build.MANUFACTURER;
        String _brand       = Build.BRAND;
        String _type        = Build.TYPE;
        String _user        = Build.USER;
        String _base        = String.valueOf(Build.VERSION_CODES.BASE);
        String _incremental = Build.VERSION.INCREMENTAL;
        String _sdk         = Build.VERSION.SDK;
        String _board       = Build.BOARD;
        String _host        = Build.HOST;
        String _fingerprint = Build.FINGERPRINT;
        String _version     = Build.VERSION.RELEASE;
        String _datetime    = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        String _signature   = signatureUtility.doSignature(_datetime, _accountId);

        CommonInterface _interface     = RetrofitService.getRetrofitService().create(CommonInterface.class);
        final Call<CommonModel> _model = _interface._doDevice(_accountId, _serial, _models, _id, _manufacture, _brand, _type, _user, _base, _incremental, _sdk, _board, _host, _fingerprint, _version, _datetime, _signature);

        _model.enqueue(new Callback<CommonModel>() {
            @Override
            public void onResponse(Call<CommonModel> call, final Response<CommonModel> response) { }
            @Override
            public void onFailure(Call<CommonModel> call, Throwable throwable) { }
        });
    }

    private void ams_info() {
        String user_id = sharedPreferences.getString("user_idKey", "");
        final Call<Result> result = apiInterface.postUserInfo(ApiClient.api_key,user_id);
        result.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                String status = response.body().getStatus();
                String message = response.body().getMessage();
                if (status.equals("200")){
                    String edc = response.body().getInfo().getIntransit_edc();
                    tv_intransit_edc.setText(response.body().getInfo().getIntransit_edc());
                    tv_intransit_simcard.setText(response.body().getInfo().getIntransit_simcard());
                    tv_intransit_peripheral.setText(response.body().getInfo().getIntransit_peripheral());
                    tv_intransit_thermal.setText(response.body().getInfo().getIntransit_thermal());
                    tv_installation_edc.setText(response.body().getInfo().getInstallation_edc());
                    tv_installation_simcard.setText(response.body().getInfo().getInstallation_simcard());
                    tv_installation_peripheral.setText(response.body().getInfo().getInstallation_peripheral());
                    tv_installation_thermal.setText(response.body().getInfo().getInstallation_thermal());
                    tv_retur_edc.setText(response.body().getInfo().getRetur_edc());
                    tv_retur_simcard.setText(response.body().getInfo().getRetur_simcard());
                    tv_retur_peripheral.setText(response.body().getInfo().getRetur_peripheral());
                    tv_retur_thermal.setText(response.body().getInfo().getRetur_thermal());
                }
                else{
                    //Intent intent = new Intent(Dashboard.this, Login.class);
                    //startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                if (! isOffline) Toast.makeText(fragmentActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    public boolean isEmptyOrBlank(String string) {
        return string == null || string.trim().isEmpty();
    }
}