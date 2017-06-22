package com.photovel;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vo.Content;

import java.util.List;

public class MainRecommendAdapter extends RecyclerView.Adapter<MainRecommendAdapter.ViewHolder>{
    private List<Content> mDataset;
    private Context mcontext;
    private MainRecommendAdapter.ViewHolder holder;
    private int position;

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public MainRecommendAdapter.ViewHolder getHolder() {
        return holder;
    }
    public void setHolder(MainRecommendAdapter.ViewHolder holder) {
        this.holder = holder;
    }

    public MainRecommendAdapter() {
    }

    public MainRecommendAdapter(List<Content> myDataset, Context mycontext) {
        mDataset = myDataset;
        mcontext = mycontext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView main_ivphoto;
        public TextView contentSubject;
        public TextView main_icthumb, main_iccomment, main_icshare;

        public ViewHolder(View view) {
            super(view);
            main_ivphoto = (ImageView)view.findViewById(R.id.main_ivphoto);
            contentSubject = (TextView)view.findViewById(R.id.contentSubject);
            main_icthumb = (TextView)view.findViewById(R.id.main_icthumb);
            main_iccomment = (TextView)view.findViewById(R.id.main_iccomment);
            main_icshare = (TextView)view.findViewById(R.id.main_icshare);

        }
    }

    @Override
    public MainRecommendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_recommend_list_adapter, parent, false);
        MainRecommendAdapter.ViewHolder vh = new MainRecommendAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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

        holder.main_icthumb.setText(holder.main_icthumb.getText()+" "+mDataset.get(position).getGood_count());
        holder.main_iccomment.setText(holder.main_iccomment.getText()+" "+mDataset.get(position).getComment_count());
        holder.main_icshare.setText(holder.main_icshare.getText()+" "+mDataset.get(position).getContent_share_count());

        //좋아요 클릭
        holder.main_icthumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //댓글 클릭
        holder.main_iccomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //공유 클릭
        holder.main_icshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
