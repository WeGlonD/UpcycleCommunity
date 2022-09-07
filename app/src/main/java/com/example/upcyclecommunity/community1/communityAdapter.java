package com.example.upcyclecommunity.community1;

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
import com.example.upcyclecommunity.database.Database;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class communityAdapter extends RecyclerView.Adapter<communityAdapter.MyViewHolder>{
    public static final String CATEGORY = "1";
    private ArrayList<Long> listData;
    private Context mContext;

    public communityAdapter(ArrayList<Long> listData, Context mContext) {
        this.listData = listData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.community1_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Long postNumber = listData.get(i);
        Database.getDBRoot().child("Post"+CATEGORY).
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
                                String value = String.valueOf(data.getValue(Long.class));
                                holder.commentCnt_tv.setText(value);
                            }
                            else if (key.equals("timestamp")){
                                String value = data.getValue(String.class).substring(0, 10).replace("-", ".");
                                holder.date_tv.setText(value);
                            }
                            else if (key.equals("writer")){
                                String value = data.getValue(String.class);
                                Database.getUserRoot().child(value).
                                        child("name").get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()){
                                        String name = task1.getResult().getValue(String.class);
                                        holder.userName_tv.setText(name);
                                    }
                                    else{
                                        holder.userName_tv.setText("error");
                                    }
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
        public TextView mTitle;
        public TextView userName_tv;
        public TextView date_tv;
        public TextView commentCnt_tv;
        public TextView mComment;
        public ImageView postFirstImage_iv;

        public MyViewHolder(@NonNull View view) {
            super(view);

            mTitle = view.findViewById(R.id.tv_post_title1);
            userName_tv = view.findViewById(R.id.community1_item_userName_textView);
            date_tv = view.findViewById(R.id.community1_item_date_textView);
            commentCnt_tv = view.findViewById(R.id.community1_item_clickCnt_textView);
            mComment = view.findViewById(R.id.tv_comment1);
            postFirstImage_iv = view.findViewById(R.id.post_iv);

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

