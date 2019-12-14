package com.neuricius.masterproject.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import androidx.core.app.NavUtils;
import androidx.preference.PreferenceFragment;

import com.neuricius.masterproject.R;
import com.neuricius.masterproject.async.NetworkStateReceiver;

import static com.neuricius.masterproject.util.UtilTools.setUpReceiver;

public class SettingsActivity extends PreferenceActivity {

    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsActivity.PrefsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpReceiver(SettingsActivity.this, networkStateReceiver);

    }

    @Override
    protected void onPause() {
        if(networkStateReceiver != null) {
            unregisterReceiver(networkStateReceiver);
            networkStateReceiver = null;
        }
        super.onPause();
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        }
    }
}
