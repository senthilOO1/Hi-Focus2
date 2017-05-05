package com.deva.androiduser2.hi_focus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by krishna on 5/10/15.
 */
public class LocalStorageSQ extends SQLiteOpenHelper {



    private static final String DB_NAME = "offline.db";
    private static final int DB_VERSION = 4;
    // Hospital Table


    private static final String TABLE_PATIENT_PRESCRIPTION = "contacts";
    private static final String PR_PID = "prid";
    private static final String PR_MEDIC_ID = "medic";
    private static final String PR_PRESCRIPTION_ID = "prescription_id";
    private static final String PR_HOSPITAL_NAME = "hospital_name";
    private static final String PR_DOCTOR_NAME = "doctor_name";
    private static final String PR_HOSPITAL_PHONE = "hospital_phone";



    private Context mContext;

    public LocalStorageSQ(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {



        String CREATE_PRESCRIPTION_TABLE = "CREATE TABLE " + TABLE_PATIENT_PRESCRIPTION + "("
                + PR_PID + " INTEGER PRIMARY KEY," + PR_MEDIC_ID + " TEXT," + PR_PRESCRIPTION_ID + " TEXT," + PR_HOSPITAL_NAME + " TEXT," + PR_DOCTOR_NAME + " TEXT," + PR_HOSPITAL_PHONE + " TEXT " + ")";



        db.execSQL(CREATE_PRESCRIPTION_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older  table if existed

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT_PRESCRIPTION);

        // create fresh  table
        this.onCreate(db);
    }


    // code to add the new contact
    public void addPrescription(ProductDetails list1) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();



            values.put(PR_MEDIC_ID, list1.Prescription_medicid);
            values.put(PR_PRESCRIPTION_ID, list1.Prescription_id); // Contact Name
            values.put(PR_PID, list1.pr_id);
            // Contact Phone
            values.put(PR_HOSPITAL_NAME, list1.Prescription_Date);
            values.put(PR_DOCTOR_NAME, list1.Prescription_doctorname);
            values.put(PR_HOSPITAL_PHONE, list1.Prescription_next_appointment);


                db.insert(TABLE_PATIENT_PRESCRIPTION, null, values);




        db.close();


    }

    public List<ProductDetails> getOngoingPrescriptions() {
        List<ProductDetails> contactList = new ArrayList<ProductDetails>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PATIENT_PRESCRIPTION ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ProductDetails contact = new ProductDetails();
                contact.pr_id = cursor.getString(0);
                contact.Prescription_medicid = cursor.getString(1);
                contact.Prescription_id = cursor.getString(2);
                contact.Prescription_doctorname = cursor.getString(4);
                contact.Prescription_next_appointment = cursor.getString(5);
                contact.Prescription_Date = cursor.getString(3);
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // return contact list
        return contactList;
    }
    public List<ProductDetails> getOngoingPr(String pro_id) {
        List<ProductDetails> contactList = new ArrayList<ProductDetails>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PATIENT_PRESCRIPTION +" where prid = "+"'"+pro_id+"'" ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ProductDetails contact = new ProductDetails();
                contact.pr_id = cursor.getString(0);
                contact.Prescription_medicid = cursor.getString(1);
                contact.Prescription_id = cursor.getString(2);
                contact.Prescription_doctorname = cursor.getString(4);
                contact.Prescription_next_appointment = cursor.getString(5);
                contact.Prescription_Date = cursor.getString(3);
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // return contact list
        return contactList;
    }
    public void deleteMedicinesList() {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from " + TABLE_PATIENT_PRESCRIPTION);

        db.close();
    }
    // Update Query Settings
    public void updateMedicines(ProductDetails medicinesList,String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();



            values.put(PR_MEDIC_ID, medicinesList.Prescription_medicid);
            values.put(PR_PRESCRIPTION_ID, medicinesList.Prescription_id); // Contact Name

            // Contact Phone
            values.put(PR_HOSPITAL_NAME, medicinesList.Prescription_Date);
            values.put(PR_DOCTOR_NAME, medicinesList.Prescription_doctorname);
            values.put(PR_HOSPITAL_PHONE, medicinesList.Prescription_next_appointment);
            int ss = db.update(TABLE_PATIENT_PRESCRIPTION, values, PR_PID + "=" +id, null);



        //Log.i("SS", "===>" + ss);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }
}
