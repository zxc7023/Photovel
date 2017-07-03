package com.photovel.user;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.photovel.BackPressCloseHandler;
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
import java.util.List;

import static java.lang.Class.forName;

/**
 * Created by Junki on 2017-06-19.
 */

/**
 * 회원가입전에 이메일형식체크,비밀번호 일치여부, 아이디 중복확인을 합니다.
 */
public class UserJoin extends FontActivity2 {

    String nickName, phoneNumber;
    String isSucess;
    int checkRadioId;
    String gender;
    int country_code;

    Context mContext;
    BackPressCloseHandler backPressCloseHandler;

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_user_join);

        backPressCloseHandler = new BackPressCloseHandler(this, new Intent(getApplicationContext(), UserLogin.class));
        //아이콘
        final TextView nickNameIcon = (TextView) findViewById(R.id.nickNameIcon);
        TextView phoneIcon = (TextView) findViewById(R.id.phoneIcon);
        TextView genderIcon = (TextView) findViewById(R.id.genderIcon);
        TextView globeIconVew = (TextView) findViewById(R.id.phone1Icon);

        //이메일 패스워드를 받기위한 EditText
        final EditText nicknameText = (EditText) findViewById(R.id.nicknameText);
        final EditText phoneText = (EditText) findViewById(R.id.phoneText);
        Spinner spinner = (Spinner) findViewById(R.id.countrySpiner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                country_code = Integer.parseInt(parent.getAdapter().getItem(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(10);
            }
        });
        //가입하기 버튼
        Button signUpButton = (Button) findViewById(R.id.signUpButton);

        //아이콘 설정하기
        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        nickNameIcon.setTypeface(fontAwesomeFont);
        phoneIcon.setTypeface(fontAwesomeFont);
        genderIcon.setTypeface(fontAwesomeFont);
        globeIconVew.setTypeface(fontAwesomeFont);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.genderGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                Toast.makeText(UserJoin.this, "현재 클릭된 id는" + checkedId, Toast.LENGTH_SHORT).show();
                checkRadioId = checkedId;
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRadioId == 0) {
                    gender = "M";
                } else {
                    RadioButton radioButton = (RadioButton) findViewById(checkRadioId);
                    if (radioButton.getText().equals("Male")) {
                        gender = "M";
                    } else {
                        gender = "F";
                    }
                }

                nickName = nicknameText.getText().toString();
                phoneNumber = phoneText.getText().toString();

                if ("".equals(nickName) || "".equals(phoneNumber)) {
                    Toast.makeText(UserJoin.this, "정보를 다 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent intent = getIntent();
                    User user;
                    JSONObject job = new JSONObject();
                    user = (User) intent.getSerializableExtra("user");
                    try {
                        job.put("user_id", user.getUser_id());
                        job.put("user_password", user.getUser_password());
                        job.put("user_nick_name", nickName);
                        job.put("user_phone2", phoneNumber);
                        job.put("user_gender", gender);
                        job.put("user_phone1", country_code);
                        job.put("user_sns_status", "O");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("myConsole", job.toString());

                    String url = Value.userJoinURL;
                    join(job.toString(), url);
                    if (isSucess.equals("1")) {
                        Log.i("isSucess", "로그인페이지로 이동");
                        Intent intent1 = new Intent(getApplicationContext(), UserLogin.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent1);
                        finish();
                    }
                }
            }
        });
    }

    public void join(final String json, final String url) {
        Log.i("myConsole_idChek", "methodStart");
        Log.i("jsonTest", json.toString());
        Thread joinThread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                InputStream is = null;
                OutputStream dos = null;
                ByteArrayOutputStream baos;

                try {
                    //송신준비 및 송신
                    URL connectURL = new URL(url);
                    conn = (HttpURLConnection) connectURL.openConnection();
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
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
                            Log.i("isSucess", isSucess);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.i("UserJoin's error", e.getMessage());
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        joinThread.start();
        try {
            joinThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
