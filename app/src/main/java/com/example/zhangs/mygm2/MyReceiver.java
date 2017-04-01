package com.example.zhangs.mygm2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by ZHANGS on 4/1/2017.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();
        trackyou(context);
    }


    double latitude =21;
    double longitude=-82;

    private void trackyou(Context context) {

        String filename = "history.json";   // not a real json file just a text for now.
        String string = "Atlantic City, NJ";
        //String string = "Salt Lake City, UT";
        FileOutputStream outputStream;

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
/*
            List<Address> addresses = geocoder.getFromLocation(longitude, latitude, 1);
            String cityName = addresses.get(0).getAddressLine(0);
            String stateName = addresses.get(0).getAddressLine(1);
            String countryName = addresses.get(0).getAddressLine(2);
            string=cityName+", "+stateName;
*/
            //outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);  // other modes
            outputStream = context.openFileOutput(filename, Context.MODE_APPEND);  // other modes

            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
