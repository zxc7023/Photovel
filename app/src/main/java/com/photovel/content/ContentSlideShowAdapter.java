package com.photovel.content;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.photovel.R;

import java.util.ArrayList;

/**
 * Created by daybreak on 2017-06-30.
 */

public class ContentSlideShowAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Bitmap> imgArray;
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public ContentSlideShowAdapter(Context context, ArrayList<Bitmap> imgs) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imgArray = imgs;
    }

    @Override
    public int getCount() {
        return imgArray.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //뷰 객체 만들기
        View imgLayout = layoutInflater.inflate(R.layout.slide_images_layout, container, false);

        ImageView imageView = (ImageView) imgLayout.findViewById(R.id.slide_images_view);
        //해당 위치의 이미지를 찾아서 이미지 붙이기
        imageView.setImageBitmap(imgArray.get(position));
        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), imgArray.get(position));
        bitmapDrawable.setAlpha(100);
        imageView.setBackground(bitmapDrawable);

        //이미지 뷰를 포함하는 레이아웃을 뷰 그룹에 붙임
        container.addView(imgLayout);
        //레이아웃 반환
        return imgLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

    public Fragment getRegisteredFragments(int position) {
        return registeredFragments.get(position);
    }

}
