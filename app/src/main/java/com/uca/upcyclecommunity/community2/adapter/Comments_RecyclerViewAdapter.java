package com.uca.upcyclecommunity.community2.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uca.upcyclecommunity.R;
import com.uca.upcyclecommunity.ReportReason;
import com.uca.upcyclecommunity.community1.CommentView;
import com.uca.upcyclecommunity.database.Database;

import java.util.ArrayList;

public class Comments_RecyclerViewAdapter extends RecyclerView.Adapter<Comments_RecyclerViewAdapter.Comments_ViewHolder> {
    public static final String CATEGORY = "2";
    private String stringOfPostNumber;
    private ArrayList<Long> listData;
    private Context context;
    private RequestManager mGlideRequestManager;

    public Comments_RecyclerViewAdapter(String stringOfPostNumber, ArrayList<Long> listData, Context context, RequestManager mGlideRequestManager){
        this.stringOfPostNumber = stringOfPostNumber;
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
    public Comments_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_comments_recyclerview_item, parent, false);
        return new Comments_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Comments_ViewHolder holder, int position) {
        String commentNumber = String.valueOf(listData.get(position));
        Database.getDBRoot().child("Post2").child("posting").
                child(stringOfPostNumber).child("comment").
                child(commentNumber).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if(task != null){
                    String text = task.getResult().child("text").getValue(String.class);
                    String user_uid = task.getResult().child("writer").getValue(String.class);

                    holder.setWriterUid(user_uid);
                    holder.setCommentNum(Long.valueOf(commentNumber));

                    holder.comment_tv.setText(text);
                    Database.getUserRoot().child(user_uid).
                            child("name").get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            String userName = task1.getResult().getValue(String.class);
                            holder.userName_tv.setText(userName);
                        }
                        else{
                            Toast.makeText(context, "load user name error", Toast.LENGTH_SHORT).show();
                            holder.userName_tv.setText("???");
                        }
                    });
                    Database.getUserProfileImageRoot().child(user_uid).getDownloadUrl().addOnSuccessListener(uri -> {
                       if (uri != null){
                           holder.progressBar.setVisibility(View.INVISIBLE);
                           mGlideRequestManager.load(uri).into(holder.profile_iv);
                       }
                       else{
                           Toast.makeText(context, "load user image error", Toast.LENGTH_SHORT).show();
                           holder.profile_iv.setImageResource(R.drawable.ic_baseline_person_24);
                       }
                    });
                }
            }
            else{
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.comment_tv.setText("??error??");
                holder.profile_iv.setImageResource(R.drawable.ic_baseline_person_24);
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

    class Comments_ViewHolder extends RecyclerView.ViewHolder{
        ImageView profile_iv;
        TextView userName_tv;
        ProgressBar progressBar;
        TextView comment_tv;

        String writerUid;
        Long commentNum;

        public void setWriterUid(String writerUid) {
            this.writerUid = writerUid;
        }

        public void setCommentNum(Long commentNum) {
            this.commentNum = commentNum;
        }

        public Comments_ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_iv = itemView.findViewById(R.id.activity_comments_recyclerview_item_profile_imageView);
            userName_tv = itemView.findViewById(R.id.activity_comments_recyclerview_item_userName_textView);
            progressBar = itemView.findViewById(R.id.activity_comments_recyclerview_item_progressBar);
            comment_tv = itemView.findViewById(R.id.activity_comments_recyclerview_item_comment_textView);

            itemView.setOnClickListener(view -> {
                if(writerUid == null || commentNum == null)
                    return;

                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.menu_report);
                popupMenu.setOnMenuItemClickListener(item -> {
                    Long commentNum = listData.get(getAdapterPosition());
                    Intent it = new Intent(context, ReportReason.class);
                    FirebaseUser User = Database.getAuth().getCurrentUser();
                    if(User!=null) {
                        switch (item.getItemId()) {
                            case R.id.reportpost:
                                it.putExtra("type", "COMMENT");
                                it.putExtra("reportpost", stringOfPostNumber);
                                it.putExtra("reportCommentNum", commentNum + "");
                                it.putExtra("category", CATEGORY);
                                it.putExtra("NowUser", User.getUid());
                                context.startActivity(it);
                                return true;
                            case R.id.reportuser:
                                it.putExtra("type", "USER");
                                it.putExtra("reportuser", writerUid);
                                it.putExtra("NowUser", User.getUid());
                                context.startActivity(it);
                                return true;
                            default:
                                return false;
                        }
                    }
                    else{
                        Toast.makeText(context, "로그인 이후 신고할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                popupMenu.show();
            });

            itemView.setOnLongClickListener(view -> {
                if(FirebaseAuth.getInstance().getCurrentUser() == null)
                    return false;
                if(!writerUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    return false;

                DialogInterface.OnClickListener deleteListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Database db = new Database();
                        int pos = getAdapterPosition();
                        db.deleteComment(Long.valueOf(stringOfPostNumber), listData.get(pos)+"", CATEGORY);
                        listData.remove(pos);
                        notifyItemRemoved(pos);
                    }
                };
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };
                new AlertDialog.Builder(context)
                        .setTitle("댓글 작업 선택")
                        .setPositiveButton("댓글 삭제", deleteListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();

                return true;
            });


        }
    }
}
