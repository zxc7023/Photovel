package com.photovel.content;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.photovel.R;
import com.vo.Comment;

import java.util.List;

/**
 * Created by EunD on 2017-06-23.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> mDataset;
    private Context mcontext;
    private CommentAdapter.ViewHolder holder;
    private int position;

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public CommentAdapter.ViewHolder getHolder() {
        return holder;
    }
    public void setHolder(CommentAdapter.ViewHolder holder) {
        this.holder = holder;
    }

    public CommentAdapter() {
    }

    public CommentAdapter(List<Comment> myDataset, Context mycontext) {
        mDataset = myDataset;
        mcontext = mycontext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvComment;

        public ViewHolder(View view) {
            super(view);
            tvComment = (TextView)view.findViewById(R.id.tvComment);
        }
    }

    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_comment_adapter, parent, false);
        CommentAdapter.ViewHolder vh = new CommentAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //imageView를 font로 바꿔주기
        Typeface fontAwesomeFont = Typeface.createFromAsset(mcontext.getAssets(), "fontawesome-webfont.ttf");
        holder.tvComment.setText(mDataset.get(position).getComment_content());
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}