package com.example.upcyclecommunity.community2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Database;

import java.util.ArrayList;


public class Fragment_CM2 extends Fragment {
    public static final String CATEGORY = "2";

    View root;
    Button upload_btn;

    RecyclerView CommunityRecycler;
    community2Adapter Cadapter;
    LinearLayoutManager layoutManager;
    ArrayList<Long> listData;

    Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_cm2, container, false);

        mContext = getContext();

        Database db = new Database();

        upload_btn = root.findViewById(R.id.btn_upload2);
        CommunityRecycler = root.findViewById(R.id.title_community2);

        listData = new ArrayList<>();
        Cadapter = new community2Adapter(listData, getContext());
        CommunityRecycler.setAdapter(Cadapter);
        CommunityRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        CommunityRecycler.setLayoutManager(layoutManager);

        db.readAllPost(listData, CATEGORY, new Acts() {
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

        upload_btn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), community2_upload.class);
            startActivity(intent);
        });

        return root;
    }
}
