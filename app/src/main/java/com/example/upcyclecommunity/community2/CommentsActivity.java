package com.example.upcyclecommunity.community2;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.community2.adapter.Comments_RecyclerViewAdapter;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Database;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {
    public static final String CATEGORY = "2";

    Database db = new Database();
    private Context mContext;
    private RequestManager mGlideRequestManager;

    private String postNumber;

    private RecyclerView recyclerView;
    private Comments_RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Long> listData;

    private ImageView my_profile_iv;
    private EditText comment_et;
    private Button submit_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        postNumber = getIntent().getStringExtra("postNumber");

        mContext = this;
        mGlideRequestManager = Glide.with(this);

        listData = new ArrayList<>();
        recyclerView = findViewById(R.id.activity_comments_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //커스텀 어댑터 생성
        recyclerViewAdapter = new Comments_RecyclerViewAdapter(postNumber, listData, this, mGlideRequestManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        my_profile_iv = findViewById(R.id.activity_comments_profile_imageView);
        comment_et = findViewById(R.id.activity_comments_comment_editText);
        submit_btn = findViewById(R.id.activity_comments_submit_button);

        submit_btn.setOnClickListener(view -> {
            String comment = comment_et.getText().toString();
            comment_et.setText("");
            submit_btn.setVisibility(View.INVISIBLE);
            findViewById(R.id.activity_comments_submit_button_progressBar).bringToFront();
            findViewById(R.id.activity_comments_submit_button_progressBar).setVisibility(View.VISIBLE);
            db.writeComment(Long.valueOf(postNumber), comment, CATEGORY, listData, new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    int position = listData.size()-1;
                    recyclerViewAdapter.notifyItemInserted(position);
                    submit_btn.setVisibility(View.VISIBLE);
                    findViewById(R.id.activity_comments_submit_button_progressBar).setVisibility(View.GONE);
                }

                @Override
                public void ifFail(Object task) {

                }
            });
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        listData.clear();
        recyclerViewAdapter.notifyDataSetChanged();

        if(db.getAuth().getCurrentUser() != null) {
            Database.getUserProfileImageRoot().child(Database.getAuth().getCurrentUser().getUid())
                    .getDownloadUrl().addOnSuccessListener(uri -> {
                if (mGlideRequestManager != null) {
                    mGlideRequestManager.load(uri).into(my_profile_iv);
                }
            });
        }
        else {
            my_profile_iv.setImageResource(R.drawable.ic_baseline_person_24);
        }

        Database.getDBRoot().child("Post2").child("posting").
                child(postNumber).child("comment").
                get().addOnCompleteListener(task -> {

                    if (task.isSuccessful()){
                        for (DataSnapshot dataSnapshot : task.getResult().getChildren()){
                            if (!(dataSnapshot.getKey().equals("cnt"))){
                                listData.add(Long.valueOf(dataSnapshot.getKey()));
                                recyclerViewAdapter.notifyItemInserted(listData.size()-1);
                            }
                        }
                    }
                    else{
                        Toast.makeText(mContext, "loading comments error!", Toast.LENGTH_LONG).show();
                    }

                });
    }
}
