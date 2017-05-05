package com.deva.androiduser2.hi_focus.FQA;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.deva.androiduser2.hi_focus.HIFOCUS;
import com.deva.androiduser2.hi_focus.R;
import com.deva.androiduser2.hi_focus.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GetFaqListActivity extends Activity {
    private String jsonResult;
    private String url = "http://hifocuscctv.com/apps/Android/get_faq.php";
    private ListView listView;
    private  TextView faq;
    Button home,aboutus,contactus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_faq_list);
        listView = (ListView) findViewById(R.id.listView1);
        faq=(TextView)findViewById(R.id.faq_id);
        Utility.showProgressDialog(GetFaqListActivity.this);
        boolean valu = Utility.internetIsAvailable(this);

        if(valu == true){
            accessWebService();
        }else {
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {
                String faqid = ((TextView) view.findViewById(R.id.id)).getText().toString();
                Intent intent = new Intent(GetFaqListActivity.this, GetFaqDetailsActivity.class);
                intent.putExtra("faqid", faqid);
                startActivity(intent);
            }
        });

//        home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent hme = new Intent(GetFaqListActivity.this, MainActivity.class);
//                startActivity(hme);
//
//            }
//        });
//
//
//        aboutus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent abo = new Intent(GetFaqListActivity.this, AboutActivity.class);
//                startActivity(abo);
//
//            }
//        });
//
//        contactus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent con = new Intent(GetFaqListActivity.this, ContactActivity.class);
//                startActivity(con);
//
//            }
//        });

    }
    //------------------------action bar---------------------------------------
  //  @Override
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
//---------------------------------------------------------------------------------

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
            try {
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
            }

            catch (ClientProtocolException e) {
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
            }

            catch (IOException e) {
                // e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Error..." + e.toString(), Toast.LENGTH_LONG).show();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            ListDrwaer();
            Utility.hideProgressDialog(GetFaqListActivity.this);
        }
    }// end async task

    // build hash set for list view
    public void ListDrwaer() {
        List<Map<String, String>> menuList = new ArrayList<Map<String, String>>();

        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("faqs");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                HashMap<String, String> itemMap = new HashMap<String, String>();
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String name = jsonChildNode.optString("title");
                String id = jsonChildNode.optString("faqid");

                itemMap.put("name", name);
                itemMap.put("id", id);
                menuList.add(itemMap);
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        String[] from = { "name","id"};
        int[] to = { R.id.name,R.id.id};
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), menuList, R.layout.product_list, from, to);
        listView.setAdapter(adapter);
    }
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
        } else {
            super.onBackPressed();
        }
    }
}
