package com.gip.fsa.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gip.fsa.EditProfile;
import com.gip.fsa.R;
import com.gip.fsa.SettingsActivity;

public class ViewProfileFragment extends Fragment {

    public ViewProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.tap_to_view_virtual_id).setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), EditProfile.class);
            intent.putExtra("EDIT_MODE", false);
            startActivity(intent);
        });
    }
}