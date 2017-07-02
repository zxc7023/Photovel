package com.photovel.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.photovel.MainActivity;
import com.photovel.http.JsonConnection;
import com.vo.User;

import java.util.HashMap;


/**
 * Created by Junki on 2017-06-28.
 */

public class KakaoSignupActivity extends Activity {

    //로그를 확인하기위하여 작성한 TAG와 Sequence
    String TAG = "KakaoSignUpTest";
    int sequence =0;

    //KakaoSignupActivity 액티비티에서 KakaoUserJoinDetail로 전달해주고 받아올때 필요한 requestCode
    int KakaoSuccessCode = 777;


    /**
     * Main으로 넘길지 가입 페이지를 그릴지 판단하기 위해 me를 호출한다.
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMe();
    }

    private void requestSignUp() {
        HashMap<String,String> properties = new HashMap<String,String>();
        UserManagement.requestSignup(new ApiResponseCallback<Long>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.i(TAG+""+sequence++,"requestSignUp onSessionClosed()");
            }

            @Override
            public void onNotSignedUp() {
                Log.i(TAG+""+sequence++,"requestSignUp onNotSignedUp()");
            }

            @Override
            public void onSuccess(Long result) {
                Log.i(TAG+""+sequence++,"requestSignUp onSuccess()");
                Log.i(TAG+""+sequence++,result.toString());
            }
        },properties);
    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    protected void requestMe() { //유저의 정보를 받아오는 함수
        UserManagement.requestMe(new MeResponseCallback() {

            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Log.i("message",message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.i(TAG+""+sequence++,"onSessionClosed");
                Log.i("error",errorResult.toString());
                redirectLoginActivity();

            }

            @Override
            public void onNotSignedUp() {
                Log.i(TAG+""+sequence++,"onNotSignUP");
                Intent intent = new Intent(getApplicationContext(),KakaoUserJoinDetail.class);
                startActivityForResult(intent,KakaoSuccessCode);
            } // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {  //성공 시 userProfile 형태로 반환
                Log.i(TAG+""+sequence++,"onSuccess");
                Log.i("UserProfile : " ,userProfile.toString());
                Log.i(TAG,userProfile.getEmail());
                Log.i(TAG,userProfile.getProfileImagePath());
                Log.i(TAG,userProfile.getNickname());

                User sendUser = new User();
                sendUser.setUser_id(userProfile.getEmail());
                sendUser.setUser_nick_name(userProfile.getNickname());
                sendUser.setUser_profile_photo(userProfile.getProfileImagePath());
                //Intent값 만들어놓기
               /* sendIntent = new Intent(getApplicationContext(),KakaoUserJoinDetail.class);
                sendIntent.putExtra("user",sendUser);*/

                Log.i(TAG+""+sequence++,"onSignUpFinish");
               redirectMainActivity(); // 로그인 성공시 MainActivity로
            }
        }/*,propertyKeys,false*/);
    }
    private void redirectMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }
    protected void redirectLoginActivity() {
        Log.i(TAG+""+sequence++,"redirectLoginActivity");
        Intent intent = new Intent(this, UserLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    public void checkKakaoUserDetailInfo(){
        Log.i(TAG+""+sequence++,"checkKakaoUserDetailInfo");
        JsonConnection connection = new JsonConnection();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG+""+sequence++ +"ResultCode",resultCode+"");
        switch (resultCode){
            case 777:
                User user = (User)data.getSerializableExtra("user");
                user.toString();
                requestSignUp();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
        //redirectLoginActivity();
    }

}