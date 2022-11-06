package com.uca.upcyclecommunity.community2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.uca.upcyclecommunity.R;
import com.uca.upcyclecommunity.database.Database;

import java.util.ArrayList;

public class Comments_RecyclerViewAdapter extends RecyclerView.Adapter<Comments_RecyclerViewAdapter.Comments_ViewHolder> {
    public static final String CATEGORY = "2";
    private String stringOfPostNumber;
    private ArrayList<Long> listData;
    private Context context;
    private RequestManager mGlideRequestManager;

    public Comments_RecyclerViewAdapter(String stringOfPostNumber, ArrayList<Long> listData, Context context, RequestManager mGlideRequestManager){
        this.stringOfPostNumber = stringOfPostNumber;
        this.listData = listData;
        this.context = context;
        this.mGlideRequestManager = mGlideRequestManager;
    }

//    public Post1_RecyclerViewAdapter(HashMap<Long, Long> listData, Context context){
//        this.listData = listData;
//        this.context = context;
//    }


    @NonNull
    @Override
    public Comments_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_comments_recyclerview_item, parent, false);
        return new Comments_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Comments_ViewHolder holder, int position) {
        String commentNumber = String.valueOf(listData.get(position));
        Database.getDBRoot().child("Post2").child("posting").
                child(stringOfPostNumber).child("comment").
                child(commentNumber).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if(task != null){
                    String text = task.getResult().child("text").getValue(String.class);
                    String user_uid = task.getResult().child("writer").getValue(String.class);
                    holder.comment_tv.setText(text);
                    Database.getUserRoot().child(user_uid).
                            child("name").get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            String userName = task1.getResult().getValue(String.class);
                            holder.userName_tv.setText(userName);
                        }
                        else{
                            Toast.makeText(context, "load user name error", Toast.LENGTH_SHORT).show();
                            holder.userName_tv.setText("???");
                        }
                    });
                    Database.getUserProfileImageRoot().child(user_uid).getDownloadUrl().addOnSuccessListener(uri -> {
                       if (uri != null){
                           holder.progressBar.setVisibility(View.INVISIBLE);
                           mGlideRequestManager.load(uri).into(holder.profile_iv);
                       }
                       else{
                           Toast.makeText(context, "load user image error", Toast.LENGTH_SHORT).show();
                           holder.profile_iv.setImageResource(R.drawable.ic_baseline_person_24);
                       }
                    });
                }
            }
            else{
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.comment_tv.setText("??error??");
                holder.profile_iv.setImageResource(R.drawable.ic_baseline_person_24);
            }
        });
    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItem(Long postNumber) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(postNumber);
    }

    class Comments_ViewHolder extends RecyclerView.ViewHolder{
        ImageView profile_iv;
        TextView userName_tv;
        ProgressBar progressBar;
        TextView comment_tv;


        public Comments_ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_iv = itemView.findViewById(R.id.activity_comments_recyclerview_item_profile_imageView);
            userName_tv = itemView.findViewById(R.id.activity_comments_recyclerview_item_userName_textView);
            progressBar = itemView.findViewById(R.id.activity_comments_recyclerview_item_progressBar);
            comment_tv = itemView.findViewById(R.id.activity_comments_recyclerview_item_comment_textView);

        }
    }
}
