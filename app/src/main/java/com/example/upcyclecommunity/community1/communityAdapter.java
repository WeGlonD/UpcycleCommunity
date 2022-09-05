package com.example.upcyclecommunity.community1;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.database.Database;

import java.util.ArrayList;

public class communityAdapter extends RecyclerView.Adapter<communityAdapter.MyViewHolder>{
    private ArrayList<TitleInfo> mTitles;
    private Context mContext;


    public communityAdapter(ArrayList<TitleInfo> mTitles, Context mContext) {
        this.mTitles = mTitles;
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
        holder.mTitle.setText(mTitles.get(i).getTitle());
        holder.mcomment.setText(mTitles.get(i).getCommentcnt().toString());
    }

    @Override
    public int getItemCount() {
        return (mTitles != null ? mTitles.size() : 0);
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTitle;
        public final TextView mcomment;

        public MyViewHolder(@NonNull View view) {
            super(view);
            this.mTitle = view.findViewById(R.id.tv_post_title1);
            this.mcomment = view.findViewById(R.id.tv_comment1);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Database.getTitleRoot().child(mTitle.getText().toString()).get().addOnCompleteListener(task -> {
                        Long postnum = task.getResult().getValue(Long.class);
                        Log.d("minseok",postnum+"");
                        Intent intent = new Intent(mContext, Personal_Post.class);
                        intent.putExtra("postn",postnum.toString());
                        mContext.startActivity(intent);
                    });
                }
            });
        }
    }
}

