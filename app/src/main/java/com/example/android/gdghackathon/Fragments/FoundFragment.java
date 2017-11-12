package com.example.android.gdghackathon.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.android.gdghackathon.Models.Lost;
import com.example.android.gdghackathon.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

/**
 * Created by HP on 12-Nov-17.
 */

public class FoundFragment extends Fragment {
    Context ctx;
    String app_id = "da94c708";
    String api_key = "c3b72e509462f155a8e5d2381222bb92";
    Kairos myKairos;
    KairosListener listener;
    Bitmap bitmap =null;

    public static final Integer REQUEST_CAMERA = 101;
    public static final Integer REQUEST_GET_IMAGES = 102;
    public static final Integer REQ_CODE = 103;
    CharSequence cameraOptions[] = new CharSequence[]{"Camera", "Gallery"};

    EditText foundClothes, foundDescription;
    ImageView foundImage;
    Button btnSubmit;
    ProgressDialog progressDialog;
    ScrollView scrollView;
    Uri imageUri;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public FoundFragment(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Lost");

        myKairos = new Kairos();
        progressDialog = new ProgressDialog(getContext());

        myKairos.setAuthentication(getContext(), app_id, api_key);

        // Create an instance of the KairosListener
        listener = new KairosListener() {

            @Override
            public void onSuccess(String response) {
                // your code here!
                Log.d("KAIROS DEMO", response);
                progressDialog.dismiss();
                foundClothes.setText("");
                foundDescription.setText("");
                foundImage.setImageResource(R.drawable.nopicc);
                Log.d(TAG, "onSuccess: "+response);
                try {
                    JSONObject json = new JSONObject(response);
                    Log.d(TAG, "onSuccess: "+json.getJSONArray("images").getJSONObject(0).getJSONObject("transaction").getString("subject_id"));
                    final String recognisedName = json.getJSONArray("images").getJSONObject(0).getJSONObject("transaction").getString("subject_id");
                    Toast.makeText(getContext(),"Recognised As : "+recognisedName, Toast.LENGTH_SHORT).show();
                    databaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Lost thisLost = dataSnapshot.getValue(Lost.class);
                            if(thisLost.getName().equals(recognisedName))
                            {
                                Log.d(TAG, "onChildAdded: " + thisLost.getMobileNo());
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(thisLost.getMobileNo(),null,"Found " +thisLost.getName()+ " at  28.6184°, 77.3726° \n Team Khoj",null,null);
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(String response) {
                // your code here!
                Log.d("KAIROS DEMO", response);
                progressDialog.dismiss();
                Toast.makeText(ctx, "Failed to Upload!!", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_found,container,false);

        foundClothes = (EditText) rootView.findViewById(R.id.foundClothes);
        foundDescription = (EditText) rootView.findViewById(R.id.foundDescription);
        foundImage = (ImageView) rootView.findViewById(R.id.foundImage);
        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView) ;
        foundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose an Option");
                builder.setItems(cameraOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            int cameraPerm = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
                            if (cameraPerm != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{
                                        Manifest.permission.CAMERA
                                }, REQUEST_CAMERA);
                            } else {
                                takeFromCamera();
                            }
                        } else if (which == 1) {
                            Intent i = new Intent(Intent.ACTION_PICK);
                            i.setType("image/*");
                            startActivityForResult(Intent.createChooser(i, "Choose Picture"),REQUEST_GET_IMAGES);
                        }
                    }
                });
                builder.show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()){
                    progressDialog.setMessage("Searching..");
                    progressDialog.show();
                    recognise();
                }
            }
        });
        return  rootView;
    }


    private Boolean isValid() {

        if (TextUtils.isEmpty(foundClothes.getText().toString())) {
            Toast.makeText(getContext(), "Enter Clothes Description", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(foundDescription.getText().toString())) {
            Toast.makeText(getContext(), "Enter Person Description", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (bitmap == null){
            Toast.makeText(getContext(), "Enter the photo", Toast.LENGTH_SHORT).show();
            scrollView.fullScroll(View.FOCUS_UP);
            return false;
        }
        return true;
    }

    private void takeFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    private void recognise() {

        String galleryId = "Population";
        String selector = "FULL";
        String threshold = "0.75";
        String minHeadScale = "0.25";
        String maxNumResults = "25";
        try {
            myKairos.recognize(bitmap,
                    galleryId,
                    selector,
                    threshold,
                    minHeadScale,
                    maxNumResults,
                    listener);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            foundImage.setImageBitmap(bitmap);
            foundImage.setVisibility(View.VISIBLE);
        }

        if (requestCode == REQUEST_GET_IMAGES && resultCode == RESULT_OK) {

//            bitmap = (Bitmap) data.getExtras().get("data");
//            foundImage.setImageBitmap(bitmap);
//            foundImage.setVisibility(View.VISIBLE);
            imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                foundImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getContext(), "Permission Not Given", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            takeFromCamera();

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void BuildSms(String phoneno)
    {

    }

}
