package com.example.upcyclecommunity.recruit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.community1.Fragment_CM1;
import com.example.upcyclecommunity.community1.Personal_Post;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Database;
import com.example.upcyclecommunity.database.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class recruit_Adapter extends RecyclerView.Adapter<recruit_Adapter.MyViewHolder>{
    public static final String CATEGORY = "3";
    private Context mContext;
    private ArrayList<Long> listData;
    private viewOnClickListener mOnClickListener = null;
    private RequestManager GlideWith = null;

    Database db = new Database();
    ArrayList<Post> postArray = new ArrayList<>();

    interface viewOnClickListener{
        void clickEvent(Long str, Long end);
    }

    public recruit_Adapter(ArrayList<Long> listData, Context mContext, viewOnClickListener mOnClickListener) {
        this.listData = listData;
        this.mContext = mContext;
        this.mOnClickListener = mOnClickListener;
    }

    public recruit_Adapter(ArrayList<Long> listData, Context mContext, viewOnClickListener mOnClickListener, RequestManager GlideWith) {
        this.listData = listData;
        this.mContext = mContext;
        this.mOnClickListener = mOnClickListener;
        this.GlideWith = GlideWith;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recruit_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Long postNumber = listData.get(i);
        if(postNumber == -1){
            holder.load_more_btn.setVisibility(View.VISIBLE);
            holder.mTitle.setVisibility(View.INVISIBLE);
            holder.userName_tv.setVisibility(View.INVISIBLE);
            holder.date_tv.setVisibility(View.INVISIBLE);
            holder.commentCnt_tv.setVisibility(View.INVISIBLE);
            holder.mComment.setVisibility(View.INVISIBLE);
            holder.userPic.setVisibility(View.INVISIBLE);
            holder.progressBar.setVisibility(View.INVISIBLE);

            holder.clickCnt_text_tv.setVisibility(View.INVISIBLE);
            holder.linearLayout.setVisibility(View.INVISIBLE);
            holder.clickCnt_text_tv.setVisibility(View.INVISIBLE);

            try{
                holder.itemView.setBackground(mContext.getDrawable(R.drawable.border_line));
            }
            catch (Exception e){

            }
        }
        else {
            holder.load_more_btn.setVisibility(View.GONE);
            holder.mTitle.setVisibility(View.VISIBLE);
            holder.userName_tv.setVisibility(View.VISIBLE);
            holder.date_tv.setVisibility(View.VISIBLE);
            holder.commentCnt_tv.setVisibility(View.VISIBLE);
            holder.mComment.setVisibility(View.VISIBLE);
            holder.userPic.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.clickCnt_text_tv.setVisibility(View.VISIBLE);
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.clickCnt_text_tv.setVisibility(View.VISIBLE);
            try {
                holder.itemView.setBackground(mContext.getDrawable(R.drawable.border_and_white));
            }
            catch (Exception e){
                
            }


            Log.d("minseok","postnumber"+postNumber+"i"+i);
            db.readOnePost(postArray, postNumber, CATEGORY, new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    Post personal_p = postArray.get(postArray.size()-1);
                    String User_Id = personal_p.getUser_id();
                    Log.d("minseok","userid"+User_Id);
                    //user
                    Database.getUserRoot().child(User_Id).child("name").get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Database.getUserProfileImageRoot().child(User_Id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String iv_url = uri.toString();
                                    GlideWith.load(iv_url).into(holder.userPic);
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
            Database.getDBRoot().child("Post" + CATEGORY).
                    child("posting").child(String.valueOf(postNumber)).
                    get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            boolean hasImage = false;
                            Iterable<DataSnapshot> postData = task.getResult().getChildren();
                            for (DataSnapshot data : postData) {
                                String key = data.getKey();
                                if (key.equals("0")) {
                                    String value = data.getValue(String.class);
                                    holder.mTitle.setText(value);
                                } else if (key.equals("comment")) {
                                    Long value = Long.valueOf(data.getChildrenCount() - 1);
                                    holder.mComment.setText(String.valueOf(value));
                                } else if (key.equals("clickcnt")) {
                                    String value = String.valueOf(data.getValue(Long.class));
                                    holder.commentCnt_tv.setText(value);
                                } else if (key.equals("timestamp")) {
                                    String value = data.getValue(String.class).substring(0, 10).replace("-", ".");
                                    holder.date_tv.setText(value);
                                } else if (key.equals("writer")) {
                                    String value = data.getValue(String.class);
                                    Database.getUserRoot().child(value).
                                            child("name").get().addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    String name = task1.getResult().getValue(String.class);
                                                    holder.userName_tv.setText(name);
                                                } else {
                                                    holder.userName_tv.setText("error");
                                                }
                                            });
                                }
                            }
                            if (!(hasImage)) {
                                holder.progressBar.setVisibility(View.INVISIBLE);
                                holder.userPic.setImageResource(R.drawable.transparent);
                            }
                        } else {
                            holder.progressBar.setVisibility(View.INVISIBLE);
                            holder.mTitle.setText("error");
                            holder.userPic.setImageResource(R.drawable.search);
                            holder.mComment.setText("?");
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Button load_more_btn;
        public TextView mTitle;
        public TextView userName_tv;
        public TextView date_tv;
        public TextView commentCnt_tv;
        public TextView mComment;
        public ImageView userPic;
        public ProgressBar progressBar;
        public TextView clickCnt_text_tv;
        public ConstraintLayout linearLayout;
        public TextView commentCnt_text_tv;

        public MyViewHolder(@NonNull View view) {
            super(view);
            load_more_btn = view.findViewById(R.id.recruit_item_load_more_button);
            load_more_btn.setOnClickListener(viw -> {
                Toast.makeText(mContext, "last click", Toast.LENGTH_SHORT).show();
                if(mOnClickListener != null){
                    Toast.makeText(mContext, "last click not null", Toast.LENGTH_SHORT).show();
                    if(listData.size()>1) {
                        Long lastPostNumber = listData.get(listData.size() - 2);
                        Fragment_CM1.isUpdating = true;
                        mOnClickListener.clickEvent(lastPostNumber + 1, lastPostNumber + 5);
                    }
                }
            });
            mTitle = view.findViewById(R.id.recruit_tv_post_title1);
            userName_tv = view.findViewById(R.id.recruit_item_userName_textView);
            date_tv = view.findViewById(R.id.recruit_item_date_textView);
            commentCnt_tv = view.findViewById(R.id.recruit_item_clickCnt_textView);
            mComment = view.findViewById(R.id.recruit_tv_comment1);
            userPic = view.findViewById(R.id.recruit_post_iv);
            progressBar = view.findViewById(R.id.recruit_item_progress_circular);

            clickCnt_text_tv = view.findViewById(R.id.recruit_item_clickCnt_text_textView);
            linearLayout = view.findViewById(R.id.recruit_item_linearLayout);
            commentCnt_text_tv = view.findViewById(R.id.recruit_noid);

            view.setOnClickListener(viw -> {
                Long postNumber = listData.get(getAdapterPosition());
                Toast.makeText(mContext, ""+postNumber, Toast.LENGTH_SHORT).show();
                if (!(postNumber.equals((long) -1))){
                    String stringPostNumber = String.valueOf(postNumber);
                    Log.d("Dirtfy_test", stringPostNumber);

                    Intent it = new Intent(mContext, Personal_Post.class);
                    it.putExtra("postn", stringPostNumber);
                    it.putExtra("category", CATEGORY);

                    mContext.startActivity(it);
                }
            });
        }
    }
}
