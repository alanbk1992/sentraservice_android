package com.gip.fsa.fragment.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gip.fsa.R;
import com.gip.fsa.apps.shopee.RevisitDetailActivity;
import com.gip.fsa.apps.shopee.service.OfflineOrder;
import com.gip.fsa.apps.shopee.service.ShopeeInterface;
import com.gip.fsa.apps.shopee.service.ShopeeModel;
import com.gip.fsa.service.RetrofitService;
import com.gip.fsa.service.order.OrderModel;
import com.gip.fsa.utility.ConstantUtility;
import com.gip.fsa.utility.SignatureUtility;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfflineOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private String _guid;
    private Context context;

    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ConstantUtility constantUtility;
    private SignatureUtility signatureUtility;
    private List<OfflineOrder> orderModel;

    public OfflineOrderAdapter(List<OfflineOrder> _orderModel) {
        orderModel = _orderModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_order_item, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout._progress, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }
    }

    @Override
    public int getItemCount() {
        return orderModel.size();
    }

    @Override
    public int getItemViewType(int position) {
        return orderModel.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linLayout;
        TextView     tvDate;
        TextView     tvTicket;
        TextView     tvMerchantName;
        TextView     tvMerchantAddress;
        TextView     tvTid;
        TextView     tvMid;
        TextView     tvNote;
        TextView     tvSla;
        TextView     tvAging;
        Button       buttonSaveOffline;

        public ItemViewHolder(@NonNull View view) {
            super(view);

            sharedPreferences   = view.getContext().getSharedPreferences(constantUtility.BASE_SESSION, Context.MODE_PRIVATE);
            editor              = sharedPreferences.edit();
            _guid               = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");

            linLayout           = view.findViewById(R.id.fragment_order_item_layout);
            tvDate              = view.findViewById(R.id.fragment_order_item_date);
            tvTicket            = view.findViewById(R.id.fragment_order_item_ticket);
            tvMerchantName      = view.findViewById(R.id.fragment_order_item_merchant_name);
            tvMerchantAddress   = view.findViewById(R.id.fragment_order_item_merchant_address);
            tvTid               = view.findViewById(R.id.fragment_order_item_tid);
            tvMid               = view.findViewById(R.id.fragment_order_item_mid);
            tvNote              = view.findViewById(R.id.fragment_order_item_note);
            tvSla               = view.findViewById(R.id.fragment_order_item_sla);
            tvAging             = view.findViewById(R.id.fragment_order_item_aging);
            buttonSaveOffline   = view.findViewById(R.id.fragment_order_item_save_offline_button);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View view) {
            super(view);
            progressBar = view.findViewById(R.id._progress_bar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) { }

    private void populateItemRows(final ItemViewHolder viewHolder, final int position) {
        final String _status  = orderModel.get(position).status;
        String _date    = orderModel.get(position).orderDate;
        String _ticket  = orderModel.get(position).orderTicket;
        String _name    = orderModel.get(position).orderMerchantName;
        String _address = orderModel.get(position).orderMerchantAddress;
        String _tid     = orderModel.get(position).orderTid;
        String _mid     = orderModel.get(position).orderMid;
        String _note    = orderModel.get(position).orderNote;
        String _sla     = orderModel.get(position).orderSla;

        if (_sla.equals("")){
            _sla = "sla_date";
        }
        viewHolder.tvDate.setText(_date);
        viewHolder.tvTicket.setText(_ticket);
        viewHolder.tvMerchantName.setText(_name);
        viewHolder.tvMerchantAddress.setText(_address);
        viewHolder.tvTid.setText(_tid);
        viewHolder.tvMid.setText(_mid);
        viewHolder.tvNote.setText(_note);
        viewHolder.tvSla.setText(_sla);

        String _aging    = orderModel.get(position).restAging;
        if (_aging.equals("")){
            _aging = "Unavailable";
        }
        String aging_expired = "";
        try {
            aging_expired = _aging.substring(_aging.indexOf("-"));
        }catch(Exception ex) {

        }
        viewHolder.tvAging.setText(_aging);
        if (aging_expired.equals("")){
            viewHolder.tvSla.setTextColor(ContextCompat.getColor(context, R.color.color_red));
        }else{
            viewHolder.tvSla.setTextColor(ContextCompat.getColor(context, R.color.color_green));
        }

        viewHolder.buttonSaveOffline.setVisibility(View.GONE);

        viewHolder.linLayout.setOnClickListener(view -> {
            if (_status.matches("In Progress")) {
                Intent intent = new Intent(context, RevisitDetailActivity.class);
                intent.putExtra("id", orderModel.get(position).getId());
                intent.putExtra("_JOBS_ID", orderModel.get(position).jobsId);
                intent.putExtra("category", orderModel.get(position).orderCategory);
                intent.putExtra("date", orderModel.get(position).orderDate);
                intent.putExtra("ticket_no", orderModel.get(position).orderTicket);
                intent.putExtra("type_status", "in_progress");
                intent.putExtra("sla", orderModel.get(position).orderSla);
                intent.putExtra("note", orderModel.get(position).orderNote);
                intent.putExtra("rest_aging", orderModel.get(position).restAging);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, RevisitDetailActivity.class);
                intent.putExtra("id", orderModel.get(position).getId());
                intent.putExtra("_JOBS_ID", orderModel.get(position).jobsId);
                intent.putExtra("category", orderModel.get(position).orderCategory);
                intent.putExtra("ticket_no", orderModel.get(position).orderTicket);
                intent.putExtra("type_status", "saved_jobs");
                intent.putExtra("sla", orderModel.get(position).orderSla);
                intent.putExtra("note", orderModel.get(position).orderNote);
                intent.putExtra("rest_aging", orderModel.get(position).restAging);
                context.startActivity(intent);
            }
        });
    }
}