package com.example.upcyclecommunity.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.example.upcyclecommunity.R;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Database {
    private static DatabaseReference mDBRoot = null;
    private static DatabaseReference brandRoot = null;
    private static DatabaseReference postRoot = null;
    private static DatabaseReference titleRoot = null;
    private static DatabaseReference tagRoot = null;
    private static DatabaseReference timeRoot = null;

    private static StorageReference mStorage = null;
    private static StorageReference brandpictureRoot = null;

    private static FirebaseAuth mAuth = null;

    public static Context context;

    public static boolean getInstance(){
        try{
            if(mAuth == null)
                mAuth = FirebaseAuth.getInstance();
            if (mStorage == null)
                mStorage = FirebaseStorage.getInstance().getReference();
            if (mStorage != null)
                brandpictureRoot = mStorage.child("Brand picture");
            if (mDBRoot == null)
                mDBRoot = FirebaseDatabase.getInstance().getReference();
            if (mDBRoot != null){
                if (brandRoot == null)
                    brandRoot = mDBRoot.child("Brand");
                if (postRoot == null)
                    postRoot = mDBRoot.child("posting");
                if (titleRoot == null)
                    titleRoot = mDBRoot.child("title");
                if (tagRoot == null)
                    tagRoot = mDBRoot.child("tag");
                if (timeRoot == null)
                    timeRoot = mDBRoot.child("time");
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

    public DatabaseReference getBrandRoot() { return brandRoot; }

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

    public void writePost(ArrayList<String> data,String title,ArrayList<String> tags){
        postRoot.child("totalnumber").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Long postnumber = task.getResult().getValue(Long.class);
                postnumber++;

                for(int i = 1;i<=data.size();i++) {
                    postRoot.child(String.valueOf(postnumber)).child(i+"").setValue(data.get(i-1));
                }

                titleRoot.child(title).setValue(postnumber);

                timeRoot.child((new Timestamp(System.currentTimeMillis())).toString()).setValue(postnumber);
                for(int i = 0;i<data.size();i++) {
                    tagRoot.child(tags.get(i)).setValue(postnumber);
                }

                postRoot.child("totalnumber").setValue(postnumber);
            }
            else{
                Toast.makeText(context, "실패!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void readAllPost(ArrayList<String> returnList, BrandQuery con, Acts acts){
        String path = "firebase.Database.readAllPost - ";

        postRoot.child("posting").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                    returnList.add(dataSnapshot.getValue(String.class));
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

    public void readPost(ArrayList<String> returnList,Long postnumber, BrandQuery con, Acts acts){
        String path = "firebase.Database.readPost - ";

        postRoot.child("posting").child(String.valueOf(postnumber)).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                    returnList.add(dataSnapshot.getValue(String.class));
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

    public void writePost(ArrayList<String> data,String title,ArrayList<String> tags){
        postRoot.child("totalnumber").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Long postnumber = task.getResult().getValue(Long.class);
                postnumber++;

                for(int i = 1;i<=data.size();i++) {
                    postRoot.child(String.valueOf(postnumber)).child(i+"").setValue(data.get(i-1));
                }

                titleRoot.child(title).setValue(postnumber);

                timeRoot.child((new Timestamp(System.currentTimeMillis())).toString()).setValue(postnumber);
                for(int i = 0;i<data.size();i++) {
                    tagRoot.child(tags.get(i)).setValue(postnumber);
                }

                postRoot.child("totalnumber").setValue(postnumber);
            }
            else{
                Toast.makeText(context, "실패!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void readAllPost(ArrayList<String> returnList, BrandQuery con, Acts acts){
        String path = "firebase.Database.readAllPost - ";

        postRoot.child("posting").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                    returnList.add(dataSnapshot.getValue(String.class));
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

    public void readPost(ArrayList<String> returnList,Long postnumber, BrandQuery con, Acts acts){
        String path = "firebase.Database.readPost - ";

        postRoot.child("posting").child(String.valueOf(postnumber)).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                    returnList.add(dataSnapshot.getValue(String.class));
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
}
