package com.photovel.content;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.photovel.FontActivity;
import com.photovel.FontActivity2;
import com.photovel.R;
import com.photovel.http.Value;
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
    private String user_nick_name;

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
        user_nick_name = intent.getStringExtra("user_nick_name");
        if(user_nick_name.equals("")){
            Log.i("user_nick_name","list_user_nick_name 못받아옴!!!");
            user_nick_name = "leeej9201@gmail.com";
        }else{
            Log.i("user_nick_name","list_user_nick_name : "+user_nick_name);
        }

        //db에 있는 userId별 content정보 받아오기
        Thread thread1 = new Thread(){
            @Override
            public void run() {
                super.run();
                myDataset = new ArrayList<>();
                myDataset = getContentData(user_nick_name);
            }
        };
        thread1.start();
        try {
            thread1.join();  //모든처리 thread처리 기다리기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("ddd","size : "+myDataset.size());
        getImage(); //content Image받아오기

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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

    //DB에서 content정보 받아오기
    public List<Content> getContentData(String id){
        List<Content> contents = null;
        HttpURLConnection conn = null;
        Log.i(TAG, "getPhotoData의 id= " + id);

        String qry = Value.contentURL+"/user/" + id;
        Log.i(TAG, "1.getPhotoData의 qry= " + qry);

        try {
            URL strUrl = new URL(qry);
            conn = (HttpURLConnection) strUrl.openConnection();
            conn.setDoInput(true);//서버로부터 결과값을 응답받음
            //conn.setDoOutput(true);//서버로 값을 출력. GET방식의 경우 이 설정을 하면 405에러가 난다. 왜???
            //conn.connect();
            conn.setRequestMethod("GET");
            Log.i(TAG, "2.getPhotoData의 qry= " + qry);
            /*
            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            // bw.write(id);

            bw.flush();
            bw.close();*/

            final int responseCode = conn.getResponseCode(); //정상인 경우 200번, 그 외 오류있는 경우 오류 번호 반환
            Log.i(TAG, "getPhotoData의 responseCode= " + responseCode);
            switch (responseCode){
                case HttpURLConnection.HTTP_OK:

                    InputStream is = conn.getInputStream();
                    Reader reader = new InputStreamReader(is, "UTF-8");
                    BufferedReader br = new BufferedReader(reader);
                    // while(br.read() != -1 ){
                    String responseData = null;

                    responseData = br.readLine();
                    Log.i(TAG, "getPhotoData의 response data= " + responseData);

                    contents = JSON.parseArray(responseData, Content.class);
                    Log.i(TAG, "getPhotoData의 contents= " + contents);

                    br.close();
                    reader.close();
                    is.close();

                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "페이지를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
                default:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "response code: " + responseCode, Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
            return contents;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            conn.disconnect();
        }

        return null;
    }

    //DB에서 bitmap정보 받아오기
    public void getImage(){
        Thread thread2 = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    for (int i = 0; i < myDataset.size(); i++) {
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(Value.contentPhotoURL+"/" + myDataset.get(i).getContent_id() + "/" + myDataset.get(i).getPhoto_file_name()).getContent());
                        myDataset.get(i).setBitmap(bitmap);
                        //File filePath = new File(Environment.getExternalStorageDirectory());
                        //FileUtils.copyURLToFile(new URL("http://photovel.com/upload/" + contentData.getContent_id() + "/" + contentData.getDetails().get(i).getPhoto().getPhotoFileName()), );
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread2.start();
        try {
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    @Override
    @NonNull
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
/*

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        searchView = (SearchView) toolbar.getMenu().findItem(R.id.menu_search).getActionView();
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.app_name));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }
}