package app.com.Managers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManagerFile {

    private Context moContext;
    private SharedPreferences moSharedPreferences;
    public static String FILE_NAME = "AppConfig";
    public static final String PREFERENCE_EMAIL_ID = "email_address";
    public static final String PREFERENCE_PASSWORD = "password";
    public static final String PREFERENCE_USER_ID = "user_id";
    public static final String PREFERENCE_FIRST_NAME = "first_name";
    public static final String PREFERENCE_LAST_NAME = "last_name";
    public static final String PREFERENCE_USER_NAME = "user_name";
    public static final String PREFERENCE_TYPE = "type";

    public SharedPreferenceManagerFile(Context foContext) {
        moContext = foContext;
        try {
            moSharedPreferences = foContext.getApplicationContext().getSharedPreferences(FILE_NAME, 0 | Context.MODE_MULTI_PROCESS);
        } catch (Exception e) {
            moSharedPreferences = foContext.getApplicationContext().getSharedPreferences(FILE_NAME, 0);
        }
    }

    public void setInSharedPreference(String fsKey, String fsValue) {
        if (moSharedPreferences != null && fsValue != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(fsKey, fsValue);
            loEditor.commit();
        }
    }

    public String getFromSharedPreference(String fsKey) {

        if (moSharedPreferences != null) {
            return moSharedPreferences.getString(fsKey, null);
        }
        return null;
    }


    public void clearPreference() {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString(PREFERENCE_EMAIL_ID, "");
            loEditor.putString(PREFERENCE_PASSWORD, "");
            loEditor.putString(PREFERENCE_USER_ID, "");
            loEditor.putString(PREFERENCE_FIRST_NAME, "");
            loEditor.putString(PREFERENCE_LAST_NAME, "");
            loEditor.putString(PREFERENCE_USER_NAME, "");
            loEditor.putString(PREFERENCE_TYPE, "");
            loEditor.commit();
        }
    }

}
