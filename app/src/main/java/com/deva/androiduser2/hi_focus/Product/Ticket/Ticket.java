package com.deva.androiduser2.hi_focus.Product.Ticket;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.deva.androiduser2.hi_focus.HIFOCUS;
import com.deva.androiduser2.hi_focus.LocalStorageSQ;
import com.deva.androiduser2.hi_focus.R;
import com.deva.androiduser2.hi_focus.UserSessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Ticket extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public SearchView searchView;
    AllDetails.OnGoingPrescriptionAdapter1 adapter1;
    LinearLayout title;
    TextView title1;
    public TextView text;
    LocalStorageSQ db;
    String UserId;
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        session = new UserSessionManager(getApplicationContext());

        final HashMap<String, String> user = session.getUserDetails();
        String name = user.get(UserSessionManager.KEY_NAME);
        UserId = user.get(UserSessionManager.KEY_USERID);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        text = (TextView) findViewById(R.id.textView9);
        text.setTextColor(Color.parseColor("#ffffff"));
        title = (LinearLayout) findViewById(R.id.title);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#1c9eb4"));


        tabLayout.setupWithViewPager(viewPager);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setVisibility(View.GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                title.setVisibility(View.VISIBLE);
                return false;
            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    searchView.setVisibility(View.VISIBLE);
                    searchView.setIconified(true);
                } else {
                    searchView.setVisibility(View.GONE);
                    searchView.setIconified(true);
                    title.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    public SearchView getSearchView() {
        return searchView;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());


        if (UserId.equals("1")) {
            adapter.addFragment(new AllDetails1(), "Tickets");
            adapter.addFragment(new CreateTickets(), "New Ticket");

        } else {
            adapter.addFragment(new AllDetails1(), "Tickets");
            //adapter.addFragment(new AllDetails(), "AllDetails");
        }


        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i("senthil", "=========");
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //adapter1.filterData(newText);
        text.setVisibility(View.GONE);
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchView:
                text.setVisibility(View.GONE);
                break;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        int postive;
        String test;

        @Override
        public Fragment getItem(int position) {
            test = String.valueOf(position);
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {

            mFragmentList.add(fragment);

            mFragmentTitleList.add(title);
            Log.i("==================>", "Senthil1");
        }


        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        Bundle extras = getIntent().getExtras();

        boolean launchedFromNotif = false;

        if (extras != null) {
            if (extras.containsKey("EXTRA_LAUNCHED_BY_NOTIFICATION")) {
                launchedFromNotif = extras.getBoolean("EXTRA_LAUNCHED_BY_NOTIFICATION");
                //        db = new LocalStorageSQ(getActivity());
//        if(db.getOngoingPrescriptions().size() !=0){
//            db.deleteMedicinesList();
//        }



            }
        }

        if (launchedFromNotif) {
            // Launched from notification, handle as special case
            Intent intent = new Intent(this, HIFOCUS.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
