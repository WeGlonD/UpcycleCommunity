package com.example.upcyclecommunity.community1;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Database;
import com.example.upcyclecommunity.database.Post;

import java.util.ArrayList;

public class Personal_Post extends AppCompatActivity {
    Database db = new Database();
    ArrayList<Post> postArray;
    TextView titleview;
    ArrayList<String> contents;
    ArrayList<String> tags;
    LinearLayout parent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_post);
        Long postn = Long.parseLong(getIntent().getStringExtra("postn"));
        postArray = new ArrayList<>();
        contents = new ArrayList<>();
        tags = new ArrayList<>();
        titleview = findViewById(R.id.tv_personal_name);
        parent = findViewById(R.id.personal_contentsLayout);
        db.readOnePost(postArray, postn, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                Post personal_p = postArray.get(0);
                titleview.setText(personal_p.getTitle());
                LinearLayout.LayoutParams layparms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                parent.setLayoutParams(layparms);
                parent.setOrientation(LinearLayout.VERTICAL);
                contents = personal_p.getContents();
                for(int i = 0;i<contents.size();i++){
                    if(i%2==0){
                        TextView tv = new TextView(Personal_Post.this);
                        tv.setLayoutParams(layparms);
                        tv.setText(contents.get(i));
                        parent.addView(tv);
                    }
                    else{
                        ImageView iv = new ImageView(Personal_Post.this);
                        iv.setLayoutParams(layparms);
                        Glide.with(getApplicationContext()).load(contents.get(i)).centerCrop().override(1000).into(iv);
                        parent.addView(iv);
                    }
                }
            }
            @Override
            public void ifFail(Object task) {

            }
        });
    }
}
