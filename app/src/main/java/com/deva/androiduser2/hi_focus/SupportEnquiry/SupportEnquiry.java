package com.deva.androiduser2.hi_focus.SupportEnquiry;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.deva.androiduser2.hi_focus.HIFOCUS;
import com.deva.androiduser2.hi_focus.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by miit on 22/10/15.
 */
public class SupportEnquiry extends Activity {


    //private SQLiteDatabase db;
    EditText name, email, mobile, productname, modelno, serialno,subject;
    Button submt;
    String na, em, mob, cit, sta, sub;
    private String url = "http://hifocuscctv.com/apps/Android/support_enquiry.php";
    private String jsonResult;

    TextView contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry);


        name = (EditText) findViewById(R.id.nameid);
        email = (EditText) findViewById(R.id.emailid);
        mobile = (EditText) findViewById(R.id.mobileid);
        productname = (EditText) findViewById(R.id.productname);
        modelno = (EditText) findViewById(R.id.modelno);
        serialno = (EditText) findViewById(R.id.serialno);
        subject = (EditText) findViewById(R.id.subject);
        submt = (Button) findViewById(R.id.sbmt);


        //db=openOrCreateDatabase("abi.db",MODE_PRIVATE,null);
        //db.execSQL("create table if not exists android(id integer auto increment,name varchar(20),email varchar(20),mobile int(15),city varchar(20),state varchar(20),subject varchar(100))");

        submt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*ContentValues val = new ContentValues();
                val.put("na", name.getText().toString());
                val.put("em", email.getText().toString());
                val.put("mob", mobile.getText().toString());
                val.put("cit", city.getText().toString());
                val.put("sta", state.getText().toString());
                val.put("sub", subject.getText().toString());*/


                //db.insert("android", null, val);
                final String email_id = email.getText().toString();
                final String mobile_id = mobile.getText().toString();
                final String names = name.getText().toString();
                if (!isValidName(names)) {
                    name.setError("Invalid UserName");
                }else if (!isValidEmail(email_id)) {
                    email.setError("Invalid Email");
                } else if (!isValidPassword(mobile_id)) {
                    mobile.setError("Invalid MobileNo");
                } else {
                    accessWebService();
                    Toast.makeText(getApplicationContext(), "Done! Our team 'll get back to you shortly.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(SupportEnquiry.this, HIFOCUS.class);
                    startActivity(i);
                }





            }
        });

    }


    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[]{url, name.getText().toString(), email.getText().toString(), mobile.getText().toString(), productname.getText().toString(), modelno.getText().toString(), serialno.getText().toString(),subject.getText().toString()});
    }

    // Async Task to access the web
    private class JsonReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            Intent myIntent = getIntent();
            String contactid = myIntent.getStringExtra("contactid");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(0);
                nameValuePairs.add(new BasicNameValuePair("name", params[1]));
                nameValuePairs.add(new BasicNameValuePair("email", params[2]));
                nameValuePairs.add(new BasicNameValuePair("mobile", params[3]));
                nameValuePairs.add(new BasicNameValuePair("productname", params[4]));
                nameValuePairs.add(new BasicNameValuePair("modelno", params[5]));
                nameValuePairs.add(new BasicNameValuePair("serialno", params[6]));
                nameValuePairs.add(new BasicNameValuePair("subject", params[7]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                System.out.println("nameValuePairs-----" + nameValuePairs);
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();

                System.out.println("json -------" + jsonResult);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
                // e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Error..." + e.toString(), Toast.LENGTH_LONG).show();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {

        }

    }// end async task

    @Override
    public void onBackPressed() {
        Bundle extras = getIntent().getExtras();

        boolean launchedFromNotif = false;

        if (extras.containsKey("EXTRA_LAUNCHED_BY_NOTIFICATION")) {
            launchedFromNotif = extras.getBoolean("EXTRA_LAUNCHED_BY_NOTIFICATION");
        }

        if (launchedFromNotif) {
            // Launched from notification, handle as special case
            Intent intent = new Intent(this, HIFOCUS.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
            //overridePendingTransition(R.drawable.from_middle, R.drawable.to_middle);
        } else {
            super.onBackPressed();
        }
    }

    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 9) {
            return true;
        }
        return false;
    }
    // validating password with retype password
    private boolean isValidName(String names) {
        if (names.length()>0) {
            return true;
        }
        return false;
    }
}
