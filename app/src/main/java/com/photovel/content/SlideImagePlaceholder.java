package com.photovel.content;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.photovel.R;

import java.util.ArrayList;

/**
 * Created by daybreak on 2017-06-30.
 */

public class SlideImagePlaceholder extends Fragment {

    private ViewPager vpSlideShow;
    private Context mContext;
    private ArrayList<Bitmap> images = new ArrayList<>();
    private static Bitmap[] imgArray;
    private SeekBar slideSeekBar;

    private static final String KEY_SELECTED_PAGE = "KEY_SELECTED_PAGE";
    private static int currPage = 0;
    private static int MAX_PAGES = 0;
    private int maxOffset;
    private int currOffset;
    int imageViewWidth;
    public static final String EXTRA_POSITION = "EXTRA_POSITION";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final int position = getArguments().getInt(EXTRA_POSITION);
        //이미지 뷰만 있는 레이아웃으로 이미지 뷰 반환
        ImageView slideImageView = (ImageView) inflater.inflate(R.layout.slide_images_layout, container, false);
        slideImageView.setImageBitmap(imgArray[position - 1]);
        return slideImageView;
    }
}
