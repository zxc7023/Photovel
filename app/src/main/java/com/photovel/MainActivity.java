package com.photovel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.photovel.content.ContentInsertMain;
import com.photovel.http.Value;
import com.photovel.user.UserLogin;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.vo.Content;
import com.vo.MainImage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity
        extends FontActivity2
        implements NavigationView.OnNavigationItemSelectedListener{
    //Log용 TAG
    final static String TAG = "MainActivity";
    //검색 필드
    private SearchView searchView;
    private SearchManager searchManager;

    //search 아이콘
    private MenuItem searchItem;

    //content의 유저 아이디와 제목을 키와 값으로 저장하기 위한 맵
    private ArrayList<Content> searchSuggestionsList = new ArrayList<>();

    //검색 리스트 뷰
    private ListView searchListView;

    //다중 자동 완성 뷰
    private MultiAutoCompleteTextView mACT;
    //다중 자동 완성 뷰 tokenizer
    private MultiAutoCompleteTextView.Tokenizer tokenizer;

    //검색 리스트
    private ArrayList<Content> contents;

    //검색 제안 위한 커서
    private SimpleCursorAdapter simpleCursorAdapter;

    //volley request queue 선언
    private RequestQueue requestQueue;

    //Volley 사용시 ProgressDialog
    ProgressDialog loadingProgress;

    //최대 연결 시간
    public static final int CONNECTION_TIMEOUT = 10000;

    //최대 읽기 시간
    public static final int READ_TIMEOUT = 15000;

    private Toolbar toolbar;
    private DrawerLayout drawer;


    //메인 이미지 케러셀뷰
    CarouselView carouselView;
    List<MainImage> images;

    //추천 게시글 가로스크롤
    private RecyclerView RVrecommend, RVnew;
    private MainRecommendAdapter mRecommendAdapter;
    private MainNewAdapter mNewAdapter;
    private RecyclerView.LayoutManager mRecommendLayoutManager, mNewLayoutManager;
    private List<Content> myRecommendDataset, myNewDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Volley 설정
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        //UI에 묶일 컬럼명
        final String[] from = new String[] {"user_id", "content_subject"};
        //from의 컬럼을 보여줄 뷰. 모두 TextView여야 함. from의 N번째와 to의 N번째가 매칭
        final int[] to = new int[] {R.id.sdl_textView, R.id.sdl_textView};

        //커서 생성
        simpleCursorAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.search_dropdown_list,
                null, //db 커서는 없음
                from, //UI에 바인드 될 자료들의 컬럼명
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


        //메인이미지 캐러셀뷰 부분
        carouselView = (CarouselView) findViewById(R.id.carouselView);

        //db에 있는 메인이미지 받아오기
        Thread getMainImage = new Thread(){
            @Override
            public void run() {
                super.run();
                //images = new ArrayList<>();
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

        //추천 스토리 가로스크롤
        //db에 있는 contentId별 content정보 받아오기
        Thread getRecommend = new Thread(){
            @Override
            public void run() {
                super.run();
                myRecommendDataset = getRecommendData();
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
        RVrecommend = (RecyclerView) findViewById(R.id.RVrecommend);
        RVrecommend.setHasFixedSize(true);
        RVrecommend.setNestedScrollingEnabled(false);
        mRecommendLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RVrecommend.setLayoutManager(mRecommendLayoutManager);
        mRecommendAdapter = new MainRecommendAdapter(myRecommendDataset, MainActivity.this);
        RVrecommend.setAdapter(mRecommendAdapter);

        //신규 스토리 세로스크롤
        //db에 있는 contentId별 content정보 받아오기
        Thread getNew = new Thread(){
            @Override
            public void run() {
                super.run();
                myNewDataset = getNewData();
            }
        };
        getNew.start();
        try {
            getNew.join();  //모든처리 thread처리 기다리기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getNewBitmap(); //content Image받아오기

        //recycleview사용선언
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

        // Adding FloatingActionButton to the activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //컨텐츠 가져오기
        HashMap<Integer,Class> map = new HashMap<>();
        map.put(R.id.loginModuel, UserLogin.class);
        map.put(R.id.contentInsert,ContentInsertMain.class);
        map.put(R.id.cok,TestActivity.class);

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

        /*Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);*/
    }

    //Android BackButton EventListener
    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Log.i(TAG, "closeDrawer");
            drawer.closeDrawer(GravityCompat.START);

        } else if(!searchView.isIconified()) {  //아이콘인 경우 true, 펼쳐져 있는 경우 false
            Log.i(TAG, "isIconified");
            //iconifi가 false이므로 false로 설정하여 펼쳐져 있는 것을 닫음

            searchView.setIconified(true);
        } else {
            Log.i(TAG, "onBackPressed의 else");
            super.onBackPressed();
        }

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

    //appbar의 메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        /*// Get the SearchView and set the searchable configuration
        searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        if(searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }

        if(searchView != null){
            Log.i(TAG, "onCreateOptionsMenu에서 searchView가 있는 경우");
            searchView.setFitsSystemWindows(true);
            Rect rect = new Rect();
            //rect.

            // Assumes current activity is the searchable activity
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            //기본 아이콘 상태. 클릭하면 확장
            searchView.setIconifiedByDefault(true);
            //커스텀 어댑터 또는 기본 어댑터 선택 가능. 기본 어댑터는 searchableinfo와 관련된 제안 제공자로부터 제안을 보여주는 데 사용
            searchView.setSuggestionsAdapter(simpleCursorAdapter);

            //제안 감시
            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int position) {
                    CursorAdapter cursorAdapter = searchView.getSuggestionsAdapter();
                    Cursor cursor = cursorAdapter.getCursor();
                    cursor.moveToPosition(position);
                                                            //컬럼 이름에 대한 인덱스 반환
                    searchView.setQuery(cursor.getString(cursor.getColumnIndex("user_id")), false);
                    //searchView.setQuery(cursor.getString(cursor.getColumnIndex("subject")), false);
                    return true;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    return true;
                }
            });



            //searchView를 클릭하거나 타이핑하는 것을 감시
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    int size = searchSuggestionsList.size();

                    //자료 필터
                    final MatrixCursor matrixCursor = new MatrixCursor(new String[]{BaseColumns._ID, "user_id", "content_subject"});
                    Log.i(TAG, "onQueryTextChange의 searchSuggestionsList.size()= " + size);

                    for(int i=0; i<size; i++) {
                        if ("".equals(newText)) {

                        } else if (searchSuggestionsList.get(i).getUser().getUser_id().toLowerCase().startsWith(newText.toLowerCase()) //입력되는 문자열의 시작부분이 "user_id"의 value와 같은지 검사
                                || searchSuggestionsList.get(i).getContent_subject().toLowerCase().startsWith(newText.toLowerCase())) {
                            matrixCursor.addRow(new Object[]{i, searchSuggestionsList.get(i).getUser().getUser_id(), searchSuggestionsList.get(i).getContent_subject()});

                        }
                    }
                    simpleCursorAdapter.changeCursor(matrixCursor);
                    return true;
                }
            });

            searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){

                    }
                }
            });

            //메뉴의 검색 아이콘이 확장 또는 축소 됐을 때
            MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    Log.i(TAG, "onMenuItemActionExpand");
                    //처음에 onStart()에서 가져온 리스트 클리어
                    searchSuggestionsList.clear();
                    getContentList();
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    Log.i(TAG, "onMenuItemActionCollapse");
                    if(searchSuggestionsList != null){
                        searchSuggestionsList.clear();
                    }

                    return true;
                }
            });
        }*/

        //SearchView를 굳이 가져올 필요 없이 메뉴 아이콘 눌러서 확장될 때 레이아웃 지정
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.i(TAG, "onMenuItemActionExpand");
                //처음에 onStart()에서 가져온 리스트 클리어
                searchSuggestionsList.clear();
                getContentList();
                animateSearchToolbar(1, true, true);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.i(TAG, "onMenuItemActionCollapse");
                if(searchSuggestionsList != null){
                    searchSuggestionsList.clear();
                }
                if(searchItem.isActionViewExpanded()) {
                    animateSearchToolbar(1, false, false);
                }

                return true;
            }
        });
        //메뉴가 보이도록 하려면 반드시 true
        //return super.onCreateOptionsMenu(menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //액티비티 시작시 검색 제안 리스트 다시 가져오기
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        searchSuggestionsList.clear();
        getContentList();
    }


    //content 가져오기
    private void getContentList(){
        final String strUrl = "http://192.168.12.23:8888/content/photo/new";
        HashMap<String, String> headers = new HashMap<>();

        contents = new ArrayList<>();

        //volley 테스트
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        ContentRequest cr = new ContentRequest(strUrl, Content.class, headers, new Response.Listener<ArrayList<Content>>() {
            @Override
            public void onResponse(ArrayList<Content> response) {
                if(response != null) {
                    Log.i(TAG, "onResponse의 response= " + response);
                    contents = response;
                    Log.i(TAG, "onResponse의 contents= " + contents);
                    Log.i(TAG, "onResponse의 contents.size()= " + contents.size());
                    for(int i=0; i<contents.size(); i++){
                        searchSuggestionsList.add(contents.get(i));
                    }

                    Log.i(TAG, "onResponse의 searchSuggestionsMap= " + searchSuggestionsList);
                    loadingProgress.dismiss();
                    Log.i(TAG, "onResponse의 loadingProgress.dismiss() 다음");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse= " + error.toString());
            }
        });
        cr.setRetryPolicy(new DefaultRetryPolicy(CONNECTION_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(cr);
        //progress dialog 초기화 및 보이기
        //매개변수로 getApplicationContext()를 주면 에러
        loadingProgress = new ProgressDialog(MainActivity.this);
        loadingProgress.setMessage("Loading...");
        loadingProgress.show();
    }




    //volley로 서버의 content 정보를 가져오기 위한 클래스
    public class ContentRequest extends Request<ArrayList<Content>>{
        private final Class<Content> clazz;
        private final Map<String, String> headers;
        private final Response.Listener<ArrayList<Content>> listener;

        //초기화용 생성자
        //url 주소, 받은 Json을 파싱할 타입(클래스), 헤더 정보, 리스너를 받아서 초기화
        public ContentRequest(String url, Class<Content> clazz, Map<String, String> headers, Response.Listener<ArrayList<Content>> listener, Response.ErrorListener errorListener){
            super(Method.GET, url, errorListener);
            this.clazz = clazz;
            this.headers = headers;
            this.listener = listener;
        }

        //요청과 함께 전달되는 추가 헤더 반환
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return headers != null ? headers : super.getHeaders();
        }

        //전달(delivery) 위해서 파싱된 응답을 주어진 타입으로 캡슐화
        @Override
        protected Response<ArrayList<Content>> parseNetworkResponse(NetworkResponse response) {
            ArrayList<Content> contents = null;
            try {
                if(response != null){
                    String strJson = new String(response.data, "UTF-8");

                    contents =(ArrayList<Content>) JSON.parseArray(strJson, clazz);
                }
                Log.i(TAG, "parseNetworkResponse의 response" + URLEncoder.encode(response.data.toString(), "UTF-8"));

                //파싱된 결과를 포함한 response<T>를 반환
                return Response.success(contents,
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            }
        }


        //parserNetworkResponse()에 반환된 객체와 함께 메인 스레드에 호출
        //대부분의 요청은 callback interface를 호출
        //파싱에 실패한 응답은 전달하지 않는다. non-null보장
        @Override
        protected void deliverResponse(ArrayList<Content> response) {
            listener.onResponse(response);
        }


    }

    //메인이미지 캐러셀뷰 부분
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageBitmap(images.get(position).getBitmap());
        }
    };
    //메인이미지 캐러셀뷰
    public List<MainImage> getMainImage(){
        HttpURLConnection conn = null;
        List<MainImage> imgs = null;
        String qry = Value.mainImageURL;
        try {
            URL strUrl = new URL(qry);
            conn = (HttpURLConnection) strUrl.openConnection();
            conn.setDoInput(true);//서버로부터 결과값을 응답받음

            conn.setRequestMethod("GET");
            Log.i(TAG, "1.메인이미지 qry= " + qry);

            final int responseCode = conn.getResponseCode(); //정상인 경우 200번, 그 외 오류있는 경우 오류 번호 반환
            Log.i(TAG, "2.메인이미지 responseCode= " + responseCode);
            switch (responseCode){
                case HttpURLConnection.HTTP_OK:

                    InputStream is = conn.getInputStream();
                    Reader reader = new InputStreamReader(is, "UTF-8");
                    BufferedReader br = new BufferedReader(reader);

                    String responseData = br.readLine();
                    Log.i(TAG, "3.메인이미지 response data= " + responseData);

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
                        Log.i(TAG, "4.메인이미지 bitmap= " + bitmap);
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

    //추천스토리 가로스크롤
    public List<Content> getRecommendData(){
        List<Content> contents = null;
        HttpURLConnection conn = null;

        String qry = Value.contentURL+"/recommend";
        try {
            URL strUrl = new URL(qry);
            conn = (HttpURLConnection) strUrl.openConnection();
            conn.setDoInput(true);//서버로부터 결과값을 응답받음
            conn.setRequestMethod("GET");
            Log.i(TAG, "1.추천스토리 qry= " + qry);

            final int responseCode = conn.getResponseCode(); //정상인 경우 200번, 그 외 오류있는 경우 오류 번호 반환
            Log.i(TAG, "2.추천스토리 responseCode= " + responseCode);
            switch (responseCode){
                case HttpURLConnection.HTTP_OK:

                    InputStream is = conn.getInputStream();
                    Reader reader = new InputStreamReader(is, "UTF-8");
                    BufferedReader br = new BufferedReader(reader);
                    String responseData = null;

                    responseData = br.readLine();
                    Log.i(TAG, "3.추천스토리 response data= " + responseData);

                    contents = JSON.parseArray(responseData, Content.class);

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
    public void getRecommendBitmap(){
        Thread thread2 = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    for (int i = 0; i < myRecommendDataset.size(); i++) {
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(Value.contentPhotoURL+"/" + myRecommendDataset.get(i).getContent_id() + "/" + myRecommendDataset.get(i).getPhoto_file_name()).getContent());
                        myRecommendDataset.get(i).setBitmap(bitmap);
                        Log.i(TAG, "4.추천스토리 bitmap= " + bitmap);
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

    //신규스토리 가로스크롤
    public List<Content> getNewData(){
        List<Content> contents = null;
        HttpURLConnection conn = null;

        String qry = Value.contentURL+"/new";

        try {
            URL strUrl = new URL(qry);
            conn = (HttpURLConnection) strUrl.openConnection();
            conn.setDoInput(true);//서버로부터 결과값을 응답받음
            conn.setRequestMethod("GET");
            Log.i(TAG, "1.신규스토리 qry= " + qry);


            final int responseCode = conn.getResponseCode(); //정상인 경우 200번, 그 외 오류있는 경우 오류 번호 반환
            Log.i(TAG, "2.신규스토리 responseCode= " + responseCode);
            switch (responseCode){
                case HttpURLConnection.HTTP_OK:

                    InputStream is = conn.getInputStream();
                    Reader reader = new InputStreamReader(is, "UTF-8");
                    BufferedReader br = new BufferedReader(reader);
                    String responseData = null;

                    responseData = br.readLine();
                    Log.i(TAG, "3.신규스토리 response data= " + responseData);

                    contents = JSON.parseArray(responseData, Content.class);

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
    public void getNewBitmap(){
        Thread thread2 = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    for (int i = 0; i < myNewDataset.size(); i++) {
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(Value.contentPhotoURL+"/" + myNewDataset.get(i).getContent_id() + "/" + myNewDataset.get(i).getPhoto_file_name()).getContent());
                        myNewDataset.get(i).setBitmap(bitmap);
                        Log.i(TAG, "4.신규스토리 bitmap= " + bitmap);
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

    //////////////////////////검색창 띄우기 테스트
    public void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {

        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        drawer.setStatusBarBackgroundColor(Color.CYAN);

        if (show) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int width = toolbar.getWidth() -
                        (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                        ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(toolbar,
                        isRtl(getResources()) ? toolbar.getWidth() - width : width, toolbar.getHeight() / 2, 0.0f, (float) width);
                createCircularReveal.setDuration(250);
                createCircularReveal.start();
            } else {
                TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, (float) (-toolbar.getHeight()), 0.0f);
                translateAnimation.setDuration(220);
                toolbar.clearAnimation();
                toolbar.startAnimation(translateAnimation);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int width = toolbar.getWidth() -
                        (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                        ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(toolbar,
                        isRtl(getResources()) ? toolbar.getWidth() - width : width, toolbar.getHeight() / 2, (float) width, 0.0f);
                createCircularReveal.setDuration(250);
                createCircularReveal.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        toolbar.setBackgroundColor(getThemeColor(MainActivity.this, R.attr.colorPrimary));
                        drawer.setStatusBarBackgroundColor(getThemeColor(MainActivity.this, R.attr.colorPrimaryDark));
                    }
                });
                createCircularReveal.start();
            } else {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (-toolbar.getHeight()));
                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setDuration(220);
                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        toolbar.setBackgroundColor(getThemeColor(MainActivity.this, R.attr.colorPrimary));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                toolbar.startAnimation(animationSet);
            }
            drawer.setStatusBarBackgroundColor(Color.CYAN);
        }
    }

    private boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    private static int getThemeColor(Context context, int id) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        int result = a.getColor(0, 0);
        a.recycle();
        return result;
    }


}
