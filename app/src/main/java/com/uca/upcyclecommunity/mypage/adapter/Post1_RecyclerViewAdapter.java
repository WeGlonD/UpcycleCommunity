package com.uca.upcyclecommunity.mypage.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.uca.upcyclecommunity.R;
import com.uca.upcyclecommunity.database.Acts;
import com.uca.upcyclecommunity.database.Database;
import com.uca.upcyclecommunity.mypage.MyPost1_Activity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class Post1_RecyclerViewAdapter extends RecyclerView.Adapter<Post1_RecyclerViewAdapter.Post1_ViewHolder>{
    public static final String CATEGORY = "2";
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
        holder.images_iv.setVisibility(View.GONE);
        db.readOnePostLine(postNumber, Long.valueOf(2), CATEGORY, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                holder.progressBar.setVisibility(View.INVISIBLE);
                String line = ((Task<DataSnapshot>) task).getResult().getValue(String.class);
                if(line != null){
                    Uri downloadUri = Uri.parse(line);
                    if(mGlideRequestManager != null)
                        mGlideRequestManager.load(downloadUri).into(holder.post_iv);
                }
                else{
                    holder.post_iv.setImageResource(R.drawable.ic_launcher_background);
                }
            }

            @Override
            public void ifFail(Object task) {
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.post_iv.setImageResource(R.drawable.ic_launcher_background);
            }
        });
        db.readOnePostLine(postNumber, Long.valueOf(3), CATEGORY, new Acts() {
            @Override
            public void ifSuccess(Object task) {
                String line = ((Task<DataSnapshot>) task).getResult().getValue(String.class);
                if(line != null){
                    holder.images_iv.setVisibility(View.VISIBLE);
                    holder.images_iv.setImageResource(R.drawable.ic_baseline_photo_library_24);
                }
            }

            @Override
            public void ifFail(Object task) {
                holder.progressBar.setVisibility(View.INVISIBLE);
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
        ImageView images_iv;
        ProgressBar progressBar;

        public Post1_ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_iv = itemView.findViewById(R.id.post1_item_imageView);
            images_iv = itemView.findViewById(R.id.post1_item_images_image_imageView);
            progressBar = itemView.findViewById(R.id.post1_item_progress_circular);

            itemView.setOnClickListener(view -> {

                String postNumber = String.valueOf(listData.get(getAdapterPosition()));
                Log.d("Dirtfy_test", postNumber);
                //Toast.makeText(context, postNumber, Toast.LENGTH_SHORT).show();

                Intent it = new Intent(context, MyPost1_Activity.class);
                it.putExtra("position", getAdapterPosition());

                context.startActivity(it);
            });
        }
    }
}

