package com.photovel.friend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.photovel.R;
import com.photovel.content.ContentDetailListMain;
import com.photovel.content.ContentListMain;
import com.photovel.http.JsonConnection;
import com.photovel.http.Value;
import com.vo.User;

import java.util.List;

public class FriendNewListAdapter extends RecyclerView.Adapter<FriendNewListAdapter.ViewHolder>{
    private List<User> mDataset;
    private Context mcontext;
    private FriendNewListAdapter.ViewHolder holder;
    private int position;
    private String user_id;

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
        public TextView tvUserNickName, btnFriendDelete, btnFriendAccept;
        public LinearLayout lluser;

        public ViewHolder(View view) {
            super(view);
            userProfile = (CircularImageView)view.findViewById(R.id.userProfile);
            tvUserNickName = (TextView)view.findViewById(R.id.tvUserNickName);
            btnFriendAccept = (TextView)view.findViewById(R.id.btnFriendAccept);
            btnFriendDelete = (TextView)view.findViewById(R.id.btnFriendDelete);
            lluser = (LinearLayout)view.findViewById(R.id.lluser);
        }
    }

    @Override
    public FriendNewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_friend_new_list_adapter, parent, false);
        FriendNewListAdapter.ViewHolder vh = new FriendNewListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SharedPreferences get_to_eat = mcontext.getSharedPreferences("loginInfo", mcontext.MODE_PRIVATE);
        user_id = get_to_eat.getString("user_id","notFound");
        holder.userProfile.setImageBitmap(mDataset.get(position).getBitmap());
        holder.tvUserNickName.setText(mDataset.get(position).getUser_nick_name());
        holder.lluser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dintent = new Intent(mcontext, ContentListMain.class);
                dintent.putExtra("user_id", mDataset.get(position).getUser_id());
                dintent.putExtra("urlflag","");
                mcontext.startActivity(dintent);
            }
        });
        holder.btnFriendAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //친구 수락
                Thread accept = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        JsonConnection.getConnection(Value.photovelURL+"/friend/accept/"+user_id+"/"+mDataset.get(position).getUser_id(), "POST", null);
                    }
                };
                accept.start();
                try {
                    accept.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(mcontext, FriendListMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //재사용 ㄴㄴ
                mcontext.startActivity(intent);
                Toast.makeText(mcontext, "친구수락완료", Toast.LENGTH_SHORT).show();
            }
        });
        holder.btnFriendDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //친구 삭제
                Thread accept = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        JsonConnection.getConnection(Value.photovelURL+"/friend/block/"+user_id+"/"+mDataset.get(position).getUser_id(), "POST", null);
                    }
                };
                accept.start();
                try {
                    accept.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(mcontext, FriendListMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //재사용 ㄴㄴ
                mcontext.startActivity(intent);
                Toast.makeText(mcontext, "친구요청삭제완료", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

