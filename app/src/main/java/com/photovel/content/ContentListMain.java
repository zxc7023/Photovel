package com.photovel.content;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.photovel.FontActivity;
import com.photovel.FontActivity2;
import com.photovel.MainActivity;
import com.photovel.NavigationItemSelected;
import com.photovel.R;
import com.photovel.http.JsonConnection;
import com.photovel.http.Value;
import com.photovel.setting.SettingMain;
import com.vo.Content;
import com.vo.ContentDetail;
import com.vo.Photo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContentListMain extends FontActivity2 implements NavigationView.OnNavigationItemSelectedListener{
    private FloatingActionButton btnTop;
    private RecyclerView mRecyclerView;
    private ContentListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Content> myDataset;
    private static final String TAG = "";
    private String user_id = "";
    private String urlflag = "";

    Toolbar toolbar;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_list_main);

        // Adding Toolbar to the activity
        toolbar = (Toolbar) findViewById(R.id.contentListToolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        if(user_id.equals("")){
            Log.i("user_id","list_user_id 못받아옴!!!");
            //현재 로그인한 user_id 받아오기
            SharedPreferences get_to_eat = getSharedPreferences("loginInfo", MODE_PRIVATE);
            user_id = get_to_eat.getString("user_id","notFound");
            Log.i("user_id", user_id);
        }else{
            Log.i("user_id","list_user_id : "+user_id);
        }

        urlflag = intent.getStringExtra("urlflag");
        if(urlflag.equals("")){
            urlflag = "C";
        }else{
            Log.i("urlflag","list_urlflag : "+urlflag);
        }

        //스토리 객체 받아오기
        Thread listMain = new Thread(){
            @Override
            public void run() {
                super.run();

                String qry = null;
                if(urlflag.equals("C")){
                    qry = Value.contentURL+"/user/" + user_id;
                }else if(urlflag.equals("M")){
                    qry = Value.contentURL+"/my/" + user_id;
                }else if(urlflag.equals("N")){
                    qry = Value.contentURL+"/new";
                }else if(urlflag.equals("R")){
                    qry = Value.contentURL+"/recommend";
                }

                String responseData = JsonConnection.getConnection(qry, "GET", null);
                myDataset = JSON.parseArray(responseData, Content.class);
            }
        };
        listMain.start();
        try {
            listMain.join();  //모든처리 thread처리 기다리기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //스토리 bitmap 받아오기
        List<Bitmap> newBitmaps = JsonConnection.getBitmap(myDataset, Value.contentPhotoURL);
        for(int i = 0; i < myDataset.size(); i++){
            myDataset.get(i).setBitmap(newBitmaps.get(i));
        }

        //recycleview사용선언
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new ContentListAdapter(myDataset, ContentListMain.this);
        mRecyclerView.setAdapter(mAdapter);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        TextView btnContentInsert = (TextView)hView.findViewById(R.id.btnContentInsert);
        btnContentInsert.setTypeface(fontAwesomeFont);
        btnContentInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ContentInsertMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
        TextView tvUserName = (TextView)hView.findViewById(R.id.tvUserName);
        tvUserName.setText(user_id);
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


        //top버튼
        btnTop = (FloatingActionButton) findViewById(R.id.btnTop);
        final NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nestedScrollView);
        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {   //스크롤내리면 보이게
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(oldScrollY > 200){
                    btnTop.show();
                }else{
                    btnTop.hide();
                }
            }
        });
        btnTop.setOnClickListener(new View.OnClickListener() {  //top으로 이동
            @Override
            public void onClick(View view) {
                nsv.fullScroll(View.FOCUS_UP);
            }
        });
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
        getMenuInflater().inflate(R.menu.other_toolbar, menu);
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