package com.example.upcyclecommunity.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.upcyclecommunity.community1.TitleInfo;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import com.example.upcyclecommunity.R;

import java.sql.Array;
import java.sql.Timestamp;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class Database {
    public final Long FIRST_POSTNUM = Long.MAX_VALUE;
    private static DatabaseReference mDBRoot = null;
    private static DatabaseReference brandRoot = null;
    private static DatabaseReference postRoot = null;
    private static DatabaseReference titleRoot = null;
    private static DatabaseReference tagRoot = null;
    private static DatabaseReference commentRoot = null;
    private static DatabaseReference userRoot = null;
    private static DatabaseReference postingRoot = null;
    private static ValueEventListener userDataListener = null;

    private static StorageReference mStorage = null;
    private static StorageReference userProfileImageRoot = null;
    private static StorageReference brandpictureRoot = null;
    private static StorageReference postImageRoot = null;
    private static StorageReference postpictureRoot = null;

    private static FirebaseAuth mAuth = null;

    public static Context context;

    public static boolean getInstance(){
        try{
            if(mAuth == null)
                mAuth = FirebaseAuth.getInstance();
            if (mStorage == null)
                mStorage = FirebaseStorage.getInstance().getReference();
            else{
                if(brandpictureRoot == null)
                    brandpictureRoot = mStorage.child("Brand picture");
                if(userProfileImageRoot == null)
                    userProfileImageRoot = mStorage.child(context.getString(R.string.ST_userProfile));
                if(postpictureRoot == null)
                    postpictureRoot = mStorage.child("Post picture");
                if(postImageRoot == null)
                    postImageRoot = mStorage.child(context.getString(R.string.ST_posting));
            }
            if (mDBRoot == null)
                mDBRoot = FirebaseDatabase.getInstance().getReference();
            else{
                if (brandRoot == null)
                    brandRoot = mDBRoot.child("Brand");
                if (postRoot == null)
                    postRoot = mDBRoot.child("Post");
                if (titleRoot == null)
                    titleRoot = postRoot.child("Title");
                if (postingRoot == null)
                    postingRoot = postRoot.child(context.getString(R.string.DB_posting));
                if (tagRoot == null)
                    tagRoot = postRoot.child("Tag");
                if (commentRoot == null)
                    commentRoot = postRoot.child("Comment");
                if (userRoot == null)
                    userRoot = mDBRoot.child("User");
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Database() { getInstance(); }
    public Database(Context context){
        this.context = context;
        getInstance();
    }

    //데이터 바뀔때 쓰는 생성자
    public Database(ValueEventListener brand,
                    Context context){
        this.context = context;
        getInstance();
        brandRoot.addValueEventListener(brand);
    }

    public static DatabaseReference getDBRoot() {
        return mDBRoot;
    }

    public static DatabaseReference getUserRoot() {
        return userRoot;
    }

    public static DatabaseReference getPostRoot() {
        return postRoot;
    }

    public static StorageReference getStorage() {
        return mStorage;
    }

    public static StorageReference getUserProfileImageRoot() {
        return userProfileImageRoot;
    }

    public static StorageReference getPostImageRoot() {
        return postImageRoot;
    }

    public static StorageReference getBrandpictureRoot() {
        return brandpictureRoot;
    }

    public static StorageReference getPostpictureRoot(){
        return postpictureRoot;
    }

    public static FirebaseAuth getAuth() {
        return mAuth;
    }

    public static DatabaseReference getTitleRoot() {
        return titleRoot;
    }

    public static DatabaseReference getPostingRoot(){
        return postingRoot;
    }

    //리스너 제거(유저)
    public void removeUserValueEventListener(){
        userRoot.child(mAuth.getCurrentUser().getUid()).
                removeEventListener(userDataListener);
        userDataListener = null;
        Log.d(context.getString(R.string.Dirtfy_test), "removeUserValueEventListener end");
    }
    //리스너 붙이기(유저)
    public void setUserValueEventListener(Reacts reacts){
        if(userDataListener != null){
            removeUserValueEventListener();
        }
        userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reacts.ifDataChanged(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                reacts.ifCancelled(error);
            }
        };

        userRoot.child(mAuth.getCurrentUser().getUid()).
                addValueEventListener(userDataListener);
    }

    public void readAllBrand(ArrayList<Brand> returnList, BrandQuery con, Acts acts){
        String path = "firebase.Database.readAllBrand - ";

        brandRoot.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                    returnList.add(dataSnapshot.getValue(Brand.class));
                }
                acts.ifSuccess(task);
                Log.d("DB_Brand", path+"success");
            }
            else{
                acts.ifFail(task);
                Log.d("DB_Brand", path+"fail");
            }
        });
    }



    //데이터베이스 쓰기
    public boolean writeUser(User.Data data){
        String path = "firebase.Database.writeUser - ";
        try{
            User user = new User(context);
            userRoot.child(mAuth.getCurrentUser().getUid()).
                    setValue(data);
            Log.d(context.getString(R.string.Dirtfy_test), path+"success");
            return true;
        }
        catch (Exception e){
            Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
            return false;
        }
    }

    //데이터 베이스 읽기
    public boolean readUser(Acts acts){
        String path = "firebase.Database.readUser - ";

        try {
            User user = new User();

            userRoot.child(mAuth.getCurrentUser().getUid()).
                    get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            acts.ifSuccess(task);
                            Log.d(context.getString(R.string.Dirtfy_test), path+"success");
                        }
                        else{
                            acts.ifFail(task);
                            Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
                        }
                    });

            return true;
        }
        catch (Exception e){
            Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
            return false;
        }
    }

    public void writeImage(Uri file, StorageReference filePath, String fileName, Acts acts){
        String path = "firebase.Database.writeImage - ";

        StorageReference newFile = filePath.child(fileName);
        newFile.putFile(file).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                acts.ifSuccess(task);
                Log.d(context.getString(R.string.Dirtfy_test), path+"success");
            }
            else{
                acts.ifFail(task);
                Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
            }
        });
    }
    public void writeImage(BitmapDrawable bitmapDrawable, StorageReference filePath, String fileName, Acts acts){
        String path = "firebase.Database.writeImage - ";

        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference newFile = filePath.child(fileName);
        newFile.putBytes(data).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                acts.ifSuccess(task);
                Log.d(context.getString(R.string.Dirtfy_test), path+"success");
            }
            else{
                acts.ifFail(task);
                Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
            }
        });
    }
    public StorageReference readImage(StorageReference filePath, String name){
        return filePath.child(name);
    }

    public void setNewPostNumber(Acts acts){
        Log.d("WeGlonD", "1");
        postingRoot.child("totalnumber").get().addOnCompleteListener(task -> {
            Long postnum;
            if(task.isSuccessful()){
                postnum = task.getResult().getValue(Long.class);
                if(postnum == null)
                    postnum = FIRST_POSTNUM;
            }
            else{
                postnum = FIRST_POSTNUM;
            }
            postnum = postnum - Long.parseLong("1");
            Log.d("WeGlonD", ""+postnum);
            final Long finalPostnum = postnum;
            postingRoot.child("totalnumber").setValue(finalPostnum).addOnCompleteListener(task2 -> {

                postingRoot.child("totalnumber").get().addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful())
                        acts.ifSuccess(task1);
                });
            });
        });
    }

    public void writePostByLine(Long postnumber, Long lineNumber, String data, String title, String timestamp, ArrayList<String> tags, int cartegory){
        Log.d("WeGlonD", "2");
        final Long finalPostnum = postnumber;
        DatabaseReference currentPosting = postingRoot.child(finalPostnum+"");
        if (lineNumber == 1) {
            //postRoot.child("totalnumber").child(""+postnumber).child("title").setValue(title);
            currentPosting.child("0").setValue(title);
            currentPosting.child("comment").child("cnt").setValue(Long.parseLong("0"));
            currentPosting.child("clickcnt").setValue(Long.parseLong("0"));
            currentPosting.child("writer").setValue(mAuth.getCurrentUser().getUid());
            currentPosting.child("timestamp").setValue(timestamp);
            userRoot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("post"+cartegory).child("cnt").get().addOnCompleteListener(task -> {
                Long hasPostcnt;
                if(task.isSuccessful()){
                    hasPostcnt = task.getResult().getValue(Long.class);
                    if(hasPostcnt == null)
                        hasPostcnt = Long.parseLong("0");
                }
                else{
                    hasPostcnt = Long.parseLong("0");
                }
                hasPostcnt++;
                userRoot.child(mAuth.getCurrentUser().getUid()).child("post"+cartegory).child("cnt").setValue(hasPostcnt);
                userRoot.child(mAuth.getCurrentUser().getUid()).child("post"+cartegory).child(hasPostcnt+"").setValue(finalPostnum);
            });
            titleRoot.child(title).child("cnt").get().addOnCompleteListener(task -> {
                Long sameTitlecnt;
                if(task.isSuccessful()){
                    sameTitlecnt = task.getResult().getValue(Long.class);
                    if(sameTitlecnt==null){
                        sameTitlecnt = Long.parseLong("0");
                    }
                }
                else{
                    sameTitlecnt = Long.parseLong("0");
                }
                sameTitlecnt++;
                titleRoot.child(title).child("cnt").setValue(sameTitlecnt);
                titleRoot.child(title).child(sameTitlecnt+"").setValue(finalPostnum);
            });
            String resultTagStr = "";
            for(String str : tags){
                postRoot.child("Tag").child(str).child("cnt").get().addOnCompleteListener(task -> {
                    Long cnt;
                    if (task.isSuccessful()){
                        cnt = task.getResult().getValue(Long.class);
                        if(cnt == null)
                            cnt = Long.parseLong("0");
                    }
                    else{
                        cnt = Long.parseLong("0");
                    }
                    cnt = cnt + 1;
                    tagRoot.child(str).child("cnt").setValue(cnt);
                    postRoot.child("Tag").child(str).child(cnt+"").setValue(finalPostnum);
                });
                resultTagStr += "#"+str+" ";
            }
            currentPosting.child("tags").setValue(resultTagStr);
        }

        currentPosting.child(lineNumber + "").setValue(data).addOnCompleteListener(tsk ->{
            if(tsk.isSuccessful())
                Log.d("WeGlonD", "setValue success");
            else
                Log.d("WeGlond", "setValue fail");
        });
    }
    public void writePost(ArrayList<String> data,String title,ArrayList<String> tags){
        postRoot.child("totalnumber").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Long postnumber = task.getResult().getValue(Long.class);
                postnumber++;

                for(int i = 1;i<=data.size();i++) {
                    postRoot.child(String.valueOf(postnumber)).child(i+"").setValue(data.get(i-1));
                }

                titleRoot.child(title).setValue(postnumber);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-");
                String time = sdf.format(new Timestamp(System.currentTimeMillis()));

                for(int i = 0;i<tags.size();i++) {
                    tagRoot.child(tags.get(i)).setValue(postnumber);
                }

                postRoot.child("totalnumber").setValue(postnumber);
            }
            else{
                Toast.makeText(context, "실패!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deletePost(Long postnum, String writerUid, Acts acts){
        postingRoot.child(""+postnum).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                String title = null;
                ArrayList<String> tags = null;
                long contentsCnt = task.getResult().getChildrenCount() - 6;
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    if (dataSnapshot.getKey().equals("0")) {
                        title = dataSnapshot.getValue(String.class);
                    } else if (dataSnapshot.getKey().equals("tags")) {
                        String tagstr = dataSnapshot.getValue(String.class);
                        tags = new ArrayList<>(Arrays.asList(tagstr.split("#")));
                        for (int i = 0; i < tags.size(); i++) {
                            tags.add(tags.get(0).trim());
                            tags.remove(0);
                        }
                        tags.remove(0);
                    }
                }
                if(title!=null) {
                    titleRoot.child(title).removeValue();
                }
                if(tags!=null) {
                    for (String tag : tags) {
                        Log.d("WeGlonD", "tag : " + tag);
                        tagRoot.child(tag).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                for (DataSnapshot dataSnapshot : task1.getResult().getChildren()) {
                                    Log.d("WeGlonD", dataSnapshot.toString() + "asdf");
                                    if (postnum.equals(dataSnapshot.getValue(Long.class)) && !dataSnapshot.getKey().equals("cnt")) {
                                        String removeKey = dataSnapshot.getKey();
                                        tagRoot.child(tag).child(removeKey).removeValue();
                                    }
                                }
                            }
                        });
                    }
                }
                userRoot.child(writerUid).child("post1").get().addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        for(DataSnapshot dataSnapshot : task1.getResult().getChildren()){
                            if(postnum.equals(dataSnapshot.getValue(Long.class))&&!dataSnapshot.getKey().equals("cnt")){
                                String removeKey = dataSnapshot.getKey();
                                userRoot.child(writerUid).child("post1").child(removeKey).removeValue();
                            }
                        }
                    }
                });
                for(int i = 1; i <= contentsCnt; i++){
                    if(i%2==0){
                        postpictureRoot.child(postnum+title+i).delete();
                    }
                }
                commentRoot.child(postnum+"").removeValue();
                postingRoot.child(postnum+"").removeValue();
            }
        });
    }

