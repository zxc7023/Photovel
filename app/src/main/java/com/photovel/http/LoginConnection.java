package com.photovel.http;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.photovel.IntroMain;
import com.photovel.MainActivity;
import com.photovel.user.UserBitmapEncoding;
import com.vo.User;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Eundi on 2017-07-04.
 */

public class LoginConnection {
    private static final String TAG = "LoginConnection";
    private static String isSuccess;
    private static User user;
    private static String cookieValues = "";

    public static void login(final Context mContext, final String url, final String method, final JSONObject json) {
        Log.i(TAG, "1. login url = " + url +", method = " + method);

        Thread loginThread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                InputStream is = null;
                URL connectURL = null;
                OutputStream dos = null;
                ByteArrayOutputStream baos;

                try {
                    connectURL = new URL(url);
                    conn = (HttpURLConnection) connectURL.openConnection();
                    conn.setRequestMethod(method);
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                    //method가 post면
                    if (method.toUpperCase().equals("POST") && json != null) {
                        Log.i(TAG, "1.1 login json = " + json.toString());
                        conn.setDoOutput(true);
                        dos = conn.getOutputStream();
                        dos.write(json.toString().getBytes());
                        dos.flush();
                    }

                    int responseCode = conn.getResponseCode();
                    Log.i(TAG, "2. login responseCode = " + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        is = conn.getInputStream();
                        baos = new ByteArrayOutputStream();
                        byte[] byteBuffer = new byte[1024];
                        byte[] byteData = null;
                        int nLength = 0;
                        while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                            baos.write(byteBuffer, 0, nLength);
                        }
                        byteData = baos.toByteArray();
                        isSuccess = new String(byteData);
                        if ("".equals(isSuccess) || isSuccess == null) {
                            Log.i(TAG, "3. login 로그인실패");
                            return;
                        }
                        Log.i(TAG, "3. login 로그인성공");

                        user = JSON.parseObject(isSuccess, User.class);
                        Log.i(TAG, "4. login user = " + user);

                        //로그인이 삭제되건 성공하건 이전의 세션은 삭제해줘야한다.
                        SharedPreferences loginInfo = mContext.getSharedPreferences("loginInfo", MODE_PRIVATE);
                        String isRemovable = loginInfo.getString("Set-Cookie", "notFound");
                        if (!"notFound".equals(isRemovable)) {
                            Log.i(TAG, "4.1 login 세션이 있음! -> loginInfo 삭제");
                            SharedPreferences.Editor editor2 = loginInfo.edit();
                            editor2.remove("Set-Cookie");
                            editor2.remove("user_id");
                            editor2.remove("user_nick_name");
                            editor2.remove("user_password");
                            editor2.remove("user_phone");
                            editor2.remove("user_friend_count");
                            editor2.remove("user_profile");
                            editor2.commit();
                        }

                        Map<String, List<String>> responseHeaders = conn.getHeaderFields();
                        Set<String> keys = responseHeaders.keySet();

                        for (String key : keys) {
                            List<String> values = responseHeaders.get(key);
                            if ("Set-Cookie".equals(key)) {
                                for (String value : values) {
                                    cookieValues += value;
                                    cookieValues += ":";
                                }
                            }
                        }

                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        loginThread.start();
        try {
            loginThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "5. login 쓰레드 종료");
        if( "".equals(isSuccess) || isSuccess == null) {
            return ;
        }
            Log.i(TAG, "5. login isSuccess = "+isSuccess);
            //user프로필사진set
            List<User> userList = new ArrayList<>();
            userList.add(user);
            Log.i(TAG, "6. login user프로필사진set 완료");
            JsonConnection.setBitmap(userList, Value.contentPhotoURL);

            Log.i(TAG, "7. login SharedPreferences 객체저장");
            //로그인한 후에 세션을 관리한다. TestActivity에 저장한다.
            SharedPreferences loginInfo = mContext.getSharedPreferences("loginInfo", MODE_PRIVATE);
            SharedPreferences.Editor editor = loginInfo.edit();
            editor.putString("Set-Cookie", cookieValues); //First라는 key값으로 infoFirst 데이터를 저장한다.
            editor.putString("user_id", user.getUser_id());
            editor.putString("user_nick_name", user.getUser_nick_name());
            editor.putString("user_password", user.getUser_password());
            editor.putString("user_phone", user.getUser_phone2());
            editor.putInt("user_friend_count", user.getUser_friend_count());

            UserBitmapEncoding ub = new UserBitmapEncoding();
            if (user.getBitmap() != null) {
                String user_profile = ub.BitMapToString(user.getBitmap());
                editor.putString("user_profile", user_profile);
            }
            editor.commit(); //완료한다.

            SharedPreferences IntroCheck = mContext.getSharedPreferences("IntroCheck", MODE_PRIVATE);
            String check = IntroCheck.getString("IntroCheck","N");
            if(check.equals("Y")){
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }else{
                Intent intent = new Intent(mContext, IntroMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }

    }
}
