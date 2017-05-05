package com.deva.androiduser2.hi_focus.FQA;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.deva.androiduser2.hi_focus.R;
import com.deva.androiduser2.hi_focus.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GetFaqDetailsActivity extends Activity {
    private String jsonResult;
    private String url = "http://hifocuscctv.com/apps/Android/get_faq.php";
    private TextView textView, textView1, textView2;
    Button home, aboutus, contactus;
    private String name, value, date;
    ScrollView scrollView;
    int space;
    Boolean spc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faqdetail);
        textView = (TextView) findViewById(R.id.name);
        textView1 = (TextView) findViewById(R.id.value);
        textView2 = (TextView) findViewById(R.id.date);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        boolean valu = Utility.internetIsAvailable(this);
        Utility.showProgressDialog(GetFaqDetailsActivity.this);
        if (valu == true) {
            accessWebService();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }

//
//
//
//        home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent hme = new Intent(GetFaqDetailsActivity.this, MainActivity.class);
//                startActivity(hme);
//
//            }
//        });
//
//
//        aboutus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent abo=new Intent(GetFaqDetailsActivity.this,AboutActivity.class);
//                startActivity(abo);
//
//            }
//        });
//
//        contactus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent con = new Intent(GetFaqDetailsActivity.this, ContactActivity.class);
//                startActivity(con);
//
//            }
//        });


    }

    //---------------------------actoin bar---------------------------------
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.activity_main_actions, menu);
//
//        // Associate searchable configuration with the SearchView
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
//                .getActionView();
//        searchView.setSearchableInfo(searchManager
//                .getSearchableInfo(getComponentName()));
//
//        return super.onCreateOptionsMenu(menu);
//    }
//-------------------------------------------------------------------
    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[]{url});
    }

    // Async Task to access the web
    private class JsonReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            Intent myIntent = getIntent();
            String faqid = myIntent.getStringExtra("faqid");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("faqid", faqid));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();
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
            ListDrwaer();
            Utility.hideProgressDialog(GetFaqDetailsActivity.this);
        }
    }// end async task

    // build hash set for list view
    public void ListDrwaer() {
        List<Map<String, String>> productList = new ArrayList<Map<String, String>>();

        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("faqs");
            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
            name = jsonChildNode.optString("content");
            value = jsonChildNode.optString("title");
            date = Utility.convertDate(jsonChildNode.optString("createdtime"));
            String id = jsonChildNode.optString("faqid");


            for (int i = 0; i < name.length(); i++) {
                if (String.valueOf(name.charAt(i)).equals(" ")) {

                    space = i;
                    spc = true;
                } else {
                    i = name.length();
                }
            }
            if (spc == true) {
                textView.setText(name.substring(space+1, name.length()));
            }else {
                textView.setText(name);
            }

            textView1.setText(value);
            textView2.setText(date);

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
