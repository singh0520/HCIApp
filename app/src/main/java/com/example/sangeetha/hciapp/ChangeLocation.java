package com.example.sangeetha.hciapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by sangeetha on 3/27/16.
 */
public class ChangeLocation extends Activity {

    EditText new_location;
    Button submit;
    String location = "";

    // Google Places
    GooglePlaces googlePlaces;
    // Place Details
    PlacesList placeDetails;

    // Progress dialog
    ProgressDialog pDialog;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();


    private static final String GEO_CODE_SERVER = "http://maps.googleapis.com/maps/api/geocode/json?";
    static String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_location);

        ActionBar actionBar;
        actionBar = getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#0044ff"));
        actionBar.setBackgroundDrawable(colorDrawable);

        submit = (Button) findViewById(R.id.button_change_location);
        new_location = (EditText) findViewById(R.id.id_location);

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                googlePlaces = new GooglePlaces();

                location = new_location.getText().toString();

                // Calling a Async Background thread
                new findLocation().execute(location);
            }
        });

    }

    /**
     * Background Async Task to Load Google places
     */
    class findLocation extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ChangeLocation.this);
            pDialog.setMessage(Html.fromHtml("<br/>Changing Location..."));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected String doInBackground(String... args) {
            // creating Places class object
            googlePlaces = new GooglePlaces();

            try {
                // get nearest places
                placeDetails = googlePlaces.getNewLocation(location);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * and show the data in UI
         * Always use runOnUiThread(new Runnable()) to update UI from background
         * thread, otherwise you will get error
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    if (placeDetails != null) {
                        String status = placeDetails.status;
                        Log.d("Status is ", status);

                        // Check for all possible status
                        if (status.equals("OK")) {

                            if (placeDetails.results == null) {
                                alert.showAlertDialog(ChangeLocation.this, "Near Places",
                                        "error inga",
                                        false);
                            }
                            // Successfully got places details
                            if (placeDetails.results != null) {

                                for (Place p : placeDetails.results) {

                                    double lt = p.geometry.location.lat;
                                    double ln = p.geometry.location.lng;

                                    Log.d("new latitude is ", Double.toString(lt));
                                    Log.d("new longtitude is ", Double.toString(ln));


                                    RetrieveNewLocation mApp = ((RetrieveNewLocation) getApplicationContext());
                                    mApp.setUserLatitude(lt);
                                    mApp.setUserLongtitude(ln);

                                    Toast.makeText(getApplicationContext(),
                                            "Location changed to " + location,
                                            Toast.LENGTH_LONG).show();

                                    Intent backtoIntroPage = new Intent(ChangeLocation.this, welcome.class);
                                    startActivity(backtoIntroPage);

                                }
                            }
                        } else if (status.equals("ZERO_RESULTS")) {
                            // Zero results found
                            alert.showAlertDialog(ChangeLocation.this, "Location Error",
                                    "Invalid location. Try to change the location",
                                    false);
                        } else if (status.equals("UNKNOWN_ERROR")) {
                            alert.showAlertDialog(ChangeLocation.this, "Location Error",
                                    "Sorry unknown error occured.",
                                    false);
                        } else if (status.equals("OVER_QUERY_LIMIT")) {
                            alert.showAlertDialog(ChangeLocation.this, "Location Error",
                                    "Sorry query limit to google places is reached",
                                    false);
                        } else if (status.equals("REQUEST_DENIED")) {
                            alert.showAlertDialog(ChangeLocation.this, "Location Error",
                                    "Sorry error occured. Request is denied",
                                    false);
                        } else if (status.equals("INVALID_REQUEST")) {
                            alert.showAlertDialog(ChangeLocation.this, "Location Error",
                                    "Sorry error occured. Invalid Request",
                                    false);
                        }
                    }
                    else {
                        alert.showAlertDialog(ChangeLocation.this, "Location Error",
                                "Sorry error occured.",
                                false);
                    }
                }
            });

        }
    }

    private static String getLocation(String code)
    {
        final String address = buildUrl(code);
        Log.d("address is ", address);

            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        String content=null;
                        URL url = new URL(address);
                        Log.d("URL is ", url.toString());
                        InputStream stream = url.openStream();
                        Log.d("stream is ", stream.toString());

                        try
                        {
                            int available = stream.available();
                            byte[] bytes = new byte[available];
                            stream.read(bytes);
                            content = new String(bytes);
                            Log.d("conetent is ", content);
                        }
                        finally
                        {
                            stream.close();
                        }
                        result = content.toString();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
            return result;
    }

    private static String buildUrl(String code)
    {
        StringBuilder builder = new StringBuilder();

        builder.append(GEO_CODE_SERVER);

        builder.append("address=");
        builder.append(code.replaceAll(" ", "+"));
        builder.append("&sensor=false");

        return builder.toString();
    }

    private static String[] parseLocation(String response)
    {
        // Look for location using brute force.
        // There are much nicer ways to do this, e.g. with Google's JSON library: Gson
        //     https://sites.google.com/site/gson/gson-user-guide

        String[] lines = response.split("\n");

        String lat = null;
        String lng = null;

        for (int i = 0; i < lines.length; i++)
        {
            if ("\"location\" : {".equals(lines[i].trim()))
            {
                lat = getOrdinate(lines[i+1]);
                lng = getOrdinate(lines[i+2]);
                break;
            }
        }

        return new String[] {lat, lng};
    }

    private static String getOrdinate(String s)
    {
        String[] split = s.trim().split(" ");

        if (split.length < 1)
        {
            return null;
        }

        String ord = split[split.length - 1];

        if (ord.endsWith(","))
        {
            ord = ord.substring(0, ord.length() - 1);
        }

        // Check that the result is a valid double
        Double.parseDouble(ord);

        return ord;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
