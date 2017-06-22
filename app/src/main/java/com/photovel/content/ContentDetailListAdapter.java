package com.photovel.content;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.photovel.R;
import com.vo.ContentDetail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by HARA on 2017-06-07.
 */

public class ContentDetailListAdapter extends RecyclerView.Adapter<ContentDetailListAdapter.ViewHolder>{
    private List<ContentDetail> mDataset;
    private Context mcontext;
    private ViewHolder holder;
    private int position;

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public ViewHolder getHolder() {
        return holder;
    }
    public void setHolder(ViewHolder holder) {
        this.holder = holder;
    }

    public ContentDetailListAdapter() {
    }

    public ContentDetailListAdapter(List<ContentDetail> myDataset, Context mycontext) {
        mDataset = myDataset;
        mcontext = mycontext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivphoto;
        public TextView tvLocation, tvDate, tvDetailContent;
        public TextView icmarker, iccircle1, iccircle2, iccircle3, iccircle4, iccircle5, tvPosition;

        public ViewHolder(View view) {
            super(view);
            ivphoto = (ImageView) view.findViewById(R.id.ivphoto);
            tvLocation = (TextView) view.findViewById(R.id.tvLocation);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvDetailContent = (TextView) view.findViewById(R.id.tvDetailContent);
            icmarker = (TextView) view.findViewById(R.id.icmarker);
            iccircle1 = (TextView) view.findViewById(R.id.iccircle1);
            iccircle2 = (TextView) view.findViewById(R.id.iccircle2);
            iccircle3 = (TextView) view.findViewById(R.id.iccircle3);
            iccircle4 = (TextView) view.findViewById(R.id.iccircle4);
            iccircle5 = (TextView) view.findViewById(R.id.iccircle5);
            tvPosition = (TextView) view.findViewById(R.id.tvPosition);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_content_detail_list_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        this.holder = holder;
        this.position = position;

        //imageView를 font로 바꿔주기
        Typeface fontAwesomeFont = Typeface.createFromAsset(mcontext.getAssets(), "fontawesome-webfont.ttf");
        holder.icmarker.setTypeface(fontAwesomeFont);
        holder.iccircle1.setTypeface(fontAwesomeFont);
        holder.iccircle2.setTypeface(fontAwesomeFont);
        holder.iccircle3.setTypeface(fontAwesomeFont);
        holder.iccircle4.setTypeface(fontAwesomeFont);
        holder.iccircle5.setTypeface(fontAwesomeFont);

        holder.tvPosition.setText(String.valueOf(position+1));

        final Date date=mDataset.get(position).getPhoto().getPhoto_date();
        String date2=null;
        try {
            date2 = new SimpleDateFormat("yyyy.MM.dd").format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //사진
        holder.ivphoto.setImageBitmap(mDataset.get(position).getPhoto().getBitmap());

        //날짜
        holder.tvDate.setText(date2);

        //위치
        //Log.i("ddd address",mDataset.get(position).getPhoto().getAddress()+"");
        holder.tvLocation.setText(mDataset.get(position).getPhoto().getAddress());

        //컨텐츠
        holder.tvDetailContent.setText(mDataset.get(position).getDetail_content());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}