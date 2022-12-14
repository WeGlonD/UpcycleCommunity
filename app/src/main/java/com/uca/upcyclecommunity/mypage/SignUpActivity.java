package com.uca.upcyclecommunity.mypage;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.uca.upcyclecommunity.R;
import com.uca.upcyclecommunity.database.Acts;
import com.uca.upcyclecommunity.database.Database;
import com.uca.upcyclecommunity.database.User;
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
    private CheckBox contract_agree_cb;
    private Button signUp_btn;
    private TextView contract_tv1;
    private TextView contract_tv2;
    private TextView contract_tv3;

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
        contract_agree_cb = findViewById(R.id.activity_signup_contract_agree_checkBox);
        signUp_btn = findViewById(R.id.activity_signup_signUp_button);
        contract_tv1 = findViewById(R.id.activity_signup_contract_textView1);
        contract_tv2 = findViewById(R.id.activity_signup_contract_textView2);
        contract_tv3 = findViewById(R.id.activity_signup_contract_textView3);

        Database db = new Database();
        User user = new User();
        mContext = getApplicationContext();

        profile_iv.setOnClickListener(view -> {
            Dialog();
        });

        signUp_btn.setOnClickListener(view -> {
            if(!contract_agree_cb.isChecked()){
                Toast.makeText(this, getString(R.string.activity_signup_contract_agree_require), Toast.LENGTH_SHORT).show();
                return;
            }

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
                    dialog.setTitle("회원가입중...");
                    dialog.show();

                    String trim_email = email.replaceAll("\\s", "");
                    String trim_password = password.replaceAll("\\s", "");

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
                                                    Toast.makeText(mContext, "유저 프로필 이미지 저장에 실패 했습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                                @Override
                                public void ifFail(Object task) {
                                    dialog.dismiss();
                                    Toast.makeText(mContext, "로그인에 실패 했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void ifFail(Object task) {
                            dialog.dismiss();
                            Toast.makeText(mContext, "계정 생성에 실패 했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            else{
                Toast.makeText(this, getString(R.string.activity_signup_password_is_not_same), Toast.LENGTH_SHORT).show();
            }
        });

        contract_tv1.setOnClickListener(view -> {
            goToWeb_Contract(1);
        });
        contract_tv2.setOnClickListener(view -> {
            goToWeb_Contract(2);
        });
        contract_tv3.setOnClickListener(view -> {
            goToWeb_Contract(3);
        });
    }

    public void goToWeb_Contract(int n){
        String url = null;
        switch(n){
            case 1:
                url = getString(R.string.activity_signup_contract_url1);
                break;
            case 2:
                url = getString(R.string.activity_signup_contract_url2);
                break;
            case 3:
                url = getString(R.string.activity_signup_contract_url3);
                break;
        }

        Intent it = new Intent(Intent.ACTION_VIEW);
        it.setData(Uri.parse(url));
        startActivity(it);
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

    public void Dialog(){
        DialogInterface.OnClickListener fromAlbum = (dialog, which) -> {
            //수정
            doTakeAlbumAction();
            dialog.dismiss();
        };
        DialogInterface.OnClickListener fromCamera = (dialog, which) -> {
            //삭제
            doTakePhotoAction();
            dialog.dismiss();
        };
        new AlertDialog.Builder(this)
                .setTitle("사진 선택")
                .setPositiveButton("앨범에서 가져오기", fromAlbum)
                .setNegativeButton("사진 촬영", fromCamera)
                .show();
    }
}
