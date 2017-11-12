package com.example.android.gdghackathon.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.android.gdghackathon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    LocationManager locman;
    LocationListener loclis;
    Double latitude = null,longitude = null;
    TextView tvTitle;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    }
                });

        tvTitle = (TextView) findViewById(R.id.title);

        tvTitle = (TextView) findViewById(R.id.title);
        Typeface typeFace = Typeface.createFromAsset(getAssets(),"fonts/VollkornSC-Regular.ttf");
        tvTitle.setTypeface(typeFace);

        getSupportActionBar().hide();



        locman = (LocationManager) getSystemService(LOCATION_SERVICE);
        loclis = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.d("123123", "onLocationChanged: " + latitude.toString() + "  " + longitude.toString() );
                Intent thisIntent = new Intent(SplashActivity.this,HomeScreenActivity.class);
                thisIntent.putExtra("lat",latitude);
                thisIntent.putExtra("long",longitude);
                locman.removeUpdates(loclis);
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

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) &&

                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, 234);
        } else {
            startLocationTracking();
        }

    }

    @SuppressWarnings("MissingPermission")
    void startLocationTracking () {
        locman.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                10000,
                10,
                loclis
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationTracking();
            }
        }
    }


}

