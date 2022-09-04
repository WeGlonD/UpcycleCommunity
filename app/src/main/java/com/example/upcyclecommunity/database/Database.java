package com.example.upcyclecommunity.database;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.example.upcyclecommunity.R;

import java.util.ArrayList;

public class Database {
    private static DatabaseReference mDBRoot = null;
    private static DatabaseReference brandRoot = null;

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
}