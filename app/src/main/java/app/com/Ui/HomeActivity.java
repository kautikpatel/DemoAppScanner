package app.com.Ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.com.BaseActivity;
import app.com.Managers.Communications.APIcall;
import app.com.Utils.AppConstant;
import app.com.Utils.Common;
import app.com.myapp.R;

public class HomeActivity extends BaseActivity implements View.OnClickListener, APIcall.ApiCallListner {

    private int REQUEST_CODE = 1000;
    private Barcode barcode = null;
    private Button btnSubmit;
    private LinearLayout llResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        setToolbar();
        setActionBarTitle(getResources().getString(R.string.title_home));
        initializeComponents();
    }

    private void initializeComponents() {
        Button btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(this);
        llResponse = findViewById(R.id.llResponse);
        llResponse.setVisibility(View.GONE);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnScan) {
            resetValues();
            checkPermissionAndOpenScanner();
        } else if (view.getId() == R.id.btnSubmit) {
            if (Common.isInternetAvailable(HomeActivity.this)) {
                APIcall apIcall = new APIcall(HomeActivity.this);
                String url = AppConstant.API_SUBMIT_DATA + AppConstant.REQUEST_CODE + "=" + barcode.displayValue;
                apIcall.execute(url, APIcall.OPERATION_ID_SUBMIT_DATA, HomeActivity.this);
            } else {
                Common.displayToastMessageShort(HomeActivity.this, AppConstant.CONNECTION_ERROR_MSG, false);
            }
        }
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

    private void resetValues() {
        llResponse.setVisibility(View.GONE);
        TextView tvScanValue = findViewById(R.id.tvQr_BarcodeValue);
        tvScanValue.setText("");
        TextView tvResponseValue = findViewById(R.id.tvResponseValue);
        tvResponseValue.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data.hasExtra("barcode_value")) {
                barcode = (Barcode) data.getExtras().get("barcode_value");
                if (barcode != null) {
                    String scanValue = barcode.displayValue;//.getSerializableExtra("barcode");
                    TextView tvScanValue = findViewById(R.id.tvQr_BarcodeValue);
                    if (!TextUtils.isEmpty(scanValue)) {
                        tvScanValue.setText("Scan Value: " + scanValue);
                        tvScanValue.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.VISIBLE);
                        llResponse.setVisibility(View.VISIBLE);
                    } else {
                        btnSubmit.setVisibility(View.GONE);
                        tvScanValue.setVisibility(View.GONE);
                    }
                }
            }
        }
    }


    @Override
    public void onStartLoading(int operationCode) {
        if (APIcall.OPERATION_ID_SUBMIT_DATA == operationCode) {

        }
    }

    @Override
    public void onProgress(int operationCode, int progress) {

    }

    @Override
    public void onSuccess(int operationCode, String response, Object customData) {
        if (APIcall.OPERATION_ID_SUBMIT_DATA == operationCode) {
            String message;
            try {
                JSONObject jsonObject = new JSONObject(response);
                int status = jsonObject.optInt(AppConstant.RESPONSE_STATUS, 0);
                if (status == 1) {
                    TextView tvResponseValue = findViewById(R.id.tvResponseValue);
                    tvResponseValue.setText(response);
                } else {
                    //showProgress(false);
                    message = jsonObject.optString(AppConstant.RESPONSE_MESSAGE, "");
                    Common.displayToastMessageShort(HomeActivity.this, message, false);
                }
            } catch (Exception e) {
                //showProgress(false);
                Common.displayToastMessageShort(HomeActivity.this, "Error in data submit", false);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFail(int operationCode, String response) {

    }
}
