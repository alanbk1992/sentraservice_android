package com.gip.fsa;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Loading extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        try {
            TextView tv_version_name = (TextView) findViewById(R.id.tvAppsVersion);
            String versionName = BuildConfig.VERSION_NAME;
            tv_version_name.setText(versionName);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        run_apps();

    }

    private void run_apps(){
        new Handler().postDelayed(() -> {
            Intent intentMain = new Intent(Loading.this,LoginActivity.class);
            startActivity(intentMain);
            finish();
        },3000);
    }
}
