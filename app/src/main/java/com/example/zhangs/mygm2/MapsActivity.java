package com.example.zhangs.mygm2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        trackyou();



    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

       // LatLng oneonta = new LatLng(42.4529, -75.0638);
       // mMap.addMarker(new MarkerOptions().position(oneonta).title("Marker in Oneonta, NY"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(oneonta));

        LatLng cll=getgeoinfo("NYC, NY");
        mMap.addMarker(new MarkerOptions().position(cll).title("NYC, Captial of the World!!!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cll));

        showcapitals(mMap);

        finddisney();

        showdisney(mMap);

        readhistory();

    }

    private void finddisney()
    {
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            InputStream in_s = getApplicationContext().getAssets().open("disney.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);

            parseXML(parser);

        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    ArrayList<disney> disneylocations = null;

    private void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        int eventType = parser.getEventType();
        disney currentlocation = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    disneylocations = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("location")){
                        currentlocation = new disney();
                    } else if (currentlocation != null){
                        if (name.equals("state")){
                            currentlocation.state = parser.nextText();
                        } else if (name.equals("city")){
                            currentlocation.city = parser.nextText();
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("location") && currentlocation != null){
                        disneylocations.add(currentlocation);
                    }
            }
            eventType = parser.next();
        }

            }

    private void showdisney(GoogleMap googleMap)
    {


        Iterator<disney> it = disneylocations.iterator();

        while(it.hasNext())
        {
            disney currlocation  = it.next();
            String content = "";
            content = content + "city :" +  currlocation.city + "n";
            content = content + "state :" +  currlocation.state + "n";

            LatLng cll=getgeoinfo(currlocation.city+", "+currlocation.state);
            mMap.addMarker(new MarkerOptions().position(cll).title("Disney at"+ currlocation.state+ currlocation.city+"!"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(cll));

        }

        //TextView display = (TextView)findViewById(R.id.info);
        //display.setText(content);
    }


    private boolean showcapitals(GoogleMap googleMap)
    {
        String jcc = null;
        try {
            InputStream is = getAssets().open("capitals.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jcc = new String(buffer, "UTF-8");

            JSONArray jsonarray = new JSONArray(jcc);

            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String city = jsonobject.getString("city");
                String state = jsonobject.getString("state");
                LatLng cll=getgeoinfo(city+", "+state);
                mMap.addMarker(new MarkerOptions().position(cll).title("Captical of "+ state+ " is: " + city+"!"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(cll));
            }
        } catch (IOException ex) {
            //ex.printStackTrace();
            return false;
        } catch (JSONException ex)
        {
            //ex.printStackTrace();
            return false;
        }

        return true;
    }

    private LatLng getgeoinfo(String cityname) {
        Address aa = null;
        LatLng all = null;
        if (Geocoder.isPresent()) {
            try {
                String location = cityname;
                Geocoder gc = new Geocoder(this);
                List<Address> addresses = gc.getFromLocationName(location, 5); // get the found Address Objects


                aa = (addresses.get(0));
                all = new LatLng(aa.getLatitude(), aa.getLongitude());
                return all;

                /*
                List<LatLng> lll = new ArrayList<LatLng>(addresses.size());

                A list to save the coordinates if they are available
                for(Address a : addresses){
                    if(a.hasLatitude() && a.hasLongitude()){
                        lll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                    }
                }
                */
            } catch (IOException e) {
                // handle the exception
                Toast.makeText(this, "Geo Code failed!", Toast.LENGTH_LONG);


            }
        }
        return null;
    }


    private void trackyou() {

        String filename = "history.json";
        String string = "Newark, NJ";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);  // other modes
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readhistory()
    {
        String FILENAME = "history.json";
        StringBuilder sb=null;

        try{
        FileInputStream fis = openFileInput(FILENAME);
            InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        sb= new StringBuilder();
        String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        }
        catch(Exception e)
        {

        }

        LatLng cll=getgeoinfo(sb.toString());
        mMap.addMarker(new MarkerOptions().position(cll).title("03/28/2017"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cll));

    }
}
