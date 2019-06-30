package com.org.tkru.pestlibrary;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.org.tkru.pestlibrary.model.ConnectionDetector;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

    // splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    CoordinatorLayout coordinatorlayout;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    Boolean checkcondition=false;
    Boolean handle_setting=false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        coordinatorlayout = (CoordinatorLayout) findViewById(R.id.checkconnection);
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    if(checkcondition){
                        onBackPressed();
                    }else if(!checkcondition) {
                        Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(i);
                        // close this activity
                        SplashActivity.this.finish();
                    }else if(!handle_setting){
                        refresh(savedInstanceState);
                    }
                }
            }, SPLASH_TIME_OUT);
        }else{
            // Ask user to connect to Internet
            Snackbar snackbar = Snackbar
                    .make(coordinatorlayout, "No internet connection ", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                            handle_setting=true;
                        }
                    });
            TextView snackbarActionTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_action);
            snackbarActionTextView.setTextSize(14);

            snackbarActionTextView.setTextColor(Color.RED);
            snackbarActionTextView.setTypeface(snackbarActionTextView.getTypeface(), Typeface.BOLD);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            textView.setMaxLines(1);
            textView.setTextSize(14);
            textView.setSingleLine(true);
            textView.setTypeface(null, Typeface.BOLD);
            snackbar.show();
        }
    }
    @Override
    public void onBackPressed() {
            checkcondition=true;
            SplashActivity.this.finish();
    }
    public void refresh(Bundle view){          //refresh is onClick name given to the button
        onRestart();
    }
    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        Intent i = new Intent(SplashActivity.this, SplashActivity.class);
        startActivity(i);
        finish();
    }
}
