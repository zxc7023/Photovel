package com.photovel.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.photovel.IntroMain;
import com.photovel.MainActivity;
import com.photovel.R;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.photovel.FontActivity2;

import com.photovel.http.JsonConnection;
import com.photovel.http.Value;
import com.vo.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junki on 2017-06-19.
 */

public class UserLogin extends FontActivity2 {
    //LOG값을 순서대로 찍어보기위해서 사용
    String TAG = "UserLoginTest";
    int sequence = 0;

    EditText emailText;
    EditText passwordTextView;
    String user_id;
    String user_password;
    User temp;

    String isSucess;
    String cookieValues = "";

    private SessionCallback callback;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    JSONObject job;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        emailText = (EditText) findViewById(R.id.emailText);
        passwordTextView = (EditText) findViewById(R.id.passwordText);

        TextView emailIconView = (TextView) findViewById(R.id.emailIcon);
        TextView passIconView = (TextView) findViewById(R.id.passwordIcon);

        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        emailIconView.setTypeface(fontAwesomeFont);
        passIconView.setTypeface(fontAwesomeFont);

        //카카오로그인
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();

        //페이스북 세션이 있으면서, 로그아웃이 안되면
        accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken !=null && !accessToken.isExpired()){
            Log.i("ddd", "id"+accessToken.getUserId());

            Thread loginThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    isSucess = JsonConnection.getConnection(Value.userLoginWithFBURL+"/"+accessToken.getUserId(), "GET", null);
                }
            });
            loginThread.start();
            try {
                loginThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setSharedPreferences();
        }

        //회원가입
        TextView joinTextView = (TextView) findViewById(R.id.joinTextView);
        joinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserValidityCheck.class);
                startActivity(intent);
            }
        });


        //일반로그인
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = emailText.getText().toString();
                user_password = passwordTextView.getText().toString();

                if (("").equals(user_id) || ("").equals(user_password)) {
                    Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    job = new JSONObject();
                    try {
                        job.put("user_id", user_id);
                        job.put("user_password", user_password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Thread loginThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            isSucess = JsonConnection.getConnection(Value.userLoginURL, "POST", job);
                        }
                    });
                    loginThread.start();
                    try {
                        loginThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setSharedPreferences();

                }
            }
        });

    }

    private void setSharedPreferences() {
        //로그인이 삭제되건 성공하건 이전의 세션은 삭제해줘야한다.
        SharedPreferences loginInfo = getSharedPreferences("loginInfo", MODE_PRIVATE);
        String isRemovable = loginInfo.getString("Set-Cookie", "notFound");
        if (!isRemovable.equals("notFound")) {
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

        if ("".equals(isSucess) || isSucess == null) {
            Toast.makeText(getApplicationContext(), "로그인실패", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "로그인성공", Toast.LENGTH_SHORT).show();

            temp = JSON.parseObject(isSucess, User.class);

            //user프로필사진set
            List<User> user = new ArrayList<>();
            user.add(temp);
            JsonConnection.setBitmap(user, Value.contentPhotoURL);

            //로그인한 후에 세션을 관리한다. TestActivity에 저장한다.
            SharedPreferences.Editor editor = loginInfo.edit();
            editor.putString("Set-Cookie", cookieValues); //First라는 key값으로 infoFirst 데이터를 저장한다.
            editor.putString("user_id", temp.getUser_id());
            editor.putString("user_nick_name", temp.getUser_nick_name());
            editor.putString("user_password", temp.getUser_password());
            editor.putString("user_phone", temp.getUser_phone2());
            editor.putInt("user_friend_count", temp.getUser_friend_count());

            UserBitmapEncoding ub = new UserBitmapEncoding();
            if (temp.getBitmap() != null) {
                String user_profile = ub.BitMapToString(temp.getBitmap());
                editor.putString("user_profile", user_profile);
            }

            editor.commit(); //완료한다.

            SharedPreferences IntroCheck = getSharedPreferences("IntroCheck", MODE_PRIVATE);
            String check = IntroCheck.getString("IntroCheck","N");
            if(check.equals("Y")){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(getApplicationContext(), IntroMain.class);
                startActivity(intent);
                finish();
            }
        }
    }

    //페이스북

    LoginButton facebook_login_button;
    User user;
    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getApplication());
        callbackManager=CallbackManager.Factory.create();

        facebook_login_button= (LoginButton)findViewById(R.id.facebook_login_button);
        facebook_login_button.setClickable(false);
        facebook_login_button.setReadPermissions("public_profile", "email");

        RelativeLayout btnLogin= (RelativeLayout) findViewById(R.id.btnLogin);
        btnLogin.bringToFront();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebook_login_button.performClick();
                facebook_login_button.setPressed(true);
                facebook_login_button.invalidate();
                facebook_login_button.registerCallback(callbackManager, mCallBack);
                facebook_login_button.setPressed(false);
                facebook_login_button.invalidate();
            }
        });
    }

    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            // App code
            GraphRequest request = GraphRequest.newMeRequest( loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            Log.e("response: ", response + "");
                            try {
                                final String user_sns_token = object.getString("id");
                                Thread loginThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        isSucess = JsonConnection.getConnection(Value.userLoginWithFBURL+"/"+user_sns_token, "GET", null);
                                    }
                                });
                                loginThread.start();
                                try {
                                    loginThread.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(isSucess != null) {
                                setSharedPreferences();
                            }else{
                                Profile profile = Profile.getCurrentProfile();
                                Log.i("ddd", "profile :"+profile.getProfilePictureUri(50,50));
                                try {
                                    user = new User();
                                    user.setUser_id(object.getString("email"));
                                    user.setUser_sns_token(object.getString("id"));
                                    user.setUser_password(object.getString("id"));
                                    user.setUser_nick_name(object.getString("name"));
                                    user.setUser_gender(object.getString("gender"));
                                    user.setUser_profile_photo(profile.getProfilePictureUri(50,50).toString());
                                    user.setUser_sns_status("F");
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                Toast.makeText(UserLogin.this, "welcome " + user.getUser_id(), Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(UserLogin.this, UserJoin.class);
                                intent.putExtra("user", user);
                                Log.i("ddd1", user.toString());
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            Log.i("ddd", "cancel되뮤ㅠ");
        }

        @Override
        public void onError(FacebookException e) {
            Log.i("ddd", "error나뮤ㅠ ");
            e.printStackTrace();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
        //accessTokenTracker.stopTracking();
    }

    //카카오톡 로그인
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            Log.i(TAG + "" + sequence++, "onSessionOpened");
            redirectSignupActivity();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.i(TAG + "" + sequence++, "onSessionOpenFailed");
            if (exception != null) {
                Log.i("onSessionOpenFailed", exception.toString());
            }
        }
    }

    protected void redirectSignupActivity() {
        Log.i(TAG + "" + sequence++, "redirectSignupActivity");
        Intent intent = new Intent(this, KakaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

}
