package com.photovel.content;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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

import com.alibaba.fastjson.JSON;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.photovel.FontActivity2;
import com.photovel.MainActivity;
import com.photovel.NavigationItemSelected;
import com.photovel.R;
import com.photovel.http.JsonConnection;
import com.photovel.http.Value;
import com.photovel.setting.SettingMain;
import com.photovel.user.UserBitmapEncoding;
import com.vo.Content;

import java.util.List;

public class ContentListMain extends FontActivity2 implements NavigationView.OnNavigationItemSelectedListener{
    private FloatingActionButton btnTop;
    private RecyclerView mRecyclerView;
    private ContentListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Content> myDataset;
    private TextView iclist, tvContentListName;
    private static final String TAG = "";
    private String content_user_id = "", user_id, user_nick_name, user_profile;
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

        iclist = (TextView)findViewById(R.id.iclist);
        tvContentListName = (TextView)findViewById(R.id.tvContentListName);

        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        iclist.setTypeface(fontAwesomeFont);

        //현재 로그인한 user_id 받아오기
        SharedPreferences get_to_eat = getSharedPreferences("loginInfo", MODE_PRIVATE);
        user_id = get_to_eat.getString("user_id","notFound");
        user_nick_name = get_to_eat.getString("user_nick_name","notFound");
        user_profile = get_to_eat.getString("user_profile","notFound");

        Intent intent = getIntent();
        content_user_id = intent.getStringExtra("user_id");
        if(content_user_id.equals("")){
            Log.i("user_id","content_list_user_id 못받아옴!!!");
        }else{
            Log.i("user_id","content_list_user_id : "+content_user_id);
        }
        SharedPreferences contentInfo = getSharedPreferences("content_user_id", MODE_PRIVATE);
        SharedPreferences.Editor editor = contentInfo.edit();
        editor.putString("content_user_id",content_user_id);
        editor.commit();

        urlflag = intent.getStringExtra("urlflag");
        if(urlflag.equals("")){
            tvContentListName.setText(content_user_id+"님의 스토리");
            urlflag = "C";
        }else if(urlflag.equals("M")){
            tvContentListName.setText("내 스토리");
        }else if(urlflag.equals("N")){
            tvContentListName.setText("전체 스토리");
        }else if(urlflag.equals("R")){
            tvContentListName.setText("인기 스토리");
        }

        //스토리 객체 받아오기
        Thread listMain = new Thread(){
            @Override
            public void run() {
                super.run();

                String qry = null;
                if(urlflag.equals("C")){
                    qry = Value.contentURL+"/user/"+content_user_id+"/";
                }else if(urlflag.equals("M")){
                    qry = Value.contentURL+"/my/";
                }else if(urlflag.equals("N")){
                    qry = Value.contentURL+"/new/";
                }else if(urlflag.equals("R")){
                    qry = Value.contentURL+"/recommend/";
                }

                String responseData = JsonConnection.getConnection(qry+user_id, "GET", null);
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
        JsonConnection.setBitmap(myDataset, Value.contentPhotoURL);
        for(int i = 0; i < myDataset.size(); i++){
            if(myDataset.get(i).getUser().getUser_profile_photo() == null){
                Bitmap profile = BitmapFactory.decodeResource(getResources(),R.drawable.ic_profile_circle);
                myDataset.get(i).getUser().setBitmap(profile);
            }
        }

        //recycleview사용선언
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new ContentListAdapter(myDataset, ContentListMain.this, urlflag);
        mRecyclerView.setAdapter(mAdapter);


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
        //finish();
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