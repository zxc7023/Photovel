package com.photovel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.photovel.user.UserLogin;
import com.alibaba.fastjson.JSON;
import com.photovel.content.ContentDetailListMain;
import com.photovel.content.ContentInsertMain;
import com.photovel.http.Value;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.vo.Content;
import com.vo.MainImage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends FontActivity2 implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Image";
    private SearchView searchView;
    Toolbar toolbar;
    //메인 이미지 케러셀뷰
    CarouselView carouselView;
    List<MainImage> images;

    //추천 게시글 가로스크롤
    private RecyclerView mRecyclerView;
    private MainRecommendAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Content> myDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //메인이미지 캐러셀뷰 부분
        carouselView = (CarouselView) findViewById(R.id.carouselView);

        //db에 있는 메인이미지 받아오기
        Thread getMainImage = new Thread(){
            @Override
            public void run() {
                super.run();
                images = getMainImage();
            }
        };
        getMainImage.start();
        try {
            getMainImage.join();  //모든처리 thread처리 기다리기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getMainBitmap(); //메인 Image받아오기

        carouselView.setPageCount(images.size());
        carouselView.setImageListener(imageListener);

        //추천 게시글 가로스크롤
        //db에 있는 contentId별 content정보 받아오기
        Thread getRecommend = new Thread(){
            @Override
            public void run() {
                super.run();
                myDataset = getRecommendData();
            }
        };
        getRecommend.start();
        try {
            getRecommend.join();  //모든처리 thread처리 기다리기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getRecommendBitmap(); //content Image받아오기


        //recycleview사용선언
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new MainRecommendAdapter(myDataset, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);


        // Adding Toolbar to the activity
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Adding FloatingActionButton to the activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        HashMap<Integer,Class> map = new HashMap<>();
        map.put(R.id.loginModuel, UserLogin.class);
        map.put(R.id.contentInsert,ContentInsertMain.class);
        map.put(R.id.cok,TestActivity.class);
        map.put(R.id.deatilList, ContentDetailListMain.class);

        Set<Integer> keys = map.keySet();
        for(int key: keys){

            final Class clazz = map.get(key);
            Button bt = (Button) findViewById(key);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),clazz);
                    startActivity(intent);
                }
            });
        }
    }

    //Android BackButton EventListener
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

    //메인이미지 캐러셀뷰 부분
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageBitmap(images.get(position).getBitmap());
        }
    };
    //
    public List<MainImage> getMainImage(){
        HttpURLConnection conn = null;
        List<MainImage> imgs = null;
        String qry = Value.mainImageURL;
        Log.i(TAG, "1.getMainImage qry= " + qry);
        try {
            URL strUrl = new URL(qry);
            conn = (HttpURLConnection) strUrl.openConnection();
            conn.setDoInput(true);//서버로부터 결과값을 응답받음

            conn.setRequestMethod("GET");
            Log.i(TAG, "2.getMainImage qry= " + qry);

            final int responseCode = conn.getResponseCode(); //정상인 경우 200번, 그 외 오류있는 경우 오류 번호 반환
            Log.i(TAG, "3.getMainImage responseCode= " + responseCode);
            switch (responseCode){
                case HttpURLConnection.HTTP_OK:

                    InputStream is = conn.getInputStream();
                    Reader reader = new InputStreamReader(is, "UTF-8");
                    BufferedReader br = new BufferedReader(reader);

                    String responseData = br.readLine();
                    Log.i(TAG, "4.getMainImage response data= " + responseData);

                    imgs =  JSON.parseArray(responseData, MainImage.class);

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
            return imgs;
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
    public void getMainBitmap(){
        Thread thread2 = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    for (int i = 0; i < images.size(); i++) {
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(Value.mainImagePhotoURL+"/"+images.get(i).getImage_file_name()).getContent());
                        images.get(i).setBitmap(bitmap);
                        Log.i(TAG, "getMainBitmap bitmap= " + bitmap);
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

    //추천게시글 가로스크롤
    public List<Content> getRecommendData(){
        List<Content> content = null;
        HttpURLConnection conn = null;

        String qry = Value.contentURL;//+"/recommend";
        Log.i(TAG, "1.getPhotoData의 qry= " + qry);

        try {
            URL strUrl = new URL(qry);
            conn = (HttpURLConnection) strUrl.openConnection();
            conn.setDoInput(true);//서버로부터 결과값을 응답받음
            conn.setRequestMethod("GET");
            Log.i(TAG, "2.getPhotoData의 qry= " + qry);


            final int responseCode = conn.getResponseCode(); //정상인 경우 200번, 그 외 오류있는 경우 오류 번호 반환
            Log.i(TAG, "getPhotoData의 responseCode= " + responseCode);
            switch (responseCode){
                case HttpURLConnection.HTTP_OK:

                    InputStream is = conn.getInputStream();
                    Reader reader = new InputStreamReader(is, "UTF-8");
                    BufferedReader br = new BufferedReader(reader);
                    String responseData = null;

                    responseData = br.readLine();
                    Log.i(TAG, "getPhotoData의 response data= " + responseData);

                    content = JSON.parseArray(responseData, Content.class);
                    Log.i(TAG, "getPhotoData의 content= " + content);

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

            return content;
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
    public void getRecommendBitmap(){
        Thread thread2 = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    for (int i = 0; i < myDataset.size(); i++) {
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(Value.contentPhotoURL+"/" + myDataset.get(i).getContent_id() + "/" + myDataset.get(i).getPhoto_file_name()).getContent());
                        myDataset.get(i).setBitmap(bitmap);
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
}
