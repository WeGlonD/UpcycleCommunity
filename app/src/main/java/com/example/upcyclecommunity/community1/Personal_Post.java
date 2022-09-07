package com.example.upcyclecommunity.community1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.upcyclecommunity.mypage.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Personal_Post extends AppCompatActivity {
    public static final String CATEGORY = "1";
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
    Context context;

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_personal_post,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Database.getDBRoot().child("Post"+CATEGORY).child("posting").child(postn+"").child("writer").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String writerUid = task.getResult().getValue(String.class);
                if(Database.getAuth().getCurrentUser()==null){
                    Toast.makeText(getApplicationContext(), "로그인 후 게시물을 수정/삭제할 수 있습니다!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }else {
                    Log.d("WeGlonD", "get : " + Database.getAuth().getCurrentUser().getUid());
                    Log.d("WeGlonD", "post writer : " + writerUid);
                    if (writerUid.equals(Database.getAuth().getCurrentUser().getUid())) {
                        switch (item.getItemId()) {
                            case R.id.menu_deltePost:
                                db.deletePost(postn, writerUid, CATEGORY, new Acts() {
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
                                Intent it = new Intent(this, WritePostActivity.class);
                                it.putExtra("postn", postn + "");
                                startActivity(it);
                                finish();
                                break;
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "자신의 게시물만 수정/삭제 가능합니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_post);

        context = this;

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
                db.writeComment(postn, et_comment.getText().toString(), CATEGORY);
                et_comment.setText("");
            }
        });

        comment_layout = findViewById(R.id.comments_layout);
        ArrayList<Comment> commentDatas = new ArrayList<>();
        db.readComment(commentDatas, postn, CATEGORY, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                for(Comment cmt : commentDatas){
                    LinearLayout newCommentContainer = new LinearLayout(getApplicationContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);
                    newCommentContainer.setOrientation(LinearLayout.HORIZONTAL);
                    newCommentContainer.setLayoutParams(layoutParams);
                    newCommentContainer.setTag(R.string.tagKey1, cmt.getKey());
                    newCommentContainer.setTag(R.string.tagKey2, cmt.getWriterUid());
                    newCommentContainer.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            DialogInterface.OnClickListener editListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseUser currUser = Database.getAuth().getCurrentUser();
                                    if(currUser==null){
                                        Toast.makeText(context, "로그인 후 댓글수정이 가능합니다.", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                    else if(!currUser.getUid().equals((String)view.getTag(R.string.tagKey2))){
                                        Toast.makeText(context, "자신의 댓글만 수정 할 수 있습니다.", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                    else{
                                        dialog.dismiss();
                                        EditText et_editComment = new EditText(context);
                                        DialogInterface.OnClickListener editCommentListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String editStr = et_editComment.getText().toString();
                                                db.editComment(postn,(String)view.getTag(R.string.tagKey1),editStr,CATEGORY);
                                                dialogInterface.dismiss();
                                            }
                                        };
                                        DialogInterface.OnClickListener editCancelListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        };
                                        new AlertDialog.Builder(context)
                                                .setTitle("댓글을 다시 작성해주세요.")
                                                .setView(et_editComment)
                                                .setPositiveButton("수정", editCommentListener)
                                                .setNeutralButton("취소", editCancelListener)
                                                .show();
                                    }
                                }
                            };
                            DialogInterface.OnClickListener deleteListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseUser currUser = Database.getAuth().getCurrentUser();
                                    if(currUser==null){
                                        Toast.makeText(context, "로그인 후 댓글삭제가 가능합니다.", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                    else if(!currUser.getUid().equals((String)view.getTag(R.string.tagKey2))){
                                        Toast.makeText(context, "자신의 댓글만 삭제 할 수 있습니다.", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                    else{
                                        db.deleteComment(postn, (String)view.getTag(R.string.tagKey1), CATEGORY);
                                    }
                                }
                            };
                            DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            };
                            new AlertDialog.Builder(context)
                                    .setTitle("댓글 작업 선택")
                                    .setPositiveButton("댓글 삭제", deleteListener)
                                    .setNeutralButton("취소", cancelListener)
                                    .setNegativeButton("댓글 수정", editListener)
                                    .show();
                            return true;
                        }
                    });


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


        db.readOnePost(postArray, postn, CATEGORY, new Acts() {
            @Override
            public void ifSuccess(Object task) {

                Post personal_p = postArray.get(0);
                String Ttitle = personal_p.getTitle();

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

                                profile_name.setText(Name);
                                profile_date.setText(personal_p.getTimeStamp());
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
