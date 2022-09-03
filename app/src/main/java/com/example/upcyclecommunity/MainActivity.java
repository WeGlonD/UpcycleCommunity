package com.example.upcyclecommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        BottomNavigationView main_bottom = findViewById(R.id.main_bottom);
        main_bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setFragment(item.getItemId());
                return true;
            }
        });

        //첫번째 프래그먼트 띄우도록 할 것.

    }

    public void setFragment(int n) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (n) {
            case R.id.bottom_community1:
                //fragmentTransaction.replace(R.id.main_frame,Fragment1).commit();
                Toast.makeText(this, "커뮤1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_community2:
                //fragmentTransaction.replace(R.id.main_frame, Fragment2).commit();
                Toast.makeText(this, "커뮤2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_brandlist:
                //fragmentTransaction.replace(R.id.main_frame,Fragment3).commit();
                Toast.makeText(this, "브랜드", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_mypage:
                //fragmentTransaction.replace(R.id.main_frame,Fragment4).commit();
                Toast.makeText(this, "마이페이지", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}