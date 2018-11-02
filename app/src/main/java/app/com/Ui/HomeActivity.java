package app.com.Ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.com.Utils.Common;
import app.com.myapp.R;

public class HomeActivity extends BaseActivity implements View.OnClickListener{

    private int REQUEST_CODE = 1000;
    private Barcode barcode = null;
    private RadioButton rbEnter, rbScan;
    private EditText etBarcode;
    private Button btnScan, btnSubmit, btnReset;
    private TextView tvQr_BarcodeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        //setToolbar();
        setNavigationPanel();
        setActionBarTitle(getResources().getString(R.string.title_home));
        initializeComponents();
    }

    private void initializeComponents() {
        etBarcode = findViewById(R.id.etBarcode);
        btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(this);
        rbEnter = findViewById(R.id.rbEnter);
        rbScan = findViewById(R.id.rbScan);
        rbEnter.setOnClickListener(this);
        rbScan.setOnClickListener(this);
        btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(this);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        tvQr_BarcodeValue = findViewById(R.id.tvQr_BarcodeValue);
        tvQr_BarcodeValue.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rbScan) {
            btnScan.setEnabled(true);
            etBarcode.setEnabled(false);
            etBarcode.setText("");
            Common.hideKeyboard(HomeActivity.this);
            rbEnter.setChecked(false);
            rbEnter.setClickable(true);
            rbScan.setClickable(false);
            btnSubmit.setEnabled(true);
            btnReset.setEnabled(true);
        } else if (view.getId() == R.id.rbEnter) {
            btnScan.setEnabled(false);
            etBarcode.setEnabled(true);
            barcode = null;
            rbScan.setChecked(false);
            rbScan.setClickable(true);
            rbEnter.setClickable(false);
            btnSubmit.setEnabled(true);
            btnReset.setEnabled(true);
            tvQr_BarcodeValue.setVisibility(View.GONE);
        } else if (view.getId() == R.id.btnReset) {
            btnScan.setEnabled(false);
            etBarcode.setEnabled(false);
            etBarcode.setText("");
            Common.hideKeyboard(HomeActivity.this);
            rbEnter.setChecked(false);
            rbScan.setChecked(false);
            btnSubmit.setEnabled(false);
            btnReset.setEnabled(false);
            tvQr_BarcodeValue.setVisibility(View.GONE);
        } else if (view.getId() == R.id.btnScan) {
            checkPermissionAndOpenScanner();
        } else if (view.getId() == R.id.btnSubmit) {
            String scanCode = getAppNumber();
            if (!TextUtils.isEmpty(scanCode)) {
                Intent intent = new Intent(HomeActivity.this, ScanDetailsActivity.class);
                intent.putExtra("scanCode", scanCode);
                startActivity(intent);
            }
            //submitData(scanCode);
            /*if (Common.isInternetAvailable(HomeActivity.this)) {
                APIcall apIcall = new APIcall(HomeActivity.this);
                String url = AppConstant.API_SUBMIT_DATA + AppConstant.REQUEST_CODE + "=" + barcode.displayValue;
                apIcall.execute(url, APIcall.OPERATION_ID_SUBMIT_DATA, HomeActivity.this);
            } else {
                Common.displayToastMessageShort(HomeActivity.this, AppConstant.CONNECTION_ERROR_MSG, false);
            }*/
        }
    }

    private String getAppNumber() {
        if (rbScan.isChecked()) {
            if (barcode == null) {
                Common.displayToastMessageShort(HomeActivity.this, "Please scan QR Code/Barcode", false);
                return "";
            }
            return barcode.displayValue;
        }
        if (rbEnter.isChecked()) {
            String enterCode = etBarcode.getText().toString().trim();
            if (TextUtils.isEmpty(enterCode)) {
                Common.displayToastMessageShort(HomeActivity.this, "Please enter App Number", false);
                return "";
            }
            return enterCode;
        }
        return "";
    }

    private void checkPermissionAndOpenScanner() {
        if (isPermissionsGranted()) {
            openScanner();
        }
    }

    private final int REQUEST_ID_MULTIPLE_PERMISSIONS = 9;

    private boolean isPermissionsGranted() {
        int camera = ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(HomeActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("in fragment on request", "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        openScanner();
                    } else {
                        Common.displayToastMessageShort(HomeActivity.this, getResources().getString(R.string.error_camera_permission), false);
                    }
                }
            }
        }

    }

    private void openScanner() {
        Intent intent = new Intent(HomeActivity.this, ScanActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data.hasExtra("barcode_value")) {
                barcode = (Barcode) data.getExtras().get("barcode_value");
                if (barcode != null) {
                    String scanCode = barcode.displayValue;//.getSerializableExtra("barcode");
                    if (!TextUtils.isEmpty(scanCode)) {
                        tvQr_BarcodeValue.setText("Scanned App Number is\n\n" + scanCode);
                        tvQr_BarcodeValue.setVisibility(View.VISIBLE);
                    } else {
                        tvQr_BarcodeValue.setVisibility(View.GONE);
                    }
                }
            }
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

}
