package com.example.upcyclecommunity.community1;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Comment;
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
    EditText et_comment;
    Button btn_comment;
    LinearLayout comment_layout;
    Long postn;

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_personal_post,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Database.getPostRoot().child(getApplicationContext().getString(R.string.DB_posting)).child(postn+"").child("writer").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String writerUid = task.getResult().getValue(String.class);
                //Log.d("WeGlonD", Database.getAuth().getCurrentUser().getUid());
                //Log.d("WeGlonD", writerUid);
                if(writerUid.equals(Database.getAuth().getCurrentUser().getUid().toString())){
                    switch (item.getItemId()){
                        case R.id.menu_deltePost:
                            db.deletePost(postn, writerUid, new Acts() {
                                @Override
                                public void ifSuccess(Object task) {
                                    finish();
                                }

                                @Override
                                public void ifFail(Object task) {
                                    Toast.makeText(getApplicationContext(), "삭제실패", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        case R.id.menu_editPost:
                            //수정 코드
                            break;
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "자신의 게시물만 수정/삭제 가능합니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_post);
        postn = Long.parseLong(getIntent().getStringExtra("postn"));
        postArray = new ArrayList<>();
        contents = new ArrayList<>();
        tags = new ArrayList<>();
        titleview = findViewById(R.id.tv_personal_name);
        tag_detail = findViewById(R.id.tag_detail);
        profile_name = findViewById(R.id.profile_name);
        profile_date = findViewById(R.id.profile_date);
        profile_src = findViewById(R.id.circle_iv);
        parent = findViewById(R.id.personal_contentsLayout);
        et_comment = findViewById(R.id.et_commentInput);
        btn_comment = findViewById(R.id.btn_commentInput);
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.writeComment(postn, et_comment.getText().toString());
            }
        });

        comment_layout = findViewById(R.id.comments_layout);
        ArrayList<Comment> commentDatas = new ArrayList<>();
        db.readComment(commentDatas, postn, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                for(Comment cmt : commentDatas){
                    LinearLayout newCommentContainer = new LinearLayout(getApplicationContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);
                    newCommentContainer.setOrientation(LinearLayout.HORIZONTAL);
                    newCommentContainer.setLayoutParams(layoutParams);


                    Database.getUserRoot().child(cmt.getWriterUid()).child("name").get().addOnCompleteListener(task1 -> {
                        String username;
                        if(task1.isSuccessful()){
                            username = task1.getResult().getValue(String.class);
                            LinearLayout.LayoutParams usernameViewlayoutParams = new LinearLayout.LayoutParams(250, ViewGroup.LayoutParams.MATCH_PARENT);
                            TextView tv_username = new TextView(getApplicationContext());
                            tv_username.setText(username);
                            tv_username.setTextSize(15);
                            tv_username.setLayoutParams(usernameViewlayoutParams);
                            newCommentContainer.addView(tv_username);

                            TextView tv_text = new TextView(getApplicationContext());
                            LinearLayout.LayoutParams textViewlayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            tv_text.setText(cmt.getText());
                            tv_text.setTextSize(10);
                            tv_text.setLayoutParams(textViewlayoutParams);
                            newCommentContainer.addView(tv_text);

                            comment_layout.addView(newCommentContainer);
                        }
                    });

                }
            }

            @Override
            public void ifFail(Object task) {

            }
        });


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
