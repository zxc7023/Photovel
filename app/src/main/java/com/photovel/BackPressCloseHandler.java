package com.photovel;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Junki on 2017-06-23.
 */

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;
    Intent intent;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public BackPressCloseHandler(Activity mContext, Intent intent) {
        this.activity =mContext;
        this.intent=intent;

    }


    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            if(intent!=null){
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
            }

            activity.finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}