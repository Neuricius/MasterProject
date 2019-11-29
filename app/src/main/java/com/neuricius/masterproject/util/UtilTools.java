package com.neuricius.masterproject.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.neuricius.masterproject.R;
import com.neuricius.masterproject.async.SimpleReceiver;
import com.neuricius.masterproject.async.SimpleService;
import com.neuricius.masterproject.dialog.AboutDialog;
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
    private static SharedPreferences sharedPreferences;

    private static SimpleReceiver sync;
    private static PendingIntent pintent;
    private static AlarmManager alarm;

    public static final String AUTHOR_NAME = "Nebojsa Matic";

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

    public static String getConnectionType(Integer type){
        switch (type){
            case 1:
                return "WIFI";
            case 2:
                return "Mobilni internet";
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

    public static void sharedPrefNotify(Context context, String msg){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean toast = sharedPreferences.getBoolean(NOTIFY_TOAST, false);
        boolean status = sharedPreferences.getBoolean(NOTIFY_STATUS, false);

        if (toast){
            showToast(context, msg);
        }

        if (status){
            showStatusMesage(context, msg);
        }
    }

    public static boolean sharedPrefWarnNoWifi(Activity activity){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        boolean conn = sharedPreferences.getBoolean(CONN_ONLY_WIFI, false);
        boolean warn = sharedPreferences.getBoolean(WARN_NO_WIFI, false);

        if (conn && warn && getConnectivityStatus(activity) != TYPE_WIFI){
            sharedPrefNotify(activity, activity.getResources().getString(R.string.not_on_wifi));
            return true;
        }

        return false;
    }


    public static void showToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showStatusMesage(Context context, String message){
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_pic_error_foreground);
        mBuilder.setContentTitle("Master Project");
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
    public static void setUpSimpleReceiver(Activity activity, Integer minutes, Integer seconds){
        sync = new SimpleReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CHECK_CONN);
        activity.registerReceiver(sync, filter);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);


//        consultPreferences();
        setUpManager(activity, minutes, seconds);
    }

    private static void setUpManager(Activity activity, Integer minutes, Integer seconds){
        Intent intent = new Intent(activity, SimpleService.class);
        int status = getConnectivityStatus(activity);
        intent.putExtra(CONN_TYPE, status);

        //allowSync i synctime su id opcija iz SharedPref
        //allowsync boolean da li je dozvoljena autosinhronizacija
        //synctime je String koji oznacava interval u minutima
        boolean allowSync = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(activity.getResources().getString(R.string.autosync), false);
        if (allowSync) {
            pintent = PendingIntent.getService(activity, 0, intent, 0);
            alarm = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    calculateTimeTillNextSync(minutes, seconds),
                    pintent);

            sharedPrefNotify(activity, "Alarm Set");
        }
    }

    public static void killSimpleService(Activity activity){
        if (alarm != null) {
            alarm.cancel(pintent);
            alarm = null;
        }
        if(sync != null){
            activity.unregisterReceiver(sync);
            sync = null;
        }
    }

    /**ukoliko smo koristili servise i alarm manager
     * neophodno je osloboditi resurse prilikom odlaska
     * aktivnosti u drugi plan. Ovaj kod ide u aktivnost
     * koja je zvala servis/alarm mngr
     **/
//    @Override
//    protected void onPause() {
//        if (manager != null) {
//            manager.cancel(pendingIntent);
//            manager = null;
//        }
//
//
//        if(sync != null){
//            unregisterReceiver(sync);
//            sync = null;
//        }
//
//        super.onPause();
//
//    }
    /*******/
}
