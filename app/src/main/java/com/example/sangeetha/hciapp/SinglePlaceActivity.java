package com.example.sangeetha.hciapp;

/**
 * Created by sangeetha on 3/30/16.
 */

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.util.List;

public class SinglePlaceActivity extends FragmentActivity implements LocationListener  {

    GoogleMap mGoogleMap;
    double mLatitude=0;
    double mLongitude=0;

    // GPS Location
    GPSTracker gps;


    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Google Places
    GooglePlaces googlePlaces;

    // Place Details
    PlaceDetails placeDetails;

    // Progress dialog
    ProgressDialog pDialog;

    // KEY Strings
    public static String KEY_REFERENCE = "reference"; // id of the place

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_place);

        ActionBar actionBar;
        actionBar = getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#0044ff"));
        actionBar.setBackgroundDrawable(colorDrawable);

        // Getting reference to the SupportMapFragment

        SupportMapFragment fragment = ( SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Google Map
        mGoogleMap = fragment.getMap();

        RetrieveNewLocation newLcn = ((RetrieveNewLocation)getApplicationContext());
        if( ( Double.compare(newLcn.getUserLatitude(),0.0) == 0 ) && ( Double.compare(newLcn.getUserLongtitude(), 0.0) == 0 ) ){
            // creating GPS Class object
            gps = new GPSTracker(this);
            Log.d("my locatio is", "Gainesville");

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d("location is ", location.toString());

            if(location!=null){
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }
        else{
            gps = new GPSTracker(this);
            gps.latitude = newLcn.getUserLatitude();
            gps.longitude = newLcn.getUserLongtitude();

            Log.d("Lat from welcome class ", Double.toString(gps.latitude));
            Log.d("Long from welcome clas ", Double.toString(gps.longitude));

            Location location = new Location("selected");
            location.setLatitude(gps.latitude);
            location.setLongitude(gps.longitude);

            if(location != null){
                onLocationChanged(location);
            }

        }


        // Enabling MyLocation in Google Map
        mGoogleMap.setMyLocationEnabled(true);

        Intent i = getIntent();

        // Place referece id
        String reference = i.getStringExtra(KEY_REFERENCE);

        // Calling a Async Background thread
        new LoadSinglePlaceDetails().execute(reference);
    }



    /**
     * Background Async Task to Load Google places
     * */
    class LoadSinglePlaceDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SinglePlaceActivity.this);
            pDialog.setMessage("Loading profile ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Profile JSON
         * */
        protected String doInBackground(String... args) {
            String reference = args[0];

            // creating Places class object
            googlePlaces = new GooglePlaces();

            // Check if used is connected to Internet
            try {
                placeDetails = googlePlaces.getPlaceDetails(reference);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed Places into LISTVIEW
                     * */
                    if (placeDetails != null) {
                        String status = placeDetails.status;

                        // check place deatils status
                        // Check for all possible status
                        if (status.equals("OK")) {
                            if (placeDetails.result != null) {
                                String name = placeDetails.result.name;
                                getActionBar().setTitle(name);
                                String address = placeDetails.result.formatted_address;
                                String phone = placeDetails.result.formatted_phone_number;
                                String latitude = Double.toString(placeDetails.result.geometry.location.lat);
                                String longitude = Double.toString(placeDetails.result.geometry.location.lng);
                                String webSite = placeDetails.result.website;
                                String icon = placeDetails.result.icon;
                                List<String> open_hours;
                                String rating;

                                if(placeDetails.result.opening_hours == null || placeDetails.result.opening_hours.weekday_text == null){
                                    open_hours = null;
                                }
                                else{
                                    open_hours = placeDetails.result.opening_hours.weekday_text;
                                }

                                if(placeDetails.result.rating == null){
                                    rating = null;
                                }
                                else{
                                    rating = Double.toString(placeDetails.result.rating);
                                }

                                Log.d("Place ", name + address + phone + latitude + longitude);

                                // Displaying all the details in the view
                                // single_place.xml
                                TextView lbl_name = (TextView) findViewById(R.id.name);
                                TextView lbl_address = (TextView) findViewById(R.id.address);
                                TextView lbl_phone = (TextView) findViewById(R.id.phone);
                                TextView lbl_website = (TextView) findViewById(R.id.website);
                                TextView lbl_rating = (TextView) findViewById(R.id.rating);
                                TextView lbl_contact_us = (TextView) findViewById(R.id.contact_label);
                                TextView lbl_open_hours = (TextView) findViewById(R.id.hours);
                                TextView lbl_hours_label = (TextView) findViewById(R.id.hours_label);
                                ImageView lbl_icon = (ImageView)findViewById(R.id.imageView);

                                if(open_hours == null || open_hours.isEmpty()){
                                    lbl_hours_label.setVisibility(View.GONE);
                                    lbl_open_hours.setVisibility(View.GONE);
                                }
                                else{
                                    Log.d("open hours found", "yes");
                                    String all_hours="";
                                    for( Object str: open_hours){
                                        Log.d("String is ", str.toString());
                                        all_hours+=str;
                                        all_hours+="\n";
                                    }

                                    lbl_open_hours.setText(all_hours);
                                }

                                if(icon == null){
                                    Log.d("icon is ", "null");
                                    lbl_icon.setVisibility(View.GONE);
                                }
                                else{
                                    new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                                            .execute(icon);
                                }

                                if(name == null){
                                    lbl_name.setVisibility(View.GONE);
                                }
                                else{
                                    lbl_name.setText(Html.fromHtml("<b>Name:</b> " + name));
                                }

                                if(webSite == null && phone == null){
                                        Log.d("got here", "i am here");
                                        lbl_contact_us.setVisibility(View.GONE);

                                }

                                if(phone == null){
                                    lbl_phone.setVisibility(View.GONE);
                                }
                                else{
                                    lbl_phone.setText(Html.fromHtml("<b>Phone: "+phone + "</b>"));
                                }

                                if(webSite == null){
                                    lbl_website.setVisibility(View.GONE);
                                }
                                else{
                                    lbl_website.setText(Html.fromHtml("<b>Website: " + webSite + "</b>"));
                                }

                                if(address == null){
                                    lbl_address.setVisibility(View.GONE);
                                }
                                else{
                                    lbl_address.setText(Html.fromHtml("<b>Address:</b> " + address));
                                }

                                if(rating == null){
                                    lbl_rating.setVisibility(View.GONE);
                                }
                                else{
                                    lbl_rating.setText(Html.fromHtml("<b>Rating:</b> " + rating));
                                }

                                // Clears all the existing markers
                                mGoogleMap.clear();

                                // Creating a marker
                                MarkerOptions markerOptions = new MarkerOptions();

                                // Getting a place from the places list
//                                    HashMap<String, String> hmPlace = list.get(i);

                                // Getting latitude of the place
                                double lat = Double.parseDouble(latitude);
                                Log.d("Latitude is ", latitude);

                                // Getting longitude of the place
                                double lng = Double.parseDouble(longitude);
                                Log.d("Longtitude is ", longitude);

                                // Getting name
//                                    String name = hmPlace.get("place_name");

                                // Getting vicinity
//                                    String vicinity = hmPlace.get("vicinity");

                                LatLng latLng = new LatLng(lat, lng);

                                // Setting the position for the marker
                                markerOptions.position(latLng);

                                // Setting the title for the marker.
                                //This will be displayed on taping the marker
                                markerOptions.title(name);

                                // Placing a marker on the touched position
                                mGoogleMap.addMarker(markerOptions);

                                Location selectedLocation = new Location("selected_location");
                                selectedLocation.setLatitude(lat);
                                selectedLocation.setLongitude(lng);
                                if(selectedLocation != null){
                                    onLocationChanged(selectedLocation);
                                }


                            }
                        } else if (status.equals("ZERO_RESULTS")) {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Near Places",
                                    "Sorry no place found.",
                                    false);
                        } else if (status.equals("UNKNOWN_ERROR")) {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry unknown error occured.",
                                    false);
                        } else if (status.equals("OVER_QUERY_LIMIT")) {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry query limit to google places is reached",
                                    false);
                        } else if (status.equals("REQUEST_DENIED")) {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry error occured. Request is denied",
                                    false);
                        } else if (status.equals("INVALID_REQUEST")) {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry error occured. Invalid Request",
                                    false);
                        } else {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry error occured.",
                                    false);
                        }
                    } else {
                        alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                "Sorry error occured.",
                                false);
                    }
                }
            });

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        Log.d("location changed", "successfully");
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }
    //
    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }
    //
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
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

}
