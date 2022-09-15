package com.example.upcyclecommunity.community2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.community1.WritePostActivity;
import com.example.upcyclecommunity.community1.WritePostUploading;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Database;
import com.example.upcyclecommunity.database.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.StorageReference;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class community2_upload extends AppCompatActivity implements View.OnClickListener {
    private final int MAX = 10;
    private int mPrePosition;                  //이전에 선택되었던 포지션 값
    private Button btn_addimage;               //뷰 페이저에 아이템 추가하는 버튼
    private ViewPager viewpager;               //뷰 페이저
    private LinearLayout mPageMark;         //현재 몇 페이지 인지 나타내는 뷰
    private ViewAdapter mViewAdapter;         //아답터 객체. 아이템을 추가 하기 위해 변수 선언
    Uri mImageCaptureUri;
    private static final int PICK_FROM_ALBUM = 0,CHANGE_ALBUM = 1;
    public static ImageView community2_upload_selected_iv;
    public static int community2_pos = 0;
    private ArrayList<Uri> mItems;
    private ImageView imagecontainer;
    private ArrayList<ImageView> mPages;
    Button upload_content;
    Long msgFromIntent;
    boolean editing;
    public static final String CATEGORY = "2";
    Button write_tag;
    ArrayList<String> tags;
    private Context context;
    String preTitle;
    String preTagStr;
    int preImageCnt;
    public static String community2_change_content = "";
    WritePostUploading writePostUploading;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community2_upload);

        btn_addimage = (Button)findViewById(R.id.btn_addimage);      //추가 버튼
        btn_addimage.setOnClickListener(this);                  //클릭 리스너 등록

        Intent it = getIntent();
        msgFromIntent = Long.parseLong(it.getStringExtra("postn"));
        context = this;
        editing = msgFromIntent != Long.MAX_VALUE;
        write_tag = findViewById(R.id.btn_tagInput2);
        write_tag.setOnClickListener(this);
        tags = new ArrayList<>();
        imagecontainer = new ImageView(this);

        writePostUploading = new WritePostUploading(this);
        writePostUploading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        writePostUploading.setCancelable(false);

        upload_content = findViewById(R.id.btn_community2_upload);
        upload_content.setOnClickListener(this);

        mPageMark = (LinearLayout)findViewById(R.id.page_mark);         //상단의 현재 페이지 나타내는 뷰
        viewpager = (ViewPager)findViewById(R.id.view_pager);                  //뷰 페이저
        mItems = new ArrayList<>();
        mPages = new ArrayList<>();
        mViewAdapter = new ViewAdapter(mItems, getApplicationContext(),new ViewAdapter.clickListener() {
            @Override
            public void mclickListener_Dialog(View view, int position) {
                Dialog(view,position);
            }
        });
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
            loadExistingPost(msgFromIntent);
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
        if (mPages.size() >= 1){
            findViewById(R.id.community2_upload_noImage).setVisibility(View.GONE);
        }
        else{
            findViewById(R.id.community2_upload_noImage).setVisibility(View.VISIBLE);
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
            writePostUploading.show();
            Upload_post();
        }
        else if(v==write_tag){
            EditText et_tag = findViewById(R.id.et_tagInput2);
            String newTag = et_tag.getText().toString();
            if(newTag.contains(".")){
                Toast.makeText(context, "'.'은 입력 불가능합니다!!", Toast.LENGTH_SHORT).show();
            }
            else if(newTag.contains("#")){
                Toast.makeText(context, "'#'은 입력 불가능합니다!!", Toast.LENGTH_SHORT).show();
            }
            else if(newTag.contains("$")){
                Toast.makeText(context, "'$'은 입력 불가능합니다!!", Toast.LENGTH_SHORT).show();
            }
            else if(newTag.contains("[")){
                Toast.makeText(context, "'['은 입력 불가능합니다!!", Toast.LENGTH_SHORT).show();
            }
            else if(newTag.contains("]")){
                Toast.makeText(context, "']'은 입력 불가능합니다!!", Toast.LENGTH_SHORT).show();
            }
            else if (newTag.equals("")){
                Toast.makeText(context, "태그를 입력해 주세요!!", Toast.LENGTH_SHORT).show();
            }
            else{
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
    }

    public void removeView(int position) {
        viewpager.setAdapter(null);
        mItems.remove(position);
        if (mItems.size() >= 1){
            findViewById(R.id.community2_upload_noImage).setVisibility(View.GONE);
        }
        else{
            findViewById(R.id.community2_upload_noImage).setVisibility(View.VISIBLE);
        }
        mViewAdapter = new ViewAdapter(mItems, getApplicationContext(), new ViewAdapter.clickListener() {
            @Override
            public void mclickListener_Dialog(View view, int position) {
                Dialog(view, position);
            }
        });
        viewpager.setAdapter(mViewAdapter);
        mPageMark.removeView(mPages.get(position));
        mPages.remove(position);
        if(mPrePosition>0){
            mPrePosition-=1;
        }
        mViewAdapter.notifyDataSetChanged();
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
                    Glide.with(this).load(mImageCaptureUri).centerCrop().override(1000).into(community2_upload_selected_iv);
                    mItems.set(community2_pos,mImageCaptureUri);
                    mViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void Upload_post(){
        Database db = new Database();
        String title = ((EditText)findViewById(R.id.community2_upload_title)).getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String time = sdf.format(new Timestamp(System.currentTimeMillis()));

        if(mItems.size()==0){
            Toast.makeText(context, "이미지 없이는 작성할 수 없습니다!!", Toast.LENGTH_SHORT).show();
            writePostUploading.dismiss();
        }
        else if(title.contains(".")){
            Toast.makeText(context, "'.'은 제목으로 입력 불가능합니다!!", Toast.LENGTH_SHORT).show();
            writePostUploading.dismiss();
        }
        else if(title.contains("#")){
            Toast.makeText(context, "'#'은 제목으로 입력 불가능합니다!!", Toast.LENGTH_SHORT).show();
            writePostUploading.dismiss();
        }
        else if(title.contains("$")){
            Toast.makeText(context, "'$'은 제목으로 입력 불가능합니다!!", Toast.LENGTH_SHORT).show();
            writePostUploading.dismiss();
        }
        else if(title.contains("[")){
            Toast.makeText(context, "'['은 제목으로 입력 불가능합니다!!", Toast.LENGTH_SHORT).show();
            writePostUploading.dismiss();
        }
        else if(title.contains("]")){
            Toast.makeText(context, "']'은 제목으로 입력 불가능합니다!!", Toast.LENGTH_SHORT).show();
            writePostUploading.dismiss();
        }
        else if (title.equals("")){
            Toast.makeText(context, "제목을 입력해 주세요!", Toast.LENGTH_SHORT).show();
            writePostUploading.dismiss();
        }
        else{
            if(editing){
                db.deletePostForUpdate(msgFromIntent, preTitle, preTagStr, Database.getAuth().getCurrentUser().getUid(), preImageCnt, CATEGORY);
                Uploading(msgFromIntent,db,title,time+" (수정됨)");
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
            db.writeImage(bitmapDrawable, Database.getPostpictureRoot().child("Post"+CATEGORY), CATEGORY + "-" + postnum + "-" + title + "-" + (i + 2), new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    db.readImage(Database.getPostpictureRoot().child("Post"+CATEGORY), CATEGORY + "-" + postnum + "-" + title + "-" + (fi + 2)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            db.writePostByLine(postnum,fi+2l, url, title, time, tags,CATEGORY);
                            Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
                            if(fi == mItems.size()-1) {
                                writePostUploading.dismiss();
                                finish();
                            }
                        }
                    });
                }

                @Override
                public void ifFail(Object task) {

                }
            });
        }
    }

    public void Dialog(View view, int position){
        DialogInterface.OnClickListener ammendimage = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //수정
                community2_upload.community2_pos = position;
                community2_upload.community2_upload_selected_iv = (ImageView) view;
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
                mViewAdapter.notifyDataSetChanged();
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


    public void loadExistingPost(Long postnum){
        Database db = new Database(this);
        ArrayList<Post> posts = new ArrayList<>();
        db.readOnePost(posts, postnum, CATEGORY, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                Post post = posts.get(0);
                //제목
                ((EditText)findViewById(R.id.community2_upload_title)).setText(post.getTitle());
                preTitle = post.getTitle();

                //태그
                preTagStr = post.getTags();
                ArrayList<String> tagStrings = new ArrayList<>(Arrays.asList(post.getTags().split("#")));
                tagStrings.remove(0);
                LinearLayout tagLayout = ((LinearLayout)findViewById(R.id.tagLayout2));
                while(tagStrings.size() > 0){
                    String tmp = tagStrings.get(0).trim();
                    Log.d("WeGlonD", tmp);
                    tags.add(tmp);
                    TextView newTagTv = new TextView(context);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(5,5,5,5);
                    newTagTv.setLayoutParams(layoutParams);
                    newTagTv.setTextSize(15);
                    newTagTv.setText(tmp);
                    newTagTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView now = (TextView) view;
                            //Toast.makeText(getApplicationContext(), now.getText(), Toast.LENGTH_SHORT).show();
                            for(String tmp1 : tags){
                                if(tmp1.equals(now.getText())){
                                    tags.remove(tmp1);
                                    break;
                                }
                            }
                            ((ViewGroup)now.getParent()).removeView(now);
                            Toast.makeText(getApplicationContext(),""+tags.size(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    tagLayout.addView(newTagTv);
                    tagStrings.remove(0);
                }

                //컨텐츠
                ArrayList<String> contents = post.getContents();
                ((EditText)findViewById(R.id.community2_upload_content)).setText(community2_change_content);

                for(int i = 0;i<contents.size();i++){
                    Log.d("minseok",contents.get(i)+"url");
                    Log.d("minseok",Uri.parse(contents.get(i))+"uri");
                    mItems.add(Uri.parse(contents.get(i)));
                    addPageMark();
                }
                mViewAdapter.notifyDataSetChanged();

//                parent = findViewById(R.id.contentsLayout);
//                LinearLayout linear=null;
//                for(int i = 0; i < contents.size(); i++){
//                    switch(i%2){
//                        case 1:
//                            linear = new LinearLayout(context);
//                            linear.setLayoutParams(layparms);
//                            linear.setOrientation(LinearLayout.VERTICAL);
//                            parent.addView(linear);
//
//                            ImageView iv = new ImageView(context);
//                            iv.setLayoutParams(layparms);
//                            iv.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    if(relative.getVisibility()==View.GONE)
//                                        relative.setVisibility(View.VISIBLE);
//                                    else
//                                        relative.setVisibility(View.GONE);
//                                    selectediv = (ImageView)view;
//                                }
//                            });
//                            Glide.with(getApplicationContext()).load(contents.get(i)).centerCrop().override(1000).into(iv);
//                            linear.addView(iv);
//                            imageViews.add(iv);
//                            break;
//                        case 0:
//                            if(i==0){
//                                ((EditText)findViewById(R.id.et_detail)).setText(contents.get(i));
//                            }
//                            else{
//                                EditText et = new EditText(WritePostActivity.this);
//                                et.setLayoutParams(layparms);
//                                et.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
//                                et.setHint("내용");
//                                et.setText(contents.get(i));
//                                linear.addView(et);
//                                editTexts.add(et);
//                            }
//                            break;
//                    }
//                }
                preImageCnt = contents.size();
                Log.d("WeGlonD", "기존 게시물 불러오기 완료");
                //Log.d("WeGlonD", "editTexts : " + editTexts.size() + " imageViews : "+imageViews.size());
            }

            @Override
            public void ifFail(Object task) {

            }
        });
    }
}

