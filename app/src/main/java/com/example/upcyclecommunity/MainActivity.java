package com.example.upcyclecommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.upcyclecommunity.community1.Fragment_CM1;
import com.example.upcyclecommunity.BrandList.FragmentBrand;
import com.example.upcyclecommunity.community1.TitleInfo;
import com.example.upcyclecommunity.community1.communityAdapter;
import com.example.upcyclecommunity.database.Database;
import com.example.upcyclecommunity.database.Database;
import com.example.upcyclecommunity.database.User;
import com.example.upcyclecommunity.mypage.MyPageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SearchView searchview;
    FragmentTransaction fragmentTransaction;
    public int mode = 1;
    public final int Namesearch = 1;
    public final int Tagsearch = 2;
    RecyclerView CommunityRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Fragment_CM1 Community1 = new Fragment_CM1();
                fragmentTransaction.replace(R.id.main_frame,Community1).commit();
                //Toast.makeText(this, "커뮤1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_community2:
                //fragmentTransaction.replace(R.id.main_frame, Fragment2).commit();
                //Toast.makeText(this, "커뮤2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_brandlist:
                FragmentBrand BrandListTab = new FragmentBrand();
                fragmentTransaction.replace(R.id.main_frame,BrandListTab).commit();
                //Toast.makeText(this, "브랜드", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_mypage:
                MyPageFragment MyPageTab = new MyPageFragment();
                fragmentTransaction.replace(R.id.main_frame, MyPageTab).commit();
                //Toast.makeText(this, "마이페이지", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);

        searchview = (SearchView)menu.findItem(R.id.search).getActionView();
        searchview.setMaxWidth(Integer.MAX_VALUE);
        searchview.setQueryHint("제목으로 검색합니다");
        mode = Namesearch;

        MenuItem searchItem = menu.findItem(R.id.search);
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
                    //searchName(query);
                }
                else if(mode == Tagsearch){
                    //searchTag(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //칠 때마다 텍스트 하나하나 입력
                if(mode == Namesearch){
                   //searchName(newText);
                }
                else if(mode == Tagsearch){
                    //searchTag(newText);
                }
                return false;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                if(mode == Namesearch){
                    searchview.setQueryHint("제목을 검색합니다.");
                }
                else if(mode == Tagsearch){
                    searchview.setQueryHint("태그를 검색합니다.");
                }
                return true;
            }

            ///검색을 실행하던 도중 뒤로가기 하면 콜랍스 함수가 실행되지만 익스팬더블이 뜨려고 했지만 검색에 검색 중이던 함수가 다시 작동이 되서 어댑터에 리사이클류 그게 뜨지 않고 오류가 발생
            /// 콜랩스 함수가 시작되면 무조건 입력중이던 함수는 적용안되게 수정함

            //검색이 종료되었을 때
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
//                q = 1; // 검색하다가 종료했을 때는
//                if(tabtype == 0){
//                    recyclerview = findViewById(R.id.recyclerview);
//                    registerForContextMenu(recyclerview);
//                    DBHelper databaseHelper = new DBHelper(getApplicationContext());
//                    ArrayList<ExpandableListAdapter.Item> mritems = databaseHelper.getItem();
//                    if (mritems != null) {
//                        recyclerview.setAdapter(new ExpandableListAdapter(mritems,ExpandableListAdapter.mContext));
//                        recyclerview.setHasFixedSize(true);
//                    }
//                }
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
            }
            else{
                mode = Tagsearch;
            }
            return false;
        }
    };


//    private void searchName(String keyword){
//        CommunityRecycler = findViewById(R.id.title_community1);
//        registerForContextMenu(CommunityRecycler);
//        Database db = new Database(getApplicationContext());
//        ArrayList<TitleInfo> contacts = db.readName(keyword);
//        if (contacts != null) {
//            CommunityRecycler.setAdapter(new communityAdapter(contacts, this));
//        }
//    }
}