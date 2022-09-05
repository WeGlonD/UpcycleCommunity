package com.example.upcyclecommunity.community1;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.upcyclecommunity.R;

public class Personal_Post extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_post);
        Long postn = Long.parseLong(getIntent().getStringExtra("postn"));


    }
}
