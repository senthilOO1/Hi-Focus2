package com.deva.androiduser2.hi_focus.Product;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.deva.androiduser2.hi_focus.R;
import com.deva.androiduser2.hi_focus.UserSessionManager;
import com.deva.androiduser2.hi_focus.Utility;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GetProductsSpecifications1 extends Activity {
    private String jsonResult;
    private String url = "http://hifocuscctv.com/apps/Android/productSpecification.php";
    private ListView listView;
    private ImageView image;
    private TextView textView, chnl, dstr, dlr;
    Button home, aboutus, contactus;
    LazyAdapter adapter;
    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 768;

    int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));
    private String name, channelpartnerprice, distributorprice, dealersprice;
    public ImageLoader imageLoader;
    UserSessionManager session;
    String UserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productspecification);
        listView = (ListView) findViewById(R.id.listView1);
        textView = (TextView) findViewById(R.id.productname);
        Utility.showProgressDialog(GetProductsSpecifications1.this);
       /* chnl = (TextView) findViewById(R.id.id1);
        dstr = (TextView) findViewById(R.id.id2);
        dlr = (TextView) findViewById(R.id.id3);*/
        image = (ImageView) findViewById(R.id.image);

        imageLoader = new ImageLoader(this.getApplicationContext());
        accessWebService();
        session = new UserSessionManager(getApplicationContext());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {
                String productid = ((TextView) view.findViewById(R.id.id)).getText().toString();
                /*String channelpartnerprice = ((TextView) view.findViewById(R.id.id1)).getText().toString();
                String distributorprice = ((TextView) view.findViewById(R.id.id2)).getText().toString();
                String dealersprice = ((TextView) view.findViewById(R.id.id3)).getText().toString();
*/

            }
        });

        HashMap<String, String> user = session.getUserDetails();

        UserId = user.get(UserSessionManager.KEY_USERID);
        if (UserId.equals("0")) {

            UserId = "1";
        }

    }
    //------------------------------action bar---------------------------------


//-------------------------------------------------------------------------------

    //asy task
    private class JsonReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            Intent myIntent = getIntent();
            String productid = myIntent.getStringExtra("productid");


           /* String channelpartnerprice = myIntent.getStringExtra("channelpartnerprice");
            String distributorprice = myIntent.getStringExtra("distributorprice");
            String dealersprice = myIntent.getStringExtra("dealersprice");*/
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("productid", productid));
                nameValuePairs.add(new BasicNameValuePair("UserId", UserId));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                JSONObject json = new JSONObject();
                for (NameValuePair pair : nameValuePairs) {
                    json.put(pair.getName(), pair.getValue());
                    //json.put("password", params.get(1).getValue());

                }
                Log.i("ProductSpecifications", "HTTP Post Params =>> " + json.toString() + ", httppost url -" + params[0]);
                JSONArray postjson = new JSONArray();
                postjson.put(json);
                httppost.setHeader("json", json.toString());
                httppost.getParams().setParameter("jsonpost", postjson);

                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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

            System.out.println(answer);
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            ListDrwaer();
            Utility.hideProgressDialog(GetProductsSpecifications1.this);

        }
    }// end async task

    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[]{url});
    }


    // build hash set for list view
    public void ListDrwaer() {
        List<Map<String, String>> productList = new ArrayList<Map<String, String>>();

        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("product_info");
            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
            name = jsonChildNode.optString("productname");
            String id = jsonChildNode.optString("productid");
            String proimagepath = jsonChildNode.optString("proimagepath");
            String price = jsonChildNode.optString("price");
            String uri = Uri.parse(proimagepath)
                    .buildUpon()
                    .appendQueryParameter("key", "val")
                    .build().toString();


            Picasso.with(GetProductsSpecifications1.this)
                    .load(uri)
                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .skipMemoryCache()
                    .placeholder(R.drawable.progress_animation)
                    .resize(size, size)
                    .centerInside()
                    .into(image);

            System.out.print("price --> " + price);

            textView.setText(name);
            JSONArray jsonMainNodes = jsonResponse.optJSONArray("specifications");
            JSONObject jsonChildNodes = jsonMainNodes.getJSONObject(0);
            Iterator iter = jsonChildNodes.keys();
            HashMap<String, String> itemMap;
            if (null != price && !"".equals(price)) {

                itemMap = new HashMap<String, String>();
                itemMap.put("name", price);
                itemMap.put("id", "Price");
                productList.add(itemMap);

            } else {

            }

            while (iter.hasNext()) {
                itemMap = new HashMap<String, String>();
                String key = (String) iter.next();
                String value = jsonChildNodes.getString(key);
                itemMap.put("name", value);
                itemMap.put("id", key);
                productList.add(itemMap);
            }


        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        String[] from = {"name", "id"};
        int[] to = {R.id.name, R.id.id};
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), productList, R.layout.product_specification_list, from, to);
        listView.setAdapter(adapter);
    }
    public class BitmapTransform implements Transformation {

        int maxWidth;
        int maxHeight;

        public BitmapTransform(int maxWidth, int maxHeight) {
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            int targetWidth, targetHeight;
            double aspectRatio;

            if (source.getWidth() > source.getHeight()) {
                targetWidth = maxWidth;
                aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                targetHeight = (int) (targetWidth * aspectRatio);
            } else {
                targetHeight = maxHeight;
                aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                targetWidth = (int) (targetHeight * aspectRatio);
            }

            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return maxWidth + "x" + maxHeight;
        }

    };
}
