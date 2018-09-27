package app.com.Utils;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import app.com.myapp.BuildConfig;

public class Log {
    private static final String APP_NAME = "HomeApp";
    private static final int BUFFER_SIZE = 8168;
    private static final String LOG_FILE_NAME;
    public static Calendar loCal = Calendar.getInstance();
    public static boolean IS_DEBUG = BuildConfig.DEBUG;

    static {
        LOG_FILE_NAME = APP_NAME+"_" + loCal.get(Calendar.YEAR) + ".txt";
    }

    public static void csv(String paramString) {
        if(!IS_DEBUG)
            return;
        try {
            BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/" + "EasyLogger.csv", true), 8168);
            localBufferedWriter.append(Common.getCurrentDateTimeString(true) + "," + paramString + "\n");
            localBufferedWriter.close();
            return;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    public static BufferedWriter getBufferedWriter()
            throws IOException {
        return new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/" + LOG_FILE_NAME, true), 8168);
    }

    public static void i(String paramString1, String paramString2) {
        if(!IS_DEBUG)
            return;
        android.util.Log.i(paramString1, paramString2);
        try {
            BufferedWriter localBufferedWriter = getBufferedWriter();
            localBufferedWriter.append(Common.getCurrentDateTimeString(true) + "\t" + APP_NAME + "/" + paramString1 + "\t" + paramString2 + "\n");
            localBufferedWriter.close();
            return;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    public static void logException(Throwable paramThrowable) {
        paramThrowable.printStackTrace();
        try {
            BufferedWriter localBufferedWriter = getBufferedWriter();
            localBufferedWriter.append(Common.getCurrentDateTimeString(true) + "\t" + APP_NAME + "/Exception" + "\n" + android.util.Log.getStackTraceString(paramThrowable) + "\n");
            localBufferedWriter.close();
            return;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }
}
