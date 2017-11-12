package com.example.android.gdghackathon.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.gdghackathon.R;

/**
 * Created by HP on 12-Nov-17.
 */

public class HelpFragment extends Fragment {
    Context ctx;

    public HelpFragment(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_help,container,false);
        return  rootView;
    }
}
