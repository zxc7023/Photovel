package com.photovel.content;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;
import com.mikhaellopez.circularimageview.CircularImageView;
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

public class ContentSlideShowMain extends FontActivity2 implements NavigationView.OnNavigationItemSelectedListener {
    private SearchView searchView;
    private static final String TAG = "ContentSlideShowMain";
    Toolbar toolbar;

////////////////슬라이드쇼용 필드//////////////////
    private ViewPager vpSlideShow;
    private Context mContext = ContentSlideShowMain.this;
    private ArrayList<Bitmap> images = new ArrayList<>();
    private SeekBar slideSeekBar;
    private ContentSlideShowAdapter contentSlideShowAdapter;
    private ContentSlideShowViewPager contentSlideShowViewPager;
    private TextView tvCurrTime;
    private CountDownTimer countDownTimer;
    private TextView tvCurrPage;


    //이미지 어댑터
    private int MAX_PAGES = 0;
    private int MAX_TIME = 0;
    long TRANS_MILLI; // time in milliseconds between successive task executions.
    int currPage = 0;

/////////////////////////////////////////////////


    private RelativeLayout RldetailData;
    private LinearLayout RLdetailDate, LLmenu, btnLike, btnComment, btnBookmark, btnMoreUserContent, btnShare;
    private TextView icglobe, icleft, icright, tvleft, tvright, iccal, icmarker, icbookmark, icthumb, iccomment, icshare, btnDetailMenu;
    private TextView tvContentInsertDate, tvContentSubject, tvContentLocation, tvUsername, tvUsername2, tvDuring, tvdetailcount, tvdetailstate, tvContent;
    private TextView tvLikeCount, tvlike, tvbookmark, tvCommentCount, tvShareCount;
    private ImageView ivTopPhoto, userProfile, userProfile1;
    private LinearLayout btnLookLeft, btnLookRight;
    private FloatingActionButton btnTop;
    private List<ContentDetail> myDataset;
    private Content content;
    private int content_id=0, likeFlag=0, bookmarkFlag=0;
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

