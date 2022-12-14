package com.uca.upcyclecommunity.mypage;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uca.upcyclecommunity.R;
import com.uca.upcyclecommunity.community2.community2Adapter;
import com.uca.upcyclecommunity.database.Acts;
import com.uca.upcyclecommunity.database.Database;

import java.util.ArrayList;

public class MyPost1_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    community2Adapter recyclerView_adapter;
    LinearLayoutManager layoutManager;
    ArrayList<Long> listData;

    Database db = new Database();

    Context mContext;
    public static String CATEGORY = "3";
    public static boolean isUpdating = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post1);

        mContext = this;

        listData = new ArrayList<>();
        recyclerView = findViewById(R.id.activity_my_post1_recyclerView);
        recyclerView_adapter = new community2Adapter(listData, mContext, new community2Adapter.clickListener() {
            @Override
            public void mclickListener_Dialog(String postNumber) {

            }
        });
        layoutManager = new LinearLayoutManager(mContext);

        recyclerView.setAdapter(recyclerView_adapter);
        recyclerView.setLayoutManager(layoutManager);

        isUpdating = true;
        loadListData(getIntent().getIntExtra("position", 0));
    }

    public void loadListData(int position){
        Database db = new Database();
        int size = listData.size();
        for(int i = size-1;i >= 0;i--){
            listData.remove(listData.size()-1);
            recyclerView_adapter.notifyItemRemoved(i);
        }

        db.readAllUserPost2AndScroll(listData, position, recyclerView, new Acts(){
            @Override
            public void ifSuccess(Object task) {
                int position = listData.size() - 1;
                recyclerView_adapter.notifyItemInserted(position);
//                Cadapter.notifyDataSetChanged();
            }

            @Override
            public void ifFail(Object task) {
                Toast.makeText(mContext, "?????? ???????????? ??????????????? ?????? ????????????.", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
