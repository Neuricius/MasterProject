package com.neuricius.masterproject.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.ListPreference;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.neuricius.masterproject.R;
import com.neuricius.masterproject.async.NetworkStateReceiver;
import com.neuricius.masterproject.dialog.AboutDialog;
import com.neuricius.masterproject.dialog.CustomDialog;
import com.neuricius.masterproject.net.model.Cast;
import com.neuricius.masterproject.net.model.Credit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class UtilTools {

    private static AlertDialog dialog;
    private static CustomDialog customDialog;
    private static SharedPreferences sharedPreferences;

    private static PendingIntent pintent;
    private static AlarmManager alarm;

    public static final String NOTIFY_TOAST = "notifyByToast";
    public static final String NOTIFY_STATUS = "notifyByStatusBar";
    public static final String CONN_ONLY_WIFI = "connOnlyWifi";
    public static final String WARN_NO_WIFI = "warnNoWifi";

    public static final String ACTOR_ID = "idActor";
    public static final String MOVIE_ID = "idMovie";

    public static final String INTENT_ORIGIN = "origin";
    public static final String INTENT_ORIGIN_NET = "net";
    public static final String INTENT_ORIGIN_DATABASE = "db";

    public static final String TMDB_APIKEY_PARAM_NAME = "api_key";
    public static final String TMDB_QUERY_PARAM_NAME = "query";
    public static final String TMDB_API_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/original";

    public static final String LOG_TAG_SQL_EXCEPTION = "SQL EXCEPTION";

    public static final String ACTION_CHECK_CONN = "com.neuricius.CHECK_CONNECTION_TYPE";
    public static final String CONN_TYPE = "com.neuricius.CONNECTION_TYPE";

    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectionType(Context context){
        int type = getConnectivityStatus(context);
        switch (type){
            case 1:
                return context.getString(R.string.conn_type_wifi);
            case 2:
                return context.getString(R.string.conn_type_cell_data);
            default:
                return "";
        }
    }

    public static int calculateTimeTillNextSync(int minutes, int seconds){
        int resultTime = 0;
        if(minutes != 0){
            resultTime += 1000 * 60 * minutes;
        }
        if(seconds != 0){
            resultTime += 1000 * seconds;
        }
        return resultTime;
    }

    public static String returnGender(Integer genderID) {
        switch (genderID) {
            case 1:
                return "Female";
            case 2:
                return "Male";
            default:
                return "N/A";
        }
    }

    public static void showAboutDialog(Context context){
        if (dialog == null){
            dialog = new AboutDialog(context).prepareDialog();
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        dialog.show();
    }

    /**Rad sa shared preferences**/
    public static void sharedPrefNotify(Context context, String title, String msg){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


        int notifyType = Integer.parseInt(sharedPreferences.getString(context.getResources().getString(R.string.notify_user_list), "1"));

        switch (notifyType) {
            case 1:
                showToast(context, title, msg);
                break;
            case 2:
                showStatusMesage(context, title, msg);
                break;
            case 3:
                new CustomDialog(context, title, msg);
                break;
        }
    }

    public static boolean sharedPrefWarnNoWifi(Activity activity){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        return !sharedPreferences.getBoolean(WARN_NO_WIFI, false);
    }

    public static boolean sharedPrefPreserveData(Activity activity) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        return !sharedPreferences.getBoolean(CONN_ONLY_WIFI, false) && getConnectivityStatus(activity) == TYPE_WIFI;
    }


    public static boolean sharedPrefNoSplashScreen(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean showSplash = sharedPreferences.getBoolean(context.getResources().getString(R.string.splash_screen_on), false);
        return showSplash;
    }
    /** **/


    public static void showToast(Context context, String title, String text){
        Toast.makeText(context, title + ": " +text, Toast.LENGTH_SHORT).show();
    }

    public static void showStatusMesage(Context context, String title, String message){
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "1");
        mBuilder.setSmallIcon(R.drawable.ic_pic_error_foreground);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);

        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_pic_error_foreground);

        mBuilder.setLargeIcon(bm);
        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    /**Rad sa teksutalnim fajlovima u android-u**/
    public static void writeToFile(String data, Context context, String filename) {
        try {
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readFromFile(Context context, String file) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(file);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static List<Cast> getCastfromJson(String file) {
        Gson gson = new Gson();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            Credit result = gson.fromJson(br, Credit.class);
            if (result != null) {
                return result.getCast();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static boolean isFileExists(Context context, String filename){
        File file = new File(context.getFilesDir().getAbsolutePath() + "/" + filename);
        if(file.exists()){
            return true;
        }else{
            return false;
        }
    }
    /*******/

    /**Rad sa servisima**/
    public static void setUpReceiver(Context context, NetworkStateReceiver networkStateReceiver) {

        if(sharedPrefWarnNoWifi((Activity) context)) {
            networkStateReceiver = new NetworkStateReceiver();

            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");

            context.registerReceiver(networkStateReceiver, intentFilter);
        }
    }
    /*******/
}
