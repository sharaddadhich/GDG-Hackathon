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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

/**
 * Created by HP on 12-Nov-17.
 */

public class LostFragment extends Fragment {
    Context ctx;

    String app_id = "da94c708";
    String api_key = "c3b72e509462f155a8e5d2381222bb92";
    Kairos myKairos;
    KairosListener listener;
    Bitmap photo =null;
    Uri imageUri;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public static final Integer REQUEST_CAMERA = 101;
    public static final Integer REQUEST_GET_IMAGES = 102;
    public static final Integer REQ_CODE = 103;
    CharSequence cameraOptions[] = new CharSequence[]{"Camera", "Gallery"};

    EditText lostName, lostClothes, lostMob;
    ImageView lostImage;
    Button btnSubmit;
    ProgressDialog progressDialog;
    ScrollView scrollView;

    public LostFragment(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myKairos = new Kairos();
        progressDialog = new ProgressDialog(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Lost");

        myKairos.setAuthentication(getContext(), app_id, api_key);

        // Create an instance of the KairosListener
        listener = new KairosListener() {

            @Override
            public void onSuccess(String response) {
                // your code here!
                Log.d("KAIROS DEMO", response);
                progressDialog.dismiss();

                Lost thisLost = new Lost(lostName.getText().toString(),lostMob.getText().toString(),lostClothes.getText().toString());
                databaseReference.push().setValue(thisLost)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(ctx, "Uploaded Successfully!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                lostName.setText("");
                lostClothes.setText("");
                lostMob.setText("");
                lostImage.setImageResource(R.drawable.nopicc);
                Log.d(TAG, "onSuccess: "+response);
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
        View rootView = inflater.inflate(R.layout.fragment_lost, container, false);
        lostName = (EditText) rootView.findViewById(R.id.lostName);
        lostClothes = (EditText) rootView.findViewById(R.id.lostClothes);
        lostMob = (EditText) rootView.findViewById(R.id.lostMob);
        lostImage = (ImageView) rootView.findViewById(R.id.lostImage);
        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView) ;
        lostImage.setOnClickListener(new View.OnClickListener() {
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
                    progressDialog.setMessage("Uploading");
                    progressDialog.show();
                    enrollPeople();
                }
            }
        });
        return rootView;
    }

    private Boolean isValid() {
        if (TextUtils.isEmpty(lostName.getText().toString())) {
            Toast.makeText(getContext(), "Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(lostClothes.getText().toString())) {
            Toast.makeText(getContext(), "Enter Clothes Description", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(lostMob.getText().toString())) {
            Toast.makeText(getContext(), "Enter Mobile Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (photo == null){
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

    void enrollPeople() {
        String subjectId = lostName.getText().toString();
        String galleryId = "Population";
        try {
            myKairos.enroll(photo, subjectId, galleryId, null, null, null, listener);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            lostImage.setImageBitmap(photo);
            lostImage.setVisibility(View.VISIBLE);
        }

        if (requestCode == REQUEST_GET_IMAGES && resultCode == RESULT_OK) {
            imageUri = data.getData();
            try {
                photo = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                lostImage.setImageBitmap(photo);
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
}
