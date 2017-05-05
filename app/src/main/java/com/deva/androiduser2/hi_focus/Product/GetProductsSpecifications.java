package com.deva.androiduser2.hi_focus.Product;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.deva.androiduser2.hi_focus.R;
import com.deva.androiduser2.hi_focus.UserSessionManager;
import com.squareup.picasso.Picasso;

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

public class GetProductsSpecifications extends Activity {
    private String jsonResult;
    private String url = "http://hifocuscctv.com/apps/Android/productSpecification.php";
    private ListView listView;
    private ImageView image;
    private TextView textView,chnl,dstr,dlr;
    Button home,aboutus,contactus;
    LazyAdapter adapter;
    private String name,channelpartnerprice,distributorprice,dealersprice;
    public ImageLoader imageLoader;
    UserSessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState){

    super.onCreate(savedInstanceState);
    setContentView(R.layout.productlist);
//    listView = (ListView) findViewById(R.id.listView2);
//    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
//    LinearLayoutManager mLayoutManager = new GridLayoutManager(GetProductsList.this, 2);
//    mRecyclerView.setLayoutManager(mLayoutManager);
//    // 3. create an adapter

    //        home=(Button)findViewById(R.id.lishomebtn);
//        aboutus=(Button)findViewById(R.id.lisaboutusbtn);
//        contactus=(Button)findViewById(R.id.liscontactusbtn);
    accessWebService();
    //mRecyclerView.setItemAnimator(new DefaultItemAnimator());

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
        String productid = myIntent.getStringExtra("productid");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("productid", productid));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            JSONObject json = new JSONObject();
            for(NameValuePair pair : nameValuePairs){
                json.put(pair.getName(), pair.getValue());
                //json.put("password", params.get(1).getValue());

            }
            Log.i("GetCategory", "HTTP Post Params =>> " + json.toString() + ", httppost url -" + params[0] + ", categoryid =>> " + productid);
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
            //String distributorprice = jsonChildNode.optString("distributorprice");
            //String dealersprice = jsonChildNode.optString("dealersprice");
            //imageLoader.DisplayImage(proimagepath, image);
            //String Price = jsonChildNode.optString("price");
            System.out.print("price --> "+price);

           // textView.setText(name);
            JSONArray jsonMainNodes = jsonResponse.optJSONArray("specifications");
            JSONObject  jsonChildNodes = jsonMainNodes.getJSONObject(0);
            Iterator iter = jsonChildNodes.keys();
            HashMap<String, String> itemMap;
            if(null != price && !"".equals(price))
            {
                itemMap = new HashMap<String,String>();
                itemMap.put("name", price);
                itemMap.put("id","Price");
                productList.add(itemMap);
                /*TextView view =(TextView) findViewById(R.id.price);
                view.setText("PRICE"+price);
                view.setVisibility(View.VISIBLE);*/
            }
            else
            {
                itemMap = new HashMap<String,String>();
                itemMap.put("name", "Not Available");
                itemMap.put("id","Price");
                productList.add(itemMap);
               /* TextView view = (TextView) findViewById(R.id.price);
                view.setText("Price"+"Not Available");
                view.setVisibility(TextView.VISIBLE);*/
            }

            while(iter.hasNext()){
                itemMap = new HashMap<String, String>();
                String key = (String)iter.next();
                String value = jsonChildNodes.getString(key);
                itemMap.put("name", value);
                itemMap.put("id", key);
                productList.add(itemMap);
            }


        }
        catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        String[] from = { "name","id"};
        int[] to = { R.id.name,R.id.id};
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), productList, R.layout.product_specification_list, from, to);
        listView.setAdapter(adapter);
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

        Picasso.with(context)
                .load(prescItem.getProductImage())
                .into(viewHolder.image);

        Log.i("imageLink","=="+ prescItem.getProductImage());

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(GetProductsList.this, GetProductsSpecifications.class);
//                intent.putExtra("productid", prescItem.getProductId());
//                startActivity(intent);
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
}}

