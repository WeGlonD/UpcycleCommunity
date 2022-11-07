package com.uca.upcyclecommunity.mypage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.uca.upcyclecommunity.R;
import com.uca.upcyclecommunity.database.Acts;
import com.uca.upcyclecommunity.database.Database;
import com.uca.upcyclecommunity.database.User;
import com.uca.upcyclecommunity.mypage.adapter.PostPageAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

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
    private ProgressBar profile_progressbar;
    private TextView userName_tv;
    private TextView userData1_tv;
    private TextView userData2_tv;
    private TextView userData3_tv;
    private ImageView setting_iv;
    private ImageView logout_iv;
    private Button setting_btn;

    private TabLayout tabLayout;
    private int currentTabIndex = 0;

    private ViewPager viewPager;
    private PostPageAdapter postPageAdapter;
    private ArrayList<Fragment> fragments;

    private Context context;
    private RequestManager mGlideRequestManager;

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
        context = getContext();
        mGlideRequestManager = Glide.with(getActivity());
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
        profile_progressbar = view.findViewById(R.id.my_page_progress_circular);
        userName_tv = view.findViewById(R.id.my_page_user_name_textView);
        userData1_tv = view.findViewById(R.id.my_page_data1_textView);
        userName_tv.setSelected(true);
        userName_tv.setHorizontallyScrolling(true);
        userData2_tv = view.findViewById(R.id.my_page_data2_textView);
        userData3_tv = view.findViewById(R.id.my_page_data3_textView);
//        setting_iv = view.findViewById(R.id.my_page_setting_imageView);
//        logout_iv = view.findViewById(R.id.my_page_logout_imageView);
        setting_btn = view.findViewById(R.id.my_page_setting_button);

        tabLayout = view.findViewById(R.id.my_page_tabLayout);
        viewPager = view.findViewById(R.id.my_page_viewPager);

//        setting_iv.setOnClickListener(viw -> {
//            ProgressDialog dialog = new ProgressDialog(context);
//            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            dialog.setTitle("loading...");
//            dialog.show();
//            db.readUser(new Acts() {
//                @Override
//                public void ifSuccess(Object task) {
//                    User.Data data = ((Task<DataSnapshot>) task).getResult().getValue(User.Data.class);
//                    StorageReference storageReference = db.readImage(Database.getUserProfileImageRoot(), Database.getAuth().getCurrentUser().getUid());
//                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//
//                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                            String email = user.getEmail();
//
//                            Intent it = new Intent(context, SettingActivity.class);
//                            it.putExtra("picUri", String.valueOf(uri));
//                            it.putExtra("name", data.getName());
//                            it.putExtra("email", email);
//                            dialog.dismiss();
//                            startActivity(it);
//                        }
//                    });
//                }
//
//                @Override
//                public void ifFail(Object task) {
//                    dialog.dismiss();
//                    Toast.makeText(getContext(), "fail to load user data", Toast.LENGTH_LONG);
//                }
//            });
//        });
        setting_btn.setOnClickListener(viw -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                ProgressDialog dialog = new ProgressDialog(context);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setTitle("정보 불러오는 중...");
                dialog.show();
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

                                Intent it = new Intent(context, SettingActivity.class);
                                it.putExtra("picUri", String.valueOf(uri));
                                it.putExtra("name", data.getName());
                                it.putExtra("email", email);
                                dialog.dismiss();
                                startActivity(it);
                            }
                        });
                    }

                    @Override
                    public void ifFail(Object task) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "유저정보를 불러오는데 실패했습니다", Toast.LENGTH_LONG);
                    }
                });
            }
            else{
                Toast.makeText(context, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        });
//        logout_iv.setOnClickListener(viw -> {
//            if(Database.getAuth().getCurrentUser() == null){
//                Intent it = new Intent(getContext(), LoginActivity.class);
//                startActivity(it);
//            }
//            else {
//                Database.getAuth().signOut();
//                onResume();
//                Toast.makeText(getContext(), "logout", Toast.LENGTH_SHORT).show();
//            }
//        });

        tabLayout.addTab(tabLayout.newTab().setText(R.string.my_page_post1_tab_name));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.my_page_post2_tab_name));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.my_page_post3_tab_name));
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

        fragments = new ArrayList<>();
        fragments.add(new MyPagePost1_Fragment());
        fragments.add(new MyPagePost2_Fragment());
        fragments.add(new MyPagePost3_Fragment());
        postPageAdapter = new PostPageAdapter(getChildFragmentManager(), fragments, tabLayout.getTabCount());
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
                    profile_progressbar.setVisibility(View.VISIBLE);

                    DataSnapshot dataSnapshot = ((Task<DataSnapshot>) task).getResult();
                    for (DataSnapshot dataLine : dataSnapshot.getChildren()){
                        String key = dataLine.getKey();
                        if (key.equals("post1")){
                            long count = dataLine.getChildrenCount()-1;
                            userData2_tv.setText(String.valueOf(count));
                        }
                        else if (key.equals("post2")){
                            long count = dataLine.getChildrenCount()-1;
                            userData1_tv.setText(String.valueOf(count));
                        }
                        else if (key.equals("post3")){
                            long count = dataLine.getChildrenCount()-1;
                            userData3_tv.setText(String.valueOf(count));
                        }
                        else if (key.equals("name")){
                            String value = dataLine.getValue(String.class);
                            userName_tv.setText(value);
                        }
                    }
                    StorageReference storageReference = db.readImage(Database.getUserProfileImageRoot(), Database.getAuth().getCurrentUser().getUid());
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (mGlideRequestManager != null){
                                profile_progressbar.setVisibility(View.INVISIBLE);
                                mGlideRequestManager.load(uri).into(profile_iv);
                            }
                        }
                    });
                }

                @Override
                public void ifFail(Object task) {
                    profile_progressbar.setVisibility(View.INVISIBLE);
                    profile_iv.setImageResource(R.drawable.ic_baseline_person_24);
                    Toast.makeText(getContext(), "유저 정보를 불러오는데 실패했습니다..", Toast.LENGTH_LONG);
                }
            });
        }
        else {
            profile_progressbar.setVisibility(View.INVISIBLE);
            profile_iv.setImageResource(R.drawable.ic_baseline_person_24);
            userName_tv.setText(R.string.my_page_user_name_initialValue);
            userData1_tv.setText("0");
            userData2_tv.setText("0");
            userData3_tv.setText("0");
            fragments.clear();
            fragments.add(new MyPagePost1_Fragment());
            fragments.add(new MyPagePost2_Fragment());
            fragments.add(new MyPagePost3_Fragment());
//            viewPager.setCurrentItem(currentTabIndex);
            int now = currentTabIndex;
            for(int i = 0;i < fragments.size();i++){
                viewPager.setCurrentItem(i);
            }
            viewPager.setCurrentItem(now);
        }
    }
}

