package com.photovel.content;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.photovel.FontActivity2;
import com.photovel.MainActivity;
import com.photovel.NavigationItemSelected;
import com.photovel.R;
import com.photovel.http.Value;
import com.vo.Content;
import com.vo.ContentDetail;
import com.vo.Photo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ContentSlideShowMain extends FontActivity2 implements NavigationView.OnNavigationItemSelectedListener {
    private SearchView searchView;
    private static final String TAG = "AppPermission";
    Toolbar toolbar;

    private RelativeLayout RldetailData;
    private LinearLayout RLdetailDate, LLmenu;
    private TextView icglobe, icleft, icright, tvleft, tvright, iccal, icmarker, icpow, icthumb, iccomment, icshare, btnDetailMenu;
    private TextView tvContentInsertDate, tvContentSubject, tvContentLocation, tvUsername, tvUsername2, tvDuring, tvdetailcount, tvdetailstate, tvContent;
    private TextView tvLikeCount, tvCommentCount, tvShareCount;
    private ImageView ivTopPhoto;
    private LinearLayout btnLookLeft, btnLookRight;
    private FloatingActionButton btnTop;
    private Button btnLike, btnComment, btnShare;
    private List<ContentDetail> myDataset;
    private Content content;
    private int content_id=-1;

    private final String contentURL = Value.contentURL;
    private final String contentPhotoURL = Value.contentPhotoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_slide_show_main);

        Intent intent = getIntent();
        content_id = intent.getIntExtra("content_id",1);
        if(content_id==-1){
            Log.i("content_id","slide_content_id 못받아옴!!!");
        }else {
            Log.i("content_id","slide_content_id : "+content_id);
        }

        // Adding Toolbar to the activity
        toolbar = (Toolbar) findViewById(R.id.slideToolbar);
        setSupportActionBar(toolbar);

        icglobe = (TextView)findViewById(R.id.icglobe);
        icleft = (TextView)findViewById(R.id.icleft);
        icright = (TextView)findViewById(R.id.icright);
        tvleft = (TextView)findViewById(R.id.tvleft);
        tvright = (TextView)findViewById(R.id.tvright);
        iccal = (TextView)findViewById(R.id.iccal);
        icmarker = (TextView)findViewById(R.id.icmarker);
        icpow = (TextView)findViewById(R.id.icpow);
        icthumb = (TextView)findViewById(R.id.icthumb);
        iccomment = (TextView)findViewById(R.id.iccomment);
        icshare = (TextView)findViewById(R.id.icshare);
        btnDetailMenu = (TextView) findViewById(R.id.btnDetailMenu);
        ivTopPhoto = (ImageView) findViewById(R.id.ivTopPhoto);

        tvContentInsertDate = (TextView) findViewById(R.id.tvContentInsertDate);    //컨텐트입력날짜
        tvContentSubject = (TextView) findViewById(R.id.tvContentSubject);           //컨텐트 제목
        tvContentLocation = (TextView) findViewById(R.id.tvContentLocation);        //컨텐트 위치(마지막)
        tvUsername = (TextView) findViewById(R.id.tvUsername);                        //유저 네임
        tvUsername2 = (TextView) findViewById(R.id.tvUsername2);                        //유저 네임
        tvDuring = (TextView) findViewById(R.id.tvDuring);                             //컨텐트 날짜첫날 ~ 날짜 끝날(2016.04.20 ~ 2016.06.20)
        tvdetailstate = (TextView) findViewById(R.id.tvdetailstate);                   //디테일 수
        tvContent = (TextView) findViewById(R.id.tvContent);                            //보고있는 화면 상태(사진/동영상/지도)
        tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);                        //컨텐트 내용
        tvCommentCount = (TextView) findViewById(R.id.tvCommentCount);
        tvShareCount = (TextView) findViewById(R.id.tvShareCount);
        tvdetailcount = (TextView) findViewById(R.id.tvdetailcount);

        btnLookLeft = (LinearLayout) findViewById(R.id.btnLookLeft);
        btnLookRight = (LinearLayout) findViewById(R.id.btnLookRight);

        //imageView를 font로 바꿔주기
        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        icglobe.setTypeface(fontAwesomeFont);
        icleft.setTypeface(fontAwesomeFont);
        icright.setTypeface(fontAwesomeFont);
        iccal.setTypeface(fontAwesomeFont);
        icmarker.setTypeface(fontAwesomeFont);
        icpow.setTypeface(fontAwesomeFont);
        icthumb.setTypeface(fontAwesomeFont);
        iccomment.setTypeface(fontAwesomeFont);
        icshare.setTypeface(fontAwesomeFont);
        btnDetailMenu.setTypeface(fontAwesomeFont);

        icleft.setText(R.string.fa_photo);
        tvleft.setText("사진으로 보기");
        icright.setText(R.string.fa_flag);
        tvright.setText("지도로 보기");

        //UI정렬
        RLdetailDate = (LinearLayout)findViewById(R.id.RLdetailDate);
        LLmenu = (LinearLayout)findViewById(R.id.LLmenu);
        RldetailData = (RelativeLayout)findViewById(R.id.RldetailData);
        RLdetailDate.bringToFront();
        LLmenu.bringToFront();
        RldetailData.bringToFront();

        //db에 있는 contentId별 content정보 받아오기
        Thread thread1 = new Thread(){
            @Override
            public void run() {
                super.run();
                content = getContentData(content_id);
            }
        };
        thread1.start();
        try {
            thread1.join();  //모든처리 thread처리 기다리기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getImage(); //content Image받아오기

        //content정보 추가하기
        tvdetailstate.setText("슬라이드");
        tvContentInsertDate.setText(new SimpleDateFormat("yyyy.MM.dd").format(content.getContent_written_date()));
        tvContentSubject.setText(content.getContent_subject());
        tvUsername.setText(content.getUser().getUser_nick_name());
        tvUsername2.setText(content.getUser().getUser_nick_name());
        tvContent.setText(content.getContent());
        tvdetailcount.setText(String.valueOf(content.getDetails().size()));
        tvLikeCount.setText(String.valueOf(content.getGood_count()));
        tvCommentCount.setText(String.valueOf(content.getComment_count()));
        tvShareCount.setText(String.valueOf(content.getContent_share_count()));

        myDataset = new ArrayList<>();
        for(int i=0; i<content.getDetails().size(); i++){
            Photo ph = new Photo();
            ph.setPhoto_latitude(content.getDetails().get(i).getPhoto().getPhoto_latitude());
            ph.setPhoto_longitude(content.getDetails().get(i).getPhoto().getPhoto_longitude());

            GetCurrentAddress getAddress = new GetCurrentAddress();
            String address = getAddress.getAddress(ph);
            content.getDetails().get(i).getPhoto().setAddress(address);
            if(content.getDetails().get(i).getPhoto().getPhoto_top_flag()==1){
                ivTopPhoto.setImageBitmap(content.getDetails().get(i).getPhoto().getBitmap());
            }
            myDataset.add(content.getDetails().get(i));
        }
        tvContentLocation.setText(content.getDetails().get(content.getDetails().size()-1).getPhoto().getAddress());
        String from = new SimpleDateFormat("yyyy.MM.dd").format(content.getDetails().get(0).getPhoto().getPhoto_date());
        String to = new SimpleDateFormat("yyyy.MM.dd").format(content.getDetails().get(content.getDetails().size()-1).getPhoto().getPhoto_date());
        tvDuring.setText(from+" ~ "+to);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        TextView btnContentInsert = (TextView)hView.findViewById(R.id.btnContentInsert);
        btnContentInsert.setTypeface(fontAwesomeFont);
        btnContentInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ContentInsertMain.class);
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

        //top버튼
        btnTop = (FloatingActionButton) findViewById(R.id.btnTop);
        final NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nestedScrollView);
        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {   //스크롤내리면 보이게
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (oldScrollY > 200) {
                    btnTop.show();
                } else {
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

    //onClick
    public void goLook(View v){
        switch (v.getId()){
            case R.id.btnLookLeft:
                Intent intent=new Intent(this, ContentDetailListMain.class);
                intent.putExtra("content_id",content_id);
                this.startActivity(intent);
                finish();
                break;
            case R.id.btnLookRight:
                Intent intent2=new Intent(this, ContentClusterMain.class);
                intent2.putExtra("content_id",content_id);
                this.startActivity(intent2);
                finish();
                break;
        }
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
                        Intent intent = new Intent(ContentSlideShowMain.this, ContentUpdateMain.class);
                        intent.putExtra("content_id", content_id);
                        ContentSlideShowMain.this.startActivity(intent);
                        Toast.makeText(getApplicationContext(), "수정", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_delete:
                        AlertDialog.Builder dalert_confirm = new AlertDialog.Builder(ContentSlideShowMain.this);
                        dalert_confirm.setMessage("정말 글을 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final String url = Value.contentURL+"/"+content_id;
                                        Thread th = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                DataOutputStream dos = null;
                                                HttpURLConnection conn = null;
                                                URL connectURL = null;
                                                try {
                                                    connectURL = new URL(url);

                                                    conn = (HttpURLConnection) connectURL.openConnection();
                                                    conn.setDoInput(true);
                                                    conn.setDoOutput(true);
                                                    conn.setUseCaches(false);
                                                    conn.setRequestMethod("DELETE");

                                                    int responseCode = conn.getResponseCode();
                                                    Log.i("responseCode","삭제 : "+responseCode);

                                                    switch (responseCode){
                                                        case HttpURLConnection.HTTP_OK:
                                                            Log.i("responseCode","삭제성공");
                                                            break;
                                                        default:
                                                            Log.i("responseCode","삭제실패 responseCode: " +responseCode);
                                                            break;
                                                    }

                                                } catch (MalformedURLException e) {
                                                    e.printStackTrace();
                                                } catch (ProtocolException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        th.start();
                                        try {
                                            th.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(getApplicationContext(),"삭제성공",Toast.LENGTH_SHORT).show();
                                        Intent dintent = new Intent(getApplicationContext(), MainActivity.class);
                                        dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //재사용 ㄴㄴ
                                        startActivity(dintent);
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
                        AlertDialog.Builder walert_confirm = new AlertDialog.Builder(ContentSlideShowMain.this);
                        walert_confirm.setMessage("정말 글을 신고 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final String url = Value.contentURL+"/"+content_id+"/warning";
                                        Thread th = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                DataOutputStream dos = null;
                                                HttpURLConnection conn = null;
                                                URL connectURL = null;
                                                try {
                                                    connectURL = new URL(url);

                                                    conn = (HttpURLConnection) connectURL.openConnection();
                                                    conn.setDoInput(true);
                                                    conn.setDoOutput(true);
                                                    conn.setUseCaches(false);
                                                    conn.setRequestMethod("POST");

                                                    int responseCode = conn.getResponseCode();
                                                    Log.i("responseCode","신고 : "+responseCode);

                                                    switch (responseCode){
                                                        case HttpURLConnection.HTTP_OK:
                                                            Log.i("responseCode","신고성공");
                                                            break;
                                                        default:
                                                            Log.i("responseCode","신고실패 responseCode: " +responseCode);
                                                            break;
                                                    }

                                                } catch (MalformedURLException e) {
                                                    e.printStackTrace();
                                                } catch (ProtocolException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        th.start();
                                        try {
                                            th.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(getApplicationContext(),"신고성공",Toast.LENGTH_SHORT).show();
                                        Intent wintent = new Intent(getApplicationContext(), MainActivity.class);
                                        wintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //재사용 ㄴㄴ
                                        startActivity(wintent);
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

    //DB에서 content정보 받아오기
    public Content getContentData(int id){
        Content content = null;
        HttpURLConnection conn = null;
        Log.i(TAG, "getPhotoData의 id= " + id);

        String qry = contentURL+"/" + id;
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

                    content = JSON.parseObject(responseData, Content.class);

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
    public void getImage(){
        Thread thread2 = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    for (int i = 0; i < content.getDetails().size(); i++) {
                        //Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL("http://photovel.com/upload/" + content.getContent_id() + "/" + content.getDetails().get(i).getPhoto().getPhoto_file_name()).getContent());
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(contentPhotoURL+ "/" + content.getContent_id() + "/" + content.getDetails().get(i).getPhoto().getPhoto_file_name()).getContent());
                        content.getDetails().get(i).getPhoto().setBitmap(bitmap);
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
}
