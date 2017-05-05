package com.deva.androiduser2.hi_focus.Product.Ticket;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.deva.androiduser2.hi_focus.GetAllDetails;
import com.deva.androiduser2.hi_focus.LocalStorageSQ;
import com.deva.androiduser2.hi_focus.R;
import com.deva.androiduser2.hi_focus.UserSessionManager;
import com.deva.androiduser2.hi_focus.Utility;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AllDetails1 extends Fragment {
    View rootView;
    private String jsonResult;
    private String url = "http://hifocuscctv.com/apps/Android/get_supports.php";
    private ListView listView;
    ArrayList<GetAllDetails> prescItem;
    EditText edittext;
    Button home, aboutus, contactus;
    OnGoingPrescriptionAdapter1 mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    String ticket = "",ticketStatusName ="";
    UserSessionManager session;
    LocalStorageSQ db;
    TextView Ticket_Id, Ticket_No, Mobile, Name, Status;
    public SimpleAdapter adapter;
    String UserId,Username;


//    @Override
//    public void onStart() {
//
//        db = new LocalStorageSQ(getActivity());
//        if(db.getOngoingPrescriptions().size() !=0){
//            db.deleteMedicinesList();
//        }
//
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.supportlayout, container, false);
        session = new UserSessionManager(getActivity());
        final HashMap<String, String> user = session.getUserDetails();
        Username = user.get(UserSessionManager.KEY_USERNAME);
        UserId = user.get(UserSessionManager.KEY_USERID);
        db = new LocalStorageSQ(getActivity());
        db.deleteMedicinesList();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        Ticket mTicket = (Ticket) getActivity();
        mTicket.getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("============>", "sss" + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("============>", "sss" + newText);
                mAdapter.filterData(newText);
                return false;
            }
        });



        // this is data fro recycler view

        // 2. set layoutManger
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 3. create an adapter
        boolean valu = Utility.internetIsAvailable(getActivity());

        if(valu == true){
            accessWebService();
        }else {
            Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_LONG).show();
        }


        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        return rootView;
    }

    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[]{url});
    }

    // Async Task to access the web
    private class JsonReadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utility.showProgressDialog(getActivity());
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            Intent myIntent = getActivity().getIntent();
            String ticketid = myIntent.getStringExtra("ticketid");

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("ticketid", ticketid));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                Log.i("ListTicketsActivity", "nameValuePairs =>>" + nameValuePairs);
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
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
                Toast.makeText(getActivity(),
                        "Error..." + e.toString(), Toast.LENGTH_LONG).show();
            }

            Log.i("ListTicketsActivity", "is -> String =>>" + answer);
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            ListDrawer();
            Utility.hideProgressDialog(getActivity());
        }
    }// end async task


    // build hash set for list view
    public void ListDrawer() {

        prescItem = new ArrayList<GetAllDetails>();
        ;

        if (null != jsonResult && !"".equals(jsonResult)) {


            List<Map<String, String>> menuList = new ArrayList<Map<String, String>>();

            try {
                JSONObject jsonResponse = new JSONObject(jsonResult);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("tickets");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    HashMap<String, String> itemMap = new HashMap<String, String>();
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    String id = jsonChildNode.optString("ticketid");
                    String ticketno = jsonChildNode.optString("ticketno");
                    String name = jsonChildNode.optString("name");
                    String mobile = jsonChildNode.optString("mobile");
                    String email = jsonChildNode.optString("email");
                    String assignedTo = jsonChildNode.optString("assignedto");
                    String date = jsonChildNode.optString("createdtime");
                    String time = Utility.convertTime(jsonChildNode.optString("createdtime"));
                    String ticketStatus = jsonChildNode.optString("ticketstatus");
                    String closed = jsonChildNode.optString("closedes");
                    String inProgress = jsonChildNode.optString("indes");

                    if (ticketStatus.equals("1")) {
                        //Status.setTextColor(Color.GREEN);
                        ticket = "In Progress";
                    } else if (ticketStatus.equals("2")) {
                        // Status.setTextColor(Color.RED);
                        ticket = "Closed";
                    } else if (ticketStatus.equals("3")) {
                        //Status.setTextColor(Color.BLUE);
                        ticket = "Open";
                    }
                    if (ticketStatus.equals("1")) {

                        ticketStatusName = "Assigned";
                    } else if (ticketStatus.equals("2")) {

                        ticketStatusName = "In Progress";
                    } else if (ticketStatus.equals("3")) {

                        ticketStatusName = "Closed";
                    }else if (ticketStatus.equals("4")) {

                        ticketStatusName = "Open";
                    }
                    String assignedValues = null;
                    if (Utility.assignedList.size() > 0) {
                        for (int j = 0; j < Utility.assignedList.size(); j++) {

                            AssignedModel model = Utility.assignedList.get(j);


                            if (model.getId().equals(assignedTo)) {
                                assignedValues = model.getUsername();
                            }


                        }
                    }


                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                    String dateStart = df.format(c.getTime());

                    String dateStop = "" +date;

                    String dateValue = "",hoursValue="",minutesValue="",secondsValue="",finalValue="";

                    //Log.i("","ddd "+dateStop);

                    //HH converts hour in 24 hours format (0-23), day calculation
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                    Date d1 = null;
                    Date d2 = null;
                    long diffDays = 0;
                    long diffMinutes;

                    try {
                        d1 = format.parse(dateStart);
                        d2 = format.parse(dateStop);

                        //in milliseconds
                        long diff = d1.getTime() - d2.getTime();

                        long diffSeconds = diff / 1000;
                        secondsValue =""+diffSeconds;

                        diffMinutes = diff / (60 * 1000);
                        minutesValue =""+diffMinutes;
                        long diffHours = diff / (60 * 60 * 1000);
                        hoursValue =""+diffHours;

                        diffDays = diff / (24 * 60 * 60 * 1000);

                        System.out.print(diffDays + " days, ");

                        Log.i("dede", "" + diffDays);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(Integer.parseInt(minutesValue) >=0) {
                        if (diffDays == 0) {
                            if (!hoursValue.equals("0")) {
                                finalValue = hoursValue + " Hour(s) ago";
                            } else if (!minutesValue.equals("0")) {
                                finalValue = minutesValue + " Minute(s) ago";
                            } else if (!secondsValue.equals("0")) {
                                finalValue = secondsValue + " Second(s) ago";
                            }

                        } else if (diffDays > 0) {
                            finalValue = diffDays + " Day(s) ago";
                        }
                    }else {
                        finalValue = "Just Now";
                    }
                    itemMap.put("ticketid", id);
                    itemMap.put("ticketno", ticketno);
                    itemMap.put("name", name);
                    itemMap.put("mobile", mobile);
                    itemMap.put("ticketstatus", ticketStatus);
                    itemMap.put("ticketstatusName", ticketStatusName);
                    itemMap.put("createdtime", date);
                    itemMap.put("email", email);
                    itemMap.put("assignedTo", assignedTo);
                    itemMap.put("closed", closed);
                    itemMap.put("inProgress", inProgress);

                    menuList.add(itemMap);
                    if(UserId.equals("1")){
                        GetAllDetails audio = new GetAllDetails(name, ticketno, id, mobile, ticketStatus, date, email, assignedTo, assignedValues,closed,ticketStatusName,inProgress);
                        prescItem.add(audio);

                    }else {
                        if(assignedValues.equals(Username.toLowerCase())){
                            GetAllDetails audio = new GetAllDetails(name, ticketno, id, mobile, ticketStatus, date, email, assignedTo, assignedValues,closed,ticketStatusName,inProgress);
                            prescItem.add(audio);
                        }
                    }



                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), "Error" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }

//            String[] from = {"ticketid", "ticketno", "name", "mobile", "ticketstatus","createdtime"};
//            int[] to = {R.id.id, R.id.ticketno, R.id.name, R.id.mobile, R.id.ticketstatus,R.id.dateReport};
//            adapter = new SimpleAdapter(getActivity().getBaseContext(), menuList, R.layout.activity_list_tickets_details, from, to);
//            listView.setAdapter(adapter);

            mAdapter = new OnGoingPrescriptionAdapter1(getActivity(), prescItem, prescItem);
            // 4. set adapter
            mRecyclerView.setAdapter(mAdapter);


        }

    }

    public void search(String s) {
        adapter.getFilter().filter(s.toString());
    }

    public class OnGoingPrescriptionAdapter1 extends RecyclerView.Adapter<OnGoingPrescriptionAdapter1.ViewHolder> {
        protected List<GetAllDetails> listCars;
        private List<GetAllDetails> originalList;
        Context context;

        public OnGoingPrescriptionAdapter1(Context context, List<GetAllDetails> listCars1, List<GetAllDetails> listCars2) {
            this.listCars = listCars2;
            this.originalList = new ArrayList<>();
            originalList.addAll(listCars1);


            this.context = context;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_alldetails, null);

            // create ViewHolder

            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }


        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {


            final GetAllDetails prescItem = listCars.get(position);



            if (prescItem.getProductName().equals("")) {
                viewHolder.name.setText("Unknown");
            } else {

                viewHolder.name.setText( prescItem.getProductName());
            }

            viewHolder.status.setText( prescItem.getTicketStatusName());


            viewHolder.date.setText(prescItem.getProductDate());

//            if (prescItem.getAssignedToName().equals("")||prescItem.getAssignedToName()==null) {
//                viewHolder.mobile.setText("Unassigned");
//            } else {
//
//                viewHolder.mobile.setText(prescItem.getAssignedToName());
//            }

            viewHolder.ticket.setText("#" + prescItem.getProductTicket());



            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), EditTicketActivity.class);
                    intent.putExtra("ticketid", prescItem.getProductId());
                    intent.putExtra("name", prescItem.getProductName());
                    intent.putExtra("email", prescItem.getEmail());
                    intent.putExtra("mobile", prescItem.getProductMobile());
                    intent.putExtra("assingedto", prescItem.getAssignedTo());
                    intent.putExtra("status", prescItem.getProductStatus());
                    intent.putExtra("ticketNo", prescItem.getProductTicket());
                    intent.putExtra("closed", prescItem.getGetClosed());
                    intent.putExtra("indes",prescItem.getInProgress());
                    db = new LocalStorageSQ(getActivity());
                    db.deleteMedicinesList();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            });

        }

        private static final String TAG = "OnGoingPrescriptionAdapter1";

        public void filterData(String query) {

            query = query.toLowerCase();
            Log.v("PrescListAdapter", String.valueOf(listCars.size()));

            listCars.clear();
            if (query.isEmpty()) {
                listCars.addAll(originalList);
            } else {
                for (GetAllDetails continent : originalList) {
                    if (continent.getProductName().toLowerCase().contains(query) || continent.getProductDate().toLowerCase().contains(query) ||
                            continent.getProductMobile().toLowerCase().contains(query) || continent.getProductTicket().toLowerCase().contains(query)
                            || continent.getProductStatus().toLowerCase().contains(query) || continent.getAssignedToName().toLowerCase().contains(query) || continent.getTicketStatusName().toLowerCase().contains(query)) {
                        listCars.add(continent);
                    }
                    List<GetAllDetails> countryList = new ArrayList<GetAllDetails>();
                    ;
                    ArrayList<GetAllDetails> newList = new ArrayList<GetAllDetails>();
                    Log.v("PrescListpter", String.valueOf(listCars.size()));
                    for (GetAllDetails country : countryList) {
//                        if (country.getCode().toLowerCase().contains(query) ||
//                                country.getName().toLowerCase().contains(query)) {
//                            newList.add(country);
//                        }
                    }
                    if (newList.size() > 0) {
//                        GetAllDetails nContinent = new GetAllDetails(continent.getName(), newList);
//                        continentList.add(nContinent);
                    }
                }
            }
            Log.v("PrescListAdapter", String.valueOf(listCars.size()));
            notifyDataSetChanged();
        }

        public void filterUser(String query) {

            query = query.toLowerCase();
            Log.v("PrescListAdapter", String.valueOf(listCars.size()));

            listCars.clear();
            if (query.isEmpty()) {
                listCars.addAll(originalList);
            } else {
                for (GetAllDetails continent : originalList) {
                    if (continent.getProductName().toLowerCase().contains(query) || continent.getProductDate().toLowerCase().contains(query) ||
                            continent.getProductMobile().toLowerCase().contains(query) || continent.getProductTicket().toLowerCase().contains(query)
                            || continent.getProductStatus().toLowerCase().contains(query)) {
                        listCars.add(continent);
                    }
                    List<GetAllDetails> countryList = new ArrayList<GetAllDetails>();
                    ;
                    ArrayList<GetAllDetails> newList = new ArrayList<GetAllDetails>();
                    Log.v("PrescListpter", String.valueOf(listCars.size()));
                    for (GetAllDetails country : countryList) {
//                        if (country.getCode().toLowerCase().contains(query) ||
//                                country.getName().toLowerCase().contains(query)) {
//                            newList.add(country);
//                        }
                    }
                    if (newList.size() > 0) {
//                        GetAllDetails nContinent = new GetAllDetails(continent.getName(), newList);
//                        continentList.add(nContinent);
                    }
                }
            }
            Log.v("PrescListAdapter", String.valueOf(listCars.size()));
            notifyDataSetChanged();
        }

        // Return the size of your itemsData (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return listCars.size();
        }

        // inner class to hold a reference to each item of RecyclerView
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, mobile, ticket, date, status;
            LinearLayout linearLayout;


            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                name = (TextView) itemLayoutView.findViewById(R.id.name);

                mobile = (TextView) itemLayoutView.findViewById(R.id.mobile);

                ticket = (TextView) itemLayoutView.findViewById(R.id.ticketno);

                date = (TextView) itemLayoutView.findViewById(R.id.dateReport);

                status = (TextView) itemLayoutView.findViewById(R.id.ticketstatus);

                linearLayout = (LinearLayout) itemLayoutView.findViewById(R.id.allDetailsLayout);


            }
        }
    }
}
