package com.example.hw.Post;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hw.Profile.EditProfileActivity;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DraftActivity extends AppCompatActivity {
    private static final String TAG = DraftActivity.class.getSimpleName();;
    private String KEYS = "KEYS";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private Set draftset;
    private List<Draft> draftList;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.getApplicationContext();
        pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Log.d(TAG, "onCreate: ");
        if (pref == null){
            Toast.makeText(getApplicationContext(), "pref null", Toast.LENGTH_SHORT).show();
        }
        if (pref != null && pref.contains(KEYS)) {

            draftset = pref.getStringSet(KEYS, null);
            SharedPreferences.Editor editor = pref.edit();
//            editor.clear();
//            editor.apply();
            for(Object key:draftset)
            {
                Log.d(TAG, "onCreate: "+key.toString());
                String Jsonstr = pref.getString(key.toString(),"");
                Log.d(TAG, "onCreate: "+Jsonstr);

            }
        } else {
            Toast.makeText(this, "你没有草稿！", Toast.LENGTH_SHORT).show();
        }

    }
}
