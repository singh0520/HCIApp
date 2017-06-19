package com.example.sangeetha.hciapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.ActionBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;


public class welcome extends Activity {

    String[] mPlaceTypeName=null;

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // GPS Location
    GPSTracker gps;

    ListView allOptionsList;

    public static String TERM_TO_SEARCH = "";
    public static String PASS = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_screen);
        MultiDex.install(this);

        ActionBar actionBar;
        actionBar = getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#0044ff"));
        actionBar.setBackgroundDrawable(colorDrawable);

        Boolean status = this.isGooglePlayServicesAvailable(this);

        if (status != true) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(0, this, requestCode);
            dialog.show();
            return;

        }
        cd = new ConnectionDetector(getApplicationContext());
        // Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(welcome.this, "Internet Connection Error",
                    "Please connect to the Internet", false);
            // stop executing code by return
            return;
        }

        RetrieveNewLocation newLcn = ((RetrieveNewLocation)getApplicationContext());
        if( ( Double.compare(newLcn.getUserLatitude(),0.0) == 0 ) && ( Double.compare(newLcn.getUserLongtitude(), 0.0) == 0 ) ){
            // creating GPS Class object
            gps = new GPSTracker(this);
            Log.d("my locatio is", "Gainesville");
        }
        else{
            gps = new GPSTracker(this);
            gps.latitude = newLcn.getUserLatitude();
            gps.longitude = newLcn.getUserLongtitude();

            Log.d("Lat from welcome class ", Double.toString(gps.latitude));
            Log.d("Long from welcome clas ", Double.toString(gps.longitude));

        }

        // check if GPS location can get
        if (gps.canGetLocation()) {
            Log.d("Your Location from welc", "latitude:" + gps.latitude + ", longitude: " + gps.longitude);
        } else {
            // Can't get user's current location
            alert.showAlertDialog(welcome.this, "GPS Status",
                    "Couldn't get location information. Please enable GPS",
                    false);
            // stop executing code by return
            return;
        }

        // Array of place type names
        mPlaceTypeName = getResources().getStringArray(R.array.place_type_name);
        allOptionsList = (ListView) findViewById(R.id.allOptions);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                mPlaceTypeName );

        allOptionsList.setAdapter(arrayAdapter);

        allOptionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item value
                String itemName= (String) allOptionsList.getItemAtPosition(position);

//                Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        DisplayAllResults.class);

                // Sending term to search for to DisplayAllResult activity
                in.putExtra(PASS, itemName);
                startActivity(in);


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.id_location:
                MenuOptions m = new MenuOptions(getApplicationContext());
                m.changeLocation();
                return true;
            case R.id.id_back_to_home:
                MenuOptions m1 = new MenuOptions(getApplicationContext());
                m1.backToHome();
                return true;
            case R.id.contact_us:
                MenuOptions m2 = new MenuOptions(getApplicationContext());
                m2.contactUs();
                return true;
            case R.id.about_us:
                MenuOptions m3 = new MenuOptions(getApplicationContext());
                m3.aboutApp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

    @Override
      protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