    private String templateId = "4639";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_slide_show_main);

        Intent intent = getIntent();
        content_id = intent.getIntExtra("content_id",-1);
        if(content_id==-1){
            Log.i("content_id","slide_content_id 못받아옴!!!");
        }else {
            Log.i("content_id","slide_content_id : "+content_id);
        }

        SharedPreferences get_to_eat = getSharedPreferences("loginInfo", MODE_PRIVATE);
        user_id = get_to_eat.getString("user_id","notFound");
        user_nick_name = get_to_eat.getString("user_nick_name","notFound");
        user_profile = get_to_eat.getString("user_profile","notFound");

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
        icbookmark = (TextView)findViewById(R.id.icbookmark);
        icthumb = (TextView)findViewById(R.id.icthumb);
        iccomment = (TextView)findViewById(R.id.iccomment);
        icshare = (TextView)findViewById(R.id.icshare);
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
        tvdetailstate = (TextView) findViewById(R.id.tvdetailstate);                   //디테일 수
        tvContent = (TextView) findViewById(R.id.tvContent);                            //보고있는 화면 상태(사진/동영상/지도)
        tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);                        //컨텐트 내용
        tvlike = (TextView) findViewById(R.id.tvlike);
        tvbookmark = (TextView) findViewById(R.id.tvbookmark);
        tvCommentCount = (TextView) findViewById(R.id.tvCommentCount);
        tvShareCount = (TextView) findViewById(R.id.tvShareCount);
        tvdetailcount = (TextView) findViewById(R.id.tvdetailcount);

        btnLookLeft = (LinearLayout) findViewById(R.id.btnLookLeft);
        btnLookRight = (LinearLayout) findViewById(R.id.btnLookRight);
        btnLike = (LinearLayout) findViewById(R.id.btnLike);
        btnComment = (LinearLayout) findViewById(R.id.btnComment);
        btnBookmark = (LinearLayout) findViewById(R.id.btnBookmark);
        btnMoreUserContent = (LinearLayout) findViewById(R.id.btnMoreUserContent);
        btnShare = (LinearLayout) findViewById(R.id.btnShare);

        //imageView를 font로 바꿔주기
        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
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

        //슬라이드 스토리, 댓글 객체 받아오기
        Thread clusterList = new Thread(){
            @Override
            public void run() {
                super.run();
                String responseData = JsonConnection.getConnection(Value.contentURL+"/"+content_id+"/"+user_id, "GET", null);
                content = JSON.parseObject(responseData, Content.class);
                myDataset = content.getDetails();
                myCommentDataset = content.getComments();
            }
        };
        clusterList.start();
        try {
            clusterList.join();  //모든처리 thread처리 기다리기
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

        //content의 이미지 bitmap 가져오기
        JsonConnection.setBitmap(myDataset, Value.contentPhotoURL);
        Log.i(TAG, "myDataset의 크기= " + myDataset.size());
        Log.i(TAG, "myDataset.get(0).getContent_detail_id()= " + myDataset.get(0).getContent_detail_id());
        Log.i(TAG, "myDataset.get(0).getPhoto().getBitmap()= " + myDataset.get(0).getPhoto().getBitmap());


        for(int i=0; i<myDataset.size(); i++){
            images.add(myDataset.get(i).getPhoto().getBitmap());
        }

        /*//content의 이미지 bitmap 가져오기
        for (int i = 0; i < content.getDetails().size(); i++) {
            //Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL("http://photovel.com/upload/" + content.getContent_id() + "/" + content.getDetails().get(i).getPhoto().getPhoto_file_name()).getContent());
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(Value.contentPhotoURL+ "/" + content.getContent_id() + "/" + content.getDetails().get(i).getPhoto().getPhoto_file_name()).getContent());
                content.getDetails().get(i).getPhoto().setBitmap(bitmap);
                //리스트에 이미지 저장
                images.add(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //이미지 배열 만들기
        imgArray = images.toArray(new Bitmap[images.size()]);*/


        //디테일 메뉴 보이기 전에 글쓴이 == 내계정 확인
        if(!content.getUser().getUser_id().equals(user_id)){
            LLmenu.setVisibility(View.GONE);
        }

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
        //userProfile1.setImageBitmap(content.getUser().getBitmap());

        //메인 위치 저장
        GetCurrentAddress getAddress = new GetCurrentAddress();
        String address = getAddress.getAddress(content.getPhoto_latitude(), content.getPhoto_longitude());
        tvContentLocation.setText(address);

        //메인 기간 저장
        String from = new SimpleDateFormat("yyyy.MM.dd").format(content.getFr_photo_date());
        String to = new SimpleDateFormat("yyyy.MM.dd").format(content.getTo_photo_date());
        tvDuring.setText(from+" ~ "+to);

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
        mCommentAdapter = new CommentAdapter(myCommentDataset, ContentSlideShowMain.this);
        RVComment.setAdapter(mCommentAdapter);

        myProfile = (CircularImageView)findViewById(R.id.myProfile);
        if(!user_profile.equals("notFound")){
            UserBitmapEncoding ub = new UserBitmapEncoding();
            myProfile.setImageBitmap(ub.StringToBitMap(user_profile));
        }

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

        //코멘트 전송
        findViewById(R.id.btnCommentSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final JSONObject comment = new JSONObject();
                try {
                    JSONObject user = new JSONObject();
                    user.put("user_id", "leeej9201@gmail.com");
                    comment.put("content_id", content_id);
                    comment.put("comment_content", etComment.getText());
                    comment.put("user",user);
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
                Intent intent = new Intent(getApplicationContext(), ContentSlideShowMain.class);
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
                final String id = "leeej9201@gmail.com";
                Thread good = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JsonConnection.getConnection(Value.contentURL+"/"+content_id+"/good/"+id, "POST", null);
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

        //사진으로보기 버튼
        btnLookLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), ContentDetailListMain.class);
                intent.putExtra("content_id",content_id);
                startActivity(intent);
                finish();
            }
        });

        //지도로보기 버튼
        btnLookRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2=new Intent(getApplicationContext(), ContentClusterMain.class);
                intent2.putExtra("content_id",content_id);
                startActivity(intent2);
                finish();
            }
        });

        ///////////////////////슬라이드쇼/////////////////////
        //뷰 페이저 객체 생성
        vpSlideShow = (ViewPager) this.findViewById(R.id.VP_slide_show);
        //슬라이드 시크바 객체 생성
        slideSeekBar = (SeekBar) findViewById(R.id.slide_seek_bar);
        //시간 보여줄 텍스트 뷰
        tvCurrTime = (TextView) findViewById(R.id.tv_curr_time);

        //이미지 간 이동 속도 조절 위한 객체 생성
        contentSlideShowViewPager = new ContentSlideShowViewPager(this);
        //어댑터에 이미지 배열 전달
        contentSlideShowAdapter = new ContentSlideShowAdapter(this, images);

        vpSlideShow.setAdapter(contentSlideShowAdapter);






        //슬라이드 쇼 실행 메소드
        init();
        ////////////////////////////////////////////////////////////

    }


