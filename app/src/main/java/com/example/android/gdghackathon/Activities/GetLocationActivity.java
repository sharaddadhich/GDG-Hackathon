package com.example.android.gdghackathon.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.android.gdghackathon.R;

public class GetLocationActivity extends AppCompatActivity {

    LocationManager locman;
    LocationListener loclis;
    ProgressDialog progressDialog;
    Double destLat = null, destLong = null, currLat = null, currLong = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Location...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        destLat = getIntent().getDoubleExtra("lat", 28.6184);
        destLong = getIntent().getDoubleExtra("long", 77.3726);

        locman = (LocationManager) getSystemService(LOCATION_SERVICE);
        loclis = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currLat = location.getLatitude();
                currLong = location.getLongitude();
                Log.d("123123", "onLocationChanged: " + location.toString());
                progressDialog.dismiss();
                locman.removeUpdates(loclis);
                Intent thisIntent = new Intent(GetLocationActivity.this,MapsActivity.class);
                thisIntent.putExtra("curlat",currLat);
                thisIntent.putExtra("curlong",currLong);
                thisIntent.putExtra("lat",destLat);
                thisIntent.putExtra("long",destLong);
                startActivity(thisIntent);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        getLocation();


    }

    @SuppressWarnings("MissingPermission")
    public void getLocation() {
        locman.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                10000,
                10,
                loclis
        );
    }

}

