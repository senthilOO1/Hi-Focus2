package com.deva.androiduser2.hi_focus;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AboutActivity extends Activity
{
    private String jsonResult;
    private String url = "http://hifocuscctv.com/apps/Android/productli.php";
    private TextView heading,title,description;
    private String Heading,Title,Description;
    ScrollView scrollView;

    LinearLayout aboutvislayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        Utility.showProgressDialog(AboutActivity.this);
        scrollView=(ScrollView)findViewById(R.id.scroll);
        heading = (TextView) findViewById(R.id.headingid);
        title = (TextView) findViewById(R.id.titleid);
        description = (TextView) findViewById(R.id.descid);
        aboutvislayout= (LinearLayout) findViewById(R.id.aboutvislayout);
        accessWebService();

    }
    //---------------------------actoin bar---------------------------------

    //-------------------------------------------------------------------
    public void accessWebService()
    {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[]{url});
    }
    // Async Task to access the web
    private class JsonReadTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            Intent myIntent = getIntent();
            String Aboutid = myIntent.getStringExtra("aboutid");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("aboutid", Aboutid));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();
            }

            catch (ClientProtocolException e)
            {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private StringBuilder inputStreamToString(InputStream is)
        {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null)
                {
                    answer.append(rLine);
                }
            }

            catch (IOException e)
            {
                // e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Error..." + e.toString(), Toast.LENGTH_LONG).show();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result)
        {
            ListDrwaer();
            aboutvislayout.setVisibility(View.VISIBLE);
            Utility.hideProgressDialog(AboutActivity.this);
        }
    }// end async task

    // build hash set for list view
    public void ListDrwaer()
    {

        List<Map<String, String>> productList = new ArrayList<Map<String, String>>();

        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONObject jsonMainNode = jsonResponse.getJSONObject("aboutus");//optJSONArray("aboutus");
           // JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
            Heading = jsonMainNode.optString("heading");
            Heading = Heading.replace("HI FOCUS","HI-FOCUS");
            Title = jsonMainNode.optString("title");
            Description = jsonMainNode.optString("desc");
           //String Aboutid = jsonChildNode.optString("aboutid");
            heading.setText(Heading);
            title.setText(Title);
            //description.setText(Base64.decode(Description, Base64.DEFAULT).toString());
            // description.setText(Base64.decode(Description.getBytes("UTF-8"),0).toString());
            description.setText(Description);

        } catch (JSONException e)
        {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();

        }
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
            overridePendingTransition(R.drawable.from_middle, R.drawable.to_middle);
        }
        else
        {
            super.onBackPressed();
        }
    }
}
