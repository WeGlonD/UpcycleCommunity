package com.example.upcyclecommunity.mypage;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity {

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private Uri mImageCaptureUri;

    private FirebaseAuth mAuth = null;

    private ImageView profile_iv;
    private EditText name_et;
    private EditText email_et;
    private EditText password_et;
    private EditText password_check_et;
    private Button signUp_btn;

    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        profile_iv = findViewById(R.id.activity_signup_profile_imageView);
        name_et = findViewById(R.id.activity_signup_name_editText);
        email_et = findViewById(R.id.activity_signup_email_editText);
        password_et = findViewById(R.id.activity_signup_password_editText);
        password_check_et = findViewById(R.id.activity_signup_password_check_editText);
        signUp_btn = findViewById(R.id.activity_signup_signUp_button);

        Database db = new Database();
        User user = new User();
        mContext = getApplicationContext();

        profile_iv.setOnClickListener(view -> {
            doTakeAlbumAction();
        });

        signUp_btn.setOnClickListener(view -> {
            Bitmap bitmap = makeBitmap(profile_iv.getDrawable());
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
            String name = name_et.getText().toString();
            String email = email_et.getText().toString();
            String password = password_et.getText().toString();
            String password_check = password_check_et.getText().toString();


            if(password.equals(password_check)){
                if (email.length() == 0){
                    Toast.makeText(mContext, "이메일을 입력해 주세요", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() == 0){
                    Toast.makeText(mContext, "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(password.replaceAll("\\s", ""))){
                    Toast.makeText(mContext, "비밀번호에 공백을 지워주세요", Toast.LENGTH_SHORT).show();
                }
                else if(!email.equals(email.replaceAll("\\s", ""))){
                    Toast.makeText(mContext, "이메일에 공백을 지워주세요", Toast.LENGTH_SHORT).show();
                }
                else{
                    ProgressDialog dialog = new ProgressDialog(this);
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setTitle("signUp...");
                    dialog.show();

                    String trim_email = email.replaceAll("\\s", "");
                    String trim_password = password.replaceAll("\\s", "");
                    Log.d("why?", email);
                    Log.d("why?", password);

                    user.create(email, password, new Acts() {
                        @Override
                        public void ifSuccess(Object task) {
                            user.login(trim_email, trim_password, new Acts() {
                                @Override
                                public void ifSuccess(Object task) {
                                    db.writeImage(bitmapDrawable, Database.getUserProfileImageRoot(),
                                            Database.getAuth().getCurrentUser().getUid(), new Acts() {
                                                @Override
                                                public void ifSuccess(Object task) {
                                                    User.Data data = new User.Data(name);
                                                    db.writeUser(data);
                                                    dialog.dismiss();
                                                    finish();
                                                }

                                                @Override
                                                public void ifFail(Object task) {
                                                    dialog.dismiss();
                                                    Toast.makeText(mContext, "save user profile image fail", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                                @Override
                                public void ifFail(Object task) {
                                    dialog.dismiss();
                                    Toast.makeText(mContext, "user login fail", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void ifFail(Object task) {
                            dialog.dismiss();
                            Toast.makeText(mContext, "user create fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            else{
                Toast.makeText(this, getString(R.string.activity_signup_password_is_not_same), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Bitmap makeBitmap(Drawable drawable){
        try {
            Bitmap bitmap;

            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            // Handle the error
            return null;
        }
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
