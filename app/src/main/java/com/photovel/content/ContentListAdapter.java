package com.photovel.content;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.photovel.MainActivity;
import com.photovel.R;
import com.photovel.http.Value;
import com.vo.Content;
import com.vo.ContentDetail;
import com.vo.Photo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by HARA on 2017-06-07.
 */

public class ContentListAdapter extends RecyclerView.Adapter<ContentListAdapter.ViewHolder>{
    private List<Content> mDataset;
    private Context mcontext;
    private ViewHolder holder;
    private int position, content_id=-1;;

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

    public ContentListAdapter(List<Content> myDataset, Context mycontext) {
        mDataset = myDataset;
        mcontext = mycontext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout RldetailData;
        public LinearLayout RLdetailDate, LLmenu;
        public TextView icglobe, icleft, icright, tvleft, tvright, iccal, icmarker, icpow, icthumb, iccomment, icshare, btnDetailMenu;
        public TextView tvContentInsertDate, tvContentSubject, tvContentLocation, tvUsername, tvDuring, tvdetailcount, tvContent;
        public TextView tvLikeCount, tvCommentCount, tvShareCount, btnComment;
        public LinearLayout btnLookLeft, btnLookRight;
        public ImageView ivTopPhoto;

        public ViewHolder(View view) {
            super(view);

            RLdetailDate = (LinearLayout)view.findViewById(R.id.RLdetailDate);
            LLmenu = (LinearLayout)view.findViewById(R.id.LLmenu);
            RldetailData = (RelativeLayout)view.findViewById(R.id.RldetailData);
            icglobe = (TextView)view.findViewById(R.id.icglobe);
            iccal = (TextView)view.findViewById(R.id.iccal);
            icmarker = (TextView)view.findViewById(R.id.icmarker);
            icpow = (TextView)view.findViewById(R.id.icpow);
            icthumb = (TextView)view.findViewById(R.id.icthumb);
            iccomment = (TextView)view.findViewById(R.id.iccomment);
            icshare = (TextView)view.findViewById(R.id.icshare);
            icleft = (TextView)view.findViewById(R.id.icleft);
            icright = (TextView)view.findViewById(R.id.icright);
            tvleft = (TextView)view.findViewById(R.id.tvleft);
            tvright = (TextView)view.findViewById(R.id.tvright);
            btnDetailMenu = (TextView) view.findViewById(R.id.btnDetailMenu);
            ivTopPhoto = (ImageView) view.findViewById(R.id.ivTopPhoto);

            tvContentInsertDate = (TextView) view.findViewById(R.id.tvContentInsertDate);    //컨텐트입력날짜
            tvContentSubject = (TextView) view.findViewById(R.id.tvContentSubject);           //컨텐트 제목
            tvContentLocation = (TextView) view.findViewById(R.id.tvContentLocation);        //컨텐트 위치(마지막)
            tvUsername = (TextView) view.findViewById(R.id.tvUsername);                        //유저 네임
            tvDuring = (TextView) view.findViewById(R.id.tvDuring);                             //컨텐트 날짜첫날 ~ 날짜 끝날(2016.04.20 ~ 2016.06.20)
            tvContent = (TextView) view.findViewById(R.id.tvContent);                            //컨텐트 내용
            tvLikeCount = (TextView) view.findViewById(R.id.tvLikeCount);
            tvCommentCount = (TextView) view.findViewById(R.id.tvCommentCount);
            tvShareCount = (TextView) view.findViewById(R.id.tvShareCount);
            tvdetailcount = (TextView) view.findViewById(R.id.tvdetailcount);                    //디테일 수

            btnComment = (TextView) view.findViewById(R.id.btnComment);
            btnLookLeft = (LinearLayout) view.findViewById(R.id.btnLookLeft);
            btnLookRight = (LinearLayout) view.findViewById(R.id.btnLookRight);
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
        holder.icpow.setTypeface(fontAwesomeFont);
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

        //주소처리
        Photo ph = new Photo();
        ph.setPhoto_latitude(mDataset.get(position).getPhoto_latitude());
        ph.setPhoto_longitude(mDataset.get(position).getPhoto_longitude());
        GetCurrentAddress getAddress = new GetCurrentAddress();
        String address = getAddress.getAddress(ph);

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

        holder.btnDetailMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content_id = mDataset.get(position).getContent_id();
                ContentMenuClick(view);
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
                                        final String url = Value.contentURL+"/"+content_id;
                                        Thread th = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                DataOutputStream dos = null;
                                                HttpURLConnection conn = null;
                                                URL connectURL = null;
                                                try {
                                                    connectURL = new URL(url);

                                                    conn = (HttpURLConnection) connectURL.openConnection();
                                                    conn.setDoInput(true);
                                                    conn.setDoOutput(true);
                                                    conn.setUseCaches(false);
                                                    conn.setRequestMethod("DELETE");

                                                    int responseCode = conn.getResponseCode();
                                                    Log.i("responseCode","삭제 : "+responseCode);

                                                    switch (responseCode){
                                                        case HttpURLConnection.HTTP_OK:
                                                            Log.i("responseCode","삭제성공");
                                                            break;
                                                        default:
                                                            Log.i("responseCode","삭제실패 responseCode: " +responseCode);
                                                            break;
                                                    }

                                                } catch (MalformedURLException e) {
                                                    e.printStackTrace();
                                                } catch (ProtocolException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        th.start();
                                        try {
                                            th.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(mcontext,"삭제성공",Toast.LENGTH_SHORT).show();
                                        Intent dintent = new Intent(mcontext, MainActivity.class);
                                        dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //재사용 ㄴㄴ
                                        mcontext.startActivity(dintent);
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
                                        final String url = Value.contentURL+"/"+content_id+"/warning";
                                        Thread th = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                DataOutputStream dos = null;
                                                HttpURLConnection conn = null;
                                                URL connectURL = null;
                                                try {
                                                    connectURL = new URL(url);

                                                    conn = (HttpURLConnection) connectURL.openConnection();
                                                    conn.setDoInput(true);
                                                    conn.setDoOutput(true);
                                                    conn.setUseCaches(false);
                                                    conn.setRequestMethod("POST");

                                                    int responseCode = conn.getResponseCode();
                                                    Log.i("responseCode","신고 : "+responseCode);

                                                    switch (responseCode){
                                                        case HttpURLConnection.HTTP_OK:
                                                            Log.i("responseCode","신고성공");
                                                            break;
                                                        default:
                                                            Log.i("responseCode","신고실패 responseCode: " +responseCode);
                                                            break;
                                                    }

                                                } catch (MalformedURLException e) {
                                                    e.printStackTrace();
                                                } catch (ProtocolException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        th.start();
                                        try {
                                            th.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(mcontext,"신고성공",Toast.LENGTH_SHORT).show();
                                        Intent wintent = new Intent(mcontext, MainActivity.class);
                                        wintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //재사용 ㄴㄴ
                                        mcontext.startActivity(wintent);
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
}
