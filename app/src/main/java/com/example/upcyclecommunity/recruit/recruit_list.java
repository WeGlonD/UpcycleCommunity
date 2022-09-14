package com.example.upcyclecommunity.recruit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.community1.WritePostActivity;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Database;

import java.util.ArrayList;

public class recruit_list extends AppCompatActivity {
    RecyclerView recruit_recyclerview;
    recruit_Adapter Radapter;
    public static boolean recruit_isUpdating;
    ArrayList<Long> listData = new ArrayList<>();
    Context mContext;
    String CATEGORY = "3";
    Button writeRecruit;
    String recruitpostnum;
    String comu1postnum;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recruit_list_layout);
        mContext = this;

        recruitpostnum = getIntent().getStringExtra("postn");
        comu1postnum = getIntent().getStringExtra("recruitPostnum");
        writeRecruit = findViewById(R.id.recruit_btn_upload);
        writeRecruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(mContext, WritePostActivity.class);
                it.putExtra("postn",Long.MAX_VALUE+"");
                it.putExtra("recruitPostnum", comu1postnum);
                startActivity(it);
            }
        });
        recruit_recyclerview = findViewById(R.id.title_recruit);
        recruit_recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        Radapter = new recruit_Adapter(listData,mContext,(str, end) -> getListDataWith(str, end));
        recruit_recyclerview.setAdapter(Radapter);
        recruit_isUpdating = true;
        resetListData(10);
        recruit_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("minseok","ScrollStateChanged");
                LinearLayoutManager nowLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (newState == RecyclerView.SCROLL_STATE_SETTLING){
                    if (recruit_isUpdating == false && nowLayoutManager.findFirstCompletelyVisibleItemPosition() > -1){
                        if (nowLayoutManager.findFirstCompletelyVisibleItemPosition() == 0){
//                            Toast.makeText(getContext(), "up "+listData.size(), Toast.LENGTH_LONG).show();
                            recruit_isUpdating = true;
                            resetListData(15);
                        }
                    }
                }
            }
        });
    }

    public void resetListData(int count){
        Database db = new Database();
        Log.d("minseok","resetListData");
        int size = listData.size();
        for(int i = size-1;i >= 0;i--){
            listData.remove(listData.size()-1);
            Radapter.notifyItemRemoved(i);
        }
        db.readAllRecruit(listData, comu1postnum, count, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                int position = listData.size() - 1;
                Radapter.notifyItemInserted(position);
//                Cadapter.notifyDataSetChanged();
            }

            @Override
            public void ifFail(Object task) {
                Toast.makeText(mContext, "hello", Toast.LENGTH_SHORT).show();
                return;
            }
        });

    }

    public void getListDataWith(Long str, Long end) {
        Database db = new Database();
        Log.d("minseok","getListDataWith");
        int lastPosition = listData.size() - 1;
        listData.remove(lastPosition);
        Radapter.notifyItemRemoved(lastPosition);
        db.readRecruitPostsWith(listData, comu1postnum, str, end, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                int position = listData.size() - 1;
                Radapter.notifyItemInserted(position);
            }
            @Override
            public void ifFail(Object task) {
                Toast.makeText(mContext, "hello", Toast.LENGTH_SHORT).show();
                return;
            }
        });

    }
}