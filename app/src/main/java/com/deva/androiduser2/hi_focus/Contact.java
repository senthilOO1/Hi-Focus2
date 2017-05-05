package com.deva.androiduser2.hi_focus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Contact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
    }
    @Override
    public void onBackPressed()
    {
        Bundle extras = getIntent().getExtras();

        boolean launchedFromNotif = false;

        if (extras.containsKey("EXTRA_LAUNCHED_BY_NOTIFICATION"))
        {
            launchedFromNotif = extras.getBoolean("EXTRA_LAUNCHED_BY_NOTIFICATION");
        }

        if (launchedFromNotif)
        {
            // Launched from notification, handle as special case
            Intent intent = new Intent(this, HIFOCUS.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
        else
        {
            super.onBackPressed();
        }
    }
}
