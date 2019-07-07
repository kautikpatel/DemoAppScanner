package app.com.Ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import app.com.Managers.SharedPreferenceManagerFile;
import app.com.Utils.Common;
import app.com.myapp.R;

public class CustomIpAddressDialog extends Dialog {

    private Activity act;

    public CustomIpAddressDialog(Activity a) {
        super(a);
        this.act = a;
    }

    String ipAddress;
    EditText etIpAddress, etPornNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ip_address);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        etIpAddress = findViewById(R.id.etIpAddress);
        etPornNo = findViewById(R.id.etPornNo);
        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.finish();
            }
        });
        final Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnSubmit.getWindowToken(), 0);                ipAddress = etIpAddress.getText().toString().trim();
                if (!Common.isIpValid(ipAddress)) {
                    Common.displayToastMessageShort(act, "Please enter valid address", false);
                    return;
                }
                String portNo = etPornNo.getText().toString().trim();
                if (Common.isEmpty(portNo)) {
                    Common.displayToastMessageShort(act, "Please enter valid Port No", false);
                    return;
                }
                SharedPreferenceManagerFile sharedPref = new SharedPreferenceManagerFile(act);
                sharedPref.setInSharedPreference(SharedPreferenceManagerFile.PREFERENCE_IP_ADDRESS, ipAddress);
                sharedPref.setInSharedPreference(SharedPreferenceManagerFile.PREFERENCE_PORT_NO, portNo);
                dismiss();
            }
        });

        etIpAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        etIpAddress.requestFocus();
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        //InputMethodManager imm = (InputMethodManager) getwin.getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(etTeamName, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void dismiss() {
        try {
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etIpAddress.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.dismiss();
    }

    public String getIpAddress() {
        return ipAddress;
    }
}