package com.gip.fsa;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gip.fsa.apps.ams.Api.ApiClient;
import com.gip.fsa.apps.ams.Api.ApiInterface;
import com.gip.fsa.service.RetrofitService;
import com.gip.fsa.service.common.CommonInterface;
import com.gip.fsa.service.common.CommonModel;
import com.gip.fsa.utility.ConstantUtility;
import com.gip.fsa.utility.SignatureUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChangePassword extends AppCompatActivity {

    Dialog pop_alert;

    Retrofit retrofit;
    ApiInterface apiInterface;
    private SharedPreferences sharedPreferences;

    TextView txt_alert_header,txt_alert_message;
    ImageView img_alert,img_old_password,img_new_password,img_confirmation_password;
    Boolean btold_password = true;
    Boolean btnew_password = true;
    Boolean btconfirmation_password = true;
    String user_id,old_password,new_password;
    EditText et_old_password,et_new_password,et_confirmation_password;
    Button btn_submit,btn_alert_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Initialisasi();
        loadData();

    }

    private void Initialisasi(){
        retrofit = ApiClient.retrofit();
        apiInterface = retrofit.create(ApiInterface.class);

        sharedPreferences = getSharedPreferences("FMS", Context.MODE_PRIVATE);

        et_old_password = (EditText) findViewById(R.id.etOldPassword);
        et_new_password = (EditText) findViewById(R.id.etNewPassword);
        et_confirmation_password = (EditText) findViewById(R.id.etConfirmationPassword);
        img_old_password = findViewById(R.id.imgOldPassword);
        img_new_password = findViewById(R.id.imgNewPassword);
        img_confirmation_password = findViewById(R.id.imgConfirmationPassword);
        btn_submit = (Button) findViewById(R.id.btnSubmit);

        pop_alert = new Dialog(this);
        pop_alert.setContentView(R.layout.ams_popup_alert);
        pop_alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop_alert.setCanceledOnTouchOutside(false);
        img_alert = pop_alert.findViewById(R.id.imgAlert);
        txt_alert_header = pop_alert.findViewById(R.id.tvAlertHeader);
        txt_alert_message = pop_alert.findViewById(R.id.tvAlertMessage);
        btn_alert_ok = pop_alert.findViewById(R.id.btnAlertOk);
    }

    private void loadData(){
        if (sharedPreferences.contains("user_idKey")) {
            user_id = sharedPreferences.getString("user_idKey", "");
        }
        if (sharedPreferences.contains("password_Key")) {
            old_password = sharedPreferences.getString("password_Key", "");
        }
    }

    private boolean cek_validasi(){
        if (TextUtils.isEmpty(et_old_password.getText().toString())){
            Toast.makeText(this, "Enter Old Password !", Toast.LENGTH_SHORT).show();
            et_old_password.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(et_new_password.getText().toString())){
            Toast.makeText(this, "Enter New Password !", Toast.LENGTH_SHORT).show();
            et_new_password.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(et_confirmation_password.getText().toString())){
            Toast.makeText(this, "Enter Confirmation Password !", Toast.LENGTH_SHORT).show();
            et_confirmation_password.requestFocus();
            return false;
        }
        if (!et_new_password.getText().toString().equals(et_confirmation_password.getText().toString())){
            Toast.makeText(this, "Confirmation Password not match!", Toast.LENGTH_SHORT).show();
            et_confirmation_password.requestFocus();
            return false;
        }
        if (!old_password.equals(et_old_password.getText().toString())){
            Toast.makeText(this, "Old Password not match!", Toast.LENGTH_SHORT).show();
            return  false;
        }
        return true;
    }

    public void btnSubmit_click(View view) {
        if (cek_validasi()){
            old_password = et_old_password.getText().toString();
            new_password = et_new_password.getText().toString();
            post_change_password();
        }
    }

    private void post_change_password() {
        String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        String _signature = SignatureUtility.doSignature(_datetime, _accountId);

        CommonInterface _interface      = RetrofitService.getRetrofitService().create(CommonInterface.class);
        final Call<CommonModel> _model = _interface.change_password(_accountId,old_password,new_password, _datetime, _signature);

        _model.enqueue(new Callback<CommonModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<CommonModel> call, @NonNull final Response<CommonModel> response) {
                if(response.body() != null) {
                    if(response.body().getSuccess().matches("false")) {
                        Toast.makeText(ChangePassword.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        txt_alert_header.setText("Success");
                        img_alert.setImageResource(R.drawable.ic_ok);
                        txt_alert_message.setText("Change Password Complete !");
                        pop_alert.show();

                        SharedPreferences sharedPreferences = getSharedPreferences("FMS", MODE_PRIVATE);
                        sharedPreferences.edit().putString("password_Key", new_password).apply();
                    }
                }
                else
                {
                    Toast.makeText(ChangePassword.this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonModel> call, @NonNull Throwable throwable) {
                Toast.makeText(ChangePassword.this, ConstantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnClose_click(View view) {
        onBackPressed();
    }

    public void btnAlertOk_click(View view) {
        sharedPreferences.edit().remove("_SESSION_ACCOUNT_ID").remove("UserProfile").apply();
        pop_alert.dismiss();
        Intent intent = new Intent(ChangePassword.this,LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    public void btnEyeOldPassword_cick(View view) {
        btold_password = !btold_password;
        if (btold_password) {
            img_old_password.setImageResource(R.drawable.ic_visibility);
            et_old_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            et_old_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            img_old_password.setImageResource(R.drawable.ic_visibility_off);
            et_old_password.setInputType(InputType.TYPE_CLASS_TEXT);
            et_old_password.setTransformationMethod(null);
        }
    }

    public void btnEyeNewPassword_cick(View view) {
        btnew_password = !btnew_password;
        if (btnew_password) {
            img_new_password.setImageResource(R.drawable.ic_visibility);
            et_new_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            et_new_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            img_new_password.setImageResource(R.drawable.ic_visibility_off);
            et_new_password.setInputType(InputType.TYPE_CLASS_TEXT);
            et_new_password.setTransformationMethod(null);
        }
    }

    public void btnEyeConfirmationPassword_cick(View view) {
        btconfirmation_password = !btconfirmation_password;
        if (btconfirmation_password) {
            img_confirmation_password.setImageResource(R.drawable.ic_visibility);
            et_confirmation_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            et_confirmation_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            img_confirmation_password.setImageResource(R.drawable.ic_visibility_off);
            et_confirmation_password.setInputType(InputType.TYPE_CLASS_TEXT);
            et_confirmation_password.setTransformationMethod(null);
        }
    }

}
