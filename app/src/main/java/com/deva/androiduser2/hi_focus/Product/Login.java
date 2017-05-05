package com.deva.androiduser2.hi_focus.Product;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.deva.androiduser2.hi_focus.JSONParser;
import com.deva.androiduser2.hi_focus.ProductActivity;
import com.deva.androiduser2.hi_focus.R;
import com.deva.androiduser2.hi_focus.UserSessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Login extends Fragment implements View.OnClickListener {

    View rootView;

    private EditText usernameField,passwordField;
    private Button buttonField;
    private ProgressDialog pDialog;
    UserSessionManager session;

    String usernames,passwords;

    JSONParser jsonParser = new JSONParser();
    private static final String LOGIN_URL = "http://hifocuscctv.com/apps/Android/login.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_USERTYPE = "usertype";
    private static final String TAG_USERID = "userid";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.product_login, container, false);


        session = new UserSessionManager(getActivity());

        usernameField = (EditText)rootView.findViewById(R.id.email);
        usernameField.setTextColor(Color.parseColor("#000000"));
        passwordField = (EditText)rootView.findViewById(R.id.password);
        passwordField.setTextColor(Color.parseColor("#000000"));
        buttonField=(Button)rootView.findViewById(R.id.siginbutton);
        buttonField.setTextColor(Color.parseColor("#ffffff"));
        buttonField.setOnClickListener(this);

        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(UserSessionManager.KEY_USERNAME);
        String password = user.get(UserSessionManager.KEY_PASSWORD);
        if((username != null) && (username != "null"))
        {
            usernameField.setText(username);
            passwordField.setText(password);
        }

        return rootView;
    }

    public void login(View view){
        new AttemptLogin().execute();
        usernames = usernameField.getText().toString();
        passwords = passwordField.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.siginbutton:
                login(v);
                break;
        }
    }

    class AttemptLogin extends AsyncTask<String, String, String> {
        boolean failure = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
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

                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);
                success = json.getInt(TAG_SUCCESS);
                usertype = json.getString(TAG_USERTYPE);
                //Name = json.getString("name");
                String userId = json.getString(TAG_USERID);

                if (success == 1) {
                    session.createUserLoginSession(userId,username,password);
                    Intent i = new Intent(getActivity(), ProductActivity.class);
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
                Toast.makeText(getActivity(), file_url, Toast.LENGTH_LONG).show();
            }
        }
    }

}