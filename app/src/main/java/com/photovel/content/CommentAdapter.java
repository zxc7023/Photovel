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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.photovel.MainActivity;
import com.photovel.R;
import com.photovel.http.Value;
import com.vo.Comment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
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
        public TextView tvComment, btnCommentMenu, tvUsername, tvCommentDate, btnCommentSubmit, tvUsername_update, tvCommentDate_update;
        public LinearLayout LLmenu, llselect, llupdate;
        public EditText etComment;

        public ViewHolder(View view) {
            super(view);
            tvComment = (TextView)view.findViewById(R.id.tvComment);
            btnCommentMenu = (TextView)view.findViewById(R.id.btnCommentMenu);
            LLmenu = (LinearLayout)view.findViewById(R.id.LLmenu);
            llselect = (LinearLayout)view.findViewById(R.id.llselect);
            llupdate = (LinearLayout)view.findViewById(R.id.llupdate);
            tvUsername = (TextView)view.findViewById(R.id.tvUsername);
            tvUsername_update = (TextView)view.findViewById(R.id.tvUsername_update);
            tvCommentDate = (TextView)view.findViewById(R.id.tvCommentDate);
            tvCommentDate_update = (TextView)view.findViewById(R.id.tvCommentDate_update);
            btnCommentSubmit = (TextView)view.findViewById(R.id.btnCommentSubmit);
            etComment = (EditText) view.findViewById(R.id.etComment);
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
        holder.tvUsername.setText(mDataset.get(position).getUser().getUser_nick_name());
        holder.tvUsername_update.setText(mDataset.get(position).getUser().getUser_nick_name());
        holder.tvCommentDate.setText(new SimpleDateFormat("yyyy.MM.dd").format(mDataset.get(position).getComment_date()));
        holder.tvCommentDate_update.setText(new SimpleDateFormat("yyyy.MM.dd").format(mDataset.get(position).getComment_date()));
        holder.tvComment.setText(mDataset.get(position).getComment_content());
        holder.etComment.setText(mDataset.get(position).getComment_content());

        holder.LLmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ca = new CommentAdapter();
                ca.setHolder(holder);
                ca.setPosition(position);

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
        final String id = "leeej9201@gmail.com";
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_update:
                        if(mDataset.get(ca.getPosition()).getUser().getUser_id().equals(id)){
                            ca.getHolder().llupdate.setVisibility(View.VISIBLE);
                            ca.getHolder().llselect.setVisibility(View.GONE);
                            ca.getHolder().LLmenu.setVisibility(View.INVISIBLE);
                            ca.getHolder().btnCommentSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final JSONObject comment = new JSONObject();
                                    try {
                                        JSONObject user = new JSONObject();
                                        user.put("user_id", id);
                                        comment.put("content_id", mDataset.get(ca.getPosition()).getContent_id());
                                        comment.put("comment_id", mDataset.get(ca.getPosition()).getComment_id());
                                        comment.put("comment_content", ca.getHolder().etComment.getText());
                                        comment.put("user",user);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.i("1. comment", comment.toString());

                                    final String url = Value.contentURL+"/"+mDataset.get(ca.getPosition()).getContent_id()+"/"+mDataset.get(ca.getPosition()).getComment_id();
                                    Thread th = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            HttpURLConnection conn = null;
                                            OutputStream dos = null;
                                            try {
                                                URL connectURL = new URL(url);
                                                Log.i("2. comment", url);
                                                conn = (HttpURLConnection) connectURL.openConnection();
                                                conn.setDoOutput(true);
                                                conn.setDoInput(true);
                                                conn.setRequestMethod("POST");
                                                conn.setRequestProperty("Connection", "Keep-Alive");
                                                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                                                dos = conn.getOutputStream();
                                                dos.write(comment.toString().getBytes());
                                                dos.flush();
                                                int responseCode = conn.getResponseCode();
                                                Log.i("3. comment", responseCode + "");

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
                                    //Toast.makeText(getApplicationContext(),"댓글달기성공",Toast.LENGTH_SHORT).show();
                                    Intent cintent = new Intent(mcontext, ContentDetailListMain.class);
                                    cintent.putExtra("content_id", mDataset.get(ca.getPosition()).getContent_id());
                                    cintent.putExtra("comment_insert", 1);
                                    cintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //재사용 ㄴㄴ
                                    mcontext.startActivity(cintent);
                                    Toast.makeText(mcontext,"수정완료!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    case R.id.action_delete:
                        if(mDataset.get(ca.getPosition()).getUser().getUser_id().equals(id)) {
                            AlertDialog.Builder dalert_confirm = new AlertDialog.Builder(mcontext);
                            dalert_confirm.setMessage("정말 댓글을 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            final String url = Value.contentURL+"/"+mDataset.get(ca.getPosition()).getContent_id()+"/"+mDataset.get(ca.getPosition()).getComment_id();
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
                                                        Log.i("comment", "삭제 : " + responseCode);

                                                        switch (responseCode) {
                                                            case HttpURLConnection.HTTP_OK:
                                                                Log.i("comment", "삭제성공");
                                                                break;
                                                            default:
                                                                Log.i("comment", "삭제실패 responseCode: " + responseCode);
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
                                            Toast.makeText(mcontext, "삭제성공", Toast.LENGTH_SHORT).show();
                                            Intent cintent = new Intent(mcontext, ContentDetailListMain.class);
                                            cintent.putExtra("content_id", mDataset.get(ca.getPosition()).getContent_id());
                                            cintent.putExtra("comment_insert", 1);
                                            cintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //재사용 ㄴㄴ
                                            mcontext.startActivity(cintent);
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