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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photovel.R;
import com.vo.Content;
import com.vo.ContentDetail;
import com.vo.Photo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by HARA on 2017-06-07.
 */

public class ContentListAdapter extends RecyclerView.Adapter<ContentListAdapter.ViewHolder>{
    private List<Content> mDataset;
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

    public ContentListAdapter() {
    }

    public ContentListAdapter(List<Content> myDataset, Context mycontext) {
        mDataset = myDataset;
        mcontext = mycontext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout RldetailData;
        public LinearLayout RLdetailDate, LLmenu;
        public TextView icglobe, icleft, icright, tvleft, tvright, iccal, icmarker, icpow, icthumb, iccomment, icshare, btnDetailMenu;
        public TextView tvContentInsertDate, tvContentSubject, tvContentLocation, tvUsername, tvDuring, tvdetailcount, tvdetailstate, tvdetailstatetext, tvContent;
        public TextView tvLikeCount, tvCommentCount, tvShareCount;
        public LinearLayout btnLookLeft, btnLookRight;
        public ImageView ivTopPhoto;

        public ViewHolder(View view) {
            super(view);

            RLdetailDate = (LinearLayout)view.findViewById(R.id.RLdetailDate);
            LLmenu = (LinearLayout)view.findViewById(R.id.LLmenu);
            RldetailData = (RelativeLayout)view.findViewById(R.id.RldetailData);
            icglobe = (TextView)view.findViewById(R.id.icglobe);
            iccal = (TextView)view.findViewById(R.id.iccal);
            icmarker = (TextView)view.findViewById(R.id.icmarker);
            icpow = (TextView)view.findViewById(R.id.icpow);
            icthumb = (TextView)view.findViewById(R.id.icthumb);
            iccomment = (TextView)view.findViewById(R.id.iccomment);
            icshare = (TextView)view.findViewById(R.id.icshare);
            icleft = (TextView)view.findViewById(R.id.icleft);
            icright = (TextView)view.findViewById(R.id.icright);
            tvleft = (TextView)view.findViewById(R.id.tvleft);
            tvright = (TextView)view.findViewById(R.id.tvright);
            btnDetailMenu = (TextView) view.findViewById(R.id.btnDetailMenu);
            ivTopPhoto = (ImageView) view.findViewById(R.id.ivTopPhoto);

            tvContentInsertDate = (TextView) view.findViewById(R.id.tvContentInsertDate);    //컨텐트입력날짜
            tvContentSubject = (TextView) view.findViewById(R.id.tvContentSubject);           //컨텐트 제목
            tvContentLocation = (TextView) view.findViewById(R.id.tvContentLocation);        //컨텐트 위치(마지막)
            tvUsername = (TextView) view.findViewById(R.id.tvUsername);                        //유저 네임
            tvDuring = (TextView) view.findViewById(R.id.tvDuring);                             //컨텐트 날짜첫날 ~ 날짜 끝날(2016.04.20 ~ 2016.06.20)
            tvdetailstate = (TextView) view.findViewById(R.id.tvdetailstate);                  //보고있는 화면 상태(사진/동영상/지도)
            tvdetailstatetext = (TextView) view.findViewById(R.id.tvdetailstatetext);
            tvContent = (TextView) view.findViewById(R.id.tvContent);                            //컨텐트 내용
            tvLikeCount = (TextView) view.findViewById(R.id.tvLikeCount);
            tvCommentCount = (TextView) view.findViewById(R.id.tvCommentCount);
            tvShareCount = (TextView) view.findViewById(R.id.tvShareCount);
            tvdetailcount = (TextView) view.findViewById(R.id.tvdetailcount);                    //디테일 수

            btnLookLeft = (LinearLayout) view.findViewById(R.id.btnLookLeft);
            btnLookRight = (LinearLayout) view.findViewById(R.id.btnLookRight);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_content_list_adapter, parent, false);
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
        holder.icglobe.setTypeface(fontAwesomeFont);
        holder.icleft.setTypeface(fontAwesomeFont);
        holder.icright.setTypeface(fontAwesomeFont);
        holder.iccal.setTypeface(fontAwesomeFont);
        holder.icmarker.setTypeface(fontAwesomeFont);
        holder.icpow.setTypeface(fontAwesomeFont);
        holder.icthumb.setTypeface(fontAwesomeFont);
        holder.iccomment.setTypeface(fontAwesomeFont);
        holder.icshare.setTypeface(fontAwesomeFont);
        holder.btnDetailMenu.setTypeface(fontAwesomeFont);

        holder.icleft.setText(R.string.fa_flag);
        holder.tvleft.setText("지도로 보기");
        holder.icright.setText(R.string.fa_video_camera);
        holder.tvright.setText("슬라이드로 보기");

        holder.RLdetailDate.bringToFront();
        holder.LLmenu.bringToFront();
        holder.RldetailData.bringToFront();

        holder.tvContentInsertDate.setText(new SimpleDateFormat("yyyy.MM.dd").format(mDataset.get(position).getContent_written_date()));
        holder.tvContentSubject.setText(mDataset.get(position).getContent_subject());

        //주소처리
        Photo ph = new Photo();
        ph.setPhoto_latitude(mDataset.get(position).getPhoto_latitude());
        ph.setPhoto_longitude(mDataset.get(position).getPhoto_longitude());
        GetCurrentAddress getAddress = new GetCurrentAddress();
        String address = getAddress.getAddress(ph);

        holder.tvContentLocation.setText(address);
        holder.tvUsername.setText(mDataset.get(position).getUser().getUser_nick_name());

        //날짜처리
        String frDate = new SimpleDateFormat("yyyy.MM.dd").format(mDataset.get(position).getFr_photo_date());
        String toDate = new SimpleDateFormat("yyyy.MM.dd").format(mDataset.get(position).getTo_photo_date());

        holder.tvDuring.setText(frDate+" ~ "+toDate);
        holder.tvdetailcount.setText(String.valueOf(mDataset.get(position).getContent_detail_count()));
        holder.tvdetailstate.setText("");
        holder.tvdetailstatetext.setText("");

        holder.tvLikeCount.setText(String.valueOf(mDataset.get(position).getGood_count()));
        holder.tvCommentCount.setText(String.valueOf(mDataset.get(position).getComment_count()));
        holder.tvShareCount.setText(String.valueOf(mDataset.get(position).getContent_share_count()));

        holder.ivTopPhoto.setImageBitmap(mDataset.get(position).getBitmap());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
