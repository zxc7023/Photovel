package com.photovel.friend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.photovel.R;
import com.vo.User;

import java.util.List;

public class FriendNewListAdapter extends RecyclerView.Adapter<FriendNewListAdapter.ViewHolder>{
    private List<User> mDataset;
    private Context mcontext;
    private FriendNewListAdapter.ViewHolder holder;
    private int position;

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public FriendNewListAdapter.ViewHolder getHolder() {
        return holder;
    }
    public void setHolder(FriendNewListAdapter.ViewHolder holder) {
        this.holder = holder;
    }

    public FriendNewListAdapter() {
    }

    public FriendNewListAdapter(List<User> myDataset, Context mycontext) {
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
    public FriendNewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_friend_list_adapter, parent, false);
        FriendNewListAdapter.ViewHolder vh = new FriendNewListAdapter.ViewHolder(v);
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

