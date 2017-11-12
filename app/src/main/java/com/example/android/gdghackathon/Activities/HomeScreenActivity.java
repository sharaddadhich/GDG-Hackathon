package com.example.android.gdghackathon.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.gdghackathon.Fragments.FoundFragment;
import com.example.android.gdghackathon.Fragments.HelpFragment;
import com.example.android.gdghackathon.Fragments.LostFragment;
import com.example.android.gdghackathon.Fragments.TipsFragment;
import com.example.android.gdghackathon.R;

public class HomeScreenActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FragmentManager fragManager;
    FragmentTransaction fragTxn;
    public static Double latitude=null,longitude=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        latitude = getIntent().getDoubleExtra("lat",0.0);
        longitude = getIntent().getDoubleExtra("long",0.0);
        fragManager = getSupportFragmentManager();
        final HelpFragment helpFragment = new HelpFragment(this);
        fragTxn = fragManager.beginTransaction();
        fragTxn.replace(R.id.fragFrame, helpFragment);
        fragTxn.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragTxn.commit();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.help){
                    fragManager = getSupportFragmentManager();
                    fragTxn = fragManager.beginTransaction();
                    fragTxn.replace(R.id.fragFrame, helpFragment);
                    fragTxn.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragTxn.commit();
                }

                if (item.getItemId() == R.id.lost){
                    fragManager = getSupportFragmentManager();
                    LostFragment lostFragment = new LostFragment(HomeScreenActivity.this);
                    fragTxn = fragManager.beginTransaction();
                    fragTxn.replace(R.id.fragFrame,lostFragment);
                    fragTxn.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragTxn.commit();
                }

                if (item.getItemId() == R.id.found){
                    fragManager = getSupportFragmentManager();
                    FoundFragment foundFragment = new FoundFragment(HomeScreenActivity.this);
                    fragTxn = fragManager.beginTransaction();
                    fragTxn.replace(R.id.fragFrame,foundFragment);
                    fragTxn.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragTxn.commit();
                }
                if (item.getItemId() == R.id.tips){
                    fragManager = getSupportFragmentManager();
                    TipsFragment tipsFragment = new TipsFragment(HomeScreenActivity.this);
                    fragTxn = fragManager.beginTransaction();
                    fragTxn.replace(R.id.fragFrame,tipsFragment);
                    fragTxn.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragTxn.commit();
                }
                return true;
            }
        });
    }
}