///////////////슬라이드쇼 실행 부분/////////////////
    private void init(){
        //이미지 넘어가는 텀
        TRANS_MILLI = 3000;
        //이미지가 넘어가는 속도 조절
        contentSlideShowViewPager.setScrollDuration((int)TRANS_MILLI);

        //슬라이드 쇼 전체 이미지 수
        MAX_PAGES = images.size();
        //슬라이드 쇼 전체 시간
        MAX_TIME = (int) TRANS_MILLI * MAX_PAGES;
        //(다음 이미지 띄울 때까지의 시간 * 이미지 개수).
        slideSeekBar.setMax(MAX_TIME);


        //슬라이드 이미지 이동 애니메이션 설정
        vpSlideShow.setPageTransformer(true, new StackTransformer());
        /*vpSlideShow.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                page.setRotationX(0);
                page.setRotationY(0);
                page.setRotation(0);
                page.setScaleX(1);
                page.setScaleY(1);
                page.setPivotX(0);
                page.setPivotY(0);
                page.setTranslationY(0);
                page.setTranslationX(0f);
            }
        });*/

        vpSlideShow.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /*vpSlideShow.setBackground(new BitmapDrawable(getApplicationContext().getResources(),
                        vpSlideShow.));*/
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        countDownTimer = new CountDownTimer(MAX_TIME, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "millisUntilFinished= " + millisUntilFinished);
                //시크바에 시간 진행 표시 -> onSeekBarChangeListener에서 감지

                slideSeekBar.setProgress((int)(MAX_TIME - millisUntilFinished));
            }

            @Override
            public void onFinish() {
                //카운트 다운 타이머 정지
                countDownTimer.cancel();
                //마지막 도달 표시
                slideSeekBar.setProgress(MAX_TIME);
                //도달시 확인 메시지
                AlertDialog.Builder restartConfirm = new AlertDialog.Builder(mContext);
                restartConfirm.setMessage("다시 시작하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        //확인 버튼 눌렀을 때
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                currPage = 0;
                                countDownTimer.start();
                            }
                        }).setNegativeButton("취소",
                        //취소 버튼 눌렀을 때
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int finishItem = images.size()  - 1;
                                int finishTime =(int) (TRANS_MILLI * images.size());
                                vpSlideShow.setCurrentItem(finishItem);
                                slideSeekBar.setProgress(finishTime);
                            }
                        });
                AlertDialog restartAlert = restartConfirm.create();
                restartAlert.show();
            }
        };
        countDownTimer.start();

        slideSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(vpSlideShow.isFakeDragging()){
                    /*//seekbar 클릭시 뷰페이저의 전체 너비 가져온다
                    //마치 여러 사진이 붙어서 드래그로 이동할 수 있는 것처럼 보여주기 위한 설정
                    int offset = (int) (maxOffset/100.0) * progress;
                    int dragBy = -1 * (offset - currOffset);
                    //fakeDragBy를 사용하려면 먼저 반드시 beginFakeDrag 사용해야 함
                    vpSlideShow.fakeDragBy(dragBy);
                    currOffset = offset;*/

                }
                Log.i(TAG, "onProgressChanged의 progress확인= " + progress);
                Log.i(TAG, "milliSecondsToTimer(progress)= " + milliSecondsToTimer(progress));
                //progress 변화에 따라 시간 표시
                if(progress == 0){
                    tvCurrTime.setText(milliSecondsToTimer(0));
                } else {
                    tvCurrTime.setText(milliSecondsToTimer(progress));
                }
                //현재 페이지
                currPage = (int) (progress / TRANS_MILLI);
                Log.i(TAG, "onProgressChanged의 currPage= " + currPage);
                vpSlideShow.setCurrentItem(currPage);
         }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //onStopTrackingTouch에서 새로운 카운트다운 타이머 시작 위해 기존 카운트다운 타이머 끄기
                countDownTimer.cancel();

                vpSlideShow.beginFakeDrag();
            }

            //터치시 시크바의 progress가 변하는 것은 onProgressChanged에서 보여주고
            //onStopTrackingTouch는 손가락을 놓는 시점의 상태 정보만 알려준다
            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                //시크바의 현재 위치
                final int current_position = seekBar.getProgress();
                Log.i(TAG, "onStopTrackingTouch의 current_position= " + current_position);
                //전체 범위에서 현재 위치만큼 빼서 전체 이동할 범위를 줄여준다
                countDownTimer = new CountDownTimer(MAX_TIME - current_position, 1) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        Log.i(TAG, "millisUntilFinished= " + millisUntilFinished);
                        //시크바에 시간 진행 표시 -> onSeekBarChangeListener에서 감지
                        //전체 범위에서 줄어든 범위만큼 빼게 되므로 결국 증가한 만큼 보여준다.
                        slideSeekBar.setProgress((int)(MAX_TIME - millisUntilFinished));
                    }

                    @Override
                    public void onFinish() {
                        //마지막 도달 표시
                        slideSeekBar.setProgress(MAX_TIME);
                        //도달시 확인 메시지
                        AlertDialog.Builder restartConfirm = new AlertDialog.Builder(mContext);
                        restartConfirm.setMessage("다시 시작하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                                //확인 버튼 눌렀을 때
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //처음 페이지로 이동시키고
                                        currPage = 0;
                                        //카운트다운 타이머 시작시키기
                                        countDownTimer.start();
                                    }
                                }).setNegativeButton("취소",
                                //취소 버튼 눌렀을 때
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //취소를 눌렀을 때 마지막 인덱스와 시간으로 보여주기 위한 값
                                        int finishItem = images.size()  - 1;
                                        int finishTime =(int) (TRANS_MILLI * images.size());
                                        vpSlideShow.setCurrentItem(finishItem);
                                        slideSeekBar.setProgress(finishTime);
                                    }
                                });
                        AlertDialog restartAlert = restartConfirm.create();
                        restartAlert.show();
                    }
                }.start();
            }
        });
    }

    //텍스트뷰에 시간 표시하기 위한 메소드. milliseconds를 시간 형태로 보여준다.
    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";


        //milliseconds를 1시간(1초 * 60 = 60초, 1분 * 60 = 60분)으로 나눈 몫
        int hours = (int)( milliseconds / (1000*60*60));
        //milliseconds를 1시간으로 나눈 나머지를 1분으로 나눈 몫
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        //millisconds를 1시간으로 나눈 나머지를 다시 1분으로 나눠서 초단위로 나눈 몫
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // 10초 미만인 경우 한 자리 숫자
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{ //10초 이상인 경우 두 자리 숫자
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
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
                        break;
                    case R.id.action_delete:
                        AlertDialog.Builder dalert_confirm = new AlertDialog.Builder(ContentSlideShowMain.this);
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
                        AlertDialog.Builder walert_confirm = new AlertDialog.Builder(ContentSlideShowMain.this);
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
                        //////////////////친구추가 db처리해야함
                        //   /friend/new/{user_id1:.+}/{user_id2:.+}   user_id1->나 user_id2 ->친구신청할아이디
                        Thread friend_plus = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                JsonConnection.getConnection(Value.photovelURL+"/friend/new/"+user_id+"/"+content.getUser().getUser_id(), "POST", null);
                            }
                        });
                        friend_plus.start();
                        try {
                            friend_plus.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),"친구추가성공",Toast.LENGTH_SHORT).show();
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
        for(int i=0; i<3; i++){
            templateArgs.put("${image_url"+i+"}", Value.contentPhotoURL+"/"+content_id+"/"+myDataset.get(i).getPhoto().getPhoto_file_name());
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
