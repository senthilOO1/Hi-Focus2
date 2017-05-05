package com.deva.androiduser2.hi_focus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText usernameField,passwordField;
    private Button buttonField,guestButton;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static final String LOGIN_URL = "http://hifocuscctv.com/apps/Android/login.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_USERTYPE = "usertype";
    private static final String TAG_USERID = "userId";
    UserSessionManager session;

    String usernames,passwords;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new UserSessionManager(getApplicationContext());
        usernameField = (EditText)findViewById(R.id.email);
        passwordField = (EditText)findViewById(R.id.password);
        buttonField=(Button)findViewById(R.id.siginbutton);
        guestButton = (Button)findViewById(R.id.guestbutton);
        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(UserSessionManager.KEY_USERNAME);
        String password = user.get(UserSessionManager.KEY_PASSWORD);
        if((username != null) && (username != "null"))
        {
            usernameField.setText(username);
            passwordField.setText(password);

        }
        guestButton.setOnClickListener(this);

    }
    public void login(View view){
        new AttemptLogin().execute();
        usernames = usernameField.getText().toString();
        passwords = passwordField.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.guestbutton:
                //session.createUserLoginSessionAsGuest("0");
                Log.e("HHH", "onClick: work" );
                Intent i = new Intent(LoginActivity.this, HIFOCUS.class);
                i.putExtra("EXTRA_LAUNCHED_BY_NOTIFICATION",true);
                startActivity(i);
                break;
        }
    }

    class AttemptLogin extends AsyncTask<String, String, String> {
        boolean failure = false;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... args) {
            int success;
            int userid;
            String usertype;
            String Name;
            HashMap<String,String> userdetailts = session.getUserDetails();
            String username = usernames;
            String password = passwords;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("products", createObject()));
                System.out.print("making request -- " + username + " - "+password);
                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);
                success = json.getInt(TAG_SUCCESS);
                usertype = json.getString(TAG_USERTYPE);
                //Name = json.getString("name");
                String userId = json.getString(TAG_USERID);
                System.out.print("json -- "+ json);
                if (success == 1) {
                    userid = json.getInt(TAG_USERID);
                    Name = json.getString("name");

                    session.createUserLoginSession(userId,username,password);
                    Intent i = new Intent(LoginActivity.this, HIFOCUS.class);
                    i.putExtra("EXTRA_LAUNCHED_BY_NOTIFICATION",true);
                    startActivity(i);
                    return json.getString(TAG_MESSAGE);
                }else{
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(LoginActivity.this, file_url, Toast.LENGTH_LONG).show();
            }

        }
    }
//    @Override
//    public void onBackPressed() {
//        Bundle extras = getIntent().getExtras();
//
//        boolean launchedFromNotif = false;
//
//        if (extras.containsKey("EXTRA_LAUNCHED_BY_NOTIFICATION")) {
//            launchedFromNotif = extras.getBoolean("EXTRA_LAUNCHED_BY_NOTIFICATION");
//        }
//
//        if (launchedFromNotif) {
//            // Launched from notification, handle as special case
//            Intent intent = new Intent(this, HIFOCUS.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//            startActivity(intent);
//            finish();
//        } else {
//            super.onBackPressed();
//        }
//    }
public String createObject() {
    JSONObject mainObj = null;

    try {
        JSONArray ja = new JSONArray();
        for (int i = 0; i < 2; i++) {
            JSONObject pnObj = new JSONObject();
            pnObj.put("ProductName", "senthil");
            pnObj.put("Description", "img");
            pnObj.put("Model", "senthil");
            pnObj.put("serialNumber", "img");
            pnObj.put("quantity", "senthil");

            ja.put(pnObj);
        }


        Log.v("Json oject", "===" + ja.toString());
        return ja.toString();
    } catch (JSONException ex) {
        ex.printStackTrace();
    }


    return null;
}
}
