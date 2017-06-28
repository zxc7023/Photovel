package com.photovel.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.photovel.FontActivity;
import com.photovel.FontActivity2;
import com.photovel.MainActivity;
import com.photovel.NavigationItemSelected;
import com.photovel.R;
import com.photovel.content.CommentAdapter;
import com.photovel.content.ContentDetailListMain;
import com.photovel.content.ContentInsertMain;
import com.photovel.content.ContentSlideShowMain;
import com.photovel.content.ContentUpdateMain;
import com.photovel.content.GetCurrentAddress;
import com.photovel.content.MultiDrawable;
import com.photovel.content.MySupportMapFragment;
import com.photovel.http.JsonConnection;
import com.photovel.http.Value;
import com.vo.Comment;
import com.vo.Content;
import com.vo.ContentDetail;
import com.vo.Photo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SettingMain extends FontActivity2 implements NavigationView.OnNavigationItemSelectedListener{
    Toolbar toolbar;
    private String user_id;
    private int content_id=0;
    private TextView tvpen1, tvpen2, tvpen3, tvUserID, tvUserFriendCount;
    private EditText etUserNickName, etUserPassword, etUserPhone;
    private Button btnSettingUpdate, btnUserFriendGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main);

        // Adding Toolbar to the activity
        toolbar = (Toolbar) findViewById(R.id.settingToolbar);
        setSupportActionBar(toolbar);

        SharedPreferences get_to_eat = getSharedPreferences("loginInfo", MODE_PRIVATE);
        user_id = get_to_eat.getString("user_id","notFound");

        tvpen1 = (TextView)findViewById(R.id.tvpen1);
        tvpen2 = (TextView)findViewById(R.id.tvpen2);
        tvpen3 = (TextView)findViewById(R.id.tvpen3);

        tvUserID = (TextView)findViewById(R.id.tvUserID);
        tvUserFriendCount = (TextView)findViewById(R.id.tvUserFriendCount);
        etUserNickName = (EditText)findViewById(R.id.etUserNickName);
        etUserPassword = (EditText)findViewById(R.id.etUserPassword);
        etUserPhone = (EditText)findViewById(R.id.etUserPhone);

        btnSettingUpdate = (Button)findViewById(R.id.btnSettingUpdate);
        btnUserFriendGo = (Button)findViewById(R.id.btnUserFriendGo);

        //imageView를 font로 바꿔주기
        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        tvpen1.setTypeface(fontAwesomeFont);
        tvpen2.setTypeface(fontAwesomeFont);
        tvpen3.setTypeface(fontAwesomeFont);


        tvUserID.setText(user_id);


        //toolbar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
        TextView tvProfileUpdate = (TextView)hView.findViewById(R.id.tvProfileUpdate);
        tvProfileUpdate.setTypeface(fontAwesomeFont);
        tvProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"프로필변경클릭",Toast.LENGTH_SHORT).show();
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
    }

    //디테일 설정 메뉴클릭시
    public void ContentMenuClick(View v){
        Context wrapper = new ContextThemeWrapper(this, R.style.MenuStyle);
        PopupMenu popup = new PopupMenu(wrapper, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.content_setting_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_update:
                        Intent intent = new Intent(SettingMain.this, ContentUpdateMain.class);
                        intent.putExtra("content_id", content_id);
                        SettingMain.this.startActivity(intent);
                        Toast.makeText(getApplicationContext(), "수정", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_delete:
                        AlertDialog.Builder dalert_confirm = new AlertDialog.Builder(SettingMain.this);
                        dalert_confirm.setMessage("정말 글을 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Thread deleteContent = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                JsonConnection.getConnection(Value.contentURL+"/"+content_id, "DELETE", null);
                                            }
                                        });
                                        deleteContent.start();
                                        try {
                                            deleteContent.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(getApplicationContext(),"삭제성공",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //재사용 ㄴㄴ
                                        startActivity(intent);
                                        finish();
                                    }
                                }).setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                });
                        AlertDialog dalert = dalert_confirm.create();
                        dalert.show();
                        break;
                    case R.id.action_report:
                        AlertDialog.Builder walert_confirm = new AlertDialog.Builder(SettingMain.this);
                        walert_confirm.setMessage("정말 글을 신고 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Thread th = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                JsonConnection.getConnection(Value.contentURL+"/"+content_id+"/warning", "POST", null);
                                            }
                                        });
                                        th.start();
                                        try {
                                            th.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(getApplicationContext(),"신고성공",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //재사용 ㄴㄴ
                                        startActivity(intent);
                                        finish();
                                    }
                                }).setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                });
                        AlertDialog walert = walert_confirm.create();
                        walert.show();
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    //프로파일 클릭시
    public void ProFileMenuClick(View v){
        Context wrapper = new ContextThemeWrapper(this, R.style.MenuStyle);
        PopupMenu popup = new PopupMenu(wrapper, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.user_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.show_profile:
                        Toast.makeText(getApplicationContext(),"프로필보기",Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        popup.show();
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