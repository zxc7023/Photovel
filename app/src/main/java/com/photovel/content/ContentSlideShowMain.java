package com.photovel.content;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.alibaba.fastjson.JSON;
import com.photovel.FontActivity2;
import com.photovel.MainActivity;
import com.photovel.NavigationItemSelected;
import com.photovel.R;
import com.photovel.http.JsonConnection;
import com.photovel.http.Value;
import com.vo.Comment;
import com.vo.Content;
import com.vo.ContentDetail;
import com.vo.Photo;

import org.json.JSONException;
import org.json.JSONObject;

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
    private static final String TAG = "ContentSlideShow";
    Toolbar toolbar;

    private RelativeLayout RldetailData;
    private LinearLayout RLdetailDate, LLmenu, btnLike, btnComment;
    private TextView icglobe, icleft, icright, tvleft, tvright, iccal, icmarker, icpow, icthumb, iccomment, icshare, btnDetailMenu;
    private TextView tvContentInsertDate, tvContentSubject, tvContentLocation, tvUsername, tvUsername2, tvDuring, tvdetailcount, tvdetailstate, tvContent;
    private TextView tvLikeCount, tvCommentCount, tvShareCount;
    private ImageView ivTopPhoto;
    private LinearLayout btnLookLeft, btnLookRight;
    private FloatingActionButton btnTop;
    //private Button btnLike, btnComment, btnShare;
    private List<ContentDetail> myDataset;
    private Content content;
    private int content_id=-1;

    private final String contentURL = Value.contentURL;
    private final String contentPhotoURL = Value.contentPhotoURL;

    //은디수정
    private ViewFlipper mViewFlipper;
    private Context mContext;
    int index;

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

    RadioButton b1, b2, b3;//radio button for indicator
    Button play, stop;
    SeekBar slideSeekBar;
    TextView tvCurrPage;
    RoundCornerProgressBar rcpb;

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
        btnLike = (LinearLayout) findViewById(R.id.btnLike);
        btnComment = (LinearLayout) findViewById(R.id.btnComment);

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

        //슬라이드 스토리, 댓글 객체 받아오기
        Thread clusterList = new Thread(){
            @Override
            public void run() {
                super.run();
                String responseData = JsonConnection.getConnection(Value.contentURL+"/"+content_id, "GET", null);
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
        List<Bitmap> contentBitmaps = JsonConnection.getBitmap(tmpContent, Value.contentPhotoURL);
        for(int i = 0; i < contentBitmaps.size(); i++){
            tmpContent.get(i).setBitmap(contentBitmaps.get(i));
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

        //메인 사진 저장
        ivTopPhoto.setImageBitmap(content.getBitmap());

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

        //Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        btnBack.setTypeface(fontAwesomeFont);

        //comment recycleview사용선언
        RVComment = (RecyclerView) findViewById(R.id.RVComment);
        RVComment.setHasFixedSize(true);
        RVComment.setNestedScrollingEnabled(false);
        mCommentLayoutManager = new LinearLayoutManager(this);
        RVComment.setLayoutManager(mCommentLayoutManager);
        mCommentAdapter = new CommentAdapter(myCommentDataset, ContentSlideShowMain.this);
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
                Intent intent = new Intent(getApplicationContext(), ContentSlideShowMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //재사용 ㄴㄴ
                intent.putExtra("content_id", content_id);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"좋아요 완료!",Toast.LENGTH_SHORT).show();
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

        //find  view
        mViewFlipper = (ViewFlipper) this.findViewById(R.id.view_flipper);
        play = (Button) findViewById(R.id.play);
        stop = (Button) findViewById(R.id.stop);

        //SeekBar 찾아오기
        slideSeekBar = (SeekBar) findViewById(R.id.slide_seek_bar);
        //SeekBar 분할
        //slideSeekBar.setMax(contentData.getDetails().size()-1);

        //현재 페이지 표시할 TextView
        tvCurrPage = (TextView)findViewById(R.id.tv_curr_page);

        final long frameInterval = 1000;
        final long maxTime = 30000;
        final int totalTime = (int) (maxTime / frameInterval);
        final int secPerFrame = totalTime / content.getDetails().size();


        mViewFlipper.setFlipInterval(secPerFrame*1000);// set interval time

        final Animation inFromLeft = AnimationUtils.loadAnimation(this, R.anim.in_from_left);
        Animation outFromLeft = AnimationUtils.loadAnimation(this, R.anim.out_from_left);
        mViewFlipper.setOutAnimation(outFromLeft);
        mViewFlipper.setInAnimation(inFromLeft);//set  animatio style

        MediaPlayer mp = new MediaPlayer();

        for(int i=0 ; i < content.getDetails().size(); i++){
            ImageView iView = new ImageView(this);
            iView.setImageBitmap(content.getDetails().get(i).getPhoto().getBitmap());
            mViewFlipper.addView(iView,mViewFlipper.getWidth(),mViewFlipper.getHeight());
        }

        rcpb = (RoundCornerProgressBar) findViewById(R.id.slide_progress_bar);
        rcpb.setMax(content.getDetails().size());

        final CountDownTimer cdt = new CountDownTimer(maxTime, frameInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "onTick의 millisUntilFinished= " + millisUntilFinished);
                /*int i = (int)(maxTime - millisUntilFinished);
                slideSeekBar.setProgress(index);*/
                int remainSeconds = (int)millisUntilFinished/1000;
                int currSeconds = totalTime - remainSeconds;
                index = currSeconds / secPerFrame;
                slideSeekBar.setProgress(currSeconds);

                //long seconds = TimeUnit.SECONDS.toSeconds(millisUntilFinished);
                String result = String.format("%02d", 00) + ":"
                        + String.format("%02d", currSeconds);

                tvCurrPage.setText("현재 시간 : " + result);
            }

            @Override
            public void onFinish() {

            }
        };

        //slideSeekBar.setMax((int)TimeUnit.MILLISECONDS.toSeconds(maxTime));
        slideSeekBar.setMax(totalTime);

        //play  animation
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play.setVisibility(View.INVISIBLE);
                mViewFlipper.startFlipping();
                cdt.start();
            }
        });

        //stop animation
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play.setVisibility(View.VISIBLE);
                cdt.cancel();
                mViewFlipper.stopFlipping();
            }
        });


        //slideSeekBar.setAnimation();
        //SeekBar의 Progress 진행 위한 Thread
        new Thread(){
            @Override
            public void run() {
                //ViewFlipper의 레이아웃이 변할 경우
                mViewFlipper.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                        //UI에 접근하기 위한 runOnUiThread 구현
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //남은 시간 확인 위한 메소드

                                //ViewFlipper의 자식 뷰의 인덱스 번호
                                index = mViewFlipper.indexOfChild(mViewFlipper.getCurrentView());
                                //index = (maxTime / interval) / myDataset.size()
                                slideSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        Log.i(TAG, "onProgressChanged의 progress= " + progress);
                                        index = progress / secPerFrame;
                                        Log.i(TAG, "onProgressChanged의 index= " + index);
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {
                                        Log.i(TAG, "onStartTrackingTouch의 index= " + index);
                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        Log.i(TAG, "onStopTrackingTouch의 index= " + index);
                                        index = seekBar.getProgress() / secPerFrame;
                                        Log.i(TAG, "onStopTrackingTouch index= " + index);
                                        mViewFlipper.setDisplayedChild(index);
                                    }
                                });

                                slideSeekBar.setOnDragListener(new View.OnDragListener() {
                                    @Override
                                    public boolean onDrag(View v, DragEvent event) {
                                        return false;
                                    }
                                });

                                rcpb.setProgress(index);

                               /* slideSeekBar.setProgress(index);
                                tvCurrPage.setText("현재 페이지 : " + String.valueOf(index+1));*/
                            }
                        });
                    }
                });
            }
        }.start();

        final Runtime runtime = Runtime.getRuntime();
        final long usedMemInMB=(runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        final long maxHeapSizeInMB=runtime.maxMemory() / 1048576L;
        final long availHeapSizeInMB = maxHeapSizeInMB;
        Log.i(TAG, "usedMemInMB= " + usedMemInMB);
        Log.i(TAG, "maxHeapSizeInMB= " + maxHeapSizeInMB);
        Log.i(TAG, "availHeapSizeInMB= " + availHeapSizeInMB);
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
}