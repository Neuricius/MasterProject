package com.neuricius.masterproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.neuricius.masterproject.R;

import static com.neuricius.masterproject.util.UtilTools.sharedPrefNoSplashScreen;

public class SplashScreenActivity extends AppCompatActivity {

    private int interval = 2000;

    private Handler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //if nesto true onda
        if (sharedPrefNoSplashScreen(this)) {
            setupSplashScreen();
        } else {
            goToSearchActivity();
        }

    }

    private void setupSplashScreen(){
        myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToSearchActivity();
            }
        }, interval);
    }

    private void goToSearchActivity() {
        Intent intent = new Intent(SplashScreenActivity.this, SearchActorsActivity.class);
        startActivity(intent);
        finish();
    }
}
