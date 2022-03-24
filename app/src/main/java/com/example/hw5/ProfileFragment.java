package com.example.hw5;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.*;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private static final String LOG_TAG = ProfileFragment.class.getSimpleName();
    private TextView name, gender, age, desc;

    public ProfileFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        name = (TextView)v.findViewById(R.id.profileName2);
        gender = (TextView)v.findViewById(R.id.profileGender2);
        age = (TextView)v.findViewById(R.id.profileAge2);
        desc = (TextView)v.findViewById(R.id.profileDesc2);

        name.setText(getResources().getText(R.string.name));
        gender.setText(getResources().getText(R.string.gender));
        age.setText(getResources().getText(R.string.age));
        desc.setText(getResources().getText(R.string.desc));

        return v;
    }
}