package com.deva.androiduser2.hi_focus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class Description extends Activity implements View.OnClickListener {

    EditText description;

    TextView done, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_description);

        description = (EditText) findViewById(R.id.editText);

        done = (TextView) findViewById(R.id.Done);

        cancel = (TextView) findViewById(R.id.cancel);

        done.setOnClickListener(this);

        cancel.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Done:
                String desp = description.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("Desp", desp);
                setResult(3, intent);
                finish();
                break;
            case R.id.cancel:
                Intent i = new Intent();
                i.putExtra("Desp", "");
                setResult(3, i);
                finish();
                break;
        }
    }
}
