package com.photovel;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by HARA on 2017-07-03.
 */

public class IntroMain extends FontActivity2{

    protected ViewPager viewpager;
    private IntroViewPagerAdapter mAdapter;

    private int[] mImageResources = {
            R.drawable.intro_1,
            R.drawable.intro_2,
            R.drawable.intro_3,
            R.drawable.intro_4
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_main);

        viewpager = (ViewPager)findViewById(R.id.pager_introduction);
        CircleIndicator indicator = (CircleIndicator)findViewById(R.id.indicator);
        mAdapter = new IntroViewPagerAdapter(IntroMain.this, mImageResources);
        viewpager.setAdapter(mAdapter);
        indicator.setViewPager(viewpager);
    }
}
