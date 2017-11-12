package com.example.android.gdghackathon.Activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.gdghackathon.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    Double destLat = null, destLong = null,currLat = null, currLong = null;

    GoogleMap gmap;

    SupportMapFragment mapFragment;
    ArrayList<LatLng> linePlaces = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        currLat = getIntent().getDoubleExtra("curlat",0.0);
        currLong = getIntent().getDoubleExtra("curlong",0.0);
        destLat = getIntent().getDoubleExtra("lat", 28.6184);
        destLong = getIntent().getDoubleExtra("long", 77.3726);
        Log.d("123123", "onCreate: " + currLat.toString()+" " +destLong );
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapsforloc);
        mapFragment.getMapAsync(this);


    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("123123", "onMapReady:  Map is ready ");
        gmap = googleMap;
        LatLng curlatlang =new LatLng(currLat,currLong);
        LatLng destlatlang = new LatLng(destLat,destLong);
        gmap.addMarker(new MarkerOptions().position(curlatlang).title("My Location"));
        gmap.addMarker(new MarkerOptions().position(destlatlang).title("Destination"));
        getRoutes(curlatlang,destlatlang);
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(curlatlang,14.0f));
    }


    public void getRoutes(LatLng origin,LatLng destination){
        String source = String.valueOf(origin.latitude)+","+String.valueOf(origin.longitude);
        String dest = String.valueOf(destination.latitude)+","+String.valueOf(destination.longitude);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(

                "https://maps.googleapis.com/maps/api/directions/json?origin="+source+"&destination="+dest+"&key=AIzaSyBn1IirDrcCv28ucbr57ZJ4lJUs1dbJ3XI",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
                            for (int i=0;i<jsonArray.length();++i){
                                Double lat = jsonArray.getJSONObject(i).getJSONObject("start_location").getDouble("lat");
                                Double lng = jsonArray.getJSONObject(i).getJSONObject("start_location").getDouble("lng");
                                LatLng point = new LatLng(lat,lng);
                                Log.d("12341234", "onResponse: "+point);
                                linePlaces.add(point);
//                                Log.d(TAG, "onResponse: End :"+jsonArray.getJSONObject(i).getString("end_location"));
//                                Log.d(TAG, "onResponse: Start :"+jsonArray.getJSONObject(i).getString("start_location"));
                            }
                            addpolyLine();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MapsActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();

                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonRequest);

    }

    private void addpolyLine() {
        PolylineOptions options = new PolylineOptions()
                .add()
                .width(10)
                .color(Color.BLUE)
                .geodesic(true)
                .jointType(JointType.ROUND);
        options.addAll(linePlaces);
        Polyline polyLine = gmap.addPolyline(options);
        polyLine.setTag("A");
        polyLine.setJointType(JointType.ROUND);
    }
}