//    public void readAllPost(ArrayList<TitleInfo> returnList, Acts acts){
//    public void readAllPost(ArrayList<TitleInfo> returnList, Acts acts){
//        String path = "firebase.Database.readAllPost - ";
//
//        titleRoot.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()){
//                for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
//                    String title = dataSnapshot.getKey();
//                    Long postn = dataSnapshot.getValue(Long.class);
//                    commentRoot.child(""+postn).child("commentcnt").get().addOnCompleteListener(task1 -> {
//                        if(task1.isSuccessful()){
//                            // null이어도 Successful
//                            Long cmt = task1.getResult().getValue(Long.class);
//                            if(cmt == null){
//                                cmt = new Long(0);
//                            }
//                            TitleInfo titleinfo = new TitleInfo(title,cmt);
//                            acts.ifSuccess(task);
//                            returnList.add(titleinfo);
//                        }
//                        else{
//                            acts.ifFail(task);
//                            return;
//                        }
//                    });
//                }
//            }
//            else{
//                return;
//            }
//        });
//    }

    public void readAllPost(ArrayList<Long> returnList, Acts acts){
        String path = "firebase.Database.readAllPost - ";

        postRoot.child("posting").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()){
                    if(!(dataSnapshot.getKey().equals("totalnumber"))){
                        Long postNumber = Long.parseLong(dataSnapshot.getKey());
                        returnList.add(postNumber);
                    }
                }
                acts.ifSuccess(task);
            }
            else{
                acts.ifFail(task);
            }
        });
    }

    public void readName(ArrayList<Long> returnList, String keyword, Acts acts){
        String path = "firebase.Database.readAllPost - ";
        Log.d("minseok", "readName called");
        titleRoot.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                    String post_title = dataSnapshot.getKey();
                    Log.d("minseok", "readName - post_title : "+post_title);
                    Log.d("minseok", "readName - keyword : "+keyword);
                    if(post_title.contains(keyword)){
                        Log.d("minseok", "readName - contains True");
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            Log.d("minseok", "readName - datasnapshot1 Key : "+dataSnapshot1.getKey());
                            if(!dataSnapshot1.getKey().equals("cnt")){
                                returnList.add(dataSnapshot1.getValue(Long.class));
                                Log.d("minseok", "readName - datasnapshot1 Key NOT EQUALS cnt");
                            }
                        }
                    }
                }
                acts.ifSuccess(task);
            }
            else{
                acts.ifFail(task);
                return;
            }
        });
    }

    public void readTag(ArrayList<Long> returnList, String keyword, Acts acts){
        String path = "firebase.Database.readAllPost - ";
        tagRoot.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                    String post_tag = dataSnapshot.getKey();
                    if(post_tag.contains(keyword)){
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            if(!dataSnapshot1.getKey().equals("cnt")){
                                Long tmp = dataSnapshot1.getValue(Long.class);
                                if(!returnList.contains(tmp))
                                    returnList.add(tmp);
                            }
                        }
                    }
                }
                acts.ifSuccess(task);
            }
            else{
                acts.ifFail(task);
                return;
            }
        });
    }

