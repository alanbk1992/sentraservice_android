package com.gip.fsa.apps.shopee;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gip.fsa.R;
import com.gip.fsa.apps.ams.Api.ApiClient;
import com.gip.fsa.apps.ams.Api.ApiInterface;
import com.gip.fsa.apps.ams.Api.JsonData;
import com.gip.fsa.apps.shopee.service.CatatanKunjunganModel;
import com.gip.fsa.apps.shopee.service.OfflineOrder;
import com.gip.fsa.apps.shopee.service.ShopeeInterface;
import com.gip.fsa.apps.shopee.service.ShopeeModel;
import com.gip.fsa.service.RetrofitService;
import com.gip.fsa.service.common.CommonInterface;
import com.gip.fsa.service.common.CommonModel;
import com.gip.fsa.utility.ConstantUtility;
import com.gip.fsa.utility.SignatureUtility;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RevisitDetailActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler,LocationListener {

    private static boolean PERMISSION_APPROVE = false;

    Dialog popup_item_checkbox;
    AlertDialog photoPictureDialog;
    ListView lv_popup_checkbox;
    TextView txt_header_popup_checkbox,tv_catatan_kunjungan,tv_detail_rootcause;
    ArrayList<CatatanKunjunganModel> catatanKunjungan_arrayList = new ArrayList<>();
    ArrayList<CatatanKunjunganModel> detailRootcause_arrayList = new ArrayList<>();
    String popup_type,category;
    Button btn_ok_item;

    RelativeLayout rl_other_type_edc, rl_other_type_edc_ditarik, rl_sp_catatan_kunjungan,rl_catatan_kunjungan,rl_reschedule_date;

    private String _status = ""
            ,_pathESignature = ""
            ,_jobsId = ""
            ,_category = ""
            ,_sla = ""
            ,_restAging = ""
            ,ticket_no = ""
            ,is_audit = "0"
            ,type_status = "";
    private long _offlineId;
    private Toolbar toolbar;
    private TextView tvCategory, tvCustomer, tvReleaseDate, tvImportBank, tvBank, tvCase, tvTicket, tvSPK, tvType, tvTid, tvTidCimb, tvMid, tvMerchantName
            , tvMerchantAddress, tvMerchantAddress2, tvZip, tvCity, tvContactPerson, tvNote, tvDamage, tvInit, tvSla, tvSnEdc, tvSnSim, tvTidLama
            ,txt_flashlight_status,txt_sn,txt_scan_status,tvRescheduleDate,tv_test_transaksi,tv_foto_belakang_edc,tv_foto_edc_lain;
    private TextView tvLatitude, tvLongitude;
    private TextView tvNamaPIC, tvNomorPIC, tvSNEDC, tvICCIDSIM;
    private TextView tvFotoBagianDepanMerchant, tvFotoBagianDepanEDC, tvFotoStrukTransaksi, tvPOSM, tvFotoFKM, tvFotoBeritaAcaraMerchant, tvTTDDigital, tvFotoLain;
    private EditText etPicName, etPicNumber,
            etOtherTypeEdc, etSnEdc, etSoftwareVersion, etSamcard1, etSamcard2, etSnSim,
            etOtherTypeEdcDitarik, etSnEdcDitarik, etSnEdcRetur, etSoftwareVersionDitarik, etSamcard1Ditarik, etSamcard2Ditarik, etSnSimDitarik,
            etNote, etFakturOTS, etFakturTambahan, etScanQR, etRescheduleDate;
    private Spinner spTypeEdc, spKondisiEdc, spProviderSimcard,
            spTypeEdcDitarik, spKondisiEdcDitarik, spProviderSimcardDitarik,
            spStatus,sp_test_transaksi,sp_simcard,sp_collateral,sp_sub_root_cause,
            sp_kondisi_detail_edc_terpasang, sp_request_merchant, spinner_reason_code;
    private Button btnSubmit, btnSaveOffline;
    private RadioGroup rbgCanvassing;

    private File photoResult;
    private final File[] photoFiles = new File[10];
    private final String[] photoNames = {"Merchant", "EdcBelakang", "EdcDepan", "Struk", "POSM", "EdcBankLain", "FKM", "BeritaAcara", "BuktiKonfirmasi", "Others"};
    private ImageView[] imageViewPhotos;
    private ImageView ivMerchant, ivEdcBelakang, ivEdcDepan, ivStruk, ivPOSM, ivEdcBankLain, ivFKM, ivBeritaAcara, ivBuktiKonfirmasi, ivESignature, ivOthers;

    final File savedRawPhotoDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "FMS");
    final File savedRawPhotoFolder = new File(savedRawPhotoDirectory, "Pictures"); // Directory to store captured images

    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ConstantUtility constantUtility;
    private SignatureUtility signatureUtility;

    ApiInterface apiInterface;
    String user_id;

    Dialog pop_alert_scan;

    LinearLayout lr_data_merchant,lr_foto_merchant,lr_status_merchant,lr_lokasi_merchant,lr_data_detail,lr_scan,lr_esignature, lr_sn_edc, lr_sn_edc_ditarik, lr_sn_sim_ditarik, lr_data_lainnya
            ,lr_detail_rootcause,lr_simcard,lr_collateral,lr_foto_bukti_konfirmasi,lr_scan_qr, LL_EDCTerpasang, LL_LDCditarik;
    ImageView img_arrow_data_merchant,img_arrow_foto_merchant,img_arrow_status_merchant,img_arrow_lokasi_merchant,img_arrow_data_lainnya;
    boolean idata_merchant = true;
    boolean ifoto_merchant = true;
    boolean istatus_merchant = true;
    boolean ilokasi_merchant = true;
    boolean idata_lainnya = true;
    boolean btcatatan_kunjungan = false;

    private ZXingScannerView mScannerView;
    Switch sw_flashlight;

    GestureOverlayView gestureView;
    String path,type="";
    File file;
    public boolean gestureTouch=false;

    OfflineOrder offline;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopee_revisit_detail);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        try{
            _initialize();
            _permission();
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

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
        LocationManager locationManager = (LocationManager) RevisitDetailActivity.this.getSystemService(Context.LOCATION_SERVICE);
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
                withErrorListener(error -> Toast.makeText(RevisitDetailActivity.this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

    void _initialize() {
        toolbar             = findViewById(R.id.shopee_revisit_detail_toolbar);
        tvCategory          = findViewById(R.id.shopee_revisit_detail_category);
        tvCustomer          = findViewById(R.id.shopee_revisit_detail_customer);
        tvReleaseDate       = findViewById(R.id.shopee_revisit_detail_ticket_release);
        tvImportBank        = findViewById(R.id.shopee_revisit_detail_import_bank);
        tvBank              = findViewById(R.id.shopee_revisit_detail_bank);
        tvCase              = findViewById(R.id.shopee_revisit_detail_case);
        tvTicket            = findViewById(R.id.shopee_revisit_detail_ticket);
        tvSPK               = findViewById(R.id.shopee_revisit_detail_spk);
        tvType              = findViewById(R.id.shopee_revisit_detail_work_type);
        tvTid               = findViewById(R.id.shopee_revisit_detail_tid);
        tvTidCimb           = findViewById(R.id.shopee_revisit_detail_tid_cimb);
        tvMid               = findViewById(R.id.shopee_revisit_detail_mid);
        tvMerchantName      = findViewById(R.id.shopee_revisit_detail_merchant_name);
        tvMerchantAddress   = findViewById(R.id.shopee_revisit_detail_merchant_address);
        tvMerchantAddress2  = findViewById(R.id.shopee_revisit_detail_merchant_address_2);
        tvZip               = findViewById(R.id.shopee_revisit_detail_zip);
        tvCity              = findViewById(R.id.shopee_revisit_detail_city);
        tvContactPerson     = findViewById(R.id.shopee_revisit_detail_contact_person);
        tvNote              = findViewById(R.id.shopee_revisit_detail_note);
        tvDamage            = findViewById(R.id.shopee_revisit_detail_damage_type);
        tvInit              = findViewById(R.id.shopee_revisit_detail_init);
        tvSla               = findViewById(R.id.shopee_revisit_detail_sla);
        tvSnEdc             = findViewById(R.id.shopee_revisit_detail_sn_edc);
        tvSnSim             = findViewById(R.id.shopee_revisit_detail_sn_sim);
        tvTidLama           = findViewById(R.id.tvTIDLama);

        // Data Merchant
        etPicName           = findViewById(R.id.shopee_revisit_detail_input_pic_name);
        etPicNumber         = findViewById(R.id.shopee_revisit_detail_input_pic_number);
        spTypeEdc           = findViewById(R.id.shopee_type_edc);
        rl_other_type_edc   = findViewById(R.id.rl_other_type_edc);
        etOtherTypeEdc      = findViewById(R.id.shopee_revisit_detail_other_type_edc);
        etSnEdc             = findViewById(R.id.shopee_revisit_detail_input_sn_edc);
        etSoftwareVersion   = findViewById(R.id.shopee_revisit_detail_input_software_version);
        spKondisiEdc        = findViewById(R.id.shopee_kondisi_edc);
        etSamcard1          = findViewById(R.id.shopee_revisit_detail_input_samcard_1);
        etSamcard2          = findViewById(R.id.shopee_revisit_detail_input_samcard_2);
        spProviderSimcard   = findViewById(R.id.shopee_provide_sim_card);
        etSnSim             = findViewById(R.id.shopee_revisit_detail_input_sn_sim);
        spTypeEdcDitarik    = findViewById(R.id.shopee_type_edc_ditarik);
        rl_other_type_edc_ditarik   = findViewById(R.id.rl_other_type_edc_ditarik);
        etOtherTypeEdcDitarik       = findViewById(R.id.shopee_revisit_detail_other_type_edc_ditarik);
        etSnEdcDitarik      = findViewById(R.id.shopee_revisit_detail_input_sn_edc_ditarik);
        etSnEdcRetur        = findViewById(R.id.shopee_revisit_detail_input_sn_edc_return);
        etSoftwareVersionDitarik    = findViewById(R.id.shopee_revisit_detail_input_software_version_ditarik);
        spKondisiEdcDitarik = findViewById(R.id.shopee_kondisi_edc_ditarik);
        etSamcard1Ditarik   = findViewById(R.id.shopee_revisit_detail_input_samcard_1_ditarik);
        etSamcard2Ditarik   = findViewById(R.id.shopee_revisit_detail_input_samcard_2_ditarik);
        spProviderSimcardDitarik    = findViewById(R.id.shopee_provide_sim_card_ditarik);
        etSnSimDitarik      = findViewById(R.id.shopee_revisit_detail_input_sn_sim_ditarik);

        ivMerchant          = findViewById(R.id.shopee_revisit_detail_input_merchant);
        ivEdcBelakang       = findViewById(R.id.shopee_revisit_detail_edc_belakang);
        ivEdcDepan          = findViewById(R.id.shopee_revisit_detail_edc_depan);
        ivStruk             = findViewById(R.id.shopee_revisit_detail_struk_transaksi);
        ivPOSM              = findViewById(R.id.shopee_revisit_detail_posm);
        ivEdcBankLain       = findViewById(R.id.shopee_revisit_detail_edc_bank_lain);
        ivFKM               = findViewById(R.id.shopee_revisit_detail_fkm);
        ivBeritaAcara       = findViewById(R.id.shopee_revisit_detail_input_berita_acara);
        ivBuktiKonfirmasi   = findViewById(R.id.shopee_revisit_detail_bukti_konformasi);
        ivESignature        = findViewById(R.id.shopee_revisit_detail_input_esignature);
        ivOthers            = findViewById(R.id.shopee_revisit_detail_input_other);

        imageViewPhotos = new ImageView[] {
                ivMerchant,
                ivEdcBelakang,
                ivEdcDepan,
                ivStruk,
                ivPOSM,
                ivEdcBankLain,
                ivFKM,
                ivBeritaAcara,
                ivBuktiKonfirmasi,
                ivOthers,
        };

        etNote              = findViewById(R.id.shopee_revisit_detail_input_note);
        etFakturOTS         = findViewById(R.id.shopee_revisit_detail_faktur_ots);
        etFakturTambahan    = findViewById(R.id.shopee_revisit_detail_faktur_tambahan);
        spStatus            = findViewById(R.id.shopee_revisit_detail_input_status);
        tvRescheduleDate    = findViewById(R.id.tvRescheduleDate);
        rl_reschedule_date  = findViewById(R.id.rlRescheduleDate);
        etRescheduleDate    = findViewById(R.id.shopee_revisit_detail_input_reschedule_date);
        sp_test_transaksi   = findViewById(R.id.shopee_test_transaksi);
        tv_test_transaksi   = findViewById(R.id.tvTestTransaksi);
        tv_catatan_kunjungan    = findViewById(R.id.tvCatatanKunjungan);
        etScanQR                = findViewById(R.id.shopee_revisit_scan_qr);
        tv_detail_rootcause     = findViewById(R.id.tvDetailRootcause);
        sp_simcard              = findViewById(R.id.shopee_simcard);
        sp_collateral           = findViewById(R.id.shopee_collateral);
        sp_sub_root_cause       = findViewById(R.id.sp_sub_root_cause);
        tvLatitude              = findViewById(R.id.shopee_revisit_detail_input_latitude);
        tvLongitude             = findViewById(R.id.shopee_revisit_detail_input_longitude);
        btnSubmit               = findViewById(R.id.shopee_revisit_detail_input_submit);
        btnSaveOffline          = findViewById(R.id.shopee_revisit_detail_input_offline_submit);
        rl_sp_catatan_kunjungan = findViewById(R.id.rlspCatatanKunjungan);
        rl_catatan_kunjungan    = findViewById(R.id.rlCatatanKunjungan);

        lr_data_detail              = findViewById(R.id.lrDataDetail);
        lr_data_merchant            = findViewById(R.id.lrDataMerchant);
        img_arrow_data_merchant     = findViewById(R.id.imgArrowDataMerchant);
        lr_foto_merchant            = findViewById(R.id.lrFotoMerchant);
        img_arrow_foto_merchant     = findViewById(R.id.imgArrowFotoMerchant);
        lr_status_merchant          = findViewById(R.id.lrStatusMerchant);
        img_arrow_status_merchant   = findViewById(R.id.imgArrowStatusMerchant);
        lr_lokasi_merchant          = findViewById(R.id.lrLokasiMerchant);
        img_arrow_lokasi_merchant   = findViewById(R.id.imgArrowLokasiMerchant);
        lr_data_lainnya             = findViewById(R.id.lrDataLainnya);
        img_arrow_data_lainnya      = findViewById(R.id.imgArrowDataLainnya);
        lr_scan                     = findViewById(R.id.lrScan);
        mScannerView                = (ZXingScannerView)findViewById(R.id.scanner);
        sw_flashlight               = findViewById(R.id.switchFlashlight);
        txt_flashlight_status       = findViewById(R.id.tvFlashlightStatus);
        lr_esignature               = findViewById(R.id.lrESignature);
        lr_sn_edc                   = findViewById(R.id.lrSN_EDC);
        lr_sn_edc_ditarik           = findViewById(R.id.lrSN_EDC_Ditarik);
        lr_sn_sim_ditarik           = findViewById(R.id.lrSN_SIM_Ditarik);
        lr_scan_qr                  = findViewById(R.id.lrScanQR);
        lr_detail_rootcause         = findViewById(R.id.lrDetailRootcause);
        lr_simcard                  = findViewById(R.id.lrSimcard);
        lr_collateral               = findViewById(R.id.lrCollateral);
        lr_foto_bukti_konfirmasi    = findViewById(R.id.lrFotoBuktiKonfirmasi);
        tv_foto_belakang_edc        = findViewById(R.id.tvFotoBagianBelakangEDC);
        tv_foto_edc_lain            = findViewById(R.id.tvFotoEDCBankLain);

        LL_EDCTerpasang             = findViewById(R.id.LL_EDCTerpasang);
        LL_LDCditarik               = findViewById(R.id.LL_LDCditarik);

        tvNamaPIC                   = findViewById(R.id.tvNamaPIC);
        tvNomorPIC                  = findViewById(R.id.tvNomorPIC);
        tvSNEDC                     = findViewById(R.id.tvSNEDC);
        tvICCIDSIM                  = findViewById(R.id.tvICCIDSIM);

        tvFotoBagianDepanMerchant   = findViewById(R.id.tvFotoBagianDepanMerchant);
        tvFotoBagianDepanEDC        = findViewById(R.id.tvFotoBagianDepanEDC);
        tvFotoStrukTransaksi        = findViewById(R.id.tvFotoStrukTransaksi);
        tvPOSM                      = findViewById(R.id.tvPOSM);
        tvFotoFKM                   = findViewById(R.id.tvFotoFKM);
        tvFotoBeritaAcaraMerchant   = findViewById(R.id.tvFotoBeritaAcaraMerchant);
        tvTTDDigital                = findViewById(R.id.tvTTDDigital);
        tvFotoLain                  = findViewById(R.id.tvFotoLain);

        ticket_no = getIntent().getStringExtra("ticket_no");

        sp_kondisi_detail_edc_terpasang = findViewById(R.id.sp_kondisi_detail_edc_terpasang);
        sp_request_merchant = findViewById(R.id.sp_request_merchant);
        spinner_reason_code = findViewById(R.id.sp_reason_code);

        pop_alert_scan              = new Dialog(this);
        pop_alert_scan.setContentView(R.layout.ams_popup_scan_unit);
        pop_alert_scan.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        txt_sn                      = pop_alert_scan.findViewById(R.id.tvScanSN);
        txt_scan_status             = pop_alert_scan.findViewById(R.id.tvScanStatus);

        popup_item_checkbox         = new Dialog(this);
        popup_item_checkbox.setContentView(R.layout.popup_listview_checkbox);
        popup_item_checkbox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup_item_checkbox.setCanceledOnTouchOutside(false);
        lv_popup_checkbox           = popup_item_checkbox.findViewById(R.id.lvPopup);
        lv_popup_checkbox.setTextFilterEnabled(true);
        txt_header_popup_checkbox   = popup_item_checkbox.findViewById(R.id.tvHeaderPopup);
        btn_ok_item                 = popup_item_checkbox.findViewById(R.id.btnOkItem);

        rbgCanvassing               = findViewById(R.id.rbgCanvassing);

        constantUtility     = new ConstantUtility();
        signatureUtility    = new SignatureUtility();
        sharedPreferences   = getSharedPreferences("FMS", MODE_PRIVATE);
        editor              = sharedPreferences.edit();

        savedRawPhotoFolder.mkdirs();

        apiInterface = ApiClient.retrofit().create(ApiInterface.class);
        user_id = sharedPreferences.getString("user_idKey", "");

        toolbar.setTitle("Detail");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent _intent          = getIntent();
        _category               = _intent.getStringExtra("category");
        Log.d("RevisitDetailActivity", "category: " + _category);
        _init();
        _typeEdc(); _kondisiEdc(); _providerSimcard();
        _status();
        changeFieldsBasedOnJobType();

        type_status = getIntent().getStringExtra("type_status");
        switch (type_status) { // search from local DB if job ID exist, if yes load all existing data
            case "in_progress":
                _loadData_progress();
                break;
            case "revisit":
                _loadData_revisit();
                break;
            case "saved_jobs":
                _loadData_savedJobs();
                break;
        }

        changeFieldsBasedOnCustomer();
    }

    private void changeFieldsBasedOnCustomer() {
        // Canvassing project BCA SP
        rbgCanvassing.setOnCheckedChangeListener((radioGroup, i) -> {
            if (is_BCA_SP_Canvassing()) {changeFields_BCA_SP();}
        });
        rbgCanvassing.check(R.id.rbBCAMaintenance);

        if (getCustomerCode().equals("CIMB")) {changeFields_CIMB();}
    }

    private String getCustomerCode() {
        return ticket_no.split("_")[0];
    }

    private boolean is_BCA_SP_Canvassing() {
        String catatanKunjungan = getIntent().getStringExtra("note").toLowerCase();
        return (getCustomerCode().equals("BCA") && _category.equals("SP") && catatanKunjungan.contains("canvassing"));
    }

    private void changeFieldsBasedOnJobType() {
        if (_category.equals("WD")) {
            LL_LDCditarik.setVisibility(View.VISIBLE);
            LL_EDCTerpasang.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void changeFields_BCA_SP() {
        //Default: BCA - Maintenance
        catatan_kunjungan("", "");// Status and Tes Transaksi not required
        findViewById(R.id.LLCanvassing).setVisibility(View.VISIBLE);

        findViewById(R.id.tvTitleEDCTerpasang).setVisibility(View.GONE);
        findViewById(R.id.tvTypeEdc).setVisibility(View.GONE);
        findViewById(R.id.RLTypeEDC).setVisibility(View.GONE);
        findViewById(R.id.tvVersiSoftware).setVisibility(View.GONE);
        findViewById(R.id.RLVersiSoftware).setVisibility(View.GONE);
        findViewById(R.id.tvKondisiEdc).setVisibility(View.GONE);
        findViewById(R.id.RLKondisiEdc).setVisibility(View.GONE);
        findViewById(R.id.tvSimcard1).setVisibility(View.GONE);
        findViewById(R.id.RLSimcard1).setVisibility(View.GONE);
        findViewById(R.id.tvSimcard2).setVisibility(View.GONE);
        findViewById(R.id.RLSimcard2).setVisibility(View.GONE);
        findViewById(R.id.tvProviderSimCard).setVisibility(View.GONE);
        findViewById(R.id.RLProviderSimCard).setVisibility(View.GONE);

        findViewById(R.id.lrSimcard).setVisibility(View.GONE);
        findViewById(R.id.lrCollateral).setVisibility(View.GONE);

        tvFotoFKM.setVisibility(View.GONE);
        tvFotoBeritaAcaraMerchant.setVisibility(View.GONE);
        tvFotoLain.setVisibility(View.GONE);
        tvFotoBagianDepanEDC.setVisibility(View.GONE);
        ivFKM.setVisibility(View.GONE);
        ivBeritaAcara.setVisibility(View.GONE);
        ivOthers.setVisibility(View.GONE);
        ivEdcDepan.setVisibility(View.GONE);

        tvNamaPIC.setText("PIC Merchant / Jabatan PIC");
        tvNomorPIC.setText("Terminal ID");tvNomorPIC.setVisibility(View.VISIBLE);
        findViewById(R.id.rlNomorPIC).setVisibility(View.VISIBLE);
        tvSNEDC.setText("Merchant ID");tvSNEDC.setVisibility(View.VISIBLE);
        findViewById(R.id.rlSNEDC).setVisibility(View.VISIBLE);
        tvICCIDSIM.setText("Nama Merchant");

        tvFotoBagianDepanMerchant.setText("Foto Merchant");
        tv_foto_belakang_edc.setText("Foto EDC");
        tvFotoStrukTransaksi.setText("Foto Struk");
        tv_foto_edc_lain.setText("Foto EDC Bank Lain"); //Same name as default
        tvTTDDigital.setText("Teknisi TTD");

        tv_foto_belakang_edc.setVisibility(View.VISIBLE);
        tvFotoStrukTransaksi.setVisibility(View.VISIBLE);
        tvPOSM.setVisibility(View.VISIBLE);
        ivEdcBelakang.setVisibility(View.VISIBLE);
        ivStruk.setVisibility(View.VISIBLE);
        ivPOSM.setVisibility(View.VISIBLE);

        findViewById(R.id.rlDataLain).setVisibility(View.VISIBLE);
        lr_data_lainnya.setVisibility(View.VISIBLE);

        spStatus.setSelection(0);
        spStatus.setEnabled(false);
        tv_test_transaksi.setVisibility(View.VISIBLE);
        findViewById(R.id.rlTestTransaksi).setVisibility(View.VISIBLE);
        lr_detail_rootcause.setVisibility(View.GONE);

        // NON BCA - AKUISISI
        if (rbgCanvassing.getCheckedRadioButtonId() == R.id.rbBCAAkuisisi) {
            tvNomorPIC.setVisibility(View.GONE);
            findViewById(R.id.rlNomorPIC).setVisibility(View.GONE);
            tvSNEDC.setVisibility(View.GONE);
            findViewById(R.id.rlSNEDC).setVisibility(View.GONE);

            tv_foto_belakang_edc.setVisibility(View.GONE);
            tvFotoStrukTransaksi.setVisibility(View.GONE);
            tvPOSM.setVisibility(View.GONE);
            ivEdcBelakang.setVisibility(View.GONE);
            ivStruk.setVisibility(View.GONE);
            ivPOSM.setVisibility(View.GONE);

            findViewById(R.id.rlDataLain).setVisibility(View.GONE);
            lr_data_lainnya.setVisibility(View.GONE);

            tv_test_transaksi.setVisibility(View.GONE);
            findViewById(R.id.rlTestTransaksi).setVisibility(View.GONE);
        }

        readjustHeight(lr_data_merchant);
    }

    private void changeFields_CIMB() {
        findViewById(R.id.ll_kondisi_detail_edc_terpasang).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_kelengkapan_edc_terpasang).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_no_simcard_terpasang).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_request_merchant).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_reason_code).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_total_kasir).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_nama_kasir).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_remark_sosialisasi_edc).setVisibility(View.VISIBLE);
        //findViewById(R.id.ll_materi_training).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_remark_edc).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_mid_tid).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_sub_root_cause).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_hasil_call_tms).setVisibility(View.VISIBLE);

        ((RadioButton) findViewById(R.id.rbTotalKasir1)).setChecked(true);

        kondisiDetailEdc();
        requestMerchant();
        reasonCode();
    }

    void _init(){
        try {
            is_audit = sharedPreferences.getString("is_auditKey", "");
        }
        catch(Exception ignored) {}

//        if (is_audit.equals("1")){
//            lr_sn_edc.setVisibility(View.VISIBLE);
//            height = 300;
//        }else{
//            lr_sn_edc.setVisibility(View.GONE);
//            height = 240;
//        }
        img_arrow_data_merchant.setImageResource(R.drawable.ic_down_white);

        final DatePickerDialog.OnDateSetListener dpRescheduleDate = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            myCalendar.set(Calendar.HOUR, 0);
            myCalendar.set(Calendar.MINUTE, 0);
            myCalendar.set(Calendar.SECOND, 0);
            updateRescheduleDateLabel();
        };

        spTypeEdc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int position, long id) {
                if (spTypeEdc.getSelectedItem().toString().equals("Lainnya")) {
                    rl_other_type_edc.setVisibility(View.VISIBLE);
                    etOtherTypeEdc.setEnabled(true);
                } else {
                    rl_other_type_edc.setVisibility(View.GONE);
                    etOtherTypeEdc.setText("");
                    etOtherTypeEdc.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        spTypeEdcDitarik.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int position, long id) {
                if (spTypeEdcDitarik.getSelectedItem().toString().equals("Lainnya")) {
                    rl_other_type_edc_ditarik.setVisibility(View.VISIBLE);
                    etOtherTypeEdcDitarik.setEnabled(true);
                } else {
                    rl_other_type_edc_ditarik.setVisibility(View.GONE);
                    etOtherTypeEdcDitarik.setText("");
                    etOtherTypeEdcDitarik.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int position, long id) {
                _status = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        setPhotoOnClickListener();

        ivESignature.setOnClickListener(view -> {
            try {
                if (PERMISSION_APPROVE) {
                    init_esignature();
                    lr_data_detail.setVisibility(View.GONE);
                    lr_esignature.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(RevisitDetailActivity.this, ConstantUtility.ERROR_PERMISSION, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(RevisitDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                _logError("RevisitDetailActivity", "_init : ivESignature.setOnClickListener", e.toString());
            }
        });

        spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try{
                    test_transaksi(spStatus.getSelectedItem().toString());
                    reschedule_date(spStatus.getSelectedItem().toString());
                    catatan_kunjungan(spStatus.getSelectedItem().toString(),sp_test_transaksi.getSelectedItem().toString());
                    detail_rootcause();
                    simcard();
                    collateral();
                    scan_QR();
                    foto_bukti_konfirmasi(spStatus.getSelectedItem().toString());
                    cek_note();
                    changeEdcInfoField();
                }catch (Exception ex){
                    //Toast.makeText(RevisitDetailActivity.this, "On Status : " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        etRescheduleDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RevisitDetailActivity.this, dpRescheduleDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        sp_test_transaksi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try{
                    catatan_kunjungan(spStatus.getSelectedItem().toString(),sp_test_transaksi.getSelectedItem().toString());
                    detail_rootcause();
                    simcard();
                    collateral();
                    scan_QR();
                    cek_note();
                }catch (Exception ex){
                    //Toast.makeText(RevisitDetailActivity.this, "On Test Transaksi : " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        sp_simcard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        btnSaveOffline.setOnClickListener(view -> {
            try{
                doSaveOffline();
            } catch (Exception e) {
                String error_message = e.getMessage();
                Toast.makeText(RevisitDetailActivity.this, error_message, Toast.LENGTH_LONG).show();
            }
        });

        btnSubmit.setOnClickListener(view -> {
            try{
            boolean isValid = is_BCA_SP_Canvassing() ? doValidate_BCA_SP() : doValidate();
            //boolean isValid = true;

            if (isValid) {
                doSubmit();

                if (getUnitStatus().contains("Installed")) { getUnitData(); }
                if (getUnitStatus().contains("Retur")) { createReturOrder(); }

            }

            } catch (Exception e) {
                Toast.makeText(RevisitDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chooseCameraPictureDialog(ActivityResultLauncher<Intent> activityResultLauncher, ActivityResultLauncher<Intent> activityResultLauncherPick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ListView listView = new ListView(this);
        final String[] list = {"Take a photo", "Choose from gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (i == 0) {
                launchPhotoIntent(activityResultLauncher);
            } else if (i == 1) {
                launchPickPhotoIntent(activityResultLauncherPick);
            }
            photoPictureDialog.dismiss();
        });

        builder.setView(listView);
        photoPictureDialog = builder.show();
    }

    void init_esignature(){
        type = "esign";
        Button donebutton = (Button) findViewById(R.id.DoneButton);
        Button clearButton = (Button) findViewById(R.id.ClearButton);

        path= Environment.getExternalStorageDirectory()+"/signature.png";
        file = new File(path);
        file.delete();
        gestureView = (GestureOverlayView) findViewById(R.id.signaturePad);
        gestureView.setDrawingCacheEnabled(true);

        gestureView.setAlwaysDrawnWithCacheEnabled(true);
        gestureView.setHapticFeedbackEnabled(false);
        gestureView.cancelLongPress();
        gestureView.cancelClearAnimation();
        gestureView.addOnGestureListener(new GestureOverlayView.OnGestureListener() {

            @Override
            public void onGesture(GestureOverlayView arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureCancelled(GestureOverlayView arg0,
                                           MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureEnded(GestureOverlayView arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureStarted(GestureOverlayView arg0,
                                         MotionEvent arg1) {
                // TODO Auto-generated method stub
                if (arg1.getAction()==MotionEvent.ACTION_MOVE){
                    gestureTouch=false;
                }
                else
                {
                    gestureTouch=true;
                }
            }});

        donebutton.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            try {
                gestureView.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(gestureView.getDrawingCache());
                ivESignature.setImageBitmap(addWatermark(bitmap));
                gestureView.setDrawingCacheEnabled(false);
                saveImage(ivESignature.getDrawable(),ticket_no + "_TTD");
                _pathESignature = savedRawPhotoFolder.getAbsolutePath() + "/" + ticket_no + "_TTD.jpg";
                type = "";
                lr_data_detail.setVisibility(View.VISIBLE);
                lr_esignature.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(!gestureTouch)
            {
                setResult(0);
                //finish();
            }
            else
            {
                setResult(1);
                //finish();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                gestureView.invalidate();
                gestureView.clear(true);
                gestureView.clearAnimation();
                gestureView.cancelClearAnimation();
            }
        });
    }

    void _typeEdc() {
        String[] defaultList = {"Ingenico Apos A8", "Ingenico ICT 250", "Ingenico IWL 220", "Ingenico Move 2500", "Ingenico Move 3500", "Verifone VX675", "Lainnya"};
        String[] CIMBList = {"Verifone VX520C", "Verifone VX520 Fixed Line", "Verifone VX520 3G", "Verifone C680 3G", "Verifone VX675 3G CTLS", "Verifone VX675 WIFI", "Verifone VX990", "Verifone H-T4230", "PAX D210H", "PAX D210", "PAX S80 GEM CL", "PAX S900", "PAX S920 CTLS", "PAX A920", "PAX E700", "PAX SP30", "Ingenico ICT 250 FIX GPRS 3G-CL", "Ingenico ICT 220 FIX GPRS 3G-CL", "Ingenico IWL 225 Mobile GPRS 3G-CL", "Ingenico IWL 228 WIFI", "Ingenico IWL 258 WIFI", "Ingenico MOVE 2500 MOBILE Wifi CL", "Ingenico MOVE 2500 MOBILE GPRS 3G-CL", "Ingenico MOVE 3500", "Ingenico MOVE 5000", "Ingenico DESK 5000", "Ingenico DESK 3000", "Ingenico DESK 1000", "Ingenico APOS A8", "Ingenico IWL 220", "Ingenico IWL 220 W CTLS", "Ingenico MOVE 2500 Wifi", "Verifone VX520", "Verifone VX675", "Verifone VX520 Mobile GPRS", "Ingenico MOVE 2500 CL", "QR(STIKER)", "Verifone VX520C Mobile GPRS"};
        String[] list = defaultList;

        if (getCustomerCode().equals("CIMB")) {
            list = CIMBList;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String> (RevisitDetailActivity.this, R.layout._spinner,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTypeEdc.setAdapter(dataAdapter); spTypeEdcDitarik.setAdapter(dataAdapter);
    }

    void _kondisiEdc() {
        String[] defaultList = {"Baik", "Buruk", "Rusak", "Tidak Dapat Dicek"};
        String[] CIMBList = {"Bisa Dicek", "Tidak Dapat Dicek"};
        String[] list = defaultList;

        if (getCustomerCode().equals("CIMB")) {
            list = CIMBList;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<> (RevisitDetailActivity.this, R.layout._spinner,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKondisiEdc.setAdapter(dataAdapter); spKondisiEdcDitarik.setAdapter(dataAdapter);
    }

    void _providerSimcard() {
        String[] defaultList = {"TSEL", "TSEL Legacy", "TSEL M2M", "XL", "XL Legacy", "XL M2M", "Indosat", "Indosat Legacy", "Indosat Reguler"};
        String[] CIMBList = {"GPRS Telkomsel", "GPRS Indosat", "GPRS XL", "WiFi", "Dial Up"};
        String[] list = defaultList;

        if (getCustomerCode().equals("CIMB")) {
            list = CIMBList;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String> (RevisitDetailActivity.this, R.layout._spinner,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProviderSimcard.setAdapter(dataAdapter); spProviderSimcardDitarik.setAdapter(dataAdapter);
    }

    void _status() {
        String[] array = {"Done", "Close", "Revisit"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String> (RevisitDetailActivity.this, R.layout._spinner,array);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(dataAdapter);
    }

    void test_transaksi(String status) {
        String[] array = new String[0];
        boolean isCimb = cek_cimb(ticket_no);

        if (status.equals("Done")){
            array = new String[2];
            array[0] = "Sukses";
            array[1] = "Gagal";
        } else if (status.equals("Close")){
            array = new String[1];
            array[0] = "Gagal";
        } else if (isCimb && status.equals("Revisit")){
            array = new String[1];
            array[0] = "Gagal";
        } else {
            array = new String[1];
            array[0] = "-";
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String> (RevisitDetailActivity.this, R.layout._spinner,array);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_test_transaksi.setAdapter(dataAdapter);


        // Set selection for saved jobs
        _offlineId        = getIntent().getLongExtra("id", 0);
        type_status       = getIntent().getStringExtra("type_status");

        if (type_status.equals("saved_jobs")) {
            offline = OfflineOrder.findById(OfflineOrder.class, _offlineId);
            if (offline.transactionTest.equals("Sukses")) sp_test_transaksi.setSelection(0);
            else if (offline.transactionTest.equals("Gagal")) sp_test_transaksi.setSelection(1);
        }
    }

    void reschedule_date(String status) {
        if (status.equals("Revisit")){
            tvRescheduleDate.setVisibility(View.VISIBLE);
            rl_reschedule_date.setVisibility(View.VISIBLE);
            etRescheduleDate.setEnabled(true);
        } else {
            tvRescheduleDate.setVisibility(View.GONE);
            rl_reschedule_date.setVisibility(View.GONE);
            etRescheduleDate.setEnabled(false);
            etRescheduleDate.setText("");
        }
    }

    /*void catatan_kunjungan_(String status,String test_transaksi) {
        String[] array = new String[0];
        if (status.equals("Done") && test_transaksi.equals("Sukses")){
            array = new String[11];
            array[0] = "Rollout";
            array[1] = "Training Kasir";
            array[2] = "Pemasangan Collateral";
            array[3] = "Replace Simcard";
            array[4] = "Replace EDC";
            array[5] = "Dipasang Bank";
            array[6] = "INIT";
            array[7] = "Scan QR";
            array[8] = "Tapcash";
            array[9] = "Transaksi Debit";
            array[10] = "Transaksi Kredit";
        }
        else if (status.equals("Done") && test_transaksi.equals("Gagal")){
            array = new String[6];
            array[0] = "EDC Bermasalah";
            array[1] = "EDC Disimpan Merchant";
            array[2] = "EDC Tidak ada di Merchant";
            array[3] = "Mesin EDC Berubah Vendor";
            array[4] = "Merchant Tidak Mengijinkan Test Transaksi";
            array[5] = "Alasan Lain";
        }
        else if (status.equals("Close") && test_transaksi.equals("Gagal")){
            array = new String[3];
            array[0] = "Tutup Sementara";
            array[1] = "Tutup Permanent";
            array[2] = "Tidak Ditemukan";
        }
        else if (status.equals("Revisit") && test_transaksi.equals("-")){
            array = new String[4];
            array[0] = "Menunggu Konfirmasi Owner";
            array[1] = "Menunggu Konfirmasi Bank";
            array[2] = "Merchant Tutup Sementara";
            array[3] = "Merchant Belum Grand Opening";
        }
        else{
            array = new String[1];
            array[0] = "-";
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String> (RevisitDetailActivity.this, R.layout._spinner,array);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_catatan_kunjungan.setAdapter(dataAdapter);
    }*/

    void catatan_kunjungan(String status,String test_transaksi) {
        String type_status = getIntent().getStringExtra("type_status");
        String ticket_no   = getIntent().getStringExtra("ticket_no");

        boolean isCimb = cek_cimb(ticket_no);
        Log.d("RevisitDetailActivity", "CIMB: " + isCimb + ", ticket_no: " + ticket_no);

        if (type_status.equals("saved_jobs")) {
            List<OfflineOrder> offlineOrder = OfflineOrder.findWithQuery(OfflineOrder.class, "Select * from OFFLINE_ORDER where jobs_id = ?", _jobsId);
//            tv_catatan_kunjungan.setText(offlineOrder.get(0).visitNote);
        } else {
            tv_catatan_kunjungan.setText("- Pilih -");
            catatanKunjungan_arrayList = new ArrayList<>();
            btcatatan_kunjungan = false;

            if (isCimb) {
                if (status.equals("Done") && test_transaksi.equals("Sukses")) {
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Rollout/Software Update", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Training Kasir", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Pemasangan Collateral", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Replace Simcard", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Replace EDC", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Dipasang Bank", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("INIT", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Scan QR", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Tapcash", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Transaksi Debit", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Transaksi Kredit", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Penarikan Sukses", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Penarikan Gagal", false));
                    btcatatan_kunjungan = true;
                } else if (status.equals("Done") && test_transaksi.equals("Gagal")) {
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("EDC tidak ada di lokasi merchant", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("EDC/Dongle/SIM CARD/SAM CARD bermasalah", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Merchant minta EDC Fixed Line", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Merchant minta EDC GPRS", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Merchant minta penambahan fitur", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Line telepon belum ada", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Line telepon bermasalah", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Merchant menolak dilakukannya pekerjaan", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("EDC berada di KP Merchant", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Alasan Lain", false));
                } else if (status.equals("Revisit") && test_transaksi.equals("Gagal")) {
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Pending atas permintaan Bank", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Perangkat Pendukung EDC belum diterima", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Bank belum setting ulang di TMS", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Belum ada konfirmasi dari Bank", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Default Reason Code for DSN", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Belum ada konfirmasi dari KP Merchant", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Lokasi merchant belum siap", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Merchant minta reschedule kunjungan", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Alasan Lain", false));
                } else if (status.equals("Close") && test_transaksi.equals("Gagal")) {
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Alamat tidak ditemukan", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Alamat/Nama Merchant berbeda dengan SPK", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Merchant tutup permanen", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Merchant tutup sementara", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Alasan Lain", false));
                }
            } else if (is_BCA_SP_Canvassing()) {
                if (rbgCanvassing.getCheckedRadioButtonId() == R.id.rbBCAMaintenance) {
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Rollout/Software Update", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Training Kasir", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Pemasangan Collateral", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Replace Simcard", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Replace EDC", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Sterilisasi EDC", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Scan QR", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Tapcash", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Transaksi Debit", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Transaksi Kredit", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Merchant Menolak Canvassing", false));
                    btcatatan_kunjungan = true;
                } else if (rbgCanvassing.getCheckedRadioButtonId() == R.id.rbBCAAkuisisi) {
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Merchant tidak ada EDC BCA", false));
                }
            } else {
                if (status.equals("Done") && test_transaksi.equals("Sukses")) {
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Rollout/Software Update", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Training Kasir", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Pemasangan Collateral", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Replace Simcard", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Replace EDC", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Dipasang Bank", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("INIT", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Scan QR", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Tapcash", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Transaksi Debit", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Transaksi Kredit", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Penarikan Sukses", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Penarikan Gagal", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Uji Transaksi Contactless", false));
                    btcatatan_kunjungan = true;
                } else if (status.equals("Done") && test_transaksi.equals("Gagal")) {
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("EDC Bermasalah", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("EDC Disimpan Merchant", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("EDC Tidak ada di Merchant", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Mesin EDC Berubah Vendor", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Merchant Tidak Mengijinkan Test Transaksi", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Alasan Lain", false));
                } else if (status.equals("Close") && test_transaksi.equals("Gagal")) {
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Merchant Tutup Sementara", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Merchant Tutup Permanent", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Merchant Tidak Ditemukan", false));
                } else if (status.equals("Revisit") && test_transaksi.equals("-")) {
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Menunggu Konfirmasi Owner", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Menunggu Konfirmasi Bank", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Merchant Tutup Sementara", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Merchant Belum Grand Opening", false));
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("Spare Belum Tersedia", false));
                } else {
                    tv_catatan_kunjungan.setText("-");
                    catatanKunjungan_arrayList.add(new CatatanKunjunganModel("-", true));
                }
            }
        }
    }

    void detail_rootcause() {
        String type_status = getIntent().getStringExtra("type_status");
        if (type_status.equals("saved_jobs")) {
            List<OfflineOrder> offlineOrder = OfflineOrder.findWithQuery(OfflineOrder.class, "Select * from OFFLINE_ORDER where jobs_id = ?", _jobsId);
//            tv_detail_rootcause.setText(offlineOrder.get(0).detailRootCause);
        } else {
            tv_detail_rootcause.setText("- Pilih -");
            detailRootcause_arrayList = new ArrayList<>();

            if (getCustomerCode().equals("CIMB")) {
                detailRootcause_arrayList.add(new CatatanKunjunganModel("Hardware", false));
                detailRootcause_arrayList.add(new CatatanKunjunganModel("Network", false));
                detailRootcause_arrayList.add(new CatatanKunjunganModel("Request", false));
                detailRootcause_arrayList.add(new CatatanKunjunganModel("Software", false));
                detailRootcause_arrayList.add(new CatatanKunjunganModel("Other", false));
                detailRootcause_arrayList.add(new CatatanKunjunganModel("- None -", false));
            } else {
                boolean replace_simcard = false;
                boolean replace_edc = false;
                boolean edc_bermasalah = false;
                for (int i = 0; i < catatanKunjungan_arrayList.size(); i++) {
                    if (catatanKunjungan_arrayList.get(i).isCheck()) {
                        String str_catatan_kunjungan = catatanKunjungan_arrayList.get(i).getName();
                        if (str_catatan_kunjungan.equals("Replace Simcard")) {
                            replace_simcard = true;
                        }
                        if (str_catatan_kunjungan.equals("Replace EDC")) {
                            replace_edc = true;
                        }
                        if (str_catatan_kunjungan.equals("EDC Bermasalah")) {
                            edc_bermasalah = true;
                        }
                    }
                }
                if (replace_simcard) {
                    detailRootcause_arrayList.add(new CatatanKunjunganModel("Sinyal Lemah", false));
                    detailRootcause_arrayList.add(new CatatanKunjunganModel("Simcard Rusak", false));
                }
                if (replace_edc) {
                    detailRootcause_arrayList.add(new CatatanKunjunganModel("EDC Rusak", false));
                    detailRootcause_arrayList.add(new CatatanKunjunganModel("Alasan Lain", false));
                }
                if (edc_bermasalah) {
                    detailRootcause_arrayList.add(new CatatanKunjunganModel("EDC Rusak", false));
                    detailRootcause_arrayList.add(new CatatanKunjunganModel("Problem Sinyal", false));
                }
                if (!replace_simcard && !replace_edc && !edc_bermasalah) {
                    detailRootcause_arrayList.add(new CatatanKunjunganModel("- None -", false));
                }
            }
        }
    }

    void simcard(){
        lr_simcard.setVisibility(View.GONE);
        String[] array;
        boolean replace_simcard = false;
        for (int i=0; i< detailRootcause_arrayList.size(); i++){
            if (detailRootcause_arrayList.get(i).isCheck()){
                String str_replace_simcard = detailRootcause_arrayList.get(i).getName();
                if (str_replace_simcard.equals("Sinyal Lemah") || str_replace_simcard.equals("Simcard Rusak")){
                    replace_simcard = true;
                }
            }
        }
        if (replace_simcard) {
            array = new String[3];
            array[0] = "XL";
            array[1] = "TSEL";
            array[2] = "ISAT";
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String> (RevisitDetailActivity.this, R.layout._spinner,array);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_simcard.setAdapter(dataAdapter);
            lr_simcard.setVisibility(View.VISIBLE);
        }
    }

    void collateral(){
        lr_collateral.setVisibility(View.GONE);
        String[] array = new String[0];
        boolean pemasangan_collateral = false;
        for (int i=0; i< catatanKunjungan_arrayList.size(); i++){
            if (catatanKunjungan_arrayList.get(i).isCheck()){
                String str_catatan_kunjungan = catatanKunjungan_arrayList.get(i).getName();
                if (str_catatan_kunjungan.equals("Pemasangan Collateral")){
                    pemasangan_collateral = true;
                }
            }
        }
        if (pemasangan_collateral) {
            array = new String[3];
            array[0] = "QRIS";
            array[1] = "Wajib PIN";
            array[2] = "Sticker Call Center";
            lr_collateral.setVisibility(View.VISIBLE);
        }else{
            array = new String[1];
            array[0] = "-";
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String> (RevisitDetailActivity.this, R.layout._spinner,array);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_collateral.setAdapter(dataAdapter);
    }

    //---------------- CIMB SPINNER ----------------

    void subRootCause() {
        String detailRootCause = tv_detail_rootcause.getText().toString();
        String[] subRootCauseList = {"None"};
        final String[] hardware = {"Adaptor", "Printer", "Reader Chip", "Baterai", "EDC Mati Total", "Tamper / Allert Irruption", "Keypad / Hang"};
        final String[] network = {"SP - Telkomsel", "SP - XL", "SP - Indosat", "Simcard Problem", "No Dial Tone", "Wifi"};
        final String[] request = {"Pengecekan Fitur", "Pengecekan Kondisi EDC", "Standby Teknisi", "Training EDC", "Kunjungan Retrieval", "Collateral", "Thermal"};
        final String[] software = {"Configure", "Please Initialize", "Problem Aplikasi", "Invalid ES", "LTWK", "Setting tanggal/bulan/tahun"};
        final String[] other = {"Inactive TID", "MID Closed", "Tidak ada kendala"};

        if (detailRootCause.equals("Hardware")) {
            subRootCauseList = hardware;
        }

        if (detailRootCause.equals("Network")) {
            subRootCauseList = network;
        }

        if (detailRootCause.equals("Request")) {
            subRootCauseList = request;
        }

        if (detailRootCause.equals("Software")) {
            subRootCauseList = software;
        }

        if (detailRootCause.equals("Other")) {
            subRootCauseList = other;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<> (RevisitDetailActivity.this, R.layout._spinner,subRootCauseList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_sub_root_cause.setAdapter(dataAdapter);
    }

    void kondisiDetailEdc() {
        final String[] list = {"Baik", "Rusak", "Reschedule", "Request Tarik", "TID Inactive", "EDC Disimpan", "Line Telp/Wifi", "EDC Tidak Ada Dilokasi", "Kendala Pass", "Lainnya"};

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<> (RevisitDetailActivity.this, R.layout._spinner,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_kondisi_detail_edc_terpasang.setAdapter(dataAdapter);
    }

    void requestMerchant() {
        final String[] list = {"Tidak ada request", "Ubah tipe EDC (harus ada BA)", "Ubah data (nama/alamat/no rekening harus ada BA)", "Request Tarik"};

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<> (RevisitDetailActivity.this, R.layout._spinner,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_request_merchant.setAdapter(dataAdapter);
    }

    void reasonCode() {
        final String[] list = {"EDC/Dongle/SIM CARD/SAM CARD bermasalah", "Alamat/Nama Merchant berbeda dengan SPK", "Merchant menolak dilakukannya pekerjaan", "Perangkat Pendukung EDC belum diterima", "Belum ada konfirmasi dari KP Merchant", "Merchant minta reschedule kunjungan", "Merchant minta penambahan fitur", "EDC tidak ada di lokasi merchant", "Merchant minta EDC Fixed Line", "Bank belum setting ulang di TMS", "Belum ada konfirmasi dari Bank", "Default Reason Code for DSN", "Belum ada rekening dan ATM", "Merchant sedang direnovasi", "Lokasi merchant belum siap", "Pending atas permintaan Bank", "EDC berada di KP Merchant", "Merchant tutup sementara", "Adaptor / Charger Hilang", "Line telepon bermasalah", "EDC disimpan di gudang", "Merchant minta EDC GPRS", "Merchant minta EDC GPRS", "Alamat tidak ditemukan", "Merchant tutup permanen", "Line telepon belum ada", "EDC Hilang"};

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<> (RevisitDetailActivity.this, R.layout._spinner,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_reason_code.setAdapter(dataAdapter);
    }

    void scan_QR(){
        lr_scan_qr.setVisibility(View.GONE);
        boolean scan_qr = false;
        for (int i=0; i< catatanKunjungan_arrayList.size(); i++){
            if (catatanKunjungan_arrayList.get(i).isCheck()){
                String str_catatan_kunjungan = catatanKunjungan_arrayList.get(i).getName();
                if (str_catatan_kunjungan.equals("Scan QR")){
                    scan_qr = true;
                }
            }
        }
        if (scan_qr){
            lr_scan_qr.setVisibility(View.VISIBLE);
        }
    }

    void foto_bukti_konfirmasi(String status){
        lr_foto_bukti_konfirmasi.setVisibility(View.GONE);
        if (status.equals("Revisit")) {
            lr_foto_bukti_konfirmasi.setVisibility(View.VISIBLE);
        }
    }


    void cek_note(){
        boolean rollout = false;
        boolean replace_edc = false;
        boolean dipasang_bank = false;
        boolean init = false;
        boolean alasan_lain = false;
        boolean edc_bermasalah = false;
        for (int i=0; i< catatanKunjungan_arrayList.size(); i++){
            if (catatanKunjungan_arrayList.get(i).isCheck()){
                String str_catatan_kunjungan = catatanKunjungan_arrayList.get(i).getName();
                if (str_catatan_kunjungan.equals("Rollout/Software Update")){
                    rollout = true;
                }
                if (str_catatan_kunjungan.equals("Replace EDC")){
                    replace_edc = true;
                }
                if (str_catatan_kunjungan.equals("Dipasang Bank")){
                    dipasang_bank = true;
                }
                if (str_catatan_kunjungan.equals("INIT")){
                    init = true;
                }
                if (str_catatan_kunjungan.equals("Alasan Lain")){
                    alasan_lain = true;
                }
                if (str_catatan_kunjungan.equals("EDC Bermasalah")) {
                    edc_bermasalah = true;
                }
            }
        }

        // Note is enabled for any combination of options
//        etNote.setText("-");
//        etNote.setEnabled(false);
//        if (rollout || replace_edc || dipasang_bank || init || alasan_lain || edc_bermasalah
//                || spStatus.getSelectedItem().toString().equals("Revisit")){
//            etNote.setText("");
//            etNote.setEnabled(true);
//        }
//        if (category.equals("withdrawal")){
//            etNote.setText("");
//            etNote.setEnabled(true);
//        }
    }

    private void changeEdcInfoField() {
        LL_EDCTerpasang.setVisibility(View.VISIBLE);
        LL_LDCditarik.setVisibility(View.GONE);
        findViewById(R.id.LL_EDCRetur).setVisibility(View.GONE);

        if (spStatus.getSelectedItem().toString().equals("Done") || spStatus.getSelectedItem().toString().equals("Revisit")) {
            if (_category.equals("WD")) {
                LL_EDCTerpasang.setVisibility(View.GONE);
                LL_LDCditarik.setVisibility(View.VISIBLE);
            }

            if (idata_merchant) { readjustHeight(lr_data_merchant); }
        }

        if (spStatus.getSelectedItem().toString().equals("Close")) {
            if (_category.equals("IS") || _category.equals("RP")) {
                LL_EDCTerpasang.setVisibility(View.GONE);
                LL_LDCditarik.setVisibility(View.GONE);
                findViewById(R.id.LL_EDCRetur).setVisibility(View.VISIBLE);

                if (isEmptyOrBlank(etSnEdcRetur.getText().toString())) {
                    ((ScrollView) findViewById(R.id.scrollViewMain)).smoothScrollTo(0, 0);
                    etSnEdcRetur.setError("This field can not be blank");
                    etSnEdcRetur.requestFocus();
                    openDataMerchant();
                } else if (idata_merchant) {
                    readjustHeight(lr_data_merchant);
                }
            } else {
                if (idata_merchant) { readjustHeight(lr_data_merchant); }
            }
        }
    }

    private void readjustHeight(LinearLayout linearLayout) {
        linearLayout.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.getLayoutParams().height = lr_data_merchant.getMeasuredHeight();
    }

    void _loadData_revisit() {
        progressDialog = new ProgressDialog(RevisitDetailActivity.this);
        progressDialog.setMessage(ConstantUtility.BASE_LOADING);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Intent _intent    = getIntent();
        _jobsId           = _intent.getStringExtra("_JOBS_ID");
        ticket_no         = _intent.getStringExtra("ticket_no");
        _sla              = _intent.getStringExtra("sla");
        _restAging        = _intent.getStringExtra("rest_aging");
        String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        String _datetime  = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        String _signature = SignatureUtility.doSignature(_datetime, _accountId);

        ShopeeInterface _interface     = RetrofitService.getRetrofitService().create(ShopeeInterface.class);
        final Call<ShopeeModel> _model = _interface._goRevisitDetail(_accountId, _jobsId, _datetime, _signature);

        _model.enqueue(new Callback<ShopeeModel>() {
            @Override
            public void onResponse(Call<ShopeeModel> call, final Response<ShopeeModel> response) {
                progressDialog.dismiss();

                if(response.body() != null) {
                    if(response.body().getSuccess().matches("false")) {
                        Toast.makeText(RevisitDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        tvCategory.setText(response.body().getDatas().get(0).getCategory());
                        tvCustomer.setText(response.body().getDatas().get(0).getCustomer());
                        String jo_no = response.body().getDatas().get(0).getTicket_Number().toLowerCase();
                        cek_cimb(jo_no);
                        cek_shopee(jo_no);
                        category = response.body().getDatas().get(0).getCategory().toLowerCase();
//                        cek_withdrawal();
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
                        tvContactPerson.setText(response.body().getDatas().get(0).getContact_Person());
                        tvNote.setText(response.body().getDatas().get(0).getNote());
                        etNote.setText(response.body().getDatas().get(0).getNoteBefore());
                        tvDamage.setText(response.body().getDatas().get(0).getDamage_Type());
                        tvInit.setText(response.body().getDatas().get(0).getInit_Code());
                        tvSla.setText(response.body().getDatas().get(0).getSla());
                        tvSnEdc.setText(response.body().getDatas().get(0).getSn_Edc());
                        tvSnSim.setText(response.body().getDatas().get(0).getSn_Sim());
                        spStatus.setSelection(2);
                    }
                }
                else
                {
                    Toast.makeText(RevisitDetailActivity.this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShopeeModel> call, Throwable throwable) {
                progressDialog.dismiss();
                Toast.makeText(RevisitDetailActivity.this, constantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void _loadData_savedJobs() {
        progressDialog = new ProgressDialog(RevisitDetailActivity.this);
        progressDialog.setMessage(constantUtility.BASE_LOADING);
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Disable all EditText, Imageview, Spinner
        LinearLayout ll = (LinearLayout) findViewById(R.id.lrDataDetail);
        for (View view : ll.getTouchables()){
            if (view instanceof EditText){
                EditText editText = (EditText) view;
                editText.setEnabled(false);
                editText.setFocusable(false);
                editText.setFocusableInTouchMode(false);
            }
            if (view instanceof ImageView){
                ImageView iv = (ImageView) view;
                iv.setEnabled(false);
            }
        }
        spStatus.setEnabled(false); spStatus.setFocusable(false); spStatus.setFocusableInTouchMode(false);
        sp_test_transaksi.setEnabled(false); sp_test_transaksi.setFocusable(false); sp_test_transaksi.setFocusableInTouchMode(false);
        tv_catatan_kunjungan.setEnabled(false); tv_catatan_kunjungan.setFocusable(false); tv_catatan_kunjungan.setFocusableInTouchMode(false);
        tv_detail_rootcause.setEnabled(false); tv_detail_rootcause.setFocusable(false); tv_detail_rootcause.setFocusableInTouchMode(false);

        btnSaveOffline.setEnabled(false); btnSaveOffline.setBackgroundColor(getResources().getColor(R.color.gray));

        Intent _intent    = getIntent();
        _offlineId        = _intent.getLongExtra("id", 0);
        _jobsId           = _intent.getStringExtra("_JOBS_ID");
        _restAging        = _intent.getStringExtra("rest_aging");


        offline = OfflineOrder.findById(OfflineOrder.class, _offlineId);

        tvCategory.setText(offline.orderCategory);
        tvCustomer.setText(offline.orderCustomer);
        String jo_no = offline.orderTicket.toLowerCase();
        cek_cimb(jo_no);
        cek_shopee(jo_no);
        category = offline.orderCategory.toLowerCase();
        tvReleaseDate.setText(offline.orderDate);
        tvTicket.setText(offline.orderTicket);
        tvSPK.setText(offline.orderSpk);
        tvTid.setText(offline.orderTid);
        tvTidCimb.setText(offline.orderTidCimb);
        tvMid.setText(offline.orderMid);
        tvMerchantName.setText(offline.orderMerchantName);
        tvMerchantAddress.setText(offline.orderMerchantAddress);
        tvMerchantAddress2.setText(offline.orderMerchantAddress2);
        tvZip.setText(offline.orderZip);
        tvCity.setText(offline.orderCity);
        tvContactPerson.setText(offline.orderContactPerson);
        tvNote.setText(offline.orderNote);
        tvSla.setText(offline.orderSla);

        // Populate form with offline data
        offline = OfflineOrder.findById(OfflineOrder.class, _offlineId);;
        etPicName.setText(offline.picName);
        etPicNumber.setText(offline.picNumber);
        etSnEdc.setText(offline.snEdc);
        etSnEdcDitarik.setText(offline.snEdcDitarik);
        etSoftwareVersion.setText(offline.softwareVersion);
        etSnSim.setText(offline.snSim);
        etSnSimDitarik.setText(offline.snSimDitarik);
        etFakturOTS.setText(offline.fakturOts);
        etFakturTambahan.setText(offline.fakturTambahan);
        if (offline.status.equals("Done")) spStatus.post(() -> spStatus.setSelection(0));
        else if (offline.status.equals("Close")) spStatus.post(() -> spStatus.setSelection(1));
        else if (offline.status.equals("Revisit")) spStatus.post(() -> spStatus.setSelection(2));
        if (offline.transactionTest.equals("Sukses")) sp_test_transaksi.post(() -> sp_test_transaksi.setSelection(0));
        else if (offline.transactionTest.equals("Gagal")) sp_test_transaksi.post(() -> sp_test_transaksi.setSelection(1));
        tv_catatan_kunjungan.setText(offline.visitNote);
        tv_detail_rootcause.setText(offline.detailRootCause);
        etNote.setText(offline.note);
        tvLatitude.setText(offline.latitude);
        tvLongitude.setText(offline.longitude);
        etRescheduleDate.setText(offline.rescheduleDate);
        if (offline.simCard.equals("XL")) sp_simcard.setSelection(0);
        else if (offline.simCard.equals("TSEL")) sp_simcard.setSelection(1);
        else if (offline.simCard.equals("ISAT")) sp_simcard.setSelection(2);
        if (offline.collateral.equals("QRIS")) sp_collateral.setSelection(0);
        else if (offline.collateral.equals("Wajib PIN")) sp_collateral.setSelection(1);
        else if (offline.collateral.equals("Sticker Call Center")) sp_collateral.setSelection(2);

        photoFiles[0] = new  File(offline.pathMerchantPhoto);
        photoFiles[1] = new  File(offline.pathEdcBackPhoto);
        photoFiles[2]= new  File(offline.pathEdcFrontPhoto);
        photoFiles[3]= new  File(offline.pathReceiptPhoto);
        photoFiles[4]= new  File(offline.pathPosmPhoto);
        photoFiles[5]= new  File(offline.pathOtherEdcPhoto);
        photoFiles[6]= new  File(offline.pathFkmPhoto);
        photoFiles[7]= new  File(offline.pathNotePhoto);
        photoFiles[9]= new  File(offline.pathOthers);
        _pathESignature = offline.pathESignature;
        File eSignatureFile = new  File(_pathESignature);

        if(photoFiles[0].exists()) { Bitmap merchantBitmap = BitmapFactory.decodeFile(photoFiles[0].getAbsolutePath()); ivMerchant.setImageBitmap(merchantBitmap); }
        if(photoFiles[1].exists()) { Bitmap backEdcBitmap = BitmapFactory.decodeFile(photoFiles[1].getAbsolutePath()); ivEdcBelakang.setImageBitmap(backEdcBitmap); }
        if(photoFiles[2].exists()) { Bitmap frontEdcBitmap = BitmapFactory.decodeFile(photoFiles[2].getAbsolutePath()); ivEdcDepan.setImageBitmap(frontEdcBitmap); }
        if(photoFiles[3].exists()) { Bitmap receiptBitmap = BitmapFactory.decodeFile(photoFiles[3].getAbsolutePath()); ivStruk.setImageBitmap(receiptBitmap); }
        if(photoFiles[4].exists()) { Bitmap posmBitmap = BitmapFactory.decodeFile(photoFiles[4].getAbsolutePath()); ivPOSM.setImageBitmap(posmBitmap); }
        if(photoFiles[5].exists()) { Bitmap otherEdcBitmap = BitmapFactory.decodeFile(photoFiles[5].getAbsolutePath()); ivEdcBankLain.setImageBitmap(otherEdcBitmap); }
        if(photoFiles[6].exists()) { Bitmap fkmBitmap = BitmapFactory.decodeFile(photoFiles[6].getAbsolutePath()); ivFKM.setImageBitmap(fkmBitmap); }
        if(photoFiles[7].exists()) { Bitmap noteBitmap = BitmapFactory.decodeFile(photoFiles[7].getAbsolutePath()); ivBeritaAcara.setImageBitmap(noteBitmap); }
        if(photoFiles[9].exists()) { Bitmap otherBitmap = BitmapFactory.decodeFile(photoFiles[9].getAbsolutePath()); ivOthers.setImageBitmap(otherBitmap); }
        if(eSignatureFile.exists()) { Bitmap eSignatureBitmap = BitmapFactory.decodeFile(eSignatureFile.getAbsolutePath()); ivESignature.setImageBitmap(eSignatureBitmap); }

        progressDialog.dismiss();
    }

    void _loadData_progress() {
        Intent _intent    = getIntent();
        _offlineId        = _intent.getLongExtra("id", 0);

        if (_offlineId != 0) {
            offline = OfflineOrder.findById(OfflineOrder.class, _offlineId);
            _jobsId = _intent.getStringExtra("_JOBS_ID");
            ticket_no = _intent.getStringExtra("ticket_no");
            tvCategory.setText(offline.orderCategory);
            tvCustomer.setText(offline.orderCustomer);
            String jo_no = offline.orderTicket.toLowerCase();
            cek_cimb(jo_no);
            cek_shopee(jo_no);
            category = offline.orderCategory.toLowerCase();
//            cek_withdrawal();
            tvReleaseDate.setText(offline.orderDate);
//            tvImportBank.setText(response.body().getDatas().get(0).getImport_Bank());
//            tvBank.setText(response.body().getDatas().get(0).getBank());
//            tvCase.setText(response.body().getDatas().get(0).getCases());
            tvTicket.setText(offline.orderTicket);
            tvSPK.setText(offline.orderSpk);
//            tvType.setText(response.body().getDatas().get(0).getWork_Type());
            tvTid.setText(offline.orderTid);
            tvTidCimb.setText(offline.orderTidCimb);
            tvMid.setText(offline.orderMid);
            tvMerchantName.setText(offline.orderMerchantName);
            tvMerchantAddress.setText(offline.orderMerchantAddress);
            tvMerchantAddress2.setText(offline.orderMerchantAddress2);
            tvZip.setText(offline.orderZip);
            tvCity.setText(offline.orderCity);
            tvContactPerson.setText(offline.orderContactPerson);
            tvNote.setText(offline.orderNote);
//            tvDamage.setText(response.body().getDatas().get(0).getDamage_Type());
//            tvInit.setText(response.body().getDatas().get(0).getInit_Code());
            tvSla.setText(offline.orderSla);
//            tvSnEdc.setText(response.body().getDatas().get(0).getSn_Edc());
//            tvSnSim.setText(response.body().getDatas().get(0).getSn_Sim());
            spStatus.setSelection(0);
        } else {
            progressDialog = new ProgressDialog(RevisitDetailActivity.this);
            progressDialog.setMessage(constantUtility.BASE_LOADING);
            progressDialog.setCancelable(false);
            progressDialog.show();

            _intent = getIntent();
            _jobsId = _intent.getStringExtra("_JOBS_ID");
            _sla = _intent.getStringExtra("sla");
            _restAging = _intent.getStringExtra("rest_aging");
            String _accountId = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
            String _datetime = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
            String _signature = SignatureUtility.doSignature(_datetime, _accountId);

            ShopeeInterface _interface = RetrofitService.getRetrofitService().create(ShopeeInterface.class);
            final Call<ShopeeModel> _model = _interface._goDetail(_accountId, _jobsId, _datetime, _signature);

            _model.enqueue(new Callback<ShopeeModel>() {
                @Override
                public void onResponse(@NonNull Call<ShopeeModel> call, @NonNull final Response<ShopeeModel> response) {
                    progressDialog.dismiss();

                    if (response.body() != null) {
                        if (response.body().getSuccess().matches("false")) {
                            Toast.makeText(RevisitDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            tvCategory.setText(response.body().getDatas().get(0).getCategory());
                            tvCustomer.setText(response.body().getDatas().get(0).getCustomer());
                            String jo_no = response.body().getDatas().get(0).getTicket_Number().toLowerCase();
                            cek_cimb(jo_no);
                            cek_shopee(jo_no);
                            category = response.body().getDatas().get(0).getCategory().toLowerCase();
//                            cek_withdrawal();
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
                            tvContactPerson.setText(response.body().getDatas().get(0).getContact_Person());
                            tvNote.setText(response.body().getDatas().get(0).getNote());
                            tvDamage.setText(response.body().getDatas().get(0).getDamage_Type());
                            tvInit.setText(response.body().getDatas().get(0).getInit_Code());
                            tvSla.setText(response.body().getDatas().get(0).getSla());
                            tvSnEdc.setText(response.body().getDatas().get(0).getSn_Edc());
                            tvSnSim.setText(response.body().getDatas().get(0).getSn_Sim());
                            spStatus.setSelection(0);
                        }
                    } else {
                        Toast.makeText(RevisitDetailActivity.this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ShopeeModel> call, @NonNull Throwable throwable) {
                    progressDialog.dismiss();
                    Toast.makeText(RevisitDetailActivity.this, ConstantUtility.ERROR_API, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    boolean cek_cimb (String jo_no){
        String cimb = "";
        try {
            cimb = jo_no.substring(jo_no.indexOf("CIMB"));
        }catch(Exception ex) {

        }
        if (cimb.equals("")){
            tvTidLama.setText("Previous TID");
            return false;
        }else{
            tvTidLama.setText("TID CIMB");
            return true;
        }
    }

    void cek_shopee (String jo_no){
        String shopee = "";
        try {
            shopee = jo_no.substring(jo_no.indexOf("shopee"));
        }catch(Exception ex) {

        }
//        if (shopee.equals("") && is_audit.equals("0")){
//            lr_sn_edc.setVisibility(View.GONE);
//            height = 240;
//        }else{
//            lr_sn_edc.setVisibility(View.VISIBLE);
//            height = 300;
//        }
        if (!shopee.equals("")){
            tv_foto_belakang_edc.setText("Foto PIC Merchant dengan EDC dan POSM");
            tv_foto_edc_lain.setText("Foto Daftar Menu Pada Daftar Perangkat");
        }
    }

    void cek_withdrawal(){
        if (category.equals("withdrawal")){
            tv_test_transaksi.setText("Status Penarikan");
        }else{
            tv_test_transaksi.setText("Test Transaksi");
        }
    }

    boolean doValidate(){
        Intent _intent          = getIntent();
        _category               = _intent.getStringExtra("category");
        String _latitude        = tvLatitude.getText().toString();
        String _longitude       = tvLongitude.getText().toString();
        String status           = spStatus.getSelectedItem().toString();
        String test_transaksi   = sp_test_transaksi.getSelectedItem().toString();
        String kondisiEdc = spKondisiEdc.getSelectedItem().toString();

        if (tv_catatan_kunjungan.getText().toString().equals("- Pilih -")){
            Toast.makeText(RevisitDetailActivity.this, "Catatan kunjungan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tv_detail_rootcause.getText().toString().equals("- Pilih -")){
            Toast.makeText(RevisitDetailActivity.this, "Detail rootcause tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(_latitude.isEmpty()) {
            Toast.makeText(RevisitDetailActivity.this, "Latitude tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(_longitude.isEmpty()) {
            Toast.makeText(RevisitDetailActivity.this, "Longitude tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (_category.equals("PM") || _category.equals("CM") || _category.equals("RP") || _category.equals("SP")) {
            if (tv_catatan_kunjungan.getText().toString().contains("Replace EDC") || tv_catatan_kunjungan.getText().toString().contains("Replace Simcard")) {
                if (isEmptyOrBlank(etSnEdcDitarik.getText().toString()) || isEmptyOrBlank(etSnSimDitarik.getText().toString())) {
                    Toast.makeText(RevisitDetailActivity.this, "SN EDC Ditarik dan ICCID SIM Ditarik tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        if (_category.equals("FK") && (etFakturOTS.getText().toString().equals("") || etFakturTambahan.getText().toString().equals(""))) {
            Toast.makeText(RevisitDetailActivity.this, "Faktur tambahan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(_pathESignature.isEmpty()) {
            Toast.makeText(RevisitDetailActivity.this, "Tanda tangan digital tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (status.equals("Revisit")){
            if(ivBuktiKonfirmasi.getDrawable() == null) {
                Toast.makeText(RevisitDetailActivity.this, "Bukti konfirmasi tidak boleh kosong saat revisit", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Check if photos are >= 2
            int imageCount = 0;
            for (int i = 0; i < lr_foto_merchant.getChildCount(); i++) {
                View v = lr_foto_merchant.getChildAt(i);
                if (v instanceof ImageView) {
                    if (((ImageView) v).getDrawable() != null) imageCount++;
                }
            }
            if (imageCount < 2) { // +1 for e-sign -1 for bukti konfirmasi
                Toast.makeText(RevisitDetailActivity.this, "Minimal jumlah foto yang diupload adalah 2", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (status.equals("Done")) {
            if (test_transaksi.equals("Gagal")) {
                // Check if photos are >= 5
                int imageCount = 0;
                for (int i = 0; i < lr_foto_merchant.getChildCount(); i++) {
                    View v = lr_foto_merchant.getChildAt(i);
                    if (v instanceof ImageView) {
                        if (((ImageView) v).getDrawable() != null) imageCount++;
                    }
                }
                if (imageCount < 6) { // +1 for e-sign
                    Toast.makeText(RevisitDetailActivity.this, "Minimal jumlah foto yang diupload adalah 5", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (etPicName.getText().toString().equals("")){
                    Toast.makeText(RevisitDetailActivity.this, "Nama PIC tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else if (etPicNumber.getText().toString().equals("")){
                    Toast.makeText(RevisitDetailActivity.this, "Nomor PIC tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else if (test_transaksi.equals("Sukses")) {
                // Check if photos are >= 7
                int imageCount = 0;
                for (int i = 0; i < lr_foto_merchant.getChildCount(); i++) {
                    View v = lr_foto_merchant.getChildAt(i);
                    if (v instanceof ImageView) {
                        if (((ImageView) v).getDrawable() != null) imageCount++;
                    }
                }
                if (imageCount < 8) { // +1 for e-sign
                    Toast.makeText(RevisitDetailActivity.this, "Minimal jumlah foto yang diupload adalah 7", Toast.LENGTH_SHORT).show();
                    return false;
                }

                //SN EDC and ICCID Simcard can't be empty
                if (_category.equals("IS")){
                    if (isEmptyOrBlank(etSnEdc.getText().toString()) && isEmptyOrBlank(etSnSim.getText().toString())) {
                        Toast.makeText(RevisitDetailActivity.this, "SN EDC dan ICCID Simcard tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }

                if (_category.equals("WD")){
                    if (isEmptyOrBlank(etSnEdcDitarik.getText().toString()) && isEmptyOrBlank(etSnSimDitarik.getText().toString())) {
                        Toast.makeText(RevisitDetailActivity.this, "SN EDC dan ICCID Simcard tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }

            if (kondisiEdc.equals("Baik") || kondisiEdc.equals("Buruk")) {
                if (isEmptyOrBlank(etSoftwareVersion.getText().toString()) && LL_EDCTerpasang.getVisibility() == View.VISIBLE) {
                    Toast.makeText(RevisitDetailActivity.this, "Versi software harus diisi", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        if (status.equals("Close")) {
            if (isEmptyOrBlank(etSnEdcRetur.getText().toString()) && findViewById(R.id.LL_EDCRetur).getVisibility() == View.VISIBLE) {
                Toast.makeText(RevisitDetailActivity.this, "SN EDC Retur harus diisi", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private boolean doValidate_BCA_SP() {
        Intent _intent          = getIntent();
        _category               = _intent.getStringExtra("category");
        String _latitude        = tvLatitude.getText().toString();
        String _longitude       = tvLongitude.getText().toString();

        if (isEmptyOrBlank(etPicName.getText().toString())) {
            Toast.makeText(RevisitDetailActivity.this, "Nama PIC tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (isEmptyOrBlank(etSnSim.getText().toString())) {
            Toast.makeText(RevisitDetailActivity.this, "Nama merchant tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (rbgCanvassing.getCheckedRadioButtonId() == R.id.rbBCAMaintenance) {
            if (isEmptyOrBlank(etPicNumber.getText().toString())) {
                Toast.makeText(RevisitDetailActivity.this, "Terminal ID tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (isEmptyOrBlank(etSnEdc.getText().toString())) {
                Toast.makeText(RevisitDetailActivity.this, "Merchant ID tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (ivStruk.getDrawable() == null) {
                Toast.makeText(RevisitDetailActivity.this, "Foto Struk tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (ivPOSM.getDrawable() == null) {
                Toast.makeText(RevisitDetailActivity.this, "Foto POSM tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (ivMerchant.getDrawable() == null) {
            Toast.makeText(RevisitDetailActivity.this, "Foto merchant tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (tv_catatan_kunjungan.getText().toString().equals("- Pilih -")){
            Toast.makeText(RevisitDetailActivity.this, "Catatan kunjungan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (_latitude.isEmpty()) {
            Toast.makeText(RevisitDetailActivity.this, "Latitude tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        } else if (_longitude.isEmpty()) {
            Toast.makeText(RevisitDetailActivity.this, "Longitude tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Check if any unit is installed, withdrawn, or returned back to warehouse
    // Retur (warehouse): EDC installation cancelled, Retur (merchant): EDC withdrawn from merchant
    private String getUnitStatus() {
        String jobStatus = spStatus.getSelectedItem().toString();

        if (_category.equals("IS")) {
            if (jobStatus.equals("Done")) {
                return "Installed";
            }

            if (jobStatus.equals("Close")) {
                return "Retur (warehouse)";
            }
        }

        if (_category.equals("WD")) {
            if (jobStatus.equals("Done")) {
                return "Retur (merchant)";
            }

            if (jobStatus.equals("Close")) {
                return "Unchanged";
            }
        }

        if (_category.equals("RP")) {
            if (jobStatus.equals("Close")) {
                return "Retur (warehouse)";
            }
        }

        if (tv_catatan_kunjungan.getText().toString().contains("Replace EDC") || tv_catatan_kunjungan.getText().toString().contains("Replace Simcard")) {
            return "Installed and Retur (merchant)";
        }

        return "Unchanged";
    }

    void doSaveOffline() {
        deleteThisJobOrder();

        boolean isValid;
        if (is_BCA_SP_Canvassing()) {isValid = doValidate_BCA_SP();}
        else {isValid = doValidate();}

        if (isValid) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(ConstantUtility.BASE_LOADING);
            progressDialog.setCancelable(false);
            progressDialog.show();

            final String _accountId     = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
            final String _datetime      = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
            final String _signature     = SignatureUtility.doSignature(_datetime, _accountId);

            String myFormat             = "yyyy-MM-dd HH:mm:ss"; //In which you need put here
            SimpleDateFormat sdf        = new SimpleDateFormat(myFormat);
            String rescheduleDate       = sdf.format(myCalendar.getTime());
            Intent _intent              = getIntent();
            _offlineId                  = _intent.getLongExtra("id", 0);
            _sla                        = _intent.getStringExtra("sla");
            _restAging                  = _intent.getStringExtra("rest_aging");
            String _date                = _intent.getStringExtra("date");

            String note = "";
            if (etNote.getText().toString().equals("")) note = "-";
            else note = etNote.getText().toString();

            String str_simcard = "";
            try {
                str_simcard = sp_simcard.getSelectedItem().toString();
            }catch (Exception ignored) {

            }
            String str_collateral = "";
            try {
                str_collateral = sp_collateral.getSelectedItem().toString();
            }catch (Exception e) {

            }

            OfflineOrder offlineOrder = new OfflineOrder(_accountId, _jobsId, etPicName.getText().toString(), etPicNumber.getText().toString(), etSnEdc.getText().toString(), etSnEdcDitarik.getText().toString(), etSoftwareVersion.getText().toString(), etSnSim.getText().toString(), etSnSimDitarik.getText().toString(), note,
                    getFilePath(photoFiles[0]), getFilePath(photoFiles[1]), getFilePath(photoFiles[2]), getFilePath(photoFiles[3]), getFilePath(photoFiles[4]), getFilePath(photoFiles[5]), getFilePath(photoFiles[6]), getFilePath(photoFiles[7]), getFilePath(photoFiles[8]), _pathESignature,  getFilePath(photoFiles[9]),
                    sp_test_transaksi.getSelectedItem().toString(), tv_catatan_kunjungan.getText().toString(), etScanQR.getText().toString(), tv_detail_rootcause.getText().toString(), str_simcard, str_collateral, etFakturOTS.getText().toString(), etFakturTambahan.getText().toString(), tvLatitude.getText().toString(), tvLongitude.getText().toString(), spStatus.getSelectedItem().toString(), rescheduleDate, _restAging, _datetime, _signature,
                    tvCategory.getText().toString(), tvCustomer.getText().toString(), _date, ticket_no, tvMerchantName.getText().toString(), tvMerchantAddress.getText().toString(), tvMerchantAddress2.getText().toString(), tvZip.getText().toString(), tvCity.getText().toString(), tvContactPerson.getText().toString(), tvSPK.getText().toString(), tvTid.getText().toString(), tvTidCimb.getText().toString(), tvMid.getText().toString(), tvNote.getText().toString(), _sla);
            offlineOrder.save();

            Log.d("Save Offline",
                    "accountId: " + _accountId + "\n"
                            + _jobsId + "\n"
                            + etPicName.getText().toString() + "\n"
                            + etPicNumber.getText().toString()  + "\n"
                            + etSnEdc.getText().toString() + "\n"
                            + etScanQR.getText().toString() + "\n"
                            + etSnSim.getText().toString()  + "\n"
                            + etNote.getText().toString()  + "\n"
                            + sp_test_transaksi.getSelectedItem().toString() + "\n"
                            + tv_catatan_kunjungan.getText().toString() + "\n"
                            + tv_detail_rootcause.getText().toString() + "\n"
                            + etFakturOTS.getText().toString() + "\n"
                            + etFakturTambahan.getText().toString() + "\n"
                            + tvLatitude.getText().toString() + "\n"
                            + tvLongitude.getText().toString() + "\n"
                            + spStatus.getSelectedItem().toString() + "\n"
                            + rescheduleDate + "\n"
                            + _restAging + "\n"
                            + _datetime + "\n"
                            + _signature);

//            offline = OfflineOrder.findById(OfflineOrder.class, _offlineId);
//            offline.delete();
            finish();
        }
    }

    private String getFilePath(File file) {
        if (file != null) { return file.getAbsolutePath(); }
        return "";
    }

    void doSubmit() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(ConstantUtility.BASE_LOADING);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String _accountId     = sharedPreferences.getString("_SESSION_ACCOUNT_ID", "");
        File _fileESignature        = new File(_pathESignature);
        final String _datetime      = (String) DateFormat.format("yyyyMMddhhmmss", new java.util.Date());
        final String _signature     = signatureUtility.doSignature(_datetime, _accountId);

        String myFormat             = "yyyy-MM-dd HH:mm:ss"; //In which you need put here
        SimpleDateFormat sdf        = new SimpleDateFormat(myFormat);
        String rescheduleDate       = sdf.format(myCalendar.getTime());
        Intent _intent              = getIntent();
        _sla                        = _intent.getStringExtra("sla");
        _restAging                  = _intent.getStringExtra("rest_aging");

        String type_edc = "";
        try {
            if (spTypeEdc.getSelectedItem().toString().equals("Lainnya")) type_edc = etOtherTypeEdc.getText().toString();
            else type_edc = spTypeEdc.getSelectedItem().toString();
        } catch (Exception e) { Log.d("RevisitDetailActivity", e.toString()); }

        String kondisi_edc = "";
        try { kondisi_edc = spKondisiEdc.getSelectedItem().toString(); } catch (Exception e) { Log.d("RevisitDetailActivity", e.toString()); }

        String provider_simcard = "";
        try { provider_simcard = spProviderSimcard.getSelectedItem().toString(); } catch (Exception e) { Log.d("RevisitDetailActivity", e.toString()); }

        String type_edc_ditarik = "";
        try {
            if (spTypeEdcDitarik.getSelectedItem().toString().equals("Lainnya")) type_edc_ditarik = etOtherTypeEdcDitarik.getText().toString();
            else type_edc_ditarik = spTypeEdcDitarik.getSelectedItem().toString();
        } catch (Exception e) { Log.d("RevisitDetailActivity", e.toString()); }

        String kondisi_edc_ditarik = "";
        try { kondisi_edc_ditarik = spKondisiEdcDitarik.getSelectedItem().toString(); } catch (Exception e) { Log.d("RevisitDetailActivity", e.toString()); }

        String provider_simcard_ditarik = "";
        try { provider_simcard_ditarik = spProviderSimcardDitarik.getSelectedItem().toString(); } catch (Exception e) { Log.d("RevisitDetailActivity", e.toString()); }

        String str_simcard = "";
        try { str_simcard = sp_simcard.getSelectedItem().toString(); } catch (Exception e) { Log.d("RevisitDetailActivity", e.toString()); }

        String str_collateral = "";
        try { str_collateral = sp_collateral.getSelectedItem().toString(); } catch (Exception e) { Log.d("RevisitDetailActivity", e.toString()); }

        final RequestBody rbAccountId           = RequestBody.create(MediaType.parse("text/plain"), _accountId);
        RequestBody rbJobsId                    = RequestBody.create(MediaType.parse("text/plain"), _jobsId);
        RequestBody rbPicName                   = RequestBody.create(MediaType.parse("text/plain"), getString(etPicName));
        RequestBody rbPicNumber                 = RequestBody.create(MediaType.parse("text/plain"), getString(etPicNumber));
        RequestBody rbTypeEdc                   = RequestBody.create(MediaType.parse("text/plain"), type_edc);
        RequestBody rbSnEdc                     = RequestBody.create(MediaType.parse("text/plain"), getString(etSnEdc));
        RequestBody rbSoftwareVersion           = RequestBody.create(MediaType.parse("text/plain"), getString(etSoftwareVersion));
        RequestBody rbKondisiEdc                = RequestBody.create(MediaType.parse("text/plain"), kondisi_edc);
        RequestBody rbSamcard1                  = RequestBody.create(MediaType.parse("text/plain"), getString(etSamcard1));
        RequestBody rbSamcard2                  = RequestBody.create(MediaType.parse("text/plain"), getString(etSamcard2));
        RequestBody rbProviderSimcard           = RequestBody.create(MediaType.parse("text/plain"), provider_simcard);
        RequestBody rbSnSim                     = RequestBody.create(MediaType.parse("text/plain"), getString(etSnSim));
        RequestBody rbTypeEdcDitarik            = RequestBody.create(MediaType.parse("text/plain"), type_edc_ditarik);
        RequestBody rbSnEdcDitarik              = RequestBody.create(MediaType.parse("text/plain"), getString(etSnEdcDitarik));
        RequestBody rbSoftwareVersionDitarik    = RequestBody.create(MediaType.parse("text/plain"), getString(etSoftwareVersionDitarik));
        RequestBody rbKondisiEdcDitarik         = RequestBody.create(MediaType.parse("text/plain"), kondisi_edc_ditarik);
        RequestBody rbSamcard1Ditarik           = RequestBody.create(MediaType.parse("text/plain"), getString(etSamcard1Ditarik));
        RequestBody rbSamcard2Ditarik           = RequestBody.create(MediaType.parse("text/plain"), getString(etSamcard2Ditarik));
        RequestBody rbProviderSimcardDitarik    = RequestBody.create(MediaType.parse("text/plain"), provider_simcard_ditarik);
        RequestBody rbSnSimDitarik              = RequestBody.create(MediaType.parse("text/plain"), getString(etSnSimDitarik));
        RequestBody rbScanQR                    = RequestBody.create(MediaType.parse("text/plain"), getString(etScanQR));
        RequestBody rbNote                      = RequestBody.create(MediaType.parse("text/plain"), getString(etNote));
        MultipartBody.Part paMerchant           = getPhotoRequestBody(0, "photoMerchant");
        MultipartBody.Part paEdcBelakang        = getPhotoRequestBody(1, "photoEdcBelakang");
        MultipartBody.Part paEdcDepan           = getPhotoRequestBody(2, "photoEdcDepan");
        MultipartBody.Part paStruk              = getPhotoRequestBody(3, "photoStruk");
        MultipartBody.Part paPOSM               = getPhotoRequestBody(4, "photoPOSM");
        MultipartBody.Part paEdcBankLain        = getPhotoRequestBody(5, "photoEdcBankLain");
        MultipartBody.Part paFKM                = getPhotoRequestBody(6, "photoFKM");
        MultipartBody.Part paBeritaAcara        = getPhotoRequestBody(7, "photoBeritaAcara");
        MultipartBody.Part paBuktiKonfirmasi    = getPhotoRequestBody(8, "bukti_konfirmasi");
        RequestBody rbPESignature               = RequestBody.create(MediaType.parse("multipart/form-data"), _fileESignature);
        MultipartBody.Part paESignature         = MultipartBody.Part.createFormData("photoESignature", _fileESignature.getName(), rbPESignature);
        MultipartBody.Part paOthers             = getPhotoRequestBody(9, "photoOthers");
        RequestBody rbTestTransaksi             = RequestBody.create(MediaType.parse("text/plain"), sp_test_transaksi.getSelectedItem().toString());
        RequestBody rbCatatanKunjungan          = RequestBody.create(MediaType.parse("text/plain"), tv_catatan_kunjungan.getText().toString());
        RequestBody rbDetailRootcause           = RequestBody.create(MediaType.parse("text/plain"), tv_detail_rootcause.getText().toString());
        RequestBody rbSimcard                   = RequestBody.create(MediaType.parse("text/plain"), str_simcard);
        RequestBody rbCollateral                = RequestBody.create(MediaType.parse("text/plain"), str_collateral);
        RequestBody rbFakturOTS                 = RequestBody.create(MediaType.parse("text/plain"), getString(etFakturOTS));
        RequestBody rbFakturTambahan            = RequestBody.create(MediaType.parse("text/plain"), getString(etFakturTambahan));
        RequestBody rbLatitude                  = RequestBody.create(MediaType.parse("text/plain"), tvLatitude.getText().toString());
        RequestBody rbLongitude                 = RequestBody.create(MediaType.parse("text/plain"), tvLongitude.getText().toString());
        RequestBody rbStatus                    = RequestBody.create(MediaType.parse("text/plain"), spStatus.getSelectedItem().toString());
        RequestBody rbRescheduleDate            = RequestBody.create(MediaType.parse("text/plain"), rescheduleDate);
        RequestBody rbRestAging                 = RequestBody.create(MediaType.parse("text/plain"), _restAging);
        RequestBody rbDatetime                  = RequestBody.create(MediaType.parse("text/plain"), _datetime);
        RequestBody rbSignature                 = RequestBody.create(MediaType.parse("text/plain"), _signature);

        if (_pathESignature.equals("")) {
            paESignature = null;
        }

        ShopeeInterface _interface       = RetrofitService.getRetrofitService().create(ShopeeInterface.class);
        Call<ShopeeModel> _model = _interface.submit3(rbAccountId, rbJobsId,
                rbPicName, rbPicNumber,
                rbTypeEdc, rbSnEdc, rbSoftwareVersion, rbKondisiEdc, rbSamcard1, rbSamcard2, rbProviderSimcard, rbSnSim,
                rbTypeEdcDitarik, rbSnEdcDitarik, rbSoftwareVersionDitarik, rbKondisiEdcDitarik, rbSamcard1Ditarik, rbSamcard2Ditarik, rbProviderSimcardDitarik, rbSnSimDitarik,
                rbNote
                ,paMerchant, paEdcBelakang, paEdcDepan, paStruk, paPOSM, paEdcBankLain, paFKM, paBeritaAcara, paBuktiKonfirmasi, paESignature, paOthers
                ,rbTestTransaksi,rbCatatanKunjungan,rbScanQR,rbDetailRootcause,rbSimcard,rbCollateral,rbFakturOTS,rbFakturTambahan, rbLatitude, rbLongitude, rbStatus, rbRescheduleDate, rbRestAging, rbDatetime, rbSignature);

        if (getCustomerCode().equals("CIMB")) {
            RequestBody kondisiDetailEdc            = RequestBody.create(MediaType.parse("text/plain"), ((Spinner) findViewById(R.id.sp_kondisi_detail_edc_terpasang)).getSelectedItem().toString());
            RequestBody kelengkapanEdc              = RequestBody.create(MediaType.parse("text/plain"), getCheckedCheckBoxAsString(findViewById(R.id.ll_kelengkapan_edc_terpasang)));
            RequestBody noSimcard                   = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.shopee_revisit_detail_no_simcard_terpasang)).getText().toString());
            RequestBody requestMerchant             = RequestBody.create(MediaType.parse("text/plain"), sp_request_merchant.getSelectedItem().toString());
            RequestBody reasonCode                  = RequestBody.create(MediaType.parse("text/plain"), spinner_reason_code.getSelectedItem().toString());
            RequestBody totalKasir                  = RequestBody.create(MediaType.parse("text/plain"), ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rbgTotalKasir)).getCheckedRadioButtonId())).getText().toString());
            RequestBody namaKasir                   = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.et_nama_kasir)).getText().toString());
            RequestBody remarkSosialisasiEdc        = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.et_remark_sosialisasi_edc)).getText().toString());
            RequestBody materiTraining              = RequestBody.create(MediaType.parse("text/plain"), getCheckedCheckBoxAsString(findViewById(R.id.ll_materi_training)));
            RequestBody remarkEdc                   = RequestBody.create(MediaType.parse("text/plain"), getCheckedCheckBoxAsString(findViewById(R.id.ll_remark_edc)));
            RequestBody subRootCause                = RequestBody.create(MediaType.parse("text/plain"), ((Spinner) findViewById(R.id.sp_sub_root_cause)).getSelectedItem().toString());
            RequestBody hasilCallTms                = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.et_hasil_call_tms)).getText().toString());

            RequestBody mid3Bulan                   = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.mid3Bulan)).getText().toString());
            RequestBody mid6Bulan                   = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.mid6Bulan)).getText().toString());
            RequestBody mid12Bulan                  = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.mid12Bulan)).getText().toString());
            RequestBody mid18Bulan                  = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.mid18Bulan)).getText().toString());
            RequestBody mid24Bulan                  = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.mid24Bulan)).getText().toString());
            RequestBody mid36Bulan                  = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.mid36Bulan)).getText().toString());
            RequestBody qr                          = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.qr)).getText().toString());

            RequestBody tid3Bulan                   = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.tid3Bulan)).getText().toString());
            RequestBody tid6Bulan                   = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.tid6Bulan)).getText().toString());
            RequestBody tid9Bulan                   = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.tid9Bulan)).getText().toString());
            RequestBody tid12Bulan                  = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.tid12Bulan)).getText().toString());
            RequestBody tid24Bulan                  = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.tid24Bulan)).getText().toString());
            RequestBody tid36Bulan                  = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.tid36Bulan)).getText().toString());
            RequestBody rekpon                      = RequestBody.create(MediaType.parse("text/plain"), ((EditText) findViewById(R.id.rekpon)).getText().toString());

            _model = _interface.submitCIMB(rbAccountId, rbJobsId,
                    rbPicName, rbPicNumber,
                    rbTypeEdc, rbSnEdc, rbSoftwareVersion, rbKondisiEdc, rbSamcard1, rbSamcard2, rbProviderSimcard, rbSnSim,
                    rbTypeEdcDitarik, rbSnEdcDitarik, rbSoftwareVersionDitarik, rbKondisiEdcDitarik, rbSamcard1Ditarik, rbSamcard2Ditarik, rbProviderSimcardDitarik, rbSnSimDitarik,
                    rbNote
                    ,paMerchant, paEdcBelakang, paEdcDepan, paStruk, paPOSM, paEdcBankLain, paFKM, paBeritaAcara, paBuktiKonfirmasi, paESignature, paOthers
                    ,rbTestTransaksi,rbCatatanKunjungan,rbScanQR,rbDetailRootcause,rbSimcard,rbCollateral,rbFakturOTS,rbFakturTambahan, rbLatitude, rbLongitude, rbStatus, rbRescheduleDate, rbRestAging, rbDatetime, rbSignature,
                    kondisiDetailEdc, kelengkapanEdc, noSimcard, requestMerchant, reasonCode, totalKasir, namaKasir, remarkSosialisasiEdc, materiTraining, remarkEdc, subRootCause, hasilCallTms,
                    mid3Bulan, mid6Bulan, mid12Bulan, mid18Bulan, mid24Bulan, mid36Bulan,qr,
                    tid3Bulan, tid6Bulan, tid9Bulan, tid12Bulan, tid24Bulan, tid36Bulan, rekpon);
        }

        Log.d("Submit Online",
                "accountId: " + _accountId + "\n"
                        + _jobsId + "\n"
                        + etPicName.getText().toString() + "\n"
                        + etPicNumber.getText().toString()  + "\n"
                        + etSnEdc.getText().toString() + "\n"
                        + etScanQR.getText().toString() + "\n"
                        + etSnSim.getText().toString()  + "\n"
                        + etNote.getText().toString()  + "\n"
                        + sp_test_transaksi.getSelectedItem().toString() + "\n"
                        + tv_catatan_kunjungan.getText().toString() + "\n"
                        + tv_detail_rootcause.getText().toString() + "\n"
                        + etFakturOTS.getText().toString() + "\n"
                        + etFakturTambahan.getText().toString() + "\n"
                        + tvLatitude.getText().toString() + "\n"
                        + tvLongitude.getText().toString() + "\n"
                        + spStatus.getSelectedItem().toString() + "\n"
                        + rescheduleDate + "\n"
                        + _restAging + "\n"
                        + _datetime + "\n"
                        + _signature);

        _model.enqueue(new Callback<ShopeeModel>() {
            @Override
            public void onResponse(Call<ShopeeModel> call, final Response<ShopeeModel> response) {
                progressDialog.dismiss();

                if(response.body() != null) {
                    if (response.body().getSuccess().matches("false")) {
                        Toast.makeText(RevisitDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        String type_status = getIntent().getStringExtra("type_status");
//                            if (type_status.equals("saved_jobs")) {
//                                offline.delete();
//                            }
//                            offline = OfflineOrder.findById(OfflineOrder.class, _offlineId);

                        // Delete saved jobs, after submit
                        deleteThisJobOrder();

                        Toast.makeText(RevisitDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else
                {
                    Log.d("Submit", "response.body is null");
                    Toast.makeText(RevisitDetailActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(RevisitDetailActivity.this, ConstantUtility.ERROR_EXCEPTION, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ShopeeModel> call, Throwable throwable) {
                progressDialog.dismiss();
                Log.d("Submit", "Fail to submit, please try again");
                Toast.makeText(RevisitDetailActivity.this, throwable.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private MultipartBody.Part getPhotoRequestBody(int photoIndex, String name) {
        if (photoFiles[photoIndex] != null) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), photoFiles[photoIndex]);
            return MultipartBody.Part.createFormData(name, photoFiles[photoIndex].getName(), requestBody);
        }

        return null;
    }

    private String escapeApostrophe(String string) {
        return string.replaceAll("'", "\\\\'");
    }

    private String getString(EditText editText) {
        return editText.getText() == null ? "" : escapeApostrophe(editText.getText().toString());
    }

    // Check if the SN of 'EDC Terpasang' have status 'installation'
    private void getUnitData() {
        Call<ResponseBody> result = apiInterface.postInstallationUnit(ApiClient.api_key,user_id, "EDC");
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseStr = response.body().string();
                    if (JsonData.get_data(responseStr,"message").equals("success")) {
                        int rowcount = JsonData.get_listdata(responseStr,"unit_id").size();

                        if (rowcount == 0) { Toast.makeText(RevisitDetailActivity.this, "You have no EDC with installation status", Toast.LENGTH_SHORT).show(); }

                        for (int i=0; i<rowcount; i++){
                            String unit_id = JsonData.get_listdata(responseStr,"unit_id").get(i);
                            String desc_1 = JsonData.get_listdata(responseStr,"desc_1").get(i);

                            if (etSnEdc.getText().toString().equals(desc_1)) {
                                String unitData = "[{\"unit_id\":\""+ unit_id + "\""
                                        +",\"status\":\""+ "installed" +"\""
                                        +",\"lat\":\""+ tvLatitude.getText().toString() +"\""
                                        +",\"lng\":\""+ tvLongitude.getText().toString() +"\"}]";

                                updateToSentraLogistik(unitData);
                            }

                            if (!etSnEdc.getText().toString().equals(desc_1) && i == rowcount - 1) {
                                Toast.makeText(RevisitDetailActivity.this, "No EDC matched", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(RevisitDetailActivity.this, JsonData.get_data(responseStr,"message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(RevisitDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(RevisitDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Update unit status on Sentra Logistik to 'installed'
    private void updateToSentraLogistik(String unitData) {
        Call<ResponseBody> result = apiInterface.postSubmitInstallationUnit(ApiClient.api_key,user_id, unitData);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseStr = response.body().string();
                    if (JsonData.get_data(responseStr,"status").equals("200")) {
                        Toast.makeText(RevisitDetailActivity.this, "Update to Sentra Logistik success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RevisitDetailActivity.this, JsonData.get_data(responseStr,"message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(RevisitDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RevisitDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Create retur order for Sentra Logistik
    private void createReturOrder() {
        Call<ResponseBody> result = apiInterface.postReturUnit(ApiClient.api_key,user_id, "");
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                String json_response = "";

                try {
                    assert response.body() != null;
                    json_response = response.body().string();
                } catch (IOException e) {
                    Toast.makeText(RevisitDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                if (JsonData.get_data(json_response,"status").equals("200")) {
                    String movement_id = JsonData.get_data(json_response,"movement_id");
                    String order_no = JsonData.get_data(json_response,"order_no");
                    String warehouse_id = JsonData.get_data(json_response,"warehouse_id");

                    sendReturOrder(movement_id, order_no, warehouse_id);
                } else {
                    Toast.makeText(RevisitDetailActivity.this, JsonData.get_data(json_response,"message"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(RevisitDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Send retur order to Sentra Logistik
    private void sendReturOrder(String movement_id, String order_no, String warehouse_id) {
        Call<ResponseBody> result = apiInterface.postSubmitReturUnit(ApiClient.api_key,user_id, movement_id, order_no, 1,warehouse_id, getReturUnitData(warehouse_id));
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                String json_response = "";
                try {
                    json_response = response.body().string();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (JsonData.get_data(json_response,"status").equals("200") && JsonData.get_data(json_response,"message").equals("success")) {
                    Toast.makeText(RevisitDetailActivity.this, "Retur order created", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RevisitDetailActivity.this, JsonData.get_data(json_response,"message"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RevisitDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getReturUnitData(String warehouse_id) {
        String snEdc = "";

        if (getUnitStatus().contains("merchant"))  { snEdc = etSnEdcDitarik.getText().toString(); }
        if (getUnitStatus().contains("warehouse")) { snEdc = etSnEdcRetur.getText().toString(); }

        String data_unit = "[{\"unit_type\":\""+ "edc" + "\""
                +",\"sn\":\""+ snEdc +"\""
                +",\"type\":\"\""
                +",\"warehouse_id\":\""+ warehouse_id +"\""
                +",\"customer\":\""+ getCustomerCode() +"\""
                +",\"status\":\""+ "retur" +"\""
                +",\"lat\":\""+ tvLatitude.getText().toString() +"\""
                +",\"lng\":\""+ tvLongitude.getText().toString() +"\""
                +",\"job_type\":\""+ category + " - " + spStatus.getSelectedItem().toString() + "\""
                +",\"reason\":\""+ tv_catatan_kunjungan.getText().toString() +"\""
                +"}]";

        return data_unit;
    }

    private void deleteThisJobOrder() {
        List<OfflineOrder> offlineOrderList;
        offlineOrderList = OfflineOrder.find(OfflineOrder.class,
                "jobs_id = ?", _jobsId);
        for (int i=0; i<offlineOrderList.size(); i++) {
            offlineOrderList.get(i).delete();
        }
    }

    //====================================== P H O T O =========================================

    private void setPhotoOnClickListener() {
        for (int count = 0; count < imageViewPhotos.length; count++) {

            if (count != 3 && count != 8) {
                ActivityResultLauncher<Intent> activityResultLauncher = getPhotoResultLauncher(count);
                imageViewPhotos[count].setOnClickListener(view -> launchPhotoIntent(activityResultLauncher));
            }
        }

        ActivityResultLauncher<Intent> launcherStruk = getPhotoResultLauncher(3);
        ActivityResultLauncher<Intent> launcherStrukPick = getPickPhotoResultLauncher(3);
        ivStruk.setOnClickListener(view -> {
            try {
                if (PERMISSION_APPROVE) {
                    chooseCameraPictureDialog(launcherStruk, launcherStrukPick);
                }
            } catch (Exception e) {
                Toast.makeText(RevisitDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncherBukti = getPickPhotoResultLauncher(8);
        ivBuktiKonfirmasi.setOnClickListener(view -> {
            try {
                if (PERMISSION_APPROVE) {
                    launchPickPhotoIntent(activityResultLauncherBukti);
                } else {
                    Toast.makeText(RevisitDetailActivity.this, ConstantUtility.ERROR_PERMISSION, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(RevisitDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ActivityResultLauncher<Intent> getPhotoResultLauncher(int index) {
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        String processedFileName = ticket_no  + "_" + photoNames[index];
                        Bitmap bitmap = resizeBitmap(BitmapFactory.decodeFile(photoResult.getAbsolutePath()));
                        bitmap = rotateBitmap(bitmap);

                        imageViewPhotos[index].setImageBitmap(addWatermark(bitmap));
                        photoFiles[index] = saveImage(imageViewPhotos[index].getDrawable(), processedFileName);
                    }

                    if (photoResult.exists()) { Log.d("Delete photo .temp file", "Delete: " + photoResult.delete()); }
                });
    }

    private ActivityResultLauncher<Intent> getPickPhotoResultLauncher(int index) {
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(result.getData().getData());
                            Bitmap bitmap = resizeBitmap(BitmapFactory.decodeStream(inputStream));
                            imageViewPhotos[index].setImageBitmap(addWatermark(bitmap));
                            photoFiles[index] = saveImage(imageViewPhotos[index].getDrawable(), ticket_no + "_" + photoNames[index]);
                            inputStream.close();
                        } catch (Exception e) {
                            Toast.makeText(RevisitDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void launchPhotoIntent (ActivityResultLauncher<Intent> activityResultLauncher) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        photoResult = new File(savedRawPhotoFolder, Calendar.getInstance().getTimeInMillis() + ".jpg");

        /*
        try {
            photoResult = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), ".jpg", savedRawPhotoFolder);
        } catch (IOException e) {
            Toast.makeText(RevisitDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        */

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoResult));
        activityResultLauncher.launch(intent);
    }

    private void launchPickPhotoIntent (ActivityResultLauncher<Intent> activityResultLauncher) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private Bitmap rotateBitmap(Bitmap bitmap) {
        Bitmap b = bitmap;
        int w = b.getWidth();
        int h = b.getHeight();
        if (w>h) {
            b = rotateImage(bitmap, 90);
        }

        return b;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private Bitmap addWatermark(Bitmap bitmap) {
        String dateTimeLocation = "";

        // Reading location
        if (!tvLatitude.getText().equals("") && !tvLongitude.getText().equals("")) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(Double.valueOf(tvLatitude.getText().toString()), Double.valueOf(tvLongitude.getText().toString()), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String addressLine = addresses.get(0).getAddressLine(0);
            dateTimeLocation = addressLine + System.getProperty("line.separator") +
                    "Lat: " + tvLatitude.getText().toString() + "°" + System.getProperty("line.separator") +
                    "Long: " + tvLongitude.getText().toString()+ "°"  + System.getProperty("line.separator");
        }

        // Reading local time in the system
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy  HH:mm:ss");
        dateTimeLocation += sdf.format(Calendar.getInstance().getTime());

        //bitmap = rotateBitmap(bitmap);

        // Draw watermark into bitmap
        Canvas cs = new Canvas(bitmap);
        LinearLayout layout = new LinearLayout(this);

        TextView textView = new TextView(this);
        textView.setText(dateTimeLocation);
        textView.setBackgroundColor(Color.parseColor("#63000000"));
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(11);
        textView.setPadding(15,15,15,15);
        textView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.0f,  getResources().getDisplayMetrics()), 1.0f);

        layout.addView(textView);
        layout.setGravity(Gravity.BOTTOM);

        int widthSpec = View.MeasureSpec.makeMeasureSpec(cs.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(cs.getHeight(), View.MeasureSpec.EXACTLY);
        layout.measure(widthSpec, heightSpec);
        layout.layout(0, 0, cs.getWidth(), cs.getHeight());

        layout.draw(cs);
        // End of [Draw watermark into bitmap]

        return bitmap;
    }

    private Bitmap resizeBitmap(Bitmap source) {
        int maxLength = 1200;
        try {
            if (source.getHeight() >= source.getWidth()) {
                if (source.getHeight() <= maxLength) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                int targetWidth = (int) (maxLength * aspectRatio);

                return Bitmap.createScaledBitmap(source, targetWidth, maxLength, false);
            } else {

                if (source.getWidth() <= maxLength) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = ((double) source.getHeight()) / ((double) source.getWidth());
                int targetHeight = (int) (maxLength * aspectRatio);

                return Bitmap.createScaledBitmap(source, maxLength, targetHeight, false);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return source;
        }
    }

    private File saveImage(Drawable drawable, String image_name) {
        String fileName = image_name + ".jpg";
        File file = new File(savedRawPhotoFolder, fileName);
        Bitmap bitmap = drawableToBitmap(drawable);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            out.flush();
            out.close();

            return file;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    //==========================================================================================

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

    private void expand(final View v, int duration, int targetHeight) {
        int prevHeight  = v.getHeight();
        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.addUpdateListener(animation -> {
            v.getLayoutParams().height = (int) animation.getAnimatedValue();
            v.requestLayout();
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    private void collapse(final View v, int duration, int targetHeight) {
        int prevHeight  = v.getHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            v.getLayoutParams().height = (int) animation.getAnimatedValue();
            v.requestLayout();
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public void DataMerchant_click(View view) {
        if (!idata_merchant){
            openDataMerchant();
        }else {
            idata_merchant = false;
            img_arrow_data_merchant.setImageResource(R.drawable.ic_up_white);
            collapse(lr_data_merchant,500,0);
        }
    }

    private void openDataMerchant() {
        idata_merchant = true;
        img_arrow_data_merchant.setImageResource(R.drawable.ic_down_white);
        lr_data_merchant.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        expand(lr_data_merchant, 500, lr_data_merchant.getMeasuredHeight());
    }

    public void FotoMerchant_click(View view) {
        if (!ifoto_merchant) {
            ifoto_merchant = true;
            img_arrow_foto_merchant.setImageResource(R.drawable.ic_down_white);
            lr_foto_merchant.setVisibility(View.VISIBLE);
        }else {
            ifoto_merchant = false;
            img_arrow_foto_merchant.setImageResource(R.drawable.ic_up_white);
            lr_foto_merchant.setVisibility(View.GONE);
        }
    }

    public void StatusMerchant_click(View view) {
        if (!istatus_merchant){
            istatus_merchant = true;
            img_arrow_status_merchant.setImageResource(R.drawable.ic_down_white);
            lr_status_merchant.setVisibility(View.VISIBLE);
        }else{
            istatus_merchant = false;
            img_arrow_status_merchant.setImageResource(R.drawable.ic_up_white);
            lr_status_merchant.setVisibility(View.GONE);
        }
    }

    public void LokasiMerchant_click(View view) {
        if (!ilokasi_merchant){
            ilokasi_merchant = true;
            img_arrow_lokasi_merchant.setImageResource(R.drawable.ic_down_white);
            lr_lokasi_merchant.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            expand(lr_lokasi_merchant, 500, lr_lokasi_merchant.getMeasuredHeight());
        }else{
            ilokasi_merchant = false;
            img_arrow_lokasi_merchant.setImageResource(R.drawable.ic_up_white);
            collapse(lr_lokasi_merchant,500,0);
        }
    }

    public void DataLainnya_click(View view) {
        if (!idata_lainnya){
            idata_lainnya = true;
            img_arrow_data_lainnya.setImageResource(R.drawable.ic_down_white);
            lr_data_lainnya.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            expand(lr_data_lainnya, 500, lr_data_lainnya.getMeasuredHeight());
        }else{
            idata_lainnya = false;
            img_arrow_data_lainnya.setImageResource(R.drawable.ic_up_white);
            collapse(lr_data_lainnya,500,0);
        }
    }

    @Override
    public void onBackPressed() {
        if (type.equals("scan") || type.equals("scanDitarik") || type.equals("scanRetur") || type.equals("scan_qr")){
            type = "";
            mScannerView.stopCamera();
            lr_data_detail.setVisibility(View.VISIBLE);
            lr_scan.setVisibility(View.GONE);
        }
        else if (type.equals("esign")){
            type = "";
            lr_data_detail.setVisibility(View.VISIBLE);
            lr_esignature.setVisibility(View.GONE);
        }
        else{
            finish();
        }
    }

    public void ScanEDC_click(View view) {
        lr_data_detail.setVisibility(View.GONE);
        lr_scan.setVisibility(View.VISIBLE);
        type = "scan";
        checkCameraPermission();
        mScannerView.startCamera();
    }

    public void ScanEDCDitarik_click(View view) {
        lr_data_detail.setVisibility(View.GONE);
        lr_scan.setVisibility(View.VISIBLE);
        type = "scanDitarik";
        checkCameraPermission();
        mScannerView.startCamera();
    }

    public void ScanEDCRetur_click(View view) {
        lr_data_detail.setVisibility(View.GONE);
        lr_scan.setVisibility(View.VISIBLE);
        type = "scanRetur";
        checkCameraPermission();
        mScannerView.startCamera();
    }

    public void ScanQR_click(View view) {
        lr_data_detail.setVisibility(View.GONE);
        lr_scan.setVisibility(View.VISIBLE);
        type = "scan_qr";
        checkCameraPermission();
        mScannerView.startCamera();
    }

    private void checkCameraPermission(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mScannerView.setResultHandler(RevisitDetailActivity.this);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void handleResult(Result rawResult) {
        String sn = rawResult.getText();
        mScannerView.stopCameraPreview();
        txt_sn.setText(sn);

        pop_alert_scan.findViewById(R.id.btnScanOk).setVisibility(View.VISIBLE);

        if (type.equals("scan") || type.equals("scanDitarik") || type.equals("scanRetur")) {
            checkReturValidation(sn);
        } else {
            txt_scan_status.setText("Valid S/N");
            pop_alert_scan.show();
        }
    }

    private void checkReturValidation(String sn) {
        progressDialog.show();
        Call<ResponseBody> result = apiInterface.postReturCheckUnit(ApiClient.api_key,user_id,"edc",sn);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                String json_response = "";
                try {
                    json_response = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String status = JsonData.get_data(json_response,"status");
                String message = JsonData.get_data(json_response,"message");
                if (status.equals("200")) {
                    String unit_status = JsonData.get_data(json_response,"unit_status");
                    if (unit_status.equals("valid")){
                        txt_scan_status.setText("Valid S/N");
                        txt_scan_status.setTextColor(getResources().getColor(R.color.color_green));
                    }else if (unit_status.equals("invalid")){
                        txt_scan_status.setText("Invalid S/N");
                        txt_scan_status.setTextColor(getResources().getColor(R.color.color_red));
                        pop_alert_scan.findViewById(R.id.btnScanOk).setVisibility(View.GONE);
                    }
                    pop_alert_scan.show();
                }
                else{
                    Toast.makeText(RevisitDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RevisitDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void switchFlashlight_click(View view) {
        if (sw_flashlight.isChecked()){
            txt_flashlight_status.setText("Flashlight ON");
            mScannerView.setFlash(true);
        }else{
            txt_flashlight_status.setText("Flashlight OFF");
            mScannerView.setFlash(false);
        }
    }

    public void btnScanCancel_click(View view) {
        pop_alert_scan.dismiss();
        mScannerView.resumeCameraPreview(this);
    }

    public void btnScanOk_click(View view) {
        if (type.equals("scan")){
            etSnEdc.setText(txt_sn.getText().toString());
        } else if (type.equals("scanDitarik")){
            etSnEdcDitarik.setText(txt_sn.getText().toString());
        } else if (type.equals("scanRetur")){
            etSnEdcRetur.setText(txt_sn.getText().toString());
            etSnEdcRetur.setError(null);
        }else if (type.equals("scan_qr")){
            etScanQR.setText(txt_sn.getText().toString());
        }
        type = "";
        mScannerView.stopCamera();
        lr_data_detail.setVisibility(View.VISIBLE);
        lr_scan.setVisibility(View.GONE);
        pop_alert_scan.dismiss();
    }

    public void CatatanKunjungan_click(View view) {
        popup_type = "catatan_kunjungan";
        txt_header_popup_checkbox.setText("Catatan Kunjungan");
        CatatanKunjunganAdapter catatanKunjunganAdapter = new CatatanKunjunganAdapter(this, R.layout.listview_item_checkbox,catatanKunjungan_arrayList);
        lv_popup_checkbox.setAdapter(catatanKunjunganAdapter);
        popup_item_checkbox.show();
        if (btcatatan_kunjungan){
            btn_ok_item.setVisibility(View.VISIBLE);
        }else{
            btn_ok_item.setVisibility(View.GONE);
        }
    }

    public void DetailRootcause_click(View view) {
        if (tv_catatan_kunjungan.getText().toString().equals("- Pilih -")){
            Toast.makeText(this, "Pilih Catatan Kunjungan dahulu", Toast.LENGTH_SHORT).show();
        }else{
            popup_type = "detail_rootcause";
            txt_header_popup_checkbox.setText("Detail Rootcause");
            CatatanKunjunganAdapter adapter = new CatatanKunjunganAdapter(this, R.layout.listview_item_checkbox, detailRootcause_arrayList);
            lv_popup_checkbox.setAdapter(adapter);
            popup_item_checkbox.show();
            btn_ok_item.setVisibility(View.GONE);
        }
    }

    public void btnCloseItemCheckbox_click(View view) {
        popup_item_checkbox.dismiss();
    }

    public void btnOkItemCheckbox_click(View view) {
        if (popup_type.equals("catatan_kunjungan")){
            String catatan_kunjungan = "";
            for (int i=0; i< catatanKunjungan_arrayList.size(); i++){
                boolean is_check = catatanKunjungan_arrayList.get(i).isCheck();
                if (is_check){
                    catatan_kunjungan = catatan_kunjungan + catatanKunjungan_arrayList.get(i).getName().toString() + ";";
                }
            }
            catatan_kunjungan = catatan_kunjungan + "]";
            catatan_kunjungan = catatan_kunjungan.replace(";]","").replace("]","");
            if (catatan_kunjungan.equals("")){
                Toast.makeText(this, "Pilih Catatan Kunjungan", Toast.LENGTH_SHORT).show();
            }else{
                tv_catatan_kunjungan.setText(catatan_kunjungan);
                popup_item_checkbox.dismiss();
            }

            if (_category.equals("PM") || _category.equals("CM") || _category.equals("RP") || _category.equals("SP")){
                if (tv_catatan_kunjungan.getText().toString().contains("Replace Simcard") || tv_catatan_kunjungan.getText().toString().contains("Replace EDC")) {
                    LL_LDCditarik.setVisibility(View.VISIBLE);
                } else {
                    LL_LDCditarik.setVisibility(View.GONE);
                }
            }

            detail_rootcause();
        }
        else if (popup_type.equals("detail_rootcause")){
            String detail_rootcause = "";
            for (int i=0; i< detailRootcause_arrayList.size(); i++){
                boolean is_check = detailRootcause_arrayList.get(i).isCheck();
                if (is_check){
                    detail_rootcause = detail_rootcause + detailRootcause_arrayList.get(i).getName().toString() + ";";
                }
            }
            detail_rootcause = detail_rootcause + "]";
            detail_rootcause = detail_rootcause.replace(";]","").replace("]","");
            if (detail_rootcause.equals("")){
                Toast.makeText(this, "Pilih Detail Rootcause", Toast.LENGTH_SHORT).show();
            }else{
                tv_detail_rootcause.setText(detail_rootcause);
                popup_item_checkbox.dismiss();
            }
        }
        simcard();
        scan_QR();
        cek_note();
        collateral();
        subRootCause();

        if (idata_merchant) { readjustHeight(lr_data_merchant); }
    }

    private String getCheckedCheckBoxAsString(LinearLayout linearLayout) {
        StringBuilder value = new StringBuilder();

        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View view = linearLayout.getChildAt(i);

            if (view instanceof CheckBox) {
                if (((CheckBox) view).isChecked()) {
                    value.append(((CheckBox) view).getText()).append("; ");
                }
            }

            if (view instanceof LinearLayout) {
                value.append(getCheckedCheckBoxAsString((LinearLayout) view));
            }
        }

        return value.toString();
    }

    private void updateRescheduleDateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        etRescheduleDate.setText(sdf.format(myCalendar.getTime()));
    }

    public boolean isEmptyOrBlank(String string) {
        return string == null || string.trim().isEmpty();
    }

    private class CatatanKunjunganAdapter extends ArrayAdapter<CatatanKunjunganModel> {

        private final ArrayList<CatatanKunjunganModel> catatanKunjunganArrayList;

        public CatatanKunjunganAdapter(Context context, int textViewResourceId, ArrayList<CatatanKunjunganModel> catatanKunjunganAdapterArrayList) {
            super(context, textViewResourceId, catatanKunjunganAdapterArrayList);
            this.catatanKunjunganArrayList = new ArrayList<CatatanKunjunganModel>();
            this.catatanKunjunganArrayList.addAll(catatanKunjunganAdapterArrayList);
        }

        private class ViewHolder {
            TextView name;
            CheckBox check;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            CatatanKunjunganAdapter.ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.listview_item_checkbox, null);
                holder = new CatatanKunjunganAdapter.ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.tvName);
                holder.check = (CheckBox) convertView.findViewById(R.id.cbCheck);
                convertView.setTag(holder);
            } else {
                holder = (CatatanKunjunganAdapter.ViewHolder) convertView.getTag();
            }

            CatatanKunjunganModel catatanKunjunganModel = catatanKunjunganArrayList.get(position);
            holder.name.setText(catatanKunjunganModel.getName());
            holder.check.setChecked(catatanKunjunganModel.isCheck());
            final String name = catatanKunjunganModel.getName();
            if (popup_type.equals("catatan_kunjungan")){
                if (btcatatan_kunjungan){
                    holder.check.setVisibility(View.VISIBLE);
                    holder.check.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean checked = ((CheckBox) v).isChecked();
                            if (checked) {
                                catatanKunjunganArrayList.get(position).setCheck(true);
                            } else {
                                catatanKunjunganArrayList.get(position).setCheck(false);
                            }
                        }
                    });
                }else{
                    holder.check.setVisibility(View.GONE);
                    holder.name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tv_catatan_kunjungan.setText(name);
                            for (int i=0; i< catatanKunjunganArrayList.size(); i++){
                                catatanKunjunganArrayList.get(i).setCheck(false);
                            }
                            catatanKunjunganArrayList.get(position).setCheck(true);
                            popup_item_checkbox.dismiss();
                            detail_rootcause();
                            simcard();
                            collateral();
                            cek_note();
                        }
                    });
                }
            }
            else if (popup_type.equals("detail_rootcause")){
                holder.check.setVisibility(View.GONE);
                holder.name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tv_detail_rootcause.setText(name);
                        catatanKunjunganArrayList.get(position).setCheck(true);
                        popup_item_checkbox.dismiss();
                        simcard();
                        collateral();
                    }
                });
            }
            return convertView;
        }
    }
}
