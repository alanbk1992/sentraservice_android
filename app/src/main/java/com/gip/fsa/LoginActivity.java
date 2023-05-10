package com.gip.fsa;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gip.fsa.apps.ams.Api.ApiClient;
import com.gip.fsa.apps.ams.Api.ApiInterface;
import com.gip.fsa.apps.auth.AuthInterface;
import com.gip.fsa.apps.auth.AuthModel;
import com.gip.fsa.service.RetrofitService;
import com.gip.fsa.utility.ConstantUtility;
import com.gip.fsa.utility.MD5Utility;
import com.gip.fsa.utility.SignatureUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    Dialog pop_logout;

    private String base64 = "", _userId = "", _positionId = "";
    private EditText etUsername, etPassword;
    private Button btnSubmit;
    private static boolean PERMISSION_APPROVE = false;

    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SignatureUtility signatureUtility;
    private MD5Utility md5Utility;
    private ApiInterface apiInterface;
    private Retrofit _retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        try{
            _initialize();
        } catch (Exception e) {
            String error = e.getMessage();
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        pop_logout.show();
    }

    public void btnLogoutYa_click(View view) {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
        return;
    }

    public void btnLogoutTidak_click(View view) {
        pop_logout.hide();
    }

    void _initialize() {
        etUsername        = findViewById(R.id.login_username);
        etPassword        = findViewById(R.id.login_password);
        btnSubmit         = findViewById(R.id.login_button);
        signatureUtility  = new SignatureUtility();
        md5Utility        = new MD5Utility();
        _retrofit         = ApiClient.retrofit();
        apiInterface      = _retrofit.create(ApiInterface.class);
        sharedPreferences = getSharedPreferences("FMS", MODE_PRIVATE);
        editor            = sharedPreferences.edit();

        pop_logout = new Dialog(this);
        pop_logout.setContentView(R.layout.popup_logout);
        pop_logout.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop_logout.setCanceledOnTouchOutside(false);

        TextView tv_version_name = (TextView) findViewById(R.id.tvVersionName);
        String versionName = BuildConfig.VERSION_NAME;
        tv_version_name.setText(versionName);

        doAutoLogin();

        btnSubmit.setOnClickListener(view -> doLogin());
    }

    boolean doValidate() {
        String _username = etUsername.getText().toString();
        String _password = etPassword.getText().toString();

        if(_username.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        } else if(_password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void doAutoLogin() {
        if (!isEmptyOrBlank(sharedPreferences.getString("_SESSION_ACCOUNT_ID", ""))) {
            goToMainActivity();
        }
    }

    void doLogin() {
        boolean _validate = doValidate();
        if(_validate == true ) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(ConstantUtility.BASE_LOADING);
            progressDialog.setCancelable(false);
            progressDialog.show();

            final String _username  = etUsername.getText().toString();
            final String _password  = etPassword.getText().toString();
            String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
            String _signature = signatureUtility.doSignature(_datetime, _username);

            AuthInterface _interface     = RetrofitService.getRetrofitService().create(AuthInterface.class);
            final Call<AuthModel> _model = _interface.doLogin(_username, _password, _datetime, _signature);

            _model.enqueue(new Callback<AuthModel>() {
                @Override
                public void onResponse(Call<AuthModel> call, final Response<AuthModel> response) {
                    progressDialog.dismiss();

                    if(response.body() != null) {
                        if (response.body().getSuccess().contains("false")) {
                            Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            //doAMS(_username, _password);
                            editor.putString("_SESSION_ACCOUNT_ID",       response.body().getDatas().get(0).getId());
                            editor.putString("_SESSION_ACCOUNT_USERNAME", response.body().getDatas().get(0).getUsername());
                            _userId = response.body().getDatas().get(0).getGuid();
                            String password_key = etPassword.getText().toString();

                            editor.putString("user_idKey", _userId);
                            editor.putString("position_idKey", "tec");
                            editor.putString("password_Key", password_key);
                            editor.commit();

                            goToMainActivity();
                        }
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<AuthModel> call, @NonNull Throwable throwable) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, ConstantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void goToMainActivity() {
        finish();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public boolean isEmptyOrBlank(String string) {
        return string == null || string.trim().isEmpty();
    }
}
