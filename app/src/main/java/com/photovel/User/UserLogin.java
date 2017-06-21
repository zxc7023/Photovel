package com.photovel.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.photovel.R;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.photovel.FontActivity2;

import com.photovel.TestActivity;

import org.json.JSONException;
import org.json.JSONObject;

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

        emailText = (EditText) findViewById(R.id.emailText);
        passwordTextView = (EditText) findViewById(R.id.passwordText);


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
                    //final String url = "http://192.128.12.44:8888/photovel/common/login/email";
                    String url ="http://192.168.12.44:8888/photovel/common/login/email";
                    try {
                        job.put("user_id", user_id);
                        job.put("user_password", user_password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("myJsonString", job.toString());
                    login(job.toString(),url);
                    //Log.i("myResult",result);
                    if(isSucess.equals("1")){
                        Toast.makeText(mContext, "로그인성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                        startActivity(intent);

                    }else{
                        Toast.makeText(mContext, "로그인실패", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        TextView joinTextView = (TextView) findViewById(R.id.joinTextView);
        joinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UserJoin.class);
                startActivity(intent);
            }
        });


    }

    public void login(final String json, final String url){
        Thread loginThread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                InputStream is = null;
                URL connectURL = null;
                OutputStream dos= null;
                ByteArrayOutputStream baos;


                try {
                    connectURL = new URL(url);
                    conn = (HttpURLConnection) connectURL.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("GET");
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
                            Log.i("isSucess",isSucess);

                            //로그인이 삭제되건 성공하건 이전의 세션은 삭제해줘야한다.
                            SharedPreferences test = getSharedPreferences("loginInfo", MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = test.edit();
                            editor2.remove("Set-Cookie");
                            editor2.commit();

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
                            editor.commit(); //완료한다.

                            break;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        dos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
