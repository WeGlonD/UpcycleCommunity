package com.example.upcyclecommunity.community2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.community1.Personal_Post;
import com.example.upcyclecommunity.community1.communityAdapter;
import com.example.upcyclecommunity.database.Database;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.sql.Array;
import java.util.ArrayList;

public class community2Adapter extends RecyclerView.Adapter<community2Adapter.MyViewHolder> {
    public static final String CATEGORY = "2";
    private ArrayList<Long> listData;
    private Context mContext;
    private community2Adapter.clickListener mclickListener;

    interface clickListener {
        public void mclickListener_Dialog(String postNumber);
    }

    public community2Adapter(ArrayList<Long> listData, Context mContext, clickListener mclickListener) {
        this.listData = listData;
        this.mContext = mContext;
        this.mclickListener = mclickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.community2_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Long postNumber = listData.get(i);
        holder.postnum = postNumber;
        FirebaseUser firebaseUser = Database.getAuth().getCurrentUser();
        String nowuser = "";
        if (firebaseUser != null) {
            nowuser = firebaseUser.getUid();
        }
        String fUser = nowuser;
        Database.getDBRoot().child("Post" + CATEGORY).
                child("posting").child(String.valueOf(postNumber)).
                get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Iterable<DataSnapshot> postData = task.getResult().getChildren();
                        holder.mUriItems = new ArrayList<>();
                        holder.postPic_progressBar.setVisibility(View.VISIBLE);
                        int count = holder.mPageMark.getChildCount();
                        for(int ii = 0;ii<count;ii++){
                            holder.mPageMark.removeView(holder.mPageMark.getChildAt(0));
                        }
                        for (DataSnapshot data : postData) {
                            String key = data.getKey();
                            if (key.equals("0")) {
                                String value = data.getValue(String.class);
                                holder.mTitle.setText(value);
                            } else if (key.equals("comment")) {
                                Long value = Long.valueOf(data.getChildrenCount() - 1);
                                holder.mComment.setText(String.valueOf(value));
                            } else if (key.equals("likeuser")) {
                                Long value = Long.valueOf(data.getChildrenCount() - 1);
                                holder.likeCnt_tv.setText(String.valueOf(value));
                                for (DataSnapshot dataSnapshot : data.getChildren()) {
                                    if (!dataSnapshot.getKey().equals("cnt") && dataSnapshot.getValue(String.class).equals(fUser)) {
                                        holder.like_btn.setSelected(true);
                                    }
                                }
                            } else if (key.equals("timestamp")) {
                                String value = data.getValue(String.class).substring(0, 10).replace("-", ".");
                                holder.timeStamp_tv.setText(value);
                            } else if (key.equals("tags")) {
                                String value = data.getValue(String.class);
                                holder.tags_tv.setText(value);
                            } else if (key.equals("writer")) {
                                String value = data.getValue(String.class);
                                Database.getUserRoot().child(value).child("name").get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        String name = task1.getResult().getValue(String.class);
                                        holder.userName.setText(name);
                                    } else {
                                        holder.userName.setText("error");
                                    }
                                });
                                Database.getUserProfileImageRoot().child(value).getDownloadUrl().addOnSuccessListener(uri -> {
                                    holder.userPic_progressBar.setVisibility(View.INVISIBLE);
                                    Glide.with(mContext).load(uri).into(holder.userPic);
                                });
                            } else if (key.equals("1")) {
                                String value = data.getValue(String.class);
                                holder.content_tv.setText(value);
                            }
                            else if(key.equals("latitude")||key.equals("longitude")){
                                //위도경도 정보처리
                            } else {
                                String value = data.getValue(String.class);
                                Uri uri = Uri.parse(value);
                                holder.mUriItems.add(uri);
                                ImageView iv = new ImageView(mContext);   //페이지 표시 이미지 뷰 생성
                                iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                iv.setBackgroundResource(R.drawable.page_not);
                                //LinearLayout에 추가

                                holder.postviewPager.setVisibility(View.VISIBLE);
                                holder.mPageMark.setVisibility(View.VISIBLE);

                                holder.mPageMark.addView(iv);
                                Log.d("minseok", key + uri + "");
                            }
                        }
                        holder.mViewAdapter = new ViewAdapter(holder.mUriItems, mContext, new ViewAdapter.clickListener() {
                            @Override
                            public void mclickListener_Dialog(View view, int position) {
                                mclickListener.mclickListener_Dialog(postNumber.toString());
                            }
                        });
                        holder.postviewPager.setAdapter(holder.mViewAdapter);
                        holder.mViewAdapter.notifyDataSetChanged();

                        if(holder.mUriItems.size()==0){
                            Log.d("minseok",holder.mUriItems.size()+"0");
                            holder.postviewPager.setVisibility(View.GONE);
                            holder.mPageMark.setVisibility(View.GONE);
                        }

                        holder.postPic_progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        holder.mTitle.setText("error");
                        /*holder.postFirstImage_iv.setImageResource(R.drawable.search);*/
                        holder.mComment.setText("?");
                    }
                });
        holder.postviewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {   //아이템이 변경되면, gallery나 listview의 onItemSelectedListener와 비슷
            //아이템이 선택이 되었으면
            @Override public void onPageSelected(int position) {
                Log.d("minseok","onPageSelected called"+position);
                holder.mPageMark.getChildAt(holder.mPrePosition).setBackgroundResource(R.drawable.page_not);   //이전 페이지에 해당하는 페이지 표시 이미지 변경
                holder.mPageMark.getChildAt(position).setBackgroundResource(R.drawable.page_select);      //현재 페이지에 해당하는 페이지 표시 이미지 변경
                holder.mPrePosition = position;            //이전 포지션 값을 현재로 변경
            }
            @Override public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {}
            @Override public void onPageScrollStateChanged(int state){}
        });
        holder.mPrePosition = 0;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView userPic;
        public ProgressBar userPic_progressBar;
        public TextView userName;
        public TextView mTitle;
        public ViewPager postviewPager;
        public ProgressBar postPic_progressBar;
        public TextView tags_tv;
        public TextView timeStamp_tv;
        public TextView likeCnt_tv;
        public Button like_btn;

        public LinearLayout comment_linearLayout;
        public ImageView comment_iv;
        public TextView mComment;

        public Long postnum;
        public ArrayList<Uri> mUriItems;
        public TextView content_tv;
        public ViewAdapter mViewAdapter;
        public LinearLayout mPageMark;
        public int mPrePosition = 0;

        @RequiresApi(api = Build.VERSION_CODES.Q)
        public MyViewHolder(@NonNull View view) {
            super(view);
            mUriItems = new ArrayList<>();
            userPic = view.findViewById(R.id.community2_item_userPic_imageView);
            mTitle = view.findViewById(R.id.community2_item_title_textView);
            userName = view.findViewById(R.id.community2_item_userName_textView);
            userPic_progressBar = view.findViewById(R.id.community2_item_user_image_progress_circular);
            postviewPager = view.findViewById(R.id.community2_item_firstImage_imageView);
            postPic_progressBar = view.findViewById(R.id.community2_item_post_image_progress_circular);
            tags_tv = view.findViewById(R.id.community2_item_tags_textView);
            timeStamp_tv = view.findViewById(R.id.community2_item_timeStamp_textView);

            comment_linearLayout = view.findViewById(R.id.community2_item_clickCnt_linearLayout);
            comment_linearLayout.setOnClickListener(viw -> {
                Long postNumber = listData.get(getAdapterPosition());
                Toast.makeText(mContext, ""+postNumber, Toast.LENGTH_SHORT).show();
                String stringPostNumber = String.valueOf(postNumber);
                Log.d("Dirtfy_test", stringPostNumber);

                Intent it = new Intent(mContext, CommentsActivity.class);
                it.putExtra("postNumber", stringPostNumber);

                mContext.startActivity(it);
            });
            comment_iv = view.findViewById(R.id.community2_item_commentImage_imageView);
            comment_iv.setOnClickListener(viw -> {
                Long postNumber = listData.get(getAdapterPosition());
                Toast.makeText(mContext, ""+postNumber, Toast.LENGTH_SHORT).show();
                String stringPostNumber = String.valueOf(postNumber);
                Log.d("Dirtfy_test", stringPostNumber);

                Intent it = new Intent(mContext, CommentsActivity.class);
                it.putExtra("postNumber", stringPostNumber);

                mContext.startActivity(it);
            });
            mComment = view.findViewById(R.id.community2_item_commentCnt_textView);
            mComment.setOnClickListener(viw -> {
                Long postNumber = listData.get(getAdapterPosition());
                Toast.makeText(mContext, ""+postNumber, Toast.LENGTH_SHORT).show();
                String stringPostNumber = String.valueOf(postNumber);
                Log.d("Dirtfy_test", stringPostNumber);

                Intent it = new Intent(mContext, CommentsActivity.class);
                it.putExtra("postNumber", stringPostNumber);

                mContext.startActivity(it);
            });

            likeCnt_tv = view.findViewById(R.id.community2_item_likeCnt_textView);
            like_btn = view.findViewById(R.id.community2_item_likeImage_likeButton);

            mPageMark = view.findViewById(R.id.community2_item_pagemark);

            content_tv = view.findViewById(R.id.community2_item_content_textView);
            content_tv.setOnClickListener(viw -> {
                if(content_tv.isSingleLine())
                    content_tv.setSingleLine(false);
                else
                    content_tv.setSingleLine(true);
            });
            mViewAdapter = new ViewAdapter(mUriItems, mContext, new ViewAdapter.clickListener() {
                @Override
                public void mclickListener_Dialog(View view, int position) {
                    Toast.makeText(mContext, "hi hi", Toast.LENGTH_SHORT).show();
                }
            });

            like_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button like = (Button) view;
                    if (Database.getAuth().getCurrentUser() == null)
                        Toast.makeText(mContext, "로그인 후 좋아요를 표시할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    else {
                        String nowUser = Database.getAuth().getCurrentUser().getUid();
                        if (like.isSelected()) {
                            like.setSelected(false);
                            Database.getDBRoot().child("Post" + CATEGORY).child("posting").child(postnum + "").child("likeuser").get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                        if (!dataSnapshot.getKey().equals("cnt") && nowUser.equals(dataSnapshot.getValue(String.class))) {
                                            String removeKey = dataSnapshot.getKey();
                                            Database.getDBRoot().child("Post" + CATEGORY).child("posting").child(postnum + "").child("likeuser").child(removeKey).removeValue();
                                        }
                                    }
                                }
                            });
                            Database.getUserRoot().child(nowUser).child("likepost").get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                        if (postnum.equals(dataSnapshot.getValue(Long.class)) && !dataSnapshot.getKey().equals("cnt")) {
                                            String removeKey = dataSnapshot.getKey();
                                            Database.getUserRoot().child(nowUser).child("likepost").child(removeKey).removeValue();
                                        }
                                    }
                                }
                            });
                        } else {
                            like.setSelected(true);
                            Database.getDBRoot().child("Post" + CATEGORY).child("posting").child(postnum + "").child("likeuser").child("cnt").get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Long likecnt = task.getResult().getValue(Long.class);
                                    if (likecnt == null)
                                        likecnt = 0l;
                                    likecnt++;
                                    Database.getDBRoot().child("Post" + CATEGORY).child("posting").child(postnum + "").child("likeuser").child("cnt").setValue(likecnt);
                                    Database.getDBRoot().child("Post" + CATEGORY).child("posting").child(postnum + "").child("likeuser").child(likecnt + "").setValue(nowUser);
                                }
                            });
                            Database.getUserRoot().child(nowUser).child("likepost").child("cnt").get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Long likepostcnt = task.getResult().getValue(Long.class);
                                    if (likepostcnt == null)
                                        likepostcnt = 0l;
                                    likepostcnt++;
                                    Database.getUserRoot().child(nowUser).child("likepost").child("cnt").setValue(likepostcnt);
                                    Database.getUserRoot().child(nowUser).child("likepost").child(likepostcnt + "").setValue(postnum);
                                }
                            });
                        }
                    }
                }
            });

            view.setOnClickListener(viw -> {
                String postNumber = String.valueOf(listData.get(getAdapterPosition()));
                mclickListener.mclickListener_Dialog(postNumber);
                Log.d("Dirtfy_test", postNumber);
            });
        }
    }
}