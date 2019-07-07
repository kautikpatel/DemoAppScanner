package app.com.Managers.Communications;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import app.com.Managers.SharedPreferenceManagerFile;
import app.com.Utils.AppConstant;
import app.com.Utils.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIcall {

    public static int OPERATION_ID_LOGIN = 100;
    public static int OPERATION_ID_SUBMIT_DATA = 101;
    public static int OPERATION_ID_GET_SCAN_DATA = 102;
    public static int OPERATION_ID_GET_DEPT_LIST = 103;
    public static int OPERATION_ID_UPDATE_RECORD = 104;
    public static int OPERATION_ID_GET_REPORT = 105;
    private Context moContext;
    private ApiCallListner apiCallListner;
    private int operationCode;
    private String url;
    private CallApiExecute callApiExecute;

    public APIcall(Context context) {
        moContext = context;
    }

    public void execute(String url, int operationCode, ApiCallListner apiCallListner) {
        this.operationCode = operationCode;
        this.url = url;
        this.apiCallListner = apiCallListner;
        this.apiCallListner.onStartLoading(operationCode);
        callApiExecute = new CallApiExecute();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            callApiExecute.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            callApiExecute.execute();
    }

    public interface ApiCallListner {
        void onStartLoading(int operationCode);

        void onProgress(int operationCode, int progress);

        void onSuccess(int operationCode, String response, Object customData);

        void onFail(int operationCode, String response);
    }

    private class CallApiExecute extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            /*if (!url.contains("http")) {
                url = AppConstant.BASE_URL + url;
            }*/
            SharedPreferenceManagerFile sharedPref = new SharedPreferenceManagerFile(moContext);
            String ipAddress = sharedPref.getFromSharedPreference(SharedPreferenceManagerFile.PREFERENCE_IP_ADDRESS);
            String portNo = sharedPref.getFromSharedPreference(SharedPreferenceManagerFile.PREFERENCE_PORT_NO);
            String prefixUrl = "http://" + ipAddress + ":" + portNo + AppConstant.BASE_URL;
            url = prefixUrl + url;
            String userAgent = System.getProperty("http.agent");
            Log.i("userAgent", "userAgent:::" + userAgent);
            Log.i("CallApiExecute", "API Url:" + url);
            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
            okHttpBuilder.connectTimeout(60000, TimeUnit.MILLISECONDS);
            okHttpBuilder.readTimeout(60000, TimeUnit.MILLISECONDS);
            OkHttpClient client = okHttpBuilder.build();//new OkHttpClient(okHttpBuilder);
            //client.networkInterceptors().add(new UserAgentInterceptor("fPrLeimKr0uF3"));
            Request request = new Request.Builder()
                    .url(url)/*.post(formBody)*/
                    .header("userAgent", userAgent)
                    .build();
            Response response = null;
            String responseStr = null;
            try {
                response = client.newCall(request).execute();
                responseStr = response.body().string();
                Log.i("CallApiExecute", "CallApiExecute Response got");
                return responseStr;
            } catch (IOException e) {
                Log.i("CallApiExecute", "Exception Message:" + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (!TextUtils.isEmpty(result)) {
                apiCallListner.onSuccess(operationCode, result, null);
            } else {
                apiCallListner.onFail(operationCode, null);
            }
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            if (apiCallListner != null) {
                apiCallListner.onProgress(operationCode, Integer.parseInt(progress[0]));
            }
            super.onProgressUpdate(progress);
        }
    }
}
