package com.photovel.friend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.photovel.FontActivity2;
import com.photovel.MainActivity;
import com.photovel.NavigationItemSelected;
import com.photovel.R;
import com.photovel.common.BookMarkAdapter;
import com.photovel.common.GridSpacingItemDecoration;
import com.photovel.content.ContentInsertMain;
import com.photovel.http.JsonConnection;
import com.photovel.http.Value;
import com.photovel.setting.SettingMain;
import com.photovel.user.UserBitmapEncoding;
import com.vo.Content;
import com.vo.User;

import java.util.ArrayList;
import java.util.List;

public class FriendListMain extends FontActivity2 implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Image";
    Toolbar toolbar;
    private String user_id, user_nick_name, user_profile;

    private RecyclerView recyclerView;
    private FriendNewListAdapter mFriendNewListAdapter;
    private FriendListAdapter mFriendListAdapter;
    private RecyclerView.LayoutManager mNewLayoutManager;
    private List<User> myFriendDataset, myNewFriendDataset;
    private TextView tvbookmark;

    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2, fab3;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list_main);

        //로그인중인 user_id받아오기
        SharedPreferences get_to_eat = getSharedPreferences("loginInfo", MODE_PRIVATE);
        user_id = get_to_eat.getString("user_id","notFound");
        user_nick_name = get_to_eat.getString("user_nick_name","notFound");
        user_profile = get_to_eat.getString("user_profile","notFound");

        //FloatingActionButton정의 및 기능
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        /*fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);*/
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "아직 개발중입니다.", Toast.LENGTH_LONG).show();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "아직 개발중입니다.", Toast.LENGTH_LONG).show();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.fab:
                        animateFAB();
                        break;
                    case R.id.fab1:
                        Log.i("ddd", "Fab 1");
                        break;
                    case R.id.fab2:
                        Log.i("ddd", "Fab 2");
                        break;
                }
            }
        });

        //친구 신청 목록
        Thread detailList = new Thread(){
            @Override
            public void run() {
                super.run();
                String responseData = JsonConnection.getConnection(Value.photovelURL+"/friend/new/"+user_id, "GET", null);
                myNewFriendDataset = JSON.parseArray(responseData, User.class);
            }
        };
        detailList.start();
        try {
            detailList.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //content의 bitmap 받아오기
        JsonConnection.setBitmap(myNewFriendDataset, Value.contentPhotoURL);
        for(int i = 0; i < myNewFriendDataset.size(); i++){
            if(myNewFriendDataset.get(i).getUser_profile_photo() == null){
                Bitmap profile = BitmapFactory.decodeResource(getResources(),R.drawable.ic_profile_circle);
                myNewFriendDataset.get(i).setBitmap(profile);
            }
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        mNewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mNewLayoutManager);
        mFriendNewListAdapter = new FriendNewListAdapter(myNewFriendDataset, FriendListMain.this);
        recyclerView.setAdapter(mFriendNewListAdapter);

        //친구 목록
        Thread detailList2 = new Thread(){
            @Override
            public void run() {
                super.run();
                String responseData = JsonConnection.getConnection(Value.photovelURL+"/friend/"+user_id, "GET", null);
                myFriendDataset = JSON.parseArray(responseData, User.class);
            }
        };
        detailList2.start();
        try {
            detailList2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //content의 bitmap 받아오기
        JsonConnection.setBitmap(myFriendDataset, Value.contentPhotoURL);
        for(int i = 0; i < myFriendDataset.size(); i++){
            if(myFriendDataset.get(i).getUser_profile_photo() == null){
                Bitmap profile = BitmapFactory.decodeResource(getResources(),R.drawable.ic_profile_circle);
                myFriendDataset.get(i).setBitmap(profile);
            }
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        mNewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mNewLayoutManager);
        mFriendListAdapter = new FriendListAdapter(myFriendDataset, FriendListMain.this);
        recyclerView.setAdapter(mFriendListAdapter);

        // Adding Toolbar to the activity
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toolbar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

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
        TextView tvProfileUpdate = (TextView)hView.findViewById(R.id.tvProfileUpdate);
        tvProfileUpdate.setTypeface(fontAwesomeFont);
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
            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
            Log.i("ddd", "close");
        } else {
            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;
            Log.i("ddd", "open");
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
