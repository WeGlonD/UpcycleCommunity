package com.example.upcyclecommunity.mypage.adapter;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.upcyclecommunity.mypage.MyPagePost1_Fragment;
import com.example.upcyclecommunity.mypage.MyPagePost2_Fragment;
import com.example.upcyclecommunity.mypage.MyPagePost3_Fragment;

import java.util.ArrayList;

public class PostPageAdapter extends FragmentStatePagerAdapter {
    private int numberOfFragment;

    private ArrayList<Fragment> fragments;

    public PostPageAdapter(FragmentManager fm, ArrayList<Fragment> fragments, int numberOfFragment){
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragments = fragments;
        this.numberOfFragment = numberOfFragment;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return numberOfFragment;
    }

    public MyPagePost1_Fragment getPost1_fragment() {
        return (MyPagePost1_Fragment) fragments.get(0);
    }

    public MyPagePost2_Fragment getPost2_fragment() {
        return (MyPagePost2_Fragment) fragments.get(1);
    }

    public MyPagePost3_Fragment getPost3_fragment() {
        return (MyPagePost3_Fragment) fragments.get(2);
    }
}

