package com.photovel;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

public class FontApp extends Application {
    @Override public void onCreate() {
        super.onCreate();
        // 폰트 정의
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "NotoSansCJKkr-Regular.otf"))
                .addBold(Typekit.createFromAsset(this, "NotoSansCJKkr-Bold.otf"));
    }
}
