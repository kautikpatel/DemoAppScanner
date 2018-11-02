package app.com.Ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.com.Managers.Communications.APIcall;
import app.com.Managers.SharedPreferenceManagerFile;
import app.com.Utils.AppConstant;
import app.com.Utils.Common;
import app.com.myapp.R;

public class EditDetailsActivity extends BaseActivity implements View.OnClickListener, APIcall.ApiCallListner {

    private ArrayList<String> deptNameList = new ArrayList<>();
    private ArrayList<String> deptIdList = new ArrayList<>();
    private String file_details;
    private String deptId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_details);
        setToolbar();
        setActionBarTitle(getResources().getString(R.string.title_update_details));
        initializeComponents();
    }

    private void initializeComponents() {
        findViewById(R.id.btnUpdate).setOnClickListener(this);
        Bundle b = getIntent().getExtras();
        if (b == null) {
            finish();
            return;
        }

        if (!b.containsKey("file_details")) {
            finish();
            return;
        }

        file_details = b.getString("file_details", "");
        if (TextUtils.isEmpty(file_details)) {
            finish();
            return;
        }

        try {
            fillUpData(new JSONObject(file_details));
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferenceManagerFile sharedPref = new SharedPreferenceManagerFile(EditDetailsActivity.this);
        String userId = sharedPref.getFromSharedPreference(SharedPreferenceManagerFile.PREFERENCE_USER_ID);
        if (Common.isInternetAvailable(EditDetailsActivity.this)) {
            APIcall apIcall = new APIcall(EditDetailsActivity.this);
            String url = AppConstant.API_GET_DEPT_LIST + AppConstant.REQUEST_USER_ID + "=" + userId;
            apIcall.execute(url, APIcall.OPERATION_ID_GET_DEPT_LIST, EditDetailsActivity.this);
        } else {
            Common.displayToastMessageShort(EditDetailsActivity.this, AppConstant.CONNECTION_ERROR_MSG, false);
            finish();
            return;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnUpdate) {
            String params = getUpdateUrl();
            if (!TextUtils.isEmpty(params)) {
                if (Common.isInternetAvailable(EditDetailsActivity.this)) {
                    APIcall apIcall = new APIcall(EditDetailsActivity.this);
                    String url = AppConstant.API_GET_UPDATE_RECORD + params;
                    url = url.replace(" ", "%20");
                    apIcall.execute(url, APIcall.OPERATION_ID_UPDATE_RECORD, EditDetailsActivity.this);
                } else {
                    Common.displayToastMessageShort(EditDetailsActivity.this, AppConstant.CONNECTION_ERROR_MSG, false);
                    finish();
                    return;
                }
            }
        }
    }

    private String getUpdateUrl() {
        EditText etFileNameValue = findViewById(R.id.etFileNameValue);
        String fileName = etFileNameValue.getText().toString().trim();

        EditText etRoomNoValue = findViewById(R.id.etRoomNoValue);
        String roomNo = etRoomNoValue.getText().toString().trim();

        EditText etRemarks = findViewById(R.id.etRemarkValue);
        String remarks = etRemarks.getText().toString().trim();

        String action = "";
        Spinner spFileStatusValue = findViewById(R.id.spFileStatusValue);
        String status = AppConstant.statusList[spFileStatusValue.getSelectedItemPosition()];
        if (spFileStatusValue.getSelectedItemPosition() == AppConstant.statusList.length - 1) {
            EditText etActionRequired = findViewById(R.id.etActionRequire);
            action = etActionRequired.getText().toString().trim();
        }

        if (TextUtils.isEmpty(fileName)) {
            Common.displayToastMessageShort(EditDetailsActivity.this, "Please enter File Name", false);
            return "";
        }

        if (TextUtils.isEmpty(roomNo)) {
            Common.displayToastMessageShort(EditDetailsActivity.this, "Please enter Room/Table Number", false);
            return "";
        }

        if (TextUtils.isEmpty(status)) {
            Common.displayToastMessageShort(EditDetailsActivity.this, "Please select valid File Status", false);
            return "";
        }

        if (TextUtils.isEmpty(remarks)) {
            Common.displayToastMessageShort(EditDetailsActivity.this, "Please enter remarks", false);
            return "";
        }

        Spinner spDeptValue = findViewById(R.id.spDeptValue);
        String deptId = deptIdList.get(spDeptValue.getSelectedItemPosition());

        String params = null;
        try {
            JSONObject jsonFileDetails = new JSONObject(file_details);
            SharedPreferenceManagerFile sharedPref = new SharedPreferenceManagerFile(EditDetailsActivity.this);
            params = "user_id=" + sharedPref.getFromSharedPreference(SharedPreferenceManagerFile.PREFERENCE_USER_ID);
            params += "&tag_code=" + jsonFileDetails.optString("tag_code", "");
            params += "&file_barcode=" + jsonFileDetails.optString("file_barcode", "");
            params += "&file_name=" + fileName;
            params += "&dept_id=" + deptId;
            params += "&room_table_no=" + roomNo;
            params += "&file_status=" + status;
            params += "&remarks=" + remarks;
            params += "&process_time=" + jsonFileDetails.optString("process_time", "");
            params += "&action_required=" + action;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

    private ProgressDialog dialog;

    private void showDialog() {
        dialog = new ProgressDialog(EditDetailsActivity.this);
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
        if (APIcall.OPERATION_ID_GET_DEPT_LIST == operationCode || APIcall.OPERATION_ID_UPDATE_RECORD == operationCode) {
            showDialog();
        }
    }

    @Override
    public void onProgress(int operationCode, int progress) {

    }

    @Override
    public void onSuccess(int operationCode, String response, Object customData) {
        if (APIcall.OPERATION_ID_GET_DEPT_LIST == operationCode) {
            String message;
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("data")) {
                    JSONObject jsonObjectData = jsonObject.optJSONObject("data");
                    boolean status = jsonObjectData.optBoolean("status", false);
                    if (status) {
                        int position = 0;
                        JSONArray jsonArray = jsonObjectData.optJSONArray("list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObj = jsonArray.optJSONObject(i);
                            String dept_name = jsonObj.optString("dept_name");
                            String id = jsonObj.optString("id");
                            deptNameList.add(dept_name);
                            deptIdList.add(id);
                            if (!TextUtils.isEmpty(deptId) && deptId.equals(id)) {
                                position = i;
                            }
                        }
                        setupDept(position);
                    }
                    findViewById(R.id.llDetails).setVisibility(View.VISIBLE);
                } else {
                    JSONObject jsonObjectError = jsonObject.optJSONObject("error");
                    if (jsonObjectError != null) {
                        message = jsonObjectError.optString("desc", "");
                        Common.displayToastMessageShort(EditDetailsActivity.this, message, false);
                    }
                }
            } catch (Exception e) {
                Common.displayToastMessageShort(EditDetailsActivity.this, "Error in get details.", false);
                e.printStackTrace();
            }
        } else if (operationCode == APIcall.OPERATION_ID_UPDATE_RECORD) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("data")) {
                    JSONObject jsonObjectData = jsonObject.optJSONObject("data");
                    boolean status = jsonObjectData.optBoolean("status", false);
                    if (status) {
                        Common.displayToastMessageShort(EditDetailsActivity.this, "Record updated successfully", false);
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else {
                        JSONObject jsonObjectError = jsonObject.optJSONObject("error");
                        if (jsonObjectError != null) {
                            String message = jsonObjectError.optString("desc", "");
                            Common.displayToastMessageShort(EditDetailsActivity.this, message, true);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        hideDialog();
    }

    @Override
    public void onFail(int operationCode, String response) {
        hideDialog();
    }

    private void fillUpData(JSONObject jsonFileDetails) {
        deptId = jsonFileDetails.optString("dept_id", "");
        TextView tvTagCodeValue = findViewById(R.id.tvTagCodeValue);
        tvTagCodeValue.setText(jsonFileDetails.optString("tag_code", ""));
        TextView tvBarcodeValue = findViewById(R.id.tvBarcodeValue);
        tvBarcodeValue.setText(jsonFileDetails.optString("file_barcode", ""));
        EditText etFileNameValue = findViewById(R.id.etFileNameValue);
        etFileNameValue.setText(jsonFileDetails.optString("file_name", ""));
        /*TextView tvDeptValue = findViewById(R.id.tvDeptValue);
        tvDeptValue.setText(jsonFileDetails.optString("dept_name", ""));*/
        EditText etRoomNoValue = findViewById(R.id.etRoomNoValue);
        etRoomNoValue.setText(jsonFileDetails.optString("room_table_no", ""));
        /*TextView tvFileStatusValue = findViewById(R.id.tvFileStatusValue);
        tvFileStatusValue.setText(jsonFileDetails.optString("file_status", ""));*/
        TextView tvDueDateValue = findViewById(R.id.tvDueDateValue);
        tvDueDateValue.setText(jsonFileDetails.optString("process_time", ""));
        final EditText etRemarkValue = findViewById(R.id.etRemarkValue);

        etRemarkValue.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (etRemarkValue.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

        final LinearLayout llActionRequire = findViewById(R.id.llActionRequire);
        Spinner spFileStatusValue = findViewById(R.id.spFileStatusValue);
        ArrayAdapter aa = new ArrayAdapter(this, R.layout.spinner_item, AppConstant.statusDisplayList);
        aa.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spFileStatusValue.setAdapter(aa);
        spFileStatusValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == AppConstant.statusList.length - 1) {
                    llActionRequire.setVisibility(View.VISIBLE);
                } else {
                    //EditText etActionRequired = findViewById(R.id.etActionRequire);
                    //etActionRequired.setText("");
                    llActionRequire.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

    }

    private void setupDept(int position) {
        Spinner spDeptValue = findViewById(R.id.spDeptValue);
        ArrayAdapter aa = new ArrayAdapter(this, R.layout.spinner_item, deptNameList);
        aa.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spDeptValue.setAdapter(aa);
        spDeptValue.setSelection(position);
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
