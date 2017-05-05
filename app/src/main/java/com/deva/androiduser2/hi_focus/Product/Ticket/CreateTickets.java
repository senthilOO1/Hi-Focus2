package com.deva.androiduser2.hi_focus.Product.Ticket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.deva.androiduser2.hi_focus.JSONParser;
import com.deva.androiduser2.hi_focus.LocalStorageSQ;
import com.deva.androiduser2.hi_focus.ProductDetails;
import com.deva.androiduser2.hi_focus.R;
import com.deva.androiduser2.hi_focus.UserSessionManager;
import com.deva.androiduser2.hi_focus.Utility;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CreateTickets extends Fragment {

    String[] ticket;
    View rootView;
    private String jsonResult;
    private String url = "http://hifocuscctv.com/apps/Android/insert.php";


    private String assignedToUrl = "http://hifocuscctv.com/apps/Android/getUsers.php";

    private EditText nameField, emailField, mobileField, ProductNameField, ModelField, Serial_noField, QuantityField, DescriptionField;
    private Button submitButton;
    private TextView plus;
    ListView listProducts;
    CustomAdapter mAdapter;
    private Spinner assignedto, status;
    private String[] ticketStatus = {"Assigned", "In Progress", "Closed", "Open"};
    Button home, aboutus, contactus;
    String tikStatus;
    LinearLayout addProductLayout, clickPro,products;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";


    List<String> asigneedToArray = new ArrayList<String>();
    ArrayAdapter<String> assinedToAdapter;

    ArrayList<AssignedModel> getAssignedId;

    private ArrayList<ArrayList<String>> list = new ArrayList<>();

    String name, email, mobile, ProductName, Model, Descriptions = "", serialNo, qty;

    String assignedId;
    String UserId;
    UserSessionManager session;

    LocalStorageSQ db;


    List<ProductDetails> productDetails = new ArrayList<ProductDetails>();

    @Override
    public void onStart() {
        Log.e("senthil", "2");
        db = new LocalStorageSQ(getActivity());
        if(db.getOngoingPrescriptions().size() !=0){
            db.deleteMedicinesList();
        }
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_ticket, container, false);

        addProductLayout = (LinearLayout) rootView.findViewById(R.id.addProductLayout);
        session = new UserSessionManager(getActivity());
        final HashMap<String, String> user = session.getUserDetails();
        String name = user.get(UserSessionManager.KEY_NAME);
        UserId = user.get(UserSessionManager.KEY_USERID);
        nameField = (EditText) rootView.findViewById(R.id.name);
        emailField = (EditText) rootView.findViewById(R.id.email);
        mobileField = (EditText) rootView.findViewById(R.id.mobile);
        assignedto = (Spinner) rootView.findViewById(R.id.assigned);
        status = (Spinner) rootView.findViewById(R.id.status);
        clickPro = (LinearLayout) rootView.findViewById(R.id.clickPro);
        listProducts = (ListView) rootView.findViewById(R.id.listProducts);
        products = (LinearLayout) rootView.findViewById(R.id.products);
        getAssignedId = new ArrayList<>();
        products.setVisibility(View.GONE);

        assinedToAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, asigneedToArray); //selected item will look like a spinner set from XML
        assinedToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignedto.setAdapter(assinedToAdapter);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, ticketStatus); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(spinnerArrayAdapter);
        submitButton = (Button) rootView.findViewById(R.id.submit);
        submitButton.setTextColor(Color.parseColor("#ffffff"));
        plus = (TextView) rootView.findViewById(R.id.plus);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                db = new LocalStorageSQ(getActivity());
