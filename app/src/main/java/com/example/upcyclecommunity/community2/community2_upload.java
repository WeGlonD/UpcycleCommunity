package com.example.upcyclecommunity.community2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.upcyclecommunity.R;

import java.util.ArrayList;

public class community2_upload extends Activity implements View.OnClickListener {
    private final int MAX = 10;
    private int mPrePosition;                  //이전에 선택되었던 포지션 값
    private Button btn_addimage;               //뷰 페이저에 아이템 추가하는 버튼
    private ViewPager viewpager;               //뷰 페이저
    private LinearLayout mPageMark;         //현재 몇 페이지 인지 나타내는 뷰
    private ViewAdapter mViewAdapter;         //아답터 객체. 아이템을 추가 하기 위해 변수 선언
    Uri mImageCaptureUri;
    private static final int PICK_FROM_ALBUM = 0,CHANGE_ALBUM = 1;
    private ImageView selected_iv;
    private int pos = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community2_upload);

        btn_addimage = (Button)findViewById(R.id.btn_addimage);      //추가 버튼
        btn_addimage.setOnClickListener(this);                  //클릭 리스너 등록

        mPageMark = (LinearLayout)findViewById(R.id.page_mark);         //상단의 현재 페이지 나타내는 뷰

        viewpager = (ViewPager)findViewById(R.id.view_pager);                  //뷰 페이저
        mViewAdapter = new ViewAdapter(getApplicationContext());
        viewpager.setAdapter(mViewAdapter);
        //PagerAdapter로 설정
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {   //아이템이 변경되면, gallery나 listview의 onItemSelectedListener와 비슷
            //아이템이 선택이 되었으면
            @Override public void onPageSelected(int position) {
                Log.d("minseok","onPageSelected called"+position);
                mPageMark.getChildAt(mPrePosition).setBackgroundResource(R.drawable.page_not);   //이전 페이지에 해당하는 페이지 표시 이미지 변경
                mPageMark.getChildAt(position).setBackgroundResource(R.drawable.page_select);      //현재 페이지에 해당하는 페이지 표시 이미지 변경
                mPrePosition = position;            //이전 포지션 값을 현재로 변경
            }
            @Override public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {
                Log.d("minseok","onPageScrolled called");
            }
            @Override public void onPageScrollStateChanged(int state) {
                Log.d("minseok","onPageScrollStateChanged called"+state);
            }
        });
        mPrePosition = 0;   //이전 포지션 값 초기화
    }

    //상단의 현재 페이지 표시하는 뷰 추가.
    //뷰 페이저에 아이템이 추가 될 때마다 한개 씩 추가한다
    private void addPageMark(){
        ImageView iv = new ImageView(getApplicationContext());   //페이지 표시 이미지 뷰 생성
        iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        iv.setBackgroundResource(R.drawable.page_not);
        mPageMark.addView(iv);//LinearLayout에 추가
    }

    //Pager 아답터 구현
    private class ViewAdapter extends PagerAdapter {
        private Context mContext;
        private ArrayList<Uri> mItems;         //아이템 뷰의 타입. 아이템 갯수 만큼
        public ViewAdapter( Context con) {
            super(); mContext = con;
            mItems = new ArrayList<>();      //아답터 생성시 리스트 생성
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
                    DialogInterface.OnClickListener ammendimage = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //수정
                            pos = position;
                            selected_iv = (ImageView) view;
                            doTakeAlbumAction(1);
                            dialog.dismiss();
                        }
                    };
                    DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    };
                    DialogInterface.OnClickListener deleteimage = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //삭제
                            destroyItem(pager,position,view);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(community2_upload.this)
                            .setTitle("업로드할 이미지 선택")
                            .setPositiveButton("사진 수정", ammendimage)
                            .setNegativeButton("취소",cancel)
                            .setNeutralButton("사진 삭제", deleteimage)
                            .show();
                }
            });
            return v;
        }

        //뷰 객체 삭제.
        @Override public void destroyItem(View pager, int position, Object view) {
            ((ViewPager)pager).removeView((View)view);
        }

        // instantiateItem메소드에서 생성한 객체를 이용할 것인지
        @Override public boolean isViewFromObject(View view, Object obj) { return view == obj; }
        @Override public void finishUpdate(View arg0) {}
        @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
        @Override public Parcelable saveState() { return null; }
        @Override public void startUpdate(View arg0) {}

        public void addItem(Uri uri){
            mItems.add(uri);            //아이템 목록에 추가
            notifyDataSetChanged();      //아답터에 데이터 변경되었다고 알림. 알아서 새로고침
        }
    }

    @Override
    public void onClick(View v) {
        //뷰 페이저에 아이템 추가
        if(v == btn_addimage) {
            if(mViewAdapter.getCount() <= MAX) {         //최대 갯수 이하이면
                doTakeAlbumAction(PICK_FROM_ALBUM);
            }
            else
                Toast.makeText(getApplicationContext(), "최대 10개의 아이템만 등록 가능합니다. 소스를 수정하세요.", Toast.LENGTH_SHORT).show();
        }
    }


    public void doTakeAlbumAction(int mode) // 앨범에서 이미지 가져오기
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        if(mode == 0){
            startActivityForResult(intent, PICK_FROM_ALBUM);
        }
        else if(mode == 1){
            startActivityForResult(intent, CHANGE_ALBUM);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICK_FROM_ALBUM:
                    mImageCaptureUri = data.getData();
                    mViewAdapter.addItem(mImageCaptureUri);      //해당 타입의 아이템을 추가한다. 뷰 페이져 새로고침
                    addPageMark();
                    break;
                case CHANGE_ALBUM:
                    mImageCaptureUri = data.getData();
                    Glide.with(this).load(mImageCaptureUri).centerCrop().override(1000).into(selected_iv);
                    mViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public static View getView(Uri uri, Context con) {
        ImageView iv = new ImageView(con);
        Glide.with(con).load(uri).centerCrop().override(1000).into(iv);
        return iv;
    }
/*
    private static TextView getTextView(Context con) {
        int color = (int)(Math.random()*256);
        TextView tv = new TextView(con);
        tv.setBackgroundColor(Color.rgb(color, color, color));   //배경색 지정
        tv.setText("TextView Item");                           //글자지정
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);      //글자 크기 24sp
        color = 255 - color;
        tv.setTextColor(Color.rgb(color, color, color));         //글자 색상은 배경과 다른 색으로
        tv.setGravity(Gravity.CENTER);
        return tv;
    }
*/
}


