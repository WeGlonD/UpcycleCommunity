package com.example.upcyclecommunity.community1;

import android.net.Uri;
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
import com.example.upcyclecommunity.database.User;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Personal_Post extends AppCompatActivity {
    Database db = new Database();
    ArrayList<Post> postArray;
    TextView titleview;
    ArrayList<String> contents;
    ArrayList<String> tags;
    LinearLayout parent;
    TextView tag_detail;
    String User_Id;
    TextView profile_name;
    TextView profile_date;
    CircleImageView profile_src;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_post);
        Long postn = Long.parseLong(getIntent().getStringExtra("postn"));
        postArray = new ArrayList<>();
        contents = new ArrayList<>();
        tags = new ArrayList<>();
        titleview = findViewById(R.id.tv_personal_name);
        tag_detail = findViewById(R.id.tag_detail);
        profile_name = findViewById(R.id.profile_name);
        profile_date = findViewById(R.id.profile_date);
        profile_src = findViewById(R.id.circle_iv);
        parent = findViewById(R.id.personal_contentsLayout);
        db.readOnePost(postArray, postn, new Acts() {
            @Override
            public void ifSuccess(Object task) {

                Post personal_p = postArray.get(0);
                String Ttitle = personal_p.getTitle();
                String Ttitle1 = Ttitle;
                Ttitle = Ttitle.substring(20, Ttitle.length());
                titleview.setText(Ttitle);
                tag_detail.setText(personal_p.getTags());
                User_Id = personal_p.getUser_id();
                //user
                Database.getUserRoot().child(User_Id).child("name").get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        String Name = task1.getResult().getValue(String.class);
                        Database.getUserProfileImageRoot().child(User_Id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String Date = Ttitle1.substring(0,10);
                                profile_name.setText(Name);
                                profile_date.setText(Date);
                                String iv_url = uri.toString();
                                Glide.with(getApplicationContext()).load(iv_url).into(profile_src);
                                LinearLayout.LayoutParams layparms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                parent.setLayoutParams(layparms);
                                parent.setOrientation(LinearLayout.VERTICAL);
                                contents = personal_p.getContents();
                                for (int i = 0; i < contents.size(); i++) {
                                    if (i % 2 == 0) {
                                        TextView tv = new TextView(Personal_Post.this);
                                        tv.setLayoutParams(layparms);
                                        tv.setText(contents.get(i));
                                        parent.addView(tv);
                                    } else {
                                        ImageView iv = new ImageView(Personal_Post.this);
                                        iv.setLayoutParams(layparms);
                                        Glide.with(getApplicationContext()).load(contents.get(i)).centerCrop().override(1000).into(iv);
                                        parent.addView(iv);
                                    }
                                }
                            }
                        });
                    } else {

                    }
                });
            }
            @Override
            public void ifFail(Object task) {

            }
        });
    }
}
