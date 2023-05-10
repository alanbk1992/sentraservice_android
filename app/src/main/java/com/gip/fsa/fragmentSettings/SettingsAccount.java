package com.gip.fsa.fragmentSettings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gip.fsa.R;

public class SettingsAccount extends Fragment {

    final String[] mainSettings = {"Change password", "Logout"};

    Context context;
    SettingsAccountInterface settingsAccountInterface;

    public interface SettingsAccountInterface {
        void onItemClicked_SettingsAccount(int position);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        settingsAccountInterface = (SettingsAccountInterface) context;
    }

    public SettingsAccount() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);
    }

    private void initialize(View view) {
        ListView listView = view.findViewById(R.id.listView_settingsAccount);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, mainSettings);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view1, i, l) -> settingsAccountInterface.onItemClicked_SettingsAccount(i));
    }
}