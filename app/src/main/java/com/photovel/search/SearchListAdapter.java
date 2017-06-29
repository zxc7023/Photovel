package com.photovel.search;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photovel.R;

//import android.support.v4.widget.CursorAdapter;

//import android.widget.CursorAdapter;

public class SearchListAdapter extends CursorAdapter implements Filterable {

    private Context context;
    private Cursor cursor;
    private boolean autoRequery;
    private int flags;
    private int indexNum;
    //비트맵 정보를 바이트로 바꿔서 비트맵 팩토리로 비트맵 재생성
    private byte[] photoBytes;
    private ViewGroup parent;

    public RelativeLayout RlmainTop;
    public ImageView main_ivphoto;
    public TextView contentSubject, userNickname;
    public TextView main_icthumb, main_iccomment, main_icshare;
    public TextView thumbCount, commentCount, shareCount;
    public LinearLayout llthumb, llcomment, llshare;

    public SearchListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.context = context;
        this.cursor = c;
        this.autoRequery = autoRequery;
    }

    public SearchListAdapter(Context context, Cursor c, boolean autoRequery, ViewGroup parent) {
        super(context, c, autoRequery);
        this.context = context;
        this.cursor = c;
        this.autoRequery = autoRequery;
        this.parent = parent;
    }

    public SearchListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
        this.cursor = c;
        this.flags = flags;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.activity_main_new_list_adapter, parent, false);
//        return View.inflate(context, R.layout.activity_main_new_list_adapter, parent);
        return v;
    }

    //커서가 가리키는 데이터에 뷰를 묶는다
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        RlmainTop = (RelativeLayout)view.findViewById(R.id.RlmainTop);
        //이미 존재하는 뷰 객체의 main_ivphoto에 커서가 가리키는 정보의 이미지 저장
        main_ivphoto = (ImageView)view.findViewById(R.id.main_ivphoto);

        //photoBytes = cursor.getBlob(cursor.getColumnIndex("content_subject"));
        //photoBytes = cursor.getBlob(cursor.getColumnIndex("main_ivphoto"));
        if(photoBytes != null){
            Bitmap mainIvPhoto = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
            main_ivphoto.setImageBitmap(mainIvPhoto);
        }

        contentSubject = (TextView)view.findViewById(R.id.contentSubject);
        contentSubject.setText(cursor.getString(cursor.getColumnIndex("content_subject")));
        userNickname = (TextView)view.findViewById(R.id.userNickname);
        main_icthumb = (TextView)view.findViewById(R.id.main_icthumb);
        main_iccomment = (TextView)view.findViewById(R.id.main_iccomment);
        main_icshare = (TextView)view.findViewById(R.id.main_icshare);
        thumbCount = (TextView)view.findViewById(R.id.thumbCount);
        commentCount = (TextView)view.findViewById(R.id.commentCount);
        shareCount = (TextView)view.findViewById(R.id.shareCount);
        llthumb = (LinearLayout)view.findViewById(R.id.llthumb);//좋아요 레이아웃
        llcomment = (LinearLayout)view.findViewById(R.id.llcomment);//코멘트 레이아웃
        llshare = (LinearLayout)view.findViewById(R.id.llshare);//공유 레이아웃

        Typeface fontAwesomeFont = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");
        main_icthumb.setTypeface(fontAwesomeFont);
        main_iccomment.setTypeface(fontAwesomeFont);
        main_icshare.setTypeface(fontAwesomeFont);

    }

    @Override
    public void changeCursor(Cursor cursor) {
        cursor = swapCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
    }
}