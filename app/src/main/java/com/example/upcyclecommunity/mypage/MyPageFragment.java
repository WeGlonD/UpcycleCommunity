package com.example.upcyclecommunity.mypage;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Database;
import com.example.upcyclecommunity.database.User;
import com.example.upcyclecommunity.mypage.adapter.PostPageAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.StorageReference;

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

    private ImageView profile_iv;
    private TextView userName_tv;
    private TextView userData1_tv;
    private TextView userData2_tv;
    private TextView userData3_tv;
    private ImageView setting_iv;
    private ImageView logout_iv;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);

        Database db = new Database();
//        if(Database.getAuth().getCurrentUser() == null){
//            Intent it = new Intent(getContext(), LoginActivity.class);
//            startActivity(it);
//        }

        profile_iv = view.findViewById(R.id.my_page_profile_imageView);
        userName_tv = view.findViewById(R.id.my_page_user_name_textView);
//        userData1_tv = view.findViewById(R.id.my_page_data1_textView);
//        userData2_tv = view.findViewById(R.id.my_page_data2_textView);
//        userData3_tv = view.findViewById(R.id.my_page_data3_textView);
        setting_iv = view.findViewById(R.id.my_page_setting_imageView);
        logout_iv = view.findViewById(R.id.my_page_logout_imageView);

        tabLayout = view.findViewById(R.id.my_page_tabLayout);
        viewPager = view.findViewById(R.id.my_page_viewPager);

        setting_iv.setOnClickListener(viw -> {
            db.readUser(new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    User.Data data = ((Task<DataSnapshot>) task).getResult().getValue(User.Data.class);
                    StorageReference storageReference = db.readImage(Database.getUserProfileImageRoot(), Database.getAuth().getCurrentUser().getUid());
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String email = user.getEmail();

                            Intent it = new Intent(getContext(), SettingActivity.class);
                            it.putExtra("picUri", String.valueOf(uri));
                            it.putExtra("name", data.getName());
                            it.putExtra("email", email);
                            startActivity(it);
                        }
                    });
                }

                @Override
                public void ifFail(Object task) {
                    Toast.makeText(getContext(), "fail to load user data", Toast.LENGTH_LONG);
                }
            });
        });
        logout_iv.setOnClickListener(viw -> {
            if(Database.getAuth().getCurrentUser() == null){
                Intent it = new Intent(getContext(), LoginActivity.class);
                startActivity(it);
            }
            else {
                Database.getAuth().signOut();
                Toast.makeText(getContext(), "logout", Toast.LENGTH_SHORT).show();
            }
        });

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
        Database db = new Database();

        if (Database.getAuth().getCurrentUser() != null){
            db.readUser(new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    User.Data data = ((Task<DataSnapshot>) task).getResult().getValue(User.Data.class);
                    StorageReference storageReference = db.readImage(Database.getUserProfileImageRoot(), Database.getAuth().getCurrentUser().getUid());
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (!(isDetached())){
                                Glide.with(getContext()).load(uri).into(profile_iv);
                                userName_tv.setText(data.getName());
                            }
                        }
                    });
                }

                @Override
                public void ifFail(Object task) {
                    Toast.makeText(getContext(), "fail to load user data", Toast.LENGTH_LONG);
                }
            });
        }
    }
}

