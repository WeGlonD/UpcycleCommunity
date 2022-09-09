package com.example.upcyclecommunity.community2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.community1.WritePostActivity;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Database;
import com.example.upcyclecommunity.mypage.LoginActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.type.LatLng;

import java.util.ArrayList;


public class Fragment_CM2 extends Fragment {
    public static final String CATEGORY = "2";

    View root;
    Button upload_btn;

    RecyclerView CommunityRecycler;
    community2Adapter Cadapter;
    LinearLayoutManager layoutManager;
    ArrayList<Long> listData;
    Location location1;
    Location location2;
    Context mContext;

    public static boolean isUpdating = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_cm2, container, false);

        mContext = getContext();

        Database db = new Database();

        upload_btn = root.findViewById(R.id.btn_upload2);
        CommunityRecycler = root.findViewById(R.id.title_community2);

//        location1 = new Location("");
//        location1.setLatitude(37.56553);
//        location1.setLongitude(126.97801);
//
//        location2 = new Location("");
//        location2.setLatitude(37.66166);
//        location2.setLongitude(127.27801);
//
//        Log.d("WeGlonD", "distance "+location1.distanceTo(location2));

        listData = new ArrayList<>();
        Cadapter = new community2Adapter(listData, getContext(), new community2Adapter.clickListener() {
            @Override
            public void mclickListener_Dialog(String postNumber) {
                if (Database.getAuth().getCurrentUser() != null){
                    Database.getDBRoot().child("Post2").child("posting").
                            child(postNumber).child("writer").get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            String user_uid = ((Task<DataSnapshot>) task).getResult().getValue(String.class);
                            if(user_uid.equals(Database.getAuth().getCurrentUser().getUid()))
                                Dialog(postNumber);
                        }
                    });
                }

            }
        });
        CommunityRecycler.setAdapter(Cadapter);
        CommunityRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        CommunityRecycler.setLayoutManager(layoutManager);

//        isUpdating = true;
//        resetListData(5);

        CommunityRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager nowLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (newState == RecyclerView.SCROLL_STATE_SETTLING){
                    if (nowLayoutManager != null){
                        if (isUpdating == false && nowLayoutManager.findFirstCompletelyVisibleItemPosition() > -1){
                            if (nowLayoutManager.findFirstVisibleItemPosition() == 0){
                                Toast.makeText(getContext(), "up "+listData.size(), Toast.LENGTH_LONG).show();
                                isUpdating = true;
                                resetListData(5);
                            }
                            else if (nowLayoutManager.findLastCompletelyVisibleItemPosition() == listData.size() -1) {
                                int position = nowLayoutManager.findLastCompletelyVisibleItemPosition();
                                if (position >= 0){
                                    Long lastPostNumber = listData.get(position);
                                    Toast.makeText(getContext(), "down "+listData.size(), Toast.LENGTH_LONG).show();
                                    //                        Toast.makeText(getContext(), ""+position+" "+lastPostNumber, Toast.LENGTH_LONG).show();
                                    //                        Log.d("Dirtfy_test", ""+position+" "+lastPostNumber);
                                    isUpdating = true;
                                    getListDataWith(lastPostNumber + 1, lastPostNumber + 5);
                                }
                            }
                        }
                    }
                }
            }
        });

        upload_btn.setOnClickListener(view -> {
            if(Database.getAuth().getCurrentUser()!=null) {
                Intent intent = new Intent(getContext(), community2_upload.class);
                intent.putExtra("postn", Long.MAX_VALUE + "");
                startActivity(intent);
            }
            else{
                Toast.makeText(mContext, "로그인 후 게시물을 작성할 수 있습니다!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    public void resetListData(int count){
        Database db = new Database();
        int size = listData.size();
        for(int i = size-1;i >= 0;i--){
            listData.remove(listData.size()-1);
            Cadapter.notifyItemRemoved(i);
        }
        db.readPostsFirst(listData, count, CATEGORY, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                int position = listData.size()-1;
                Cadapter.notifyItemInserted(position);
//                Cadapter.notifyDataSetChanged();
            }

            @Override
            public void ifFail(Object task) {
                Toast.makeText(getContext(), "hello", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    public void getFirstListData(int count){
        Database db = new Database();
        db.readPostsFirst(listData, count, CATEGORY, new Acts() {
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
    }

    public void getListDataWith(Long str, Long end){
        Database db = new Database();
        db.readPostsWith(listData, str, end, CATEGORY, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                int position = listData.size()-1;
                Cadapter.notifyItemInserted(position);
//                Cadapter.notifyDataSetChanged();
//                int position = layoutManager.findLastCompletelyVisibleItemPosition();
//                Long lastPostNumber = listData.get(position);
//                Toast.makeText(getContext(), ""+position+" "+lastPostNumber, Toast.LENGTH_LONG).show();
            }

            @Override
            public void ifFail(Object task) {
                Toast.makeText(getContext(), "hello", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isUpdating = true;
        resetListData(5);
    }

    public void Dialog(String postNumber){
        DialogInterface.OnClickListener ammendimage = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //수정
                change_posting(postNumber,0);
                dialog.dismiss();
            }
        };
        DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
        DialogInterface.OnClickListener deleteimage = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //삭제
                change_posting(postNumber,2);
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(getActivity())
                .setTitle("게시물 변경(본인 게시물만 가능)")
                .setPositiveButton("게시물 수정", ammendimage)
                .setNegativeButton("취소",cancel)
                .setNeutralButton("게시물 삭제", deleteimage)
                .show();
    }

    public void change_posting(String postNumber, int mode){
        Database db = new Database();
        Database.getDBRoot().child("Post"+CATEGORY).child("posting").child(postNumber).child("writer").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String writerUid = task.getResult().getValue(String.class);
                if(Database.getAuth().getCurrentUser()==null){
                    Toast.makeText(getActivity(), "로그인 후 게시물을 수정/삭제할 수 있습니다!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }else {
                    Log.d("WeGlonD", "get : " + Database.getAuth().getCurrentUser().getUid());
                    Log.d("WeGlonD", "post writer : " + writerUid);
                    if (writerUid.equals(Database.getAuth().getCurrentUser().getUid())) {
                        switch (mode) {
                            case 1:
                                //수정 코드
                                Intent it = new Intent(getActivity(), community2_upload.class);
                                it.putExtra("postn", postNumber);
                                startActivity(it);
                                //finish();
                                break;
                            case 2:
                                db.deletePost(Long.parseLong(postNumber), writerUid, CATEGORY, new Acts() {
                                    @Override
                                    public void ifSuccess(Object task) {
                                        //finish();
                                    }
                                    @Override
                                    public void ifFail(Object task) {
                                        Toast.makeText(getActivity(), "삭제실패", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                        }
                    } else {
                        Toast.makeText(getActivity(), "자신의 게시물만 수정/삭제 가능합니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
