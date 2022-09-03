package com.example.upcyclecommunity.mypage.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Post1_RecyclerViewAdapter extends RecyclerView.Adapter<Post1_ViewHolder>{

    @NonNull
    @Override
    public Post1_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull Post1_ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
class Post1_ViewHolder extends RecyclerView.ViewHolder{

    public Post1_ViewHolder(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(view -> {

        });
    }
}
