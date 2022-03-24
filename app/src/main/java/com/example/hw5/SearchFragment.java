package com.example.hw5;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import androidx.fragment.app.Fragment;

public class SearchFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = SearchFragment.class.getSimpleName();
    private EditText searchTxt;
    private TextView searchOut;
    private ImageButton searchButton;

    public SearchFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        searchTxt = (EditText)v.findViewById(R.id.searchTxt);
        searchOut = (TextView)v.findViewById(R.id.searchOut);
        searchButton = (ImageButton)v.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);

        return v;
    }

    public void onClick(final View v) {
        switch(v.getId()) {
            case R.id.searchButton:
                Log.d(LOG_TAG, "Search");
                clickSearch();
                break;
            default:
                Log.d(LOG_TAG, "No match");
                break;
        }
    }

    private void clickSearch() {
        String search = searchTxt.getText().toString();
        if (search.isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "未输入搜索内容", Toast.LENGTH_LONG).show();
        }
        else {
            searchTxt.getText().clear();
            String txt = "\'" + search + "\'" + "搜索结果: ";
            Toast.makeText(getActivity().getApplicationContext(), "搜索成功", Toast.LENGTH_LONG).show();
            searchOut.setText(txt);
        }
    }
}