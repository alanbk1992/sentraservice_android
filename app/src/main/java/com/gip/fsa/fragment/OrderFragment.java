package com.gip.fsa.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gip.fsa.MainActivity;
import com.gip.fsa.R;
import com.gip.fsa.apps.shopee.service.OfflineOrder;
import com.gip.fsa.fragment.adapter.OfflineOrderAdapter;
import com.gip.fsa.fragment.adapter.OrderAdapter;
import com.gip.fsa.service.RetrofitService;
import com.gip.fsa.service.order.FilterJobModel;
import com.gip.fsa.service.order.FilterModel;
import com.gip.fsa.service.order.OrderInterface;
import com.gip.fsa.service.order.OrderModel;
import com.gip.fsa.utility.ConstantUtility;
import com.gip.fsa.utility.SignatureUtility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class OrderFragment extends Fragment {

    private boolean isLoading = false;
    private boolean isEnd     = false;
    private boolean isJT      = false;
    private boolean isJF      = false;
    private boolean isJO      = false;
    private boolean isOffline = false;

    private String _jobType = "In Progress", _filter = "Near Me", _jo = "", _keywords = "";
    private View view;
    private EditText etKeywords;
    private Spinner spinnerJobs, spinnerFilter, spinnerJO;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Handler handler;

    private ConstantUtility constantUtility;
    private SignatureUtility signatureUtility;
    public static OrderModel orderModel;
    private OrderAdapter orderAdapter;
    private OfflineOrderAdapter offlineOrderAdapter;
    List<OfflineOrder> offlineOrderList = new ArrayList<>();

    FragmentActivity fragmentActivity;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        view = layoutInflater.inflate(R.layout.fragment_order, viewGroup, false);
        _initialize();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        _grid();
        _grid_noLoading();
    }

    @Override
    public void onAttach(Activity activity) {
        fragmentActivity=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    void _initialize() {
        spinnerJobs         = view.findViewById(R.id.fragment_order_jobs);
        spinnerFilter       = view.findViewById(R.id.fragment_order_filter);
        spinnerJO           = view.findViewById(R.id.fragment_order_jo);
        etKeywords          = view.findViewById(R.id.fragment_order_keyword);
        recyclerView        = view.findViewById(R.id.fragment_order_recycle);
        swipeRefreshLayout  = view.findViewById(R.id.fragment_order_swipe);
        constantUtility     = new ConstantUtility();
        signatureUtility    = new SignatureUtility();
        sharedPreferences   = getActivity().getSharedPreferences("FMS", MODE_PRIVATE);
        editor              = sharedPreferences.edit();
        isOffline           = sharedPreferences.getBoolean("_OFFLINE_MODE", false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(constantUtility.BASE_LOADING);
        progressDialog.setCancelable(false);

        try{
            _jobs();
            _filter();
            _jo();
            _grid();
            _common();
        }
        catch(Exception ex) {
            Toast.makeText(getActivity(), "Timeout " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void _common() {
        swipeRefreshLayout.setOnRefreshListener(
                () -> {
                    isEnd = false;
                    _grid();
                    swipeRefreshLayout.setRefreshing(false);
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

                if (!isLoading) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (linearLayoutManager != null)  {
                        int last = linearLayoutManager.findLastVisibleItemPosition();
                        if (!isOffline) {
                            if (last == orderModel.getDatas().size() - 1){
                                if(isEnd == false) {
                                    _more();
                                    isLoading = true;
                                }
                            }
                        }
                    }
                }
            }
        });

        spinnerJobs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int position, long id) {
                if (isJT == true){
                    _jobType = adapterView.getItemAtPosition(position).toString();
                    _grid();
                }
                isJT = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int position, long id) {
                if (isJF == true) {
                    _filter = adapterView.getItemAtPosition(position).toString();
                    _grid();
                }
                isJF = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        spinnerJO.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int position, long id) {
                if (isJO == true) {
                    _jo = adapterView.getItemAtPosition(position).toString();
                    if (_jo.matches("All Job")) {
                        _jo = "";
                    }
                    _grid();
                }
                isJO = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        etKeywords.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                _keywords = etKeywords.getText().toString();
                _grid();
                return true;
            }
            return false;
        });
    }

    void _jobs() {
//        String[] array = {"In Progress", "Close", "Done", "Revisit"};
        String[] array = {"In Progress", "Close", "Done", "Revisit", "Saved Jobs"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String> (getActivity(), R.layout._spinner_primary,array);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobs.setAdapter(dataAdapter);
    }

    void _filter() {
        String[] array = {"Near Me"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String> (getActivity(), R.layout._spinner_primary,array);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(dataAdapter);

        String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        String _signature = signatureUtility.doSignature(_datetime, _accountId);

        OrderInterface _interface      = RetrofitService.getRetrofitService().create(OrderInterface.class);
        final Call<FilterModel> _model = _interface.filter_jobs(_accountId, _datetime, _signature);

        _model.enqueue(new Callback<FilterModel>() {
            @Override
            public void onResponse(Call<FilterModel> call, final Response<FilterModel> response) {
                if(response.body() != null) {
                    if(response.body().getSuccess().matches("false")) {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        int rowcount = response.body().getDatas().size();
                        String[] array = new String[rowcount+1];
                        array[0] = "Near Me";
                        for (int i=0; i<rowcount; i++){
                            array[i+1] = response.body().getDatas().get(i).getName();
                        }
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String> (getActivity(), R.layout._spinner_primary,array);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerFilter.setAdapter(dataAdapter);
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FilterModel> call, Throwable throwable) {
                if (! isOffline) Toast.makeText(fragmentActivity, constantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void _jo() {
        String[] array = {"All Job"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String> (getActivity(), R.layout._spinner_primary,array);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJO.setAdapter(dataAdapter);

        String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        String _signature = signatureUtility.doSignature(_datetime, _accountId);

        OrderInterface _interface      = RetrofitService.getRetrofitService().create(OrderInterface.class);
        final Call<FilterJobModel> _model = _interface.job_order(_accountId, _datetime, _signature);

        _model.enqueue(new Callback<FilterJobModel>() {
            @Override
            public void onResponse(Call<FilterJobModel> call, final Response<FilterJobModel> response) {
                if(response.body() != null) {
                    if(response.body().getSuccess().matches("false")) {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        int rowcount = response.body().getDatas().size();
                        String[] array = new String[rowcount+1];
                        array[0] = "All Job";
                        for (int i=0; i<rowcount; i++){
                            array[i+1] = response.body().getDatas().get(i).getName();
                        }
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String> (getActivity(), R.layout._spinner_primary,array);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerJO.setAdapter(dataAdapter);
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FilterJobModel> call, Throwable throwable) {
                if (! isOffline) Toast.makeText(fragmentActivity, constantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void _grid() {
        progressDialog.show();

        if (_jobType.equals("Saved Jobs")) { // if (isOffline), load all saved jobs
            progressDialog.dismiss();
            offlineOrderList = OfflineOrder.findWithQuery(OfflineOrder.class,
                    "Select * from offline_order where path_e_signature != ?", "");
            offlineOrderAdapter = new OfflineOrderAdapter(offlineOrderList);
            recyclerView.setAdapter(offlineOrderAdapter);
        } else if (isOffline) {
            progressDialog.dismiss();
            offlineOrderList = OfflineOrder.find(OfflineOrder.class,
                    "status = ? and path_e_signature = ?", _jobType, "");
            offlineOrderAdapter = new OfflineOrderAdapter(offlineOrderList);
            recyclerView.setAdapter(offlineOrderAdapter);
        } else {
            String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
            String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
            String _signature = signatureUtility.doSignature(_datetime, _accountId);

            isEnd = false;
            isLoading = false;

            OrderInterface _interface     = RetrofitService.getRetrofitService().create(OrderInterface.class);
            final Call<OrderModel> _model = _interface._goJobs(_accountId, _jobType, _filter, _jo, _keywords, "0", _datetime, _signature);

            _model.enqueue(new Callback<OrderModel>() {
                @Override
                public void onResponse(Call<OrderModel> call, final Response<OrderModel> response) {
                    progressDialog.dismiss();

                    if(response.body() != null) {
                        if(response.body().getSuccess().matches("false")) {
                            Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            orderModel   = response.body();
                            orderAdapter = new OrderAdapter(orderModel);
                            recyclerView.setAdapter(orderAdapter);

                            if (response.body().getDatas().size() < 10) {
                                isLoading = true;
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(getActivity(), ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OrderModel> call, Throwable throwable) {
                    progressDialog.dismiss();
                    Toast.makeText(fragmentActivity, constantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    void _grid_noLoading() {
        String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        String _signature = signatureUtility.doSignature(_datetime, _accountId);

        OrderInterface _interface     = RetrofitService.getRetrofitService().create(OrderInterface.class);
        final Call<OrderModel> _model = _interface._goJobs(_accountId, _jobType, _filter, _jo, _keywords,"0", _datetime, _signature);

        _model.enqueue(new Callback<OrderModel>() {
            @Override
            public void onResponse(Call<OrderModel> call, final Response<OrderModel> response) {
                progressDialog.dismiss();
                if(response.body() != null) {
                    if(response.body().getSuccess().matches("false")) {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        orderModel   = response.body();
                        orderAdapter = new OrderAdapter(orderModel);
                        recyclerView.setAdapter(orderAdapter);

                        if (response.body().getDatas().size() < 10) {
                            isLoading = true;
                        }
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderModel> call, Throwable throwable) {
                if (! isOffline) Toast.makeText(fragmentActivity, constantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void _more() {
        orderModel.getDatas().add(null);
        orderAdapter.notifyItemInserted(orderModel.getDatas().size() - 1);

        handler = new Handler();
        handler.postDelayed(() -> {
            orderModel.getDatas().remove(orderModel.getDatas().size() - 1);
            int modelSize = orderModel.getDatas().size();
            orderAdapter.notifyItemRemoved(modelSize);

            String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
            String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
            String _signature = signatureUtility.doSignature(_datetime, _accountId);

            OrderInterface _interface     = RetrofitService.getRetrofitService().create(OrderInterface.class);
            final Call<OrderModel> _model = _interface._goJobs(_accountId, _jobType, _filter, _jo, _keywords, String.valueOf(modelSize), _datetime, _signature);

            _model.enqueue(new Callback<OrderModel>() {
                @Override
                public void onResponse(Call<OrderModel> call, final Response<OrderModel> response) {
                    if(response.body() != null) {
                        for(int i = 0; i < response.body().getDatas().size(); i++) {
                            orderModel.getDatas().add(response.body().getDatas().get(i));
                        }
                        if (response.body().getDatas().size() < 10) {
                            isEnd = true;
                        }
                        orderAdapter.notifyDataSetChanged();
                        isLoading = false;
                    }
                    else
                    {
                        Toast.makeText(getActivity(), ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OrderModel> call, Throwable t) {
                    if (! isOffline) Toast.makeText(fragmentActivity, constantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
                }
            });
        }, 2000);
    }

}