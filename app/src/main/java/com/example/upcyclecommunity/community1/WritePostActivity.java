package com.example.upcyclecommunity.community1;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.upcyclecommunity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WritePostActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "WritePostActivity";
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private Uri mImageCaptureUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        findViewById(R.id.btn_check).setOnClickListener(this);
        findViewById(R.id.btn_image).setOnClickListener(this);
        findViewById(R.id.btn_video).setOnClickListener(this);
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
    public void doTakeAlbumAction() // 앨범에서 이미지 가져오기
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        //!!!!!startActivityForResult(intent, PICK_FROM_ALBUM);
        //
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case 0:
                if(resultCode == Activity.RESULT_OK){
                    Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
                    //String profilePath = data.getStringExtra("profilePath");

                    LinearLayout parent = findViewById(R.id.contentsLayout);

                    ViewGroup.LayoutParams layparms = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ImageView iv = new ImageView(WritePostActivity.this);
                    iv.setLayoutParams(layparms);
                    /*
                    with() : View, Fragment 혹은 Activity로부터 Context를 가져온다.
                    load() : 이미지를 로드한다. 다양한 방법으로 이미지를 불러올 수 있다. (Bitmap, Drawable, String, Uri, File, ResourId(Int), ByteArray)
                    into() : 이미지를 보여줄 View를 지정한다.*/

                    Glide.with(this).load(mImageCaptureUri).centerCrop().override(1000).into(iv);
                    parent.addView(iv);
                    EditText et = new EditText(WritePostActivity.this);
                    et.setLayoutParams(layparms);
                    et.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    parent.addView(et);
                }
        }
        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
                mImageCaptureUri = data.getData();
                Log.d("SmartWheel", mImageCaptureUri.getPath().toString());
                //Toast.makeText(this,"wqewqeqe",Toast.LENGTH_LONG).show();
            }
            case PICK_FROM_CAMERA: {
                //iv_Post.setImageURI(mImageCaptureUri);
                //iv.setImageURI(mImageCaptureUri);
                break;
            }
        }
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btn_check:
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
                        doTakeAlbumAction();
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
                //doActivity(Gallery.class,"video");
                break;
        }
    }

    private void update(){
        final String name = ((EditText) findViewById(R.id.et_name)).getText().toString();
        final String contents = ((EditText) findViewById(R.id.et_detail)).getText().toString();
        if(name.length()>0 && contents.length()>0){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            WriteInfo writeinfo = new WriteInfo(name,contents);
            //upload
            uploader(writeinfo);
        }
    }

    private void uploader(WriteInfo writeinfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

    }

    private void startToast(String msg){Toast.makeText(this,msg, Toast.LENGTH_LONG).show();}

    private void doActivity(Class c,String media){
        Intent intent = new Intent(this,c);
        intent.putExtra("media",media);
        startActivityForResult(intent, 0);
    }
}
