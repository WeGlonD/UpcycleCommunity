package com.example.upcyclecommunity.mypage.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.upcyclecommunity.mypage.MyPagePost1_Fragment;
import com.example.upcyclecommunity.mypage.MyPagePost2_Fragment;

public class PostPageAdapter extends FragmentStatePagerAdapter {
    private int numberOfFragment;

    public PostPageAdapter(FragmentManager fm, int numberOfFragment){
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numberOfFragment = numberOfFragment;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new MyPagePost1_Fragment();
            default: return new MyPagePost2_Fragment();
        }
    }

    @Override
    public int getCount() {
        return numberOfFragment;
    }
}

