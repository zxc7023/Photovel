package com.photovel.friend;

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

import com.mikhaellopez.circularimageview.CircularImageView;
import com.photovel.R;
import com.photovel.common.BookMarkMain;
import com.photovel.content.ContentDetailListMain;
import com.photovel.http.JsonConnection;
import com.photovel.http.Value;
import com.vo.Content;
import com.vo.User;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder>{
    private List<User> mDataset;
    private Context mcontext;
    private FriendListAdapter.ViewHolder holder;
    private int position;

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public FriendListAdapter.ViewHolder getHolder() {
        return holder;
    }
    public void setHolder(FriendListAdapter.ViewHolder holder) {
        this.holder = holder;
    }

    public FriendListAdapter() {
    }

    public FriendListAdapter(List<User> myDataset, Context mycontext) {
        mDataset = myDataset;
        mcontext = mycontext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircularImageView userProfile;
        public TextView tvUserNickName, btnFriendDelete, btnFriendBlock;

        public ViewHolder(View view) {
            super(view);
            userProfile = (CircularImageView)view.findViewById(R.id.userProfile);
            tvUserNickName = (TextView)view.findViewById(R.id.tvUserNickName);
            btnFriendDelete = (TextView)view.findViewById(R.id.btnFriendDelete);
            btnFriendBlock = (TextView)view.findViewById(R.id.btnFriendBlock);
        }
    }

    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_friend_list_adapter, parent, false);
        FriendListAdapter.ViewHolder vh = new FriendListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

