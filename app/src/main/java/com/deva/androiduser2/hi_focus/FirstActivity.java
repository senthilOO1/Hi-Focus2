package com.deva.androiduser2.hi_focus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class FirstActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    Context context;
    UserSessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        context = this;
        session = new UserSessionManager(getApplicationContext());
        session.logoutUser();
    }
    protected void onStart() {
        super.onStart();
          /*New Handler to start the Menu-Activity      and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                //Create an Intent that will start the Menu-Activity.
                Intent mIentent = new Intent(FirstActivity.this, ProductLogInActivity.class);
                startActivity(mIentent);
                finish();



            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
