package com.uca.upcyclecommunity.community1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uca.upcyclecommunity.R;
import com.uca.upcyclecommunity.ReportReason;
import com.uca.upcyclecommunity.community2.community2Adapter;
import com.uca.upcyclecommunity.database.Acts;
import com.uca.upcyclecommunity.database.Comment;
import com.uca.upcyclecommunity.database.Database;
import com.uca.upcyclecommunity.database.Post;
import com.uca.upcyclecommunity.mypage.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Personal_Post extends AppCompatActivity {
    public static String CATEGORY = "1";
    public static Personal_Post CurInst;
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
    WritePostDeleting writePostDeleting;
    ImageView Report;
    MenuItem menu_edit;
    MenuItem menu_delete;
    MenuItem menu_reportpost;
    MenuItem menu_reportuser;

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_personal_post, menu);
        menu_edit = menu.findItem(R.id.menu_editPost);
        menu_delete = menu.findItem(R.id.menu_deltePost);
        menu_reportpost = menu.findItem(R.id.menu_reportPost);
        menu_reportuser = menu.findItem(R.id.menu_reportUser);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Database.getDBRoot().child("Post" + CATEGORY).child("posting").child(postn + "").child("writer").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String writerUid = task.getResult().getValue(String.class);
                if (Database.getAuth().getCurrentUser() == null) {
                    Toast.makeText(getApplicationContext(), "????????? ??? ???????????? ??????/??????, ????????? ??? ????????????!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    String nowUser = Database.getAuth().getCurrentUser().getUid();
                    Log.d("WeGlonD", "get : " + nowUser);
                    Log.d("WeGlonD", "post writer : " + writerUid);
                    switch (item.getItemId()) {
                        case R.id.menu_deltePost:
                            writePostDeleting.show();
                            db.deletePost(postn, writerUid, CATEGORY, recruitPostnum + "", new Acts() {
                                @Override
                                public void ifSuccess(Object task) {
                                    writePostDeleting.dismiss();
                                    finish();
                                }

                                @Override
                                public void ifFail(Object task) {
                                    Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        case R.id.menu_editPost:
                            //?????? ??????
                            Intent it = new Intent(this, WritePostActivity.class);
                            it.putExtra("postn", postn + "");
                            if (CATEGORY.equals("3"))
                                it.putExtra("recruitPostnum", recruitPostnum + "");
                            startActivity(it);
                            finish();
                            break;
                        case R.id.menu_reportPost:
                            Toast.makeText(this, "Report Post clicked!", Toast.LENGTH_SHORT).show();
                            Intent it1 = new Intent(this, ReportReason.class);
                            it1.putExtra("type", "POST");
                            it1.putExtra("reportpost", postn + "");
                            it1.putExtra("NowUser", nowUser);
                            it1.putExtra("category", CATEGORY);
                            startActivity(it1);
                            break;
                        case R.id.menu_reportUser:
                            Toast.makeText(this, "Report clicked!", Toast.LENGTH_SHORT).show();
                            Intent it2 = new Intent(this, ReportReason.class);
                            it2.putExtra("type", "USER");
                            it2.putExtra("reportuser", postArray.get(0).getUser_id());
                            it2.putExtra("NowUser", nowUser);
                            it2.putExtra("category", CATEGORY);
                            startActivity(it2);
                            break;
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
        CurInst = this;

        context = this;

        postn = Long.parseLong(getIntent().getStringExtra("postn"));
        postArray = new ArrayList<>();
        contents = new ArrayList<>();
        tags = new ArrayList<>();

        writePostDeleting = new WritePostDeleting(context);
        writePostDeleting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        writePostDeleting.setCancelable(false);

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
        Report = findViewById(R.id.personal_post_report_imageView);
        if (getIntent().getStringExtra("category").equals("3")) {
            CATEGORY = "3";
            ArrayList<Long> arr = new ArrayList<>();
            Database.getDBRoot().child("Post3").child("posting").child(postn + "").child("recruitFrom").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    recruitPostnum = task.getResult().getValue(Long.class);
                    Database.getDBRoot().child("Post2").child("posting").child(recruitPostnum + "").get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            if (task1.getResult().getChildrenCount() == 0) {
                                TextView tv = new TextView(context);
                                tv.setText("????????? ??????????????????.");
                                tv.setTextSize(30);
                                tv.setTextColor(getResources().getColor(R.color.black));
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                tv.setGravity(Gravity.CENTER);
                                tv.setLayoutParams(lp);
                                ((LinearLayout) findViewById(R.id.recruit_linearlayout)).addView(tv);
                            } else {
                                arr.add(recruitPostnum);
                                community2Adapter adapter = new community2Adapter(arr, context, new community2Adapter.clickListener() {
                                    @Override
                                    public void mclickListener_Dialog(String postNumber) {
                                    }
                                });
                                recruitRecycler.setAdapter(adapter);
                            }
                        }
                    });

                }
            });
        } else {
            CATEGORY = "1";
        }

        comment_layout = findViewById(R.id.comments_layout);


        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(context, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<String> keylist = new ArrayList<>();
                    db.writeCommentPersonal(postn, et_comment.getText().toString(), CATEGORY, keylist, new Acts() {
                        @Override
                        public void ifSuccess(Object task) {
                            Comment cmt = new Comment(et_comment.getText().toString(), Database.getAuth().getCurrentUser().getUid(), keylist.get(0));
                            CommentView newCommentContainer = new CommentView(context, cmt, Glide.with(getApplicationContext()));
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
                        public void ifFail(Object task) {
                        }
                    });
                }
            }
        });


        ArrayList<Comment> commentDatas = new ArrayList<>();
        db.readComment(commentDatas, postn, CATEGORY, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                for (Comment cmt : commentDatas) {
                    CommentView newCommentContainer = new CommentView(context, cmt, Glide.with(getApplicationContext()));
                    //tagkey1 : key / tagkey2 : userUid
                    newCommentContainer.setTag(R.string.tagKey1, cmt.getKey());
                    newCommentContainer.setTag(R.string.tagKey2, cmt.getWriterUid());
                    newCommentContainer.setOnClickListener(viw ->{
                        commentClickAction(newCommentContainer);
                    });
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
                Log.d("Minseok", CATEGORY + "");
                Post personal_p = postArray.get(0);
                String Ttitle = personal_p.getTitle();
                //Toast.makeText(context, String.valueOf(postn), Toast.LENGTH_SHORT).show();
                /*if (Database.getAuth().getCurrentUser() != null)
                    findViewById(R.id.personal_post_report_imageView).setVisibility(View.VISIBLE);
                else
                    findViewById(R.id.personal_post_report_imageView).setVisibility(View.GONE);*/

                titleview.setText(Ttitle);
                tag_detail.setText(personal_p.getTags());
                User_Id = personal_p.getUser_id();

                menu_delete.setVisible(false);
                menu_edit.setVisible(false);
                menu_reportpost.setVisible(true);
                menu_reportuser.setVisible(true);

                if(Database.getAuth().getCurrentUser()!=null && Database.getAuth().getCurrentUser().getUid().equals(User_Id)){
                    menu_delete.setVisible(true);
                    menu_edit.setVisible(true);
                    menu_reportpost.setVisible(false);
                    menu_reportuser.setVisible(false);
                }

                Log.d("Minseok", User_Id);
                //user
                Database.getUserRoot().child(User_Id).child("name").get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        try {
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
                                            tv.setTextSize(22);
                                            tv.setLayoutParams(layparms);
                                            tv.setText(contents.get(i).trim() + "\n");
                                            Log.d("albumResult", contents.get(i).trim() + "");
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
                        } catch (Exception e) {
                            Toast.makeText(context, "??????????????? ???????????? ????????????!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {

                    }
                });
            }

            @Override
            public void ifFail(Object task) {

            }
        });
    }

    public void commentClickAction(View view) {
        DialogInterface.OnClickListener reportuserListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser currUser = Database.getAuth().getCurrentUser();
                if (currUser == null) {
                    Toast.makeText(context, "????????? ??? ??????????????? ???????????????.", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else if (currUser.getUid().equals((String) view.getTag(R.string.tagKey2))) {
                    Toast.makeText(context, "????????? ????????? ????????? ??? ????????????.", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    String nowUser1 = Database.getAuth().getCurrentUser().getUid();
                    Intent it = new Intent(context,ReportReason.class);
                    it.putExtra("type","USER");
                    it.putExtra("reportuser",(String) view.getTag(R.string.tagKey2));
                    it.putExtra("category",CATEGORY);
                    it.putExtra("NowUser",nowUser1);
                    startActivity(it);
                }
            }
        };
        DialogInterface.OnClickListener reportpostListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser currUser = Database.getAuth().getCurrentUser();
                if (currUser == null) {
                    Toast.makeText(context, "????????? ??? ????????? ????????? ???????????????.", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else if (currUser.getUid().equals((String) view.getTag(R.string.tagKey2))) {
                    Toast.makeText(context, "????????? ???????????? ????????? ??? ????????????.", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    Intent it = new Intent(context,ReportReason.class);
                    it.putExtra("type","COMMENT");
                    it.putExtra("reportpost",postn+"");
                    it.putExtra("reportCommentNum",(String) view.getTag(R.string.tagKey1));
                    it.putExtra("category",CATEGORY);
                    String nowUser1 = Database.getAuth().getCurrentUser().getUid();
                    it.putExtra("NowUser",nowUser1);
                    startActivity(it);
                }
            }
        };
        new AlertDialog.Builder(context)
                .setTitle("??????")
                .setPositiveButton("?????? ??????", reportuserListener)
                .setNeutralButton("?????? ??????", reportpostListener)
                .show();
    }

    public void commentLongClickAction(View view) {
        DialogInterface.OnClickListener editListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser currUser = Database.getAuth().getCurrentUser();
                if (currUser == null) {
                    Toast.makeText(context, "????????? ??? ??????????????? ???????????????.", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else if (!currUser.getUid().equals((String) view.getTag(R.string.tagKey2))) {
                    Toast.makeText(context, "????????? ????????? ?????? ??? ??? ????????????.", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    EditText et_editComment = new EditText(context);
                    DialogInterface.OnClickListener editCommentListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String editStr = et_editComment.getText().toString();
                            db.editComment(postn, (String) view.getTag(R.string.tagKey1), editStr, CATEGORY);
                            ((CommentView) view).text.setText(editStr);
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
                            .setTitle("????????? ?????? ??????????????????.")
                            .setView(et_editComment)
                            .setPositiveButton("??????", editCommentListener)
                            .setNeutralButton("??????", editCancelListener)
                            .show();
                }
            }
        };
        DialogInterface.OnClickListener deleteListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser currUser = Database.getAuth().getCurrentUser();
                if (currUser == null) {
                    Toast.makeText(context, "????????? ??? ??????????????? ???????????????.", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else if (!currUser.getUid().equals((String) view.getTag(R.string.tagKey2))) {
                    Toast.makeText(context, "????????? ????????? ?????? ??? ??? ????????????.", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else {
                    db.deleteComment(postn, (String) view.getTag(R.string.tagKey1), CATEGORY);
                    ((LinearLayout) ((CommentView) view).getParent()).removeView(view);
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
                .setTitle("?????? ?????? ??????")
                .setPositiveButton("?????? ??????", deleteListener)
                .setNeutralButton("??????", cancelListener)
                .setNegativeButton("?????? ??????", editListener)
                .show();
    }
}

