package com.example.upcyclecommunity.mypage.adapter;

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
import com.bumptech.glide.RequestManager;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.community1.Personal_Post;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Database;
import com.example.upcyclecommunity.database.Post1;
import com.example.upcyclecommunity.database.Post2;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class Post2_RecyclerViewAdapter extends RecyclerView.Adapter<Post2_RecyclerViewAdapter.Post2_ViewHolder>{
    public static final String CATEGORY = "2";

    private ArrayList<Long> listData;
    private Context context;
    private RequestManager mGlideRequestManager;

    public Post2_RecyclerViewAdapter(ArrayList<Long> listData, Context context, RequestManager mGlideRequestManager){
        this.listData = listData;
        this.context = context;
        this.mGlideRequestManager = mGlideRequestManager;
    }

    @NonNull
    @Override
    public Post2_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post2_item, parent, false);
        return new Post2_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Post2_ViewHolder holder, int position) {
//        holder.post_iv.setImageResource(data.pic);
//        holder.post_tv.setText(data.title);
        holder.post_iv.setImageResource(R.drawable.ic_launcher_background);
        holder.post_tv.setText("test");
        Database db = new Database();
        Long postNumber = listData.get(position);
        db.readOnePostLine(postNumber, Long.valueOf(1), CATEGORY, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                String line = ((Task<DataSnapshot>) task).getResult().getValue(String.class);
                holder.post_tv.setText(line);
                db.readOnePostLine(postNumber, Long.valueOf(2), CATEGORY, new Acts() {
                    @Override
                    public void ifSuccess(Object task) {
                        String line = ((Task<DataSnapshot>) task).getResult().getValue(String.class);
                        if(line != null){
                            Uri downloadUri = Uri.parse(line);
                            if(mGlideRequestManager != null)
                                mGlideRequestManager.load(downloadUri).into(holder.post_iv);
                        }
                    }

                    @Override
                    public void ifFail(Object task) {
                        holder.post_iv.setImageResource(R.drawable.ic_launcher_background);
                    }
                });
            }

            @Override
            public void ifFail(Object task) {
                holder.post_tv.setText("test");
            }
        });

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItem(Long data){
        listData.add(data);
    }
//    public void addItem(Post2 data) {
//        // 외부에서 item을 추가시킬 함수입니다.
//        listData.add(data);
//    }

    class Post2_ViewHolder extends RecyclerView.ViewHolder{
        ImageView post_iv;
        TextView post_tv;

        public Post2_ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_iv = itemView.findViewById(R.id.post2_item_imageView);
            post_tv = itemView.findViewById(R.id.post2_item_textView);

            itemView.setOnClickListener(view -> {
                String postNumber = String.valueOf(listData.get(getAdapterPosition()));
                Log.d("Dirtfy_test", postNumber);

                Intent it = new Intent(context, Personal_Post.class);
                it.putExtra("postn", postNumber);

                context.startActivity(it);
            });
        }
    }
}
