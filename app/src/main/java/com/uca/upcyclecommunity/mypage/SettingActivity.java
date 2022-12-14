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
            dialog.setTitle("?????? ??????");
            dialog.setMessage("?????? ????????? ?????????????????????????\n????????? ??? ?????? ?????? ?????? ??????????????????.");

            dialog.setPositiveButton("???", ((dialogInterface, i) -> {
                ProgressDialog dlg = new ProgressDialog(context);
                dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dlg.setTitle("?????? ????????? ?????????...");
                dlg.show();

                Database.getUserRoot().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                        removeValue().addOnCompleteListener(task -> {
                            dlg.setTitle("????????? ????????? ?????????...");
                           if(task.isSuccessful()) {
                               Database.getUserProfileImageRoot().
                                       child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                       delete().addOnCompleteListener(taskProfile -> {
                                           dlg.setTitle("?????? ?????????...");
                                    if(taskProfile.isSuccessful()){
                                        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(taskUser -> {
                                            if(taskUser.isSuccessful())
                                                printToast("????????? ?????? ???????????????...");
                                            else
                                                printToast("?????? ????????? ?????????????????????");
                                            dlg.dismiss();
                                            finish();
                                        });
                                    }
                                    else{
                                        printToast("????????? ????????? ????????? ?????????????????????");
                                        dlg.dismiss();
                                    }
                               });
                           }
                           else {
                                printToast("?????? ????????? ????????? ?????????????????????");
                                dlg.dismiss();
                           }
                        });
            }));
            dialog.setNegativeButton("??????", ((dialogInterface, i) -> {

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
            dialog.setTitle("?????????...");
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
                                                        printToast("????????? ?????? ??????????????? ?????? ????????????.");
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
                                    printToast("????????? ??????????????? ?????? ????????????.");
                                }
                            });
                        }
                        else{
                            dialog.dismiss();
                            printToast("?????? ??????????????? ?????? ????????????.");
                        }

                    });

        });
    }

    public void printToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    public void doTakePhotoAction() // ????????? ?????? ??? ????????? ????????????
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
            Toast.makeText(this, "?????? ??????", Toast.LENGTH_LONG).show();
        }
        mImageCaptureUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", image);
        grantUriPermission("com.example.upcyclecommunity.fileprovider", mImageCaptureUri, FLAG_GRANT_READ_URI_PERMISSION);
        grantUriPermission("com.example.upcyclecommunity.fileprovider", mImageCaptureUri, FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    /**
     * ???????????? ????????? ????????????
     */
    public void doTakeAlbumAction() // ???????????? ????????? ????????????
    {
        // ?????? ??????
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
                    with() : View, Fragment ?????? Activity????????? Context??? ????????????.
                    load() : ???????????? ????????????. ????????? ???????????? ???????????? ????????? ??? ??????. (Bitmap, Drawable, String, Uri, File, ResourId(Int), ByteArray)
                    into() : ???????????? ????????? View??? ????????????.*/

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
            //??????
            doTakeAlbumAction();
            dialog.dismiss();
        };
        DialogInterface.OnClickListener fromCamera = (dialog, which) -> {
            //??????
            doTakePhotoAction();
            dialog.dismiss();
        };
        new AlertDialog.Builder(this)
                .setTitle("?????? ??????")
                .setPositiveButton("???????????? ????????????", fromAlbum)
                .setNegativeButton("?????? ??????", fromCamera)
                .show();
    }
}
