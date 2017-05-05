package com.deva.androiduser2.hi_focus;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.deva.androiduser2.hi_focus.Product.GetCategory;
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

/**
 * Created by androiduser2 on 18/4/16.
 */
public class ProductActivity extends AppCompatActivity {
    private String jsonResult;
    private String url = "http://hifocuscctv.com/apps/Android/products.php";
    ListView lts;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_productactivity);
        Utility.showProgressDialog(this);

        textView = (TextView)findViewById(R.id.Product9);

        textView.setTextColor(Color.parseColor("#ffffff"));

        lts = (ListView)findViewById(R.id.listView);
        accessWebService();

        lts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {
                String parentid = ((TextView) view.findViewById(R.id.id)).getText().toString();
                Intent intent = new Intent(ProductActivity.this,GetCategory.class);
                intent.putExtra("parentid", parentid);
                startActivity(intent);
            }
        });
    }
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
            String rLine = " ";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            }

            catch (IOException e) {
                // e.printStackTrace();
                Toast.makeText(ProductActivity.this,
                        "Error..." + e.toString(), Toast.LENGTH_LONG).show();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            ListDrwaer();
            Utility.hideProgressDialog(ProductActivity.this);
        }
    }// end async task

    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[]{url});
    }

    // build hash set for list view
    public void ListDrwaer() {
        List<Map<String, String>> menuList = new ArrayList<Map<String, String>>(0);

        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("product_info");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                HashMap<String, String> itemMap = new HashMap<String, String>();
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String name = jsonChildNode.optString("parentitem");
                String id = jsonChildNode.optString("parentid");

                itemMap.put("name", name);
                itemMap.put("id", id);
                menuList.add(itemMap);
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        String[] from = { "name","id"};
        int[] to = { R.id.name,R.id.id};
        SimpleAdapter adapter = new SimpleAdapter(this, menuList, R.layout.product_list, from, to);
        lts.setAdapter(adapter);
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
