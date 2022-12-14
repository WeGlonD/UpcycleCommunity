package com.uca.upcyclecommunity.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uca.upcyclecommunity.MainActivity;
import com.uca.upcyclecommunity.community1.Fragment_CM1;
import com.uca.upcyclecommunity.community2.Fragment_CM2;
import com.uca.upcyclecommunity.community2.community2_upload;
import com.uca.upcyclecommunity.recruit.recruit_list;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.uca.upcyclecommunity.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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

    //????????? ????????? ?????? ?????????
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

    //????????? ??????(??????)
    public void removeUserValueEventListener(){
        userRoot.child(mAuth.getCurrentUser().getUid()).
                removeEventListener(userDataListener);
        userDataListener = null;
        Log.d(context.getString(R.string.Dirtfy_test), "removeUserValueEventListener end");
    }
    //????????? ?????????(??????)
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



    //?????????????????? ??????
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

    //????????? ????????? ??????
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
            currentPosting.child("comment").child("cnt").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Long commentCnt = task.getResult().getValue(Long.class);
                    if(commentCnt==null){
                        currentPosting.child("comment").child("cnt").setValue(Long.parseLong("0"));
                    }
                }
            });
            double latitude = MainActivity.location.getLatitude();
            double longitude = MainActivity.location.getLongitude();
            currentPosting.child("latitude").setValue(latitude);
            currentPosting.child("longitude").setValue(longitude);
            Log.d("WeGlonD", "setValue - latitude, longitude = " + latitude + ", " + longitude);
            if(category.equals("1")) {
                currentPosting.child("clickcnt").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Long clickCnt = task.getResult().getValue(Long.class);
                        if (clickCnt == null) {
                            currentPosting.child("clickcnt").setValue(Long.parseLong("0"));
                        }
                    }
                });
            }
            else if (category.equals("2")){
                currentPosting.child("likeuser").child("cnt").get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Long likeKeyMax = task.getResult().getValue(Long.class);
                        if(likeKeyMax==null){
                            currentPosting.child("likeuser").child("cnt").setValue(Long.parseLong("0"));
                        }
                    }
                });
            }
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

    public void deletePostForUpdate(Long postnum, String preTitle, String preTagStr, String userUid, int imageCnt,String category){
        DatabaseReference postRoot = mDBRoot.child("Post"+category);
        DatabaseReference titleRoot = postRoot.child("Title");
        DatabaseReference tagRoot = postRoot.child("Tag");

        titleRoot.child(preTitle).get().addOnCompleteListener(task1 -> {
            if(task1.isSuccessful()) {
                for (DataSnapshot dataSnapshot : task1.getResult().getChildren()) {
                    if (postnum.equals(dataSnapshot.getValue(Long.class)) && !dataSnapshot.getKey().equals("cnt")) {
                        String removeKey = dataSnapshot.getKey();
                        titleRoot.child(preTitle).child(removeKey).removeValue();
                        Log.d("WeGlonD", "deletePostForUpdate - title ??????");
                        break;
                    }
                }
            }
        });

        userRoot.child(userUid).child("post"+category).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                    if(postnum.equals(dataSnapshot.getValue(Long.class)) && !dataSnapshot.getKey().equals("cnt")){
                        String removeKey = dataSnapshot.getKey();
                        userRoot.child(userUid).child("post"+category).child(removeKey).removeValue();
                        Log.d("WeGlonD", "deletePostForUpdate - user - postnum ??????");
                    }
                }
            }
        });

        if(preTagStr!=null) {
            ArrayList<String> preTags = new ArrayList<>(Arrays.asList(preTagStr.split("#")));
            for (int i = 0; i < preTags.size(); i++) {
                String str = preTags.get(0);
                str = str.trim();
                preTags.add(str);
                preTags.remove(0);
            }
            preTags.remove(0);
            for (String str : preTags) {
                Log.d("WeGlonD", "preTags - " + str);
                tagRoot.child(str).get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        for (DataSnapshot dataSnapshot : task1.getResult().getChildren()) {
                            if (postnum.equals(dataSnapshot.getValue()) && !dataSnapshot.getKey().equals("cnt")) {
                                String removeKey = dataSnapshot.getKey();
                                tagRoot.child(str).child(removeKey).removeValue();
                                Log.d("WeGlonD", "deletePostForUpdate - Existing Tag Found - " + str + " " + removeKey + " " + dataSnapshot.getValue(Long.class));
                                break;
                            }
                        }
                    }
                });
            }
        }
        for(int i = 1; i <= imageCnt; i++) {
            postpictureRoot.child("Post"+category).child(category + "-" + postnum + "-" + preTitle + "-" + i*2).delete();
            Log.d("WeGlonD", "deletePostForUpdate - image Found - " + category + "-" + postnum + "-" + preTitle + "-" + i*2);
        }
    }

    public void deletePost(Long postnum, String writerUid, String category, String recruitNum, Acts acts){
        DatabaseReference postRoot = mDBRoot.child("Post"+category);
        DatabaseReference postingRoot = postRoot.child("posting");
        DatabaseReference titleRoot = postRoot.child("Title");
        DatabaseReference tagRoot = postRoot.child("Tag");

        if(category.equals("3")){
            mDBRoot.child("Post2").child("posting").child(recruitNum).child("recruit").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                        if(!dataSnapshot.getKey().equals("cnt")&&dataSnapshot.getValue(Long.class).equals(postnum)){
                            String removeKey = dataSnapshot.getKey();
                            mDBRoot.child("Post2").child("posting").child(recruitNum).child("recruit").child(removeKey).removeValue();
                        }
                    }
                }
            });
        }

        postingRoot.child(""+postnum).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                String title = null;
                ArrayList<String> tags = null;
                int subtract = 0;
                if(category.equals("1"))
                    subtract = 8;
                else if(category.equals("2"))
                    subtract = 9;
                else if(category.equals("3"))
                    subtract = 8;
                long contentsCnt = task.getResult().getChildrenCount() - subtract;
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
                    String fTitle = title;
                    ArrayList<String> fTags = tags;
                    if(!title.equals("")){
                        Log.d("minseok","deletepost2"+title);
                        titleRoot.child(title).get().addOnCompleteListener(task1 -> {
                            for(DataSnapshot dataSnapshot : task1.getResult().getChildren()) {
                                Log.d("minseok", "datasnapshotpostnum" + dataSnapshot.getValue(Long.class));
                                Log.d("minseok", "postnum" + postnum);
                                if (postnum.equals(dataSnapshot.getValue(Long.class)) && !dataSnapshot.getKey().equals("cnt")) {
                                    titleRoot.child(fTitle).child(dataSnapshot.getKey()).removeValue();
                                    break;
                                }
                            }
                            for (String tag : fTags) {
                                Log.d("minseok", "tag : " + tag);
                                tagRoot.child(tag).get().addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        for (DataSnapshot dataSnapshot : task2.getResult().getChildren()) {
                                            Log.d("WeGlonD", dataSnapshot.toString() + "asdf");
                                            if (postnum.equals(dataSnapshot.getValue(Long.class)) && !dataSnapshot.getKey().equals("cnt")) {
                                                String removeKey = dataSnapshot.getKey();
                                                tagRoot.child(tag).child(removeKey).removeValue();
                                                break;
                                            }
                                        }

                                    }
                                });
                            }
                            userRoot.child(writerUid).child("post"+category).get().addOnCompleteListener(task3 -> {
                                if(task3.isSuccessful()){
                                    for(DataSnapshot dataSnapshot : task3.getResult().getChildren()){
                                        if(postnum.equals(dataSnapshot.getValue(Long.class))&&!dataSnapshot.getKey().equals("cnt")){
                                            String removeKey = dataSnapshot.getKey();
                                            userRoot.child(writerUid).child("post"+category).child(removeKey).removeValue();
                                            Log.d("WeGlonD", "user post del - " + removeKey + " " + dataSnapshot.getValue(Long.class));
                                        }
                                    }

                                    for(int i = 1; i <= contentsCnt; i++){
                                        if((category.equals("1")||category.equals("3")) && i%2==0){
                                            postpictureRoot.child("Post"+category).child(category + "-" + postnum + "-" + fTitle + "-" +i).delete();
                                            Log.d("WeGlonD", "picture del - "+category + "-" + postnum + "-" + fTitle + "-" +i);
                                        }
                                        else if(category.equals("2") && i >= 2){
                                            postpictureRoot.child("Post"+category).child(category + "-" + postnum + "-" + fTitle + "-" +i).delete();
                                            Log.d("WeGlonD", "picture del - "+category + "-" + postnum + "-" + fTitle + "-" +i);
                                        }
                                    }
                                    postingRoot.child(""+postnum).child("likeuser").removeValue();
                                    postingRoot.child(""+postnum).child("comment").removeValue();
                                    postingRoot.child(""+postnum).child("recruit").removeValue();
                                    postingRoot.child(postnum+"").removeValue();
                                    Log.d("WeGlonD", "Database - deletePost - ????????????");
                                    acts.ifSuccess(task);
                                }
                            });

                        });
                    }
                }

            }
        });
    }

    public void readAllPost(ArrayList<Long> returnList, String category, Acts acts){
        String path = "firebase.Database.readAllPost - ";
        DatabaseReference postRoot = mDBRoot.child("Post"+category);

        ArrayList<Long> bannedPost = new ArrayList<>();
        ArrayList<String> bannedUser = new ArrayList<>();
        String userUID = null;
        if(mAuth.getCurrentUser()!=null){
            userUID = mAuth.getCurrentUser().getUid();
            String finalUserUID = userUID;
            userRoot.child(userUID).child("reportpost"+category).get().addOnCompleteListener(task2 -> {
                if(task2.isSuccessful()){
                    for(DataSnapshot dataSnapshot : task2.getResult().getChildren()){
                        if(!(dataSnapshot.getKey().equals("cnt"))){
                            Long bannedNumber = dataSnapshot.getValue(Long.class);
                            Log.d("WeGlonD", "Banned Post : " + bannedNumber);
                            bannedPost.add(bannedNumber);
                        }
                    }
                    userRoot.child(finalUserUID).child("reportuser").get().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            for(DataSnapshot dataSnapshot : task1.getResult().getChildren()){
                                if(!(dataSnapshot.getKey().equals("cnt"))){
                                    String bannedUserId = dataSnapshot.getValue(String.class);
                                    Log.d("WeGlonD", "Banned User : " + bannedUserId);
                                    bannedUser.add(bannedUserId);
                                }
                            }

                            //????????? ?????? ??????
                            postRoot.child("posting").get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()){
                                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()){
                                        if(!(dataSnapshot.getKey().equals("totalnumber"))){
                                            if(!(bannedUser.contains(dataSnapshot.child("writer").getValue(String.class)) ||
                                                    bannedPost.contains(Long.parseLong(dataSnapshot.getKey())))) {
                                                Long postNumber = Long.parseLong(dataSnapshot.getKey());
                                                Log.d("WeGlonD", postNumber + "");
                                                returnList.add(postNumber);
                                            }
                                        }
                                        acts.ifSuccess(task);
                                        if (category.equals("1")){
                                            Fragment_CM1.isUpdating = false;
                                        }
                                        if (category.equals("2")){
                                            Fragment_CM2.isUpdating = false;
                                        }
                                        if (category.equals("3")){
                                            recruit_list.recruit_isUpdating = false;
                                        }
                                    }
                                }
                                else{
                                    acts.ifFail(task);
                                }
                            });

                        }
                    });
                }
            });
        }
        else{
            postRoot.child("posting").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()){
                        if(!(dataSnapshot.getKey().equals("totalnumber"))){
                            if(!(bannedUser.contains(dataSnapshot.child("writer").getValue(String.class)) ||
                                    bannedPost.contains(Long.parseLong(dataSnapshot.getKey())))) {
                                Long postNumber = Long.parseLong(dataSnapshot.getKey());
                                Log.d("WeGlonD", postNumber + "");
                                returnList.add(postNumber);
                            }
                        }
                        acts.ifSuccess(task);
                        if (category.equals("1")){
                            Fragment_CM1.isUpdating = false;
                        }
                        if (category.equals("2")){
                            Fragment_CM2.isUpdating = false;
                        }
                        if (category.equals("3")){
                            recruit_list.recruit_isUpdating = false;
                        }
                    }
                }
                else{
                    acts.ifFail(task);
                }
            });
        }

    }

    public void readPostsFirst(ArrayList<Long> returnList, int count, String category, Acts acts) {
        String path = "firebase.Database.readAllPost - ";
        DatabaseReference postRoot = mDBRoot.child("Post" + category);

        ArrayList<Long> bannedPost = new ArrayList<>();
        ArrayList<String> bannedUser = new ArrayList<>();
        String userUID = null;
        if(mAuth.getCurrentUser()!=null){
            userUID = mAuth.getCurrentUser().getUid();
            String finalUserUID = userUID;
            userRoot.child(userUID).child("reportpost"+category).get().addOnCompleteListener(task2 -> {
                if(task2.isSuccessful()){
                    for(DataSnapshot dataSnapshot : task2.getResult().getChildren()){
                        if(!(dataSnapshot.getKey().equals("cnt"))){
                            Long bannedNumber = dataSnapshot.getValue(Long.class);
                            Log.d("WeGlonD", "Banned Post : " + bannedNumber);
                            bannedPost.add(bannedNumber);
                        }
                    }
                    userRoot.child(finalUserUID).child("reportuser").get().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            for(DataSnapshot dataSnapshot : task1.getResult().getChildren()){
                                if(!(dataSnapshot.getKey().equals("cnt"))){
                                    String bannedUserId = dataSnapshot.getValue(String.class);
                                    Log.d("WeGlonD", "Banned User : " + bannedUserId);
                                    bannedUser.add(bannedUserId);
                                }
                            }

                            //????????? ?????? ??????
                            postRoot.child("posting").get().addOnCompleteListener(task ->  {
                                if(task.isSuccessful()) {
                                    DataSnapshot res = task.getResult();
                                    for (DataSnapshot dataSnapshot : res.getChildren()) {
                                        if (!(dataSnapshot.getKey().equals("totalnumber"))) {
                                            if(!(bannedUser.contains(dataSnapshot.child("writer").getValue(String.class)) ||
                                                    bannedPost.contains(Long.parseLong(dataSnapshot.getKey())))) {
                                                Long postNumber = Long.parseLong(dataSnapshot.getKey());
                                                Log.d("WeGlonD", "readpostfirst list data " + postNumber + "");
                                                returnList.add(postNumber);
                                                acts.ifSuccess(res);
                                            }
                                        }
                                    }
                                    if (category.equals("1")) {
                                        returnList.add((long) -1);
                                        acts.ifSuccess(res);
                                        Fragment_CM1.isUpdating = false;
                                    }
                                    if (category.equals("2")) {
                                        //returnList.add((long) -1);
                                        acts.ifSuccess(res);
                                        Fragment_CM2.isUpdating = false;
                                    }
                                }
                            });

                        }
                    });
                }
            });
        }
        else{
            postRoot.child("posting").get().addOnCompleteListener(task ->  {
                if(task.isSuccessful()) {
                    DataSnapshot res = task.getResult();
                    for (DataSnapshot dataSnapshot : res.getChildren()) {
                        if (!(dataSnapshot.getKey().equals("totalnumber"))) {
                            if(!(bannedUser.contains(dataSnapshot.child("writer").getValue(String.class)) ||
                                    bannedPost.contains(Long.parseLong(dataSnapshot.getKey())))) {
                                Long postNumber = Long.parseLong(dataSnapshot.getKey());
                                Log.d("WeGlonD", postNumber + "");
                                returnList.add(postNumber);
                                acts.ifSuccess(res);
                            }
                        }
                    }
                    if (category.equals("1")) {
                        returnList.add((long) -1);
                        acts.ifSuccess(res);
                        Fragment_CM1.isUpdating = false;
                    }
                    if (category.equals("2")) {
                        //returnList.add((long) -1);
                        acts.ifSuccess(res);
                        Fragment_CM2.isUpdating = false;
                    }
                }
            });
        }
    }

    public void readAllRecruit(ArrayList<Long> returnList, String postnum, int count, Acts acts) {

        mDBRoot.child("Post3").child("posting").get().addOnCompleteListener(task5 -> {
            if(task5.isSuccessful()){
                final DataSnapshot post3 = task5.getResult();

                ArrayList<Long> bannedPost = new ArrayList<>();
                ArrayList<String> bannedUser = new ArrayList<>();
                String userUID = null;
                if(mAuth.getCurrentUser()!=null){
                    userUID = mAuth.getCurrentUser().getUid();
                    String finalUserUID = userUID;
                    userRoot.child(userUID).child("reportpost3").get().addOnCompleteListener(task2 -> {
                        if(task2.isSuccessful()){
                            for(DataSnapshot dataSnapshot : task2.getResult().getChildren()){
                                if(!(dataSnapshot.getKey().equals("cnt"))){
                                    Long bannedNumber = dataSnapshot.getValue(Long.class);
                                    Log.d("WeGlonD", "Banned Post : " + bannedNumber);
                                    bannedPost.add(bannedNumber);
                                }
                            }
                            userRoot.child(finalUserUID).child("reportuser").get().addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    for(DataSnapshot dataSnapshot : task1.getResult().getChildren()){
                                        if(!(dataSnapshot.getKey().equals("cnt"))){
                                            String bannedUserId = dataSnapshot.getValue(String.class);
                                            Log.d("WeGlonD", "Banned User : " + bannedUserId);
                                            bannedUser.add(bannedUserId);
                                        }
                                    }

                                    //????????? ?????? ??????
                                    mDBRoot.child("Post2").child("posting").child(postnum).child("recruit").orderByValue()
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        if (!dataSnapshot.getKey().equals("cnt")) {
                                                            if (!(bannedUser.contains(post3.child(dataSnapshot.getValue(Long.class) + "").child("writer").getValue(String.class)) ||
                                                                    bannedPost.contains(dataSnapshot.getValue(Long.class)))) {
                                                                Log.d("minseok", "" + dataSnapshot.getValue(Long.class));
                                                                returnList.add(dataSnapshot.getValue(Long.class));
                                                                acts.ifSuccess(snapshot);
                                                                if (returnList.size() >= count)
                                                                    break;
                                                            }
                                                        }
                                                    }
                                                    returnList.add(-1L);
                                                    acts.ifSuccess(snapshot);
                                                    recruit_list.recruit_isUpdating = false;
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                }
                            });
                        }
                    });
                }
                else{
                    mDBRoot.child("Post2").child("posting").child(postnum).child("recruit").orderByValue()
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        if (!dataSnapshot.getKey().equals("cnt")) {
                                            if (!(bannedUser.contains(post3.child(dataSnapshot.getValue(Long.class) + "").child("writer").getValue(String.class)) ||
                                                    bannedPost.contains(dataSnapshot.getValue(Long.class)))) {
                                                Log.d("minseok", "" + dataSnapshot.getValue(Long.class));
                                                returnList.add(dataSnapshot.getValue(Long.class));
                                                acts.ifSuccess(snapshot);
                                                if (returnList.size() >= count)
                                                    break;
                                            }
                                        }
                                    }
                                    returnList.add(-1L);
                                    acts.ifSuccess(snapshot);
                                    recruit_list.recruit_isUpdating = false;
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }

            }
        });

    }

    public void readNearAllRecruit(ArrayList<Long> returnList, String postnum, int count, double MaxDistanceKm, Acts acts){

        Location nowPosition = new Location("");
        nowPosition.setLatitude(MainActivity.location.getLatitude());
        nowPosition.setLongitude(MainActivity.location.getLongitude());

        mDBRoot.child("Post3").child("posting").get().addOnCompleteListener(task5 -> {
           if(task5.isSuccessful()){
               final DataSnapshot post3 = task5.getResult();

               ArrayList<Long> bannedPost = new ArrayList<>();
               ArrayList<String> bannedUser = new ArrayList<>();
               String userUID = null;
               if(mAuth.getCurrentUser()!=null){
                   userUID = mAuth.getCurrentUser().getUid();
                   String finalUserUID = userUID;
                   userRoot.child(userUID).child("reportpost3").get().addOnCompleteListener(task2 -> {
                       if(task2.isSuccessful()){
                           for(DataSnapshot dataSnapshot : task2.getResult().getChildren()){
                               if(!(dataSnapshot.getKey().equals("cnt"))){
                                   Long bannedNumber = dataSnapshot.getValue(Long.class);
                                   Log.d("WeGlonD", "Banned Post : " + bannedNumber);
                                   bannedPost.add(bannedNumber);
                               }
                           }
                           userRoot.child(finalUserUID).child("reportuser").get().addOnCompleteListener(task1 -> {
                               if(task1.isSuccessful()){
                                   for(DataSnapshot dataSnapshot : task1.getResult().getChildren()){
                                       if(!(dataSnapshot.getKey().equals("cnt"))){
                                           String bannedUserId = dataSnapshot.getValue(String.class);
                                           Log.d("WeGlonD", "Banned User : " + bannedUserId);
                                           bannedUser.add(bannedUserId);
                                       }
                                   }

                                   //????????? ?????? ??????
                                   mDBRoot.child("Post2").child("posting").child(postnum).child("recruit").orderByValue()
                                           .addListenerForSingleValueEvent(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                   int idx = -1;
                                                   for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                       idx++;
                                                       Log.d("weglond", dataSnapshot.getKey());
                                                       if (!dataSnapshot.getKey().equals("cnt")) {
                                                           if (!(bannedUser.contains(post3.child(dataSnapshot.getValue(Long.class) + "").child("writer").getValue(String.class)) ||
                                                                   bannedPost.contains(dataSnapshot.getValue(Long.class)))) {
                                                               int finalIdx = idx;

                                                               double latitude = post3.child(""+dataSnapshot.getValue(Long.class)).child("latitude").getValue(Double.class);
                                                               Log.d("WeGlonD", "lat " + latitude);

                                                               double longitude = post3.child(""+dataSnapshot.getValue(Long.class)).child("longitude").getValue(Double.class);
                                                               Log.d("WeGlonD", "lng " + longitude);
                                                               Location postPosition = new Location("");
                                                               postPosition.setLatitude(latitude);
                                                               postPosition.setLongitude(longitude);
                                                               if (nowPosition.distanceTo(postPosition) <= MaxDistanceKm * 1000) {
                                                                   Log.d("minseok", "" + dataSnapshot.getValue(Long.class));
                                                                   if (returnList.size() < count) {
                                                                       returnList.add(dataSnapshot.getValue(Long.class));
                                                                       acts.ifSuccess(snapshot);
                                                                   }
                                                               }
                                                               if (finalIdx == snapshot.getChildrenCount() - 1) {
                                                                   Log.d("weglond", "????????? child / -1 add");
                                                                   returnList.add(-1L);
                                                                   acts.ifSuccess(snapshot);
                                                                   recruit_list.recruit_isUpdating = false;
                                                               }
                                                           }
                                                       }
                                                   }
                                                   if(idx<=0){
                                                       returnList.add(-1L);
                                                       acts.ifSuccess(snapshot);
                                                       recruit_list.recruit_isUpdating = false;
                                                   }
                                               }
                                               @Override
                                               public void onCancelled(@NonNull DatabaseError error) {}
                                           });

                               }
                           });
                       }
                   });
               }
               else{
                   mDBRoot.child("Post2").child("posting").child(postnum).child("recruit").orderByValue()
                           .addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   int idx = -1;
                                   for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                       idx++;
                                       Log.d("weglond", dataSnapshot.getKey());
                                       if (!dataSnapshot.getKey().equals("cnt")) {
                                           if (!(bannedUser.contains(post3.child(dataSnapshot.getValue(Long.class) + "").child("writer").getValue(String.class)) ||
                                                   bannedPost.contains(dataSnapshot.getValue(Long.class)))) {
                                               int finalIdx = idx;

                                               double latitude = post3.child(""+dataSnapshot.getValue(Long.class)).child("latitude").getValue(Double.class);
                                               Log.d("WeGlonD", "lat " + latitude);

                                               double longitude = post3.child(""+dataSnapshot.getValue(Long.class)).child("longitude").getValue(Double.class);
                                               Log.d("WeGlonD", "lng " + longitude);
                                               Location postPosition = new Location("");
                                               postPosition.setLatitude(latitude);
                                               postPosition.setLongitude(longitude);
                                               if (nowPosition.distanceTo(postPosition) <= MaxDistanceKm * 1000) {
                                                   Log.d("minseok", "" + dataSnapshot.getValue(Long.class));
                                                   if (returnList.size() < count) {
                                                       returnList.add(dataSnapshot.getValue(Long.class));
                                                       acts.ifSuccess(snapshot);
                                                   }
                                               }
                                               if (finalIdx == snapshot.getChildrenCount() - 1) {
                                                   Log.d("weglond", "????????? child / -1 add");
                                                   returnList.add(-1L);
                                                   acts.ifSuccess(snapshot);
                                                   recruit_list.recruit_isUpdating = false;
                                               }
                                           }
                                       }
                                   }
                                   if(idx<=0){
                                       returnList.add(-1L);
                                       acts.ifSuccess(snapshot);
                                       recruit_list.recruit_isUpdating = false;
                                   }
                               }
                               @Override
                               public void onCancelled(@NonNull DatabaseError error) {}
                           });
               }
           }
        });


    }

    public void readRecruitPostsWith(ArrayList<Long> returnList,String postnum, Long str, Long end, Acts acts) {
        String path = "firebase.Database.readAllPost - ";
        DatabaseReference postRoot = mDBRoot.child("Post2");
        if (str < 0){
            str = Long.valueOf(0);
        }
        if (end < 0){
            end = FIRST_POSTNUM;
        }
        String from = String.valueOf(str);
        String to = String.valueOf(end);

        int firstSize = returnList.size();

        Long finalEnd = end;
        Long finalStr = str;

        mDBRoot.child("Post3").child("posting").get().addOnCompleteListener(task5 -> {
            if(task5.isSuccessful()) {
                final DataSnapshot post3 = task5.getResult();

                ArrayList<Long> bannedPost = new ArrayList<>();
                ArrayList<String> bannedUser = new ArrayList<>();
                String userUID = null;
                if(mAuth.getCurrentUser()!=null){
                    userUID = mAuth.getCurrentUser().getUid();
                    String finalUserUID = userUID;
                    userRoot.child(userUID).child("reportpost3").get().addOnCompleteListener(task2 -> {
                        if(task2.isSuccessful()){
                            for(DataSnapshot dataSnapshot : task2.getResult().getChildren()){
                                if(!(dataSnapshot.getKey().equals("cnt"))){
                                    Long bannedNumber = dataSnapshot.getValue(Long.class);
                                    Log.d("WeGlonD", "Banned Post : " + bannedNumber);
                                    bannedPost.add(bannedNumber);
                                }
                            }
                            userRoot.child(finalUserUID).child("reportuser").get().addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    for(DataSnapshot dataSnapshot : task1.getResult().getChildren()){
                                        if(!(dataSnapshot.getKey().equals("cnt"))){
                                            String bannedUserId = dataSnapshot.getValue(String.class);
                                            Log.d("WeGlonD", "Banned User : " + bannedUserId);
                                            bannedUser.add(bannedUserId);
                                        }
                                    }

                                    //????????? ?????? ??????
                                    postRoot.child("posting").child(postnum).child("recruit").orderByValue().startAt(from).
                                            addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                        if(!(dataSnapshot.getKey().equals("cnt"))) {
                                                            if(!(bannedUser.contains(post3.child(dataSnapshot.getValue(Long.class) + "").child("writer").getValue(String.class)) ||
                                                                    bannedPost.contains(dataSnapshot.getValue(Long.class)))) {
                                                                Long postNumber = Long.parseLong(dataSnapshot.getKey());
                                                                Log.d("WeGlonD", postNumber + "");
                                                                returnList.add(postNumber);
                                                                acts.ifSuccess(snapshot);

                                                                if (returnList.size() >= firstSize + finalEnd - finalStr)
                                                                    break;
                                                            }
                                                        }
                                                    }
                                                    returnList.add(-1L);
                                                    acts.ifSuccess(snapshot);
                                                    recruit_list.recruit_isUpdating = false;
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                }
                            });
                        }
                    });
                }
                else{
                    postRoot.child("posting").child(postnum).child("recruit").orderByValue().startAt(from).
                            addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        if(!(dataSnapshot.getKey().equals("cnt"))) {
                                            if(!(bannedUser.contains(post3.child(dataSnapshot.getValue(Long.class) + "").child("writer").getValue(String.class)) ||
                                                    bannedPost.contains(dataSnapshot.getValue(Long.class)))) {
                                                Long postNumber = Long.parseLong(dataSnapshot.getKey());
                                                Log.d("WeGlonD", postNumber + "");
                                                returnList.add(postNumber);
                                                acts.ifSuccess(snapshot);

                                                if (returnList.size() >= firstSize + finalEnd - finalStr)
                                                    break;
                                            }
                                        }
                                    }
                                    returnList.add(-1L);
                                    acts.ifSuccess(snapshot);
                                    recruit_list.recruit_isUpdating = false;
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }
            }
        });


    }

    public void readNearRecruitWith(ArrayList<Long> returnList,String postnum, Long str, Long end, double MaxDistanceKm, Acts acts){

        Location nowPosition = new Location("");
        nowPosition.setLatitude(MainActivity.location.getLatitude());
        nowPosition.setLongitude(MainActivity.location.getLongitude());

        DatabaseReference postRoot = mDBRoot.child("Post2");
        if (str < 0){
            str = Long.valueOf(0);
        }
        if (end < 0){
            end = FIRST_POSTNUM;
        }
        String from = String.valueOf(str);

        int maxcnt = (int)(end - str + 1 + returnList.size());

        mDBRoot.child("Post3").child("posting").get().addOnCompleteListener(task5 -> {
            if(task5.isSuccessful()){
                final DataSnapshot post3 = task5.getResult();

                ArrayList<Long> bannedPost = new ArrayList<>();
                ArrayList<String> bannedUser = new ArrayList<>();
                String userUID = null;
                if(mAuth.getCurrentUser()!=null){
                    userUID = mAuth.getCurrentUser().getUid();
                    String finalUserUID = userUID;
                    userRoot.child(userUID).child("reportpost3").get().addOnCompleteListener(task2 -> {
                        if(task2.isSuccessful()){
                            for(DataSnapshot dataSnapshot : task2.getResult().getChildren()){
                                if(!(dataSnapshot.getKey().equals("cnt"))){
                                    Long bannedNumber = dataSnapshot.getValue(Long.class);
                                    Log.d("WeGlonD", "Banned Post : " + bannedNumber);
                                    bannedPost.add(bannedNumber);
                                }
                            }
                            userRoot.child(finalUserUID).child("reportuser").get().addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    for(DataSnapshot dataSnapshot : task1.getResult().getChildren()){
                                        if(!(dataSnapshot.getKey().equals("cnt"))){
                                            String bannedUserId = dataSnapshot.getValue(String.class);
                                            Log.d("WeGlonD", "Banned User : " + bannedUserId);
                                            bannedUser.add(bannedUserId);
                                        }
                                    }

                                    //????????? ?????? ??????
                                    postRoot.child("posting").child(postnum).child("recruit").orderByValue().startAt(from).
                                            addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    int idx = -1;
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                        idx++;
                                                        if(!(dataSnapshot.getKey().equals("cnt"))) {
                                                            if(!(bannedUser.contains(post3.child(dataSnapshot.getValue(Long.class) + "").child("writer").getValue(String.class)) ||
                                                                    bannedPost.contains(dataSnapshot.getValue(Long.class)))) {
                                                                Long postNumber = Long.parseLong(dataSnapshot.getKey());
                                                                int finalIdx = idx;

                                                                double latitude = post3.child(dataSnapshot.getValue(Long.class)+"").child("latitude").getValue(Double.class);
                                                                double longitude = post3.child(dataSnapshot.getValue(Long.class)+"").child("longitude").getValue(Double.class);
                                                                Location postPosition = new Location("");
                                                                postPosition.setLatitude(latitude);
                                                                postPosition.setLongitude(longitude);
                                                                if (nowPosition.distanceTo(postPosition) <= MaxDistanceKm * 1000) {
                                                                    if (returnList.size() < maxcnt) {
                                                                        Log.d("WeGlonD", postNumber + "");
                                                                        returnList.add(postNumber);
                                                                        acts.ifSuccess(snapshot);
                                                                    }
                                                                }
                                                                if (finalIdx == snapshot.getChildrenCount() - 1) {
                                                                    returnList.add(-1L);
                                                                    acts.ifSuccess(snapshot);
                                                                    recruit_list.recruit_isUpdating = false;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if(idx == -1){
                                                        Log.d("weglond", "?????? ?????????");
                                                        returnList.add(-1L);
                                                        acts.ifSuccess(snapshot);
                                                        recruit_list.recruit_isUpdating = false;
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                }
                            });
                        }
                    });
                }
                else{
                    postRoot.child("posting").child(postnum).child("recruit").orderByValue().startAt(from).
                            addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int idx = -1;
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        idx++;
                                        if(!(dataSnapshot.getKey().equals("cnt"))) {
                                            if(!(bannedUser.contains(post3.child(dataSnapshot.getValue(Long.class) + "").child("writer").getValue(String.class)) ||
                                                    bannedPost.contains(dataSnapshot.getValue(Long.class)))) {
                                                Long postNumber = Long.parseLong(dataSnapshot.getKey());
                                                int finalIdx = idx;

                                                double latitude = post3.child(dataSnapshot.getValue(Long.class)+"").child("latitude").getValue(Double.class);
                                                double longitude = post3.child(dataSnapshot.getValue(Long.class)+"").child("longitude").getValue(Double.class);
                                                Location postPosition = new Location("");
                                                postPosition.setLatitude(latitude);
                                                postPosition.setLongitude(longitude);
                                                if (nowPosition.distanceTo(postPosition) <= MaxDistanceKm * 1000) {
                                                    if (returnList.size() < maxcnt) {
                                                        Log.d("WeGlonD", postNumber + "");
                                                        returnList.add(postNumber);
                                                        acts.ifSuccess(snapshot);
                                                    }
                                                }
                                                if (finalIdx == snapshot.getChildrenCount() - 1) {
                                                    returnList.add(-1L);
                                                    acts.ifSuccess(snapshot);
                                                    recruit_list.recruit_isUpdating = false;
                                                }
                                            }
                                        }
                                    }
                                    if(idx == -1){
                                        Log.d("weglond", "?????? ?????????");
                                        returnList.add(-1L);
                                        acts.ifSuccess(snapshot);
                                        recruit_list.recruit_isUpdating = false;
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }
        });


    }

    public void readNearPostsFirst(ArrayList<Long> returnList, int count, double MaxDistanceKm, String category, Acts acts){
        String path = "firebase.Database.readNearPostsFirst - ";
        DatabaseReference postRoot = mDBRoot.child("Post"+category);
        DatabaseReference postingRoot = postRoot.child("posting");

        Location nowPosition = new Location("");
        nowPosition.setLatitude(MainActivity.location.getLatitude());
        nowPosition.setLongitude(MainActivity.location.getLongitude());

        ArrayList<Long> bannedPost = new ArrayList<>();
        ArrayList<String> bannedUser = new ArrayList<>();
        String userUID = null;
        if(mAuth.getCurrentUser()!=null){
            userUID = mAuth.getCurrentUser().getUid();
            String finalUserUID = userUID;
            userRoot.child(userUID).child("reportpost"+category).get().addOnCompleteListener(task2 -> {
                if(task2.isSuccessful()){
                    for(DataSnapshot dataSnapshot : task2.getResult().getChildren()){
                        if(!(dataSnapshot.getKey().equals("cnt"))){
                            Long bannedNumber = dataSnapshot.getValue(Long.class);
                            Log.d("WeGlonD", "Banned Post : " + bannedNumber);
                            bannedPost.add(bannedNumber);
                        }
                    }
                    userRoot.child(finalUserUID).child("reportuser").get().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            for(DataSnapshot dataSnapshot : task1.getResult().getChildren()){
                                if(!(dataSnapshot.getKey().equals("cnt"))){
                                    String bannedUserId = dataSnapshot.getValue(String.class);
                                    Log.d("WeGlonD", "Banned User : " + bannedUserId);
                                    bannedUser.add(bannedUserId);
                                }
                            }

                            //????????? ?????? ??????
                            postingRoot.
                                    addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                if(!(dataSnapshot.getKey().equals("totalnumber"))){
                                                    if(!(bannedUser.contains(dataSnapshot.child("writer").getValue(String.class)) ||
                                                            bannedPost.contains(Long.parseLong(dataSnapshot.getKey())))) {
                                                        double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                                                        double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                                                        Location postLocation = new Location("");
                                                        postLocation.setLatitude(latitude);
                                                        postLocation.setLongitude(longitude);
                                                        if (nowPosition.distanceTo(postLocation) <= MaxDistanceKm * 1000) {
                                                            Long postNumber = Long.parseLong(dataSnapshot.getKey());
                                                            Log.d("WeGlonD", postNumber + "");
                                                            returnList.add(postNumber);
                                                            acts.ifSuccess(snapshot);
                                                        }
                                                    }
                                                }
                                                if(returnList.size() == count)
                                                    break;
                                            }
                                            if (category.equals("1")){
                                                returnList.add((long) -1);
                                                acts.ifSuccess(snapshot);
                                                Fragment_CM1.isUpdating = false;
                                            }
                                            if (category.equals("2")){
                                                //returnList.add((long) -1);
                                                acts.ifSuccess(snapshot);
                                                Fragment_CM2.isUpdating = false;
                                            }
                                            if (category.equals("3")){
                                                returnList.add((long) -1);
                                                acts.ifSuccess(snapshot);
                                                recruit_list.recruit_isUpdating = false;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }
                    });
                }
            });
        }
        else{
            postingRoot.
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                if(!(dataSnapshot.getKey().equals("totalnumber"))){
                                    if(!(bannedUser.contains(dataSnapshot.child("writer").getValue(String.class)) ||
                                            bannedPost.contains(Long.parseLong(dataSnapshot.getKey())))) {
                                        double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                                        double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                                        Location postLocation = new Location("");
                                        postLocation.setLatitude(latitude);
                                        postLocation.setLongitude(longitude);
                                        if (nowPosition.distanceTo(postLocation) <= MaxDistanceKm * 1000) {
                                            Long postNumber = Long.parseLong(dataSnapshot.getKey());
                                            Log.d("WeGlonD", postNumber + "");
                                            returnList.add(postNumber);
                                            acts.ifSuccess(snapshot);
                                        }
                                    }
                                }
                                if(returnList.size() == count)
                                    break;
                            }
                            if (category.equals("1")){
                                returnList.add((long) -1);
                                acts.ifSuccess(snapshot);
                                Fragment_CM1.isUpdating = false;
                            }
                            if (category.equals("2")){
                                //returnList.add((long) -1);
                                acts.ifSuccess(snapshot);
                                Fragment_CM2.isUpdating = false;
                            }
                            if (category.equals("3")){
                                returnList.add((long) -1);
                                acts.ifSuccess(snapshot);
                                recruit_list.recruit_isUpdating = false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    public void readPostsWith(ArrayList<Long> returnList, Long str, Long end, String category, Acts acts){
        String path = "firebase.Database.readAllPost - ";

        DatabaseReference postRoot = mDBRoot.child("Post"+category);

        if (str < 0){
            str = Long.valueOf(0);
        }
        if (end < 0){
            end = FIRST_POSTNUM;
        }

        String from = String.valueOf(str);
        String to = String.valueOf(end);

        int count = (int)(end - str + 1);
        int maxcnt = returnList.size() + count;

//        Toast.makeText(context, from+" "+to, Toast.LENGTH_LONG).show();

        ArrayList<Long> bannedPost = new ArrayList<>();
        ArrayList<String> bannedUser = new ArrayList<>();
        String userUID = null;
        if(mAuth.getCurrentUser()!=null){
            userUID = mAuth.getCurrentUser().getUid();
            String finalUserUID = userUID;
            userRoot.child(userUID).child("reportpost"+category).get().addOnCompleteListener(task2 -> {
                if(task2.isSuccessful()){
                    for(DataSnapshot dataSnapshot : task2.getResult().getChildren()){
                        if(!(dataSnapshot.getKey().equals("cnt"))){
                            Long bannedNumber = dataSnapshot.getValue(Long.class);
                            Log.d("WeGlonD", "Banned Post : " + bannedNumber);
                            bannedPost.add(bannedNumber);
                        }
                    }
                    userRoot.child(finalUserUID).child("reportuser").get().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            for(DataSnapshot dataSnapshot : task1.getResult().getChildren()){
                                if(!(dataSnapshot.getKey().equals("cnt"))){
                                    String bannedUserId = dataSnapshot.getValue(String.class);
                                    Log.d("WeGlonD", "Banned User : " + bannedUserId);
                                    bannedUser.add(bannedUserId);
                                }
                            }

                            //????????? ?????? ??????
                            postRoot.child("posting").orderByKey().startAt(from).
                                    addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                if(!(dataSnapshot.getKey().equals("totalnumber"))) {
                                                    if(!(bannedUser.contains(dataSnapshot.child("writer").getValue(String.class)) ||
                                                            bannedPost.contains(Long.parseLong(dataSnapshot.getKey())))) {
                                                        Long postNumber = Long.parseLong(dataSnapshot.getKey());
                                                        Log.d("WeGlonD", postNumber + "");
                                                        returnList.add(postNumber);
                                                        acts.ifSuccess(snapshot);

                                                        if (returnList.size() == maxcnt)
                                                            break;
                                                    }
                                                }
                                            }
                                            if (category.equals("1")){
                                                returnList.add((long) -1);
                                                acts.ifSuccess(snapshot);
                                                Fragment_CM1.isUpdating = false;
                                            }
                                            if (category.equals("2")){
                                                //returnList.add((long) -1);
                                                //acts.ifSuccess(snapshot);
                                                Fragment_CM2.isUpdating = false;
                                            }
                                            if (category.equals("3")){
                                                returnList.add((long) -1);
                                                recruit_list.recruit_isUpdating = false;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }
                    });
                }
            });
        }
        else{
            postRoot.child("posting").orderByKey().startAt(from).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                if(!(dataSnapshot.getKey().equals("totalnumber"))) {
                                    if(!(bannedUser.contains(dataSnapshot.child("writer").getValue(String.class)) ||
                                            bannedPost.contains(Long.parseLong(dataSnapshot.getKey())))) {
                                        Long postNumber = Long.parseLong(dataSnapshot.getKey());
                                        Log.d("WeGlonD", postNumber + "");
                                        returnList.add(postNumber);
                                        acts.ifSuccess(snapshot);

                                        if (returnList.size() == maxcnt)
                                            break;
                                    }
                                }
                            }
                            if (category.equals("1")){
                                returnList.add((long) -1);
                                acts.ifSuccess(snapshot);
                                Fragment_CM1.isUpdating = false;
                            }
                            if (category.equals("2")){
                                //returnList.add((long) -1);
                                //acts.ifSuccess(snapshot);
                                Fragment_CM2.isUpdating = false;
                            }
                            if (category.equals("3")){
                                returnList.add((long) -1);
                                recruit_list.recruit_isUpdating = false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

    }

    public void readNearPostsWith(ArrayList<Long> returnList, Long str, Long end, Double MaxDistanceKm, String category, Acts acts){

        String path = "firebase.Database.readNearPostsWith - ";

        DatabaseReference postRoot = mDBRoot.child("Post"+category);

        Location nowPosition = new Location("");
        nowPosition.setLatitude(MainActivity.location.getLatitude());
        nowPosition.setLongitude(MainActivity.location.getLongitude());
        Log.d("WeGlonD", "str : " + str + " end : " + end);

        if (str < 0){
            str = Long.valueOf(0);
        }
        if (end < 0){
            end = FIRST_POSTNUM;
        }

        String from = String.valueOf(str);
        String to = String.valueOf(end);

        Log.d("WeGlonD", "from : " + from + " to : " + to);

        int count = (int)(end - str + 1);
        int maxcnt = returnList.size() + count;

//        Toast.makeText(context, from+" "+to, Toast.LENGTH_LONG).show();

        ArrayList<Long> bannedPost = new ArrayList<>();
        ArrayList<String> bannedUser = new ArrayList<>();
        String userUID = null;
        if(mAuth.getCurrentUser()!=null){
            userUID = mAuth.getCurrentUser().getUid();
            String finalUserUID = userUID;
            userRoot.child(userUID).child("reportpost"+category).get().addOnCompleteListener(task2 -> {
                if(task2.isSuccessful()){
                    for(DataSnapshot dataSnapshot : task2.getResult().getChildren()){
                        if(!(dataSnapshot.getKey().equals("cnt"))){
                            Long bannedNumber = dataSnapshot.getValue(Long.class);
                            Log.d("WeGlonD", "Banned Post : " + bannedNumber);
                            bannedPost.add(bannedNumber);
                        }
                    }
                    userRoot.child(finalUserUID).child("reportuser").get().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            for(DataSnapshot dataSnapshot : task1.getResult().getChildren()){
                                if(!(dataSnapshot.getKey().equals("cnt"))){
                                    String bannedUserId = dataSnapshot.getValue(String.class);
                                    Log.d("WeGlonD", "Banned User : " + bannedUserId);
                                    bannedUser.add(bannedUserId);
                                }
                            }

                            //????????? ?????? ??????
                            postRoot.child("posting").orderByKey().startAt(from).
                                    addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                Log.d("WeGlonD", dataSnapshot.toString());
                                                if(!(dataSnapshot.getKey().equals("totalnumber"))) {
                                                    if(!(bannedUser.contains(dataSnapshot.child("writer").getValue(String.class)) ||
                                                            bannedPost.contains(Long.parseLong(dataSnapshot.getKey())))) {
                                                        double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                                                        double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                                                        Location postLocation = new Location("");
                                                        postLocation.setLatitude(latitude);
                                                        postLocation.setLongitude(longitude);
                                                        if (nowPosition.distanceTo(postLocation) <= MaxDistanceKm * 1000) {
                                                            Long postNumber = Long.parseLong(dataSnapshot.getKey());
                                                            Log.d("WeGlonD", postNumber + "");
                                                            returnList.add(postNumber);
                                                            acts.ifSuccess(snapshot);
                                                        }
                                                    }
                                                }
                                                if(returnList.size() == maxcnt)
                                                    break;
                                            }
                                            if (category.equals("1")){
                                                returnList.add((long) -1);
                                                acts.ifSuccess(snapshot);
                                                Fragment_CM1.isUpdating = false;
                                            }
                                            if (category.equals("2")){
                                                //returnList.add((long) -1);
                                                //acts.ifSuccess(snapshot);
                                                Fragment_CM2.isUpdating = false;
                                            }
                                            if (category.equals("3")){
                                                returnList.add((long) -1);
                                                acts.ifSuccess(snapshot);
                                                recruit_list.recruit_isUpdating = false;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }
                    });
                }
            });
        }
        else{
            postRoot.child("posting").orderByKey().startAt(from).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Log.d("WeGlonD", dataSnapshot.toString());
                                if(!(dataSnapshot.getKey().equals("totalnumber"))) {
                                    if(!(bannedUser.contains(dataSnapshot.child("writer").getValue(String.class)) ||
                                            bannedPost.contains(Long.parseLong(dataSnapshot.getKey())))) {
                                        double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                                        double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                                        Location postLocation = new Location("");
                                        postLocation.setLatitude(latitude);
                                        postLocation.setLongitude(longitude);
                                        if (nowPosition.distanceTo(postLocation) <= MaxDistanceKm * 1000) {
                                            Long postNumber = Long.parseLong(dataSnapshot.getKey());
                                            Log.d("WeGlonD", postNumber + "");
                                            returnList.add(postNumber);
                                            acts.ifSuccess(snapshot);
                                        }
                                    }
                                }
                                if(returnList.size() == maxcnt)
                                    break;
                            }
                            if (category.equals("1")){
                                returnList.add((long) -1);
                                acts.ifSuccess(snapshot);
                                Fragment_CM1.isUpdating = false;
                            }
                            if (category.equals("2")){
                                //returnList.add((long) -1);
                                //acts.ifSuccess(snapshot);
                                Fragment_CM2.isUpdating = false;
                            }
                            if (category.equals("3")){
                                returnList.add((long) -1);
                                acts.ifSuccess(snapshot);
                                recruit_list.recruit_isUpdating = false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    public void readName(ArrayList<Long> returnList, String keyword, String category, Acts acts){
        String path = "firebase.Database.readAllPost - ";
        Log.d("minseok", "readName called");

        DatabaseReference postRoot = mDBRoot.child("Post"+category);
        DatabaseReference titleRoot = postRoot.child("Title");

        postRoot.child("posting").get().addOnCompleteListener(task5 -> {
            if(task5.isSuccessful()){
                final DataSnapshot postN = task5.getResult();

                ArrayList<Long> bannedPost = new ArrayList<>();
                ArrayList<String> bannedUser = new ArrayList<>();
                String userUID = null;
                if(mAuth.getCurrentUser()!=null){
                    userUID = mAuth.getCurrentUser().getUid();
                    String finalUserUID = userUID;
                    userRoot.child(userUID).child("reportpost"+category).get().addOnCompleteListener(task2 -> {
                        if(task2.isSuccessful()){
                            for(DataSnapshot dataSnapshot : task2.getResult().getChildren()){
                                if(!(dataSnapshot.getKey().equals("cnt"))){
                                    Long bannedNumber = dataSnapshot.getValue(Long.class);
                                    Log.d("WeGlonD", "Banned Post : " + bannedNumber);
                                    bannedPost.add(bannedNumber);
                                }
                            }
                            userRoot.child(finalUserUID).child("reportuser").get().addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    for(DataSnapshot dataSnapshot : task1.getResult().getChildren()){
                                        if(!(dataSnapshot.getKey().equals("cnt"))){
                                            String bannedUserId = dataSnapshot.getValue(String.class);
                                            Log.d("WeGlonD", "Banned User : " + bannedUserId);
                                            bannedUser.add(bannedUserId);
                                        }
                                    }

                                    //????????? ?????? ??????
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
                                                            if(!(bannedUser.contains(postN.child(dataSnapshot1.getValue(Long.class)+"").child("writer").getValue(String.class)) ||
                                                                    bannedPost.contains(dataSnapshot1.getValue(Long.class)))) {
                                                                returnList.add(dataSnapshot1.getValue(Long.class));
                                                                Log.d("minseok", "readName - datasnapshot1 Key NOT EQUALS cnt");
                                                            }
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
                            });
                        }
                    });
                }
                else{
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
                                            if(!(bannedUser.contains(postN.child(dataSnapshot1.getValue(Long.class)+"").child("writer").getValue(String.class)) ||
                                                    bannedPost.contains(dataSnapshot1.getValue(Long.class)))) {
                                                returnList.add(dataSnapshot1.getValue(Long.class));
                                                Log.d("minseok", "readName - datasnapshot1 Key NOT EQUALS cnt");
                                            }
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
            }
        });


    }

    public void readTag(ArrayList<Long> returnList, String keyword, String category, Acts acts){
        String path = "firebase.Database.readAllPost - ";

        DatabaseReference postRoot = mDBRoot.child("Post"+category);
        DatabaseReference tagRoot = postRoot.child("Tag");

        postRoot.child("posting").get().addOnCompleteListener(task5 -> {
            if(task5.isSuccessful()){
                final DataSnapshot postN = task5.getResult();

                ArrayList<Long> bannedPost = new ArrayList<>();
                ArrayList<String> bannedUser = new ArrayList<>();
                String userUID = null;
                if(mAuth.getCurrentUser()!=null){
                    userUID = mAuth.getCurrentUser().getUid();
                    String finalUserUID = userUID;
                    userRoot.child(userUID).child("reportpost"+category).get().addOnCompleteListener(task2 -> {
                        if(task2.isSuccessful()){
                            for(DataSnapshot dataSnapshot : task2.getResult().getChildren()){
                                if(!(dataSnapshot.getKey().equals("cnt"))){
                                    Long bannedNumber = dataSnapshot.getValue(Long.class);
                                    Log.d("WeGlonD", "Banned Post : " + bannedNumber);
                                    bannedPost.add(bannedNumber);
                                }
                            }
                            userRoot.child(finalUserUID).child("reportuser").get().addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    for(DataSnapshot dataSnapshot : task1.getResult().getChildren()){
                                        if(!(dataSnapshot.getKey().equals("cnt"))){
                                            String bannedUserId = dataSnapshot.getValue(String.class);
                                            Log.d("WeGlonD", "Banned User : " + bannedUserId);
                                            bannedUser.add(bannedUserId);
                                        }
                                    }

                                    //????????? ?????? ??????
                                    tagRoot.get().addOnCompleteListener(task -> {
                                        if (task.isSuccessful()){
                                            for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                                                String post_tag = dataSnapshot.getKey();
                                                if(post_tag.contains(keyword)){
                                                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                                        if(!dataSnapshot1.getKey().equals("cnt")){
                                                            if(!(bannedUser.contains(postN.child(dataSnapshot1.getValue(Long.class)+"").child("writer").getValue(String.class)) ||
                                                                    bannedPost.contains(dataSnapshot1.getValue(Long.class)))) {
                                                                Long tmp = dataSnapshot1.getValue(Long.class);
                                                                if (!returnList.contains(tmp))
                                                                    returnList.add(tmp);
                                                            }
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
                            });
                        }
                    });
                }
                else{
                    tagRoot.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                                String post_tag = dataSnapshot.getKey();
                                if(post_tag.contains(keyword)){
                                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                        if(!dataSnapshot1.getKey().equals("cnt")){
                                            if(!(bannedUser.contains(postN.child(dataSnapshot1.getValue(Long.class)+"").child("writer").getValue(String.class)) ||
                                                    bannedPost.contains(dataSnapshot1.getValue(Long.class)))) {
                                                Long tmp = dataSnapshot1.getValue(Long.class);
                                                if (!returnList.contains(tmp))
                                                    returnList.add(tmp);
                                            }
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
                            if (!(dataSnapshot.getKey().equals("comment") || dataSnapshot.getKey().equals("clickcnt") || dataSnapshot.getKey().equals("recruitFrom") ||
                                    dataSnapshot.getKey().equals("latitude") || dataSnapshot.getKey().equals("longitude")||dataSnapshot.getKey().equals("likeuser") || dataSnapshot.getKey().equals("recruit"))) {
                                String key = dataSnapshot.getKey();
                                String value = dataSnapshot.getValue(String.class);
                                Log.d("minseok","value"+value);
                                if (key.equals("0")) {
                                    Title = value;
                                } else if (key.equals("tags")) {
                                    Tag = value;
                                } else if (key.equals("writer")) {
                                    Log.d("minseok","writer"+value);
                                    User_Id = value;
                                } else if (key.equals("timestamp")){
                                    timeStamp = value;
                                } else {
                                    if(category.equals("2") && key.equals("1")){
                                        community2_upload.community2_change_content = value;
                                    }
                                    else{
                                        Content.add(value);
                                    }
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
                        ArrayList<Long> postNumbs = new ArrayList<>();

                        for(DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                            if (!(dataSnapshot.getKey().equals("cnt"))) {
                                postNumbs.add(dataSnapshot.getValue(Long.class));
                            }
                        }

                        Collections.sort(postNumbs, (t1, t2) -> {
                            if (t1 > t2)
                                return +1;
                            else if (t1 < t2)
                                return -1;
                            else
                                return 0;
                        });

                        for(Long postNum : postNumbs) {
                            returnList.add(postNum);
                            acts.ifSuccess(task);
                        }

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
                        ArrayList<Long> postNumbs = new ArrayList<>();

                        for(DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                            if (!(dataSnapshot.getKey().equals("cnt"))) {
                                postNumbs.add(dataSnapshot.getValue(Long.class));
                            }
                        }

                        Collections.sort(postNumbs, (t1, t2) -> {
                            if (t1 > t2)
                                return +1;
                            else if (t1 < t2)
                                return -1;
                            else
                                return 0;
                        });

                        for(Long postNum : postNumbs) {
                            returnList.add(postNum);
                            acts.ifSuccess(task);
                        }

                    }
                    else{
                        acts.ifFail(task);
                    }
                });
    }
    public void readAllUserPost2AndScroll(ArrayList<Long> returnList, int position, RecyclerView recyclerView, Acts acts){
        userRoot.child(mAuth.getCurrentUser().getUid()).
                child(context.getString(R.string.DB_user_posting2)).
                get().addOnCompleteListener(task -> {

                    if(task.isSuccessful()){
                        ArrayList<Long> postNumbs = new ArrayList<>();

                        for(DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                            if (!(dataSnapshot.getKey().equals("cnt"))) {
                                postNumbs.add(dataSnapshot.getValue(Long.class));
                            }
                        }

                        Collections.sort(postNumbs, (t1, t2) -> {
                            if (t1 > t2)
                                return +1;
                            else if (t1 < t2)
                                return -1;
                            else
                                return 0;
                        });

                        for(Long postNum : postNumbs) {
                            returnList.add(postNum);
                            acts.ifSuccess(task);
                        }

                        recyclerView.smoothScrollToPosition(position);

                    }
                    else{
                        acts.ifFail(task);
                    }
                });
    }
    public void readAllUserPost3(ArrayList<Long> returnList, Acts acts){
        userRoot.child(mAuth.getCurrentUser().getUid()).
                child(context.getString(R.string.DB_user_posting3)).
                get().addOnCompleteListener(task -> {

                    if(task.isSuccessful()){
                        ArrayList<Long> postNumbs = new ArrayList<>();

                        for(DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                            if (!(dataSnapshot.getKey().equals("cnt"))) {
                                postNumbs.add(dataSnapshot.getValue(Long.class));
                            }
                        }

                        Collections.sort(postNumbs, (t1, t2) -> {
                            if (t1 > t2)
                                return +1;
                            else if (t1 < t2)
                                return -1;
                            else
                                return 0;
                        });

                        for(Long postNum : postNumbs) {
                            returnList.add(postNum);
                            acts.ifSuccess(task);
                        }

                    }
                    else{
                        acts.ifFail(task);
                    }
                });
    }

    public void writeCommentPersonal(Long postnumber, String text, String category, ArrayList<String> keylist, Acts acts){
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
            if (FirebaseAuth.getInstance().getCurrentUser() != null){
                currentComment.child("writer").setValue(mAuth.getCurrentUser().getUid());
                currentComment.child("text").setValue(text);
                keylist.add(""+commentNum);
                acts.ifSuccess(task);
            }
        });
    }
    public void writeComment(Long postnumber, String text, String category, ArrayList<Long> addingList,Acts acts){
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
            Long finalCommentNum = commentNum;
            currPosting.child("comment").child("cnt").setValue(commentNum).addOnCompleteListener(t1 -> {
                DatabaseReference currentComment = currPosting.child("comment").child(finalCommentNum +"");
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    currentComment.child("writer").setValue(mAuth.getCurrentUser().getUid()).addOnCompleteListener(t2 -> {
                        currentComment.child("text").setValue(text).addOnCompleteListener(t3 -> {
                            addingList.add(finalCommentNum);
                            acts.ifSuccess(t3);
                        });
                    });
                }
            });
        });
    }

    public void readComment(ArrayList<Comment> data, Long postnum, String category, Acts acts){
        DatabaseReference postRoot = mDBRoot.child("Post"+category);
        DatabaseReference postingRoot = postRoot.child("posting");

        //report : Report - comment - Post1 - 9999999999 - 1 - UID : 1 , reason : ????????????
        //user : User - Uid - reportcomment - Post1 - 999999999 - 1(key_cnt) : 1(comment's key)
        ArrayList<Long> bannedComment = new ArrayList<>();
        ArrayList<String> bannedUser = new ArrayList<>();
        String userUID = null;
        if(mAuth.getCurrentUser()!=null) {
            userUID = mAuth.getCurrentUser().getUid();
            String finalUserUID = userUID;
            userRoot.child(userUID).child("reportcomment").child("Post"+category).child(postnum+"").get().addOnCompleteListener(task2 -> {
                if (task2.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task2.getResult().getChildren()) {
                        if (!(dataSnapshot.getKey().equals("cnt"))) {
                            Long bannedNumber = dataSnapshot.getValue(Long.class);
                            Log.d("WeGlonD", "Banned Post : " + bannedNumber);
                            bannedComment.add(bannedNumber);
                        }
                    }
                    userRoot.child(finalUserUID).child("reportuser").get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            for (DataSnapshot dataSnapshot : task1.getResult().getChildren()) {
                                if (!(dataSnapshot.getKey().equals("cnt"))) {
                                    String bannedUserId = dataSnapshot.getValue(String.class);
                                    Log.d("WeGlonD", "Banned User : " + bannedUserId);
                                    bannedUser.add(bannedUserId);
                                }
                            }

                            //????????? ?????? ??????
                            DatabaseReference currPosting = postingRoot.child(postnum+"");
                            currPosting.child("comment").get().addOnCompleteListener(task -> {
                                if(task.isSuccessful()) {
                                    if(task.getResult().getChildrenCount() > 1) {
                                        for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                            if (!dataSnapshot.getKey().equals("cnt")) {
                                                if(!(bannedUser.contains(dataSnapshot.child("writer").getValue(String.class)) ||
                                                        bannedComment.contains(Long.parseLong(dataSnapshot.getKey())))) {
                                                    Comment tmp = new Comment(dataSnapshot.child("text").getValue(String.class), dataSnapshot.child("writer").getValue(String.class), dataSnapshot.getKey());
                                                    Log.d("WeGlonD", dataSnapshot.child("text").getValue(String.class));
                                                    Log.d("WeGlonD", dataSnapshot.child("writer").getValue(String.class));
                                                    data.add(tmp);
                                                }
                                            }
                                        }
                                        acts.ifSuccess(task);
                                    }
                                    else {
                                        currPosting.child("comment").child("cnt").setValue(0);
                                        acts.ifFail(task);
                                    }
                                }
                                else {
                                    currPosting.child("comment").child("cnt").setValue(0);
                                    acts.ifFail(task);
                                }
                            });

                        }
                    });
                }
            });
        }
        else{
            DatabaseReference currPosting = postingRoot.child(postnum+"");
            currPosting.child("comment").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    if(task.getResult().getChildrenCount() > 1) {
                        for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                            if (!dataSnapshot.getKey().equals("cnt")) {
                                Comment tmp = new Comment(dataSnapshot.child("text").getValue(String.class),dataSnapshot.child("writer").getValue(String.class),dataSnapshot.getKey());
                                Log.d("WeGlonD", dataSnapshot.child("text").getValue(String.class));
                                Log.d("WeGlonD", dataSnapshot.child("writer").getValue(String.class));
                                data.add(tmp);
                            }
                        }
                        acts.ifSuccess(task);
                    }
                    else {
                        currPosting.child("comment").child("cnt").setValue(0);
                        acts.ifFail(task);
                    }
                }
                else {
                    currPosting.child("comment").child("cnt").setValue(0);
                    acts.ifFail(task);
                }
            });
        }
    }

    public void editComment(Long postnum, String key, String newText, String category){
        DatabaseReference currComment = mDBRoot.child("Post"+category).child("posting").child(""+postnum).child("comment").child(key);
        currComment.child("text").setValue(newText);
        Log.d("WeGlonD", "editComment " + currComment.toString());
    }

    public void deleteComment(Long postnum, String key, String category){
        DatabaseReference currComment = mDBRoot.child("Post"+category).child("posting").child(""+postnum).child("comment").child(key);
        currComment.removeValue();
    }
}
