package com.photovel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by HARA on 2017-07-03.
 */

public class IntroViewPagerAdapter extends PagerAdapter{

    private Context mContext;
    private int[] mResources;
    private ImageView imageView;
    private TextView tvStart;
    private CheckBox checkNonSee;

    public IntroViewPagerAdapter(Context mContext, int[] mResources) {
        this.mContext = mContext;
        this.mResources = mResources;
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.pager_item, container, false);

        imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);
        tvStart = (TextView) itemView.findViewById(R.id.tvStart);
        checkNonSee = (CheckBox) itemView.findViewById(R.id.checkNonSee);

        if (position == (getCount()-1)){
            tvStart.setVisibility(View.VISIBLE);
            checkNonSee.setVisibility(View.VISIBLE);
        }
        imageView.setImageResource(mResources[position]);
        container.addView(itemView);

        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkNonSee.isChecked() == true){
                    SharedPreferences check = mContext.getSharedPreferences("IntroCheck", mContext.MODE_PRIVATE);
                    SharedPreferences.Editor editor = check.edit();
                    editor.putString("IntroCheck", "Y");
                    editor.commit();
                }
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
                ((Activity)mContext).finish();
            }
        });
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
