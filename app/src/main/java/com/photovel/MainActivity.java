package com.photovel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.photovel.search.SearchView;
import com.photovel.search.SearchListAdapter;
import com.photovel.http.JsonConnection;
import com.photovel.setting.SettingMain;
import com.alibaba.fastjson.JSON;
import com.photovel.content.ContentInsertMain;
import com.photovel.http.Value;
import com.photovel.user.UserBitmapEncoding;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.vo.Content;
import com.vo.MainImage;
import com.vo.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends FontActivity2 implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    Toolbar toolbar;
    private String user_id, user_nick_name, user_profile;

    //메인 이미지 케러셀뷰
    CarouselView carouselView;
    List<MainImage> images;

    //추천, 신규 게시글
    private RecyclerView RVrecommend, RVnew;
    private MainRecommendAdapter mRecommendAdapter;
    private MainNewAdapter mNewAdapter;
    private RecyclerView.LayoutManager mRecommendLayoutManager, mNewLayoutManager;
    private List<Content> myRecommendDataset, myNewDataset;
    private boolean doubleBackToExitPressedOnce = false;

    //검색
    //search 아이콘
    private MenuItem searchItem;

    //커서
    MatrixCursor matrixCursor;

    //검색 제안 위한 커서 어댑터
    private SearchListAdapter searchListAdapter;

    //굳이 클래스로 객체 만들 필요 없이 xml에 태그로 정의하여 호출 가능
    SearchView searchView;
    ListView suggestionsListView;

    private BottomSheetBehavior bottomSheetBehavior;
    private RelativeLayout RlSearch;
    private ImageButton btnBack;

    String isTokenSucess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences get_to_eat = getSharedPreferences("loginInfo", MODE_PRIVATE);
        user_id = get_to_eat.getString("user_id","notFound");
        user_nick_name = get_to_eat.getString("user_nick_name","notFound");
        user_profile = get_to_eat.getString("user_profile","notFound");

        RlSearch = (RelativeLayout) findViewById(R.id.RlSearch);
        bottomSheetBehavior = BottomSheetBehavior.from(RlSearch);
        bottomSheetBehavior.setPeekHeight(0);
        btnBack = (ImageButton) findViewById(R.id.btn_action_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        searchView = (SearchView) findViewById(R.id.search_view);

        suggestionsListView = (ListView) findViewById(R.id.suggestion_list);
        searchListAdapter = new SearchListAdapter(this, matrixCursor, true, suggestionsListView);

        searchView.setAdapter(searchListAdapter);

        //메인이미지 캐러셀뷰 부분
        carouselView = (CarouselView) findViewById(R.id.carouselView);

        //메인이미지 객체 받아오기
        Thread getMainImage = new Thread(){
            @Override
            public void run() {
                super.run();
                String responseData = JsonConnection.getConnection(Value.mainImageURL, "GET", null);
                images = JSON.parseArray(responseData, MainImage.class);
            }
        };
        getMainImage.start();
        try {
            getMainImage.join();  //모든처리 thread처리 기다리기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //메인이미지 bitmap 받아오기
        JsonConnection.setBitmap(images, Value.mainImagePhotoURL);

        carouselView.setPageCount(images.size());
        carouselView.setImageListener(imageListener);

        //추천 스토리 객체 받아오기
        Thread getRecommend = new Thread(){
            @Override
            public void run() {
                super.run();
                String responseData = JsonConnection.getConnection(Value.contentURL+"/recommend/"+user_id, "GET", null);
                myRecommendDataset = JSON.parseArray(responseData, Content.class);
            }
        };
        getRecommend.start();
        try {
            getRecommend.join();  //모든처리 thread처리 기다리기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //추천 스토리 bitmap 받아오기
        JsonConnection.setBitmap(myRecommendDataset, Value.contentPhotoURL);
        Log.i("ddd","오류로그 : "+myRecommendDataset.size());

        //추천 스토리 recycleview사용선언
        RVrecommend = (RecyclerView) findViewById(R.id.RVrecommend);
        RVrecommend.bringToFront();
        RVrecommend.setHasFixedSize(true);
        RVrecommend.setNestedScrollingEnabled(false);
        mRecommendLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RVrecommend.setLayoutManager(mRecommendLayoutManager);
        mRecommendAdapter = new MainRecommendAdapter(myRecommendDataset, MainActivity.this);
        RVrecommend.setAdapter(mRecommendAdapter);

        //신규 스토리 객체 받아오기
        Thread getNew = new Thread(){
            @Override
            public void run() {
                super.run();
                String responseData = JsonConnection.getConnection(Value.contentURL+"/new/"+user_id, "GET", null);
                myNewDataset = JSON.parseArray(responseData, Content.class);
            }
        };
        getNew.start();
        try {
            getNew.join();  //모든처리 thread처리 기다리기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //신규 스토리 bitmap 받아오기
        JsonConnection.setBitmap(myNewDataset, Value.contentPhotoURL);
        for(int i = 0; i < myNewDataset.size(); i++){
            if(myNewDataset.get(i).getUser().getUser_profile_photo() == null){
                Bitmap profile = BitmapFactory.decodeResource(getResources(),R.drawable.ic_profile_circle);
                myNewDataset.get(i).getUser().setBitmap(profile);
            }
        }

        //신규 스토리 recycleview사용선언
        RVnew = (RecyclerView) findViewById(R.id.RVnew);
        RVnew.setHasFixedSize(true);
        RVnew.setNestedScrollingEnabled(false);
        mNewLayoutManager = new LinearLayoutManager(this);
        RVnew.setLayoutManager(mNewLayoutManager);
        mNewAdapter = new MainNewAdapter(myNewDataset, MainActivity.this);
        RVnew.setAdapter(mNewAdapter);

        // Adding Toolbar to the activity
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        //메뉴 navigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        TextView btnContentInsert = (TextView)hView.findViewById(R.id.btnContentInsert);
        btnContentInsert.setTypeface(fontAwesomeFont);
        btnContentInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ContentInsertMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //준기오빠의 낮은 버전을 위해 인텐트할때 넣어주기
                getApplicationContext().startActivity(intent);
            }
        });
        TextView tvUserName = (TextView)hView.findViewById(R.id.tvUserName);
        tvUserName.setText(user_nick_name);
        CircularImageView userProfile = (CircularImageView)hView.findViewById(R.id.userProfile);
        if(!user_profile.equals("notFound")){
            UserBitmapEncoding ub = new UserBitmapEncoding();
            userProfile.setImageBitmap(ub.StringToBitMap(user_profile));
        }
        LinearLayout tvProfileUpdate = (LinearLayout)hView.findViewById(R.id.tvProfileUpdate);
        tvProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        //검색 제안 목록 등록
        searchView.setSuggestions(myNewDataset, matrixCursor, true);

        //리스너 등록
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "onQueryTextChange= " + newText);
                int size = myNewDataset.size();

                //자료 필터
                matrixCursor = new MatrixCursor(new String[]{BaseColumns._ID,
                        "content_id",
                        "user_nick_name",
                        "user_bitmap",
                        "content_subject"
                });
                Log.i(TAG, "onQueryTextChange의 searchSuggestionsList.size()= " + size);

                for(int i=0; i<size; i++) {

                    UserBitmapEncoding ub = new UserBitmapEncoding();
                    String user_bitmap = ub.BitMapToString(myNewDataset.get(i).getUser().getBitmap());

                    if ("".equals(newText)) {

                    } else if (myNewDataset.get(i).getUser().getUser_nick_name().toLowerCase().startsWith(newText.toLowerCase()) //입력되는 문자열의 시작부분이 "User_nick_name"의 value와 같은지 검사
                            || myNewDataset.get(i).getContent_subject().toLowerCase().startsWith(newText.toLowerCase())) {
                        Log.i(TAG, "onQueryTextChange 들어온 contentList= " + myNewDataset);
                        Log.i(TAG, "onQueryTextChange 들어온 getUser_id()= " + myNewDataset.get(i).getUser().getUser_nick_name().toLowerCase());
                        Log.i(TAG, "onQueryTextChange 들어온 getContent_subject()= " + myNewDataset.get(i).getContent_subject().toLowerCase());
                        matrixCursor.addRow(new Object[]{i,
                                myNewDataset.get(i).getContent_id(),
                                myNewDataset.get(i).getUser().getUser_nick_name(),
                                user_bitmap,
                                myNewDataset.get(i).getContent_subject()
                        });
                    }else{
                        Log.i(TAG, "onQueryTextChange 못들어옴엉엉 " + myNewDataset);
                    }
                }
                searchListAdapter.changeCursor(matrixCursor);
                searchListAdapter.notifyDataSetChanged();
                searchView.setAdapter(searchListAdapter);
                return true;
            }
        });

        /*searchView.setOnSearchViewListener(new SearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });*/
    }

    //Android BackButton EventListener
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "한번 더 누르면 종료됩니돱", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            List<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    //툴바 메뉴 클릭 시
    @Override
    @NonNull
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        NavigationItemSelected ns = new NavigationItemSelected();
        ns.selected(id, getApplicationContext());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        searchItem = menu.findItem(R.id.menu_search);
        if(searchItem != null){
            Log.i(TAG, "searchItem != null");
            searchView.setMenuItem(searchItem);
        }
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);   //search용 bottomSheet열어주기
                RlSearch.bringToFront();
                searchView.showSearch();    //검색칸?도 열어주기
                return false;
            }
        });
        return true;
    }

    //메인이미지 캐러셀뷰 부분
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageBitmap(images.get(position).getBitmap());
        }
    };
}
