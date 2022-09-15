package com.example.upcyclecommunity.community1;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.database.Comment;
import com.example.upcyclecommunity.database.Database;
import com.google.android.gms.tasks.OnSuccessListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentView extends ConstraintLayout {
    Context context;
    Comment comment;
    TextView text;
    RequestManager GlideWith;

    public CommentView(Context context, Comment comment, RequestManager GlideWith) {
        super(context);
        this.context = context;
        this.comment = comment;
        this.GlideWith = GlideWith;
        init();
    }

    public void init(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.comment_item_layout,this,true);

        CircleImageView userPic = findViewById(R.id.comment_post_iv);
        TextView nickname = findViewById(R.id.comment_item_tv_nickname);
        text = findViewById(R.id.comment_item_tv_text);
        text.setText(comment.getText());

        String User_Id = comment.getWriterUid();

        Database.getUserRoot().child(User_Id).child("name").get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                nickname.setText(task1.getResult().getValue(String.class));
            }
        });
        Database.getUserProfileImageRoot().child(User_Id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String iv_url = uri.toString();
                GlideWith.load(iv_url).into(userPic);
                findViewById(R.id.comment_item_progress_circular).setVisibility(GONE);
            }
        });
    }
}
