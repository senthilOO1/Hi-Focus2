package com.deva.androiduser2.hi_focus;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deva.androiduser2.hi_focus.FQA.GetFaqListActivity;
import com.deva.androiduser2.hi_focus.Product.Ticket.AssignedModel;
import com.deva.androiduser2.hi_focus.Product.Ticket.Ticket;
import com.deva.androiduser2.hi_focus.SupportEnquiry.SupportEnquiry;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HIFOCUS extends AppCompatActivity {

    private static final String TAG = "HIFOCUS";
    LinearLayout productLinear,aboutLayout,contactLayout,fqaLayout,supportLayout,liveLayout;

    TextView product,support,fqa,live,about,contact,titleBar;

    private SearchView searchView;
    JSONParser jsonParser = new JSONParser();
    private String assignedToUrl = "http://hifocuscctv.com/apps/Android/getUsers.php";

    String UserId;
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hifocus);
        session = new UserSessionManager(getApplicationContext());

        final HashMap<String, String> user = session.getUserDetails();
        String name = user.get(UserSessionManager.KEY_NAME);
        UserId = user.get(UserSessionManager.KEY_USERID);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        titleBar = (TextView) findViewById(R.id.titlebar);

        titleBar.setTextColor(Color.parseColor("#ffffff"));

        product = (TextView) findViewById(R.id.product1);

        product.setTextColor(Color.parseColor("#ffffff"));

        support = (TextView) findViewById(R.id.support1);

        support.setTextColor(Color.parseColor("#ffffff"));

        live = (TextView) findViewById(R.id.live1);

        live.setTextColor(Color.parseColor("#ffffff"));

        fqa = (TextView) findViewById(R.id.fqa1);

        fqa.setTextColor(Color.parseColor("#ffffff"));

        about = (TextView) findViewById(R.id.about1);

        about.setTextColor(Color.parseColor("#ffffff"));

        contact = (TextView) findViewById(R.id.contact1);

        contact.setTextColor(Color.parseColor("#ffffff"));

        productLinear = (LinearLayout)findViewById(R.id.productLinear);

        aboutLayout = (LinearLayout)findViewById(R.id.aboutLayout);

        contactLayout = (LinearLayout)findViewById(R.id.contactLayout);

        fqaLayout = (LinearLayout)findViewById(R.id.fqaLayout);

        supportLayout = (LinearLayout)findViewById(R.id.supportLayout);

        liveLayout = (LinearLayout)findViewById(R.id.liveLayout);

        new AttemptDistrictSearch().execute();

        productLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXTRA_LAUNCHED_BY_NOTIFICATION",true);
                startActivity(intent);
                finish();
            }
        });

        supportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpannableString spannableString = new SpannableString("RMA access is limited to Hi-Focus (Internal purpose)");
                spannableString.setSpan(
                        new ForegroundColorSpan(getResources().getColor(android.R.color.white)),
                        0,
                        spannableString.length(),
                        0);
                Log.e(TAG, "onClick: User Id  "+UserId );
                if(UserId.equals("2")) {
                    Toast.makeText(HIFOCUS.this, spannableString, Toast.LENGTH_SHORT).show();

                }else if(UserId.equals("3")){
                    Toast.makeText(HIFOCUS.this, spannableString, Toast.LENGTH_SHORT).show();
                }else if(UserId.equals("4")){
                    Toast.makeText(HIFOCUS.this, spannableString, Toast.LENGTH_SHORT).show();
                }else if(UserId.equals("1")){
                    Intent intent = new Intent(getApplicationContext(), Ticket.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXTRA_LAUNCHED_BY_NOTIFICATION", true);
                    startActivity(intent);
                    finish();

                }else if(UserId.equals("0")){

                    Toast.makeText(HIFOCUS.this, spannableString, Toast.LENGTH_SHORT).show();

                }else{
                    Intent intent = new Intent(getApplicationContext(), Ticket.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXTRA_LAUNCHED_BY_NOTIFICATION", true);
                    startActivity(intent);
                    finish();
                }
            }
        });

        fqaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GetFaqListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXTRA_LAUNCHED_BY_NOTIFICATION",true);
                startActivity(intent);
                finish();
            }
        });

        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXTRA_LAUNCHED_BY_NOTIFICATION",true);
                startActivity(intent);
                finish();
                overridePendingTransition(R.drawable.from_middle, R.drawable.to_middle);
            }
        });

        contactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXTRA_LAUNCHED_BY_NOTIFICATION",true);
                startActivity(intent);
                finish();
                overridePendingTransition(R.drawable.from_middle, R.drawable.to_middle);
            }
        });
        liveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getBaseContext(),"Currently you do not have access to support information",Toast.LENGTH_LONG).show();;
                Intent intent = new Intent(getApplicationContext(), SupportEnquiry.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXTRA_LAUNCHED_BY_NOTIFICATION",true);
                startActivity(intent);
                finish();
            }
        });

    }

    class AttemptDistrictSearch extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Utility.showProgressDialog(getActivity());
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            JSONObject json = jsonParser.makeHttpRequest(assignedToUrl, "POST", params);
            Log.e(TAG, "doInBackground: " + json);

            if (json != null) {
                return json.toString();
            } else {
                return null;
            }
        }

        protected void onPostExecute(String file_url) {

            if (file_url != null) {
                Utility.assignedList.clear();

                Utility.assignedList.add(new AssignedModel("",""));
                try {
                    JSONObject jsonResponse = new JSONObject(file_url);

                    JSONArray jsonMainNode = jsonResponse.optJSONArray("assignedTo");

                    for (int i = 0; i < jsonMainNode.length(); i++) {

                        JSONObject c = jsonMainNode.getJSONObject(i);
                        String name = c.getString("username");
                        String id = c.getString("id");
                        Utility.assignedList.add(new AssignedModel(id,name));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
               // Toast.makeText(getContext(), "Please connect internet.", Toast.LENGTH_SHORT).show();
            }
//            Utility.hideProgressDialog(getActivity());

        }

    }

}
