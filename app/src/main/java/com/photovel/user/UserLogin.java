package com.photovel.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.photovel.MainActivity;
import com.photovel.R;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.photovel.FontActivity2;

import com.photovel.TestActivity;
import com.photovel.http.Value;
import com.vo.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Junki on 2017-06-19.
 */

public class UserLogin extends FontActivity2 {

    Context mContext;
    EditText emailText;
    EditText passwordTextView;
    String user_id;
    String user_password;


    String isSucess;
    String cookieValues = "";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        mContext = this;

        //FacebookLogin 배경색을 java로 동적으로 함
        LinearLayout facebookLayout = (LinearLayout) findViewById(R.id.FacebookLoginLayout);
        GradientDrawable bgShape = (GradientDrawable)facebookLayout.getBackground();
        int facebookColor = ContextCompat.getColor(this,R.color.facebookColor);
        bgShape.setColor(facebookColor);

        //KAKAO 배경색을 java로 동적으로 함
        LinearLayout kakaoLayout = (LinearLayout) findViewById(R.id.kakaoLoginLayout);
        GradientDrawable bgShape2 = (GradientDrawable)kakaoLayout.getBackground();
        int kakaoColor = ContextCompat.getColor(this,R.color.kakaoColor);
        bgShape2.setColor(kakaoColor);

        TextView emailIconView = (TextView)findViewById(R.id.emailIcon);
        TextView passIconView = (TextView)findViewById(R.id.passwordIcon);
        TextView facebookView = (TextView)findViewById(R.id.fa_facebook_official);
        TextView kakaoView = (TextView)findViewById(R.id.fa_commenting);


        emailText = (EditText) findViewById(R.id.emailText);
        passwordTextView = (EditText) findViewById(R.id.passwordText);


        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        emailIconView.setTypeface(fontAwesomeFont);
        passIconView.setTypeface(fontAwesomeFont);
        facebookView.setTypeface(fontAwesomeFont);
        kakaoView.setTypeface(fontAwesomeFont);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = emailText.getText().toString();
                user_password = passwordTextView.getText().toString();

                if (("").equals(user_id) || ("").equals(user_password) ) {
                    Toast.makeText(mContext, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject job = new JSONObject();
                    String url =Value.userLoginURL;
                    try {
                        job.put("user_id", user_id);
                        job.put("user_password", user_password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("myJsonString", job.toString());
                    login(job.toString(),url);
                    if("".equals(isSucess)){
                        Toast.makeText(mContext, "로그인실패", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mContext, "로그인성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }
        });

        TextView joinTextView = (TextView) findViewById(R.id.joinTextView);
        joinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UserValidityCheck.class);
                startActivity(intent);
            }
        });
    }

    public void login(final String json, final String url){
        Thread loginThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("please",json+"\n"+url);
                HttpURLConnection conn = null;
                InputStream is = null;
                URL connectURL = null;
                OutputStream dos= null;
                ByteArrayOutputStream baos;


                try {
                    connectURL = new URL(url);
                    conn = (HttpURLConnection) connectURL.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    dos = conn.getOutputStream();
                    dos.write(json.getBytes());
                    dos.flush();


                    int responseCode = conn.getResponseCode();
                    Log.i("myResponseCode", responseCode + "");
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
                            if(isSucess.equals("")){
                                Log.i("ddd","로그인실패");
                                break;
                            }
                            User temp = JSON.parseObject(isSucess, User.class);
                            Log.i("temp","temp : "+temp.toString());

                            //로그인이 삭제되건 성공하건 이전의 세션은 삭제해줘야한다.
                            SharedPreferences test = getSharedPreferences("loginInfo", MODE_PRIVATE);
                            String isRemovable = test.getString("Set-Cookie","notFound");
                            if(!isRemovable.equals("notFound")){
                                SharedPreferences.Editor editor2 = test.edit();
                                editor2.remove("Set-Cookie");
                                editor2.remove("user_id");
                                editor2.remove("user_nick_name");
                                editor2.remove("user_password");
                                editor2.remove("user_phone");
                                editor2.remove("user_friend_count");
                                editor2.remove("user_profile");
                                editor2.commit();
                            }


                            Map<String,List<String>> responseHeaders = conn.getHeaderFields();
                            Set<String> keys = responseHeaders.keySet();
                            Log.i("HttpNetwork","응답헤더목록");
                            for(String key: keys){
                                List<String>values = responseHeaders.get(key);
                                Log.i("HttpNetwork",key+"="+values.toString());
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
                            editor.putString("user_id",user_id);
                            editor.putString("user_nick_name",temp.getUser_nick_name());
                            editor.putString("user_password",temp.getUser_password());
                            editor.putString("user_phone",temp.getUser_phone2());
                            editor.putInt("user_friend_count",temp.getUser_friend_count());

                            UserBitmapEncoding ub = new UserBitmapEncoding();
                            if(temp.getBitmap() != null){
                                String user_profile = ub.BitMapToString(temp.getBitmap());
                                editor.putString("user_profile",user_profile);
                            }

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
}
