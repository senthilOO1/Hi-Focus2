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


public class ProductLogInActivity extends Activity implements View.OnClickListener {
    private EditText usernameField, passwordField;
    private Button buttonField, guestButton;
    private ProgressDialog pDialog;
    UserSessionManager session;

    String usernames, passwords;

    JSONParser jsonParser = new JSONParser();
    private static final String LOGIN_URL = "http://hifocuscctv.com/apps/Android/login.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_USERTYPE = "usertype";
    private static final String TAG_USERID = "userid";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        session = new UserSessionManager(getApplicationContext());

        usernameField = (EditText) findViewById(R.id.email);
        passwordField = (EditText) findViewById(R.id.password);
        buttonField = (Button) findViewById(R.id.siginbutton);
        guestButton = (Button) findViewById(R.id.guestbutton);
        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(UserSessionManager.KEY_USERNAME);
        String password = user.get(UserSessionManager.KEY_PASSWORD);
        if ((username != null) && (username != "null")) {
            //usernameField.setText(username);
            //passwordField.setText(password);
        }
        guestButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.guestbutton:
                session.createUserLoginSession("0", "kkkk", "222");
                Log.e("HHH", "onClick: work" );
                Intent i = new Intent(this, HIFOCUS.class);
                i.putExtra("EXTRA_LAUNCHED_BY_NOTIFICATION", true);
                startActivity(i);
                break;
        }
    }


    public void login(View view) {

        boolean valu = Utility.internetIsAvailable(this);

        if(valu == true){

            usernames = usernameField.getText().toString();
            passwords = passwordField.getText().toString();
            new AttemptLogin().execute();

        }else {
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();
        }

    }

    class AttemptLogin extends AsyncTask<String, String, String> {
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProductLogInActivity.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            String usertype;
            String Name;
            String username = usernames;
            String password = passwords;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                //String raw = "[{\"ProductName\":\"senthil\",\"Description\":\"img\",\"Model\":\"senthil\",\"serialNumber\":\"img\",\"quantity\":\"senthil\"},{\"ProductName\":\"senthil\",\"Description\":\"img\",\"Model\":\"senthil\",\"serialNumber\":\"img\",\"quantity\":\"senthil\"}]"
//                params.add(new BasicNameValuePair("products", createObject()));
//                params.add(new BasicNameValuePair())

                JSONObject user = createObject();
                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);
                success = json.getInt(TAG_SUCCESS);
                usertype = json.getString(TAG_USERTYPE);
                //Name = json.getString("name");
                String userId = json.getString(TAG_USERID);

                if (success == 1) {
                    session.createUserLoginSession(userId, username, password);
                    Intent i = new Intent(ProductLogInActivity.this, HIFOCUS.class);
                    startActivity(i);
                    return json.getString(TAG_MESSAGE);
                } else {

                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(ProductLogInActivity.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }

    public JSONObject createObject() {
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

            mainObj = new JSONObject();
            mainObj.put("id", 1);
            mainObj.put("username", "SenthilVen");
            mainObj.put("password", "8148153229");
            mainObj.put("email", "senthilyuvi66@gmail.com");
            mainObj.put("ticketstatus", "0");
            mainObj.put("userid", "1");
            mainObj.put("assignee", "6");
            mainObj.put("products", ja);
            Log.v("Json oject", "===" + ja.toString());
            return mainObj;
        } catch (JSONException ex) {
            ex.printStackTrace();
        }


        return null;
    }
}
