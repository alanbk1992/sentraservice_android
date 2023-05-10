package com.gip.fsa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gip.fsa.service.RetrofitService;
import com.gip.fsa.service.statistic.LogoModel;
import com.gip.fsa.service.statistic.ProfilePictureModel;
import com.gip.fsa.service.statistic.StatisticInterface;
import com.gip.fsa.service.statistic.UserProfileModel;
import com.gip.fsa.utility.ConstantUtility;
import com.gip.fsa.utility.SignatureUtility;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity {

    //Insert name from drawable
    boolean PERMISSION_APPROVE_STORAGE = false;

    final File savedRawPhotoFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "FMS");
    File compressedPhotoFolder;
    File compressedPhotoFile;
    File savedRawPhotoFile;
    String fileNameCompressed;

    final File logoFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "FMS/Logos");
    final File profilePictureFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "FMS/Profile Pictures");

    SharedPreferences sharedPreferences;

    Toolbar toolbar;
    TextView txt_fullname, txt_position, txt_warehouse, txt_address, txt_email, txt_phone;
    ImageView IV_profile_logo;
    CircleImageView profilePicture;

    ArrayList<LogoModel.Datas> logoData;
    ArrayList<ProfilePictureModel.Datas> profilePictureData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        askPermission();
        initialize();
        onViewEditMode();
    }

    public void initialize() {
        profilePicture          = findViewById(R.id.profilePicture);
        IV_profile_logo         = findViewById(R.id.IV_profile_logo);
        txt_fullname            = findViewById(R.id.tvFullName);
        txt_position            = findViewById(R.id.tvPosition);
        txt_warehouse           = findViewById(R.id.tvWarehouse);
        txt_address             = findViewById(R.id.tvAddress);
        txt_email               = findViewById(R.id.tvEmail);
        txt_phone               = findViewById(R.id.tvPhone);

        logoData = new ArrayList<>();
        profilePictureData = new ArrayList<>();
        sharedPreferences = Objects.requireNonNull(getSharedPreferences("FMS", MODE_PRIVATE));

        toolbar = findViewById(R.id.toolbar_settings);
        toolbar.setTitle("Edit Profile");
        toolbar.setTitleTextColor(Color.parseColor("#292929"));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        IV_profile_logo.setOnClickListener(view -> {
            LogoListDialog logoListDialog = new LogoListDialog();
            logoListDialog.show();
        });
        profilePicture.setOnClickListener(view -> showProfilePictureDialog());

        compressedPhotoFolder = new File(getFilesDir(), "Pictures");
        fileNameCompressed = "profile_pic_" + getUserIDKey() + ".jpg";
        compressedPhotoFile = new File(compressedPhotoFolder, fileNameCompressed);

        String fileName = "profile_pic_raw.jpg";
        savedRawPhotoFile = new File(savedRawPhotoFolder, fileName);

        loadUserProfile();
        getUserProfileFromServer();
        getLogoFromServer();
        getProfilePictureFromServer();
        loadLogo();
    }

    private void getLogoFromServer() {
        String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        //String _signature = SignatureUtility.doSignature(_datetime, _accountId);
        String _signature = "ON-DEVELOPMENT";

        StatisticInterface _interface     = RetrofitService.getRetrofitService().create(StatisticInterface.class);
        final Call<LogoModel> _model = _interface._goGetLogo(_accountId, _datetime, _signature);

        _model.enqueue(new Callback<LogoModel>() {
            @Override
            public void onResponse(@NonNull Call<LogoModel> call, @NonNull final Response<LogoModel> response) {
                if(response.body() != null) {
                    if(response.body().getSuccess().matches("false")) {
                        Toast.makeText(EditProfile.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        logoData = response.body().getDatas();
                        checkLogoFiles();
                    }
                }

                else
                {
                    Toast.makeText(EditProfile.this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LogoModel> call, @NonNull Throwable throwable) {
                Toast.makeText(EditProfile.this, ConstantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProfilePictureFromServer() {
        String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        //String _signature = SignatureUtility.doSignature(_datetime, _accountId);
        String _signature = "ON-DEVELOPMENT";

        StatisticInterface _interface     = RetrofitService.getRetrofitService().create(StatisticInterface.class);
        final Call<ProfilePictureModel> _model = _interface._goGetProfilePicture(_accountId, _datetime, _signature);

        _model.enqueue(new Callback<ProfilePictureModel>() {
            @Override
            public void onResponse(@NonNull Call<ProfilePictureModel> call, @NonNull final Response<ProfilePictureModel> response) {
                if(response.body() != null) {
                    if(response.body().getSuccess().matches("false")) {
                        Toast.makeText(EditProfile.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        profilePictureData = response.body().getDatas();
                        checkProfilePictureFiles();
                    }
                }

                else
                {
                    Toast.makeText(EditProfile.this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfilePictureModel> call, @NonNull Throwable throwable) {
                Toast.makeText(EditProfile.this, ConstantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLogoFiles() {
        for (LogoModel.Datas logo : logoData) {
            File[] logoFiles = logoFolder.listFiles();
            boolean logoExist = false;

            for (int i = 0; i < Objects.requireNonNull(logoFiles).length; i++)
            {
                String fileName = logoFiles[i].getName().split("\\.")[0];

                if (logo.getName().equals(fileName)) {
                    logoExist = true;
                    break;
                }
            }

            if (!logoExist) {
                downloadFile(logo.getUrl(), logoFolder, logo.getName(), ".png");
            }
        }

        if (logoData.size() == 0) {
            Toast.makeText(EditProfile.this, ConstantUtility.ERROR_DOWNLOAD_2, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkProfilePictureFiles() {
        final String[] fileName = {"ingenico", "gss", "mms"};
        File[] profilePictureFiles = profilePictureFolder.listFiles();

        for (String s : fileName) {
            boolean profilePictureExist = false;
            assert profilePictureFiles != null;
            for (File profilePictureFile : profilePictureFiles) {
                String profilePictureFileName = profilePictureFile.getName().split("\\.")[0];

                if (profilePictureFileName.equals(s)) {
                    profilePictureExist = true;
                    break;
                }
            }

            if (!profilePictureExist) {
                switch (s) {
                    case "ingenico":
                        downloadFile(profilePictureData.get(0).getIngenicoPhoto(), profilePictureFolder, s, ".jpg");
                        break;
                    case "gss":
                        downloadFile(profilePictureData.get(0).getGssPhoto(), profilePictureFolder, s, ".jpg");
                        break;
                    case "mms":
                        downloadFile(profilePictureData.get(0).getMmsPhoto(), profilePictureFolder, s, ".jpg");
                        break;
                }
            }
        }
    }

    private void downloadFile(String url, File folderName, String fileName, String format) {
        if (!isEmptyOrBlank(url)) {
            try {
                DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(url);

                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                request.setVisibleInDownloadsUi(false);

                File file = new File(folderName, fileName + format);
                request.setDestinationUri(Uri.fromFile(file));

                Toast.makeText(this, "Downloading " + fileName + format, Toast.LENGTH_SHORT).show();
                downloadmanager.enqueue(request);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onViewEditMode() {
        boolean EDIT_MODE = getIntent().getBooleanExtra("EDIT_MODE", true);
        if (!EDIT_MODE) {
            toolbar.setVisibility(View.GONE);
            IV_profile_logo.setEnabled(false);

            String chosenLogoFileName = sharedPreferences.getString("profileLogo", "");
            File chosenLogoFile = new File(logoFolder, chosenLogoFileName);
            if (!chosenLogoFile.exists() || isEmptyOrBlank(chosenLogoFileName)) {
                IV_profile_logo.setVisibility(View.GONE);
            }
        }
    }

    public String getUserIDKey() { return getSharedPreferences("FMS", Context.MODE_PRIVATE).getString("user_idKey", ""); }

    private void getUserProfileFromServer() { //Get user's profile from server, then save it to SharedPreferences
        String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        String _signature = SignatureUtility.doSignature(_datetime, _accountId);

        StatisticInterface _interface     = RetrofitService.getRetrofitService().create(StatisticInterface.class);
        final Call<UserProfileModel> _model = _interface.user_profile(_accountId, _datetime, _signature);

        _model.enqueue(new Callback<UserProfileModel>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileModel> call, @NonNull final Response<UserProfileModel> response) {
                if(response.body() != null) {
                    if(response.body().getSuccess().matches("false")) {
                        Toast.makeText(EditProfile.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        txt_fullname.setText(String.valueOf(response.body().getDatas().get(0).getFullname()));
                        txt_position.setText(String.valueOf(response.body().getDatas().get(0).getPositionName()));
                        txt_warehouse.setText(String.valueOf(response.body().getDatas().get(0).getWarehouseName()));
                        txt_address.setText("-");
                        txt_email.setText(String.valueOf(response.body().getDatas().get(0).getEmail()));
                        txt_phone.setText(String.valueOf(response.body().getDatas().get(0).getPhone()));

                        saveUserToLocal(response.body().getDatas().get(0));
                    }
                } else {
                    Toast.makeText(EditProfile.this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileModel> call, @NonNull Throwable throwable) {
                Toast.makeText(EditProfile.this, ConstantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToLocal(UserProfileModel.Datas userProfile) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userProfile);
        editor.putString("UserProfile", json);
        editor.apply();
    }

    void loadUserProfile() {
        try {
            if (!isEmptyOrBlank(sharedPreferences.getString("UserProfile", ""))) {
                Gson gson = new Gson();
                String json = sharedPreferences.getString("UserProfile", "");
                UserProfileModel.Datas userProfile = gson.fromJson(json, UserProfileModel.Datas.class);

                txt_fullname.setText(String.valueOf(userProfile.getFullname()));
                txt_position.setText(String.valueOf(userProfile.getPositionName()));
                txt_warehouse.setText(String.valueOf(userProfile.getWarehouseName()));
                txt_address.setText("-");
                txt_email.setText(String.valueOf(userProfile.getEmail()));
                txt_phone.setText(String.valueOf(userProfile.getPhone()));
            }
        } catch (Exception e) {
            Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLogo() {
        String chosenLogoName = sharedPreferences.getString("profileLogo", "");

        if (PERMISSION_APPROVE_STORAGE) {
            if (!chosenLogoName.isEmpty()) {
                File chosenLogo = new File(logoFolder, chosenLogoName);

                if (chosenLogo.exists()) {
                    Bitmap logoBitmap = BitmapFactory.decodeFile(chosenLogo.getAbsolutePath());
                    IV_profile_logo.setImageBitmap(logoBitmap);

                    //Measure and set the right height based on the logo's height to width ratio
                    //Equation: Height = [(135 x ratio) + 62] dp -> Multiply this with the screen's density to get the height in px
                    float density = getResources().getDisplayMetrics().density;
                    float logoHeight = logoBitmap.getHeight();
                    float logoWidth = logoBitmap.getWidth();
                    float heightToWidthRatio = logoHeight/logoWidth;
                    IV_profile_logo.getLayoutParams().height = Math.round((135*heightToWidthRatio + 62)*density);

                    loadProfilePicture();
                }
            }
        }
    }

    private void loadProfilePicture() {
        String chosenLogoName = sharedPreferences.getString("profileLogo", "").split("\\.")[0];

        if (!chosenLogoName.isEmpty()) {
            File profilePictureFile = new File(profilePictureFolder, chosenLogoName + ".jpg");
            if (profilePictureFile.exists()) {
                Bitmap profilePictureBitmap = BitmapFactory.decodeFile(profilePictureFile.getAbsolutePath());
                profilePicture.setImageBitmap(profilePictureBitmap);
            }
        }
    }

    void askPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            PERMISSION_APPROVE_STORAGE = true;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

    private void showProfilePictureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ImageView IVProfilePicture = new ImageView(this);
        IVProfilePicture.setImageDrawable(profilePicture.getDrawable());
        builder.setView(IVProfilePicture);
        builder.show().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void goToAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    public boolean isEmptyOrBlank(String string) {
        return string == null || string.trim().isEmpty();
    }

    // -------------------- Custom dialog to choose which logo to display -------------------- \\

    public class LogoListDialog extends Dialog {

        public RecyclerView RV_logoList;

        public LogoListDialog() {
            super(EditProfile.this);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_choose_logo);

            getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            RV_logoList = findViewById(R.id.RV_logoList);
            ((TextView) findViewById(R.id.tvHeader_dialogLogo)).setText("Choose Logo");

            RecyclerViewLogoAdapter recyclerViewLogoAdapter = new RecyclerViewLogoAdapter();
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(EditProfile.this, 1, GridLayoutManager.VERTICAL, false);
            RV_logoList.setLayoutManager(layoutManager);
            RV_logoList.setAdapter(recyclerViewLogoAdapter);
        }

        public class RecyclerViewLogoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

            final File[] logoFiles = logoFolder.listFiles();

            private class ItemViewHolder extends RecyclerView.ViewHolder {

                ImageView IV_logo;
                View a_line_logo;

                public ItemViewHolder(@NonNull View view) {
                    super(view);
                    IV_logo   = view.findViewById(R.id.IV_logo);
                    a_line_logo = view.findViewById(R.id.a_line_logo);
                }
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_logo, parent, false);
                return new ItemViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
                assert logoFiles != null;
                Bitmap logoBitmap = BitmapFactory.decodeFile(logoFiles[position].getAbsolutePath());
                ((ItemViewHolder) viewHolder).IV_logo.setImageBitmap(logoBitmap);
                viewHolder.itemView.setOnClickListener(view -> saveAndDismiss(position));

                //Delete last item's bottom separating line
                if (position == logoFiles.length - 1) { ((ItemViewHolder) viewHolder).a_line_logo.setVisibility(View.GONE); }
                else { ((ItemViewHolder) viewHolder).a_line_logo.setVisibility(View.VISIBLE); }
            }

            @Override
            public int getItemCount() {
                assert logoFiles != null;
                return logoFiles.length;
            }

            private void saveAndDismiss(int position) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                assert logoFiles != null;
                editor.putString("profileLogo", logoFiles[position].getName());
                editor.apply();
                loadLogo();
                dismiss();
            }
        }
    }
}