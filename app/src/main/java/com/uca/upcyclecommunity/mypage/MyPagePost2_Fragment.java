package com.uca.upcyclecommunity.mypage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.uca.upcyclecommunity.R;
import com.uca.upcyclecommunity.community1.communityAdapter;
import com.uca.upcyclecommunity.database.Acts;
import com.uca.upcyclecommunity.database.Database;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPagePost2_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPagePost2_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Database db = new Database();
    private Context mContext;
    private RequestManager mGlideRequestManager;

    private RecyclerView recyclerView;
//    private Post2_RecyclerViewAdapter recyclerViewAdapter;
    private communityAdapter recyclerViewAdapter;
    private ArrayList<Long> listData;

    public MyPagePost2_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPagePost2_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPagePost2_Fragment newInstance(String param1, String param2) {
        MyPagePost2_Fragment fragment = new MyPagePost2_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mContext = getContext();
        mGlideRequestManager = Glide.with(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_page_post2_, container, false);

        listData = new ArrayList<>();
        recyclerView = view.findViewById(R.id.my_page_post2_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        //커스텀 어댑터 생성
//        recyclerViewAdapter = new Post2_RecyclerViewAdapter(listData, "1", getContext(), mGlideRequestManager);
        recyclerViewAdapter = new communityAdapter(listData, "1", mContext);
        recyclerView.setAdapter(recyclerViewAdapter);

//        for (int i = 0;i < 15;i++){
//            Post2 data = new Post2();
//            recyclerViewAdapter.addItem(data);
//        }
//        recyclerViewAdapter.notifyDataSetChanged();
//
//        Toast.makeText(getContext(), String.valueOf(recyclerViewAdapter.getItemCount()), Toast.LENGTH_SHORT).show();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(db.getAuth().getCurrentUser() != null){
            listData.clear();
            recyclerViewAdapter.notifyDataSetChanged();
            db.readAllUserPost1(listData, new Acts() {
                @Override
                public void ifSuccess(Object task) {
//                    Collections.sort(listData);
//                    recyclerViewAdapter.notifyDataSetChanged();
                    int position = listData.size()-1;
                    recyclerViewAdapter.notifyItemInserted(position);
//                    if (mContext != null){
//                        Toast.makeText(mContext, String.valueOf(recyclerViewAdapter.getItemCount()), Toast.LENGTH_SHORT).show();
//                        Log.d(getContext().getString(R.string.Dirtfy_test), "post2 list count "+String.valueOf(listData.size()));
//                    }
                }

                @Override
                public void ifFail(Object task) {
                    if (mContext != null)
                        Toast.makeText(mContext, "유저 게시글을 불러오는데 실패 했습니다..", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            listData.clear();
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }
}