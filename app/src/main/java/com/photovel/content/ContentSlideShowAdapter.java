package com.photovel.content;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.photovel.R;

import java.util.ArrayList;

/**
 * Created by daybreak on 2017-06-28.
 */

public class ContentSlideShowAdapter extends PagerAdapter {
    private static final String TAG = "ContentSlideShowAdapter";
    private ArrayList<Bitmap> images;
    private LayoutInflater layoutInflater;
    private Context context;

    //각 슬라이드 이미지용 이미지 뷰 객체

    public ContentSlideShowAdapter(ArrayList<Bitmap> images, Context context) {
        this.images = images;
        this.context = context;
        //레이아웃 인플레이터 생성
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return images.size();
    }



    @Override
    public boolean isViewFromObject(View view, Object object) {
        //return false;
        return view.equals(object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //각 슬라이드 이미지용 뷰 객체 인플레이트
        View slideImageLayout = layoutInflater.inflate(R.layout.slide_images_layout, container, false);
        //assert slideImageLayout != null;
        final ImageView imageView = (ImageView) slideImageLayout.findViewById(R.id.slide_images_view);
        if(imageView != null){
            imageView.setImageBitmap(images.get(position));
            Log.i(TAG, "instantiateItem의 bitmap= " + images.get(position).toString());
            //뷰 그룹에 자식 뷰 추가
            container.addView(imageView);
        }

        return slideImageLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Parcelable saveState() {
//        return super.saveState();
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);
    }
}
