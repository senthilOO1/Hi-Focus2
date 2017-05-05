package com.deva.androiduser2.hi_focus.Product;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.deva.androiduser2.hi_focus.R;

public class ProductsDetails extends AppCompatActivity {
    ListView lts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_details);

        lts = (ListView)findViewById(R.id.listView3);

        String[] values = new String[]{
                "Analog Technology",
                "HD-AHD Technology",
                "HD-CVI Technology",
                "HD-TVI Technology",
                "IP Technology",
                "Smart IP Cameras",
                "Video Door Phones",
                "BIOMETRIC SYSTEM"
        };
        Log.v("llllllllllllllll", "BIOMETRIC" );
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);

        lts.setAdapter(adapter);
        lts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("llllllllllllllll", "=====" + position);
                Toast.makeText(ProductsDetails.this,"dddd",Toast.LENGTH_LONG).show();
            }
        });
    }
}
