package com.example.hw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private BottomBarAdapter pagerAdapter;
    private ViewPager viewPagerMain;
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    PostFragment postFragment = new PostFragment();
    SearchFragment searchFragment = new SearchFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPagerMain = findViewById(R.id.viewPagerMain);

        viewPagerMain.setOffscreenPageLimit(4);

        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());
        pagerAdapter.addFragments(homeFragment);
        pagerAdapter.addFragments(postFragment);
        pagerAdapter.addFragments(searchFragment);
        pagerAdapter.addFragments(profileFragment);

        viewPagerMain.setAdapter(pagerAdapter);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.home:
                        viewPagerMain.setCurrentItem(0, false);
                        return true;
                    case R.id.post:
                        viewPagerMain.setCurrentItem(1, false);
                        return true;
                    case R.id.search:
                        viewPagerMain.setCurrentItem(2, false);
                        return true;
                    case R.id.profile:
                        viewPagerMain.setCurrentItem(3, false);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        int currentItem = viewPagerMain.getCurrentItem();
        if (currentItem != 0){
            viewPagerMain.setCurrentItem(currentItem-1, true);
        }
        else {
            super.onBackPressed();
        }
    }

    public void switchHome(String title, String msg) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
        homeFragment.addStatus(title, msg);
        viewPagerMain.setCurrentItem(0, false);
    }

    public static void verifyStoragePermissions(Activity activity) {
        Log.d(LOG_TAG, "permission");
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}