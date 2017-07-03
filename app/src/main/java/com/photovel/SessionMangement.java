package com.photovel;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.internal.pr;
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
    String TAG ="junkiSession";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        // 저장된 jSession 쿠키값을 받아온다
        SharedPreferences loginInfo = getSharedPreferences("loginInfo", MODE_PRIVATE);
        String firstData = loginInfo.getString("Set-Cookie", "fail");
        Log.i(TAG,firstData);


        /**
         * loginInfo라는 객체속에 "Set-Cookie"로 저장해둔 jSession값에 따라 해당하는 결과를 수행한다.
         */
        checkLoginInfo(firstData);


    }

    public void checkLoginInfo(String firstData) {
        //jSession값이 있는경우
        if(!firstData.equals("fail")){
            compareSession(firstData);
            //jSession값에 일치하는 로그인 인포가 있을경우
            if (sessionCheckValue.equals("1")) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
                finish();
            }
            //jSession에 일치하는 로그인 인포가 없을 경우
            else{
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
            Intent intent = new Intent(getApplication(), UserLogin.class);
            startActivity(intent);
            finish();
        }
    }

    public void compareSession(final String jSessionValue){
        final int returnValue=0;
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

                    final int responseCode = conn.getResponseCode();
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



/*

    public void getSession(final String url){
        Thread loginThread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                InputStream is = null;
                URL connectURL = null;
                ByteArrayOutputStream baos;


                try {
                    Log.i("myStatus",url);
                    connectURL = new URL(url);
                    conn = (HttpURLConnection) connectURL.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("Post");
                    conn.setRequestProperty("Connection", "Keep-Alive");


                    int responseCode = conn.getResponseCode();
                    Log.i("myStatus", responseCode + "");
                    switch (responseCode){
                        case HttpURLConnection.HTTP_OK :
                            is=conn.getInputStream();
                            baos = new ByteArrayOutputStream();
                            byte[] byteBuffer = new byte[1024];
                            byte[] byteData = null;
                            int nLength =0;
                            while((nLength = is.read(byteBuffer,0,byteBuffer.length))!=-1){
                                baos.write(byteBuffer,0,nLength);
                            }
                            byteData=baos.toByteArray();
                            isSucess = new String(byteData);
                            Log.i("myStatus",isSucess);

                            //로그인이 삭제되건 성공하건 이전의 세션은 삭제해줘야한다.
                            SharedPreferences test = getSharedPreferences("loginInfo", MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = test.edit();
                            editor2.remove("Set-Cookie");
                            editor2.commit();

                            Map<String,List<String>> responseHeaders = conn.getHeaderFields();
                            Set<String> keys = responseHeaders.keySet();
                            Log.i("myStatus","응답헤더목록");
                            for(String key: keys){
                                List<String>values = responseHeaders.get(key);
                                Log.i("myStatus",key+"="+values.toString());
                                if("Set-Cookie".equals(key)){
                                    for(String value:values){
                                        cookieValues +=value;
                                        cookieValues+=":";
                                    }
                                }
                            }

                            //로그인한 후에 세션을 관리한다. TestActivity에 저장한다.
                            SharedPreferences loginInfo = getSharedPreferences("loginInfo", MODE_PRIVATE);
                            SharedPreferences.Editor editor = loginInfo.edit();
                            editor.putString("Set-Cookie", cookieValues); //First라는 key값으로 infoFirst 데이터를 저장한다.
                            editor.commit(); //완료한다.
                            break;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                }
            }
        });

        loginThread.start();
        try {
            loginThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
*/

}
