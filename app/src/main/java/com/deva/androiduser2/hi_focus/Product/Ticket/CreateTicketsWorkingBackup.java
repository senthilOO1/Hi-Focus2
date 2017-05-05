package com.deva.androiduser2.hi_focus.Product.Ticket;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.deva.androiduser2.hi_focus.JSONParser;
import com.deva.androiduser2.hi_focus.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CreateTicketsWorkingBackup extends Fragment {
    String[] ticket;
    View rootView;
    private String jsonResult;
    private String url = "http://hifocuscctv.com/apps/Android/insert.php";
    private EditText nameField, emailField, mobileField, ProductNameField, ModelField, Serial_noField, QuantityField, DescriptionField;
    private Button submitButton;
    Button home, aboutus, contactus;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String name, email, mobile, ProductName, Model, Description, serialNo, qty;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_ticket, container, false);

        nameField = (EditText) rootView.findViewById(R.id.name);
        emailField = (EditText) rootView.findViewById(R.id.email);
        mobileField = (EditText) rootView.findViewById(R.id.mobile);
        ProductNameField = (EditText) rootView.findViewById(R.id.ProductName);
        ModelField = (EditText) rootView.findViewById(R.id.model);
        Serial_noField = (EditText) rootView.findViewById(R.id.ser_no);
        QuantityField = (EditText) rootView.findViewById(R.id.qty);
        DescriptionField = (EditText) rootView.findViewById(R.id.Description);
        submitButton = (Button) rootView.findViewById(R.id.submit);


        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                save();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AttemptSaveTickets().execute();
                Log.e(TAG, "onClick: Called" );
//                name = nameField.getText().toString();
//                email = emailField.getText().toString();
//                mobile = mobileField.getText().toString();
//                ProductName = ProductNameField.getText().toString();
//                Model = ModelField.getText().toString();
//                Description = DescriptionField.getText().toString();
//                serialNo = Serial_noField.getText().toString();
//                qty = QuantityField.getText().toString();
            }
        });


        return rootView;
    }

    public void save() {

    }

    private static final String TAG = "CreateTickets";
    class AttemptSaveTickets extends AsyncTask<String, String, String> {
        boolean failure = false;

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
                JSONObject user = createObject();
                JSONObject json = jsonParser.makeHttpRequesttest(url, "POST", user);
                Log.e(TAG, "doInBackground: Json return"+ json);
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

    private JSONObject getJson() {


        JSONObject studentsObj = new JSONObject();
        try {
            studentsObj.put("", createObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return studentsObj;
    }

    public JSONObject createObject() {
        JSONObject mainObj = null;

        try {
            JSONArray ja = new JSONArray();
            for (int i = 0; i < 2; i++) {
                JSONObject pnObj = new JSONObject();
                pnObj.put("ProductName", "senthil");
                pnObj.put("Description", "img");
                pnObj.put("Model", "senthil");
                pnObj.put("serialNumber", 34);
                pnObj.put("quantity", 45);

                ja.put(pnObj);
            }

            mainObj = new JSONObject();
            mainObj.put("id", 23);
            mainObj.put("name", "Senthil Ven");
            mainObj.put("mobile", "8148153229");
            mainObj.put("email", "senthilyuvi66@gmail.com");
            mainObj.put("ticketstatus", 1);
            mainObj.put("userid", 8);
            mainObj.put("assignee", 6);
            mainObj.put("products", ja);
            Log.v("Json oject", "===" + ja.toString());
            return mainObj;
        } catch (JSONException ex) {
            ex.printStackTrace();
        }


        return null;
    }
}
