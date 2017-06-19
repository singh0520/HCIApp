package com.example.sangeetha.hciapp;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by sangeetha on 4/10/16.
 */
public class AboutApp extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_app);

        ActionBar actionBar;
        actionBar = getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#0044ff"));
        actionBar.setBackgroundDrawable(colorDrawable);
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
