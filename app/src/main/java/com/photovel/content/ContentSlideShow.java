package com.photovel.content;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.photovel.R;

import com.alibaba.fastjson.JSON;
import com.photovel.http.JsonConnection;
import com.photovel.http.Value;
import com.vo.Content;
import com.vo.ContentDetail;

import java.util.List;

public class ContentSlideShow extends AppCompatActivity {
    private ViewFlipper mViewFlipper;
    int index;
    private int content_id = -1;
    private Content content;

    private static final String TAG = "SlideShow";

    Button play, stop;
    SeekBar slideSeekBar;
    TextView tvCurrPage;

    private List<ContentDetail> myDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_slide_show);

        Intent intent = getIntent();
        content_id = intent.getIntExtra("content_id", 1);

        Log.i(TAG, "onCreate의 id= " + content_id);

        Thread slideShow = new Thread(){
            @Override
            public void run() {
                super.run();
                String responseData = JsonConnection.getConnection(Value.contentURL+"/"+content_id, "GET", null);
                content = JSON.parseObject(responseData, Content.class);
                myDataset = content.getDetails();
            }
        };
        slideShow.start();
        try {
            slideShow.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Bitmap> detailBitmaps = JsonConnection.getBitmap(myDataset, Value.contentPhotoURL);
        for(int i = 0; i < myDataset.size(); i++){
            myDataset.get(i).getPhoto().setBitmap(detailBitmaps.get(i));
        }

        //find  view
        mViewFlipper = (ViewFlipper) this.findViewById(R.id.view_flipper);
        play = (Button) findViewById(R.id.play);
        stop = (Button) findViewById(R.id.stop);

        //SeekBar 찾아오기
        slideSeekBar = (SeekBar) findViewById(R.id.slide_seek_bar);
        //현재 페이지 표시할 TextView
        tvCurrPage = (TextView)findViewById(R.id.tv_curr_page);

        final long frameInterval = 1000;
        final int secPerFrame = 3;
        final int totalTime = secPerFrame * myDataset.size()*1000;

        mViewFlipper.setFlipInterval(secPerFrame*1000);// set interval time

        final Animation inFromLeft = AnimationUtils.loadAnimation(this, R.anim.in_from_left);
        Animation outFromLeft = AnimationUtils.loadAnimation(this, R.anim.out_from_left);
        mViewFlipper.setOutAnimation(outFromLeft);
        mViewFlipper.setInAnimation(inFromLeft);//set  animatio style

        for(int i=0; i<myDataset.size(); i++){
            ImageView iView = new ImageView(this);
            iView.setImageBitmap(myDataset.get(i).getPhoto().getBitmap());
            mViewFlipper.addView(iView);
        }

        slideSeekBar.setMax(totalTime/1000);

        final CountDownTimer cdt = new CountDownTimer(totalTime-1000, frameInterval) {
            @Override
            public void onTick(long millisUntilFinished) {

                int remainSeconds = (int)millisUntilFinished/1000;
                int currSeconds = totalTime/1000 - remainSeconds;
                index = currSeconds / secPerFrame;
                slideSeekBar.setProgress(currSeconds-1);

                String result = String.format("%02d", 00) + ":" + String.format("%02d", currSeconds-1);
                tvCurrPage.setText("현재 시간 : " + result);
            }

            @Override
            public void onFinish() {
                slideSeekBar.setProgress(totalTime/1000);
                mViewFlipper.stopFlipping();
                play.setVisibility(View.VISIBLE);
                String result = String.format("%02d", 00) + ":" + String.format("%02d", totalTime/1000);
                tvCurrPage.setText("현재 시간 : " + result);
            }
        };

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
                mViewFlipper.stopFlipping();
                cdt.cancel();
            }
        });


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
                                //index = (maxTime / interval) / contentData.getDetails().size()
                                slideSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        index = progress / secPerFrame;
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        index = seekBar.getProgress() / secPerFrame;
                                        mViewFlipper.setDisplayedChild(index);
                                    }
                                });

                                slideSeekBar.setOnDragListener(new View.OnDragListener() {
                                    @Override
                                    public boolean onDrag(View v, DragEvent event) {
                                        return false;
                                    }
                                });

                            }
                        });
                    }
                });
            }
        }.start();
    }
}