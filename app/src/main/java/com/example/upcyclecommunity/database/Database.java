package com.example.upcyclecommunity.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

import java.sql.Timestamp;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Database {
    private static DatabaseReference mDBRoot = null;
    private static DatabaseReference brandRoot = null;
    private static DatabaseReference postRoot = null;
    private static DatabaseReference titleRoot = null;
    private static DatabaseReference tagRoot = null;
    private static DatabaseReference timeRoot = null;
    private static DatabaseReference userRoot = null;
    private static ValueEventListener userDataListener = null;

    private static StorageReference mStorage = null;
    private static StorageReference userProfileImageRoot = null;
    private static StorageReference brandpictureRoot = null;
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
            }
            if (mDBRoot == null)
                mDBRoot = FirebaseDatabase.getInstance().getReference();
            else{
                if (brandRoot == null)
                    brandRoot = mDBRoot.child("Brand");
                if (postRoot == null)
                    postRoot = mDBRoot.child("Post");
                if (titleRoot == null)
                    titleRoot = mDBRoot.child("title");
                if (tagRoot == null)
                    tagRoot = mDBRoot.child("tag");
                if (timeRoot == null)
                    timeRoot = mDBRoot.child("time");
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

    public static StorageReference getStorage() {
        return mStorage;
    }

    public static StorageReference getUserProfileImageRoot() {
        return userProfileImageRoot;
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

    public void writePostByLine(Long lineNumber, String data, String title, ArrayList<String> tags){
        postRoot.child("posting").child("totalnumber").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Long postnumber = task.getResult().getValue(Long.class);
                if (lineNumber == 1) {
                    postnumber++;
                    postRoot.child("posting").child("totalnumber").setValue(postnumber);
                    //postRoot.child("totalnumber").child(""+postnumber).child("title").setValue(title);
                    postRoot.child("posting").child(""+postnumber).child("0").setValue(title);
                    postRoot.child("Title").child(title).setValue(postnumber);
                    String resultTagStr = "";
                    for(String str : tags){
                        Long finalPostnumber = postnumber;
                        postRoot.child("Tag").child(str).child("cnt").get().addOnCompleteListener(task1 -> {
                            Long cnt;
                            if (task1.isSuccessful()){
                                cnt = task.getResult().getValue(Long.class);
                            }
                            else{
                                cnt = Long.parseLong("0");
                            }
                            cnt++;
                            postRoot.child("Tag").child(str).child("cnt").setValue(cnt);
                            postRoot.child("Tag").child(str).child(cnt+"").setValue(finalPostnumber);
                        });
                        resultTagStr += "#"+str+" ";
                    }
                    postRoot.child("posting").child(""+postnumber).child("tags").setValue(resultTagStr);
                }

                postRoot.child("posting").child(String.valueOf(postnumber)).child(lineNumber + "").setValue(data);
            }
            else{
                Toast.makeText(context, "실패!", Toast.LENGTH_LONG).show();
            }
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

                timeRoot.child(time).setValue(postnumber);
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

    public void readAllPost(ArrayList<Post> returnList, BrandQuery con, Acts acts){
        String path = "firebase.Database.readAllPost - ";

        titleRoot.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                    returnList.add(dataSnapshot.getValue(Post.class));
                }
                acts.ifSuccess(task);
                Log.d("DB_Post", path+"success");
            }
            else{
                acts.ifFail(task);
                Log.d("DB_Post", path+"fail");
            }
        });
    }

    public void readPost(ArrayList<Post> returnList,String title, BrandQuery con, Acts acts){
        String path = "firebase.Database.readPost - ";

        titleRoot.child(title).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Long postnumber1 = Long.parseLong(task.getResult().getValue(String.class));

                postRoot.child(String.valueOf(postnumber1)).get().addOnCompleteListener(task1->{

                    for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                        returnList.add(dataSnapshot.getValue(Post.class));
                    }
                    acts.ifSuccess(task);
                    Log.d("DB_Post", path+"success");
                });
            }
            else{
                acts.ifFail(task);
                Log.d("DB_Post", path+"fail");
            }
        });
    }
}
