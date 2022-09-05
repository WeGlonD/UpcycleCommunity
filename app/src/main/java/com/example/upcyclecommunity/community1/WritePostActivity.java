package com.example.upcyclecommunity.community1;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Database;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.grpc.Context;

public class WritePostActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "WritePostActivity";
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

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
                uploadPost();
                break;
            case R.id.btn_tagInput:
                EditText et_tag = findViewById(R.id.et_tagInput);
                String newTag = et_tag.getText().toString();
                et_tag.setText("");
                if(!newTag.equals("")){
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
                if(relative.getVisibility() == View.VISIBLE){
                    relative.setVisibility(View.GONE);
                }
                break;
            case R.id.changeimage:
                doTakeAlbumAction(1);
                break;
            case R.id.deleteimage:
                parent.removeView((View)selectediv.getParent());
                break;
        }
    }

    public void uploadPost(){
        Database db = new Database();
        ArrayList<String> contents = new ArrayList<>();
        String title = ((EditText)findViewById(R.id.et_name)).getText().toString();

        for(int i = 0; i < tags.size(); i++){

        }

        for(long i = 1;i <= editTexts.size();i++){
            switch ((int)i%2){
                case 0:
                    StorageReference picRoot = db.getPostpictureRoot();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-");
                    String time = sdf.format(new Timestamp(System.currentTimeMillis()));
                    String picName = time + title + i;
                    final String postTitle = time + title;
                    Long finalI = (Long)i;
                    db.writeImage((BitmapDrawable) imageViews.get((int)((i-1)/2)).getDrawable(), picRoot, picName, new Acts() {
                        @Override
                        public void ifSuccess(Object task) {
                            db.readImage(picRoot,picName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    db.writePostByLine(finalI, url, postTitle, tags);
                                    Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
                                }
                            });

                        }

                        @Override
                        public void ifFail(Object task) {

                        }
                    });
                    break;
                case 1:
                    db.writePostByLine(i, editTexts.get((int)((i-1)/2)).getText().toString(), title, tags);
            }
        }

//        for(int i = 0; i < editTexts.size(); i++){
//            contents.add(editTexts.get(i).getText().toString());
//            if(i < imageViews.size()){
//                StorageReference picRoot = db.getPostpictureRoot();
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-");
//                String time = sdf.format(new Timestamp(System.currentTimeMillis()));
//                String picName = time + title + i;
//                db.writeImage((BitmapDrawable) imageViews.get(i).getDrawable(), picRoot, picName, new Acts() {
//                    @Override
//                    public void ifSuccess(Object task) {
//                        String url = db.readImage(picRoot,picName).getDownloadUrl().toString();
//                        contents.add(url);
//                        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void ifFail(Object task) {
//
//                    }
//                });
//            }
//        }
//        while(true){
//            if(contents.size()==editTexts.size()+imageViews.size())
//                break;
//        }
//        db.writePost(contents, title, tags);
    }

//    private void update(){
//        final String name = ((EditText) findViewById(R.id.et_name)).getText().toString();
//        final String contents = ((EditText) findViewById(R.id.et_detail)).getText().toString();
//        if(name.length()>0 && contents.length()>0){
//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            WriteInfo writeinfo = new WriteInfo(name,contents);
//            //upload
//            uploader(writeinfo);
//        }
//    }
}
