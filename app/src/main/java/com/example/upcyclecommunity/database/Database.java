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
    private static DatabaseReference post1Root = null;
    private static DatabaseReference post2Root = null;
    private static DatabaseReference userRoot = null;
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
                if (post1Root == null)
                    post1Root = mDBRoot.child("Post1");
                if (post2Root == null)
                    post2Root = mDBRoot.child("Post2");
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

    public static DatabaseReference getPost1Root() {
        return post1Root;
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

    public void setNewPostNumber(String category, Acts acts){
        DatabaseReference postingRoot = mDBRoot.child("Post"+category).child("posting");
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

    public void writePostByLine(Long postnumber, Long lineNumber, String data, String title, String timestamp, ArrayList<String> tags, String category){
        DatabaseReference postRoot = mDBRoot.child("Post"+category);
        DatabaseReference postingRoot = postRoot.child("posting");
        DatabaseReference titleRoot = postRoot.child("Title");

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
            userRoot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("post"+category).child("cnt").get().addOnCompleteListener(task -> {
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
                userRoot.child(mAuth.getCurrentUser().getUid()).child("post"+category).child("cnt").setValue(hasPostcnt);
                userRoot.child(mAuth.getCurrentUser().getUid()).child("post"+category).child(hasPostcnt+"").setValue(finalPostnum);
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
                    postRoot.child("Tag").child(str).child("cnt").setValue(cnt);
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

    public void deletePost(Long postnum, String writerUid, String category, Acts acts){
        DatabaseReference postRoot = mDBRoot.child("Post"+category);
        DatabaseReference postingRoot = postRoot.child("posting");
        DatabaseReference titleRoot = postRoot.child("Title");
        DatabaseReference tagRoot = postRoot.child("Tag");
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
                userRoot.child(writerUid).child("post"+category).get().addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        for(DataSnapshot dataSnapshot : task1.getResult().getChildren()){
                            if(postnum.equals(dataSnapshot.getValue(Long.class))&&!dataSnapshot.getKey().equals("cnt")){
                                String removeKey = dataSnapshot.getKey();
                                userRoot.child(writerUid).child("post"+category).child(removeKey).removeValue();
                            }
                        }
                    }
                });
                for(int i = 1; i <= contentsCnt; i++){
                    if(i%2==0){
                        postpictureRoot.child(postnum+title+i).delete();
                    }
                }
                postingRoot.child(postnum+"").removeValue();
            }
        });
    }

    public void readAllPost(ArrayList<Long> returnList, String category, Acts acts){
        String path = "firebase.Database.readAllPost - ";

        DatabaseReference postRoot = mDBRoot.child("Post"+category);

        postRoot.child("posting").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()){
                    if(!(dataSnapshot.getKey().equals("totalnumber"))){
                        Long postNumber = Long.parseLong(dataSnapshot.getKey());
                        Log.d("WeGlonD", postNumber+"");
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

    public void readName(ArrayList<Long> returnList, String keyword, String category, Acts acts){
        String path = "firebase.Database.readAllPost - ";
        Log.d("minseok", "readName called");

        DatabaseReference postRoot = mDBRoot.child("Post"+category);
        DatabaseReference titleRoot = postRoot.child("Title");

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

    public void readTag(ArrayList<Long> returnList, String keyword, String category, Acts acts){
        String path = "firebase.Database.readAllPost - ";

        DatabaseReference postRoot = mDBRoot.child("Post"+category);
        DatabaseReference tagRoot = postRoot.child("Tag");

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

    public void readOnePost(ArrayList<Post> returnList, Long postNumber, String category, Acts acts){
        String path = "firebase.Database.readPost - ";

        DatabaseReference postRoot = mDBRoot.child("Post"+category);
        DatabaseReference postingRoot = postRoot.child("posting");

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
                                clickCnt = dataSnapshot.getValue(Long.class) + 1;
                                postingRoot.child(String.valueOf(postNumber)).child("clickcnt").setValue(clickCnt);
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

    public void readOnePostLine(Long postNumber, Long lineNumber, String category, Acts acts){
        String path = "firebase.Database.readPost - ";

        DatabaseReference postRoot = mDBRoot.child("Post"+category);
        DatabaseReference postingRoot = postRoot.child("posting");

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

    public void writeComment(Long postnumber, String text, String category){
        DatabaseReference postRoot = mDBRoot.child("Post"+category);
        DatabaseReference postingRoot = postRoot.child("posting");

        DatabaseReference currPosting = postingRoot.child(postnumber+"");
        currPosting.child("comment").child("cnt").get().addOnCompleteListener(task -> {
            Long commentNum;
            if(task.isSuccessful()){
                commentNum = task.getResult().getValue(Long.class);

            }
            else{
                commentNum = Long.parseLong("0");
            }
            commentNum++;
            currPosting.child("comment").child("cnt").setValue(commentNum);
            DatabaseReference currentComment = currPosting.child("comment").child(commentNum+"");
            currentComment.child("writer").setValue(mAuth.getCurrentUser().getUid());
            currentComment.child("text").setValue(text);
        });
    }

    public void readComment(ArrayList<Comment> data, Long postnum, String category, Acts acts){
        DatabaseReference postRoot = mDBRoot.child("Post"+category);
        DatabaseReference postingRoot = postRoot.child("posting");

        DatabaseReference currPosting = postingRoot.child(postnum+"");
        currPosting.child("comment").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(task.getResult().getChildrenCount() > 1) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        if (!dataSnapshot.getKey().equals("cnt")) {
                            Comment tmp = new Comment(dataSnapshot.child("text").getValue(String.class),dataSnapshot.child("writer").getValue(String.class));
                            Log.d("WeGlonD", dataSnapshot.child("text").getValue(String.class));
                            Log.d("WeGlonD", dataSnapshot.child("writer").getValue(String.class));
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
