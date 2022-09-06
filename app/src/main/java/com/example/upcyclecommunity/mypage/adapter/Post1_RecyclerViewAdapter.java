package com.example.upcyclecommunity.mypage.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.example.upcyclecommunity.database.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class Post1_RecyclerViewAdapter extends RecyclerView.Adapter<Post1_RecyclerViewAdapter.Post1_ViewHolder>{

    private ArrayList<Long> listData;
//    private HashMap<Long, Long> listData;
    private Context context;
    private RequestManager mGlideRequestManager;

    public Post1_RecyclerViewAdapter(ArrayList<Long> listData, Context context, RequestManager mGlideRequestManager){
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
    public Post1_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post1_item, parent, false);
        return new Post1_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Post1_ViewHolder holder, int position) {
//        Post1 data = listData.get(position);
//        Long postKey = listData.get(Long.valueOf(position));
        Database db = new Database();
        Long postNumber = listData.get(position);
        db.readOnePostLine(postNumber, Long.valueOf(2), new Acts() {
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
    public int getItemCount() {
        return listData.size();
    }

    public void addItem(Long postNumber) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(postNumber);
    }
//    public void addItem(Long key, Long value) {
//        // 외부에서 item을 추가시킬 함수입니다.
//        listData.put(key, value);
//    }

    class Post1_ViewHolder extends RecyclerView.ViewHolder{
        ImageView post_iv;

        public Post1_ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_iv = itemView.findViewById(R.id.post1_item_imageView);

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

