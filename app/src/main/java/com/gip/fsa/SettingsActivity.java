package com.gip.fsa;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.gip.fsa.apps.shopee.service.OfflineOrder;
import com.gip.fsa.fragmentSettings.SettingsAccount;
import com.gip.fsa.fragmentSettings.SettingsMain;

public class SettingsActivity extends AppCompatActivity implements SettingsMain.SettingsMainInterface, SettingsAccount.SettingsAccountInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initialize();
    }

    private void initialize() {
        Toolbar toolbar = findViewById(R.id.shopee_revisit_detail_toolbar);

        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout_fragmentContainer, new SettingsMain())
                .commit();
    }

    void deleteSavedJobs() {
        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Delete all offline saved jobs?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> OfflineOrder.deleteAll(OfflineOrder.class))
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void goToFragment (Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout_fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("FMS", MODE_PRIVATE);
        sharedPreferences.edit().remove("_SESSION_ACCOUNT_ID").apply();
        sharedPreferences.edit().remove("UserProfile").apply();

        finishAffinity();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClicked_SettingsMain(int position) {
        switch(position) {
            case 0: //Edit Profile
                Intent intent = new Intent(this, EditProfile.class);
                startActivity(intent);
                break;
            case 1: //Account
                goToFragment(new SettingsAccount());
                break;
            case 2: //Delete saved jobs
                deleteSavedJobs();
                break;
        }
    }

    @Override
    public void onItemClicked_SettingsAccount(int position) {
        switch(position) {
            case 0:  //Change Password
                Intent intent = new Intent(this, ChangePassword.class);
                startActivity(intent);
                break;
            case 1: //Logout
                logout();
                break;
        }
    }
}