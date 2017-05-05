package com.deva.androiduser2.hi_focus;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.deva.androiduser2.hi_focus.Product.Ticket.AssignedModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by androiduser2 on 29/3/16.
 */
public class Utility {

    public static ProgressDialog progress;

    public static void showProgressDialog(Context context) {
        progress = ProgressDialog.show(context, "HI-FOCUS",
                "Please wait", true);
    }

    public static void hideProgressDialog(Context context) {
        if (progress != null) {
            progress.dismiss();
        }
    }
    public static String convertDate(String inputDate) {

        DateFormat theDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;

        try {
            date = theDateFormat.parse(inputDate);
        } catch (ParseException parseException) {
            // Date is invalid. Do what you want.
        } catch (Exception exception) {
            // Generic catch. Do what you want.
        }

        theDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return theDateFormat.format(date);
    }

    public static String convertTime(String inputDate) {

        DateFormat theDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;

        try {
            date = theDateFormat.parse(inputDate);
        } catch (ParseException parseException) {
            // Date is invalid. Do what you want.
        } catch (Exception exception) {
            // Generic catch. Do what you want.
        }

        theDateFormat = new SimpleDateFormat("HH:mm:ss");

        return theDateFormat.format(date);
    }
    public static ArrayList<AssignedModel> assignedList =new ArrayList<>();

    public static void getListViewSize(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
        // print height of adapter on log
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }
    public static boolean internetIsAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (netInfo != null
                    && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            } else {
                netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (netInfo != null
                        && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }


}
