package com.gip.fsa.fragment.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
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
import com.gip.fsa.service.order.OrderModel;
import com.gip.fsa.utility.ConstantUtility;
import com.gip.fsa.utility.SignatureUtility;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private String _guid;
    private boolean isOffline = false;
    private Context context;

    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ConstantUtility constantUtility;
    private SignatureUtility signatureUtility;
    private OrderModel orderModel;

    public OrderAdapter(OrderModel _orderModel) {
        orderModel  = _orderModel;
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
        return orderModel.getDatas() == null ? 0 : orderModel.getDatas().size();
    }

    @Override
    public int getItemViewType(int position) {
        return orderModel.getDatas().get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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
            isOffline           = sharedPreferences.getBoolean("_OFFLINE_MODE", false);

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
        final String _status  = orderModel.getDatas().get(position).getAssignStatus();
        String _date    = orderModel.getDatas().get(position).getCreatedDate();
        String _ticket  = orderModel.getDatas().get(position).getTicket_Number();
        String _name    = orderModel.getDatas().get(position).getMerchant_Name();
        String _address = orderModel.getDatas().get(position).getMerchant_Address();
        String _tid     = orderModel.getDatas().get(position).getTid();
        String _mid     = orderModel.getDatas().get(position).getMid();
        String _note    = orderModel.getDatas().get(position).getNote();
        String _sla    = orderModel.getDatas().get(position).getSla();
        if (_sla.equals("")){
            _sla = "sla_date";
        }
        String _aging    = orderModel.getDatas().get(position).getAging();
        if (_aging.equals("")){
            _aging = "aging_date";
        }
        String aging_expired = "";
        try {
            aging_expired = _aging.substring(_aging.indexOf("-"));
        }catch(Exception ex) {

        }
        viewHolder.tvDate.setText(_date);
        viewHolder.tvTicket.setText(_ticket);
        viewHolder.tvMerchantName.setText(_name);
        viewHolder.tvMerchantAddress.setText(_address);
        viewHolder.tvTid.setText(_tid);
        viewHolder.tvMid.setText(_mid);
        viewHolder.tvNote.setText(_note);
        viewHolder.tvSla.setText(_sla);
        viewHolder.tvAging.setText(_aging);
        if (aging_expired.equals("")){
            viewHolder.tvSla.setTextColor(ContextCompat.getColor(context, R.color.color_red));
        }else{
            viewHolder.tvSla.setTextColor(ContextCompat.getColor(context, R.color.color_green));
        }

        List<OfflineOrder> offlineOrder = OfflineOrder.findWithQuery(OfflineOrder.class, "Select * from OFFLINE_ORDER where jobs_id = ?", orderModel.getDatas().get(position).getId());

        /*
        if (offlineOrder.size() > 0) {
            disableSaveOfflineButton(viewHolder);
        } else {
            viewHolder.buttonSaveOffline.setVisibility(View.VISIBLE);
            viewHolder.buttonSaveOffline.setEnabled(true);
            viewHolder.buttonSaveOffline.setText("Save Offline");
            viewHolder.buttonSaveOffline.setBackgroundColor(Color.parseColor("#494949"));
            viewHolder.buttonSaveOffline.setTextColor(context.getResources().getColor(R.color.white));
            viewHolder.buttonSaveOffline.setOnClickListener(view -> {
                saveOffline(position);
                disableSaveOfflineButton(viewHolder);
            });
        }

         */

        viewHolder.linLayout.setOnClickListener(view -> {
            if (_status.matches("In Progress")) {
                Intent intent = new Intent(context, RevisitDetailActivity.class);
                intent.putExtra("_JOBS_ID", orderModel.getDatas().get(position).getId());
                intent.putExtra("category", orderModel.getDatas().get(position).getCategory());
                intent.putExtra("date", orderModel.getDatas().get(position).getCreatedDate());
                intent.putExtra("ticket_no", orderModel.getDatas().get(position).getTicket_Number());
                intent.putExtra("type_status", "in_progress");
                intent.putExtra("sla", orderModel.getDatas().get(position).getSla());
                intent.putExtra("note", orderModel.getDatas().get(position).getNote());
                intent.putExtra("rest_aging", orderModel.getDatas().get(position).getAging());
                context.startActivity(intent);
            } else if (_status.matches("Revisit")) {
                    Intent intent = new Intent(context, RevisitDetailActivity.class);
                    intent.putExtra("_JOBS_ID", orderModel.getDatas().get(position).getId());
                    intent.putExtra("ticket_no", orderModel.getDatas().get(position).getTicket_Number());
                    intent.putExtra("type_status", "revisit");
                    intent.putExtra("note", orderModel.getDatas().get(position).getNote());
                    intent.putExtra("rest_aging", orderModel.getDatas().get(position).getAging());
                    context.startActivity(intent);
                Toast.makeText(view.getContext(), "Status Revisit", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveOffline(final int position) {
        OrderModel.Datas model = orderModel.getDatas().get(position);
        OfflineOrder offlineOrder = new OfflineOrder(_guid, model.getId(), "", "", "", "", "", "", "", "",
                null, null, null, null, null, null, null, null, null, "", null,
                "", "", "", "", "", "", "", "", "", "", model.getAssignStatus(), "", model.getAging(), "", "",
                model.getCategory(), model.getCustomer(), model.getCreatedDate(), model.getTicket_Number(), model.getMerchant_Name(), model.getMerchant_Address(), model.getMerchant_Address_2(), model.getPostal_Code(), model.getCity(),  model.getPic_Number(), model.getSpk_Number(), model.getTid(), model.getTid_Cimb(), model.getMid(), model.getNote(), model.getSla());
        offlineOrder.save();
        Log.d("Save Offline From List",
                "accountId: " + _guid + "\n"
                        + "jobsId: " + model.getId() + "\n"
                        + model.getAssignStatus() + "\n"
                        + model.getAging() + "\n"
                        + model.getCategory() + "\n"
                        + model.getCustomer() + "\n"
                        + model.getCreatedDate() + "\n"
                        + model.getTicket_Number() + "\n"
                        + model.getMerchant_Name() + "\n"
                        + model.getMerchant_Address() + "\n"
                        + model.getMerchant_Address_2() + "\n"
                        + model.getPostal_Code() + "\n"
                        + model.getCity() + "\n"
                        + model.getPic_Number() + "\n"
                        + model.getSpk_Number() + "\n"
                        + model.getTid() + "\n"
                        + model.getTid_Cimb() + "\n"
                        + model.getMid() + "\n"
                        + model.getNote() + "\n"
                        + model.getSla());
        Toast.makeText(context, model.getId() + " saved for offline mode", Toast.LENGTH_SHORT).show();
    }

    private void disableSaveOfflineButton(ItemViewHolder viewHolder) {
        viewHolder.buttonSaveOffline.setVisibility(View.VISIBLE);
        viewHolder.buttonSaveOffline.setEnabled(false);
        viewHolder.buttonSaveOffline.setText("Already Saved");
        viewHolder.buttonSaveOffline.setTextColor(context.getResources().getColor(R.color.color_red));
        viewHolder.buttonSaveOffline.setBackgroundColor(context.getResources().getColor(R.color.white));
    }
}