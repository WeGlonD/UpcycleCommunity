package com.example.upcyclecommunity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.upcyclecommunity.community1.Fragment_CM1;
import com.example.upcyclecommunity.BrandList.FragmentBrand;
import com.example.upcyclecommunity.community1.TitleInfo;
import com.example.upcyclecommunity.community1.WritePostActivity;
import com.example.upcyclecommunity.community1.communityAdapter;
import com.example.upcyclecommunity.community2.community2Adapter;
import com.example.upcyclecommunity.community2.community2_upload;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.community2.Fragment_CM2;
import com.example.upcyclecommunity.database.Database;
import com.example.upcyclecommunity.database.Database;
import com.example.upcyclecommunity.database.User;
import com.example.upcyclecommunity.mylocation.MyLocation;
import com.example.upcyclecommunity.mypage.LoginActivity;
import com.example.upcyclecommunity.mypage.MyPageFragment;
import com.example.upcyclecommunity.recruit.recruit_list;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.lang.reflect.Array;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                if(currentTab == 1){
                    if(Database.getAuth().getCurrentUser()!=null) {
                        Intent intent = new Intent(mainContext, WritePostActivity.class);
                        intent.putExtra("postn", Long.MAX_VALUE + "");
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(mainContext, "로그인 후 게시물을 작성할 수 있습니다!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(mainContext, LoginActivity.class);
                        startActivity(intent);
                    }
                }
                else if(currentTab==2){
                    if(Database.getAuth().getCurrentUser()!=null) {
                        Intent intent = new Intent(mainContext, community2_upload.class);
                        intent.putExtra("postn", Long.MAX_VALUE + "");
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(mainContext, "로그인 후 게시물을 작성할 수 있습니다!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(mainContext, LoginActivity.class);
                        startActivity(intent);
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
//                Toast.makeText(MainActivity.this, "위도: "+latitude+"\n경도: "+longitude, Toast.LENGTH_LONG).show();
//            }
//        });

        //첫번째 프래그먼트 띄우도록 할 것.
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

    public void setFragment(int n) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (n) {
            case R.id.bottom_community1:
                if(currentTab==1) break;
                Fragment_CM2 Community2 = new Fragment_CM2();
                fragmentTransaction.replace(R.id.main_frame, Community2).commit();
                category = 2;
                currentTab = 1;
                fab.setImageResource(R.drawable.upload);
                searchItem.setVisible(true);
                NameItem.setVisible(true);
                TagItem.setVisible(true);
                nosearch = findViewById(R.id.no_search);
                //Toast.makeText(this, "커뮤1", Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(this, "커뮤2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_brandlist:
                if(currentTab==3) break;
                FragmentBrand BrandListTab = new FragmentBrand();
                fragmentTransaction.replace(R.id.main_frame, BrandListTab).commit();
                category = 3;
                currentTab = 3;
                fab.setImageResource(R.drawable.search);
                searchItem.setVisible(false);
                NameItem.setVisible(false);
                TagItem.setVisible(false);
                //Toast.makeText(this, "브랜드", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_mypage:
                if(currentTab==4) break;
                MyPageFragment MyPageTab = new MyPageFragment();
                fragmentTransaction.replace(R.id.main_frame, MyPageTab).commit();
                category = 4;
                currentTab = 4;
                fab.setImageResource(R.drawable.ic_baseline_login_24);
                searchItem.setVisible(false);
                NameItem.setVisible(false);
                TagItem.setVisible(false);
                //Toast.makeText(this, "마이페이지", Toast.LENGTH_SHORT).show();
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
            //퍼미션 거부한 적 있는 경우
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[0])){
                Snackbar.make(findViewById(R.id.main_frame), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            }
            else{
                //퍼미션 거부 한적 없으면 퍼미션 요청 바로 함
                //요청결과는 onRequestPermissionResult에 수신
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

    //ActivityCompat.requestPermissions 퍼미션 요청의 결과를 리턴받는 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;

            //모든 퍼미션 허용했는지 체크
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if (check_result) {
                //퍼미션 허용했으니 위치업데이트 시작
                //startLocationUpdates();
                location = new MyLocation(this);
            } else {
                //퍼미션 거부된거 있으면 종료
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    //유저가 거부만 선택한 경우 앱 재실행시 퍼미션 허용 가능
                    Snackbar.make(findViewById(R.id.main_frame), "권한이 거부되었습니다. 앱을 다시 실행하여 권한을 허용해주세요.", Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                } else {
                    //다시묻지않음 체크시 설정에서 퍼미션 허용해야함
                    Snackbar.make(findViewById(R.id.main_frame), "권한이 거부되었습니다. 설정(앱 정보)에서 권한을 허용해야 합니다.", Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
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
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
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
                //gps활성 체크
                if(checkLocationServicesStatus()){
                    if(checkLocationServicesStatus()){
                        Log.d("WeGlonD", "onActivityResult : GPS 활성화 돼있음");
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
        searchview.setQueryHint("제목으로 검색합니다");
        mode = Namesearch;

        NameItem = menu.findItem(R.id.menu_name);
        TagItem = menu.findItem(R.id.menu_tag);

        NameItem.setOnMenuItemClickListener(modelistener);
        TagItem.setOnMenuItemClickListener(modelistener);

        searchview.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //다 검색완료후
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
                //칠 때마다 텍스트 하나하나 입력
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
                    searchview.setQueryHint("제목을 검색합니다.");
                }
                else if(mode == Tagsearch){
                    Log.d("minseok",mode+"");
                    searchview.setQueryHint("태그를 검색합니다.");
                }
                return true;
            }

            ///검색을 실행하던 도중 뒤로가기 하면 콜랍스 함수가 실행되지만 익스팬더블이 뜨려고 했지만 검색에 검색 중이던 함수가 다시 작동이 되서 어댑터에 리사이클류 그게 뜨지 않고 오류가 발생
            /// 콜랩스 함수가 시작되면 무조건 입력중이던 함수는 적용안되게 수정함

            //검색이 종료되었을 때
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                CommunityRecycler.setVisibility(View.VISIBLE);
                nosearch.setVisibility(View.GONE);
                searching = false;
                Log.d("minseok","onMenuItemActionCollapse called");
                //if(searchItem.isVisible()) Toast.makeText(mainContext, "보이니", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(mainContext, "hello", Toast.LENGTH_SHORT).show();
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
            if(menuItem.getTitle().equals("제목으로 검색")){
                mode = Namesearch;
//                searchview.setQueryHint("제목으로 검색합니다.");
                Log.d("minseok","hdf2");
            }
            else{
                mode = Tagsearch;
//                searchview.setQueryHint("태그로 검색합니다.");
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
                //수정
                change_posting(postNumber,1);
                dialog.dismiss();
            }
        };
        DialogInterface.OnClickListener deleteimage = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //삭제
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
                .setTitle("게시물 변경(본인 게시물만 가능)")
                .setPositiveButton("게시물 수정", ammendimage)
                .setNeutralButton("모임 조회",recruitPosting)
                .setNegativeButton("게시물 삭제", deleteimage)
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
                .setPositiveButton("모임 조회", recruitPosting)
                .setNegativeButton("취소",cancel)
                .show();
    }
    public void change_posting(String postNumber, int mode){
        Database db = new Database();
        Database.getDBRoot().child("Post"+category).child("posting").child(postNumber).child("writer").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String writerUid = task.getResult().getValue(String.class);
                if(Database.getAuth().getCurrentUser()==null){
                    Toast.makeText(mainContext, "로그인 후 게시물을 수정/삭제할 수 있습니다!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mainContext, LoginActivity.class);
                    startActivity(intent);
                }else {
                    Log.d("WeGlonD", "get : " + Database.getAuth().getCurrentUser().getUid());
                    Log.d("WeGlonD", "post writer : " + writerUid);
                    if (writerUid.equals(Database.getAuth().getCurrentUser().getUid())) {
                        switch (mode) {
                            case 1:
                                //수정 코드
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
                                        Toast.makeText(mainContext, "삭제실패", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                        }
                    } else {
                        Toast.makeText(mainContext, "자신의 게시물만 수정/삭제 가능합니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}