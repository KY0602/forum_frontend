package com.example.hw.Profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.hw.R;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = ProfileFragment.class.getSimpleName();
    private EditText name, age, desc;
    private RadioGroup genderGroup;
    private Button saveButton;
    private String gender = "男";

    public ProfileFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        name = (EditText)v.findViewById(R.id.profileName2);
        age = (EditText)v.findViewById(R.id.profileAge2);
        desc = (EditText)v.findViewById(R.id.profileDesc2);
        genderGroup = (RadioGroup)v.findViewById(R.id.radioGroupGender);
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int childCount = radioGroup.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton)radioGroup.getChildAt(x);
                    if (btn.getId() == i) {
                        gender = btn.getText().toString();
                    }
                }
            }
        });
        saveButton = (Button)v.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        return v;
    }

    public void onClick(final View v) {
        switch(v.getId()) {
            case R.id.saveButton:
                Log.d(LOG_TAG, "Save");
                clickSave();
                break;
            default:
                Log.d(LOG_TAG, "No match");
                break;
        }
    }

    private void clickSave() {
        String inName = name.getText().toString();
        String inAge = age.getText().toString();
        String inDesc = desc.getText().toString();
        if (inName.isEmpty() || inAge.isEmpty()) {
            Log.d(LOG_TAG, "Error");
            Toast.makeText(getActivity().getApplicationContext(), "用户名和年龄不能为空", Toast.LENGTH_LONG).show();
        }
        else {
            Log.d(LOG_TAG, inName + " " + gender + " " + inAge + " " + inDesc);
            Toast.makeText(getActivity().getApplicationContext(), "保存成功", Toast.LENGTH_LONG).show();
        }
    }
}