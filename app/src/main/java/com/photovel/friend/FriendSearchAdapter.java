package com.photovel.friend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.photovel.R;
import com.photovel.content.ContentListMain;
import com.photovel.http.JsonConnection;
import com.photovel.http.Value;
import com.vo.User;

import java.util.List;

public class FriendSearchAdapter extends RecyclerView.Adapter<FriendSearchAdapter.ViewHolder>{
    private List<User> mDataset;
    private Context mcontext;
    private FriendSearchAdapter.ViewHolder holder;
    private int position;
    private String user_id;

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public FriendSearchAdapter.ViewHolder getHolder() {
        return holder;
    }
    public void setHolder(FriendSearchAdapter.ViewHolder holder) {
        this.holder = holder;
    }

    public FriendSearchAdapter() {
    }

    public FriendSearchAdapter(List<User> myDataset, Context mycontext) {
        mDataset = myDataset;
        mcontext = mycontext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircularImageView userProfile;
        public TextView tvUserNickName, tvUserID, btnFriendPlus;
        public LinearLayout lluser;

        public ViewHolder(View view) {
            super(view);
            userProfile = (CircularImageView)view.findViewById(R.id.userProfile);
            tvUserNickName = (TextView)view.findViewById(R.id.tvUserNickName);
            tvUserID = (TextView)view.findViewById(R.id.tvUserID);
            btnFriendPlus = (TextView)view.findViewById(R.id.btnFriendPlus);
        }
    }

    @Override
    public FriendSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_friend_search_adapter, parent, false);
        FriendSearchAdapter.ViewHolder vh = new FriendSearchAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Typeface fontAwesomeFont = Typeface.createFromAsset(mcontext.getAssets(), "fontawesome-webfont.ttf");
        holder.btnFriendPlus.setTypeface(fontAwesomeFont);

    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

