package com.example.upcyclecommunity.community1;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.community2.ViewAdapter;
import com.example.upcyclecommunity.community2.community2Adapter;
import com.example.upcyclecommunity.community2.community2Adapter.MyViewHolder;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Database;
import com.example.upcyclecommunity.database.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class WritePostActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "WritePostActivity";
    public static final String CATEGORY = "1";
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CHANGE_ALBUM = 2;
    private Uri mImageCaptureUri;
    private ArrayList<EditText> editTexts;
    private ArrayList<ImageView> imageViews;
    private ArrayList<String> tags;
    private RelativeLayout relative;
    private ImageView selectediv;
    private LinearLayout parent;
    private WritePostUploading writePostUploading;
    private Context context;
    boolean editing;
    boolean recruiting;
    String preTitle;
    String recruitNum;
    String preTagStr;
    int preImageCnt;
    Long msgFromIntent;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        Intent it = getIntent();
        msgFromIntent = Long.parseLong(it.getStringExtra("postn"));

        context = this;

        editing = msgFromIntent != Long.MAX_VALUE;

        editTexts = new ArrayList<>();
        imageViews = new ArrayList<>();
        tags = new ArrayList<>();

        editTexts.add(findViewById(R.id.et_detail));

        relative = findViewById(R.id.relative);
        relative.setOnClickListener(this);

        findViewById(R.id.changeimage).setOnClickListener(this);
        findViewById(R.id.deleteimage).setOnClickListener(this);
        findViewById(R.id.btn_check).setOnClickListener(this);
        findViewById(R.id.btn_image).setOnClickListener(this);
        findViewById(R.id.btn_video).setOnClickListener(this);
        findViewById(R.id.btn_tagInput).setOnClickListener(this);

        writePostUploading = new WritePostUploading(this);
        writePostUploading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (editing){
            loadExistingPost(msgFromIntent);
        }
        recruitNum = it.getStringExtra("recruitPostnum");
        recruiting = false;
        if(recruitNum!=null){
            recruiting = true;
            Log.d("WeGlonD", "recruiting! "+recruitNum);
            recruitPost(recruitNum);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void recruitPost(String recruitNum) {
        //매개변수로 받은 Post2의 postnum으로 db에서 들고와서 뷰 띄우기
        ArrayList<Long> arr = new ArrayList<>();
        arr.add(Long.parseLong(recruitNum));

        community2Adapter adapter = new community2Adapter(arr, context, new community2Adapter.clickListener() {
            @Override
            public void mclickListener_Dialog(String postNumber) {}
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ((RecyclerView)findViewById(R.id.recruitFor)).setLayoutManager(layoutManager);
        ((RecyclerView)findViewById(R.id.recruitFor)).setAdapter(adapter);
    }

    public void loadExistingPost(Long postnum){
        Database db = new Database(this);
        ArrayList<Post> posts = new ArrayList<>();
        String category = CATEGORY;
        if(recruiting) category = "3";
        db.readOnePost(posts, postnum, category, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                Post post = posts.get(0);
                //제목
                ((EditText)findViewById(R.id.et_name)).setText(post.getTitle());
                preTitle = post.getTitle();

                //태그
                preTagStr = post.getTags();
                ArrayList<String> tagStrings = new ArrayList<>(Arrays.asList(post.getTags().split("#")));
                tagStrings.remove(0);
                LinearLayout tagLayout = ((LinearLayout)findViewById(R.id.tagLayout));
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
                ViewGroup.LayoutParams layparms = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                parent = findViewById(R.id.contentsLayout);
                LinearLayout linear=null;
                for(int i = 0; i < contents.size(); i++){
                    switch(i%2){
                        case 1:
                            linear = new LinearLayout(context);
                            linear.setLayoutParams(layparms);
                            linear.setOrientation(LinearLayout.VERTICAL);
                            parent.addView(linear);

                            ImageView iv = new ImageView(context);
                            iv.setLayoutParams(layparms);
                            iv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(relative.getVisibility()==View.GONE)
                                        relative.setVisibility(View.VISIBLE);
                                    else
                                        relative.setVisibility(View.GONE);
                                    selectediv = (ImageView)view;
                                }
                            });
                            Glide.with(getApplicationContext()).load(contents.get(i)).centerCrop().override(1000).into(iv);
                            linear.addView(iv);
                            imageViews.add(iv);
                            break;
                        case 0:
                            if(i==0){
                                ((EditText)findViewById(R.id.et_detail)).setText(contents.get(i));
                            }
                            else{
                                EditText et = new EditText(WritePostActivity.this);
                                et.setLayoutParams(layparms);
                                et.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                                et.setHint("내용");
                                et.setText(contents.get(i));
                                linear.addView(et);
                                editTexts.add(et);
                            }
                            break;
                    }
                }
                preImageCnt = imageViews.size();
                Log.d("WeGlonD", "기존 게시물 불러오기 완료");
                Log.d("WeGlonD", "editTexts : " + editTexts.size() + " imageViews : "+imageViews.size());
            }

            @Override
            public void ifFail(Object task) {

            }
        });
    }

    public void doTakePhotoAction() // 카메라 촬영 후 이미지 가져오기
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File StorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
        } catch (IOException exception) {
            Toast.makeText(this, "파일 실패", Toast.LENGTH_LONG).show();
        }
        mImageCaptureUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", image);
        grantUriPermission("com.example.upcyclecommunity.fileprovider", mImageCaptureUri, FLAG_GRANT_READ_URI_PERMISSION);
        grantUriPermission("com.example.upcyclecommunity.fileprovider", mImageCaptureUri, FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    /**
     * 앨범에서 이미지 가져오기
     */
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

        //
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICK_FROM_ALBUM:
                    mImageCaptureUri = data.getData();

                case PICK_FROM_CAMERA:
                    Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
                    //String profilePath = data.getStringExtra("profilePath");
                    parent = findViewById(R.id.contentsLayout);

                    ViewGroup.LayoutParams layparms = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    LinearLayout linear = new LinearLayout(WritePostActivity.this);
                    linear.setLayoutParams(layparms);
                    linear.setOrientation(LinearLayout.VERTICAL);
                    parent.addView(linear);

                    ImageView iv = new ImageView(WritePostActivity.this);
                    iv.setLayoutParams(layparms);
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(relative.getVisibility()==View.GONE)
                                relative.setVisibility(View.VISIBLE);
                            else
                                relative.setVisibility(View.GONE);
                                selectediv = (ImageView)view;
                        }
                    });


                    /*
                    with() : View, Fragment 혹은 Activity로부터 Context를 가져온다.
                    load() : 이미지를 로드한다. 다양한 방법으로 이미지를 불러올 수 있다. (Bitmap, Drawable, String, Uri, File, ResourId(Int), ByteArray)
                    into() : 이미지를 보여줄 View를 지정한다.*/

                    Glide.with(this).load(mImageCaptureUri).centerCrop().override(1000).into(iv);
                    linear.addView(iv);
                    imageViews.add(iv);
                    EditText et = new EditText(WritePostActivity.this);
                    et.setLayoutParams(layparms);
                    et.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    et.setHint("내용");
                    linear.addView(et);
                    editTexts.add(et);
                    break;
                case CHANGE_ALBUM:
                    mImageCaptureUri = data.getData();
                    Glide.with(this).load(mImageCaptureUri).centerCrop().override(1000).into(selectediv);
                    break;
            }
        }
    }



    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btn_check:
                writePostUploading.show();
                uploadPost();
                break;
            case R.id.btn_tagInput:
                EditText et_tag = findViewById(R.id.et_tagInput);
                String newTag = et_tag.getText().toString();
                et_tag.setText("");
                if(!newTag.equals("") && !tags.contains(newTag)){
                    LinearLayout tags_field = findViewById(R.id.tagLayout);
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
                break;
            case R.id.btn_image:
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakePhotoAction();
                    }
                };
                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakeAlbumAction(0);
                    }
                };
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };
                new AlertDialog.Builder(this)
                        .setTitle("업로드할 이미지 선택")
                        .setPositiveButton("사진촬영", cameraListener)
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
                break;
            case R.id.btn_video:
                //doTakeVideoAction();
                break;
            case R.id.relative:
                if(relative.getVisibility() == View.GONE){
                    relative.setVisibility(View.VISIBLE);
                }
                else{

                    relative.setVisibility(View.GONE);
                }
                break;
            case R.id.changeimage:
                doTakeAlbumAction(1);
                relative.setVisibility(View.GONE);
                break;
            case R.id.deleteimage:
                parent.removeView((View)selectediv.getParent());
                relative.setVisibility(View.GONE);
                break;
        }
    }

    public void uploadPost(){
        Database db = new Database();
        ArrayList<String> contents = new ArrayList<>();
        String title = ((EditText)findViewById(R.id.et_name)).getText().toString();
        String category = CATEGORY;
        if(recruiting) category = "3";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String time = sdf.format(new Timestamp(System.currentTimeMillis()));

        Log.d("WeGlonD", "postnum " + msgFromIntent);

        if(editing){
            db.deletePostForUpdate(msgFromIntent, preTitle, preTagStr, Database.getAuth().getCurrentUser().getUid(), preImageCnt, category);
            Uploading(msgFromIntent,db,title,time+" (수정됨)", category);
        }
        else {
            String finalCategory = category;
            db.setNewPostNumber(category, new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    Long postnum = ((Task<DataSnapshot>) task).getResult().getValue(Long.class);
                    Uploading(postnum, db, title, time, finalCategory);
                }

                @Override
                public void ifFail(Object task) {
                }
            });
        }

    }

    public void Uploading(Long postnum, Database db, String title, String time, String category){
        Log.d("WeGlonD", "editTexts : " + editTexts.size() + " imageViews : "+imageViews.size());
        if(category.equals("3")){
            Database.getDBRoot().child("Post3").child("posting").child(postnum+"").child("recruitFrom").setValue(Long.parseLong(recruitNum));
            Database.getDBRoot().child("Post2").child("posting").child(recruitNum).child("recruit").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Long newKey = task.getResult().getValue(Long.class);
                    if(newKey==null)
                        newKey = 0l;
                    newKey++;
                    Database.getDBRoot().child("Post2").child("posting").child(recruitNum).child("recruit").child("cnt").setValue(newKey);
                    Database.getDBRoot().child("Post2").child("posting").child(recruitNum).child("recruit").child(newKey+"").setValue(postnum);
                }
            });
        }
        for(long i = 1;i <= editTexts.size()+imageViews.size()-1;i++){
            switch ((int)i%2){
                case 0:
                    Log.d("WeGlonD", "Uploading for - " + i);
                    StorageReference picRoot = Database.getPostpictureRoot().child("Post"+category);
                    Long finalI = (Long)i;
                    db.writeImage((BitmapDrawable) imageViews.get((int)((i-1)/2)).getDrawable(), picRoot,category+ "-" + postnum+ "-" + title+ "-" + i, new Acts() {
                        @Override
                        public void ifSuccess(Object task1) {
                            Log.d("WeGlonD", "after writeImage " + finalI);
                            db.readImage(picRoot,category+ "-" + postnum+ "-" + title+ "-" + finalI).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    db.writePostByLine(postnum,finalI, url, title, time, tags,category);
                                    Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
                                    if((int)((finalI-1)/2) == imageViews.size()-1){
                                        db.writePostByLine(postnum, finalI+1, editTexts.get((int)(finalI/2)).getText().toString(), title, time, tags,category);
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
                    break;
                case 1:
                    db.writePostByLine(postnum,i, editTexts.get((int)((i-1)/2)).getText().toString(), title, time, tags,category);
                    Log.d("WeGlonD", "editText 작성" + i);
                    break;
            }
        }
        if(editTexts.size()==1){
            db.writePostByLine(postnum,1l, editTexts.get(0).getText().toString(), title, time, tags,category);
            Log.d("WeGlonD", "하나뿐인 editText 작성 1");
            writePostUploading.dismiss();
            finish();
        }
    }
}
