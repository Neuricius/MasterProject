package com.neuricius.masterproject.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.neuricius.masterproject.R;

public class Contract {

    public static String BASE_URL= "https://api.themoviedb.org/3/";

//    public static final String API_KEY = "50038a6708b8a31d633d6e86190e6cd9";

    public static String getApiKey(Context context) {
        String apiKey;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        apiKey = sharedPreferences.getString(context.getString(R.string.tmdb_api_key), "");

        return apiKey;
    }

}
