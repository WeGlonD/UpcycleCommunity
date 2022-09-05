package com.example.upcyclecommunity.community1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upcyclecommunity.BrandList.BrandRecyclerAdapter;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Brand;
import com.example.upcyclecommunity.database.BrandQuery;
import com.example.upcyclecommunity.database.Database;

import java.util.ArrayList;


public class Fragment_CM1 extends Fragment {
    View root;
    RecyclerView CommunityRecycler;
    communityAdapter Cadapter;
    LinearLayoutManager layoutManager;
    ArrayList<TitleInfo> TitleArray;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_cm1, container, false);

        CommunityRecycler = root.findViewById(R.id.title_community1);
        CommunityRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        CommunityRecycler.setLayoutManager(layoutManager);

        Button btn_upload = (Button) root.findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WritePostActivity.class);
                startActivity(intent);
            }
        });

        TitleArray = new ArrayList<>();
        Database db = new Database();
        Cadapter = new communityAdapter(TitleArray, getContext());
        CommunityRecycler.setAdapter(Cadapter);
        db.readAllPost(TitleArray, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                Cadapter.notifyDataSetChanged();
            }

            @Override
            public void ifFail(Object task) {
                Toast.makeText(getContext(), "hello", Toast.LENGTH_SHORT).show();
                return;
            }
        });
        return root;
    }
}
