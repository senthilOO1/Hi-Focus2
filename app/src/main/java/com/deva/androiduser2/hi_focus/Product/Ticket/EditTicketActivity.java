package com.deva.androiduser2.hi_focus.Product.Ticket;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.deva.androiduser2.hi_focus.Description;
import com.deva.androiduser2.hi_focus.JSONParser;
import com.deva.androiduser2.hi_focus.LocalStorageSQ;
import com.deva.androiduser2.hi_focus.ProductDetails;
import com.deva.androiduser2.hi_focus.R;
import com.deva.androiduser2.hi_focus.SetandGetProducts;
import com.deva.androiduser2.hi_focus.UserSessionManager;
import com.deva.androiduser2.hi_focus.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
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

//import android.support.v7.app.ActionBarActivity;
public class EditTicketActivity extends Activity {

    String ticketStatus, assignedTo;
    private String jsonResult, savejsonResult;
    private String assignedTojsonResult, viewValue = "1";
    private String url = "http://hifocuscctv.com/apps/Android/get_supports.php";
    private String inserturl = "http://hifocuscctv.com/apps/Android/insert.php";
    private ListView listView;
    private EditText nameField, emailField, mobileField, ProductNameField, ModelField, DescriptionField, SerialnoField, QuantityField, MoreDescField;
    private Spinner ticketStatusField, AssignedToField;
    private Button Submit;
    LocalStorageSQ db;
    Custom mAdapter;
    int valProgress = 1;
    //Button home,aboutus,contactus;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    String tikStatus;
    private boolean isCalled = false;
    String UserId;
    UserSessionManager session;
    int val = 1;
    ProductDetails productDetails;
    String Descriptions = "";
    List<ProductDetails> products = new ArrayList<ProductDetails>();
    ImageView description;
    ListView editList;
    String editValue = "0";
    TextView plus, edit, ticketId, listsDup, ticketStatusDup;
    LinearLayout addProductLayout, duplicate1, duplicate2, duplicate3, clickPro;

