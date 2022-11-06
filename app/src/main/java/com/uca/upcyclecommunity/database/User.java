package com.uca.upcyclecommunity.database;

import android.content.Context;
import android.util.Log;

import com.uca.upcyclecommunity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.IgnoreExtraProperties;

public class User {
    @IgnoreExtraProperties
    public static class  Data{
        private Boolean isManager;
        private String name;

        public Data() {
        }

        public Data(String name){
            this.isManager = false;
            this.name = name;
        }

        public Data(Boolean isManager, String name){
            this.isManager = isManager;
            this.name = name;
        }

        public Boolean getManager() {
            return isManager;
        }

        public String getName() {
            return name;
        }

        public void setManager(Boolean manager) {
            isManager = manager;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static Context context = null;


    public static void getInstance(){
        Database db = new Database();
    }

    public User(){
        getInstance();
    }
    public User(Context context){
        getInstance();
        this.context = context;
    }

    public void create(String email, String password, Acts acts){
        Database db = new Database();
        FirebaseAuth mAuth = db.getAuth();
        String path = "firebase.User.create - ";

        mAuth.createUserWithEmailAndPassword(email, password).
            addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    acts.ifSuccess(task);
                    Log.d(context.getString(R.string.Dirtfy_test), path+"success");
                } else {
                    acts.ifFail(task);
                    Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
                }
            });
    }

    public void delete(Acts acts){
        Database db = new Database();
        FirebaseAuth mAuth = db.getAuth();
        String path = "firebase.User.delete - ";

        db.removeUserValueEventListener();
        db.getUserRoot().child(db.getAuth().getCurrentUser().getUid()).
                removeValue().
                addOnCompleteListener(valueTask -> {
                    if(valueTask.isSuccessful()){
                        Log.d(context.getString(R.string.Dirtfy_test), path+"value success");
                        mAuth.getCurrentUser().delete().
                                addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        acts.ifSuccess(task);
                                        Log.d(context.getString(R.string.Dirtfy_test), path+"success");
                                    } else {
                                        acts.ifFail(task);
                                        Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
                                    }
                                });
                    }
                    else{
                        Log.d(context.getString(R.string.Dirtfy_test), path+"value fail");
                    }

        });
    }

    public void login(String email, String password, Acts acts){
        Database db = new Database();
        FirebaseAuth mAuth = db.getAuth();
        String path = "firebase.User.login - ";

        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                acts.ifSuccess(task);
                Log.d(context.getString(R.string.Dirtfy_test), path+"success");
            }
            else{
                acts.ifFail(task);
                Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
            }
        });
    }

    public void logout(){
        Database db = new Database();
        FirebaseAuth mAuth = db.getAuth();
        String path = "firebase.User.logout - ";

        try {
            mAuth.signOut();
            Log.d(context.getString(R.string.Dirtfy_test), path+"success");
        }
        catch (Exception e){
            Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
        }
    }
}