/*package com.example.upcyclecommunity.community2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.upcyclecommunity.R;

import java.util.ArrayList;

public class community2_upload extends Activity implements View.OnClickListener {
    private final int MAX = 10;
    private int mPrePosition;						//이전에 선택되었던 포지션 값
    private Button btn_addimage;					//뷰 페이저에 아이템 추가하는 버튼
    private ViewPager viewpager;					//뷰 페이저
    private LinearLayout mPageMark;			//현재 몇 페이지 인지 나타내는 뷰
    private ViewAdapter mViewAdapter;			//아답터 객체. 아이템을 추가 하기 위해 변수 선언
    Uri mImageCaptureUri;
    private static final int PICK_FROM_ALBUM = 0,CHANGE_ALBUM = 1;
    private ImageView selected_iv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community2_upload);

        btn_addimage = (Button)findViewById(R.id.btn_addimage);		//추가 버튼
        btn_addimage.setOnClickListener(this);						//클릭 리스너 등록

        mPageMark = (LinearLayout)findViewById(R.id.page_mark);			//상단의 현재 페이지 나타내는 뷰

        viewpager = (ViewPager)findViewById(R.id.view_pager);						//뷰 페이저
        mViewAdapter = new ViewAdapter(getApplicationContext());
        viewpager.setAdapter(mViewAdapter);
        //PagerAdapter로 설정
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {	//아이템이 변경되면, gallery나 listview의 onItemSelectedListener와 비슷
            //아이템이 선택이 되었으면
            @Override public void onPageSelected(int position) {
                mPageMark.getChildAt(mPrePosition).setBackgroundResource(R.drawable.page_not);	//이전 페이지에 해당하는 페이지 표시 이미지 변경
                mPageMark.getChildAt(position).setBackgroundResource(R.drawable.page_select);		//현재 페이지에 해당하는 페이지 표시 이미지 변경
                mPrePosition = position;				//이전 포지션 값을 현재로 변경
            }
            @Override public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {}
            @Override public void onPageScrollStateChanged(int state) {}
        });
        mPrePosition = 0;	//이전 포지션 값 초기화
    }

    //상단의 현재 페이지 표시하는 뷰 추가.
    //뷰 페이저에 아이템이 추가 될 때마다 한개 씩 추가한다
    private void addPageMark(){
        ImageView iv = new ImageView(getApplicationContext());	//페이지 표시 이미지 뷰 생성
        iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        iv.setBackgroundResource(R.drawable.page_not);
        mPageMark.addView(iv);//LinearLayout에 추가
    }

    //Pager 아답터 구현
    private class ViewAdapter extends PagerAdapter {
        private Context mContext;
        private ArrayList<Uri> mItems;			//아이템 뷰의 타입. 아이템 갯수 만큼
        public ViewAdapter( Context con) {
            super(); mContext = con;
            mItems = new ArrayList<>();		//아답터 생성시 리스트 생성
        }
        //뷰 페이저의 아이템 갯수는 리스트의 갯수
        //나중에 뷰 페이저에 아이템을 추가하면 리스트에 아이템의 타입을 추가 후 새로 고침하게 되면
        //자동으로 뷰 페이저의 갯수도 늘어난다.
        @Override public int getCount() { return mItems == null ? 0:mItems.size(); }

        //뷰페이저에서 사용할 뷰객체 생성/등록
        @Override public Object instantiateItem(View pager, int position)
        {
            Log.d("minseok",position+"");
            View v = getView(mItems.get(position), mContext);	//해당 포지션의 아이템 뷰를 생성한다
            ((ViewPager)pager).addView(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    *//*DialogInterface.OnClickListener ammendimage = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //수정
                            selected_iv = (ImageView) view;
                            doTakeAlbumAction(1);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    };
                    DialogInterface.OnClickListener deleteimage = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //삭제
                            destroyItem(pager,position,view);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(community2_upload.this)
                            .setTitle("업로드할 이미지 선택")
                            .setPositiveButton("사진 수정", ammendimage)
                            .setNeutralButton("사진 삭제", deleteimage)
                            .show();
                    notifyDataSetChanged();*//*
                }
            });
            return v;
        }

        //뷰 객체 삭제.
        @Override public void destroyItem(View pager, int position, Object view) {
            ((ViewPager)pager).removeView((View)view);
            mItems.remove(position);
        }

        // instantiateItem메소드에서 생성한 객체를 이용할 것인지
        @Override public boolean isViewFromObject(View view, Object obj) { return view == obj; }
        @Override public void finishUpdate(View arg0) {}
        @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
        @Override public Parcelable saveState() { return null; }
        @Override public void startUpdate(View arg0) {}

        public void addItem(Uri uri){
            mItems.add(uri);				//아이템 목록에 추가
            notifyDataSetChanged();		//아답터에 데이터 변경되었다고 알림. 알아서 새로고침
        }
    }

    @Override
    public void onClick(View v) {
        //뷰 페이저에 아이템 추가
        if(v == btn_addimage) {
            if(mViewAdapter.getCount() <= MAX) {			//최대 갯수 이하이면
                doTakeAlbumAction(PICK_FROM_ALBUM);
            }
            else
                Toast.makeText(getApplicationContext(), "최대 10개의 아이템만 등록 가능합니다. 소스를 수정하세요.", Toast.LENGTH_SHORT).show();
        }
    }


    public void doTakeAlbumAction(int mode) // 앨범에서 이미지 가져오기
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        if(mode == 0){
            startActivityForResult(intent, PICK_FROM_ALBUM);
        }
        else if(mode == 1){
            startActivityForResult(intent, CHANGE_ALBUM);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICK_FROM_ALBUM:
                    mImageCaptureUri = data.getData();
                    mViewAdapter.addItem(mImageCaptureUri);		//해당 타입의 아이템을 추가한다. 뷰 페이져 새로고침
                    addPageMark();
                    break;
                case CHANGE_ALBUM:
                    mImageCaptureUri = data.getData();
                    Glide.with(this).load(mImageCaptureUri).centerCrop().override(1000).into(selected_iv);
                    break;
            }
        }
    }

    public static View getView(Uri uri, Context con) {
        //LinearLayout sv = new LinearLayout(con);
        ImageView iv = new ImageView(con);
        Glide.with(con).load(uri).centerCrop().override(1000).into(iv);
        //iv.setBackgroundResource(R.drawable.search);
        //sv.addView(iv);
        return iv;
    }
}*/

