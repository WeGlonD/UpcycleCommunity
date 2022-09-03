package com.example.upcyclecommunity.BrandList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upcyclecommunity.R;

public class FragmentBrand extends Fragment implements View.OnClickListener {
    View root;
    int[] filter_ids = new int[]{R.id.filter_clothes,R.id.filter_accessary,R.id.filter_interior,R.id.filter_fashionStuff,R.id.filter_etc};
    RecyclerView BrandListRecycler;
    LinearLayoutManager layoutManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_brandlist, container,false);

        BrandListRecycler = root.findViewById(R.id.BrandRecycler);
        BrandListRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        BrandListRecycler.setLayoutManager(layoutManager);

        for(int i = 0; i < filter_ids.length; i++){
            Button btn = root.findViewById(filter_ids[i]);
            btn.setOnClickListener(this);
        }

        Button btn = root.findViewById(R.id.search_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //db검색 후 리사이클러 notify
            }
        });

        return root;
    }

    @Override
    public void onClick(View view) {
        Button curButton = (Button)view;
        if(curButton.isSelected()){
            curButton.setSelected(false);
            curButton.setTextColor(getResources().getColor(R.color.black));
        }
        else{
            curButton.setSelected(true);
            curButton.setTextColor(getResources().getColor(R.color.white));
        }
    }
}
