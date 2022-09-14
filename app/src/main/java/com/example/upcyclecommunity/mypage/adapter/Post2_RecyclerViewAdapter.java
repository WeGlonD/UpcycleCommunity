package com.example.upcyclecommunity.mypage.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    private String CATEGORY;

    private ArrayList<Long> listData;
    private Context context;
    private RequestManager mGlideRequestManager;

    public Post2_RecyclerViewAdapter(ArrayList<Long> listData, String CATEGORY, Context context, RequestManager mGlideRequestManager){
        this.listData = listData;
        this.CATEGORY = CATEGORY;
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
//        holder.post_iv.setImageResource(R.drawable.ic_launcher_background);
//        holder.post_tv.setText("test");
        Database db = new Database();
        Long postNumber = listData.get(position);
//        db.readOnePostLine(postNumber, Long.valueOf(1), CATEGORY, new Acts() {
//            @Override
//            public void ifSuccess(Object task) {
//                String line = ((Task<DataSnapshot>) task).getResult().getValue(String.class);
//                holder.post_tv.setText(line);
//                db.readOnePostLine(postNumber, Long.valueOf(2), CATEGORY, new Acts() {
//                    @Override
//                    public void ifSuccess(Object task) {
//                        holder.progressBar.setVisibility(View.INVISIBLE);
//                        String line = ((Task<DataSnapshot>) task).getResult().getValue(String.class);
//                        if(line != null){
//                            Uri downloadUri = Uri.parse(line);
//                            if(mGlideRequestManager != null)
//                                mGlideRequestManager.load(downloadUri).into(holder.post_iv);
//                        }
//                        else{
//                            holder.post_iv.setImageResource(R.drawable.ic_launcher_background);
//                        }
//                    }
//
//                    @Override
//                    public void ifFail(Object task) {
//                        holder.progressBar.setVisibility(View.INVISIBLE);
//                        holder.post_iv.setImageResource(R.drawable.ic_launcher_background);
//                    }
//                });
//            }
//
//            @Override
//            public void ifFail(Object task) {
//                holder.post_tv.setText("test");
//            }
//        });

        Database.getDBRoot().child("Post"+CATEGORY).
                child("posting").child(String.valueOf(postNumber)).
                get().addOnCompleteListener(task -> {

                    if (task.isSuccessful()){
                        boolean hasImage = false;
                        boolean hasClickCnt = false;
                        Iterable<DataSnapshot> postData = task.getResult().getChildren();
                        for(DataSnapshot data : postData){
                            String key = data.getKey();
                            if(key.equals("0")){
                                String value = data.getValue(String.class);
                                holder.post_tv.setText(value);
                            }
                            else if(key.equals("2")){
                                holder.progressBar.setVisibility(View.INVISIBLE);
                                String value = data.getValue(String.class);
                                hasImage = true;
                                Uri uri = Uri.parse(value);
                                Glide.with(holder.itemView).load(uri).into(holder.post_iv);
                            }
                            else if(key.equals("comment")){
                                Long value = Long.valueOf(data.getChildrenCount()-1);
                                holder.commentCnt_tv.setText(String.valueOf(value));
                            }
                            else if (key.equals("clickcnt")){
                                hasClickCnt = true;
                                String value = String.valueOf(data.getValue(Long.class));
                                holder.clickCnt_tv.setText(value);
                            }
                        }
                        if (!(hasImage)){
                            holder.progressBar.setVisibility(View.INVISIBLE);
//                            holder.post_iv.setImageResource(R.drawable.ic_baseline_image_not_supported_24);
                            holder.post_iv.setVisibility(View.GONE);
                        }
                        if (!(hasClickCnt)){
                            holder.clickCnt_tv.setVisibility(View.GONE);
                        }
                    }
                    else{
                        holder.progressBar.setVisibility(View.INVISIBLE);
                        holder.post_tv.setText("error");
                        holder.post_iv.setImageResource(R.drawable.search);
                        holder.commentCnt_tv.setText("?");
                        holder.clickCnt_tv.setText("?");
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
        ProgressBar progressBar;
        TextView post_tv;
        TextView clickCnt_tv;
        TextView commentCnt_tv;

        public Post2_ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_iv = itemView.findViewById(R.id.post2_item_imageView);
            progressBar = itemView.findViewById(R.id.post2_item_progress_circular);
            post_tv = itemView.findViewById(R.id.post2_item_textView);
            clickCnt_tv = itemView.findViewById(R.id.post2_item_clickCount_textView);
            commentCnt_tv = itemView.findViewById(R.id.post2_item_commentCount_textView);

            itemView.setOnClickListener(view -> {
                String postNumber = String.valueOf(listData.get(getAdapterPosition()));
                Log.d("Dirtfy_test", postNumber);

                Intent it = new Intent(context, Personal_Post.class);
                it.putExtra("postn", postNumber);
                it.putExtra("category", CATEGORY);

                context.startActivity(it);
//                Intent it = new Intent(context, Personal_Post.class);
//                it.putExtra("postn", postNumber);

//                context.startActivity(it);
            });
        }
    }
}
