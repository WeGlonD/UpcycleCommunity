package com.example.upcyclecommunity.community1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.community2.community2Adapter;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Comment;
import com.example.upcyclecommunity.database.Database;
import com.example.upcyclecommunity.database.Post;
import com.example.upcyclecommunity.database.User;
import com.example.upcyclecommunity.mypage.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Personal_Post extends AppCompatActivity {
    public static String CATEGORY = "1";
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
    Long recruitPostnum;
    Context context;
    RecyclerView recruitRecycler;

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
                                db.deletePost(postn, writerUid, CATEGORY, recruitPostnum+"", new Acts() {
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
                                if(CATEGORY.equals("3"))
                                    it.putExtra("recruitPostnum", recruitPostnum+"");
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
        recruitRecycler = findViewById(R.id.recruitPost);
        recruitRecycler.setLayoutManager(new LinearLayoutManager(context));

        if(getIntent().getStringExtra("category").equals("3")) {
            CATEGORY = "3";
            ArrayList<Long> arr = new ArrayList<>();
            Database.getDBRoot().child("Post3").child("posting").child(postn+"").child("recruitFrom").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    recruitPostnum = task.getResult().getValue(Long.class);
                    Database.getDBRoot().child("Post2").child("posting").child(recruitPostnum+"").get().addOnCompleteListener(task1 -> {
                       if (task1.isSuccessful()){
                           if (task1.getResult().getChildrenCount() == 0){
                               TextView tv = new TextView(context);
                               tv.setText("삭제된 게시물입니다.");
                               tv.setTextSize(30);
                               tv.setTextColor(getResources().getColor(R.color.black));
                               LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                               tv.setGravity(Gravity.CENTER);
                               tv.setLayoutParams(lp);
                               ((LinearLayout)findViewById(R.id.recruit_linearlayout)).addView(tv);
                           }
                           else{
                               arr.add(recruitPostnum);
                               community2Adapter adapter = new community2Adapter(arr, context, new community2Adapter.clickListener() {
                                   @Override
                                   public void mclickListener_Dialog(String postNumber) {}
                               });
                               recruitRecycler.setAdapter(adapter);
                           }
                       }
                    });

                }
            });
        }
        else{
            CATEGORY = "1";
        }

        comment_layout = findViewById(R.id.comments_layout);

        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null){
                    Toast.makeText(context, "로그인을 하세요~", Toast.LENGTH_SHORT).show();
                }
                else{
                    ArrayList<String> keylist = new ArrayList<>();
                    db.writeCommentPersonal(postn, et_comment.getText().toString(), CATEGORY, keylist, new Acts() {
                        @Override
                        public void ifSuccess(Object task) {
                            Comment cmt = new Comment(et_comment.getText().toString(),Database.getAuth().getCurrentUser().getUid(),keylist.get(0));
                            CommentView newCommentContainer = new CommentView(context,cmt, Glide.with(getApplicationContext()));
                            newCommentContainer.setTag(R.string.tagKey1, cmt.getKey());
                            newCommentContainer.setTag(R.string.tagKey2, cmt.getWriterUid());

                            newCommentContainer.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View view) {
                                    commentLongClickAction(view);
                                    return true;
                                }
                            });
                            comment_layout.addView(newCommentContainer);
                            et_comment.setText("");
                        }

                        @Override
                        public void ifFail(Object task) {}
                    });
                }
            }
        });


        ArrayList<Comment> commentDatas = new ArrayList<>();
        db.readComment(commentDatas, postn, CATEGORY, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                for(Comment cmt : commentDatas){
                    CommentView newCommentContainer = new CommentView(context,cmt,Glide.with(getApplicationContext()));
                    //tagkey1 : key / tagkey2 : userUid
                    newCommentContainer.setTag(R.string.tagKey1, cmt.getKey());
                    newCommentContainer.setTag(R.string.tagKey2, cmt.getWriterUid());

                    newCommentContainer.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            commentLongClickAction(view);
                            return true;
                        }
                    });
                    comment_layout.addView(newCommentContainer);
                }
            }

            @Override
            public void ifFail(Object task) {

            }
        });


        db.readOnePost(postArray, postn, CATEGORY, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                Log.d("Minseok",CATEGORY+"");
                Post personal_p = postArray.get(0);
                String Ttitle = personal_p.getTitle();
                Toast.makeText(context, String.valueOf(postn), Toast.LENGTH_SHORT).show();

                titleview.setText(Ttitle);
                tag_detail.setText(personal_p.getTags());
                User_Id = personal_p.getUser_id();
                Log.d("Minseok", User_Id);
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
    public void commentLongClickAction(View view){
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
                            ((CommentView)view).text.setText(editStr);
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
                    ((LinearLayout)((CommentView)view).getParent()).removeView(view);
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
    }
}
