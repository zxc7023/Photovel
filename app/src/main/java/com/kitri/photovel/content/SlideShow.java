package com.kitri.photovel.content;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.kitri.photovel.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.alibaba.fastjson.JSON;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.kitri.vo.Content;
import com.kitri.vo.ContentDetail;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.acl.Group;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SlideShow extends AppCompatActivity {
    private ViewFlipper mViewFlipper;
    private Context mContext;
    int index;


    private static final String TAG = "SlideShow";

    RadioButton b1, b2, b3;//radio button for indicator
    Button play, stop;
    SeekBar slideSeekBar;
    TextView tvCurrPage;
    RoundCornerProgressBar rcpb;

    Content contentData;
    List<ContentDetail> contentDetailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);

        mContext = this;

        Intent intent = getIntent();
        final int id = intent.getIntExtra("content_id", 1);

        Log.i(TAG, "onCreate의 id= " + id);


        final PhotoData photoData = new PhotoData(id);

        Thread newThread = new Thread(){
            @Override
            public void run() {
                super.run();
                contentData = photoData.getContentData(id);
                Log.i(TAG, "onCreate의 contentDataList= " + contentData);
            }
        };
        newThread.start();
        try {
            newThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        getImage();



        //SequenceEncoder enc = new SequenceEncoder();
// GOP size will be supported in 0.2
// enc.getEncoder().setKeyInterval(25);
        /*for(...) {
            BufferedImage image = ... // Obtain an image to encode
            enc.encodeImage(image);
        }
        enc.finish();*/




        contentDetailList = contentData.getDetails();
        Log.i(TAG, "onCreate의 contentDetailList= " + contentDetailList.toString());

        String photoFileName = null;
        for(ContentDetail cd : contentDetailList){
            photoFileName = cd.getPhoto().getPhoto_file_name();
            Log.i(TAG, "onCreate의 photoFileName= " + photoFileName);
        }



        getImage();

        //((ImageView)findViewById(R.id.testImageVIew)).setImageBitmap(contentData.getDetails().get(0).getPhoto().getBitmap());



        //find  view
        mViewFlipper = (ViewFlipper) this.findViewById(R.id.view_flipper);
        play = (Button) findViewById(R.id.play);
        stop = (Button) findViewById(R.id.stop);

        //SeekBar 찾아오기
        slideSeekBar = (SeekBar) findViewById(R.id.slide_seek_bar);
        //SeekBar 분할
//        slideSeekBar.setMax(contentData.getDetails().size()-1);

        //현재 페이지 표시할 TextView
        tvCurrPage = (TextView)findViewById(R.id.tv_curr_page);

        final long frameInterval = 1000;
        final long maxTime = 30000;
        final int totalTime = (int) (maxTime / frameInterval);
        final int secPerFrame = totalTime / contentData.getDetails().size();


        mViewFlipper.setFlipInterval(secPerFrame*1000);// set interval time

        final Animation inFromLeft = AnimationUtils.loadAnimation(this, R.anim.in_from_left);
        Animation outFromLeft = AnimationUtils.loadAnimation(this, R.anim.out_from_left);
        mViewFlipper.setOutAnimation(outFromLeft);
        mViewFlipper.setInAnimation(inFromLeft);//set  animatio style




        MediaPlayer mp = new MediaPlayer();




        setFlipperImage();

/*      상현이형 나중에 추가수정할때 TargetAPI를 높여서 쓰시면 됩니다.
        slideSeekBar.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                int currIndex = mViewFlipper.indexOfChild(mViewFlipper.getCurrentView());
                Log.i(TAG, "slideSeekBar.setOnClickListener의 currIndex= " + currIndex);
                Log.i(TAG, "slideSeekBar.setOnClickListener의 scrollX= " + scrollX);
                Log.i(TAG, "slideSeekBar.setOnClickListener의 oldScrollX= " + oldScrollX);
            }
        });
*/


        rcpb = (RoundCornerProgressBar) findViewById(R.id.slide_progress_bar);
        rcpb.setMax(contentData.getDetails().size());
//        pb.setMax(contentData.getDetails().size());

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
                                //index = (maxTime / interval) / contentData.getDetails().size()
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

//        slideSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Log.i(TAG, "onProgressChanged의 progress= " + progress);
//                index = progress;
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                mViewFlipper.stopFlipping();
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                index = seekBar.getProgress() + 1;
//                Log.i(TAG, "onStopTrackingTouch의 index= " + index);
//                mViewFlipper.setDisplayedChild(index);
//                mViewFlipper.startFlipping();
//            }
//        });

        final Runtime runtime = Runtime.getRuntime();
        final long usedMemInMB=(runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        final long maxHeapSizeInMB=runtime.maxMemory() / 1048576L;
        final long availHeapSizeInMB = maxHeapSizeInMB;
        Log.i(TAG, "usedMemInMB= " + usedMemInMB);
        Log.i(TAG, "maxHeapSizeInMB= " + maxHeapSizeInMB);
        Log.i(TAG, "availHeapSizeInMB= " + availHeapSizeInMB);

    }




    public void setFlipperImage(){
        for(int i=0, size=contentData.getDetails().size(); i<size; i++){
            ImageView iView = new ImageView(this);
            iView.setImageBitmap(contentData.getDetails().get(i).getPhoto().getBitmap());
            mViewFlipper.addView(iView);
        }
    }



    /**
     * 이미지를 서버에서 가져오는 메서드입니다.
     */
    public void getImage(){


        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    for (int i = 0; i < contentData.getDetails().size(); i++) {
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL("http://photovel.com/upload/" + contentData.getContent_id() + "/" + contentData.getDetails().get(i).getPhoto().getPhoto_file_name()).getContent());
                        contentData.getDetails().get(i).getPhoto().setBitmap(bitmap);
                        //File filePath = new File(Environment.getExternalStorageDirectory());
                        //FileUtils.copyURLToFile(new URL("http://photovel.com/upload/" + contentData.getContent_id() + "/" + contentData.getDetails().get(i).getPhoto().getPhotoFileName()), );
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public class PhotoData extends Thread{
        int id;
        private int responseCode;

        public PhotoData(int id){
            this.id = id;
        }

        @Override
        public void run() {
            super.run();
            getContentData(id);
        }

        public Content getContentData(int id){
            Content content = null;
            HttpURLConnection conn = null;
            Log.i(TAG, "getPhotoData의 id= " + id);
            String qry = "http://www.photovel.com/content/photo/" + id;
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
//                bw.write(id);

                bw.flush();
                bw.close();*/

                responseCode = conn.getResponseCode(); //정상인 경우 200번, 그 외 오류있는 경우 오류 번호 반환
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



    }

}