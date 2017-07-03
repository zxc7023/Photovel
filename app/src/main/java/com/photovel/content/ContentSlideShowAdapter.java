package com.photovel.content;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.photovel.R;
/**
 * Created by daybreak on 2017-06-30.
 */

public class ContentSlideShowAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private Bitmap[] imgArray;

    public ContentSlideShowAdapter(Context context, Bitmap[] imgs) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imgArray = imgs;
    }

    @Override
    public int getCount() {
        return imgArray.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View imgLayout = layoutInflater.inflate(R.layout.slide_images_layout, container, false);

        ImageView imageView = (ImageView) imgLayout.findViewById(R.id.slide_images_view);
        //해당 위치의 이미지를 찾아서 이미지 붙이기
        imageView.setImageBitmap(imgArray[position]);

        //이미지 뷰를 포함하는 레이아웃을 뷰 그룹에 붙임
        container.addView(imgLayout);
        //레이아웃 반환
        return imgLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}
