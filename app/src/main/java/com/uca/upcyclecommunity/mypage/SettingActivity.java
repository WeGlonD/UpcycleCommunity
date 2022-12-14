package com.uca.upcyclecommunity.mypage;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.uca.upcyclecommunity.R;
import com.uca.upcyclecommunity.database.Acts;
import com.uca.upcyclecommunity.database.Database;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;

public class SettingActivity extends AppCompatActivity {

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private Uri mImageCaptureUri;

    private FirebaseAuth mAuth = null;

    private Button delete_btn;
    private ImageView profile_iv;
    private EditText name_et;
    private EditText email_et;
    private EditText password_et;
    private EditText password_check_et;
    private Button update_btn;

    private boolean isImageChanged = false;

    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        context = this;

        delete_btn = findViewById(R.id.activity_setting_account_delete_button);
        profile_iv = findViewById(R.id.activity_setting_profile_imageView);
        name_et = findViewById(R.id.activity_setting_name_editText);
        email_et = findViewById(R.id.activity_setting_email_editText);
//        password_et = findViewById(R.id.activity_setting_password_editText);
//        password_check_et = findViewById(R.id.activity_setting_password_check_editText);
        update_btn = findViewById(R.id.activity_setting_update_button);

        Uri it_uri = Uri.parse(getIntent().getStringExtra("picUri"));
        String it_name = getIntent().getStringExtra("name").replaceAll("\\s", "");
        String it_email = getIntent().getStringExtra("email").replaceAll("\\s", "");
        Glide.with(this).load(it_uri).into(profile_iv);
        name_et.setText(it_name);
        email_et.setText(it_email);


        Database db = new Database();

        delete_btn.setOnClickListener(view -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("계정 삭제");
            dialog.setMessage("정말 계정을 삭제하시겠습니까?\n게시물 및 댓글 등은 삭제 되지않습니다.");

            dialog.setPositiveButton("예", ((dialogInterface, i) -> {
                ProgressDialog dlg = new ProgressDialog(context);
                dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dlg.setTitle("계정 데이터 삭제중...");
                dlg.show();

                Database.getUserRoot().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                        removeValue().addOnCompleteListener(task -> {
                            dlg.setTitle("프로필 이미지 삭게중...");
                           if(task.isSuccessful()) {
                               Database.getUserProfileImageRoot().
                                       child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                       delete().addOnCompleteListener(taskProfile -> {
                                           dlg.setTitle("계정 삭제중...");
                                    if(taskProfile.isSuccessful()){
                                        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(taskUser -> {
                                            if(taskUser.isSuccessful())
                                                printToast("삭제가 완료 되었습니다...");
                                            else
                                                printToast("계정 삭제에 실패하였습니다");
                                            dlg.dismiss();
                                            finish();
                                        });
                                    }
                                    else{
                                        printToast("프로필 이미지 삭제에 실패하였습니다");
                                        dlg.dismiss();
                                    }
                               });
                           }
                           else {
                                printToast("계정 데이터 삭제에 실패하였습니다");
                                dlg.dismiss();
                           }
                        });
            }));
            dialog.setNegativeButton("취소", ((dialogInterface, i) -> {

            }));
            dialog.show();
        });

        profile_iv.setOnClickListener(view -> {
            Dialog();
        });

        update_btn.setOnClickListener(view -> {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) profile_iv.getDrawable();
            String name = name_et.getText().toString().replaceAll("\\s", "");
            String email = email_et.getText().toString().replaceAll("\\s", "");

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            ProgressDialog dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setTitle("로딩중...");
            dialog.show();

            Database.getUserRoot().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                    child("name").setValue(name).addOnCompleteListener(taskName -> {
                        if (taskName.isSuccessful()){
                            user.updateEmail(email).addOnCompleteListener(taskEmail -> {

                                if(taskEmail.isSuccessful()){
                                    if (isImageChanged) {
                                        db.writeImage(bitmapDrawable, Database.getUserProfileImageRoot(),
                                                Database.getAuth().getCurrentUser().getUid(), new Acts() {
                                                    @Override
                                                    public void ifSuccess(Object task) {
                                                        dialog.dismiss();
                                                        finish();
                                                    }

                                                    @Override
                                                    public void ifFail(Object task) {
                                                        dialog.dismiss();
                                                        printToast("프로필 사진 업데이트에 실패 했습니다.");
                                                    }
                                                });
                                    }
                                    else{
                                        dialog.dismiss();
                                        finish();
                                    }
                                }
                                else{
                                    dialog.dismiss();
                                    printToast("이메일 업데이트에 실패 했습니다.");
                                }
                            });
                        }
                        else{
                            dialog.dismiss();
                            printToast("이름 업데이트에 실패 했습니다.");
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
        String imageFileName = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

                    if (mImageCaptureUri != null){
                        Glide.with(this).load(mImageCaptureUri).centerCrop().override(100).into(profile_iv);
                        isImageChanged = true;
                    }

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
