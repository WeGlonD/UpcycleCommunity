package com.uca.upcyclecommunity.community2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.uca.upcyclecommunity.R;
import com.uca.upcyclecommunity.ReportReason;
import com.uca.upcyclecommunity.database.Database;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class community2Adapter extends RecyclerView.Adapter<community2Adapter.MyViewHolder> {
    public static final String CATEGORY = "2";
    private ArrayList<Long> listData;
    private Context mContext;
    private community2Adapter.clickListener mclickListener;

    public interface clickListener {
        public void mclickListener_Dialog(String postNumber);
    }

    public community2Adapter(ArrayList<Long> listData, Context mContext, clickListener mclickListener) {
        this.listData = listData;
        this.mContext = mContext;
        this.mclickListener = mclickListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
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
        holder.mTitle.setSelected(true);
        holder.mTitle.setHorizontallyScrolling(true);
        String fUser = nowuser;
        Database.getDBRoot().child("Post" + CATEGORY).
                child("posting").child(String.valueOf(postNumber)).
                get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Iterable<DataSnapshot> postData = task.getResult().getChildren();
                        holder.mUriItems = new ArrayList<>();
                        holder.postPic_progressBar.setVisibility(View.VISIBLE);
                        holder.like_btn.setSelected(false);
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
                                holder.writerUID = value;
                                Database.getUserRoot().child(value).child("name").get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        String name = task1.getResult().getValue(String.class);
                                        holder.userName.setText(name);
                                        /*if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                                            if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(value)){
                                                holder.more.setVisibility(View.VISIBLE);
                                            }
                                        }*/
                                    } else {
                                        holder.userName.setText("error");
                                    }
                                });
                                Database.getUserProfileImageRoot().child(value).getDownloadUrl().addOnSuccessListener(uri -> {
                                    holder.userPic_progressBar.setVisibility(View.INVISIBLE);
                                    try{
                                        Glide.with(mContext).load(uri).into(holder.userPic);
                                    }
                                    catch (Exception e){

                                    }
                                });
                            } else if (key.equals("1")) {
                                String value = data.getValue(String.class);
                                holder.content_tv.setText(value);
                            }
                            else if(key.equals("latitude")||key.equals("longitude")||key.equals("recruit")){
                                //???????????? ????????????
                            } else {
                                String value = data.getValue(String.class);
                                Uri uri = Uri.parse(value);
                                holder.mUriItems.add(uri);
                                ImageView iv = new ImageView(mContext);   //????????? ?????? ????????? ??? ??????
                                iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                iv.setBackgroundResource(R.drawable.page_not);
                                //LinearLayout??? ??????

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
        holder.postviewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {   //???????????? ????????????, gallery??? listview??? onItemSelectedListener??? ??????
            //???????????? ????????? ????????????
            @Override public void onPageSelected(int position) {
                Log.d("minseok","onPageSelected called"+position);
                holder.mPageMark.getChildAt(holder.mPrePosition).setBackgroundResource(R.drawable.page_not);   //?????? ???????????? ???????????? ????????? ?????? ????????? ??????
                holder.mPageMark.getChildAt(position).setBackgroundResource(R.drawable.page_select);      //?????? ???????????? ???????????? ????????? ?????? ????????? ??????
                holder.mPrePosition = position;            //?????? ????????? ?????? ????????? ??????
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
        public ConstraintLayout profile_layout;
        public ImageView more;

        public String writerUID;

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
            more = view.findViewById(R.id.more);
            profile_layout = view.findViewById(R.id.community2_item_metaData_constraintLayout);
            profile_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(mContext, view);
                    popupMenu.inflate(R.menu.menu_report);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            FirebaseUser User = Database.getAuth().getCurrentUser();
                            if(User != null) {
                                String nowUser = User.getUid();
                                switch (item.getItemId()) {
                                    case R.id.reportpost:
                                        Toast.makeText(mContext, "Report Post clicked!", Toast.LENGTH_SHORT).show();
                                        Intent it = new Intent(mContext, ReportReason.class);
                                        it.putExtra("type", "POST");
                                        it.putExtra("reportpost", postnum + "");
                                        it.putExtra("category", CATEGORY);
                                        it.putExtra("NowUser", nowUser);
                                        mContext.startActivity(it);
                                        return true;
                                    case R.id.reportuser:
                                        Toast.makeText(mContext, "Report clicked!", Toast.LENGTH_SHORT).show();
                                        Intent it2 = new Intent(mContext, ReportReason.class);
                                        it2.putExtra("type", "USER");
                                        it2.putExtra("reportuser", writerUID);
                                        it2.putExtra("category", CATEGORY);
                                        it2.putExtra("NowUser", nowUser);
                                        mContext.startActivity(it2);
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                            else{
                                Toast.makeText(mContext, "????????? ?????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        }
                    });
                    if(!(Database.getAuth().getCurrentUser()!=null && Database.getAuth().getCurrentUser().getUid().equals(writerUID)))
                        popupMenu.show();
                }
            });

            comment_linearLayout = view.findViewById(R.id.community2_item_clickCnt_linearLayout);
            comment_linearLayout.setOnClickListener(viw -> {
                Long postNumber = listData.get(getAdapterPosition());
                //Toast.makeText(mContext, ""+postNumber, Toast.LENGTH_SHORT).show();
                String stringPostNumber = String.valueOf(postNumber);
                Log.d("Dirtfy_test", stringPostNumber);

                Intent it = new Intent(mContext, CommentsActivity.class);
                it.putExtra("postNumber", stringPostNumber);

                mContext.startActivity(it);
            });
            comment_iv = view.findViewById(R.id.community2_item_commentImage_imageView);
            comment_iv.setOnClickListener(viw -> {
                Long postNumber = listData.get(getAdapterPosition());
                //Toast.makeText(mContext, ""+postNumber, Toast.LENGTH_SHORT).show();
                String stringPostNumber = String.valueOf(postNumber);
                Log.d("Dirtfy_test", stringPostNumber);

                Intent it = new Intent(mContext, CommentsActivity.class);
                it.putExtra("postNumber", stringPostNumber);

                mContext.startActivity(it);
            });
            mComment = view.findViewById(R.id.community2_item_commentCnt_textView);
            mComment.setOnClickListener(viw -> {
                Long postNumber = listData.get(getAdapterPosition());
                //Toast.makeText(mContext, ""+postNumber, Toast.LENGTH_SHORT).show();
                String stringPostNumber = String.valueOf(postNumber);
                Log.d("Dirtfy_test", stringPostNumber);

                Intent it = new Intent(mContext, CommentsActivity.class);
                it.putExtra("postNumber", stringPostNumber);

                mContext.startActivity(it);
            });

            likeCnt_tv = view.findViewById(R.id.community2_item_likeCnt_textView);
            like_btn = view.findViewById(R.id.community2_item_likeImage_likeButton);
            like_btn.setSelected(false);

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
                    //Toast.makeText(mContext, "hi hi", Toast.LENGTH_SHORT).show();
                }
            });

            like_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button like = (Button) view;
                    if (Database.getAuth().getCurrentUser() == null)
                        Toast.makeText(mContext, "????????? ??? ???????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    else {
                        String nowUser = Database.getAuth().getCurrentUser().getUid();
                        if (like.isSelected()) {
                            likeCnt_tv.setText(String.valueOf(Integer.parseInt(likeCnt_tv.getText().toString())-1));
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
                            likeCnt_tv.setText(String.valueOf(Integer.parseInt(likeCnt_tv.getText().toString())+1));
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