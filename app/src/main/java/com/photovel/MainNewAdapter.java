package com.photovel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photovel.content.ContentDetailListMain;
import com.vo.Content;

import java.util.List;

public class MainNewAdapter extends RecyclerView.Adapter<MainNewAdapter.ViewHolder>{
    private List<Content> mDataset;
    private Context mcontext;
    private MainNewAdapter.ViewHolder holder;
    private int position;

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public MainNewAdapter.ViewHolder getHolder() {
        return holder;
    }
    public void setHolder(MainNewAdapter.ViewHolder holder) {
        this.holder = holder;
    }

    public MainNewAdapter() {
    }

    public MainNewAdapter(List<Content> myDataset, Context mycontext) {
        mDataset = myDataset;
        mcontext = mycontext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout RlmainTop;
        public ImageView main_ivphoto;
        public TextView contentSubject, userNickname;
        public TextView main_icthumb, main_iccomment, main_icshare;
        public TextView thumbCount, commentCount, shareCount;
        public LinearLayout llthumb, llcomment, llshare;

        public ViewHolder(View view) {
            super(view);
            RlmainTop = (RelativeLayout)view.findViewById(R.id.RlmainTop);
            main_ivphoto = (ImageView)view.findViewById(R.id.main_ivphoto);
            contentSubject = (TextView)view.findViewById(R.id.contentSubject);
            userNickname = (TextView)view.findViewById(R.id.userNickname);
            main_icthumb = (TextView)view.findViewById(R.id.main_icthumb);
            main_iccomment = (TextView)view.findViewById(R.id.main_iccomment);
            main_icshare = (TextView)view.findViewById(R.id.main_icshare);
            thumbCount = (TextView)view.findViewById(R.id.thumbCount);
            commentCount = (TextView)view.findViewById(R.id.commentCount);
            shareCount = (TextView)view.findViewById(R.id.shareCount);
            llthumb = (LinearLayout)view.findViewById(R.id.llthumb);
            llcomment = (LinearLayout)view.findViewById(R.id.llcomment);
            llshare = (LinearLayout)view.findViewById(R.id.llshare);

        }
    }

    @Override
    public MainNewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_new_list_adapter, parent, false);
        MainNewAdapter.ViewHolder vh = new MainNewAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //imageView를 font로 바꿔주기
        Typeface fontAwesomeFont = Typeface.createFromAsset(mcontext.getAssets(), "fontawesome-webfont.ttf");
        holder.main_icthumb.setTypeface(fontAwesomeFont);
        holder.main_iccomment.setTypeface(fontAwesomeFont);
        holder.main_icshare.setTypeface(fontAwesomeFont);

        holder.main_ivphoto.setImageBitmap(mDataset.get(position).getBitmap());
        //subject 15자 이상이면 ... 붙이기
        String subject = mDataset.get(position).getContent_subject();
        if(subject.length() >= 16){
            StringBuilder temp = new StringBuilder(subject.substring(0, 16));
            temp.append("...");
            subject = temp.toString();
        }
        holder.contentSubject.setText(subject);
        holder.userNickname.setText(mDataset.get(position).getUser().getUser_nick_name());
        holder.thumbCount.setText(String.valueOf(mDataset.get(position).getGood_count()));
        holder.commentCount.setText(String.valueOf(mDataset.get(position).getComment_count()));
        holder.shareCount.setText(String.valueOf(mDataset.get(position).getContent_share_count()));

        //사진클릭
        holder.RlmainTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click","메인이 클릭되었당!");
                Intent intent = new Intent(mcontext, ContentDetailListMain.class);
                intent.putExtra("content_id", mDataset.get(position).getContent_id());
                ((Activity)mcontext).startActivity(intent);
            }
        });

        //좋아요 클릭
        holder.llthumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click","좋아요가 클릭되었당!");
            }
        });

        //댓글 클릭
        holder.llcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click","댓글 클릭되었당!");
            }
        });

        //공유 클릭
        holder.llshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click","공유 클릭되었당!");
            }
        });
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}