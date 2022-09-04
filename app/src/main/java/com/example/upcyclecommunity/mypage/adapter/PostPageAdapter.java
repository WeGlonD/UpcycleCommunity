package com.example.upcyclecommunity.mypage.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.upcyclecommunity.mypage.MyPagePost1_Fragment;
import com.example.upcyclecommunity.mypage.MyPagePost2_Fragment;

public class PostPageAdapter extends FragmentStatePagerAdapter {
    private int numberOfFragment;

    private MyPagePost1_Fragment post1_fragment = null;
    private MyPagePost2_Fragment post2_fragment = null;

    public PostPageAdapter(FragmentManager fm, int numberOfFragment){
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numberOfFragment = numberOfFragment;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if (post1_fragment == null)
                    post1_fragment = new MyPagePost1_Fragment();
                return post1_fragment;
            default:
                if (post2_fragment == null)
                    post2_fragment = new MyPagePost2_Fragment();
                return post2_fragment;
        }
    }

    @Override
    public int getCount() {
        return numberOfFragment;
    }
}

