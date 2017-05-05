package com.deva.androiduser2.hi_focus.Product;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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


public class Products extends Fragment {

    View rootView;
    private String jsonResult;
    private String url = "http://hifocuscctv.com/apps/Android/products.php";
    ListView lts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.layout_product, container, false);

        Utility.showProgressDialog(getActivity());

        lts = (ListView) rootView.findViewById(R.id.listView);

        accessWebService();

        lts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {
                String parentid = ((TextView) view.findViewById(R.id.id)).getText().toString();
                Intent intent = new Intent(getActivity(),GetCategory.class);
                intent.putExtra("parentid", parentid);
                startActivity(intent);
            }
        });
        return rootView;
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
                Toast.makeText(getActivity(),
                        "Error..." + e.toString(), Toast.LENGTH_LONG).show();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            ListDrwaer();
            Utility.hideProgressDialog(getActivity());
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
            Toast.makeText(getActivity(), "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        String[] from = { "name","id"};
        int[] to = { R.id.name,R.id.id};
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), menuList, R.layout.product_list, from, to);
        lts.setAdapter(adapter);
    }

}
