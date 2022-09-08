package com.example.upcyclecommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.upcyclecommunity.community1.Fragment_CM1;
import com.example.upcyclecommunity.BrandList.FragmentBrand;
import com.example.upcyclecommunity.community1.TitleInfo;
import com.example.upcyclecommunity.community1.communityAdapter;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.community2.Fragment_CM2;
import com.example.upcyclecommunity.database.Database;
import com.example.upcyclecommunity.database.Database;
import com.example.upcyclecommunity.database.User;
import com.example.upcyclecommunity.mypage.MyPageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SearchView searchview;
    FragmentTransaction fragmentTransaction;
    Context mainContext;
    public int mode = 1;
    public final int Namesearch = 1;
    public final int Tagsearch = 2;
    int currentTab = 0;
    RecyclerView CommunityRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainContext = this;

        Database db = new Database(this);
        User user = new User(this);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        BottomNavigationView main_bottom = findViewById(R.id.main_bottom);
        main_bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setFragment(item.getItemId());
                return true;
            }
        });

        //첫번째 프래그먼트 띄우도록 할 것.
        setFragment(R.id.bottom_community1);
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
                Fragment_CM1 Community1 = new Fragment_CM1();
                fragmentTransaction.replace(R.id.main_frame,Community1).commit();
                currentTab = 1;
                //Toast.makeText(this, "커뮤1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_community2:
                if(currentTab==2) break;
                Fragment_CM2 Community2 = new Fragment_CM2();
                fragmentTransaction.replace(R.id.main_frame, Community2).commit();
                currentTab = 2;
                //Toast.makeText(this, "커뮤2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_brandlist:
                if(currentTab==3) break;
                FragmentBrand BrandListTab = new FragmentBrand();
                fragmentTransaction.replace(R.id.main_frame,BrandListTab).commit();
                currentTab = 3;
                //Toast.makeText(this, "브랜드", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_mypage:
                if(currentTab==4) break;
                MyPageFragment MyPageTab = new MyPageFragment();
                fragmentTransaction.replace(R.id.main_frame, MyPageTab).commit();
                currentTab = 4;
                //Toast.makeText(this, "마이페이지", Toast.LENGTH_SHORT).show();
                break;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchview = (SearchView)searchItem.getActionView();
        searchview.setMaxWidth(Integer.MAX_VALUE);
        searchview.setQueryHint("제목으로 검색합니다");
        mode = Namesearch;

        MenuItem NameItem = menu.findItem(R.id.menu_name);
        MenuItem TagItem = menu.findItem(R.id.menu_tag);

        NameItem.setOnMenuItemClickListener(modelistener);
        TagItem.setOnMenuItemClickListener(modelistener);

        searchview.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //다 검색완료후
                if(mode == Namesearch){
                    Log.d("minseok", "onQueryTextSubmit - call searchName");
                    searchName(query);
                }
                else if(mode == Tagsearch){
                    Log.d("minseok", "onQueryTextSubmit - call searchTag");
                    searchTag(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //칠 때마다 텍스트 하나하나 입력
                if(mode == Namesearch){
                    Log.d("minseok", "onQueryTextChange - call searchName");
                    searchName(newText);
                }
                else if(mode == Tagsearch){
                    Log.d("minseok", "onQueryTextChange - call searchTag");
                    searchTag(newText);
                }
                return false;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
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
                Log.d("minseok","onMenuItemActionCollapse called");
                Database db = new Database(mainContext);
                ArrayList<Long> listData = new ArrayList<>();
                db.readAllPost(listData, currentTab+"", new Acts() {
                    @Override
                    public void ifSuccess(Object task) {
                        CommunityRecycler.setAdapter(new communityAdapter(listData, mainContext));
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
        CommunityRecycler = findViewById(R.id.title_community1);
//        registerForContextMenu(CommunityRecycler);
        Database db = new Database(getApplicationContext());
        db.readName(searched, keyword, currentTab+"", new Acts() {
            @Override
            public void ifSuccess(Object task) {

                if (searched != null) {
                    Log.d("minseok", "searchName - ifSuccess - searched is NOT null");
                    CommunityRecycler.setAdapter(new communityAdapter(searched, mainContext));
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
        CommunityRecycler = findViewById(R.id.title_community1);
        //registerForContextMenu(CommunityRecycler);
        Database db = new Database(getApplicationContext());
        db.readTag(searched, keyword, currentTab+"", new Acts() {
            @Override
            public void ifSuccess(Object task) {
                if (searched != null) {
                    CommunityRecycler.setAdapter(new communityAdapter(searched,mainContext));
                }
            }
            @Override
            public void ifFail(Object task) {

            }
        });
    }
}