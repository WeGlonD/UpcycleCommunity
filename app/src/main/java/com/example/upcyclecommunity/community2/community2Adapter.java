package com.example.upcyclecommunity.community2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.community1.Personal_Post;
import com.example.upcyclecommunity.community1.communityAdapter;
import com.example.upcyclecommunity.database.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class community2Adapter extends RecyclerView.Adapter<community2Adapter.MyViewHolder>{
    private ArrayList<Long> listData;
    private Context mContext;

    public community2Adapter(ArrayList<Long> listData, Context mContext) {
        this.listData = listData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.community2_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Long postNumber = listData.get(i);
        Database.getDBRoot().child("Post").
                child("posting").child(String.valueOf(postNumber)).
                get().addOnCompleteListener(task -> {

                    if (task.isSuccessful()){
                        Iterable<DataSnapshot> postData = task.getResult().getChildren();
                        for(DataSnapshot data : postData){
                            String key = data.getKey();
                            if(key.equals("0")){
                                String value = data.getValue(String.class);
                                holder.mTitle.setText(value);
                            }
                            else if(key.equals("2")){
                                String value = data.getValue(String.class);
                                Uri uri = Uri.parse(value);
                                Glide.with(holder.itemView).load(uri).into(holder.postFirstImage_iv);
                            }
                            else if(key.equals("comment")){
                                Long value = Long.valueOf(data.getChildrenCount()-1);
                                holder.mComment.setText(String.valueOf(value));
                            }
                            else if (key.equals("clickcnt")){
                                Long value = Long.valueOf(data.getValue(Long.class));
                                holder.clickCnt_tv.setText(String.valueOf(value));
                            }
                            else if (key.equals("timestamp")){
                                String value = data.getValue(String.class).substring(0, 10).replace("-", ".");
                                holder.timeStamp_tv.setText(value);
                            }
                            else if (key.equals("tags")){
                                String value = data.getValue(String.class);
                                holder.tags_tv.setText(value);
                            }
                            else if (key.equals("writer")){
                                String value = data.getValue(String.class);
                                Database.getUserRoot().child(value).child("name").get().addOnCompleteListener(task1 -> {
                                   if (task1.isSuccessful()){
                                       String name = task1.getResult().getValue(String.class);
                                       holder.userName.setText(name);
                                   }
                                   else{
                                       holder.userName.setText("error");
                                   }
                                });
                                Database.getUserProfileImageRoot().child(value).getDownloadUrl().addOnSuccessListener(uri -> {
                                   Glide.with(mContext).load(uri).into(holder.userPic);
                                });
                            }
                        }
                    }
                    else{
                        holder.mTitle.setText("error");
                        holder.postFirstImage_iv.setImageResource(R.drawable.search);
                        holder.mComment.setText("?");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView userPic;
        public TextView userName;
        public TextView mTitle;
        public ImageView postFirstImage_iv;
        public TextView tags_tv;
        public TextView timeStamp_tv;
        public TextView clickCnt_tv;
        public TextView mComment;

        public MyViewHolder(@NonNull View view) {
            super(view);

            userPic = view.findViewById(R.id.community2_item_userPic_imageView);
            mTitle = view.findViewById(R.id.community2_item_title_textView);
            userName = view.findViewById(R.id.community2_item_userName_textView);
            postFirstImage_iv = view.findViewById(R.id.community2_item_firstImage_imageView);
            tags_tv = view.findViewById(R.id.community2_item_tags_textView);
            timeStamp_tv = view.findViewById(R.id.community2_item_timeStamp_textView);
            clickCnt_tv = view.findViewById(R.id.community2_item_clickCnt_textView);
            mComment = view.findViewById(R.id.community2_item_commentCnt_textView);

            view.setOnClickListener(viw -> {
                String postNumber = String.valueOf(listData.get(getAdapterPosition()));
                Log.d("Dirtfy_test", postNumber);

                Intent it = new Intent(mContext, Personal_Post.class);
                it.putExtra("postn", postNumber);

                mContext.startActivity(it);
            });
        }
    }
}

