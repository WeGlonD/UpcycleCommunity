package com.example.upcyclecommunity.mypage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.database.Post1;
import com.example.upcyclecommunity.database.Post2;
import com.example.upcyclecommunity.mypage.adapter.Post1_RecyclerViewAdapter;
import com.example.upcyclecommunity.mypage.adapter.Post2_RecyclerViewAdapter;

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

    private RecyclerView recyclerView;
    private Post2_RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Post2> listData;

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
        recyclerViewAdapter = new Post2_RecyclerViewAdapter(listData, getContext());
        recyclerView.setAdapter(recyclerViewAdapter);

        for (int i = 0;i < 15;i++){
            Post2 data = new Post2();
            recyclerViewAdapter.addItem(data);
        }
        recyclerViewAdapter.notifyDataSetChanged();

        Toast.makeText(getContext(), String.valueOf(recyclerViewAdapter.getItemCount()), Toast.LENGTH_SHORT).show();

        return view;
    }
}