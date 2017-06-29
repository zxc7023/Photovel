package com.photovel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.photovel.content.ContentDetailListMain;
import com.photovel.http.JsonConnection;
import com.photovel.http.Value;
import com.vo.Content;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainNewAdapter extends RecyclerView.Adapter<MainNewAdapter.ViewHolder>{
    private List<Content> mDataset;
    private Context mcontext;
    private MainNewAdapter.ViewHolder holder;
    private int position;
    private int[] likeFlag;

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
        likeFlag = new int[getItemCount()];
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout RlmainTop, RLBookmark;
        public ImageView main_ivphoto;
        public TextView contentSubject, userNickname;
        public TextView main_icthumb, main_iccomment, main_icshare;
        public TextView thumbCount, commentCount, shareCount, tvbookmark;
        public LinearLayout llthumb, llcomment, llshare;

        public ViewHolder(View view) {
            super(view);
            RlmainTop = (RelativeLayout)view.findViewById(R.id.RlmainTop);
            RLBookmark = (RelativeLayout)view.findViewById(R.id.RLBookmark);
            main_ivphoto = (ImageView)view.findViewById(R.id.main_ivphoto);
            contentSubject = (TextView)view.findViewById(R.id.contentSubject);
            userNickname = (TextView)view.findViewById(R.id.userNickname);
            main_icthumb = (TextView)view.findViewById(R.id.main_icthumb);
            main_iccomment = (TextView)view.findViewById(R.id.main_iccomment);
            main_icshare = (TextView)view.findViewById(R.id.main_icshare);
            thumbCount = (TextView)view.findViewById(R.id.thumbCount);
            commentCount = (TextView)view.findViewById(R.id.commentCount);
            shareCount = (TextView)view.findViewById(R.id.shareCount);
            tvbookmark = (TextView)view.findViewById(R.id.tvbookmark);
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //imageView를 font로 바꿔주기
        Typeface fontAwesomeFont = Typeface.createFromAsset(mcontext.getAssets(), "fontawesome-webfont.ttf");
        holder.main_icthumb.setTypeface(fontAwesomeFont);
        holder.main_iccomment.setTypeface(fontAwesomeFont);
        holder.main_icshare.setTypeface(fontAwesomeFont);
        holder.tvbookmark.setTypeface(fontAwesomeFont);

        if(mDataset.get(position).getBookmark_status() == 1){ //bookmark유무
            holder.RLBookmark.setVisibility(View.VISIBLE);
            holder.RLBookmark.bringToFront();
        }

        if(mDataset.get(position).getGood_status() == 1){   //좋아요 유무
            holder.main_icthumb.setTextColor(ContextCompat.getColor(mcontext, R.color.textBlue));
            likeFlag[position]=1;
        }else{
            likeFlag[position]=0;
        }

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
                mcontext.startActivity(intent);
            }
        });

        //좋아요 클릭
        holder.llthumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences get_to_eat = mcontext.getSharedPreferences("loginInfo", mcontext.MODE_PRIVATE);
                final String user_id = get_to_eat.getString("user_id","notFound");
                Thread good = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JsonConnection.getConnection(Value.contentURL+"/"+mDataset.get(position).getContent_id()+"/good/"+user_id, "POST", null);
                    }
                });
                good.start();
                try {
                    good.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(likeFlag[position] == 1){
                    holder.main_icthumb.setTextColor(ContextCompat.getColor(mcontext, R.color.bgDarkGrey));
                    holder.thumbCount.setText(String.valueOf(Integer.parseInt(holder.thumbCount.getText().toString())-1));
                    likeFlag[position]=0;
                }else{
                    holder.main_icthumb.setTextColor(ContextCompat.getColor(mcontext, R.color.textBlue));
                    holder.thumbCount.setText(String.valueOf(Integer.parseInt(holder.thumbCount.getText().toString())+1));
                    likeFlag[position]=1;
                }
            }
        });

        //댓글 클릭
        holder.llcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, ContentDetailListMain.class);
                intent.putExtra("content_id", mDataset.get(position).getContent_id());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //재사용 ㄴㄴ
                mcontext.startActivity(intent);
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

