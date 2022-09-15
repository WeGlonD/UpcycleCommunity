package com.example.upcyclecommunity.BrandList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Brand;
import com.example.upcyclecommunity.database.BrandQuery;
import com.example.upcyclecommunity.database.Database;

import java.util.ArrayList;

public class FragmentBrand extends Fragment implements View.OnClickListener {
    View root;
    int[] filter_ids = new int[]{R.id.filter_clothes,R.id.filter_accessary,R.id.filter_interior,R.id.filter_fashionStuff,R.id.filter_etc};
    RecyclerView BrandListRecycler;
    BrandRecyclerAdapter adapter;
    LinearLayoutManager layoutManager;
    LinearLayout Filter;
    ArrayList<Brand> BrandArrayList;
    ArrayList<CheckBox> filterOptions;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_brandlist, container,false);

        BrandListRecycler = root.findViewById(R.id.BrandRecycler);
        BrandListRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        BrandListRecycler.setLayoutManager(layoutManager);
        Filter = root.findViewById(R.id.Filter);
        filterOptions = new ArrayList<>();

        root.findViewById(R.id.FilterTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Filter.getVisibility()==View.VISIBLE){
//                    (root.findViewById(R.id.FilterTitleText)).setVisibility(View.VISIBLE);
                    Filter.setVisibility(View.GONE);
                }
                else{
//                    (root.findViewById(R.id.FilterTitleText)).setVisibility(View.INVISIBLE);
                    Filter.setVisibility(View.VISIBLE);
                }
            }
        });

        for(int i = 0; i < filter_ids.length; i++){
            CheckBox chkbox = root.findViewById(filter_ids[i]);
            chkbox.setOnClickListener(this);
            filterOptions.add(chkbox);
        }

        BrandArrayList = new ArrayList<>();
        Database db = new Database();
        db.readAllBrand(BrandArrayList, new BrandQuery() {
            @Override
            public boolean Q(Brand brand) {
                return true;
            }
        }, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                //Toast.makeText(getContext(),""+BrandArrayList.size(), Toast.LENGTH_SHORT).show();
                adapter = new BrandRecyclerAdapter(BrandArrayList, getContext());
                BrandListRecycler.setAdapter(adapter);
            }

            @Override
            public void ifFail(Object task) {
                //Toast.makeText(getContext(),"FragmentBrand - readAllBrand 실패", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        Button btn = root.findViewById(R.id.search_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searching();
            }
        });

        return root;
    }

    @Override
    public void onClick(View view) {
        Log.d("WeGlonD", "checkbox clicked");
        searching();
    }

    public void searching(){
        ArrayList<String> filterCondition = new ArrayList<>();
        String searchName = ((EditText)root.findViewById(R.id.search_editText)).getText().toString().toUpperCase();
        for(CheckBox btn : filterOptions){
            if(btn.isChecked()){
                filterCondition.add(btn.getText().toString());
            }
        }
        ArrayList<Brand> filtered = new ArrayList<>();
        for(Brand tmp : BrandArrayList){
            boolean flag = true;
            if(!tmp.getName().contains(searchName))
                continue;
            for(String cond : filterCondition){
                if(!tmp.getTags().contains(cond)){
                    flag = false;
                    break;
                }
            }
            if(flag)
                filtered.add(tmp);
        }
        BrandRecyclerAdapter adt = new BrandRecyclerAdapter(filtered,getContext());
        BrandListRecycler.setAdapter(adt);
        adt.notifyDataSetChanged();
    }
}
