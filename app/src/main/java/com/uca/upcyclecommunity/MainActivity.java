package com.uca.upcyclecommunity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.uca.upcyclecommunity.community1.Fragment_CM1;
import com.uca.upcyclecommunity.BrandList.FragmentBrand;
import com.uca.upcyclecommunity.community1.WritePostActivity;
import com.uca.upcyclecommunity.community1.communityAdapter;
import com.uca.upcyclecommunity.community2.community2Adapter;
import com.uca.upcyclecommunity.community2.community2_upload;
import com.uca.upcyclecommunity.database.Acts;
import com.uca.upcyclecommunity.community2.Fragment_CM2;
import com.uca.upcyclecommunity.database.Database;
import com.uca.upcyclecommunity.database.User;
import com.uca.upcyclecommunity.mylocation.MyLocation;
import com.uca.upcyclecommunity.mypage.LoginActivity;
import com.uca.upcyclecommunity.mypage.MyPageFragment;
import com.uca.upcyclecommunity.recruit.recruit_list;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SearchView searchview;
    FragmentTransaction fragmentTransaction;
    Context mainContext;
    BottomNavigationView main_bottom;
    public int mode = 1;
    public final int Namesearch = 1;
    public final int Tagsearch = 2;
    int currentTab = 0;
    int category = 0;
    public static MyLocation location;
    RecyclerView CommunityRecycler;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    String[] REQUIRED_PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    community2Adapter.clickListener mclickListener;
    boolean searching = false;
    ConstraintLayout nosearch;
    MenuItem searchItem;
    MenuItem NameItem;
    MenuItem TagItem;
    FloatingActionButton fab;

    FragmentBrand BrandListTab;
    MyPageFragment MyPageTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, Loading.class);
        startActivity(intent);
        mainContext = this;
        nosearch = findViewById(R.id.no_search);
        mclickListener = new community2Adapter.clickListener() {
            @Override
            public void mclickListener_Dialog(String postNumber) {
                if (Database.getAuth().getCurrentUser() != null){
                    Database.getDBRoot().child("Post2").child("posting").
                            child(postNumber).child("writer").get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()){
                                    String user_uid = ((Task<DataSnapshot>) task).getResult().getValue(String.class);
                                    if(user_uid.equals(Database.getAuth().getCurrentUser().getUid()))
                                        Dialog(postNumber);
                                    else
                                        recruitDialog(postNumber);
                                }
                            });
                }
            }
        };


        Database db = new Database(this);
        User user = new User(this);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        main_bottom = findViewById(R.id.main_bottom);
        main_bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setFragment(item.getItemId());
                return true;
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.floating_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentTab == 2){
                    if(Database.getAuth().getCurrentUser()!=null) {
                        Intent intent = new Intent(mainContext, WritePostActivity.class);
                        intent.putExtra("postn", Long.MAX_VALUE + "");
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(mainContext, "????????? ??? ???????????? ????????? ??? ????????????!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(mainContext, LoginActivity.class);
                        startActivity(intent);
                    }
                }
                else if(currentTab==1){
                    if(Database.getAuth().getCurrentUser()!=null) {
                        Intent intent = new Intent(mainContext, community2_upload.class);
                        intent.putExtra("postn", Long.MAX_VALUE + "");
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(mainContext, "????????? ??? ???????????? ????????? ??? ????????????!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(mainContext, LoginActivity.class);
                        startActivity(intent);
                    }
                }
                else if (currentTab==3){
                    if(BrandListTab.Filter.getVisibility()==View.VISIBLE){
//                    (root.findViewById(R.id.FilterTitleText)).setVisibility(View.VISIBLE);
                        BrandListTab.Filter.setVisibility(View.GONE);
                    }
                    else{
//                    (root.findViewById(R.id.FilterTitleText)).setVisibility(View.INVISIBLE);
                        BrandListTab.Filter.setVisibility(View.VISIBLE);
                    }
                }
                else if (currentTab==4){
                    if(Database.getAuth().getCurrentUser() == null){
                        Intent it = new Intent(mainContext, LoginActivity.class);
                        startActivity(it);
                    }
                    else {
                        Database.getAuth().signOut();
                        MyPageTab.onResume();
                        Toast.makeText(mainContext, "logout", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mRequestPermission();
        if(checkPermission()) {
            if (!checkLocationServicesStatus()) {
                showDialogForLocationServiceSetting();
            }
        }

//        findViewById(R.id.position).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();
//                Toast.makeText(MainActivity.this, "??????: "+latitude+"\n??????: "+longitude, Toast.LENGTH_LONG).show();
//            }
//        });

        //????????? ??????????????? ???????????? ??? ???.
        //setFragment(R.id.bottom_community1);
    }

    @Override
    protected void onStop() {
        //FirebaseAuth.getInstance().signOut();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        FirebaseAuth.getInstance().signOut();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener no = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //???
                dialog.dismiss();
            }
        };
        DialogInterface.OnClickListener yes = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //?????????
                finish();
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(mainContext)
                .setTitle("?????? ?????????????????????????")
                .setPositiveButton("?????????", no)
                .setNegativeButton("???",yes)
                .show();
    }

    public void setFragment(int n) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (n) {
            case R.id.bottom_community1:
                if(currentTab==1) break;
                Fragment_CM2 Community2 = new Fragment_CM2();
                fragmentTransaction.replace(R.id.main_frame, Community2).commitAllowingStateLoss();
                category = 2;
                currentTab = 1;
                fab.setImageResource(R.drawable.upload);
                searchItem.setVisible(true);
                NameItem.setVisible(true);
                TagItem.setVisible(true);
                nosearch = findViewById(R.id.no_search);
                //Toast.makeText(this, "??????1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_community2:
                if(currentTab==2) break;
                Fragment_CM1 Community1 = new Fragment_CM1();
                fragmentTransaction.replace(R.id.main_frame,Community1).commit();
                category = 1;
                currentTab = 2;
                fab.setImageResource(R.drawable.upload);
                nosearch = findViewById(R.id.no_search2);
                searchItem.setVisible(true);
                NameItem.setVisible(true);
                TagItem.setVisible(true);
                //Toast.makeText(this, "??????2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_brandlist:
                if(currentTab==3) break;
                BrandListTab = new FragmentBrand();
                fragmentTransaction.replace(R.id.main_frame, BrandListTab).commit();
                category = 3;
                currentTab = 3;
                fab.setImageResource(R.drawable.search);
                searchItem.setVisible(false);
                NameItem.setVisible(false);
                TagItem.setVisible(false);
                //Toast.makeText(this, "?????????", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_mypage:
                if(currentTab==4) break;
                MyPageTab = new MyPageFragment();
                fragmentTransaction.replace(R.id.main_frame, MyPageTab).commit();
                category = 4;
                currentTab = 4;
                fab.setImageResource(R.drawable.ic_baseline_login_24);
                searchItem.setVisible(false);
                NameItem.setVisible(false);
                TagItem.setVisible(false);
                //Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void mRequestPermission(){
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){
            //startLocationUpdates();
            location = new MyLocation(this);
        }else{
            //????????? ????????? ??? ?????? ??????
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[0])){
                Snackbar.make(findViewById(R.id.main_frame), "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.", Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            }
            else{
                //????????? ?????? ?????? ????????? ????????? ?????? ?????? ???
                //??????????????? onRequestPermissionResult??? ??????
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    private boolean checkPermission(){
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    //ActivityCompat.requestPermissions ????????? ????????? ????????? ???????????? ?????????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;

            //?????? ????????? ??????????????? ??????
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if (check_result) {
                //????????? ??????????????? ?????????????????? ??????
                //startLocationUpdates();
                location = new MyLocation(this);
            } else {
                //????????? ???????????? ????????? ??????
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    //????????? ????????? ????????? ?????? ??? ???????????? ????????? ?????? ??????
                    Snackbar.make(findViewById(R.id.main_frame), "????????? ?????????????????????. ?????? ?????? ???????????? ????????? ??????????????????.", Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                } else {
                    //?????????????????? ????????? ???????????? ????????? ???????????????
                    Snackbar.make(findViewById(R.id.main_frame), "????????? ?????????????????????. ??????(??? ??????)?????? ????????? ???????????? ?????????.", Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }

    public boolean checkLocationServicesStatus(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n" + "?????? ????????? ???????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case GPS_ENABLE_REQUEST_CODE:
                //gps?????? ??????
                if(checkLocationServicesStatus()){
                    if(checkLocationServicesStatus()){
                        Log.d("WeGlonD", "onActivityResult : GPS ????????? ?????????");
                        location = new MyLocation(this);
                        return;
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        searchItem = menu.findItem(R.id.search);
        searchview = (SearchView)searchItem.getActionView();
        searchview.setMaxWidth(Integer.MAX_VALUE);
        searchview.setQueryHint("???????????? ???????????????");
        mode = Namesearch;

        NameItem = menu.findItem(R.id.menu_name);
        TagItem = menu.findItem(R.id.menu_tag);

        NameItem.setOnMenuItemClickListener(modelistener);
        TagItem.setOnMenuItemClickListener(modelistener);

        searchview.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //??? ???????????????
                if(mode == Namesearch){
                    Log.d("minseok", "onQueryTextSubmit - call searchName");
                    if(searching)searchName(query);
                }
                else if(mode == Tagsearch){
                    Log.d("minseok", "onQueryTextSubmit - call searchTag");
                    if(searching)searchTag(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //??? ????????? ????????? ???????????? ??????
                if(mode == Namesearch){
                    Log.d("minseok", "onQueryTextChange - call searchName");
                    if(searching)searchName(newText);
                }
                else if(mode == Tagsearch){
                    Log.d("minseok", "onQueryTextChange - call searchTag");
                    if(searching)searchTag(newText);
                }
                return false;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {

                if(currentTab == 1){
                    CommunityRecycler = findViewById(R.id.title_community2);
                    nosearch = findViewById(R.id.no_search);
                    nosearch.setVisibility(View.VISIBLE);
                }
                else if(currentTab == 2){
                    CommunityRecycler = findViewById(R.id.title_community1);
                    nosearch = findViewById(R.id.no_search2);
                    nosearch.setVisibility(View.VISIBLE);
                }
                CommunityRecycler.setVisibility(View.GONE);

                main_bottom.setVisibility(View.INVISIBLE);

                searching = true;
                TagItem.setVisible(false);
                NameItem.setVisible(false);
                Log.d("minseok","onMenuItemActionExpand called");
                if(mode == Namesearch){
                    Log.d("minseok",mode+"");
                    searchview.setQueryHint("????????? ???????????????.");
                }
                else if(mode == Tagsearch){
                    Log.d("minseok",mode+"");
                    searchview.setQueryHint("????????? ???????????????.");
                }
                return true;
            }

            ///????????? ???????????? ?????? ???????????? ?????? ????????? ????????? ??????????????? ?????????????????? ????????? ????????? ????????? ?????? ????????? ????????? ?????? ????????? ?????? ???????????? ??????????????? ?????? ?????? ?????? ????????? ??????
            /// ????????? ????????? ???????????? ????????? ??????????????? ????????? ??????????????? ?????????

            //????????? ??????????????? ???
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                CommunityRecycler.setVisibility(View.VISIBLE);
                nosearch.setVisibility(View.GONE);
                searching = false;
                Log.d("minseok","onMenuItemActionCollapse called");
                //if(searchItem.isVisible()) Toast.makeText(mainContext, "?????????", Toast.LENGTH_SHORT).show();
                TagItem.setVisible(true);
                NameItem.setVisible(true);
                main_bottom.setVisibility(View.VISIBLE);
//                searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//                searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                Database db = new Database(mainContext);
                ArrayList<Long> listData = new ArrayList<>();
                db.readAllPost(listData, category+"", new Acts() {
                    @Override
                    public void ifSuccess(Object task) {
                        if(currentTab == 1)CommunityRecycler.setAdapter(new community2Adapter(listData,mainContext,mclickListener));
                        else if(currentTab==2)CommunityRecycler.setAdapter(new communityAdapter(listData, mainContext));
                    }

                    @Override
                    public void ifFail(Object task) {
                        Toast.makeText(mainContext, "error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
                return true;
            }
        });
        setFragment(R.id.bottom_community1);
        return super.onCreateOptionsMenu(menu);
    }

    MenuItem.OnMenuItemClickListener modelistener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(menuItem.getTitle().equals("???????????? ??????")){
                mode = Namesearch;
//                searchview.setQueryHint("???????????? ???????????????.");
                Log.d("minseok","hdf2");
            }
            else{
                mode = Tagsearch;
//                searchview.setQueryHint("????????? ???????????????.");
                Log.d("minseok","hdf3");
            }
            return false;
        }
    };

    private void searchName(String keyword){
        ArrayList<Long> searched = new ArrayList<>();

//        registerForContextMenu(CommunityRecycler);
        Database db = new Database(getApplicationContext());
        ArrayList<Long> empty = new ArrayList<>();
        db.readName(searched, keyword, category+"", new Acts() {
            @Override
            public void ifSuccess(Object task) {
                if(!searching){
                    Database db = new Database(mainContext);
                    ArrayList<Long> listData = new ArrayList<>();
                    db.readAllPost(listData, category+"", new Acts() {
                        @Override
                        public void ifSuccess(Object task) {
                            if(currentTab == 1)CommunityRecycler.setAdapter(new community2Adapter(listData,mainContext,mclickListener));
                            else if(currentTab==2)CommunityRecycler.setAdapter(new communityAdapter(listData, mainContext));
                        }

                        @Override
                        public void ifFail(Object task) {
                            Toast.makeText(mainContext, "error", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
                }
                else{
                    if (searched != null) {
                        Log.d("minseok", "searchName - ifSuccess - searched is NOT null");
                        if(keyword.equals("")||searched.size()==0){
                            CommunityRecycler.setVisibility(View.GONE);
                            nosearch.setVisibility(View.VISIBLE);
                            //CommunityRecycler.setAdapter(new communityAdapter(searched,mainContext));
                        }
                        else{
                            CommunityRecycler.setVisibility(View.VISIBLE);
                            nosearch.setVisibility(View.GONE);
                            if(currentTab == 1)CommunityRecycler.setAdapter(new community2Adapter(searched,mainContext,mclickListener));
                            else if(currentTab==2)CommunityRecycler.setAdapter(new communityAdapter(searched, mainContext));
                        }
                    }
                    else{
                        Log.d("minseok", "searchName - ifSuccess - searched is null");
                    }
                }
            }

            @Override
            public void ifFail(Object task) {
                Log.d("minseok", "searchName - ifFail");
            }
        });
    }


    private void searchTag(String keyword){
        ArrayList<Long> searched = new ArrayList<>();
        if(currentTab == 1)CommunityRecycler = findViewById(R.id.title_community2);
        else if(currentTab == 2)CommunityRecycler = findViewById(R.id.title_community1);
        //registerForContextMenu(CommunityRecycler);
        Database db = new Database(getApplicationContext());
        db.readTag(searched, keyword, category+"", new Acts() {
            @Override
            public void ifSuccess(Object task) {
                if (searched != null) {
                    if(keyword.equals("")||searched.size()==0){
                        CommunityRecycler.setVisibility(View.GONE);
                        nosearch.setVisibility(View.VISIBLE);
                        //CommunityRecycler.setAdapter(new communityAdapter(searched,mainContext));
                    }
                    else{
                        CommunityRecycler.setVisibility(View.VISIBLE);
                        nosearch.setVisibility(View.GONE);
                        if(currentTab==1)CommunityRecycler.setAdapter(new community2Adapter(searched,mainContext,mclickListener));
                        else if(currentTab==2)CommunityRecycler.setAdapter(new communityAdapter(searched,mainContext));
                    }
                }
            }
            @Override
            public void ifFail(Object task) {

            }
        });
    }

    public void Dialog(String postNumber){
        DialogInterface.OnClickListener ammendimage = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //??????
                change_posting(postNumber,1);
                dialog.dismiss();
            }
        };
        DialogInterface.OnClickListener deleteimage = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //??????
                change_posting(postNumber,2);
                dialog.dismiss();
            }
        };
        DialogInterface.OnClickListener recruitPosting = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent it = new Intent(mainContext, recruit_list.class);
                it.putExtra("postn",Long.MAX_VALUE+"");
                it.putExtra("recruitPostnum", postNumber);
                startActivity(it);
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(mainContext)
                .setTitle("????????? ??????(?????? ???????????? ??????)")
                .setPositiveButton("????????? ??????", ammendimage)
                .setNeutralButton("?????? ??????",recruitPosting)
                .setNegativeButton("????????? ??????", deleteimage)
                .show();
    }

    public void recruitDialog(String postNumber){
        DialogInterface.OnClickListener recruitPosting = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent it = new Intent(mainContext, recruit_list.class);
                it.putExtra("postn",Long.MAX_VALUE+"");
                it.putExtra("recruitPostnum", postNumber);
                startActivity(it);
                dialog.dismiss();
            }
        };
        DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(mainContext)
                .setTitle("")
                .setPositiveButton("?????? ??????", recruitPosting)
                .setNegativeButton("??????",cancel)
                .show();
    }
    public void change_posting(String postNumber, int mode){
        Database db = new Database();
        Database.getDBRoot().child("Post"+category).child("posting").child(postNumber).child("writer").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String writerUid = task.getResult().getValue(String.class);
                if(Database.getAuth().getCurrentUser()==null){
                    Toast.makeText(mainContext, "????????? ??? ???????????? ??????/????????? ??? ????????????!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mainContext, LoginActivity.class);
                    startActivity(intent);
                }else {
                    Log.d("WeGlonD", "get : " + Database.getAuth().getCurrentUser().getUid());
                    Log.d("WeGlonD", "post writer : " + writerUid);
                    if (writerUid.equals(Database.getAuth().getCurrentUser().getUid())) {
                        switch (mode) {
                            case 1:
                                //?????? ??????
                                Intent it = new Intent(mainContext, community2_upload.class);
                                it.putExtra("postn", postNumber);
                                startActivity(it);
                                //finish();
                                break;
                            case 2:
                                db.deletePost(Long.parseLong(postNumber), writerUid, category+"", "", new Acts() {
                                    @Override
                                    public void ifSuccess(Object task) {
                                        //finish();
                                    }
                                    @Override
                                    public void ifFail(Object task) {
                                        Toast.makeText(mainContext, "????????????", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                        }
                    } else {
                        Toast.makeText(mainContext, "????????? ???????????? ??????/?????? ???????????????", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}