package com.example.sangeetha.hciapp;

import android.content.Context;
import android.content.Intent;

/**
 * Created by sangeetha on 4/9/16.
 */
public class MenuOptions{

    Context c;
    public MenuOptions(Context context)
    {
        c = context.getApplicationContext();
    }

    public void changeLocation(){

        Intent in1 = new Intent(c,
                ChangeLocation.class);
        in1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        c.startActivity(in1);
    }

    public void backToHome(){
        Intent in2 = new Intent(c,
                welcome.class);
        in2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(in2);
    }

    public void contactUs(){
        Intent in3 = new Intent(c,
                ContactUs.class);
        in3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(in3);
    }
    public void aboutApp(){
        Intent in3 = new Intent(c,
                AboutApp.class);
        in3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(in3);
    }


}
