package com.photovel.search;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.photovel.R;
import com.photovel.content.ContentDetailListMain;
import com.photovel.user.UserBitmapEncoding;

public class SearchListAdapter extends CursorAdapter implements Filterable {

    private Context context;
    private Cursor cursor;
    private boolean autoRequery;
    private int flags;
    private int indexNum;
    //비트맵 정보를 바이트로 바꿔서 비트맵 팩토리로 비트맵 재생성
    private byte[] photoBytes;
    private ViewGroup parent;

    public ImageView userProfile;
    public TextView contentSubject, userNickname;
    public LinearLayout llmainSubject;

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
        View v = inflater.inflate(R.layout.activity_search_view_adapter, parent, false);
//        return View.inflate(context, R.layout.activity_main_new_list_adapter, parent);
        return v;
    }

    //커서가 가리키는 데이터에 뷰를 묶는다
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        contentSubject = (TextView)view.findViewById(R.id.contentSubject);
        userNickname = (TextView)view.findViewById(R.id.userNickname);
        userProfile = (ImageView)view.findViewById(R.id.userProfile);
        llmainSubject = (LinearLayout) view.findViewById(R.id.llmainSubject);

        UserBitmapEncoding ub = new UserBitmapEncoding();
        Bitmap user_bitmap = ub.StringToBitMap(cursor.getString(cursor.getColumnIndex("user_bitmap")));

        userNickname.setText(cursor.getString(cursor.getColumnIndex("user_nick_name")));
        userProfile.setImageBitmap(user_bitmap);
        contentSubject.setText(cursor.getString(cursor.getColumnIndex("content_subject")));

        //사진클릭
        llmainSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContentDetailListMain.class);
                intent.putExtra("content_id", Integer.parseInt(cursor.getString(cursor.getColumnIndex("content_id"))));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void changeCursor(Cursor cursor) {
        cursor = swapCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
    }
}