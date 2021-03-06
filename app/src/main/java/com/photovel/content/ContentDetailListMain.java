package com.photovel.content;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.photovel.FcmPushTest;
import com.photovel.FontActivity2;
import com.photovel.MainActivity;
import com.photovel.NavigationItemSelected;
import com.photovel.R;
import com.photovel.http.JsonConnection;
import com.photovel.http.Value;
import com.photovel.setting.SettingMain;
import com.photovel.user.UserBitmapEncoding;
import com.vo.Comment;
import com.vo.Content;
import com.vo.ContentDetail;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentDetailListMain extends FontActivity2 implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "AppPermission";
    Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ContentDetailListAdapter mAdapter;

    private RelativeLayout RldetailData;
    private LinearLayout RLdetailDate, LLmenu, btnMoreUserContent, btnLike, btnComment, btnBookmark, btnShare;
    private TextView icglobe, icleft, icright, tvleft, tvright, iccal, icmarker, icbookmark, icthumb, iccomment, icshare, btnDetailMenu;
    private TextView tvContentInsertDate, tvContentSubject, tvContentLocation, tvUsername, tvUsername2, tvDuring, tvdetailcount, tvdetailstate, tvContent;
    private TextView tvLikeCount, tvlike, tvbookmark, tvCommentCount, tvShareCount;
    private LinearLayout btnLookLeft, btnLookRight;
    private ImageView ivTopPhoto, userProfile, userProfile1;
    private FloatingActionButton btnTop;
    private List<ContentDetail> myDataset;
    private Content content;
    private int content_id=-1, likeFlag=0, bookmarkFlag=0;
    private String user_id, user_nick_name, user_profile;
    CircularImageView navUserProfile, myProfile;

    //comment
    private BottomSheetBehavior bottomSheetBehavior;
    private RelativeLayout RlComment;
    private RecyclerView RVComment;
    private LinearLayoutManager mCommentLayoutManager;
    private CommentAdapter mCommentAdapter;
    private List<Comment> myCommentDataset;
    private LinearLayout llBack;
    private TextView btnBack;
    private EditText etComment;

    private int friend_state;
    private String templateId = "4639";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_detail_list_main);

        final Intent intent = getIntent();
        content_id = intent.getIntExtra("content_id",-1);
        if(content_id==-1){
            Log.i("content_id","detail_content_id를 못받아옴");
        }else {
            Log.i("content_id", "detail_content_id : " + content_id);
        }

        SharedPreferences get_to_eat = getSharedPreferences("loginInfo", MODE_PRIVATE);
        user_id = get_to_eat.getString("user_id","notFound");
        user_nick_name = get_to_eat.getString("user_nick_name","notFound");
        user_profile = get_to_eat.getString("user_profile","notFound");

        // Adding Toolbar to the activity
        toolbar = (Toolbar) findViewById(R.id.detailListToolbar);
        setSupportActionBar(toolbar);

        icglobe = (TextView)findViewById(R.id.icglobe);
        iccal = (TextView)findViewById(R.id.iccal);
        icmarker = (TextView)findViewById(R.id.icmarker);
        icbookmark = (TextView)findViewById(R.id.icbookmark);
        icthumb = (TextView)findViewById(R.id.icthumb);
        iccomment = (TextView)findViewById(R.id.iccomment);
        icshare = (TextView)findViewById(R.id.icshare);
        icleft = (TextView)findViewById(R.id.icleft);
        icright = (TextView)findViewById(R.id.icright);
        tvleft = (TextView)findViewById(R.id.tvleft);
        tvright = (TextView)findViewById(R.id.tvright);
        btnDetailMenu = (TextView) findViewById(R.id.btnDetailMenu);
        ivTopPhoto = (ImageView) findViewById(R.id.ivTopPhoto);
        userProfile = (ImageView) findViewById(R.id.userProfile);
        userProfile1 = (ImageView) findViewById(R.id.userProfile1);

        tvContentInsertDate = (TextView) findViewById(R.id.tvContentInsertDate);    //컨텐트입력날짜
        tvContentSubject = (TextView) findViewById(R.id.tvContentSubject);           //컨텐트 제목
        tvContentLocation = (TextView) findViewById(R.id.tvContentLocation);        //컨텐트 위치(마지막)
        tvUsername = (TextView) findViewById(R.id.tvUsername);                        //유저 네임
        tvUsername2 = (TextView) findViewById(R.id.tvUsername2);                        //유저 네임
        tvDuring = (TextView) findViewById(R.id.tvDuring);                             //컨텐트 날짜첫날 ~ 날짜 끝날(2016.04.20 ~ 2016.06.20)
        tvdetailstate = (TextView) findViewById(R.id.tvdetailstate);                  //보고있는 화면 상태(사진/동영상/지도)
        tvContent = (TextView) findViewById(R.id.tvContent);                            //컨텐트 내용
        tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);
        tvlike = (TextView) findViewById(R.id.tvlike);
        tvbookmark = (TextView) findViewById(R.id.tvbookmark);
        tvCommentCount = (TextView) findViewById(R.id.tvCommentCount);
        tvShareCount = (TextView) findViewById(R.id.tvShareCount);
        tvdetailcount = (TextView) findViewById(R.id.tvdetailcount);                    //디테일 수
        btnBack = (TextView) findViewById(R.id.btnBack);

        btnLookLeft = (LinearLayout) findViewById(R.id.btnLookLeft);
        btnLookRight = (LinearLayout) findViewById(R.id.btnLookRight);
        btnMoreUserContent = (LinearLayout) findViewById(R.id.btnMoreUserContent);
        btnLike = (LinearLayout) findViewById(R.id.btnLike);
        btnComment = (LinearLayout) findViewById(R.id.btnComment);
        btnBookmark = (LinearLayout) findViewById(R.id.btnBookmark);
        btnShare = (LinearLayout) findViewById(R.id.btnShare);


        btnLookLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"지도로보기",Toast.LENGTH_SHORT).show();
                Log.i("ddd","지도로 보기 클릭");
                Intent intent=new Intent(getApplicationContext(), ContentClusterMain.class);
                intent.putExtra("content_id",content_id);
                startActivity(intent);
                finish();
            }
        });
        //imageView를 font로 바꿔주기
        final Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        icglobe.setTypeface(fontAwesomeFont);
        icleft.setTypeface(fontAwesomeFont);
        icright.setTypeface(fontAwesomeFont);
        iccal.setTypeface(fontAwesomeFont);
        icmarker.setTypeface(fontAwesomeFont);
        icbookmark.setTypeface(fontAwesomeFont);
        icthumb.setTypeface(fontAwesomeFont);
        iccomment.setTypeface(fontAwesomeFont);
        icshare.setTypeface(fontAwesomeFont);
        btnDetailMenu.setTypeface(fontAwesomeFont);
        btnBack.setTypeface(fontAwesomeFont);

        icleft.setText(R.string.fa_flag);
        tvleft.setText("지도로 보기");
        icright.setText(R.string.fa_video_camera);
        tvright.setText("슬라이드로 보기");

        //UI정렬
        RLdetailDate = (LinearLayout)findViewById(R.id.RLdetailDate);
        LLmenu = (LinearLayout)findViewById(R.id.LLmenu);
        RldetailData = (RelativeLayout)findViewById(R.id.RldetailData);
        RLdetailDate.bringToFront();
        LLmenu.bringToFront();
        RldetailData.bringToFront();

        //사진 스토리, 댓글 객체 받아오기
        Thread detailList = new Thread(){
            @Override
            public void run() {
                super.run();
                String responseData = JsonConnection.getConnection(Value.contentURL+"/"+content_id+"/"+user_id, "GET", null);
                content = JSON.parseObject(responseData, Content.class);
                myDataset = content.getDetails();
                myCommentDataset = content.getComments();
            }
        };
        detailList.start();
        try {
            detailList.join();  //모든처리 thread처리 기다리기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //content의 bitmap 받아오기
        List<Content> tmpContent = new ArrayList<>();
        tmpContent.add(content);
        JsonConnection.setBitmap(tmpContent, Value.contentPhotoURL);
        if(content.getUser().getUser_profile_photo() == null){
            Bitmap profile = BitmapFactory.decodeResource(getResources(),R.drawable.ic_profile_circle);
            content.getUser().setBitmap(profile);
        }

        //사진 스토리 bitmap 받아오기
        JsonConnection.setBitmap(myDataset, Value.contentPhotoURL);

        //코멘트 bitmap 받아오기
        JsonConnection.setBitmap(myCommentDataset, Value.contentPhotoURL);

        //디테일 메뉴 보이기 전에 글쓴이 == 내계정 확인
        if(!user_id.equals(content.getUser().getUser_id())){
            LLmenu.setVisibility(View.GONE);
        }

        //content정보 추가하기
        tvdetailstate.setText("사진");
        tvContentInsertDate.setText(new SimpleDateFormat("yyyy.MM.dd").format(content.getContent_written_date()));
        tvContentSubject.setText(content.getContent_subject());
        tvUsername.setText(content.getUser().getUser_nick_name());
        tvUsername2.setText(content.getUser().getUser_nick_name());
        tvContent.setText(content.getContent());
        tvdetailcount.setText(String.valueOf(content.getDetails().size()));
        tvLikeCount.setText(String.valueOf(content.getGood_count()));
        tvCommentCount.setText(String.valueOf(content.getComment_count()));
        tvShareCount.setText(String.valueOf(content.getContent_share_count()));

        //bookmark유무
        if(content.getBookmark_status() == 1){
            icbookmark.setText(R.string.fa_bookmark);
            icbookmark.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textBlue));
            tvbookmark.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textBlue));
            bookmarkFlag=1;
        }

        //좋아요 유무
        if(content.getGood_status() == 1){
            icthumb.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textBlue));
            tvlike.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textBlue));
            likeFlag=1;
        }

        //메인 사진 저장
        ivTopPhoto.setImageBitmap(content.getBitmap());
        userProfile.setImageBitmap(content.getUser().getBitmap());
        userProfile1.setImageBitmap(content.getUser().getBitmap());

        //메인 위치 저장
        GetCurrentAddress getAddress = new GetCurrentAddress();
        String address = getAddress.getAddress(content.getPhoto_latitude(), content.getPhoto_longitude());
        tvContentLocation.setText(address);

        //메인 기간 저장
        String from = new SimpleDateFormat("yyyy.MM.dd").format(content.getFr_photo_date());
        String to = new SimpleDateFormat("yyyy.MM.dd").format(content.getTo_photo_date());
        tvDuring.setText(from+" ~ "+to);

        //recycleview사용선언
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new ContentDetailListAdapter(myDataset, ContentDetailListMain.this);
        mRecyclerView.setAdapter(mAdapter);

        //comment
        RlComment = (RelativeLayout) findViewById(R.id.RlComment);
        btnBack = (TextView) findViewById(R.id.btnBack);
        etComment = (EditText) findViewById(R.id.etComment);
        llBack = (LinearLayout) findViewById(R.id.llBack);

        //comment recycleview사용선언
        RVComment = (RecyclerView) findViewById(R.id.RVComment);
        RVComment.setHasFixedSize(true);
        RVComment.setNestedScrollingEnabled(false);
        mCommentLayoutManager = new LinearLayoutManager(this);
        RVComment.setLayoutManager(mCommentLayoutManager);
        mCommentAdapter = new CommentAdapter(myCommentDataset, ContentDetailListMain.this);
        RVComment.setAdapter(mCommentAdapter);

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        bottomSheetBehavior = BottomSheetBehavior.from(RlComment);
        bottomSheetBehavior.setPeekHeight(0);
        if(intent.getIntExtra("comment_insert",-1) == 1){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        myProfile = (CircularImageView)findViewById(R.id.myProfile);
        if(!user_profile.equals("notFound")){
            UserBitmapEncoding ub = new UserBitmapEncoding();
            myProfile.setImageBitmap(ub.StringToBitMap(user_profile));
        }

        //코멘트 전송
        findViewById(R.id.btnCommentSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final JSONObject comment = new JSONObject();
                try {
                    final JSONObject user = new JSONObject();
                    user.put("user_id", user_id);
                    comment.put("content_id", content_id);
                    comment.put("comment_content", etComment.getText());
                    comment.put("user",user);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap<String,String> hashMap = new HashMap<String, String>();
                            JSONObject job = new JSONObject();
                            try {
                                job.put("user_id",content.getUser().getUser_id());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String token = JsonConnection.getConnection(Value.usergetPushTokenURL,"POST",job);
                            Log.i("tokenValue",token);
                            hashMap.put("user_id_sender",user_nick_name);
                            hashMap.put("user_id_receiver",content.getUser().getUser_id());
                            hashMap.put("comment_content", etComment.getText().toString());
                            hashMap.put("content_id", String.valueOf(content_id));
                            FcmPushTest.pushFCMNotification(token,hashMap);
                        }
                    }).start();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("1. comment", comment.toString());

                Thread commentSend = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JsonConnection.getConnection(Value.contentURL+"/"+content_id+"/comment", "POST", comment);
                    }
                });
                commentSend.start();
                try {
                    commentSend.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(), ContentDetailListMain.class);
                intent.putExtra("content_id", content_id);
                intent.putExtra("comment_insert", 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //재사용 ㄴㄴ
                startActivity(intent);
                finish();
            }
        });

        //좋아요 버튼
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread good = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JsonConnection.getConnection(Value.contentURL+"/"+content_id+"/good/"+user_id, "POST", null);
                    }
                });
                good.start();
                try {
                    good.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(likeFlag == 1){
                    icthumb.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.bgDarkGrey));
                    tvlike.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.bgDarkGrey));
                    tvLikeCount.setText(String.valueOf(Integer.parseInt(tvLikeCount.getText().toString())-1));
                    likeFlag=0;
                }else{
                    icthumb.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textBlue));
                    tvlike.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textBlue));
                    tvLikeCount.setText(String.valueOf(Integer.parseInt(tvLikeCount.getText().toString())+1));
                    likeFlag=1;
                }
            }
        });

        //북마크
        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread bookmark = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JsonConnection.getConnection(Value.contentURL+"/"+content_id+"/bookmark/"+user_id, "POST", null);
                    }
                });
                bookmark.start();
                try {
                    bookmark.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(bookmarkFlag == 1){
                    icbookmark.setText(R.string.fa_bookmark_o);
                    icbookmark.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.bgDarkGrey));
                    tvbookmark.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.bgDarkGrey));
                    bookmarkFlag=0;
                }else{
                    icbookmark.setText(R.string.fa_bookmark);
                    icbookmark.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textBlue));
                    tvbookmark.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textBlue));
                    bookmarkFlag=1;
                }
            }
        });

        //공유하기
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentshare(v);
            }
        });

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
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //준기오빠의 낮은 버전을 위해 인텐트할때 넣어주기
                getApplicationContext().startActivity(intent);
            }
        });
        TextView tvUserName = (TextView)hView.findViewById(R.id.tvUserName);
        tvUserName.setText(user_nick_name);
        navUserProfile = (CircularImageView)hView.findViewById(R.id.userProfile);
        if(!user_profile.equals("notFound")){
            UserBitmapEncoding ub = new UserBitmapEncoding();
            navUserProfile.setImageBitmap(ub.StringToBitMap(user_profile));
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

        //user_content 더보기 버튼
        btnMoreUserContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dintent = new Intent(getApplicationContext(), ContentListMain.class);
                user_id = content.getUser().getUser_id();
                dintent.putExtra("user_id",user_id);
                dintent.putExtra("urlflag","");
                getApplicationContext().startActivity(dintent);
                finish();
            }
        });

        //지도로보기 버튼
        btnLookLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), ContentClusterMain.class);
                intent.putExtra("content_id",content_id);
                startActivity(intent);
                finish();
            }
        });

        //슬라이드로보기 버튼
        btnLookRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2=new Intent(getApplicationContext(), ContentSlideShowMain.class);
                intent2.putExtra("content_id",content_id);
                startActivity(intent2);
                finish();
            }
        });
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
                        Intent intent = new Intent(ContentDetailListMain.this, ContentUpdateMain.class);
                        intent.putExtra("content_id", content_id);
                        ContentDetailListMain.this.startActivity(intent);
                        Toast.makeText(getApplicationContext(), "수정", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_delete:
                        AlertDialog.Builder dalert_confirm = new AlertDialog.Builder(ContentDetailListMain.this);
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
                        AlertDialog.Builder walert_confirm = new AlertDialog.Builder(ContentDetailListMain.this);
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
                    case R.id.friend_plus:
                        //친구상태 확인하기
                        friend_state = -1;
                        Thread tfriend_state = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                friend_state = Integer.parseInt(JsonConnection.getConnection(Value.photovelURL+"/friend/new/"+user_id+"/"+content.getUser().getUser_id(), "POST", null));
                            }
                        });
                        tfriend_state.start();
                        try {
                            tfriend_state.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.i("ddd","friend_state"+friend_state);

                        if(friend_state == 0){ //친구가아닐때
                            Toast.makeText(getApplicationContext(),"친구신청을 성공 하였습니다!",Toast.LENGTH_SHORT).show();
                        }else if(friend_state == 1){ //내가 이미 친구신청을 했을때
                            Toast.makeText(getApplicationContext(),"이미 친구신청을 하였습니다",Toast.LENGTH_SHORT).show();
                        }else if(friend_state == 2){ //친구일때
                            Toast.makeText(getApplicationContext(),"이미 친구입니다",Toast.LENGTH_SHORT).show();
                        }else if(friend_state == 3){ //상대방이 이미 친구신청을 했을때
                            Toast.makeText(getApplicationContext(),content.getUser().getUser_id()+"님이 이미 친구신청을 하였습니다",Toast.LENGTH_SHORT).show();
                        }
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
        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    //공유 메뉴클릭시
    public void contentshare(View v){
        android.widget.PopupMenu menu = new android.widget.PopupMenu(this, v);
        menu.inflate(R.menu.content_share_menu);
        menu.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.kakao_share:
                        sendFeedTemplate();
                        tvShareCount.setText(String.valueOf(content.getContent_share_count()+1));
                        Thread share = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                JsonConnection.getConnection(Value.contentURL+"/"+content_id+"/share", "POST", null);
                            }
                        });
                        share.start();
                        try {
                            share.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.facebook_share:
                        Toast.makeText(getApplicationContext(),"아직 개발중입니닷",Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        try {
            Field[] fields = menu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(menu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        menu.show();
    }

    //카카오 링크 공유
    private void sendFeedTemplate() {
        Map<String, String> templateArgs = new HashMap<String, String>();
        templateArgs.put("${img_size}", String.valueOf(myDataset.size()));
        if(myDataset.size() >= 3){
            for(int i=0; i<3; i++){
                templateArgs.put("${image_url"+i+"}", Value.contentPhotoURL+"/"+content_id+"/"+myDataset.get(i).getPhoto().getPhoto_file_name());
            }
        }else{
            for(int i=0; i<myDataset.size(); i++){
                templateArgs.put("${image_url"+i+"}", Value.contentPhotoURL+"/"+content_id+"/"+myDataset.get(i).getPhoto().getPhoto_file_name());
            }
        }

        templateArgs.put("${user_profile}", Value.contentPhotoURL+"/profile/"+content.getUser().getUser_profile_photo());
        templateArgs.put("${user_nick_name}", content.getUser().getUser_nick_name());
        templateArgs.put("${content_subject}",content.getContent_subject());
        templateArgs.put("${content}", content.getContent());
        templateArgs.put("${good_count}", String.valueOf(content.getGood_count()));
        templateArgs.put("${comment_count}",String.valueOf(content.getComment_count()));
        templateArgs.put("${content_share_count}", String.valueOf(content.getContent_share_count()));
        KakaoLinkService.getInstance().sendCustom(this, templateId, templateArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
                Toast.makeText(getApplicationContext(), errorResult.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
            }
        });
    }
}