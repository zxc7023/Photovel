package com.photovel.content;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;
import com.photovel.MainActivity;
import com.photovel.R;
import com.photovel.http.JsonConnection;
import com.photovel.http.Value;
import com.vo.Content;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HARA on 2017-06-07.
 */

public class ContentListAdapter extends RecyclerView.Adapter<ContentListAdapter.ViewHolder>{
    private List<Content> mDataset;
    private Context mcontext;
    private ViewHolder holder;
    private int position, content_id=-1;
    private String user_id;
    private String urlflag;
    private int[] likeFlag, bookmarkFlag;
    private ContentListAdapter adapter;
    private String templateId = "4643";

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public ViewHolder getHolder() {
        return holder;
    }
    public void setHolder(ViewHolder holder) {
        this.holder = holder;
    }

    public ContentListAdapter() {
    }

    public ContentListAdapter(List<Content> myDataset, Context mycontext, String urlflag) {
        mDataset = myDataset;
        mcontext = mycontext;
        this.urlflag = urlflag;
        likeFlag = bookmarkFlag = new int[getItemCount()];
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout RldetailData;
        public LinearLayout RLdetailDate, LLmenu;
        public TextView icglobe, icleft, icright, tvleft, tvright, iccal, icmarker, icbookmark, icthumb, iccomment, icshare, btnDetailMenu;
        public TextView tvContentInsertDate, tvContentSubject, tvContentLocation, tvUsername, tvDuring, tvdetailcount, tvContent;
        public TextView tvLikeCount, tvlike, tvbookmark, tvCommentCount, tvShareCount, btnComment;
        public LinearLayout btnLookLeft, btnLookRight, btnLike, btnBookmark, btnShare;
        public ImageView ivTopPhoto, userProfile;

        public ViewHolder(View view) {
            super(view);

            RLdetailDate = (LinearLayout)view.findViewById(R.id.RLdetailDate);
            LLmenu = (LinearLayout)view.findViewById(R.id.LLmenu);
            RldetailData = (RelativeLayout)view.findViewById(R.id.RldetailData);
            icglobe = (TextView)view.findViewById(R.id.icglobe);
            iccal = (TextView)view.findViewById(R.id.iccal);
            icmarker = (TextView)view.findViewById(R.id.icmarker);
            icbookmark = (TextView)view.findViewById(R.id.icbookmark);
            icthumb = (TextView)view.findViewById(R.id.icthumb);
            iccomment = (TextView)view.findViewById(R.id.iccomment);
            icshare = (TextView)view.findViewById(R.id.icshare);
            icleft = (TextView)view.findViewById(R.id.icleft);
            icright = (TextView)view.findViewById(R.id.icright);
            tvleft = (TextView)view.findViewById(R.id.tvleft);
            tvright = (TextView)view.findViewById(R.id.tvright);
            btnDetailMenu = (TextView) view.findViewById(R.id.btnDetailMenu);
            ivTopPhoto = (ImageView) view.findViewById(R.id.ivTopPhoto);
            userProfile = (ImageView) view.findViewById(R.id.userProfile);

            tvContentInsertDate = (TextView) view.findViewById(R.id.tvContentInsertDate);    //컨텐트입력날짜
            tvContentSubject = (TextView) view.findViewById(R.id.tvContentSubject);           //컨텐트 제목
            tvContentLocation = (TextView) view.findViewById(R.id.tvContentLocation);        //컨텐트 위치(마지막)
            tvUsername = (TextView) view.findViewById(R.id.tvUsername);                        //유저 네임
            tvDuring = (TextView) view.findViewById(R.id.tvDuring);                             //컨텐트 날짜첫날 ~ 날짜 끝날(2016.04.20 ~ 2016.06.20)
            tvContent = (TextView) view.findViewById(R.id.tvContent);                            //컨텐트 내용
            tvLikeCount = (TextView) view.findViewById(R.id.tvLikeCount);
            tvlike = (TextView) view.findViewById(R.id.tvlike);
            tvbookmark = (TextView) view.findViewById(R.id.tvbookmark);
            tvCommentCount = (TextView) view.findViewById(R.id.tvCommentCount);
            tvShareCount = (TextView) view.findViewById(R.id.tvShareCount);
            tvdetailcount = (TextView) view.findViewById(R.id.tvdetailcount);                    //디테일 수

            btnComment = (TextView) view.findViewById(R.id.btnComment);
            btnLookLeft = (LinearLayout) view.findViewById(R.id.btnLookLeft);
            btnLookRight = (LinearLayout) view.findViewById(R.id.btnLookRight);
            btnLike = (LinearLayout) view.findViewById(R.id.btnLike);
            btnBookmark = (LinearLayout) view.findViewById(R.id.btnBookmark);
            btnShare = (LinearLayout) view.findViewById(R.id.btnShare);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_content_list_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        this.holder = holder;
        this.position = position;

        //imageView를 font로 바꿔주기
        Typeface fontAwesomeFont = Typeface.createFromAsset(mcontext.getAssets(), "fontawesome-webfont.ttf");
        holder.icmarker.setTypeface(fontAwesomeFont);
        holder.icglobe.setTypeface(fontAwesomeFont);
        holder.icleft.setTypeface(fontAwesomeFont);
        holder.icright.setTypeface(fontAwesomeFont);
        holder.iccal.setTypeface(fontAwesomeFont);
        holder.icmarker.setTypeface(fontAwesomeFont);
        holder.icbookmark.setTypeface(fontAwesomeFont);
        holder.icthumb.setTypeface(fontAwesomeFont);
        holder.iccomment.setTypeface(fontAwesomeFont);
        holder.icshare.setTypeface(fontAwesomeFont);
        holder.btnDetailMenu.setTypeface(fontAwesomeFont);

        holder.icleft.setText(R.string.fa_flag);
        holder.tvleft.setText("지도로 보기");
        holder.icright.setText(R.string.fa_video_camera);
        holder.tvright.setText("슬라이드로 보기");

        holder.RLdetailDate.bringToFront();
        holder.LLmenu.bringToFront();
        holder.RldetailData.bringToFront();

        holder.tvContentInsertDate.setText(new SimpleDateFormat("yyyy.MM.dd").format(mDataset.get(position).getContent_written_date()));
        holder.tvContentSubject.setText(mDataset.get(position).getContent_subject());

        //bookmark유무
        if(mDataset.get(position).getBookmark_status() == 1){
            holder.icbookmark.setText(R.string.fa_bookmark);
            holder.icbookmark.setTextColor(ContextCompat.getColor(mcontext, R.color.textBlue));
            holder.tvbookmark.setTextColor(ContextCompat.getColor(mcontext, R.color.textBlue));
            bookmarkFlag[position]=1;
        }

        //좋아요 유무
        if(mDataset.get(position).getGood_status() == 1){
            holder.icthumb.setTextColor(ContextCompat.getColor(mcontext, R.color.textBlue));
            holder.tvlike.setTextColor(ContextCompat.getColor(mcontext, R.color.textBlue));
            likeFlag[position]=1;
        }

        //주소처리
        GetCurrentAddress getAddress = new GetCurrentAddress();
        String address = getAddress.getAddress(mDataset.get(position).getPhoto_latitude(), mDataset.get(position).getPhoto_longitude());

        holder.tvContentLocation.setText(address);
        holder.tvUsername.setText(mDataset.get(position).getUser().getUser_nick_name());

        //날짜처리
        String frDate = new SimpleDateFormat("yyyy.MM.dd").format(mDataset.get(position).getFr_photo_date());
        String toDate = new SimpleDateFormat("yyyy.MM.dd").format(mDataset.get(position).getTo_photo_date());

        holder.tvDuring.setText(frDate+" ~ "+toDate);
        holder.tvdetailcount.setText(String.valueOf(mDataset.get(position).getContent_detail_count()));

        holder.tvLikeCount.setText(String.valueOf(mDataset.get(position).getGood_count()));
        holder.tvCommentCount.setText(String.valueOf(mDataset.get(position).getComment_count()));
        holder.tvShareCount.setText(String.valueOf(mDataset.get(position).getContent_share_count()));

        holder.ivTopPhoto.setImageBitmap(mDataset.get(position).getBitmap());
        holder.userProfile.setImageBitmap(mDataset.get(position).getUser().getBitmap());

        holder.btnLookLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content_id = mDataset.get(position).getContent_id();
                goLook(view);
            }
        });
        holder.btnLookRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content_id = mDataset.get(position).getContent_id();
                goLook(view);
            }
        });
        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content_id = mDataset.get(position).getContent_id();
                goLook(view);
            }
        });

        //디테일 메뉴 보이기 전에 글쓴이 == 내계정 확인
        SharedPreferences get_to_eat = mcontext.getSharedPreferences("loginInfo", mcontext.MODE_PRIVATE);
        user_id = get_to_eat.getString("user_id","notFound");
        if(!mDataset.get(position).getUser().getUser_id().equals(user_id)){
            holder.LLmenu.setVisibility(View.GONE);
        }
        holder.btnDetailMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content_id = mDataset.get(position).getContent_id();
                ContentMenuClick(view);
            }
        });

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    holder.icthumb.setTextColor(ContextCompat.getColor(mcontext, R.color.bgDarkGrey));
                    holder.tvlike.setTextColor(ContextCompat.getColor(mcontext, R.color.bgDarkGrey));
                    holder.tvLikeCount.setText(String.valueOf(Integer.parseInt(holder.tvLikeCount.getText().toString())-1));
                    likeFlag[position]=0;
                }else{
                    holder.icthumb.setTextColor(ContextCompat.getColor(mcontext, R.color.textBlue));
                    holder.tvlike.setTextColor(ContextCompat.getColor(mcontext, R.color.textBlue));
                    holder.tvLikeCount.setText(String.valueOf(Integer.parseInt(holder.tvLikeCount.getText().toString())+1));
                    likeFlag[position]=1;
                }
            }
        });

        holder.btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread bookmark = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JsonConnection.getConnection(Value.contentURL+"/"+mDataset.get(position).getContent_id()+"/bookmark/"+user_id, "POST", null);
                    }
                });
                bookmark.start();
                try {
                    bookmark.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(bookmarkFlag[position] == 1){
                    holder.icbookmark.setText(R.string.fa_bookmark_o);
                    holder.icbookmark.setTextColor(ContextCompat.getColor(mcontext, R.color.bgDarkGrey));
                    holder.tvbookmark.setTextColor(ContextCompat.getColor(mcontext, R.color.bgDarkGrey));
                    bookmarkFlag[position]=0;
                }else{
                    holder.icbookmark.setText(R.string.fa_bookmark);
                    holder.icbookmark.setTextColor(ContextCompat.getColor(mcontext, R.color.textBlue));
                    holder.tvbookmark.setTextColor(ContextCompat.getColor(mcontext, R.color.textBlue));
                    bookmarkFlag[position]=1;
                }
            }
        });

        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread share = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JsonConnection.getConnection(Value.contentURL+"/"+mDataset.get(position).getContent_id()+"/share", "POST", null);
                    }
                });
                share.start();
                try {
                    share.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                holder.tvShareCount.setText(String.valueOf(Integer.parseInt(holder.tvShareCount.getText().toString())+1));
                contentshare(v);
                adapter = new ContentListAdapter();
                adapter.setHolder(holder);
                adapter.setPosition(position);
            }
        });

    }

    //onClick
    public void goLook(View v){
        switch (v.getId()){
            case R.id.btnLookLeft:
                //Toast.makeText(mcontext,"지도로보기",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(mcontext, ContentClusterMain.class);
                intent.putExtra("content_id",content_id);
                mcontext.startActivity(intent);
                break;
            case R.id.btnLookRight:
                Intent intent2=new Intent(mcontext, ContentSlideShowMain.class);
                intent2.putExtra("content_id",content_id);
                mcontext.startActivity(intent2);
                break;
            case  R.id.btnComment:
                Intent intent3=new Intent(mcontext, ContentDetailListMain.class);
                intent3.putExtra("content_id",content_id);
                mcontext.startActivity(intent3);
                break;
        }
    }

    //디테일 설정 메뉴클릭시
    public void ContentMenuClick(View v){
        Context wrapper = new ContextThemeWrapper(mcontext, R.style.MenuStyle);
        PopupMenu popup = new PopupMenu(wrapper, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.content_setting_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_update:
                        Intent intent = new Intent(mcontext, ContentUpdateMain.class);
                        intent.putExtra("content_id", content_id);
                        mcontext.startActivity(intent);
                        Toast.makeText(mcontext, "수정", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_delete:
                        AlertDialog.Builder dalert_confirm = new AlertDialog.Builder(mcontext);
                        dalert_confirm.setMessage("정말 글을 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Thread deleteContent = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                JsonConnection.getConnection(Value.contentURL+"/"+content_id, "DELETE", null);
                                            }
                                        });
                                        deleteContent.start();
                                        try {
                                            deleteContent.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(mcontext,"삭제성공",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(mcontext, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //재사용 ㄴㄴ
                                        mcontext.startActivity(intent);
                                        //finish();
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
                        break;
                    case R.id.action_report:
                        AlertDialog.Builder walert_confirm = new AlertDialog.Builder(mcontext);
                        walert_confirm.setMessage("정말 글을 신고 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Thread th = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                JsonConnection.getConnection(Value.contentURL+"/"+content_id+"/warning", "POST", null);
                                            }
                                        });
                                        th.start();
                                        try {
                                            th.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(mcontext,"신고성공",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(mcontext, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //재사용 ㄴㄴ
                                        mcontext.startActivity(intent);
                                        //finish();
                                    }
                                }).setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                });
                        AlertDialog walert = walert_confirm.create();
                        walert.show();
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

    //공유 메뉴클릭시
    public void contentshare(View v){
        android.widget.PopupMenu menu = new android.widget.PopupMenu(mcontext, v);
        menu.inflate(R.menu.content_share_menu);
        menu.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.kakao_share:
                        sendFeedTemplate();
                        Toast.makeText(mcontext,"카카오",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.facebook_share:
                        Toast.makeText(mcontext,"페북",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mcontext, errorResult.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
            }
        });
    }
}
