package com.gip.fsa.apps.shopee.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gip.fsa.R;
import com.gip.fsa.apps.shopee.DetailActivity;
import com.gip.fsa.apps.shopee.service.ShopeeModel;
import com.gip.fsa.utility.ConstantUtility;
import com.gip.fsa.utility.SignatureUtility;

public class ProgressShopeeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private String _guid;
    private Context context;

    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ConstantUtility constantUtility;
    private SignatureUtility signatureUtility;
    private ShopeeModel shopeeModel;

    public ProgressShopeeAdapter(ShopeeModel _shopeeModel) {
        shopeeModel = _shopeeModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopee_progress_item, parent, false);
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
        return shopeeModel.getDatas() == null ? 0 : shopeeModel.getDatas().size();
    }

    @Override
    public int getItemViewType(int position) {
        return shopeeModel.getDatas().get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linLayout;
        TextView     tvCategory;
        TextView     tvCustomer;
        TextView     tvCity;
        TextView     tvBank;
        TextView     tvAddress;
        TextView     tvTid;
        TextView     tvMid;
        TextView     tvNote;

        public ItemViewHolder(@NonNull View view) {
            super(view);

            sharedPreferences   = view.getContext().getSharedPreferences(constantUtility.BASE_SESSION, Context.MODE_PRIVATE);
            editor              = sharedPreferences.edit();
            _guid               = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");

            linLayout   = view.findViewById(R.id.shopee_progress_item_layout);
            tvCategory  = view.findViewById(R.id.shopee_progress_item_category);
            tvCustomer  = view.findViewById(R.id.shopee_progress_item_customer);
            tvCity      = view.findViewById(R.id.shopee_progress_item_city);
            tvBank      = view.findViewById(R.id.shopee_progress_item_bank);
            tvAddress   = view.findViewById(R.id.shopee_progress_item_address);
            tvTid       = view.findViewById(R.id.shopee_progress_item_tid);
            tvMid       = view.findViewById(R.id.shopee_progress_item_mid);
            tvNote      = view.findViewById(R.id.shopee_progress_item_note);
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
        String _category  = shopeeModel.getDatas().get(position).getCategory();
        String _customer  = shopeeModel.getDatas().get(position).getCustomer();
        String _city      = shopeeModel.getDatas().get(position).getCity();
        String _bank      = shopeeModel.getDatas().get(position).getBank();
        String _name      = shopeeModel.getDatas().get(position).getMerchant_Name();
        String _address   = shopeeModel.getDatas().get(position).getMerchant_Address();
        String _tid       = shopeeModel.getDatas().get(position).getTid();
        String _mid       = shopeeModel.getDatas().get(position).getMid();
        String _note      = shopeeModel.getDatas().get(position).getNote();

        viewHolder.tvCategory.setText(_category);
        viewHolder.tvCustomer.setText(_name);
        viewHolder.tvCity.setText(_city);
        viewHolder.tvBank.setText(_bank);
        viewHolder.tvAddress.setText(_address);
        viewHolder.tvTid.setText(_tid);
        viewHolder.tvMid.setText(_mid);
        viewHolder.tvNote.setText(_note);

        viewHolder.linLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("_JOBS_ID", shopeeModel.getDatas().get(position).getId());
                context.startActivity(intent);
            }
        });
    }
}