    ArrayList<SetandGetProducts> editproductlist;
    ArrayList<AssignedModel> getAssignedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] params = {url, "view"};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit);
        session = new UserSessionManager(getApplication());
        final HashMap<String, String> user = session.getUserDetails();
        String name = user.get(UserSessionManager.KEY_NAME);
        UserId = user.get(UserSessionManager.KEY_USERID);

        getAssignedId = new ArrayList<>();
        boolean valu = Utility.internetIsAvailable(this);

        if (valu == true) {
            accessWebService(params);
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }


        nameField = (EditText) findViewById(R.id.name);
        emailField = (EditText) findViewById(R.id.email);
        mobileField = (EditText) findViewById(R.id.mobile);
        edit = (TextView) findViewById(R.id.edit);

        description = (ImageView) findViewById(R.id.Descr);
        plus = (TextView) findViewById(R.id.plus);
        ticketId = (TextView) findViewById(R.id.ticketId);
        clickPro = (LinearLayout) findViewById(R.id.clickPro);
        editList = (ListView) findViewById(R.id.editList);
        addProductLayout = (LinearLayout) findViewById(R.id.addProductLayout);
        duplicate1 = (LinearLayout) findViewById(R.id.duplicate1);
        // duplicate2 = (LinearLayout) findViewById(R.id.duplicate2);
        duplicate3 = (LinearLayout) findViewById(R.id.duplicate3);
        MoreDescField = (EditText) findViewById(R.id.more_desc);
        Submit = (Button) findViewById(R.id.submit);
        ticketStatusField = (Spinner) findViewById(R.id.ticketstatus);
        AssignedToField = (Spinner) findViewById(R.id.lists);
        clickPro.setVisibility(View.GONE);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getApplicationContext(), PopupActivity.class);
                in.putExtra("Value", "AddProduct");
                in.putExtra("id", "add");
                in.putExtra("View", "0");
                startActivityForResult(in, 2);

            }
        });
        if (getIntent().getStringExtra("status").equals("3")) {
            edit.setVisibility(View.INVISIBLE);
            Submit.setVisibility(View.GONE);
            description.setImageResource(R.drawable.descriptions);
            //duplicate2.setVisibility(View.GONE);
        } else if (getIntent().getStringExtra("status").equals("2")) {
            //edit.setVisibility(View.GONE);

            description.setImageResource(R.drawable.descriptions);
        } else {
            description.setImageResource(R.drawable.descriptions);
        }
        db = new LocalStorageSQ(this);
        productDetails = new ProductDetails();


        nameField.setEnabled(false);
        emailField.setEnabled(false);
        mobileField.setEnabled(false);
        AssignedToField.setEnabled(false);
        ticketStatusField.setEnabled(false);
        duplicate3.setVisibility(View.GONE);

        plus.setVisibility(View.GONE);

        if (UserId.equals("5")) {
            nameField.setEnabled(false);
            emailField.setEnabled(false);
            mobileField.setEnabled(false);
            AssignedToField.setEnabled(false);

        }
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valProgress == 1) {
                    if (getIntent().getStringExtra("status").equals("3")) {
                        showDialog();
                    } else if (getIntent().getStringExtra("status").equals("2")) {
                        showDialog();
                    }
                } else {
                    if (!UserId.equals("1")) {

                        Intent in = new Intent(EditTicketActivity.this, Description.class);
                        in.putExtra("Value", "AddProduct");
                        in.putExtra("id", "add");
                        startActivityForResult(in, 3);
                    }
                }

            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valProgress = 0;
                editValue = "1";

                if (UserId.equals("1")) {
                    nameField.setEnabled(true);
                    emailField.setEnabled(true);
                    mobileField.setEnabled(true);
                    AssignedToField.setEnabled(true);
                    ticketStatusField.setEnabled(true);
                    viewValue = "0";
                    plus.setVisibility(View.VISIBLE);
                    duplicate1.setVisibility(View.GONE);
                    //duplicate2.setVisibility(View.GONE);
                    val = 0;
                } else {
                    duplicate1.setVisibility(View.GONE);
                    viewValue = "1";
                    ticketStatusField.setEnabled(true);
                }
            }
        });

        nameField.setText(getIntent().getStringExtra("name"));
        emailField.setText(getIntent().getStringExtra("email"));
        mobileField.setText(getIntent().getStringExtra("mobile"));
        ticketId.setText(getIntent().getStringExtra("ticketNo"));

        duplicate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        duplicate3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        ticketStatusField = (Spinner) findViewById(R.id.ticketstatus);

        if (UserId.equals("1")) {
            String[] ticket = {"Assigned", "In Progress", "Closed", "Open"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ticket);
            ticketStatusField.setAdapter(adapter);

            ticketStatusField.setSelection(Integer.parseInt(getIntent().getStringExtra("status")) - 1);
            Descriptions = getIntent().getStringExtra("indes");
        } else {
            if (getIntent().getStringExtra("status").equals("1")) {
                String[] ticket = {"Assigned", "In Progress", "Closed"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ticket);
                ticketStatusField.setAdapter(adapter);
                //ticketStatusField.setSelection(Integer.parseInt(getIntent().getStringExtra("status")) - 1);
            } else if (getIntent().getStringExtra("status").equals("2")) {
                String[] ticket = {"In Progress", "Closed"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ticket);
                ticketStatusField.setAdapter(adapter);
                //ticketStatusField.setSelection(Integer.parseInt(getIntent().getStringExtra("status")));
            } else if (getIntent().getStringExtra("status").equals("4")) {
                String[] ticket = {"Assigned", "In Progress", "Closed", "Open"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ticket);
                ticketStatusField.setAdapter(adapter);
                ticketStatusField.setSelection(Integer.parseInt(getIntent().getStringExtra("status")) - 1);
            } else if (getIntent().getStringExtra("status").equals("3")) {
                String[] ticket = {"Closed"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ticket);
                ticketStatusField.setAdapter(adapter);
            }
        }


        ticketStatusField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (isCalled == true) {

                    Log.i("Selected item : ", "888888888888");
                    if (!UserId.equals("1")) {

                        Intent in = new Intent(EditTicketActivity.this, Description.class);
                        in.putExtra("Value", "AddProduct");
                        in.putExtra("id", "add");
                        startActivityForResult(in, 3);
                    }
                } else {
                    isCalled = true;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        Submit.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                boolean valu = Utility.internetIsAvailable(EditTicketActivity.this);

                if (valu == true) {
                    new AttemptSaveTickets().execute();
                } else {
                    Toast.makeText(EditTicketActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }


            }
        });


    }


    public void showDialog() {
        final Dialog dialog = new Dialog(EditTicketActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_closed);

        TextView text = (TextView) dialog.findViewById(R.id.descriptionInfo);
        if (getIntent().getStringExtra("status").equals("3")) {
            text.setText(getIntent().getStringExtra("closed"));
        } else if (getIntent().getStringExtra("status").equals("2")) {
            text.setText(getIntent().getStringExtra("indes"));
        }


        TextView dialogButton = (TextView) dialog.findViewById(R.id.closed);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void save(String ticketStatus) {

    }

    public JSONObject createObject() {
        String name = nameField.getText().toString();
        String email = emailField.getText().toString();
        String mobile = mobileField.getText().toString();
        String assignedtotxt = null;
        String value = "";
        if (AssignedToField.getSelectedItemPosition() >= 0) {
            AssignedModel item = getAssignedId.get(AssignedToField.getSelectedItemPosition());
            assignedtotxt = item.getId();
        }
        if (ticketStatusField.getSelectedItem().toString().equals("Assigned")) {
            tikStatus = "1";
        } else if (ticketStatusField.getSelectedItem().toString().equals("In Progress")) {
            tikStatus = "2";
            value = "indes";
        } else if (ticketStatusField.getSelectedItem().toString().equals("Closed")) {
            tikStatus = "3";
            value = "closedes";
        } else if (ticketStatusField.getSelectedItem().toString().equals("Open")) {
            tikStatus = "4";
        }
        String statustxt = tikStatus;

        Log.e(TAG, "createObject: " + assignedtotxt + "  " + statustxt + "  " + ticketid);

        JSONObject mainObj = null;

        try {
            JSONArray ja = new JSONArray();
            if (products != null) {
                db = new LocalStorageSQ(this);
                products = db.getOngoingPrescriptions();
                for (int i = 0; i < products.size(); i++) {
                    // SetandGetProducts list = editproductlist.get(i);
                    JSONObject pnObj = new JSONObject();
                    pnObj.put("productname", products.get(i).Prescription_medicid);
                    pnObj.put("description", products.get(i).Prescription_doctorname);
                    pnObj.put("model", products.get(i).Prescription_Date);
                    pnObj.put("serialno", products.get(i).Prescription_next_appointment);
                    pnObj.put("quantity", products.get(i).Prescription_id);

                    ja.put(pnObj);
                }
            }
            mainObj = new JSONObject();
            mainObj.put("ticketid", ticketid);
            mainObj.put("name", name);
            mainObj.put("mobile", mobile);
            mainObj.put("email", email);
            mainObj.put("ticketstatus", statustxt);
            mainObj.put("assignedto", assignedtotxt);
            mainObj.put("products", ja);
            mainObj.put("indes", Descriptions);
            if (ticketStatusField.getSelectedItem().toString().equals("Closed")) {
                mainObj.put("closedes", Descriptions);
            }

//            Log.v("Json oject", "===" + ja.toString());
            return mainObj;
        } catch (JSONException ex) {
            ex.printStackTrace();
        }


        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 2) {
            if (data != null) {
                ArrayList<String> msg = data.getStringArrayListExtra("msg");

                if (editproductlist == null) {
                    editproductlist = new ArrayList<>();
                }

                String productname = msg.get(0);
                String model = msg.get(1);
                String serialno = msg.get(2);
                String quantity = msg.get(4);
                String description = msg.get(3);
//                SetandGetProducts setandGetProducts=new SetandGetProducts(0,productname,model,serialno,description,quantity);
//                editproductlist.add(setandGetProducts);
                products = db.getOngoingPrescriptions();

                mAdapter = new Custom(this, products);
                editList.setAdapter(mAdapter);
                Utility.getListViewSize(editList);
                if(products.size() == 0){
                    clickPro.setVisibility(View.GONE);
                }else {
                    if(products.size() == 0){
                        clickPro.setVisibility(View.GONE);
                    }

                }

                Log.e(TAG, "onActivityResult: " + msg);
                //addLayout(msg.get(0), msg.get(1), msg.get(2), msg.get(3), msg.get(4));
            }


        } else if (requestCode == 3) {
            if (data != null) {
                String msg = data.getStringExtra("Desp");
                Descriptions = msg;

            }
        }

    }

    public void addLayout(String productname, String model, String sinumber, String description, String quantity) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.layout_alldetailmutliproduct, null);

        view.setTag(editproductlist.size() - 1);
        addProductLayout.setTag(editproductlist.size() - 1);

        final EditText pname, pdes, pmodel, psi, pquantity;
        pname = (EditText) view.findViewById(R.id.ProductName);

        pname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() != 0) {
                    Log.e("Test", "onTextChanged: " + pname.getText().toString());
                    int position = (Integer) view.getTag();

                    editproductlist.get(position).setProductName(pname.getText().toString());

//                    Toast.makeText(getApplicationContext(), "Updated " + position, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                        if (s.length() != 0) {
//                            Log.e("Test", "onTextChanged: " + pname.getText().toString());
//                        }
            }
        });
        pdes = (EditText) view.findViewById(R.id.Description);

        pdes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() != 0) {
                    Log.e("Test", "onTextChanged: " + pdes.getText().toString());
                    int position = (Integer) view.getTag();

                    editproductlist.get(position).setDescription(pdes.getText().toString());

//                            Toast.makeText(getApplicationContext(), ""+position, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                        if (s.length() != 0) {
//                            Log.e("Test", "onTextChanged: " + pname.getText().toString());
//                        }
            }
        });

        pmodel = (EditText) view.findViewById(R.id.model);

        pmodel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() != 0) {
                    Log.e("Test", "onTextChanged: " + pmodel.getText().toString());
                    int position = (Integer) view.getTag();

                    editproductlist.get(position).setModel(pmodel.getText().toString());

//                            Toast.makeText(getApplicationContext(), ""+position, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                        if (s.length() != 0) {
//                            Log.e("Test", "onTextChanged: " + pname.getText().toString());
//                        }
            }
        });

        psi = (EditText) view.findViewById(R.id.ser_no);

        psi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() != 0) {
                    Log.e("Test", "onTextChanged: " + psi.getText().toString());
                    int position = (Integer) view.getTag();

                    editproductlist.get(position).setSinumber(psi.getText().toString());

//                            Toast.makeText(getApplicationContext(), ""+position, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                        if (s.length() != 0) {
//                            Log.e("Test", "onTextChanged: " + pname.getText().toString());
//                        }
            }
        });

        pquantity = (EditText) view.findViewById(R.id.qty);

        pquantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() != 0) {
                    Log.e("Test", "onTextChanged: " + pquantity.getText().toString());
                    int position = (Integer) view.getTag();

                    editproductlist.get(position).setQuantity(pquantity.getText().toString());

//                            Toast.makeText(getApplicationContext(), ""+position, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                        if (s.length() != 0) {
//                            Log.e("Test", "onTextChanged: " + pname.getText().toString());
//                        }
            }
        });


        pname.setText(productname);
        pdes.setText(description);
        pmodel.setText(model);
        psi.setText(sinumber);
        pquantity.setText(quantity);


        addProductLayout.addView(view);
        Log.e(TAG, "addLayout: called...");
    }

    private static final String TAG = "EditTicketActivity";



    class AttemptSaveTickets extends AsyncTask<String, String, String> {
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utility.showProgressDialog(EditTicketActivity.this);
        }

        @Override
        protected String doInBackground(String... args) {
            int success;

            //String ticketstatus =ticket.getClass().toString();

            try {
                JSONObject user = createObject();
                JSONObject json = jsonParser.makeHttpRequesttest(inserturl, "POST", user);
                Log.e(TAG, "doInBackground: Json return" + json);

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    db = new LocalStorageSQ(EditTicketActivity.this);
                    db.deleteMedicinesList();
                    Intent i = new Intent(getApplicationContext(), Ticket.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    return json.getString(TAG_MESSAGE);
                } else {
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {

            if (file_url != null) {
                Toast.makeText(getApplicationContext(), file_url, Toast.LENGTH_LONG).show();
            }
            Utility.hideProgressDialog(EditTicketActivity.this);

        }
    }


    String ticketid;

    //String url, String status, String comments,String flag)
    public void updateTicketStatus(String[] params) {
        accessWebService(params);
    }

    public void accessWebService(String[] params) {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(params);
    }

    /* public void accessWebService()
     {
         JsonReadTask task = new JsonReadTask();
         // passes values for the urls string array
         task.execute(new String[]
                 {
                         url
                 });
     }*/
    // Async Task to access the web
    private class JsonReadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utility.showProgressDialog(EditTicketActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            Intent myIntent = getIntent();
            ticketid = myIntent.getStringExtra("ticketid");
            List<NameValuePair> nameValuePairs;
            Log.i("EditTicketActivity", "Action Type -" + params[1] + ", Ticket ID - " + ticketid);
            if ("save".equals(params[1])) {
                HttpPost httppost = new HttpPost(params[0]);
                nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("ticketStatus", params[2]));
                nameValuePairs.add(new BasicNameValuePair("ticketComments", params[3]));
                nameValuePairs.add(new BasicNameValuePair("userId", params[4]));
                nameValuePairs.add(new BasicNameValuePair("ticketId", params[5]));
                System.out.println("pairs :- " + nameValuePairs);
                JSONObject json = new JSONObject();
                try {
                    for (NameValuePair pair : nameValuePairs) {
                        json.put(pair.getName(), pair.getValue());
                        //json.put("password", params.get(1).getValue());

                    }
                    Log.i("EditTicketActivity", "HTTP Post Params =>> " + json.toString() + ", httppost url -" + params[0]);
                    JSONArray postjson = new JSONArray();
                    postjson.put(json);
                    httppost.setHeader("json", json.toString());
                    httppost.getParams().setParameter("jsonpost", postjson);
                    //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    savejsonResult = inputStreamToString(response.getEntity().getContent()).toString();
                    Log.i("EditTicketActivity", "savejsonResult=>> " + savejsonResult);

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            HttpPost httppost = new HttpPost(url);
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("ticketid", ticketid));
            JSONObject json = new JSONObject();

            try {
                json.put("ticketid", ticketid);
                Log.i("EditTicketActivity", "HTTP Post Params =>> " + json.toString());
                JSONArray postjson = new JSONArray();
                postjson.put(json);
                httppost.setHeader("json", json.toString());
                httppost.getParams().setParameter("jsonpost", postjson);
                //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
                Log.i("EditTicketActivity", "url - " + url + ", jsonResult -" + jsonResult);
                String GET_USERS_URL = "http://hifocuscctv.com/apps/Android/getUsers.php";
                httppost = new HttpPost(GET_USERS_URL);
                HttpResponse getUsersResponse = httpclient.execute(httppost);
                assignedTojsonResult = inputStreamToString(getUsersResponse.getEntity().getContent()).toString();
                Log.i("EditTicketActivity", "url - " + GET_USERS_URL + ", assignedTojsonResult - " + assignedTojsonResult);

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
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            ListDrwaer();

            Utility.hideProgressDialog(EditTicketActivity.this);
        }
    }// end async task

    // build hash set for list view
    public void ListDrwaer() {


        List<Map<String, String>> menuList = new ArrayList<Map<String, String>>();

        Log.e(TAG, "ListDrwaer: Testing " + jsonResult);

        if (jsonResult != null) {
            try {

                JSONObject assignedTojsonObj = new JSONObject(assignedTojsonResult);
                JSONArray assignedTojsonArray = assignedTojsonObj.optJSONArray("assignedTo");
                final HashMap<String, String> usersMap = new HashMap<String, String>();

                List<String> asigneedToArray = new ArrayList<String>();
                asigneedToArray.clear();
                getAssignedId.clear();
                //usersMap.put("Select User","Select User");
                for (int i = 0; i < assignedTojsonArray.length(); i++) {
                    JSONObject assignedToObj = assignedTojsonArray.getJSONObject(i);
                    usersMap.put(assignedToObj.optString("id"), assignedToObj.optString("username"));
                    String username = assignedToObj.getString("username");
                    String userid = assignedToObj.getString("id");

                    asigneedToArray.add(assignedToObj.getString("username"));
                    getAssignedId.add(new AssignedModel(userid, username));
                }
                String ddItems[] = new String[usersMap.size()];
                usersMap.values().toArray(ddItems);
//            Spinner spinner = (Spinner) findViewById(R.id.lists);

                ArrayAdapter<String> sadapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, asigneedToArray);
                AssignedToField.setAdapter(sadapter);
                if(getIntent().getStringExtra("assingedto").equals("")){
                    AssignedToField.setSelection(0);
                }else {
                    AssignedToField.setSelection(Integer.parseInt(getIntent().getStringExtra("assingedto")) - 5);
                }



                JSONObject jsonResponse = new JSONObject(jsonResult);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("tickets-products");
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
                editproductlist = new ArrayList<>();

//            final ArrayList<SetandGetProducts> list=new ArrayList<>();

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode1 = jsonMainNode.getJSONObject(i);
                    String productname = jsonChildNode1.optString("productname");
                    String model = jsonChildNode1.optString("model");
                    String serialno = jsonChildNode1.optString("serialno");
                    String quantity = jsonChildNode1.optString("quantity");
                    String description = jsonChildNode1.optString("description");
                    SetandGetProducts setandGetProducts = new SetandGetProducts(i, productname, model, serialno, description, quantity);
                    //editproductlist.add(setandGetProducts);
                    productDetails.Prescription_medicid = productname;
                    productDetails.Prescription_Date = model;
                    productDetails.Prescription_next_appointment = serialno;
                    productDetails.Prescription_doctorname = description;
                    productDetails.Prescription_id = quantity;
                    db.addPrescription(productDetails);
                    products = db.getOngoingPrescriptions();

                    mAdapter = new Custom(this, products);
                    editList.setAdapter(mAdapter);
                    Utility.getListViewSize(editList);
                    if(products.size() == 0){
                        clickPro.setVisibility(View.GONE);
                    }else {
                        clickPro.setVisibility(View.VISIBLE);
                    }
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View view = inflater.inflate(R.layout.layout_alldetailmutliproduct, null);
                    view.setTag(i);
                    addProductLayout.setTag(i);

                    final EditText pname, pdes, pmodel, psi, pquantity;
                    pname = (EditText) view.findViewById(R.id.ProductName);


//                    pname.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                            if (s.length() != 0) {
//                                Log.e("Test", "onTextChanged: " + pname.getText().toString());
//                                int position = (Integer) view.getTag();
//
//                                editproductlist.get(position).setProductName(pname.getText().toString());
//
////                            Toast.makeText(getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable s) {
////                        if (s.length() != 0) {
////                            Log.e("Test", "onTextChanged: " + pname.getText().toString());
////                        }
//                        }
//                    });
//
//                    pdes = (EditText) view.findViewById(R.id.Description);
//
//                    pdes.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                            if (s.length() != 0) {
//                                Log.e("Test", "onTextChanged: " + pdes.getText().toString());
//                                int position = (Integer) view.getTag();
//
//                                editproductlist.get(position).setDescription(pdes.getText().toString());
//
////                            Toast.makeText(getApplicationContext(), ""+position, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable s) {
////                        if (s.length() != 0) {
////                            Log.e("Test", "onTextChanged: " + pname.getText().toString());
////                        }
//                        }
//                    });
//
//                    pmodel = (EditText) view.findViewById(R.id.model);
//
//                    pmodel.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                            if (s.length() != 0) {
//                                Log.e("Test", "onTextChanged: " + pmodel.getText().toString());
//                                int position = (Integer) view.getTag();
//
//                                editproductlist.get(position).setModel(pmodel.getText().toString());
//
////                            Toast.makeText(getApplicationContext(), ""+position, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable s) {
////                        if (s.length() != 0) {
////                            Log.e("Test", "onTextChanged: " + pname.getText().toString());
////                        }
//                        }
//                    });
//
//                    psi = (EditText) view.findViewById(R.id.ser_no);
//
//                    psi.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                            if (s.length() != 0) {
//                                Log.e("Test", "onTextChanged: " + psi.getText().toString());
//                                int position = (Integer) view.getTag();
//
//                                editproductlist.get(position).setSinumber(psi.getText().toString());
//
////                            Toast.makeText(getApplicationContext(), ""+position, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable s) {
////                        if (s.length() != 0) {
////                            Log.e("Test", "onTextChanged: " + pname.getText().toString());
////                        }
//                        }
//                    });
//
//                    pquantity = (EditText) view.findViewById(R.id.qty);
//
//                    pquantity.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                            if (s.length() != 0) {
//                                Log.e("Test", "onTextChanged: " + pquantity.getText().toString());
//                                int position = (Integer) view.getTag();
//
//                                editproductlist.get(position).setQuantity(pquantity.getText().toString());
//
////                            Toast.makeText(getApplicationContext(), ""+position, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable s) {
////                        if (s.length() != 0) {
////                            Log.e("Test", "onTextChanged: " + pname.getText().toString());
////                        }
//                        }
//                    });
//
//                    pname.setText(productname);
//                    pdes.setText(description);
//                    pmodel.setText(model);
//                    psi.setText(serialno);
//                    pquantity.setText(quantity);
//                    if (UserId.equals("5")) {
//                        pname.setFocusable(false);
//                        pmodel.setFocusable(false);
//                        psi.setFocusable(false);
//                        pdes.setFocusable(false);
//                        pquantity.setFocusable(false);
//                    } else {
//                        pname.setFocusable(true);
//                        pmodel.setFocusable(true);
//                        psi.setFocusable(true);
//                        pdes.setFocusable(true);
//                        pquantity.setFocusable(true);
//                    }

                    //     addProductLayout.addView(view);


                }

//            editproductlist.add(list);

                String name = jsonChildNode.optString("name");
                String email = jsonChildNode.optString("email");
                String mobile = jsonChildNode.optString("mobile");
                String productname = jsonChildNode.optString("productname");
                String model = jsonChildNode.optString("model");
                String serialno = jsonChildNode.optString("serialno");
                String quantity = jsonChildNode.optString("quantity");
                String description = jsonChildNode.optString("description");
                String inprocessdes = jsonChildNode.optString("inprocessdes");
                String closedes = jsonChildNode.optString("closedes");
                String ticketstatus = jsonChildNode.optString("ticketstatus");
                String assignedto = jsonChildNode.optString("assignedto");

                Log.e(TAG, "ListDrwaer: " + ticketstatus + "      " + assignedto);

                String uname = usersMap.get(assignedto);
                int i = 0;
                for (String item : ddItems) {
                    if (item.equals(uname)) {
                        AssignedToField.setSelection(i);
                    }
                    i++;
                }

                AssignedToField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //int index = parent.getSelectedItemPosition();
                        assignedTo = parent.getItemAtPosition(position).toString();


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if (null != ticketstatus && "1".equalsIgnoreCase(ticketstatus)) {
                    MoreDescField.setText(inprocessdes);
                    //ticketstatus.setText(inprocessdes);
                    ticketStatusField.setSelection(0);
                } else if (null != ticketstatus && "2".equalsIgnoreCase(ticketstatus)) {
                    MoreDescField.setText(closedes);
                    // save(ticketStatus);
                    ticketStatusField.setSelection(1);
                } else if (null != ticketstatus && "3".equalsIgnoreCase(ticketstatus)) {
                    MoreDescField.setText(description);
                    //save(ticketStatus);
                    ticketStatusField.setSelection(2);
                }
                if (null != savejsonResult && "true".equals(savejsonResult)) {
                    Toast.makeText(getApplicationContext(), "Ticket Updated.", Toast.LENGTH_SHORT).show();
                    save(ticketStatus);
                }
                if (null != savejsonResult && "false".equals(savejsonResult)) {
                    Toast.makeText(getApplicationContext(), "Error Updating Ticket.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class Custom extends BaseAdapter {
        List<ProductDetails> productDetail;
        Context context;
        int[] imageId;
        private LayoutInflater inflater = null;

        public Custom(Context context, List<ProductDetails> productDetail) {
            // TODO Auto-generated constructor stub
            this.productDetail = productDetail;
            this.context = context;

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return productDetail.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder {
            TextView tv, tv1, tv2, tv3, tv4;
            ImageView img;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder = new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.layout_multiproduct, null);
            holder.tv = (TextView) rowView.findViewById(R.id.pname);
            holder.tv1 = (TextView) rowView.findViewById(R.id.pdes);
            holder.tv2 = (TextView) rowView.findViewById(R.id.pmodel);
            holder.tv3 = (TextView) rowView.findViewById(R.id.psi);
            holder.tv4 = (TextView) rowView.findViewById(R.id.pquantity);
            holder.tv.setText(productDetail.get(position).Prescription_medicid+" - "+productDetail.get(position).Prescription_id+" No(s)" );
            holder.tv1.setText(productDetail.get(position).Prescription_Date);
            holder.tv2.setText(productDetail.get(position).Prescription_next_appointment);

            holder.tv4.setText(productDetail.get(position).Prescription_doctorname);
            holder.img = (ImageView) rowView.findViewById(R.id.editReport);
            holder.img.setVisibility(View.VISIBLE);
            if(viewValue.equals("0")){
                holder.img.setImageResource(R.drawable.edit_128);
            }

           holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Toast.makeText(context, "You Clicked " + position, Toast.LENGTH_LONG).show();
                    Intent in = new Intent(context, PopupActivity.class);
                    in.putExtra("Value", "Update");
                    in.putExtra("View", viewValue);
                    in.putExtra("id", productDetail.get(position).pr_id);
                    //in.putExtra("editValue", editValue);
                    startActivityForResult(in, 2);
                }
            });
            return rowView;
        }

    }
}