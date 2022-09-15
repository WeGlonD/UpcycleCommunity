package com.example.upcyclecommunity.community1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.upcyclecommunity.mypage.LoginActivity;

import java.util.ArrayList;


public class Fragment_CM1 extends Fragment {
    public static final String CATEGORY = "1";

    View root;
//    Button load_more_btn;
    Button upload_btn;
    Button cur_pos_condition_btn;

    RecyclerView CommunityRecycler;
    communityAdapter Cadapter;
    LinearLayoutManager layoutManager;
    ArrayList<Long> listData;

    int distance = -1;
    Context mContext;
    public static boolean isUpdating;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_cm1, container, false);

        mContext = getContext();

        Database db = new Database();

//        load_more_btn = root.findViewById(R.id.fragment_cm1_load_more_button);
        upload_btn = root.findViewById(R.id.btn_upload);
        cur_pos_condition_btn = root.findViewById(R.id.currPosCondition);
        cur_pos_condition_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cur_pos_condition_btn.isSelected()) {
                    distance = -1;
                    cur_pos_condition_btn.setSelected(false);
                    isUpdating = true;
                    resetListData(5);
                }
                else {
                    //다이얼로그
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("몇 킬로미터 내의 게시물만 조회할까요?")
                                    .setMultiChoiceItems(new String[]{"3km", "5km", "10km"}, null, new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                            switch (i) {
                                                case 0:
                                                    distance = 3;
                                                    break;
                                                case 1:
                                                    distance = 5;
                                                    break;
                                                case 2:
                                                    distance = 10;
                                                    break;
                                            }
                                            dialogInterface.dismiss();
                                            if(distance > 0) {
                                                cur_pos_condition_btn.setSelected(true);
                                                isUpdating = true;
                                                resetListData(5);
                                            }
                                        }
                                    });
                    builder.show();
                }
            }
        });
        CommunityRecycler = root.findViewById(R.id.title_community1);

        listData = new ArrayList<>();
        Cadapter = new communityAdapter(listData, getContext(), (str, end) -> getListDataWith(str, end));
        CommunityRecycler.setAdapter(Cadapter);
        CommunityRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        CommunityRecycler.setLayoutManager(layoutManager);

//        isUpdating = true;
//        resetListData(10);

        CommunityRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager nowLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (newState == RecyclerView.SCROLL_STATE_SETTLING){
                    if (isUpdating == false && nowLayoutManager.findFirstCompletelyVisibleItemPosition() > -1){
                        if (nowLayoutManager.findFirstCompletelyVisibleItemPosition() == 0){
//                            Toast.makeText(getContext(), "up "+listData.size(), Toast.LENGTH_LONG).show();
                            isUpdating = true;
                            resetListData(15);
                        }
//                        else if (nowLayoutManager.findLastCompletelyVisibleItemPosition() == listData.size() -1){
//                            int position = nowLayoutManager.findLastCompletelyVisibleItemPosition();
//                            Long lastPostNumber = listData.get(position);
////                            Toast.makeText(getContext(), "down "+listData.size(), Toast.LENGTH_LONG).show();
////                        Toast.makeText(getContext(), ""+position+" "+lastPostNumber, Toast.LENGTH_LONG).show();
////                        Log.d("Dirtfy_test", ""+position+" "+lastPostNumber);
//                            isUpdating = true;
//                            getListDataWith(lastPostNumber+1, lastPostNumber+5);
//                        }
                    }
                }
            }
        });

//        load_more_btn.setOnClickListener(view -> {
//            int position = layoutManager.findLastCompletelyVisibleItemPosition();
//            Long lastPostNumber = listData.get(position);
//            isUpdating = true;
//            getListDataWith(lastPostNumber+1, lastPostNumber+5);
//        });

        upload_btn.setOnClickListener(view -> {
            if(Database.getAuth().getCurrentUser()!=null) {
                Intent intent = new Intent(getContext(), WritePostActivity.class);
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
        if(!cur_pos_condition_btn.isSelected()) {
            db.readPostsFirst(listData, count, CATEGORY, new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    int position = listData.size() - 1;
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
        else{
            db.readNearPostsFirst(listData, count, distance, CATEGORY, new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    int position = listData.size() - 1;
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
        int lastPosition = listData.size()-1;
        listData.remove(lastPosition);
        Cadapter.notifyItemRemoved(lastPosition);
        if(!cur_pos_condition_btn.isSelected()) {
            db.readPostsWith(listData, str, end, CATEGORY, new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    int position = listData.size() - 1;
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
        else{
            db.readNearPostsWith(listData, str, end, (double)distance, CATEGORY, new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    int position = listData.size() - 1;
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
    }

    @Override
    public void onResume() {
        super.onResume();
        isUpdating = true;
        resetListData(15);
    }
}
