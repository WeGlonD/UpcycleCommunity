package com.example.upcyclecommunity.mypage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.database.Post1;
import com.example.upcyclecommunity.database.Post2;

import java.util.ArrayList;

public class Post2_RecyclerViewAdapter extends RecyclerView.Adapter<Post2_ViewHolder>{

    private ArrayList<Post2> listData;
    private Context context;

    public Post2_RecyclerViewAdapter(ArrayList<Post2> listData, Context context){
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public Post2_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post2_item, parent, false);
        return new Post2_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Post2_ViewHolder holder, int position) {
        Post2 data = listData.get(position);
//        holder.post_iv.setImageResource(data.pic);
//        holder.post_tv.setText(data.title);
        holder.post_iv.setImageResource(R.drawable.ic_launcher_background);
        holder.post_tv.setText("test");
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItem(Post2 data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }
}
class Post2_ViewHolder extends RecyclerView.ViewHolder{
    ImageView post_iv;
    TextView post_tv;

    public Post2_ViewHolder(@NonNull View itemView) {
        super(itemView);

        post_iv = itemView.findViewById(R.id.post2_item_imageView);
        post_tv = itemView.findViewById(R.id.post2_item_textView);

        itemView.setOnClickListener(view -> {
            Toast.makeText(itemView.getContext(), "click2", Toast.LENGTH_SHORT).show();
        });
    }
}