//    public void readPost(ArrayList<Post> returnList, String title, PostQuery con, Acts acts){
//        String path = "firebase.Database.readPost - ";
//
//        titleRoot.child(title).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()){
//                Long postnumber1 = Long.parseLong(task.getResult().getValue(String.class));
//
//                postRoot.child(String.valueOf(postnumber1)).get().addOnCompleteListener(task1->{
//
//                    for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
//                        returnList.add(dataSnapshot.getValue(Post.class));
//                    }
//                    acts.ifSuccess(task);
//                    Log.d("DB_Post", path+"success");
//                });
//            }
//            else{
//                acts.ifFail(task);
//                Log.d("DB_Post", path+"fail");
//            }
//        });
//    }

    public void readOnePost(ArrayList<Post> returnList, Long postNumber, Acts acts){
        String path = "firebase.Database.readPost - ";
        postingRoot.child(String.valueOf(postNumber)).
                get().addOnCompleteListener(task -> {
                    String Title="";
                    ArrayList<String> Content = new ArrayList<>();
                    String Tag = "";
                    String User_Id = "";
                    String timeStamp = "";
                    Long clickCnt = 0L;

                    if (task.isSuccessful()){
                        for(DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                            if (!(dataSnapshot.getKey().equals("comment") || dataSnapshot.getKey().equals("clickcnt"))) {
                                String key = dataSnapshot.getKey();
                                String value = dataSnapshot.getValue(String.class);
                                if (key.equals("0")) {
                                    Title = value;
                                } else if (key.equals("tags")) {
                                    Tag = value;
                                } else if (key.equals("writer")) {
                                    User_Id = value;
                                } else if (key.equals("timestamp")){
                                    timeStamp = value;
                                } else {
                                    Content.add(value);
                                }
                            } else if (dataSnapshot.getKey().equals("clickcnt")){
                                clickCnt = dataSnapshot.getValue(Long.class);
                            }
                        }
                        Post newpost = new Post(Title,Content,Tag,User_Id, timeStamp, clickCnt);
                        returnList.add(newpost);
                        acts.ifSuccess(task);
                        Log.d("Personal_Post", path+"success");
                    }
                    else{
                        acts.ifFail(task);
                        Log.d("Personal_Post", path+"fail");
                    }
                });
    }

    public void readOnePostLine(Long postNumber, Long lineNumber, Acts acts){
        String path = "firebase.Database.readPost - ";

        postingRoot.child(String.valueOf(postNumber)).
                child(String.valueOf(lineNumber)).
                get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        acts.ifSuccess(task);
                        Log.d("DB_Post", path+"success");
                    }
                    else{
                        acts.ifFail(task);
                        Log.d("DB_Post", path+"fail");
                    }
                });
    }

    public void readAllUserPost1(ArrayList<Long> returnList, Acts acts){
        userRoot.child(mAuth.getCurrentUser().getUid()).
                child(context.getString(R.string.DB_user_posting1)).
                get().addOnCompleteListener(task -> {

                    if(task.isSuccessful()){
                        for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                            if(!(dataSnapshot.getKey().equals("cnt"))){
                                returnList.add(dataSnapshot.getValue(Long.class));
                            }
                        }
                        acts.ifSuccess(task);
                    }
                    else{
                        acts.ifFail(task);
                    }
                });
    }
    public void readAllUserPost2(ArrayList<Long> returnList, Acts acts){
        userRoot.child(mAuth.getCurrentUser().getUid()).
                child(context.getString(R.string.DB_user_posting2)).
                get().addOnCompleteListener(task -> {

                    if(task.isSuccessful()){
                        for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                            if(!(dataSnapshot.getKey().equals("cnt"))){
                                returnList.add(dataSnapshot.getValue(Long.class));
                            }
                        }
                        acts.ifSuccess(task);
                    }
                    else{
                        acts.ifFail(task);
                    }
                });
    }

    public void writeComment(Long postnumber, String text){
        commentRoot.child(""+postnumber).child("commentcnt").get().addOnCompleteListener(task -> {
            Long commentNum;
            if(task.isSuccessful()){
                commentNum = task.getResult().getValue(Long.class);
            }
            else{
                commentNum = Long.parseLong("0");
            }
            commentNum++;
            commentRoot.child(""+postnumber).child("commentcnt").setValue(commentNum);
            DatabaseReference currentComment = commentRoot.child(""+postnumber).child(commentNum+"");
            currentComment.child("userId").setValue(mAuth.getCurrentUser().getUid());
            currentComment.child("text").setValue(text);
        });
    }

    public void readComment(ArrayList<Comment> data, Long postnum, Acts acts){
        commentRoot.child(postnum+"").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(task.getResult().getChildren()!=null) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        if (!dataSnapshot.getKey().equals("commentcnt")) {
                            Comment tmp = new Comment(dataSnapshot.child("text").getValue(String.class),dataSnapshot.child("userId").getValue(String.class));
                            Log.d("minseok_1", dataSnapshot.child("text").getValue(String.class));
                            Log.d("minseok_1",dataSnapshot.child("userId").getValue(String.class));
                            data.add(tmp);
                        }
                    }
                    acts.ifSuccess(task);
                }
                else
                    acts.ifFail(task);
            }
            else
                acts.ifFail(task);
        });
    }
}
