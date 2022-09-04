package com.example.upcyclecommunity.community1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.upcyclecommunity.R;


public class Fragment_CM1 extends Fragment {
    View root;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_cm1, container, false);
        Button btn_upload = (Button) root.findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),WritePostActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}
