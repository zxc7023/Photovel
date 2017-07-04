package com.photovel.friend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private int position, friend_state;
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
        public TextView tvUserNickName, btnFriendPlus;
        public LinearLayout lluser;

        public ViewHolder(View view) {
            super(view);
            userProfile = (CircularImageView)view.findViewById(R.id.userProfile);
            tvUserNickName = (TextView)view.findViewById(R.id.tvUserNickName);
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
        SharedPreferences get_to_eat = mcontext.getSharedPreferences("loginInfo", mcontext.MODE_PRIVATE);
        user_id = get_to_eat.getString("user_id","notFound");

        Typeface fontAwesomeFont = Typeface.createFromAsset(mcontext.getAssets(), "fontawesome-webfont.ttf");
        holder.btnFriendPlus.setTypeface(fontAwesomeFont);

        holder.userProfile.setImageBitmap(mDataset.get(position).getBitmap());
        holder.tvUserNickName.setText(mDataset.get(position).getUser_nick_name());

        holder.btnFriendPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //친구신청하기
                friend_state = -1;
                Thread tfriend_state = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        friend_state = Integer.parseInt(JsonConnection.getConnection(Value.photovelURL+"/friend/new/"+user_id+"/"+mDataset.get(position).getUser_id(), "POST", null));
                    }
                });
                tfriend_state.start();
                try {
                    tfriend_state.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i("ddd","friend_state"+friend_state);

                if(friend_state == 0){ //친구가아닐때
                    Toast.makeText(mcontext,"친구신청 성공!",Toast.LENGTH_SHORT).show();
                }else if(friend_state == 1){ //내가 이미 친구신청을 했을때
                    Toast.makeText(mcontext,"이미 친구신청을 하였습니다",Toast.LENGTH_SHORT).show();
                }else if(friend_state == 2){ //친구일때
                    Toast.makeText(mcontext,"이미 친구입니다",Toast.LENGTH_SHORT).show();
                }else if(friend_state == 3){ //상대방이 이미 친구신청을 했을때
                    Toast.makeText(mcontext,mDataset.get(position).getUser_id()+"님이 이미 친구신청을 하였습니다",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

