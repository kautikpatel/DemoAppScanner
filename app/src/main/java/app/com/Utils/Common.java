package app.com.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.com.myapp.R;

public class Common {

    public static final long MILLIS_PER_SECOND = 1000;
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final long TIME_INTERVAL = 24 * MILLIS_PER_HOUR;

    public static String DIVIDER = "--KP--";
    public static String TAG = "Commen";

    public static boolean isInternetAvailable(Context foContext) {
        NetworkInfo loNetInfo = ((ConnectivityManager) foContext
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();

        if (loNetInfo != null)
            if (loNetInfo.isAvailable())
                if (loNetInfo.isConnected())
                    return true;

        return false;
    }

    public static long getTimeInMilliSecond(String givenDateString) {
        //String givenDateString = "Tue Apr 23 16:08:28 GMT+05:30 2013"; // String 12-Jan-2017 17:41
        SimpleDateFormat sdf = new SimpleDateFormat(/*"EEE MMM dd HH:mm:ss z yyyy"*/"dd-MMM-yyyy HH:mm");
        try {
            Date mDate = sdf.parse(givenDateString);
            long timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
            return timeInMilliseconds;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getConvertedDate(String date) {
        //String date = "2011/11/12 16:05:06";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date testDate = null;
            try {
                testDate = sdf.parse(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            String newFormat = formatter.format(testDate);
            System.out.println(".....Date..." + newFormat);
            return newFormat;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }


    public static String getCurrentDateTimeString(boolean paramBoolean) {
        Date localDate = new Date(System.currentTimeMillis());
        String str1;
        str1 = "MM/dd/yyyy'T'hh:mm:ss" + ".SSS";
        try {
            while (true) {
                String str2 = new SimpleDateFormat(str1).format(localDate);
                return str2;
                //str1 = "MM/dd/yyyy'T'hh:mm:ss" + " a";
            }
        } catch (Exception localException) {
        }
        return localDate.toString();
    }

    public static String getDateFormat(long paramLong) {
        Date localDate = new Date(paramLong);
        try {
            String str = new SimpleDateFormat("dd/MM/yyyy").format(localDate);
            return str;
        } catch (Exception localException) {
        }
        return localDate.toString();
    }

    public static String getDateTimeString(long fsTimeInmillis) {
        Date loToday = new Date(fsTimeInmillis);
        String lsDateFormat = "dd-MMM-yyyy  hh:mm:ss";

        lsDateFormat += " a";
        try {
            return new SimpleDateFormat(lsDateFormat).format(loToday);
        } catch (Exception e) {
            return loToday.toString();
        }
    }

    public static String getFormattedDateTime(long foTimeInMillis) {
        String seconds = ((foTimeInMillis / 1000) % 60) + "";
        String minutes = (((foTimeInMillis / (1000 * 60)) % 60)) + "";
        String hours = (((foTimeInMillis / (1000 * 60 * 60)) % 24)) + "";
        if (seconds.length() == 1) {
            seconds = "0" + seconds;
        }
        if (minutes.length() == 1) {
            minutes = "0" + minutes;
        }
        if (hours.length() == 1) {
            hours = "0" + hours;
        }
        return hours + ":" + minutes + ":" + seconds;
    }

    public static String getString(InputStream paramInputStream)
            throws IOException {
        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
        StringBuffer localStringBuffer = new StringBuffer();
        char[] arrayOfChar = new char[2048];
        for (int i = localBufferedReader.read(arrayOfChar); ; i = localBufferedReader.read(arrayOfChar)) {
            if (i <= 0)
                return localStringBuffer.toString();
            localStringBuffer.append(arrayOfChar, 0, i);
        }
    }


    public static String getTimeFormat(long fsTimeInmillis) {
        Date loToday = new Date(fsTimeInmillis);
        String lsDateFormat = "hh:mm";
        lsDateFormat += " a";
        try {
            return new SimpleDateFormat(lsDateFormat).format(loToday);
        } catch (Exception e) {
            return loToday.toString();
        }
    }

    public static String getFormattedDateTime1(long foTimeInMillis) {

        String seconds = ((foTimeInMillis / 1000) % 60) + "";
        String minutes = (((foTimeInMillis / (1000 * 60)) % 60)) + "";
        String hours = (((foTimeInMillis / (1000 * 60 * 60)) % 24)) + "";
        if (seconds.length() == 1) {
            seconds = "0" + seconds;
        }
        if (minutes.length() == 1) {
            minutes = "0" + minutes;
        }
        if (hours.length() == 1) {
            hours = "0" + hours;
        }
        return hours + ":" + minutes;
    }

    public static Toast toast;

    public static void displayToastMessageShort(Context context, String msg, boolean isLong) {
        if (toast == null)
            toast = Toast.makeText(context, msg, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        else {
            toast.setText(msg);
        }
        toast.show();
    }



    public static String getDeviceId(Context context) {
        return Settings.Secure
                .getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void loadImage(String imageUri, final ImageView imageView, final int defaultImg) {
        if(TextUtils.isEmpty(imageUri)){
            imageView.setImageResource(defaultImg);
            return;
        }
        initOptionForImage();
        /*ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(imageUri, imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                imageView.setImageResource(defaultImg);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                imageView.setImageResource(defaultImg);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imageView.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                imageView.setImageResource(defaultImg);
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
            }
        });*/
    }

    //private static DisplayImageOptions options;

    public static void initOptionForImage() {
        /*if (options == null)
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new RoundedBitmapDisplayer(20))
                    .build();*/
    }


    public static void showDialogMessage(Activity a, String msg, String txtPositiveBtn,
                                         View.OnClickListener onPositiveButtonClick,
                                         String txtNagativeBtn,
                                         View.OnClickListener onNegativeButtonClick) {
        /*CustomAlertDialog customAlertDialog = new CustomAlertDialog(a, msg, txtPositiveBtn, onPositiveButtonClick, txtNagativeBtn, onNegativeButtonClick);
        customAlertDialog.setCancelable(false);
        customAlertDialog.show();*/
    }

    public static void changeImageColorToPrimaryColor(ImageView imgBtn, Activity activity){
        final Drawable upArrow = imgBtn.getDrawable();//activity.getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.mutate().setColorFilter(activity.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        imgBtn.setImageDrawable(upArrow);
    }

    public static AlertDialog dialog;

    /*public static void showNotification(final Context context, CouponCampPrams couponCampPram) {
        if (false) {
            // Show Dialog for Check In
            *//*PackageManager pm = context.getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
            context.startActivity(intent);*//*
           *//* if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(CheckInViewFull.Instance.getContext());
            builder.setTitle("Check-In");
            builder.setMessage("Check-In today and Get " + checkInAmount + " Rs");

            String positiveText = "Check-In";
            builder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(CheckInViewFull.Instance.getContext(), CheckInActivity.class);
                            CheckInViewFull.Instance.getContext().startActivity(intent);
                        }
                    });

            String negativeText = "Cancel";
            builder.setNegativeButton(negativeText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();*//*
        } else {
            // Show Notification for Check In
            PackageManager pm = context.getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
            intent.putExtra(AppConstant.NOTI_KEY_TITLE,couponCampPram.coupon_title);
            intent.putExtra(AppConstant.NOTI_KEY_IS_FROM, true);
            int requestID = (int) System.currentTimeMillis();
            PendingIntent pintent = PendingIntent.getActivity(context, requestID,
                    intent, 0);
            //ImageView ivAppIcon = null;
            Bitmap icon = null;
            try {
                Drawable drawable = context.getPackageManager().getApplicationIcon(context.getPackageName());
                icon = drawableToBitmap(drawable);
                //ivAppIcon.setImageDrawable(icon);
            } catch (Exception e) {
                //ivAppIcon.setImageResource(R.drawable.ic_check_in_app_holder);
                e.printStackTrace();
            }

            ApplicationInfo ai;
            try {
                ai = pm.getApplicationInfo(context.getPackageName(), 0);
            } catch (final Exception e) {
                ai = null;
            }
            String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");

            //ImageView ivFMR = null;
            //ivFMR.setImageResource(R.drawable.icon_fmr);
            int iconId = R.drawable.ic_launcher_final;
            long when = System.currentTimeMillis();
            Notification notification = new Notification(iconId, "Custom Notification", when);

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
            if (icon != null)
                contentView.setImageViewBitmap(R.id.image, icon);
            else
                contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher_final);
            contentView.setTextViewText(R.id.tv_check_in_amount, couponCampPram.coupon_code);

            contentView.setTextViewText(R.id.title, couponCampPram.coupon_title);
            contentView.setTextViewText(R.id.text, couponCampPram.coupon_code);
            notification.contentView = contentView;
            notification.contentIntent = pintent;

            notification.flags |= Notification.FLAG_AUTO_CANCEL; //Do not clear the notification
            notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
            notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
            notification.defaults |= Notification.DEFAULT_SOUND; // Sound
            mNotificationManager.notify(AppAlarmReceiver.CHECK_IN_NOTIFICATION_ID, notification);
        }
        SharedPreferenceManagerFile sharedPreferenceManagerFile = new SharedPreferenceManagerFile(context);
        sharedPreferenceManagerFile.setLastNotificationViewTime(Calendar.getInstance().getTimeInMillis());
    }*/

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static boolean isEmailValid(String email) {
        return (email.matches(AppConstant.EMAIL_PATTERN));
    }

}