package com.example.y.contentproviderexample;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    MyAdapter myAdapter;
    // The ListView
    private ListView lstNames;
    private ArrayList<String> al_contactName,al_contactNumber;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        al_contactName = new ArrayList<>();
        al_contactNumber = new ArrayList<>();
        showContacts();
        myAdapter = new MyAdapter();
        lstNames = (ListView) findViewById(R.id.lstNames);
        lstNames.setAdapter(myAdapter);
    }

    private void displayAllContacts(){
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        if(cursor.getCount()>0){
            while (cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))>0){
                    Cursor pCur = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?",
                            new String[]{id},null);
                    while (pCur.moveToNext()){
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        al_contactName.add(name);
                        al_contactNumber.add(phoneNo);
                    }
                }

            }
        }
    }
    /**
     * Show the contacts in the ListView.
     */
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            displayAllContacts();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return al_contactName.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.name_and_contacts,parent,false);
                holder = new ViewHolder();
                holder.bindView(convertView);
                convertView.setTag(holder);

            }
            else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.contactName.setText(al_contactName.get(position));
            holder.contactNumber.setText(al_contactNumber.get(position));
            return convertView;
        }
    }
    public class ViewHolder{
        TextView contactName, contactNumber;
        void bindView(View convertView){
            contactName = (TextView)convertView.findViewById(R.id.contact_name);
            contactNumber = (TextView)convertView.findViewById(R.id.contact_number);
        }
    }
}