//                db.deleteMedicinesList();
                Intent in = new Intent(getActivity(), PopupActivity.class);
                in.putExtra("Value", "AddProduct");
                in.putExtra("id", "add");
                in.putExtra("View", "0");
                startActivityForResult(in, 2);
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String email_id = emailField.getText().toString();
                final String mobile_id = mobileField.getText().toString();
                final String names = nameField.getText().toString();
                if (!isValidName(names)) {
                    nameField.setError("Invalid UserName");
                } else if (!isValidEmail(email_id)) {
                    emailField.setError("Invalid Email");
                } else if (!isValidPassword(mobile_id)) {
                    mobileField.setError("Invalid MobileNo");
                } else {

                    db = new LocalStorageSQ(getActivity());
                    productDetails = db.getOngoingPrescriptions();
                    if (productDetails.size() != 0) {
                        new AttemptSaveTickets().execute();
                    } else {
                        SpannableString spannableString = new SpannableString("Plz Add Products ");
                        spannableString.setSpan(
                                new ForegroundColorSpan(getResources().getColor(android.R.color.white)),
                                0,
                                spannableString.length(),
                                0);
                        Toast.makeText(getActivity(),spannableString, Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        boolean valu = Utility.internetIsAvailable(getActivity());

        if (valu == true) {

            new AttemptDistrictSearch().execute();

        } else {
            SpannableString spannableString = new SpannableString("No Internet Connection");
            spannableString.setSpan(
                    new ForegroundColorSpan(getResources().getColor(android.R.color.white)),
                    0,
                    spannableString.length(),
                    0);
            Toast.makeText(getActivity(), spannableString, Toast.LENGTH_LONG).show();
        }


        return rootView;
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
//            params.add(new BasicNameValuePair("firstname", username));
//            params.add(new BasicNameValuePair("lastname", password));
//            params.add(new BasicNameValuePair("email", email_id));
//            params.add(new BasicNameValuePair("phonenumber", phone));

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
                asigneedToArray.clear();
//                asigneedToArray.add("Assigned To");
                getAssignedId.clear();
//                getAssignedId.add(new AssignedModel("",""));
                try {
                    JSONObject jsonResponse = new JSONObject(file_url);

                    JSONArray jsonMainNode = jsonResponse.optJSONArray("assignedTo");

                    for (int i = 0; i < jsonMainNode.length(); i++) {

                        JSONObject c = jsonMainNode.getJSONObject(i);
                        String name = c.getString("username");
                        String id = c.getString("id");
                        asigneedToArray.add(name);
                        getAssignedId.add(new AssignedModel(id, name));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                assinedToAdapter.notifyDataSetChanged();
                assignedto.setAdapter(assinedToAdapter);
            } else {
                SpannableString spannableString = new SpannableString("Please connect internet.");
                spannableString.setSpan(
                        new ForegroundColorSpan(getResources().getColor(android.R.color.white)),
                        0,
                        spannableString.length(),
                        0);
                Toast.makeText(getContext(), spannableString, Toast.LENGTH_SHORT).show();
            }
//            Utility.hideProgressDialog(getActivity());

        }

    }


    public JSONObject createObject() {
        String name = nameField.getText().toString();
        String email = emailField.getText().toString();
        String mobile = mobileField.getText().toString();
        AssignedModel item = getAssignedId.get(assignedto.getSelectedItemPosition());
        String assignedtotxt = item.getId();
        Log.e(TAG, "createObject: assignedtotxt " + assignedtotxt);
        if (status.getSelectedItem().toString().equals("Assigned")) {
            tikStatus = "1";
        } else if (status.getSelectedItem().toString().equals("In Progress")) {
            tikStatus = "2";
        } else if (status.getSelectedItem().toString().equals("Closed")) {
            tikStatus = "3";
        } else if (status.getSelectedItem().toString().equals("Open")) {
            tikStatus = "4";
        }
        String statustxt = tikStatus;
        JSONObject mainObj = null;

        try {
            JSONArray ja = new JSONArray();
            db = new LocalStorageSQ(getActivity());
            productDetails = db.getOngoingPrescriptions();
            for (int i = 0; i < productDetails.size(); i++) {
                ArrayList<String> msg = list.get(i);
                JSONObject pnObj = new JSONObject();
                pnObj.put("productname", productDetails.get(i).Prescription_medicid);
                pnObj.put("description", productDetails.get(i).Prescription_doctorname);
                pnObj.put("model", productDetails.get(i).Prescription_Date);
                pnObj.put("serialno", productDetails.get(i).Prescription_next_appointment);
                pnObj.put("quantity", productDetails.get(i).Prescription_id);
                ja.put(pnObj);
            }

            mainObj = new JSONObject();
//            mainObj.put("ticketid", 23);
            mainObj.put("name", name);
            mainObj.put("mobile", mobile);
            mainObj.put("email", email);
            mainObj.put("ticketstatus", statustxt);
            mainObj.put("userid", 8);
            mainObj.put("assignedto", assignedtotxt);
            mainObj.put("products", ja);
            Log.v("Json oject", "===" + ja.toString());
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
                products.setVisibility(View.VISIBLE);
                ArrayList<String> msg = data.getStringArrayListExtra("msg");
                list.add(msg);
                clickPro.setVisibility(View.GONE);
                Log.e(TAG, "onActivityRest: " + msg);
                //addLayout(msg.get(0), msg.get(1), msg.get(2), msg.get(3), msg.get(4));
                db = new LocalStorageSQ(getActivity());
                productDetails = db.getOngoingPrescriptions();

                mAdapter = new CustomAdapter(getActivity(), productDetails);
                listProducts.setAdapter(mAdapter);
                Utility.getListViewSize(listProducts);
            } else {
                db = new LocalStorageSQ(getActivity());
                if (db.getOngoingPrescriptions().size() == 0) {
                    products.setVisibility(View.GONE);
                }
            }


        } else if (requestCode == 3) {
            if (data != null) {
                String msg = data.getStringExtra("Desp");
                Descriptions = msg;



            }else {

            }
        }

    }

    public void addLayout(String productname, String model, String sinumber, String description, String quantity) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_multiproduct, null);
        TextView pname, pdes, pmodel, psi, pquantity;
        pname = (TextView) view.findViewById(R.id.pname);

        pdes = (TextView) view.findViewById(R.id.pdes);

        pmodel = (TextView) view.findViewById(R.id.pmodel);

        psi = (TextView) view.findViewById(R.id.psi);

        pquantity = (TextView) view.findViewById(R.id.pquantity);

        pname.setText(productname);
        pdes.setText(description);
        pmodel.setText(model);
        psi.setText(sinumber);
        pquantity.setText(quantity);


        addProductLayout.addView(view);
        Log.e(TAG, "addLayout: called...");
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

            try {
                JSONObject user = createObject();
                JSONObject json = jsonParser.makeHttpRequesttest(url, "POST", user);
                Log.e(TAG, "doInBackground: Json return" + json);

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    db = new LocalStorageSQ(getActivity());
                    db.deleteMedicinesList();

                    Intent i = new Intent(getActivity(), Ticket.class);
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
            pDialog.dismiss();
            if (file_url != null) {
                SpannableString spannableString = new SpannableString(file_url);
                spannableString.setSpan(
                        new ForegroundColorSpan(getResources().getColor(android.R.color.white)),
                        0,
                        spannableString.length(),
                        0);
                Toast.makeText(getActivity(), spannableString, Toast.LENGTH_LONG).show();
            }

        }
    }

    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 9) {
            return true;
        }
        return false;
    }

    // validating password with retype password
    private boolean isValidName(String names) {
        if (names.length() > 0) {
            return true;
        }
        return false;
    }

    public class CustomAdapter extends BaseAdapter {
        List<ProductDetails> productDetail;
        Context context;
        int[] imageId;
        private LayoutInflater inflater = null;

        public CustomAdapter(Context context, List<ProductDetails> productDetail) {
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
            holder.img = (ImageView) rowView.findViewById(R.id.editReport);
            holder.tv4 = (TextView) rowView.findViewById(R.id.pquantity);
            holder.tv.setText(productDetail.get(position).Prescription_medicid+" - "+productDetail.get(position).Prescription_id+" No(s)" );
            holder.tv1.setText(productDetail.get(position).Prescription_Date);
            holder.tv2.setText(productDetail.get(position).Prescription_next_appointment);
            holder.tv4.setText(productDetail.get(position).Prescription_doctorname);


            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Toast.makeText(context, "You Clicked "+position, Toast.LENGTH_LONG).show();
                    Intent in = new Intent(context, PopupActivity.class);
                    in.putExtra("Value", "Update");
                    in.putExtra("View", "0");
                    in.putExtra("id", productDetail.get(position).pr_id);
                    startActivityForResult(in, 2);
                }
            });
            return rowView;
        }

    }
}
