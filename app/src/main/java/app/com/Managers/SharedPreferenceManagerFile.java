package app.com.Managers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManagerFile {

    Context moContext;
    SharedPreferences moSharedPreferences;

    public static String APP_DATA = "app_data";
    public static String GCM_TOKEN = "gcm_token";
    public static String FILE_NAME = "AppConfig";

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
}
