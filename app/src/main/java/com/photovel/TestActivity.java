package com.photovel;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Junki on 2017-06-21.
 */

public class TestActivity extends Activity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences loginInfo = getSharedPreferences("loginInfo", MODE_PRIVATE);
        String firstData = loginInfo.getString("Set-Cookie", "d");
        Log.i("Set-Cookie",firstData);

    }
}
