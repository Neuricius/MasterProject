package com.neuricius.masterproject.async;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.neuricius.masterproject.R;
import com.neuricius.masterproject.util.UtilTools;

import static com.neuricius.masterproject.util.UtilTools.getConnectionType;


public class NetworkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        if("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {

            if(!context.getString(R.string.conn_type_wifi).equals(getConnectionType(context))){
                UtilTools.sharedPrefNotify(context, "No WiFi", "Not connected to WiFi");
            } else {
                UtilTools.sharedPrefNotify(context, "WiFi", "Connected to WiFi");
            }
        }

    }
}
