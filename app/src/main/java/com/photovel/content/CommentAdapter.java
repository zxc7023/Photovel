package com.photovel.content;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.photovel.MainActivity;
import com.photovel.R;
import com.photovel.http.Value;
import com.vo.Comment;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

/**
 * Created by EunD on 2017-06-23.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> mDataset;
    private Context mcontext;
    private CommentAdapter ca;
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
        public TextView tvComment, btnCommentMenu;

        public ViewHolder(View view) {
            super(view);
            tvComment = (TextView)view.findViewById(R.id.tvComment);
            btnCommentMenu = (TextView)view.findViewById(R.id.btnCommentMenu);
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //imageView를 font로 바꿔주기
        Typeface fontAwesomeFont = Typeface.createFromAsset(mcontext.getAssets(), "fontawesome-webfont.ttf");
        holder.btnCommentMenu.setTypeface(fontAwesomeFont);

        holder.tvComment.setText(mDataset.get(position).getComment_content());

        holder.btnCommentMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*ca = new CommentAdapter();
                ca.setHolder(holder);
                ca.setPosition(position);*/
                CommentMenu(view);
            }
        });

    }

    //디테일 설정 메뉴클릭시
    public void CommentMenu(View v){
        Context wrapper = new ContextThemeWrapper(mcontext, R.style.MenuStyle);
        PopupMenu popup = new PopupMenu(wrapper, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.comment_setting_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_update:
                        Toast.makeText(mcontext,"수정",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_delete:
                        Toast.makeText(mcontext,"삭제",Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}