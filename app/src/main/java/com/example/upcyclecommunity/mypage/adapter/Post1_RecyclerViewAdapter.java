package com.example.upcyclecommunity.mypage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.database.Post1;
import com.example.upcyclecommunity.database.Post2;

import java.util.ArrayList;

public class Post1_RecyclerViewAdapter extends RecyclerView.Adapter<Post1_ViewHolder>{

    private ArrayList<Post1> listData;
    private Context context;

    public Post1_RecyclerViewAdapter(ArrayList<Post1> listData, Context context){
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public Post1_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post1_item, parent, false);
        return new Post1_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Post1_ViewHolder holder, int position) {
        Post1 data = listData.get(position);
//        holder.post_iv.setImageResource(data.pic);
        holder.post_iv.setImageResource(R.drawable.ic_launcher_background);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItem(Post1 data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }
}
class Post1_ViewHolder extends RecyclerView.ViewHolder{
    ImageView post_iv;

    public Post1_ViewHolder(@NonNull View itemView) {
        super(itemView);

        post_iv = itemView.findViewById(R.id.post1_item_imageView);

        itemView.setOnClickListener(view -> {
            Toast.makeText(itemView.getContext(), "click1", Toast.LENGTH_SHORT).show();
        });
    }
}
