/*
package com.photovel.content;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.photovel.R;
import com.photovel.http.Value;
import com.vo.Content;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

*/
/**
 * Created by daybreak on 2017-06-29.
 *//*


public class SlideShowTest extends AppCompatActivity{
    private static final String TAG = "SlideShowTest";
    ////////////////슬라이드쇼용 필드//////////////////
    private ViewPager vpSlideShow;
    private Context mContext;
    private ArrayList<Bitmap> images = new ArrayList<>();
    private SeekBar slideSeekBar;

    private static int currPage = 0;
    private static int MAX_PAGES = 0;
    private int maxOffset;
    private int currOffset;
    int index;
    /////////////////////////////////////////////////

    private Content content;
    private final String contentURL = Value.contentURL;
    private final String contentPhotoURL = Value.contentPhotoURL;
    private int content_id=-1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show_test);

        //게시글 번호 가져오기
        Intent intent = getIntent();
        content_id = intent.getIntExtra("content_id",1);

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

        getImage();

        //find  view
        vpSlideShow = (ViewPager) this.findViewById(R.id.VP_slide_show2);
*/
/*        play = (Button) findViewById(R.id.play);
        stop = (Button) findViewById(R.id.stop);*//*


//        PageIndicator indicator = (PageIndicator) findViewById(R.id.slide_seek_bar);

        //SeekBar 찾아오기
        slideSeekBar = (SeekBar) findViewById(R.id.slide_seek_bar2);

        //현재 페이지 표시할 TextView
//        tvCurrPage = (TextView)findViewById(R.id.tv_curr_page);
        MAX_PAGES = images.size();
        Log.i(TAG, "MAX_PAGES= " + images.size());

        */
/*final long frameInterval = 1000;
        final long maxTime = 30000;
        final int totalTime = (int) (maxTime / frameInterval);
        final int secPerFrame = totalTime / contentData.getDetails().size();*//*


        //슬라이드쇼 어댑터 등록
        if(vpSlideShow != null){
            Log.i(TAG, "vpSlideShow 있음");
            for(Bitmap bitmap : images){
                Log.i(TAG, "vpSlideShow에 나올 bitmap= " + bitmap.getRowBytes());
            }
            vpSlideShow.setAdapter(new ContentSlideShowAdapter(images, getApplicationContext()));
        }

        //이미지 ArrayList에 추가
        //setViewPagerImage();


        //뷰 페이저 자동 실행
        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if(currPage == MAX_PAGES){
                    currPage = 0;
                }
                Log.i(TAG, "update의 currpage= "+ currPage);
                vpSlideShow.setCurrentItem(currPage++, true);
                Log.i(TAG, "update의 vpSlideShow= "+ vpSlideShow.getCurrentItem());

            }
        };


        //시간 설정
        Timer timer = new Timer();
        //시작할 때까지의 딜레이 시간과 각 작업 사이의 딜레이 시간 설정
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //핸들러가 등록된 곳에서 runnable이 실행된다
                handler.post(update);
            }
        }, 3000, 3000);



        slideSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(vpSlideShow.isFakeDragging()){
                    //seekbar 클릭시 뷰페이저의 전체 너비 가져온다
                    //전체 너비를 100등분(100%로 표현)해서 현재 progress(위치)와 곱하면 현재 위치
                    int offset = (int) ((maxOffset/100.0) * progress);
                    int dragBy = -1 * (offset - currOffset);
                    //fakeDragBy를 사용하려면 먼저 반드시 beginFakeDrag 사용해야 함
                    vpSlideShow.fakeDragBy(dragBy);
                    currOffset = offset;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                maxOffset = vpSlideShow.getWidth();
                vpSlideShow.beginFakeDrag();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                vpSlideShow.endFakeDrag();
                currOffset = 0;
                seekBar.setProgress(0);
            }
        });
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
            */
/*
            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            // bw.write(id);

            bw.flush();
            bw.close();*//*


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
                        //리스트에 이미지 저장
                        images.add(bitmap);
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
}
*/
