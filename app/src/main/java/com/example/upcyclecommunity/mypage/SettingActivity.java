package com.example.upcyclecommunity.mypage;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.community1.WritePostActivity;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Database;
import com.example.upcyclecommunity.database.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingActivity extends AppCompatActivity {

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private Uri mImageCaptureUri;

    private FirebaseAuth mAuth = null;

    private ImageView profile_iv;
    private EditText name_et;
    private EditText email_et;
    private EditText password_et;
    private EditText password_check_et;
    private Button update_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();

        profile_iv = findViewById(R.id.activity_setting_profile_imageView);
        name_et = findViewById(R.id.activity_setting_name_editText);
        email_et = findViewById(R.id.activity_setting_email_editText);
//        password_et = findViewById(R.id.activity_setting_password_editText);
//        password_check_et = findViewById(R.id.activity_setting_password_check_editText);
        update_btn = findViewById(R.id.activity_setting_update_button);

        Uri it_uri = Uri.parse(getIntent().getStringExtra("picUri"));
        String it_name = getIntent().getStringExtra("name");
        String it_email = getIntent().getStringExtra("email");
        Glide.with(this).load(it_uri).into(profile_iv);
        name_et.setText(it_name);
        email_et.setText(it_email);


        Database db = new Database();

        profile_iv.setOnClickListener(view -> {
            doTakeAlbumAction();
        });

        update_btn.setOnClickListener(view -> {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) profile_iv.getDrawable();
            String name = name_et.getText().toString();
            String email = email_et.getText().toString();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            db.writeImage(bitmapDrawable, Database.getUserProfileImageRoot(),
                    Database.getAuth().getCurrentUser().getUid(), new Acts() {
                        @Override
                        public void ifSuccess(Object task) {
                            Database.getUserRoot().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                    child("name").setValue(name).addOnCompleteListener(taskName -> {
                                        user.updateEmail(email).addOnCompleteListener(taskEmail -> {
                                            if(taskEmail.isSuccessful())
                                                finish();
                                            else
                                                printToast("email update fail");
                                        });
                            });
                        }

                        @Override
                        public void ifFail(Object task) {
                            printToast("image update fail");
                        }
                    });
        });
    }

    public void printToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
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
        startActivityForResult(intent, PICK_FROM_ALBUM);
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

                    /*
                    with() : View, Fragment 혹은 Activity로부터 Context를 가져온다.
                    load() : 이미지를 로드한다. 다양한 방법으로 이미지를 불러올 수 있다. (Bitmap, Drawable, String, Uri, File, ResourId(Int), ByteArray)
                    into() : 이미지를 보여줄 View를 지정한다.*/

                    Glide.with(this).load(mImageCaptureUri).centerCrop().override(100).into(profile_iv);
                    break;
            }
        }
    }
}
