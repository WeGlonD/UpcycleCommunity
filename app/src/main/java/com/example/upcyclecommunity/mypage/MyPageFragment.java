package com.example.upcyclecommunity.mypage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.mypage.adapter.PostPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth = null;

    private ImageView profile_iv;
    private TextView userName_tv;
    private TextView userData1_tv;
    private TextView userData2_tv;
    private TextView userData3_tv;

    private TabLayout tabLayout;
    private int currentTabIndex = 0;

    private ViewPager viewPager;
    private PostPageAdapter postPageAdapter;

    public MyPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPageFragment newInstance(String param1, String param2) {
        MyPageFragment fragment = new MyPageFragment();
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
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);

        if(mAuth.getCurrentUser() == null){
            Intent it = new Intent(getContext(), LoginActivity.class);
            startActivity(it);
        }

        profile_iv = view.findViewById(R.id.my_page_profile_imageView);
        userName_tv = view.findViewById(R.id.my_page_user_name_textView);
        userData1_tv = view.findViewById(R.id.my_page_data1_textView);
        userData2_tv = view.findViewById(R.id.my_page_data2_textView);
        userData3_tv = view.findViewById(R.id.my_page_data3_textView);

        tabLayout = view.findViewById(R.id.my_page_tabLayout);
        viewPager = view.findViewById(R.id.my_page_viewPager);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.my_page_post1_tab_name));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.my_page_post2_tab_name));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabIndex = tab.getPosition();
                viewPager.setCurrentItem(currentTabIndex);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        postPageAdapter = new PostPageAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(postPageAdapter);
        viewPager.setCurrentItem(currentTabIndex);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAuth.getCurrentUser() != null){
//            FirebaseUser user = mAuth.getCurrentUser();
//            profile_iv.setImageResource(user.pic);
//            userName_tv.setText(user.name);
//            userData1_tv.setText(user.data1);
//            userData2_tv.setText(user.data2);
//            userData3_tv.setText(user.data3);
            profile_iv.setImageResource(R.drawable.ic_launcher_background);
            userName_tv.setText("name");
            userData1_tv.setText(String.valueOf(1));
            userData2_tv.setText(String.valueOf(2));
            userData3_tv.setText(String.valueOf(3));
        }
    }
}

