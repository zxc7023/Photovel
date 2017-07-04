package com.photovel.user;

import android.app.Activity;
import android.app.Application;

import com.kakao.auth.KakaoSDK;
import com.tsengvn.typekit.Typekit;

/**
 * Created by Junki on 2017-06-28.
 */

public class GlobalApplication extends Application {
    private static volatile GlobalApplication instance = null;
    private static volatile Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "NotoSansCJKkr-Regular.otf"))
                .addBold(Typekit.createFromAsset(this, "NotoSansCJKkr-Bold.otf"));

        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        GlobalApplication.currentActivity = currentActivity;
    }

    /**
     * singleton 애플리케이션 객체를 얻는다.
     *
     * @return singleton 애플리케이션 객체
     */
    public static GlobalApplication getGlobalApplicationContext() {
        if (instance == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        return instance;
    }

    /**
     * 애플리케이션 종료시 singleton 어플리케이션 객체 초기화한다.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }
}