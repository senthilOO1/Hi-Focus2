package com.deva.androiduser2.hi_focus.Product.Ticket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deva.androiduser2.hi_focus.JSONParser;
import com.deva.androiduser2.hi_focus.LocalStorageSQ;
import com.deva.androiduser2.hi_focus.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CreateTickets1 extends Fragment {
    private static final String TAG = "CreateTickets";
    String[] ticket;
    View rootView;
    private String jsonResult;
    private String url = "http://hifocuscctv.com/apps/Android/insert.php";
    private EditText nameField, emailField, mobileField, ProductNameField, ModelField, Serial_noField, QuantityField, DescriptionField;
    private Button submitButton;
    TextView clickAdd, plus;
    Button home, aboutus, contactus;
    LinearLayout addProductLayout;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    LocalStorageSQ db;

    private ArrayList<ArrayList<String>> list = new ArrayList<>();

    String name, email, mobile, ProductName, Model, Description, serialNo, qty;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_ticket1, container, false);

        db = new LocalStorageSQ(getActivity());



        addProductLayout = (LinearLayout) rootView.findViewById(R.id.addProductLayout);

        nameField = (EditText) rootView.findViewById(R.id.name);
        emailField = (EditText) rootView.findViewById(R.id.email);
        mobileField = (EditText) rootView.findViewById(R.id.mobile);
        clickAdd = (TextView)rootView.findViewById(R.id.clickAdd);
//        ProductNameField=(EditText)rootView.findViewById(R.id.ProductName);
//        ModelField=(EditText)rootView.findViewById(R.id.model);
//        Serial_noField=(EditText)rootView.findViewById(R.id.ser_no);
//        QuantityField=(EditText)rootView.findViewById(R.id.qty);
//        DescriptionField=(EditText)rootView.findViewById(R.id.Description);
        submitButton = (Button) rootView.findViewById(R.id.submit);

        plus = (TextView) rootView.findViewById(R.id.plus);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getActivity(), PopupActivity.class);
                startActivityForResult(in, 2);

            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                save();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                name = nameField.getText().toString();
                email = emailField.getText().toString();
                mobile = mobileField.getText().toString();
                JSONObject s = new JSONObject();
                for (int i = 0; i < list.size(); i++) {
                    ArrayList<String> msg = list.get(i);
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("name", name));
                    params.add(new BasicNameValuePair("email", email));
                    params.add(new BasicNameValuePair("mobile", mobile));
                    params.add(new BasicNameValuePair("products", getJson().toString()));

                    for (NameValuePair pair : params) {
                        try {
                            s.put(pair.getName(), pair.getValue());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //json.put("password", params.get(1).getValue());

                    }
                }


                new AttemptSaveTickets(getJson()).execute();
//                 ProductName = ProductNameField.getText().toString();
//                 Model =ModelField.getText().toString();
//                 Description =DescriptionField.getText().toString();
//                 serialNo = Serial_noField.getText().toString();
//                 qty = QuantityField.getText().toString();
            }
        });


        return rootView;
    }
    private JSONObject getJson(){



        JSONObject studentsObj = new JSONObject();
        try {
            studentsObj.put("", createObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return studentsObj;
    }

    public void save() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 2) {
            ArrayList<String> msg = data.getStringArrayListExtra("msg");
            db.getOngoingPrescriptions();
            list.add(msg);
            clickAdd.setVisibility(View.GONE);

            Log.e(TAG, "onActivityResult: " + msg.size());
            addLayout(msg.get(0), msg.get(1), msg.get(2), msg.get(3), msg.get(4));


        }

    }

    public void addLayout(String productname, String model, String sinumber, String description, String quantity) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_popup, null);
        TextView name, mobile, ticket, date, status;
        name = (TextView) view.findViewById(R.id.name);

        mobile = (TextView) view.findViewById(R.id.mobile);

        ticket = (TextView) view.findViewById(R.id.ticketno);

        date = (TextView) view.findViewById(R.id.dateReport);

        status = (TextView) view.findViewById(R.id.ticketstatus);

        ticket.setText(productname);

        date.setText(model);

        name.setText(sinumber);

        mobile.setText(description);

        status.setText(quantity);




        addProductLayout.addView(view);
        Log.e(TAG, "addLayout: called...");
    }


    class AttemptSaveTickets extends AsyncTask<String, String, String> {
        boolean failure = false;

        JSONObject msg;

        public AttemptSaveTickets(JSONObject msg) {
            this.msg = msg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Attempting Save...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;

            //String ticketstatus =ticket.getClass().toString();

//            try {
                // Building Parameters

//                (String productname,String model,String sinumber,String description,String quantity)


//                JSONObject json = jsonParser.makeHttpRequest(url, "POST", msg);
//                success = json.getInt(TAG_SUCCESS);
//                if (success == 1) {
//                    Intent i = new Intent(getActivity(), Ticket.class);
//                    startActivity(i);
//                    return json.getString(TAG_MESSAGE);
//                } else {
//                    return json.getString(TAG_MESSAGE);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(getActivity(), file_url, Toast.LENGTH_LONG).show();
            }

        }
    }
    public JSONObject createObject() {
        JSONObject mainObj = null;

        try {
            JSONArray ja = new JSONArray();
            for (int i = 0; i <10; i++) {
                JSONObject pnObj = new JSONObject();

                    pnObj.put("imagename", "senthil");
                    pnObj.put("image", "img");


                ja.put(pnObj);
            }


            mainObj = new JSONObject();
            mainObj.put("", ja);
            Log.v("Json oject", "===" + mainObj.toString());
            return mainObj;
        } catch (JSONException ex) {
            ex.printStackTrace();
        }


        return null;
    }
}
