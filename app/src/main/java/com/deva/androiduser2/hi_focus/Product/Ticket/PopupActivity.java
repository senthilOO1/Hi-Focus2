package com.deva.androiduser2.hi_focus.Product.Ticket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.deva.androiduser2.hi_focus.LocalStorageSQ;
import com.deva.androiduser2.hi_focus.ProductDetails;
import com.deva.androiduser2.hi_focus.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PRMobile on 5/23/16.
 */
public class PopupActivity extends Activity {

    TextView remove,cancel,ok;

    EditText productname,model,sinumber,description,quantity;

    LocalStorageSQ db;

    String userName,id,view;

    ProductDetails productDetails;

    List<ProductDetails> productGet = new ArrayList<ProductDetails>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productpopup);

        db = new LocalStorageSQ(this);
        productDetails = new ProductDetails();


        cancel= (TextView) findViewById(R.id.cancel);
        ok= (TextView) findViewById(R.id.ok);
        productname= (EditText) findViewById(R.id.productname);
        model= (EditText) findViewById(R.id.model);
        sinumber= (EditText) findViewById(R.id.sinumber);
        description= (EditText) findViewById(R.id.Description);
        quantity= (EditText) findViewById(R.id.Quantity);
        Bundle extras = getIntent().getExtras();
         userName = extras.getString("Value");

         view = extras.getString("View");

         if(view.equals("1")){
             //ok.setEnabled(false);
             cancel.setVisibility(View.VISIBLE);
             ok.setBackgroundColor(Color.parseColor("#d2d2d2"));
             ok.setEnabled(false);
             productname.setFocusableInTouchMode(false);
             model.setFocusableInTouchMode(false);
             sinumber.setFocusableInTouchMode(false);
             description.setFocusableInTouchMode(false);
             quantity.setFocusableInTouchMode(false);
             productname.setEnabled(false);
             model.setEnabled(false);
             sinumber.setEnabled(false);
             description.setEnabled(false);
             quantity.setEnabled(false);
         }else if(view.equals("0")){
            // ok.setEnabled(true);
             cancel.setVisibility(View.VISIBLE);
             ok.setBackgroundColor(Color.parseColor("#15b603"));
             ok.setEnabled(true);
             productname.setEnabled(true);
             model.setEnabled(true);
             sinumber.setEnabled(true);
             description.setEnabled(true);
             quantity.setEnabled(true);
         }

        if(userName.equals("AddProduct"))
        {

        }else  if(userName.equals("Update")){
             id = extras.getString("id");
            productGet = db.getOngoingPr(id);
            productname.setText(productGet.get(0).Prescription_medicid);
            model.setText(productGet.get(0).Prescription_Date);
            sinumber.setText(productGet.get(0).Prescription_next_appointment);
            description.setText(productGet.get(0).Prescription_doctorname);
            quantity.setText(productGet.get(0).Prescription_id);

        }


//        sinumber.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
//        sinumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                sinumber.setInputType(InputType.TYPE_CLASS_NUMBER);
//                return false;
//            }
//        });
//        sinumber.setRawInputType(InputType.TYPE_CLASS_NUMBER);



        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent();
////                intent.putStringArrayListExtra("msg","");
//                setResult(2,intent);
                finish();//finishing activity
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(view.equals("1")){
                    finish();
                }else if(view.equals("0")) {
                    productDetails.Prescription_medicid = productname.getText().toString();
                    productDetails.Prescription_Date = model.getText().toString();
                    productDetails.Prescription_next_appointment = sinumber.getText().toString();
                    productDetails.Prescription_doctorname = description.getText().toString();
                    productDetails.Prescription_id = quantity.getText().toString();
                    if (userName.equals("AddProduct")) {
                        db.addPrescription(productDetails);


                        ArrayList<String> msg = new ArrayList<String>();

                        msg.add(productname.getText().toString());
                        msg.add(model.getText().toString());
                        msg.add(sinumber.getText().toString());
                        msg.add(description.getText().toString());
                        msg.add(quantity.getText().toString());

                        Intent intent = new Intent();
                        intent.putStringArrayListExtra("msg", msg);
                        setResult(2, intent);
                        finish();//finishing activity
                    } else if (userName.equals("Update")) {
                        db.updateMedicines(productDetails, id);


                        ArrayList<String> msg = new ArrayList<String>();

                        msg.add(productname.getText().toString());
                        msg.add(model.getText().toString());
                        msg.add(sinumber.getText().toString());
                        msg.add(description.getText().toString());
                        msg.add(quantity.getText().toString());

                        Intent intent = new Intent();
                        intent.putStringArrayListExtra("msg", msg);
                        setResult(2, intent);
                        finish();//finishing activity
                    }

                }
            }
        });

    }
}
