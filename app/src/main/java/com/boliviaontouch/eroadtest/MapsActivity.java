package com.boliviaontouch.eroadtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boliviaontouch.eroadtest.Clases.Bubble;
import com.boliviaontouch.eroadtest.Clases.TimezoneResponse;
import com.boliviaontouch.eroadtest.WsUtils.AsyncGetTimezone;
import com.boliviaontouch.eroadtest.WsUtils.GPSUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MapsActivity extends FragmentActivity {

    public static String LOG="EROAD test";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double lat=0;
    private double lon=0;
    public LocationManager locationManager;
    public LocationListener locationListener;
    public Location location;
    private ProgressDialog barProgressDialog;

    private Context context;
    private boolean isLocationObtained=false;

    //Parameter to setup GPS updates
    public static int MIN_TIME=5000; //Every 5 seconds
    public static float MIN_DISTANCE=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context=this;

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isLocationObtained=false;
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try
        {
            locationManager.removeUpdates(locationListener);
        }
        catch (Exception e)
        {
            Log.e(LOG, "Error removing updates" + e.toString());
        }

        if(barProgressDialog.isShowing())
            barProgressDialog.dismiss();

    }

    private void setUpMapIfNeeded() {
        if (mMap == null)
        {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null)
            {
                setUpMap();
            }
        }
        else
            setUpMap();
    }

    private void setUpMap() {
        barProgressDialog = new ProgressDialog(context);
        barProgressDialog.setTitle("Getting GPS Location");
        barProgressDialog.setMessage("Please wait");
        barProgressDialog.setIndeterminate(true);
        if(!isLocationObtained) {
            if (!barProgressDialog.isShowing())
                barProgressDialog.show();
        }

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        if(location!=null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            MoveCamera(new LatLng(lat,lon));
        }

        locationListener = new LocationListener()
        {
            public void onLocationChanged(Location location) {
                if(!isLocationObtained)
                {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    MoveCamera(new LatLng(lat, lon));
                    Log.i(LOG, lat + "," + lon);

                    MapsActivity.this.locationManager.removeUpdates(this);
                    isLocationObtained=true;

                    if (barProgressDialog.isShowing())
                        barProgressDialog.dismiss();

                    AsyncGetTimezone asyncGetTimezone = new AsyncGetTimezone(context, new AsyncGetTimezone.Receiver() {
                        @Override
                        public void onLoad(TimezoneResponse timezoneResponse) {
                            if (timezoneResponse != null) {
                                Bubble bubble = new Bubble();
                                bubble.setLatitude(lat);
                                bubble.setLongitude(lon);
                                bubble.setTimezone(timezoneResponse.getTimeZoneId());
                                bubble.setUTC_time(getUTCtime());
                                bubble.setLocal_time(getLocalTime());

                                mMap.addMarker(
                                        new MarkerOptions().
                                                position(new LatLng(lat, lon)).
                                                title("Your position").
                                                snippet(strBubble(bubble)));

                                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                    @Override
                                    public View getInfoWindow(Marker marker) {
                                        View v = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
                                        TextView info = (TextView) v.findViewById(android.R.id.text1);
                                        info.setBackgroundColor(getResources().getColor(R.color.background_material_light));
                                        info.setText(marker.getSnippet().toString());
                                        return v;
                                    }

                                    @Override
                                    public View getInfoContents(Marker marker) {
                                        return null;
                                    }

                                });
                            } else
                                Toast.makeText(context, "Error obtaining Timezone from Google API", Toast.LENGTH_LONG).show();
                        }
                    });

                    Long timestamp = System.currentTimeMillis() / 1000;
                    String strLocation = lat + "," + lon;
                    String strTimestamp = String.valueOf(timestamp);
                    //0: Location, 1: timestamp
                    asyncGetTimezone.execute(strLocation, strTimestamp);
                }
            }

            public void onProviderDisabled(String provider) {
                if(barProgressDialog.isShowing())
                    barProgressDialog.dismiss();
                ShowMessageGPSDisabled();
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }
        };
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);

    }

    private void MoveCamera(LatLng newLocation)
    {
        CameraPosition camPos = new CameraPosition.Builder()
                .target(newLocation)
                .zoom(15)
                .build();
        CameraUpdate camUpd1 = CameraUpdateFactory.newCameraPosition(camPos);
        mMap.moveCamera(camUpd1);
    }

    private void ShowMessageGPSDisabled()
    {
        Toast.makeText(context,"GPS is disabled, please Turn ON defore continue",Toast.LENGTH_LONG).show();
        Log.e(LOG,"GPS is disabled, please Turn ON defore continue");
        Intent in = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(in);
    }

    public static String getUTCtime()
    {
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());
        return utcTime;
    }


    public static String getLocalTime(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            String localTime = dateFormat.format(new Date());
            return localTime;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String strBubble(Bubble bubble)
    {
        StringBuilder tmp = new StringBuilder();
        tmp.append("-Location: ");
        tmp.append(bubble.getLatitude()+"/"+bubble.getLongitude());
        tmp.append("\n");
        tmp.append("-Timezone: ");
        tmp.append(bubble.getTimezone());
        tmp.append("\n");
        tmp.append("-Current UTC time: ");
        tmp.append(bubble.getUTC_time());
        tmp.append("\n");
        tmp.append("-Current Local time: ");
        tmp.append(bubble.getLocal_time());
        tmp.append("\n");
        tmp.append("-Distance to EROAD Office: ");
        tmp.append(GPSUtils.DistanciaDosPuntos(-36.722215,174.706298,bubble.getLatitude(),bubble.getLongitude())+"Km");
        tmp.append("\n");
        return tmp.toString();
    }
}
