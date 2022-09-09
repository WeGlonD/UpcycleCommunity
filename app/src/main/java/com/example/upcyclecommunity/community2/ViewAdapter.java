package com.example.upcyclecommunity.community2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

//Pager 아답터 구현
public class ViewAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<Uri> mItems;         //아이템 뷰의 타입. 아이템 갯수 만큼
    private clickListener mclickListener;

    interface clickListener{
        public void mclickListener_Dialog(View view, int position);
    }

    public ViewAdapter(ArrayList<Uri> mItems,Context con, clickListener mclickListener) {
        super(); mContext = con;
        this.mItems = mItems;//아답터 생성시 리스트 생성
        this.mclickListener = mclickListener;
    }

    //뷰 페이저의 아이템 갯수는 리스트의 갯수
    //나중에 뷰 페이저에 아이템을 추가하면 리스트에 아이템의 타입을 추가 후 새로 고침하게 되면
    //자동으로 뷰 페이저의 갯수도 늘어난다.

    @Override public int getCount() { return mItems == null ? 0:mItems.size(); }

    //뷰페이저에서 사용할 뷰객체 생성/등록
    @Override public Object instantiateItem(View pager, int position)
    {
        View v = getView(mItems.get(position), mContext);   //해당 포지션의 아이템 뷰를 생성한다
        ((ViewPager)pager).addView(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("minseok","instantiateItem called"+view+position);
                mclickListener.mclickListener_Dialog(view,position);
            }
        });
        return v;
    }

    //뷰 객체 삭제.
    @Override public void destroyItem(View pager, int position, Object view) {
        ((ViewPager)pager).removeView((View)view);
        notifyDataSetChanged();
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //Log.d("minseok",position+"setprimaryItem");
        super.setPrimaryItem(container, position, object);
    }

    // instantiateItem메소드에서 생성한 객체를 이용할 것인지
    @Override public boolean isViewFromObject(View view, Object obj) { return view == obj; }
    @Override public void finishUpdate(View arg0) {}
    @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
    @Override public Parcelable saveState() { return null; }
    @Override public void startUpdate(View arg0) {}

    public void addItem(Uri uri){
        mItems.add(uri);            //아이템 목록에 추가
        View v = getView(uri,mContext);
        Log.d("minseok","뭐임"+(Object)v);
        notifyDataSetChanged();      //아답터에 데이터 변경되었다고 알림. 알아서 새로고침
    }

    public static View getView(Uri uri, Context con) {
        ImageView iv = new ImageView(con);
        Glide.with(con).load(uri).centerCrop().override(1000).into(iv);
        return iv;
    }
}
