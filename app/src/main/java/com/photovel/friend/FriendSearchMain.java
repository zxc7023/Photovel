package com.photovel.friend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.photovel.FontActivity2;
import com.photovel.MainActivity;
import com.photovel.NavigationItemSelected;
import com.photovel.R;
import com.photovel.content.ContentInsertMain;
import com.photovel.http.JsonConnection;
import com.photovel.http.Value;
import com.photovel.setting.SettingMain;
import com.photovel.user.UserBitmapEncoding;
import com.vo.User;

import java.util.List;

public class FriendSearchMain extends FontActivity2 implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Image";
    Toolbar toolbar;
    private String user_id, user_nick_name, user_profile, searchFlag;

    private RecyclerView recyclerView;
    private FriendSearchAdapter mFriendNewListAdapter;
    private RecyclerView.LayoutManager mNewLayoutManager;
    private List<User> myNewFriendDataset;

    private Boolean isFabOpen = false;
    private FloatingActionButton fab_more, fab_friend_list, fab_search;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    private EditText etSearch;
    private TextView icsearch, icclose, btnSearchOk, tvSearchNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search_main);

        //로그인중인 user_id받아오기
        SharedPreferences get_to_eat = getSharedPreferences("loginInfo", MODE_PRIVATE);
        user_id = get_to_eat.getString("user_id","notFound");
        user_nick_name = get_to_eat.getString("user_nick_name","notFound");
        user_profile = get_to_eat.getString("user_profile","notFound");

        //FloatingActionButton정의 및 기능
        fab_more = (FloatingActionButton) findViewById(R.id.fab_more);
        fab_friend_list = (FloatingActionButton) findViewById(R.id.fab_friend_list);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

        icsearch = (TextView)findViewById(R.id.icsearch);
        icclose = (TextView)findViewById(R.id.icclose);
        btnSearchOk = (TextView)findViewById(R.id.btnSearchOk);
        tvSearchNo = (TextView)findViewById(R.id.tvSearchNo);
        etSearch = (EditText)findViewById(R.id.etSearch);

        final Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        icsearch.setTypeface(fontAwesomeFont);
        icclose.setTypeface(fontAwesomeFont);

        etSearch.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!etSearch.getText().equals("")){
                    icclose.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        icclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.setText("");
                icclose.setVisibility(View.INVISIBLE);
            }
        });

        //intent받아오기
        Intent intent = getIntent();
        searchFlag = intent.getStringExtra("search");
        if(searchFlag.equals("id")){
            etSearch.setHint("아이디, 닉네임 검색");
            fab_search.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_number_off));
        }else{
            etSearch.setHint("전화번호 검색");
            etSearch.setInputType(InputType.TYPE_CLASS_NUMBER);
            fab_search.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_id_off));
        }

        fab_friend_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FriendListMain.class);
                startActivity(intent);
                finish();
            }
        });
        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchFlag.equals("id")){
                    Intent intent = new Intent(getApplicationContext(), FriendSearchMain.class);
                    intent.putExtra("search","phone");
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(getApplicationContext(), FriendSearchMain.class);
                    intent.putExtra("search","id");
                    startActivity(intent);
                    finish();
                }
            }
        });
        fab_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.fab_more:
                        animateFAB();
                        break;
                    case R.id.fab_friend_list:
                        break;
                    case R.id.fab_search:
                        break;
                }
            }
        });

        btnSearchOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //번호나 아이디를 넘기고 리스트 받기

                final String url;
                if(searchFlag.equals("id")){
                    url = "/selectid/";
                }else{
                    url = "/selectphone/";
                }

                Thread searchList = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        String responseData = JsonConnection.getConnection(Value.photovelURL + "/friend" + url + etSearch.getText().toString() +"/" + user_id, "GET", null);
                        myNewFriendDataset = JSON.parseArray(responseData, User.class);
                    }
                };
                searchList.start();
                try {
                    searchList.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //user의 bitmap 받아오기
                if(myNewFriendDataset != null){
                    tvSearchNo.setVisibility(View.GONE);
                    JsonConnection.setBitmap(myNewFriendDataset, Value.contentPhotoURL);
                    for(int i = 0; i < myNewFriendDataset.size(); i++){
                        if(myNewFriendDataset.get(i).getUser_profile_photo() == null){
                            Bitmap profile = BitmapFactory.decodeResource(getResources(),R.drawable.ic_profile_circle);
                            myNewFriendDataset.get(i).setBitmap(profile);
                        }
                    }

                    recyclerView = (RecyclerView) findViewById(R.id.recycler_search_view);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setNestedScrollingEnabled(false);
                    mNewLayoutManager = new LinearLayoutManager(FriendSearchMain.this);
                    recyclerView.setLayoutManager(mNewLayoutManager);
                    mFriendNewListAdapter = new FriendSearchAdapter(myNewFriendDataset, FriendSearchMain.this);
                    recyclerView.setAdapter(mFriendNewListAdapter);
                }else{
                    tvSearchNo.setVisibility(View.VISIBLE);
                }

            }
        });

        // Adding Toolbar to the activity
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toolbar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

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
    }

    //FloatingActionButton 회전하기
    public void animateFAB() {
        if (isFabOpen) {
            fab_more.startAnimation(rotate_backward);
            fab_friend_list.startAnimation(fab_close);
            fab_search.startAnimation(fab_close);
            fab_friend_list.setClickable(false);
            fab_search.setClickable(false);
            isFabOpen = false;
        } else {
            fab_more.startAnimation(rotate_forward);
            fab_friend_list.startAnimation(fab_open);
            fab_search.startAnimation(fab_open);
            fab_friend_list.setClickable(true);
            fab_search.setClickable(true);
            isFabOpen = true;
        }
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

    //Android BackButton EventListener
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            super.onBackPressed();
        }
    }
}
