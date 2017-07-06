package com.photovel.friend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
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

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder>{
    private List<User> mDataset;
    private Context mcontext;
    private FriendListAdapter.ViewHolder holder;
    private int position;
    private String user_id;

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
        public LinearLayout lluser;

        public ViewHolder(View view) {
            super(view);
            userProfile = (CircularImageView)view.findViewById(R.id.userProfile);
            tvUserNickName = (TextView)view.findViewById(R.id.tvUserNickName);
            btnFriendDelete = (TextView)view.findViewById(R.id.btnFriendDelete);
            btnFriendBlock = (TextView)view.findViewById(R.id.btnFriendBlock);
            lluser = (LinearLayout)view.findViewById(R.id.lluser);
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

        //친구 삭제
        holder.btnFriendDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dalert_confirm = new AlertDialog.Builder(mcontext);
                dalert_confirm.setMessage("정말 친구를 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Thread delete = new Thread(){
                                    @Override
                                    public void run() {
                                        super.run();
                                        JsonConnection.getConnection(Value.photovelURL+"/friend/delete/"+user_id+"/"+mDataset.get(position).getUser_id(), "POST", null);
                                    }
                                };
                                delete.start();
                                try {
                                    delete.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent(mcontext, FriendListMain.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //재사용 ㄴㄴ
                                mcontext.startActivity(intent);
                                Toast.makeText(mcontext, "친구삭제 완료", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                AlertDialog dalert = dalert_confirm.create();
                dalert.show();
            }
        });

    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

