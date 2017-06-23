package com.photovel.content;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.photovel.FontActivity2;
import com.photovel.MainActivity;
import com.photovel.MainRecommendAdapter;
import com.photovel.R;
import com.vo.Comment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by EunD on 2017-06-23.
 */

public class CommentMain extends FontActivity2 {
    private BottomSheetBehavior bottomSheetBehavior;
    private RelativeLayout RlComment;
    private RecyclerView RVComment;
    private LinearLayoutManager mCommentLayoutManager;
    private CommentAdapter mCommentAdapter;
    private List<Comment> myCommentDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_main);
        RlComment = (RelativeLayout) findViewById(R.id.RlComment);

        bottomSheetBehavior = BottomSheetBehavior.from(RlComment);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        setTitle("1111");
                        break;

                    default:
                        setTitle("default");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        myCommentDataset = new ArrayList<Comment>();
        myCommentDataset.add(new Comment("test1"));
        myCommentDataset.add(new Comment("test2"));
        myCommentDataset.add(new Comment("test3"));
        myCommentDataset.add(new Comment("test1"));
        myCommentDataset.add(new Comment("test2"));
        myCommentDataset.add(new Comment("test3"));
        myCommentDataset.add(new Comment("test1"));
        myCommentDataset.add(new Comment("test2"));
        myCommentDataset.add(new Comment("test3"));
        myCommentDataset.add(new Comment("test1"));
        myCommentDataset.add(new Comment("test2"));
        myCommentDataset.add(new Comment("test3"));
        myCommentDataset.add(new Comment("test1"));
        myCommentDataset.add(new Comment("test2"));
        myCommentDataset.add(new Comment("test3"));
        myCommentDataset.add(new Comment("test1"));
        myCommentDataset.add(new Comment("test2"));
        myCommentDataset.add(new Comment("test3"));


        RVComment = (RecyclerView) findViewById(R.id.RVComment);
        RVComment.setHasFixedSize(true);
        RVComment.setNestedScrollingEnabled(false);
        mCommentLayoutManager = new LinearLayoutManager(this);
        RVComment.setLayoutManager(mCommentLayoutManager);
        mCommentAdapter = new CommentAdapter(myCommentDataset, CommentMain.this);
        RVComment.setAdapter(mCommentAdapter);

        Button btn_show = (Button) findViewById(R.id.btn_show);
        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        findViewById(R.id.btn_hidden).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        findViewById(R.id.btn_expanded).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Bottom Sheet state change.
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        } else {
            super.onBackPressed();
        }
    }
}
