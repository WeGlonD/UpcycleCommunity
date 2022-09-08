package com.example.upcyclecommunity.community2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Database;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.StorageReference;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
    private ArrayList<Uri> mItems;
    private ImageView imagecontainer;
    private ArrayList<ImageView> mPages;
    Button upload_content;
    Long msgFromIntent;
    boolean editing;
    public static final String CATEGORY = "2";
    Button write_tag;
    ArrayList<String> tags;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community2_upload);

        btn_addimage = (Button)findViewById(R.id.btn_addimage);      //추가 버튼
        btn_addimage.setOnClickListener(this);                  //클릭 리스너 등록

        Intent it = getIntent();
        msgFromIntent = Long.parseLong(it.getStringExtra("postn"));
        editing = msgFromIntent != Long.MAX_VALUE;
        write_tag = findViewById(R.id.btn_tagInput2);
        write_tag.setOnClickListener(this);
        tags = new ArrayList<>();
        imagecontainer = new ImageView(this);

        upload_content = findViewById(R.id.btn_community2_upload);
        upload_content.setOnClickListener(this);

        mPageMark = (LinearLayout)findViewById(R.id.page_mark);         //상단의 현재 페이지 나타내는 뷰
        viewpager = (ViewPager)findViewById(R.id.view_pager);                  //뷰 페이저
        mItems = new ArrayList<>();
        mPages = new ArrayList<>();
        mViewAdapter = new ViewAdapter(mItems,getApplicationContext());
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
            @Override public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {}
            @Override public void onPageScrollStateChanged(int state){}
        });
        mPrePosition = 0;   //이전 포지션 값 초기화
        if (editing){
            //loadExistingPost(msgFromIntent);
        }
    }

    //상단의 현재 페이지 표시하는 뷰 추가.
    //뷰 페이저에 아이템이 추가 될 때마다 한개 씩 추가한다
    private void addPageMark(){
        ImageView iv = new ImageView(getApplicationContext());   //페이지 표시 이미지 뷰 생성
        iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        iv.setBackgroundResource(R.drawable.page_not);
        mPageMark.addView(iv);//LinearLayout에 추가
        mPages.add(iv);
    }

    //Pager 아답터 구현
    private class ViewAdapter extends PagerAdapter {
        private Context mContext;
        private ArrayList<Uri> mItems;         //아이템 뷰의 타입. 아이템 갯수 만큼
        public ViewAdapter(ArrayList<Uri> mItems,Context con) {
            super(); mContext = con;
            this.mItems = mItems;     //아답터 생성시 리스트 생성
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
                            removeView(position);
                            //removeView(position);
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

        public void removeView(int position) {
            viewpager.setAdapter(null);
            mItems.remove(position);
            mViewAdapter = new ViewAdapter(mItems,mContext);
            viewpager.setAdapter(mViewAdapter);
            mPageMark.removeView(mPages.get(position));
            mPages.remove(position);
            mPrePosition-=1;
            mViewAdapter.notifyDataSetChanged();
        }



        //뷰 객체 삭제.
        @Override public void destroyItem(View pager, int position, Object view) {
            ((ViewPager)pager).removeView((View)view);
            mViewAdapter.notifyDataSetChanged();
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
        else if(v==upload_content){
            Upload_post();
        }
        else if(v==write_tag){
            EditText et_tag = findViewById(R.id.et_tagInput2);
            String newTag = et_tag.getText().toString();
            et_tag.setText("");
            if(!newTag.equals("") && !tags.contains(newTag)){
                LinearLayout tags_field = findViewById(R.id.tagLayout2);
                TextView newTagTv = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(5,5,5,5);
                newTagTv.setLayoutParams(layoutParams);
                newTagTv.setTextSize(15);
                newTagTv.setText(newTag);
                tags.add(newTag);
                newTagTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView now = (TextView) view;
                        //Toast.makeText(getApplicationContext(), now.getText(), Toast.LENGTH_SHORT).show();
                        for(String tmp : tags){
                            if(tmp.equals(now.getText())){
                                tags.remove(tmp);
                                break;
                            }
                        }
                        ((ViewGroup)now.getParent()).removeView(now);
                        Toast.makeText(getApplicationContext(),""+tags.size(),Toast.LENGTH_SHORT).show();
                    }
                });
                tags_field.addView(newTagTv);
            }
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
                    mItems.set(pos,mImageCaptureUri);
                    mViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public static View getView(Uri uri, Context con) {
        ImageView iv = new ImageView(con);
        Glide.with(con).load(uri).centerCrop().override(1000).into(iv);
        return iv;
    }

    public void Upload_post(){
        Database db = new Database();
        String title = ((EditText)findViewById(R.id.community2_upload_title)).getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String time = sdf.format(new Timestamp(System.currentTimeMillis()));

        if(editing){
            //db.deletePostForUpdate(msgFromIntent, preTitle, preTagStr, Database.getAuth().getCurrentUser().getUid(), preImageCnt, CATEGORY);
            //Uploading(msgFromIntent,db,title,time+" (수정됨)");
        }
        else {
            db.setNewPostNumber(CATEGORY, new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    Long postnum = ((Task<DataSnapshot>) task).getResult().getValue(Long.class);
                    Uploading(postnum, db, title, time);
                }

                @Override
                public void ifFail(Object task) {
                }
            });
        }
    }

    public void Uploading(Long postnum, Database db, String title, String time){
        String content = ((EditText)findViewById(R.id.community2_upload_content)).getText().toString();
        db.writePostByLine(postnum, 1l, content, title, time, tags, CATEGORY);
        for(int i = 0;i<mItems.size();i++){
            int fi = i;
            Glide.with(this).load(mItems.get(i)).centerCrop().override(1000).into(imagecontainer);
            Log.d("minseok", "uri : " + mItems.get(i).toString());
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imagecontainer.getDrawable();
            Log.d("minseok", "drawable : " + bitmapDrawable);
            db.writeImage(bitmapDrawable, Database.getPostpictureRoot(), CATEGORY + "-" + postnum + "-" + title + "-" + (i + 2), new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    db.readImage(Database.getPostpictureRoot(), CATEGORY + "-" + postnum + "-" + title + "-" + (fi + 2)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            db.writePostByLine(postnum,fi+2l, url, title, time, tags,CATEGORY);
                            Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void ifFail(Object task) {

                }
            });
        }
    }
}

