package app.com.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import org.json.JSONObject;

import app.com.Managers.Communications.APIcall;
import app.com.Managers.SharedPreferenceManagerFile;
import app.com.Utils.AppConstant;
import app.com.Utils.Common;
import app.com.myapp.R;

/**
 * A login screen that offers login via email/password.
 */
public class SplashActivity extends AppCompatActivity implements APIcall.ApiCallListner {

    private Handler handler = new Handler();
    private Runnable runnable;
    private SharedPreferenceManagerFile sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPref = new SharedPreferenceManagerFile(SplashActivity.this);
        String emailId = sharedPref.getFromSharedPreference(SharedPreferenceManagerFile.PREFERENCE_EMAIL_ID);
        if (!TextUtils.isEmpty(emailId)) {
            findViewById(R.id.pb).setVisibility(View.VISIBLE);
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    attemptLogin();
                }
            }, 100);
        } else {
            findViewById(R.id.btn_staff_login).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_staff_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        addShortcut();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private void attemptLogin() {
        String email = sharedPref.getFromSharedPreference(SharedPreferenceManagerFile.PREFERENCE_EMAIL_ID);
        String password = sharedPref.getFromSharedPreference(SharedPreferenceManagerFile.PREFERENCE_PASSWORD);

        if (Common.isInternetAvailable(SplashActivity.this)) {
            Common.hideKeyboard(SplashActivity.this);
            APIcall apIcall = new APIcall(SplashActivity.this);
            String url = AppConstant.API_DO_LOGIN + AppConstant.REQUEST_EMAIL + "=" + email + "&" + AppConstant.REQUEST_PASSWORD + "=" + password;
            apIcall.execute(url, APIcall.OPERATION_ID_LOGIN, SplashActivity.this);
        } else {
            Common.displayToastMessageShort(SplashActivity.this, AppConstant.CONNECTION_ERROR_MSG, false);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    @Override
    public void onStartLoading(int operationCode) {
        findViewById(R.id.pb).setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgress(int operationCode, int progress) {

    }

    @Override
    public void onSuccess(int operationCode, String response, Object customData) {
        if (APIcall.OPERATION_ID_LOGIN == operationCode) {
            String message;
            try {
                //{"data":{"login_done":false},"error":{"code":1,"desc":"'email' is not set , 'password' is not set"}}
                JSONObject jsonObjectOrg = new JSONObject(response);
                JSONObject jsonObject = jsonObjectOrg.optJSONObject("data");
                boolean login_done = jsonObject.optBoolean("login_done", false);
                if (login_done) {
                    jsonObject = jsonObject.optJSONObject("user_details");
                    String user_id = jsonObject.optString("user_id", "");
                    String first_name = jsonObject.optString("first_name", "");
                    String last_name = jsonObject.optString("last_name", "");
                    String email = jsonObject.optString("email", "");
                    String user_name = jsonObject.optString("user_name", "");
                    String type = jsonObject.optString("type", "");

                    SharedPreferenceManagerFile sharedPref = new SharedPreferenceManagerFile(SplashActivity.this);
                    sharedPref.setInSharedPreference(SharedPreferenceManagerFile.PREFERENCE_USER_ID, user_id);
                    sharedPref.setInSharedPreference(SharedPreferenceManagerFile.PREFERENCE_FIRST_NAME, first_name);
                    sharedPref.setInSharedPreference(SharedPreferenceManagerFile.PREFERENCE_LAST_NAME, last_name);
                    sharedPref.setInSharedPreference(SharedPreferenceManagerFile.PREFERENCE_USER_NAME, user_name);
                    sharedPref.setInSharedPreference(SharedPreferenceManagerFile.PREFERENCE_EMAIL_ID, email);
                    //sharedPref.setInSharedPreference(SharedPreferenceManagerFile.PREFERENCE_PASSWORD, mPasswordView.getText().toString().trim());
                    sharedPref.setInSharedPreference(SharedPreferenceManagerFile.PREFERENCE_TYPE, type);
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    //message = jsonObject.optString(AppConstant.RESPONSE_MESSAGE, "");
                    //Common.displayToastMessageShort(LoginActivity.this, message, false);
                    finish();
                } else {
                    jsonObject = jsonObjectOrg.optJSONObject("error");
                    message = jsonObject.optString("desc", "");
                    //message = jsonObject.optString(AppConstant.RESPONSE_MESSAGE, "");
                    Common.displayToastMessageShort(SplashActivity.this, message, false);
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            } catch (Exception e) {
                Common.displayToastMessageShort(SplashActivity.this, "Error in Login. Please try again later", false);
                e.printStackTrace();
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
        findViewById(R.id.pb).setVisibility(View.GONE);
    }

    @Override
    public void onFail(int operationCode, String response) {
        if (APIcall.OPERATION_ID_LOGIN == operationCode) {
            Common.displayToastMessageShort(SplashActivity.this, "Error in Login. Please try again later", false);
        }
    }


    private void addShortcut() {
        /*Intent shortcutIntent = new Intent(getApplicationContext(), SplashActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);
        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        R.drawable.ic_launcher_));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);  //may it's already there so don't duplicate
        getApplicationContext().sendBroadcast(addIntent);*/
    }
}

