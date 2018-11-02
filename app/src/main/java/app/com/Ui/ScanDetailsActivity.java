package app.com.Ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import app.com.Managers.Communications.APIcall;
import app.com.Managers.SharedPreferenceManagerFile;
import app.com.Utils.AppConstant;
import app.com.Utils.Common;
import app.com.myapp.R;

public class ScanDetailsActivity extends BaseActivity implements View.OnClickListener, APIcall.ApiCallListner {

    private String barcodeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_details);
        setToolbar();
        //setNavigationPanel();
        setActionBarTitle(getResources().getString(R.string.title_details));
        initializeComponents();
    }

    private void initializeComponents() {
        findViewById(R.id.btnUpdate).setOnClickListener(this);
        Bundle b = getIntent().getExtras();
        if (b == null) {
            finish();
            return;
        }

        if (!b.containsKey("scanCode")) {
            finish();
            return;
        }

        barcodeValue = b.getString("scanCode", "");
        if (TextUtils.isEmpty(barcodeValue)) {
            finish();
            return;
        }
        loadData();
    }

    private void loadData() {
        SharedPreferenceManagerFile sharedPref = new SharedPreferenceManagerFile(ScanDetailsActivity.this);
        String userId = sharedPref.getFromSharedPreference(SharedPreferenceManagerFile.PREFERENCE_USER_ID);
        if (Common.isInternetAvailable(ScanDetailsActivity.this)) {
            APIcall apIcall = new APIcall(ScanDetailsActivity.this);
            String url = AppConstant.API_SEARCH_FILE + AppConstant.REQUEST_BARCODE + "=" + barcodeValue + "&" + AppConstant.REQUEST_USER_ID + "=" + userId;
            apIcall.execute(url, APIcall.OPERATION_ID_GET_SCAN_DATA, ScanDetailsActivity.this);
        } else {
            Common.displayToastMessageShort(ScanDetailsActivity.this, AppConstant.CONNECTION_ERROR_MSG, false);
            finish();
            return;
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnUpdate) {
            Intent intent = new Intent(ScanDetailsActivity.this, EditDetailsActivity.class);
            intent.putExtra("file_details", strFileDetails);
            startActivityForResult(intent, 1000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                loadData();
            }
        }
    }

    private ProgressDialog dialog;

    private void showDialog() {
        dialog = new ProgressDialog(ScanDetailsActivity.this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
    }

    private void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onStartLoading(int operationCode) {
        if (APIcall.OPERATION_ID_GET_SCAN_DATA == operationCode) {
            showDialog();
        }
    }

    @Override
    public void onProgress(int operationCode, int progress) {

    }

    @Override
    public void onSuccess(int operationCode, String response, Object customData) {
        if (APIcall.OPERATION_ID_GET_SCAN_DATA == operationCode) {
            String message;
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("data")) {
                    JSONObject jsonObjectData = jsonObject.optJSONObject("data");
                    JSONObject jsonObjectFileDetails = jsonObjectData.optJSONObject("file_details");
                    fillUpData(jsonObjectFileDetails);
                    findViewById(R.id.llDetails).setVisibility(View.VISIBLE);
                } else {
                    JSONObject jsonObjectError = jsonObject.optJSONObject("error");
                    if (jsonObjectError != null) {
                        message = jsonObjectError.optString("desc", "");
                        Common.displayToastMessageShort(ScanDetailsActivity.this, message, false);
                        finish();
                    }
                }
            } catch (Exception e) {
                Common.displayToastMessageShort(ScanDetailsActivity.this, "Error in get details.", false);
                e.printStackTrace();
            }
        }
        hideDialog();
    }

    @Override
    public void onFail(int operationCode, String response) {
        hideDialog();
    }

    private String strFileDetails;

    private void fillUpData(JSONObject jsonFileDetails) {
        strFileDetails = jsonFileDetails.toString();
        TextView tvTagCodeValue = findViewById(R.id.tvTagCodeValue);
        tvTagCodeValue.setText(jsonFileDetails.optString("tag_code", ""));
        TextView tvBarcodeValue = findViewById(R.id.tvBarcodeValue);
        tvBarcodeValue.setText(jsonFileDetails.optString("file_barcode", ""));
        TextView tvFileNameValue = findViewById(R.id.tvFileNameValue);
        tvFileNameValue.setText(jsonFileDetails.optString("file_name", ""));
        TextView tvDeptValue = findViewById(R.id.tvDeptValue);
        tvDeptValue.setText(jsonFileDetails.optString("dept_name", ""));
        TextView tvRoomNoValue = findViewById(R.id.tvRoomNoValue);
        tvRoomNoValue.setText(jsonFileDetails.optString("room_table_no", ""));
        TextView tvFileStatusValue = findViewById(R.id.tvFileStatusValue);
        tvFileStatusValue.setText(jsonFileDetails.optString("file_status", ""));
        TextView tvDueDateValue = findViewById(R.id.tvDueDateValue);
        tvDueDateValue.setText(jsonFileDetails.optString("process_time", ""));
        TextView tvRemarkValue = findViewById(R.id.tvRemarkValue);
        tvRemarkValue.setText(jsonFileDetails.optString("remarks", ""));
        String actionRequire = jsonFileDetails.optString("action_required", "");
        TextView tvActionValue = findViewById(R.id.tvActionValue);
        if (!TextUtils.isEmpty(actionRequire)) {
            tvActionValue.setText(actionRequire);
            findViewById(R.id.llActionRequire).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.llActionRequire).setVisibility(View.GONE);
        }
    }
}
