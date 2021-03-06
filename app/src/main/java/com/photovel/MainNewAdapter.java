package com.photovel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;
import com.photovel.content.ContentDetailListMain;
import com.photovel.http.JsonConnection;
import com.photovel.http.Value;
import com.vo.Content;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainNewAdapter extends RecyclerView.Adapter<MainNewAdapter.ViewHolder>{
    private List<Content> mDataset;
    private Context mcontext;
    private MainNewAdapter.ViewHolder holder;
    private int position;
    private int[] likeFlag;
    private MainNewAdapter adapter;
    private String templateId = "4643";

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
        public ImageView main_ivphoto, userProfile;
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
            userProfile = (ImageView)view.findViewById(R.id.userProfile);

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
        holder.userProfile.setImageBitmap(mDataset.get(position).getUser().getBitmap());
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
                adapter = new MainNewAdapter();
                adapter.setHolder(holder);
                adapter.setPosition(position);
                contentshare(v);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    //공유 메뉴클릭시
    public void contentshare(View v){
        PopupMenu menu = new PopupMenu(mcontext, v);
        menu.inflate(R.menu.content_share_menu);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.kakao_share:
                        sendFeedTemplate();
                        adapter.getHolder().shareCount.setText(String.valueOf(Integer.parseInt(adapter.getHolder().shareCount.getText().toString())+1));
                        Thread share = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                JsonConnection.getConnection(Value.contentURL+"/"+mDataset.get(adapter.getPosition()).getContent_id()+"/share", "POST", null);
                            }
                        });
                        share.start();
                        try {
                            share.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.facebook_share:
                        Toast.makeText(mcontext,"아직 개발중입니닷",Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        try {
            Field[] fields = menu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(menu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        menu.show();
    }

    //카카오 링크 공유
    private void sendFeedTemplate() {
        Map<String, String> templateArgs = new HashMap<String, String>();

        templateArgs.put("${image_url}", Value.contentPhotoURL+"/"+mDataset.get(adapter.getPosition()).getContent_id()+"/"+mDataset.get(adapter.getPosition()).getPhoto_file_name());
        templateArgs.put("${user_profile}", Value.contentPhotoURL+"/profile/"+mDataset.get(adapter.getPosition()).getUser().getUser_profile_photo());
        templateArgs.put("${user_nick_name}", mDataset.get(adapter.getPosition()).getUser().getUser_nick_name());
        templateArgs.put("${content_subject}", mDataset.get(adapter.getPosition()).getContent_subject());
        templateArgs.put("${content}", mDataset.get(adapter.getPosition()).getContent());
        templateArgs.put("${good_count}", String.valueOf(mDataset.get(adapter.getPosition()).getGood_count()));
        templateArgs.put("${comment_count}",String.valueOf(mDataset.get(adapter.getPosition()).getComment_count()));
        templateArgs.put("${content_share_count}", String.valueOf(mDataset.get(adapter.getPosition()).getContent_share_count()));
        KakaoLinkService.getInstance().sendCustom(mcontext, templateId, templateArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
            }
        });
    }
}

