package app.com.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import app.com.Managers.Communications.APIcall;
import app.com.Managers.SharedPreferenceManagerFile;
import app.com.Utils.AppConstant;
import app.com.Utils.Common;
import app.com.myapp.R;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements APIcall.ApiCallListner {

    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setToolbar();
        setActionBarTitle(getResources().getString(R.string.title_login));
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!Common.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (Common.isInternetAvailable(LoginActivity.this)) {
                Common.hideKeyboard(LoginActivity.this);
                APIcall apIcall = new APIcall(LoginActivity.this);
                String url = AppConstant.API_DO_LOGIN + AppConstant.REQUEST_EMAIL + "=" + email + "&" + AppConstant.REQUEST_PASSWORD + "=" + password;
                apIcall.execute(url, APIcall.OPERATION_ID_LOGIN, LoginActivity.this);
            } else {
                Common.displayToastMessageShort(LoginActivity.this, AppConstant.CONNECTION_ERROR_MSG, false);
            }
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onStartLoading(int operationCode) {
        showProgress(true);
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

                    SharedPreferenceManagerFile sharedPref = new SharedPreferenceManagerFile(LoginActivity.this);
                    sharedPref.setInSharedPreference(SharedPreferenceManagerFile.PREFERENCE_USER_ID, user_id);
                    sharedPref.setInSharedPreference(SharedPreferenceManagerFile.PREFERENCE_FIRST_NAME, first_name);
                    sharedPref.setInSharedPreference(SharedPreferenceManagerFile.PREFERENCE_LAST_NAME, last_name);
                    sharedPref.setInSharedPreference(SharedPreferenceManagerFile.PREFERENCE_USER_NAME, user_name);
                    sharedPref.setInSharedPreference(SharedPreferenceManagerFile.PREFERENCE_EMAIL_ID, email);
                    sharedPref.setInSharedPreference(SharedPreferenceManagerFile.PREFERENCE_PASSWORD, mPasswordView.getText().toString().trim());
                    sharedPref.setInSharedPreference(SharedPreferenceManagerFile.PREFERENCE_TYPE, type);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    //message = jsonObject.optString(AppConstant.RESPONSE_MESSAGE, "");
                    //Common.displayToastMessageShort(LoginActivity.this, message, false);
                    finish();
                } else {
                    jsonObject = jsonObjectOrg.optJSONObject("error");
                    message = jsonObject.optString("desc", "");
                    showProgress(false);
                    //message = jsonObject.optString(AppConstant.RESPONSE_MESSAGE, "");
                    Common.displayToastMessageShort(LoginActivity.this, message, false);
                }
            } catch (Exception e) {
                showProgress(false);
                Common.displayToastMessageShort(LoginActivity.this, "Error in Login. Please try again later", false);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFail(int operationCode, String response) {
        if (APIcall.OPERATION_ID_LOGIN == operationCode) {
            showProgress(false);
            Common.displayToastMessageShort(LoginActivity.this, "Error in Login. Please try again later", false);
        }
    }

}

