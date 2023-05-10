package com.gip.fsa.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gip.fsa.LoginActivity;
import com.gip.fsa.MainActivity;
import com.gip.fsa.R;
import com.gip.fsa.apps.ams.Installation;
import com.gip.fsa.apps.ams.Intransit;
import com.gip.fsa.apps.ams.Retur;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AmsFragment extends Fragment {

    Context context;
    private View view;
    LinearLayout lr_intransit,lr_installation,lr_retur;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ams, container, false);
        initialization();
        return view;
    }

    void initialization(){
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

}
