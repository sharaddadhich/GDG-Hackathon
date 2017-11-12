package com.example.android.gdghackathon.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.gdghackathon.Models.Police;
import com.example.android.gdghackathon.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.example.android.gdghackathon.Activities.HomeScreenActivity.latitude;
import static com.example.android.gdghackathon.Activities.HomeScreenActivity.longitude;

/**
 * Created by HP on 12-Nov-17.
 */

public class HelpFragment extends Fragment {
    Context ctx;
    ArrayList<Police> police = new ArrayList<>();
    ArrayList<Marker> myMarkers;
    ProgressDialog progressDailog;
    String policeNumber="100";
    CardView policeInfo;
    LinearLayout callLayout;
    TextView policeName, policeAddress, policeDistance;

    public HelpFragment(Context ctx) {
        this.ctx = ctx;
    }


    GoogleMap gmap;
    MapView mMapView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDailog = new ProgressDialog(ctx);
        progressDailog.setMessage("Fetching Nearby Stations...");
        progressDailog.setCanceledOnTouchOutside(false);
        myMarkers = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        policeName = (TextView) rootView.findViewById(R.id.stationName);
        policeAddress = (TextView) rootView.findViewById(R.id.policeAddress);
        policeDistance = (TextView) rootView.findViewById(R.id.policeDistance);
        policeInfo = (CardView) rootView.findViewById(R.id.policeInfo);
        callLayout = (LinearLayout) rootView.findViewById(R.id.callLayout);
        progressDailog.show();

        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                gmap = mMap;

                LatLng location = new LatLng(latitude, longitude);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "&radius=5000&type=police&key=AIzaSyBgEqbEuZ8LJdG7BmDXn3frx89AK1IVd0c",
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    JSONArray jsonArray = response.getJSONArray("results");
                                    for (int i = 0; i < jsonArray.length(); ++i) {
                                        JSONObject loc = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location");
                                        String name = jsonArray.getJSONObject(i).getString("name");
                                        Log.d("123123", "onResponse: " + name);
                                        String address = jsonArray.getJSONObject(i).getString("vicinity");
                                        String placeId = jsonArray.getJSONObject(i).getString("place_id");
                                        LatLng latlng = new LatLng(loc.getDouble("lat"), loc.getDouble("lng"));
                                        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.police1);
                                        Bitmap resized = Bitmap.createScaledBitmap(icon, 80, 80, true);
                                        String dist = String.valueOf(round(distance(latitude, longitude, loc.getDouble("lat"), loc.getDouble("lng"), 'K'), 2)) + " km away";
                                        Marker marker = gmap.addMarker(new MarkerOptions().position(latlng).title(name)
                                                .icon(BitmapDescriptorFactory.fromBitmap(resized)));
                                        myMarkers.add(marker);
                                        Police item = new Police(name, address, placeId, latlng, dist);
                                        police.add(item);
                                    }
                                    progressDailog.dismiss();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ctx, "Volley Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                RequestQueue requestQueue = Volley.newRequestQueue(ctx);
                requestQueue.add(jsonObjectRequest);
                gmap.addCircle(new CircleOptions()
                        .center(location)
                        .radius(5000)
                        .strokeWidth(0f)
                        .fillColor(getTransparentColor(Color.parseColor("#4682b4"))));
                gmap.addMarker(new MarkerOptions().position(location).title("My Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)));
                gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f));
                gmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        for (int i = 0; i < myMarkers.size(); i++) {
                            if (marker.equals(myMarkers.get(i))) {
                                policeName.setText(police.get(i).getName());
                                policeAddress.setText(police.get(i).getAddress());
                                policeDistance.setText(police.get(i).getDistance());
                                policeInfo.setVisibility(View.VISIBLE);
                                getContact(police.get(i).getPlaceId());
                                callLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(getContext(), "Call Police " +policeNumber, Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(Intent.ACTION_DIAL);
                                        i.setData(Uri.parse("tel:" + policeNumber));
                                        startActivity(i);
                                    }
                                });
                            }

                        }
                        return true;
                    }

                    ;
                });
            }
        });

        return rootView;
    }

    private int getTransparentColor(int color) {
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        alpha *= 0.25;
        return Color.argb(alpha, red, green, blue);
    }

    void fetchStations() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "&radius=5000&type=police&key=AIzaSyBgEqbEuZ8LJdG7BmDXn3frx89AK1IVd0c",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); ++i) {
                                JSONObject loc = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location");
                                String name = jsonArray.getJSONObject(i).getString("name");
                                Log.d("123123", "onResponse: " + name);
                                String address = jsonArray.getJSONObject(i).getString("vicinity");
                                String placeId = jsonArray.getJSONObject(i).getString("place_id");
                                LatLng latlng = new LatLng(loc.getDouble("lat"), loc.getDouble("lng"));
                                String dist = String.valueOf(round(distance(latitude, longitude, loc.getDouble("lat"), loc.getDouble("lng"), 'K'), 2)) + " km away";
                                Police item = new Police(name, address, placeId, latlng, dist);
                                police.add(item);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, "Volley Error", Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(jsonObjectRequest);
    }

    public static final double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    private static final double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static final double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void getContact(String placeId) {



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=AIzaSyBgEqbEuZ8LJdG7BmDXn3frx89AK1IVd0c",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("result");
                            String no;
                            no = response.getJSONObject("result").getString("formatted_phone_number");
                            Log.d(TAG, "onResponse: Number" + no);
                            policeNumber = no;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, "Volley Error", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onErrorResponse: " + error);
                    }
                });
        RequestQueue rQueue = Volley.newRequestQueue(ctx);
        rQueue.add(jsonObjectRequest);
       return;

    }
}
