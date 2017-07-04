package com.photovel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.photovel.common.BookMarkMain;
import com.photovel.content.ContentListMain;
import com.photovel.friend.FriendListMain;
import com.photovel.http.Value;
import com.photovel.setting.SettingMain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by HARA on 2017-06-26.
 */

public class NavigationItemSelected extends FontActivity{
    public void selected(int id, Context context){
        Intent intent;
        SharedPreferences get_to_eat = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
        String user_id = get_to_eat.getString("user_id","notFound");

        if(id == R.id.nav_home){
            intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            finish();
        }else if(id==R.id.nav_my_story){
            intent = new Intent(context, ContentListMain.class);
            intent.putExtra("urlflag","M");
            intent.putExtra("user_id",user_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            finish();
        }else if(id==R.id.nav_my_friend){
            intent = new Intent(context, FriendListMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            finish();
        }else if(id==R.id.nav_book_mark){
            intent = new Intent(context, BookMarkMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            finish();
        }else if(id==R.id.nav_new_story){
            intent = new Intent(context, ContentListMain.class);
            intent.putExtra("urlflag","N");
            intent.putExtra("user_id","");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            finish();
        }else if(id==R.id.nav_hot_story){
            intent = new Intent(context, ContentListMain.class);
            intent.putExtra("urlflag","R");
            intent.putExtra("user_id","");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            finish();
        }else if(id==R.id.nav_log_out){
            Toast.makeText(context, "로그아웃", Toast.LENGTH_SHORT).show();
            logout(Value.userLogoutURL, context);
            kakaoLogout(context);
            facebookLogout();
            intent = new Intent(context, SessionMangement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            finish();
        }else if(id==R.id.nav_setting){
            intent = new Intent(context, SettingMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            finish();
        }else if(id==R.id.nav_qna){
            Toast.makeText(context,"아직 개발중입니닷",Toast.LENGTH_SHORT).show();
        }else if(id==R.id.nav_howto){
            Toast.makeText(context,"아직 개발중입니닷",Toast.LENGTH_SHORT).show();
        }
    }

    private void kakaoLogout(final Context context) {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Intent intent = new Intent(context,SessionMangement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void facebookLogout() {
        LoginManager.getInstance().logOut();
    }

    public void logout(final String userLogoutURL, final Context context){
        Thread logoutThread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                InputStream is = null;
                URL connectURL = null;
                OutputStream dos= null;
                ByteArrayOutputStream baos;

                try {
                    connectURL = new URL(userLogoutURL);
                    conn = (HttpURLConnection) connectURL.openConnection();
                    conn.setDoInput(true);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                    int responseCode = conn.getResponseCode();
                    Log.i("myResponseCode", responseCode + "");
                    switch (responseCode){
                        case HttpURLConnection.HTTP_OK :
                            //로그아웃이되면 공유객체의 jSession 삭제
                            SharedPreferences test = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
                            String isRemovable = test.getString("Set-Cookie","notFound");
                            if(!isRemovable.equals("notFound")){
                                SharedPreferences.Editor editor2 = test.edit();
                                editor2.remove("user_id");
                                editor2.remove("user_nick_name");
                                editor2.remove("user_password");
                                editor2.remove("user_phone");
                                editor2.remove("user_friend_count");
                                editor2.remove("user_profile");
                                editor2.remove("Set-Cookie");
                                editor2.commit();
                            }
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

        logoutThread.start();
        try {
            logoutThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
