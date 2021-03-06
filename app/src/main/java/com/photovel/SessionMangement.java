package com.photovel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.photovel.http.SessionConnection;
import com.photovel.user.UserLogin;


/**
 * Created by Junki on 2017-06-23.
 */

public class SessionMangement extends FontActivity2 {

    String cookieValues;
    String sessionCheckValue="0";
    String TAG ="SessionManageTest";
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
        Log.i(TAG+sequence+++"세션값:",jSessionValue);


        /**
         * loginInfo라는 객체속에 "Set-Cookie"로 저장해둔 jSession값에 따라 해당하는 결과를 수행한다.
         * jSession이 있는경우는 두가지의 경우로 나누어 지는데,
         * 서버의 loginInfo로 저장된 값과 비교하여 옳은경우와 옳지 않은 경우이다.
         */
        checkLoginInfo(jSessionValue);


    }

    public void checkLoginInfo(final String jSessionValue) {
        if(!jSessionValue.equals("fail")){
            Log.i(TAG+sequence++," Jsession 값 존재");
            //compareSession(jSessionValue);
            Thread compareSession = new Thread(new Runnable() {
                @Override
                public void run() {
                    sessionCheckValue = SessionConnection.compareSession(jSessionValue);
                }
            });
            compareSession.start();
            try {
                compareSession.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i(TAG+sequence ++ +"받은값",sessionCheckValue);

            //SessionConnection.compareSession(jSessionValue);

            //jSession값에 일치하는 로그인 인포가 있을경우
            if (sessionCheckValue.equals("1")) {
                Log.i(TAG+sequence ++ +"조건2", "jsession이 일치함 메인으로 이동");
                SharedPreferences loginInfo = getSharedPreferences("loginInfo", MODE_PRIVATE);
                Log.i(TAG+sequence ++ +"세션값",loginInfo.getAll().toString());

                SharedPreferences IntroCheck = getSharedPreferences("IntroCheck", MODE_PRIVATE);
                String check = IntroCheck.getString("IntroCheck","N");
                if(check.equals("Y")){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getApplicationContext(), IntroMain.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
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
                    editor2.clear();
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
}
