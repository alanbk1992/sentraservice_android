package com.gip.fsa.apps.shopee;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gip.fsa.R;
import com.gip.fsa.apps.shopee.service.ShopeeInterface;
import com.gip.fsa.apps.shopee.service.ShopeeModel;
import com.gip.fsa.service.RetrofitService;
import com.gip.fsa.service.common.CommonInterface;
import com.gip.fsa.service.common.CommonModel;
import com.gip.fsa.utility.ConstantUtility;
import com.gip.fsa.utility.SignatureUtility;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements LocationListener {

    private static boolean PERMISSION_APPROVE = false;

    private String _status = "", _photoType = "", _pathMerchant = "", _pathEdc = "", _pathFormulir = "", _pathOther = "", _jobsId = "";
    private Toolbar toolbar;
    private TextView tvCategory, tvCustomer, tvReleaseDate, tvImportBank, tvBank, tvCase, tvTicket, tvSPK, tvType, tvTid, tvTidCimb, tvMid, tvMerchantName, tvMerchantAddress, tvMerchantAddress2, tvZip, tvCity, tvPicName, tvPicNumber, tvNote, tvDamage, tvInit, tvSla, tvSnEdc, tvSnSim;
    private TextView tvLatitude, tvLongitude;
    private EditText etPicName, etPicNumber, etSnEdc, etSnSim, etNote;
    private ImageView ivMerchant, ivEdc, ivFormulir, ivOthers;
    private Spinner spStatus;
    private Button btnSubmit;
    private Uri _uriMerchant, _uriEdc, _uriFormulir, _uriOthers;
    private File _fileMerchant, _fileEdc, _fileFormulir, _fileOthers;

    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ConstantUtility constantUtility;
    private SignatureUtility signatureUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopee_detail);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        _initialize();
        _permission();
    }

    @Override
    public void onLocationChanged(Location location) { }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) { }

    @Override
    public void onProviderDisabled(String s) { }

    @SuppressLint("MissingPermission")
    void _location() {
        LocationManager locationManager = (LocationManager) DetailActivity.this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, this);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null) {
            tvLatitude.setText(String.valueOf(location.getLatitude()));
            tvLongitude.setText(String.valueOf(location.getLongitude()));
        }
    }

    void _permission(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            PERMISSION_APPROVE = true;
                            _location();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(DetailActivity.this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    void _initialize() {
        toolbar             = findViewById(R.id.shopee_detail_toolbar);
        tvCategory          = findViewById(R.id.shopee_detail_category);
        tvCustomer          = findViewById(R.id.shopee_detail_customer);
        tvReleaseDate       = findViewById(R.id.shopee_detail_ticket_release);
        tvImportBank        = findViewById(R.id.shopee_detail_import_bank);
        tvBank              = findViewById(R.id.shopee_detail_bank);
        tvCase              = findViewById(R.id.shopee_detail_case);
        tvTicket            = findViewById(R.id.shopee_detail_ticket);
        tvSPK               = findViewById(R.id.shopee_detail_spk);
        tvType              = findViewById(R.id.shopee_detail_work_type);
        tvTid               = findViewById(R.id.shopee_detail_tid);
        tvTidCimb           = findViewById(R.id.shopee_detail_tid_cimb);
        tvMid               = findViewById(R.id.shopee_detail_mid);
        tvMerchantName      = findViewById(R.id.shopee_detail_merchant_name);
        tvMerchantAddress   = findViewById(R.id.shopee_detail_merchant_address);
        tvMerchantAddress2  = findViewById(R.id.shopee_detail_merchant_address_2);
        tvZip               = findViewById(R.id.shopee_detail_zip);
        tvCity              = findViewById(R.id.shopee_detail_city);
        tvPicName           = findViewById(R.id.shopee_detail_pic_name);
        tvPicNumber         = findViewById(R.id.shopee_detail_pic_number);
        tvNote              = findViewById(R.id.shopee_detail_note);
        tvDamage            = findViewById(R.id.shopee_detail_damage_type);
        tvInit              = findViewById(R.id.shopee_detail_init);
        tvSla               = findViewById(R.id.shopee_detail_sla);
        tvSnEdc             = findViewById(R.id.shopee_detail_sn_edc);
        tvSnSim             = findViewById(R.id.shopee_detail_sn_sim);

        etPicName           = findViewById(R.id.shopee_detail_input_pic_name);
        etPicNumber         = findViewById(R.id.shopee_detail_input_pic_number);
        etSnEdc             = findViewById(R.id.shopee_detail_input_sn_edc);
        etSnSim             = findViewById(R.id.shopee_detail_input_sn_sim);
        ivMerchant          = findViewById(R.id.shopee_detail_input_merchant);
        ivEdc               = findViewById(R.id.shopee_detail_input_edc);
        ivFormulir          = findViewById(R.id.shopee_detail_input_formulir);
        ivOthers            = findViewById(R.id.shopee_detail_input_other);
        etNote              = findViewById(R.id.shopee_detail_input_note);
        spStatus            = findViewById(R.id.shopee_detail_input_status);
        tvLatitude          = findViewById(R.id.shopee_detail_input_latitude);
        tvLongitude         = findViewById(R.id.shopee_detail_input_longitude);
        btnSubmit           = findViewById(R.id.shopee_detail_input_submit);

        constantUtility     = new ConstantUtility();
        signatureUtility    = new SignatureUtility();
        sharedPreferences   = getSharedPreferences("FMS", MODE_PRIVATE);
        editor              = sharedPreferences.edit();

        toolbar.setTitle("Detail");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        _loadData();
        _init();
        _status();
    }
    void _init(){
        spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int position, long id) {
                _status = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        ivMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {
                    if (PERMISSION_APPROVE == true) {
                        _photoType     = "MERCHANT";
                        Intent _intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        _intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        _fileMerchant  = new File(android.os.Environment.getExternalStorageDirectory(), Calendar.getInstance().getTimeInMillis() + ".jpg");
                        _intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(_fileMerchant));
                        _uriMerchant   = Uri.fromFile(_fileMerchant);
                        startActivityForResult(_intent, 1);
                    } else {
                        Toast.makeText(DetailActivity.this, ConstantUtility.ERROR_PERMISSION, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(DetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    _logError("DetailActivity", "_init : ivMerchant.setOnClickListener", e.toString());
                }
            }
        });
        ivEdc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {
                    if (PERMISSION_APPROVE == true) {
                        _photoType     = "EDC";
                        Intent _intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        _intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        _fileEdc       = new File(android.os.Environment.getExternalStorageDirectory(), Calendar.getInstance().getTimeInMillis() + ".jpg");
                        _intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(_fileEdc));
                        _uriEdc        = Uri.fromFile(_fileEdc);
                        startActivityForResult(_intent, 1);
                    } else {
                        Toast.makeText(DetailActivity.this, ConstantUtility.ERROR_PERMISSION, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(DetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    _logError("DetailActivity", "_init : ivEdc.setOnClickListener", e.toString());
                }
            }
        });
        ivFormulir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {
                    if (PERMISSION_APPROVE == true) {
                        _photoType     = "FORMULIR";
                        Intent _intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        _intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        _fileFormulir  = new File(android.os.Environment.getExternalStorageDirectory(), Calendar.getInstance().getTimeInMillis() + ".jpg");
                        _intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(_fileFormulir));
                        _uriFormulir   = Uri.fromFile(_fileFormulir);
                        startActivityForResult(_intent, 1);
                    } else {
                        Toast.makeText(DetailActivity.this, ConstantUtility.ERROR_PERMISSION, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(DetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    _logError("DetailActivity", "_init : ivFormulir.setOnClickListener", e.toString());
                }
            }
        });
        ivOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {
                    if (PERMISSION_APPROVE == true) {
                        _photoType     = "LAIN";
                        Intent _intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        _intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        _fileOthers    = new File(android.os.Environment.getExternalStorageDirectory(), Calendar.getInstance().getTimeInMillis() + ".jpg");
                        _intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(_fileOthers));
                        _uriOthers     = Uri.fromFile(_fileOthers);
                        startActivityForResult(_intent, 1);
                    } else {
                        Toast.makeText(DetailActivity.this, ConstantUtility.ERROR_PERMISSION, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(DetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    _logError("DetailActivity", "_init : ivOthers.setOnClickListener", e.toString());
                }
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                doSubmit();
            }
        });
    }
    void _status() {
        String[] array = {"Done", "Close", "Revisit"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String> (DetailActivity.this, R.layout._spinner,array);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(dataAdapter);
    }

    void _loadData() {
        progressDialog = new ProgressDialog(DetailActivity.this);
        progressDialog.setMessage(constantUtility.BASE_LOADING);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Intent _intent    = getIntent();
        _jobsId           = _intent.getStringExtra("_JOBS_ID");
        String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        String _signature = signatureUtility.doSignature(_datetime, _accountId);

        ShopeeInterface _interface     = RetrofitService.getRetrofitService().create(ShopeeInterface.class);
        final Call<ShopeeModel> _model = _interface._goDetail(_accountId, _jobsId, _datetime, _signature);

        _model.enqueue(new Callback<ShopeeModel>() {
            @Override
            public void onResponse(Call<ShopeeModel> call, final Response<ShopeeModel> response) {
                progressDialog.dismiss();

                if(response.body() != null) {
                    if(response.body().getSuccess().matches("false")) {
                        Toast.makeText(DetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        tvCategory.setText(response.body().getDatas().get(0).getCategory());
                        tvCustomer.setText(response.body().getDatas().get(0).getCustomer());
                        tvReleaseDate.setText(response.body().getDatas().get(0).getImport_Ticket_Receive());
                        tvImportBank.setText(response.body().getDatas().get(0).getImport_Bank());
                        tvBank.setText(response.body().getDatas().get(0).getBank());
                        tvCase.setText(response.body().getDatas().get(0).getCases());
                        tvTicket.setText(response.body().getDatas().get(0).getTicket_Number());
                        tvSPK.setText(response.body().getDatas().get(0).getSpk_Number());
                        tvType.setText(response.body().getDatas().get(0).getWork_Type());
                        tvTid.setText(response.body().getDatas().get(0).getTid());
                        tvTidCimb.setText(response.body().getDatas().get(0).getTid_Cimb());
                        tvMid.setText(response.body().getDatas().get(0).getMid());
                        tvMerchantName.setText(response.body().getDatas().get(0).getMerchant_Name());
                        tvMerchantAddress.setText(response.body().getDatas().get(0).getMerchant_Address());
                        tvMerchantAddress2.setText(response.body().getDatas().get(0).getMerchant_Address_2());
                        tvZip.setText(response.body().getDatas().get(0).getPostal_Code());
                        tvCity.setText(response.body().getDatas().get(0).getCity());
                        tvPicName.setText(response.body().getDatas().get(0).getPic_Name());
                        tvPicNumber.setText(response.body().getDatas().get(0).getPic_Number());
                        tvNote.setText(response.body().getDatas().get(0).getNote());
                        tvDamage.setText(response.body().getDatas().get(0).getDamage_Type());
                        tvInit.setText(response.body().getDatas().get(0).getInit_Code());
                        tvSla.setText(response.body().getDatas().get(0).getSla());
                        tvSnEdc.setText(response.body().getDatas().get(0).getSn_Edc());
                        tvSnSim.setText(response.body().getDatas().get(0).getSn_Sim());
                    }
                }
                else
                {
                    Toast.makeText(DetailActivity.this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShopeeModel> call, Throwable throwable) {
                progressDialog.dismiss();
                Toast.makeText(DetailActivity.this, constantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean doValidate(){
        String _name      = etNote.getText().toString();
        String _latitude  = tvLatitude.getText().toString();
        String _longitude = tvLongitude.getText().toString();

        if(_name.isEmpty()) {
            Toast.makeText(DetailActivity.this, "Catatan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        } else if(_pathMerchant.isEmpty()) {
            Toast.makeText(DetailActivity.this, "Foto bagian depan merchant tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        } else if(_pathEdc.isEmpty()) {
            Toast.makeText(DetailActivity.this, "Foto mesin EDC tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        } else if(_pathFormulir.isEmpty()) {
            Toast.makeText(DetailActivity.this, "Foto Formulir/Berita Acara tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        } else if(_pathOther.isEmpty()) {
            Toast.makeText(DetailActivity.this, "Foto Formulir/Berita Acara tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        } else if(_latitude.isEmpty()) {
            Toast.makeText(DetailActivity.this, "Latitude tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        } else if(_longitude.isEmpty()) {
            Toast.makeText(DetailActivity.this, "Longitude tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    void doSubmit() {
        boolean _validate = doValidate();
        if(_validate == true ) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(ConstantUtility.BASE_LOADING);
            progressDialog.setCancelable(false);
            progressDialog.show();

            String _accountId  = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
            File _fileMerchant = new File(_pathMerchant);
            File _fileEdc      = new File(_pathEdc);
            File _fileFormulir = new File(_pathFormulir);
            File _fileOther    = new File(_pathOther);
            String _datetime   = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
            String _signature  = signatureUtility.doSignature(_datetime, _accountId);

            RequestBody rbAccountId         = RequestBody.create(MediaType.parse("text/plain"), _accountId);
            RequestBody rbJobsId            = RequestBody.create(MediaType.parse("text/plain"), _jobsId);
            RequestBody rbPicName           = RequestBody.create(MediaType.parse("text/plain"), etPicName.getText().toString());
            RequestBody rbPicNumber         = RequestBody.create(MediaType.parse("text/plain"), etPicNumber.getText().toString());
            RequestBody rbSnEdc             = RequestBody.create(MediaType.parse("text/plain"), etSnEdc.getText().toString());
            RequestBody rbSnSim             = RequestBody.create(MediaType.parse("text/plain"), etSnSim.getText().toString());
            RequestBody rbNote              = RequestBody.create(MediaType.parse("text/plain"), etNote.getText().toString());
            RequestBody rbPMerchant         = RequestBody.create(MediaType.parse("multipart/form-data"), _fileMerchant);
            MultipartBody.Part paMerchant   = MultipartBody.Part.createFormData("photoMerchant", _fileMerchant.getName(), rbPMerchant);
            RequestBody rbPEdc              = RequestBody.create(MediaType.parse("multipart/form-data"), _fileEdc);
            MultipartBody.Part paEdc        = MultipartBody.Part.createFormData("photoEdc", _fileEdc.getName(), rbPEdc);
            RequestBody rbPFormulir         = RequestBody.create(MediaType.parse("multipart/form-data"), _fileFormulir);
            MultipartBody.Part paFormulir   = MultipartBody.Part.createFormData("photoFormulir", _fileFormulir.getName(), rbPFormulir);
            RequestBody rbPOthers           = RequestBody.create(MediaType.parse("multipart/form-data"), _fileOther);
            MultipartBody.Part paOthers     = MultipartBody.Part.createFormData("photoOthers", _fileOther.getName(), rbPOthers);
            RequestBody rbLatitude          = RequestBody.create(MediaType.parse("text/plain"), tvLatitude.getText().toString());
            RequestBody rbLongitude         = RequestBody.create(MediaType.parse("text/plain"), tvLongitude.getText().toString());
            RequestBody rbStatus            = RequestBody.create(MediaType.parse("text/plain"), _status);
            RequestBody rbDatetime          = RequestBody.create(MediaType.parse("text/plain"), _datetime);
            RequestBody rbSignature         = RequestBody.create(MediaType.parse("text/plain"), _signature);

            ShopeeInterface _interface       = RetrofitService.getRetrofitService().create(ShopeeInterface.class);
            final Call<ShopeeModel> _model = _interface._doSubmit(rbAccountId, rbJobsId, rbPicName, rbPicNumber, rbSnEdc, rbSnSim, rbNote, paMerchant, paEdc, paFormulir, paOthers, rbLatitude, rbLongitude, rbStatus, rbDatetime, rbSignature);

            _model.enqueue(new Callback<ShopeeModel>() {
                @Override
                public void onResponse(Call<ShopeeModel> call, final Response<ShopeeModel> response) {
                    progressDialog.dismiss();

                    if(response.body() != null) {
                        if (response.body().getSuccess().matches("false")) {
                            Toast.makeText(DetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    else
                    {
                        Toast.makeText(DetailActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ShopeeModel> call, Throwable throwable) {
                    progressDialog.dismiss();
                    Toast.makeText(DetailActivity.this, throwable.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK){
            try {
                if (_photoType.matches("MERCHANT")) {
                    try {
                        Bitmap bitmap = getBitmap(_uriMerchant.getPath());
                        ExifInterface ei = new ExifInterface(_fileMerchant.getPath());
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                        Bitmap rotatedBitmap = null;
                        switch(orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(bitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = bitmap;
                        }

                        _pathMerchant = _fileMerchant.getPath();
                        ivMerchant.setImageBitmap(rotatedBitmap);
                    } catch (Exception e) {
                        _logError("DetailActivity", "onActivityResult : Merchant", e.toString());
                    }
                }
                if (_photoType.matches("EDC")) {
                    try {
                        Bitmap bitmap = getBitmap(_uriEdc.getPath());
                        ExifInterface ei = new ExifInterface(_fileEdc.getPath());
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                        Bitmap rotatedBitmap = null;
                        switch(orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(bitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = bitmap;
                        }

                        _pathEdc = _fileEdc.getPath();
                        ivEdc.setImageBitmap(rotatedBitmap);
                    } catch (Exception e) {
                        _logError("DetailActivity", "onActivityResult : Edc", e.toString());
                    }
                }
                if (_photoType.matches("FORMULIR")) {
                    try {
                        Bitmap bitmap = getBitmap(_uriFormulir.getPath());
                        ExifInterface ei = new ExifInterface(_fileFormulir.getPath());
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                        Bitmap rotatedBitmap = null;
                        switch(orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(bitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = bitmap;
                        }

                        _pathFormulir = _fileFormulir.getPath();
                        ivFormulir.setImageBitmap(rotatedBitmap);
                    } catch (Exception e) {
                        _logError("DetailActivity", "onActivityResult : Formulir", e.toString());
                    }
                }
                if (_photoType.matches("LAIN")) {
                    try {
                        Bitmap bitmap = getBitmap(_uriOthers.getPath());
                        ExifInterface ei = new ExifInterface(_fileOthers.getPath());
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                        Bitmap rotatedBitmap = null;
                        switch(orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(bitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = bitmap;
                        }

                        _pathOther = _fileOthers.getPath();
                        ivOthers.setImageBitmap(rotatedBitmap);
                    } catch (Exception e) {
                        _logError("DetailActivity", "onActivityResult : Others", e.toString());
                    }
                }
            } catch (Exception e) {
                _logError("DetailActivity", "onActivityResult", e.toString());
            }
        }
    }
    void _logError(String _class, String _function, String _description) {
            String __id       = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
            String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
            String _signature = signatureUtility.doSignature(_datetime, __id);

            CommonInterface _interface     = RetrofitService.getRetrofitService().create(CommonInterface.class);
            final Call<CommonModel> _model = _interface.doError(__id, _class, _function, _description, _datetime, _signature);

            _model.enqueue(new Callback<CommonModel>() {
                @Override
                public void onResponse(Call<CommonModel> call, final Response<CommonModel> response) {}
                @Override
                public void onFailure(Call<CommonModel> call, Throwable throwable) {}
        });
    }
    private Bitmap getBitmap(String path) {
        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getContentResolver().openInputStream(uri);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }

            Bitmap b = null;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                int height = b.getHeight();
                int width = b.getWidth();

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();
            return b;
        } catch (IOException e) {
            return null;
        }
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
