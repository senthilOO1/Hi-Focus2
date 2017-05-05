package com.deva.androiduser2.hi_focus.Product;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.deva.androiduser2.hi_focus.R;
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
import java.util.List;
import java.util.Map;

public class GetProductsList extends Activity {
    private String jsonResult;
    private String url = "http://hifocuscctv.com/apps/Android/productslist.php";
    private ListView listView;
    Button home,aboutus,contactus;
    OnGoingPrescriptionAdapter mAdapter;
    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 768;

    int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));
    ArrayList<ProductList> prescItem;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    LazyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.productlist);
       // listView = (ListView) findViewById(R.id.listView1);
        Utility.showProgressDialog(GetProductsList.this);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(GetProductsList.this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // 3. create an adapter

//        home=(Button)findViewById(R.id.lishomebtn);
//        aboutus=(Button)findViewById(R.id.lisaboutusbtn);
//        contactus=(Button)findViewById(R.id.liscontactusbtn);
        accessWebService();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView parent, View view,
//                                    int position, long id) {
////                String productid = ((TextView) view.findViewById(R.id.id)).getText().toString();
////                Intent intent = new Intent(GetProductsList.this, GetProductsSpecifications.class);
////                intent.putExtra("productid", productid);
////                startActivity(intent);
//                //finish();
//            }
//        });
//        home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent hme = new Intent(GetProductsList.this, MainActivity.class);
//                startActivity(hme);
//
//            }
//        });
//
//
//        aboutus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent abo = new Intent(GetProductsList.this, AboutActivity.class);
//                startActivity(abo);
//
//            }
//        });
//
//        contactus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent con = new Intent(GetProductsList.this, ContactActivity.class);
//                startActivity(con);
//
//            }
//        });

    }
    //--------------------------action bar--------------------------------
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
//---------------------------------------------------------------

    // Async Task to access the web
    private class JsonReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            Intent myIntent = getIntent();
            String categoryid = myIntent.getStringExtra("categoryid");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("categoryid", categoryid));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                JSONObject json = new JSONObject();
                for(NameValuePair pair : nameValuePairs){
                    json.put(pair.getName(), pair.getValue());
                    //json.put("password", params.get(1).getValue());

                }
                Log.i("GetCategory", "HTTP Post Params =>> " + json.toString() + ", httppost url -" + params[0] + ", categoryid =>> " + categoryid);
                JSONArray postjson=new JSONArray();
                postjson.put(json);
                httppost.setHeader("json", json.toString());
                httppost.getParams().setParameter("jsonpost", postjson);

                //System.out.println("nameValuePairs-----"+nameValuePairs+"categoryId ------"+categoryid);
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();

                System.out.println("json -------"+jsonResult);
            }

            catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
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
            Utility.hideProgressDialog(GetProductsList.this);
        }
    }// end async task

    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[]{url});
    }

    // build hash set for list view
    public void ListDrwaer() {
        prescItem = new ArrayList<ProductList>();
        if(null != jsonResult && !"".equals(jsonResult)){
            List<Map<String, String>> productList = new ArrayList<Map<String, String>>();

            try {
                JSONObject jsonResponse = new JSONObject(jsonResult);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("product_info");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    HashMap<String, String> itemMap = new HashMap<String, String>();
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    String name = jsonChildNode.optString("productname");
                    String id = jsonChildNode.optString("productid");
                    String proimagepath = jsonChildNode.optString("proimagepath");

                    ProductList audio = new ProductList(name,proimagepath,id);
                    prescItem.add(audio);
                    itemMap.put("name", name);
                    itemMap.put("id", id);
                    itemMap.put("image", proimagepath);
                    productList.add(itemMap);
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }

//            adapter=new LazyAdapter(this, productList);
//            mRecyclerView.setAdapter(adapter);
            mAdapter = new OnGoingPrescriptionAdapter(this, prescItem);
            // 4. set adapter
            mRecyclerView.setAdapter(mAdapter);
        }

    }
    public class OnGoingPrescriptionAdapter extends RecyclerView.Adapter<OnGoingPrescriptionAdapter.ViewHolder> {
        protected List<ProductList> listCars;
        Context context;

        public OnGoingPrescriptionAdapter(Context context, List<ProductList> listCars) {
            this.listCars = listCars;
            this.context = context;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.get_category_product, null);

            // create ViewHolder

            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }


        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {


            final ProductList prescItem = listCars.get(position);

            viewHolder.tv.setText( prescItem.getProductName());

            String uri = Uri.parse(prescItem.getProductImage())
                    .buildUpon()
                    .appendQueryParameter("key", "val")
                    .build().toString();


            Picasso.with(context)
                    .load(uri)
                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .skipMemoryCache()
                    .placeholder(R.drawable.progress_animation)
                    .resize(size, size)
                    .centerInside()
                    .into(viewHolder.image);

            Log.i("imageLink","=="+ prescItem.getProductImage());

            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                Intent intent = new Intent(GetProductsList.this, GetProductsSpecifications1.class);
                intent.putExtra("productid", prescItem.getProductId());


                startActivity(intent);
                }
            });

        }

        // Return the size of your itemsData (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return listCars.size();
        }

        // inner class to hold a reference to each item of RecyclerView
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv;

            ImageView image;

            LinearLayout linearLayout;


            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                tv = (TextView) itemLayoutView.findViewById(R.id.productName);

                image = (ImageView) itemLayoutView.findViewById(R.id.image);

                linearLayout = (LinearLayout) itemLayoutView.findViewById(R.id.productLayout);


            }
        }
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
