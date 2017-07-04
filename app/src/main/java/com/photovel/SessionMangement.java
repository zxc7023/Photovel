package com.photovel;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.internal.pr;
import com.photovel.http.SessionConnection;
import com.photovel.http.Value;
import com.photovel.user.UserLogin;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Junki on 2017-06-23.
 */

public class SessionMangement extends FontActivity2 {

    String cookieValues;
    String sessionCheckValue="0";
    String TAG ="SessionMangeTest";
    int sequence = 0;


    SharedPreferences loginInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        // 저장된 jSession 쿠키값을 받아온다
        loginInfo = getSharedPreferences("loginInfo", MODE_PRIVATE);
        String jSessionValue = loginInfo.getString("Set-Cookie", "fail");
        Log.i(TAG+sequence+++"세션값 : ",jSessionValue);


        /**
         * loginInfo라는 객체속에 "Set-Cookie"로 저장해둔 jSession값에 따라 해당하는 결과를 수행한다.
         * jSession이 있는경우는 두가지의 경우로 나누어 지는데,
         * 서버의 loginInfo로 저장된 값과 비교하여 옳은경우와 옳지 않은 경우이다.
         */
        checkLoginInfo(jSessionValue);


    }

    public void checkLoginInfo(String jSessionValue) {
        if(!jSessionValue.equals("fail")){
            Log.i(TAG+sequence++," Jsession 값 존재");
            SessionConnection.compareSession(jSessionValue);
            //jSession값에 일치하는 로그인 인포가 있을경우
            if (sessionCheckValue.equals("1")) {
                Log.i(TAG+"조건2", "jsession이 일치함 메인으로 이동");
                SharedPreferences loginInfo = getSharedPreferences("loginInfo", MODE_PRIVATE);
                Log.i(TAG+"세션값",loginInfo.getAll().toString());
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
                finish();
            }
            //jSession에 일치하는 로그인 인포가 없을 경우
            else{
                Log.i(TAG+"조건3", "jsession이 일치하지않음 로그인으로 이동");
                //내 로컬에 저장된 jSession의 값이 서버의 저장된 세션의 인포와 똑같은게 없을때 삭제를 해줘야한다.
                SharedPreferences test = getSharedPreferences("loginInfo", MODE_PRIVATE);
                String isRemovable = test.getString("Set-Cookie","notFound");
                if(!isRemovable.equals("notFound")){
                    SharedPreferences.Editor editor2 = test.edit();
                    editor2.remove("Set-Cookie");
                    editor2.remove("user_id");
                    editor2.commit();
                }
                Intent intent = new Intent(getApplication(), UserLogin.class);
                startActivity(intent);
                finish();
            }
        }
        //jSession이 없는경우
        else{
            Log.i(TAG+"조건4", "jsession이 없음 로그인으로 이동");
            Intent intent = new Intent(getApplication(), UserLogin.class);
            startActivity(intent);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
        }
    }

    public void compareSession(final String jSessionValue){
        Thread compareThread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpURLConnection conn = null;
                OutputStream os;
                BufferedWriter bw;
                InputStream is = null;
                ByteArrayOutputStream baos;

                try {
                    String startURL = Value.userCompareURL;
                    url = new URL(startURL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    //conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestProperty("Cookie", jSessionValue);
/*
                    //요청데이터 전송
                    String queryString="jSessionValue="+jSessionValue;
                    os = conn.getOutputStream();
                    bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bw.write(queryString);
                    bw.flush();
                    bw.close();*/

                    int responseCode = conn.getResponseCode();
                    Log.i("여기까지됨",responseCode+"");
                    switch (responseCode) {
                        case HttpURLConnection.HTTP_OK:
                            is=conn.getInputStream();
                            baos = new ByteArrayOutputStream();
                            byte[] byteBuffer = new byte[1024];
                            byte[] byteData = null;
                            int nLength =0;
                            while((nLength = is.read(byteBuffer,0,byteBuffer.length))!=-1){
                                baos.write(byteBuffer,0,nLength);
                            }
                            byteData=baos.toByteArray();
                            sessionCheckValue = new String(byteData);
                            Log.i(TAG,sessionCheckValue);
                            break;
                        default:
                            Log.i(TAG,"responseCode="+responseCode);
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
        compareThread.start();
        try {
            compareThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
