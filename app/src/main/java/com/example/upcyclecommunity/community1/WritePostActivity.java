package com.example.upcyclecommunity.community1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.upcyclecommunity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class WritePostActivity extends AppCompatActivity {
    private static final String TAG = "WritePostActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        findViewById(R.id.btn_check).setOnClickListener(onClickListener);
        findViewById(R.id.btn_image).setOnClickListener(onClickListener);
        findViewById(R.id.btn_video).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.btn_check:
                    break;
                case R.id.btn_image:
                    //doActivity(Gallery.class,"image");
                    break;
                case R.id.btn_video:
                    //doActivity(Gallery.class,"video");
                    break;
            }
        }
    };

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
