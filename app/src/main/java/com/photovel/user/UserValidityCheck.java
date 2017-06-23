package com.photovel.user;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.photovel.FontActivity2;
import com.photovel.R;
import com.photovel.http.Value;
import com.vo.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Junki on 2017-06-19.
 */

/**
 * 회원가입전에 이메일형식체크,비밀번호 일치여부, 아이디 중복확인을 합니다.
 */
public class UserValidityCheck extends FontActivity2 {

    String email,password,passwordCheck;
    String isSucess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_validity_check);

        //아이콘 텍스트
        final TextView emailIconView = (TextView)findViewById(R.id.emailIcon);
        final TextView passIconView = (TextView)findViewById(R.id.passwordIcon);
        final TextView passIconView2 = (TextView)findViewById(R.id.passwordIcon2);


        //이메일 패스워드를 받기위한 EditText
        final EditText emailText = (EditText) findViewById(R.id.emailText);
        final EditText passwordTextView = (EditText) findViewById(R.id.passwordText);
        final EditText passwordTextView2 = (EditText) findViewById(R.id.passwordText2);

        //유효성검사버튼
        Button validityButton = (Button)findViewById(R.id.validityButton);

        //아이콘 설정하기
        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        emailIconView.setTypeface(fontAwesomeFont);
        passIconView.setTypeface(fontAwesomeFont);
        passIconView2.setTypeface(fontAwesomeFont);

        validityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailText.getText().toString();
                password = passwordTextView.getText().toString();
                passwordCheck= passwordTextView2.getText().toString();

                if("".equals(email) || "".equals(password)) {
                    Toast.makeText(UserValidityCheck.this, "정보를 다 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(!isValidEmail(email)){
                    Toast.makeText(UserValidityCheck.this, "이메일을 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if(!password.equals(passwordCheck)){
                    Toast.makeText(UserValidityCheck.this, "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
                } else{
                    Log.i("myConsole_getIdPassword",email+","+password);//아이디 비밀번호 로그확인

                    //id 중복체크
                    JSONObject job = new JSONObject();
                    //final String url = "http://192.128.12.44:8888/photovel/common/login/email";
                    String url = Value.userValidityCheckURL;
                    try {
                        job.put("user_id", email);
                        job.put("user_password", password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("myConsole_getJsonString", job.toString());
                    idCheck(job.toString(),url);

                    if(("0").equals(isSucess)){
                        Toast.makeText(UserValidityCheck.this, "중복된 아이디 입니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent = new Intent(getApplicationContext(),UserJoin.class);
                        User user = new User();
                        user.setUser_id(email);
                        user.setUser_password(password);
                        intent.putExtra("user",user);
                        startActivity(intent);

                    }

                    //중복체크가 되면 다음화면에서 다른정보도 받음
                }


            }
        });
    }
    public void idCheck(final String json, final String url){
        Log.i("myConsole_idChek","methodStart");
        Thread idCheckThread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                InputStream is = null;
                URL connectURL = null;
                OutputStream dos= null;
                ByteArrayOutputStream baos;

                try {
                    //송신준비 및 송신
                    connectURL = new URL(url);
                    conn = (HttpURLConnection) connectURL.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    dos = conn.getOutputStream();
                    dos.write(json.getBytes());
                    dos.flush();


                    //수신측면
                    int responseCode = conn.getResponseCode();
                    Log.i("myResponseCode", responseCode + "");
                    switch (responseCode) {
                        case HttpURLConnection.HTTP_OK:
                            is = conn.getInputStream();
                            baos = new ByteArrayOutputStream();
                            byte[] byteBuffer = new byte[1024];
                            byte[] byteData = null;
                            int nLength = 0;
                            while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                                baos.write(byteBuffer, 0, nLength);
                            }
                            byteData = baos.toByteArray();
                            isSucess = new String(byteData);
                            Log.i("isSucess",isSucess);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        dos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    conn.disconnect();
                }

            }
        });
        idCheckThread.start();
        try {
            idCheckThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public  boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
