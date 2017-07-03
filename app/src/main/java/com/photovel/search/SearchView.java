package com.photovel.search;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.database.MatrixCursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photovel.R;
import com.photovel.utils.AnimationUtil.AnimationUtil;
import com.vo.Content;

import java.util.List;

/**
 * @author Miguel Catalan Bañuls
 */

public class SearchView extends FrameLayout implements Filter.FilterListener {

    private static final String TAG = "ContentSearchView";
    //메뉴 xml의 아이템. searchItem 가져오기
    private MenuItem menuItem;
    //검색창이 열려있는지 여부
    private boolean isSearchOpen = false;
    //애니메이션 시간
    private int animationDuration;
    //포커스 지울 것인지 여부
    private boolean clearingFocus;

    //View 객체
    //검색 클릭시 나오는 전체 레이아웃
    private View searchLayout;
    //??
    private View tintView;
    //제안 목록이 나올 리스트 뷰
    private ListView suggestionsListView;
    //실제 검색어가 입력될 EditText
//    private EditText edtSearchSrc;
    private AutoCompleteTextView edtSearchSrc;

    //뒤로 가기 버튼
    private ImageButton btnBack;
    //검색창 비우기 버튼
    private ImageButton btnEmpty;
    //실질적인 검색창 부분
    private RelativeLayout searchView;

    //이전 쿼리 문자
    private CharSequence oldQuery;
    //사용자 입력 쿼리 문자
    private CharSequence usersQuey;
    //쿼리 텍스트 감시
    private OnQueryTextListener onQueryTextListener;
    //searchLayout이 열리고 닫히는 이벤트 감지
    private SearchViewListener searchViewListener;

    //해당 컨텍스트
    private Context context;

    //검색 결과를 보여줄 어댑터
    private SearchListAdapter searchListAdapter;

    //onSaveInstanceState() 구현 위해 텍스트 뷰에 저장되는 상태값
    private TextView.SavedState savedState;

    //submit
    private boolean submit = false;


    //데이터 집합 감시자
    private DataSetObserver dataSetObserver;







    //searchLayout이 열리고 닫히는 이벤트 감지하기 위한 임의의 인터페이스
    public interface SearchViewListener {
        void onSearchViewShown();

        void onSearchViewClosed();
    }

    //생성자
    public SearchView(@NonNull Context context) {
        super(context);
        this.context = context;
        initiateView();
        getAttrs(null, 0);
    }

    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initiateView();
        getAttrs(attrs, 0);
    }

    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs);
        this.context = context;
        //뷰 객체화
        initiateView();
        //뷰에 아이콘들 적용
        getAttrs(attrs, defStyleAttr);
    }



    //어댑터 등록 메소드
    public void setAdapter(SearchListAdapter adapter) {
        searchListAdapter = adapter;
        Log.i(TAG, "searchListAdapter.getCount()= " + searchListAdapter.getCount());
        suggestionsListView.setAdapter(searchListAdapter);
        startFilter(edtSearchSrc.getText());
    }



    public SearchListAdapter getAdapter() {
        return searchListAdapter;
    }


    @Override
    public void onFilterComplete(int count) {
        if (count > 0) {
            showSuggestions();
        } else {
            dismissSuggestions();
        }
    }

    private void getAttrs(AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SearchView, defStyleAttr, 0);

        initStyle(typedArray);
    }

    //menuItem(searchItem) 등록
    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showSearch();
                return true;
            }
        });
    }


////화면에 보일 뷰 객체 생성. 미리 만들어둔 xml 파일을 할당해주고 각각의 View를 설정
    private void initiateView() {
        LayoutInflater.from(context).inflate(R.layout.activity_search_view, this, true);
        searchLayout = findViewById(R.id.search_layout);
//
        searchView = (RelativeLayout) searchLayout.findViewById(R.id.top_search_view);
        suggestionsListView = (ListView) searchLayout.findViewById(R.id.suggestion_list);
//        edtSearchSrc = (EditText) searchLayout.findViewById(R.id.searchEditTextView);
        edtSearchSrc = (AutoCompleteTextView) findViewById(R.id.searchAutoCompleteTextView);
        btnBack = (ImageButton) searchLayout.findViewById(R.id.btn_action_back);
        btnEmpty = (ImageButton) searchLayout.findViewById(R.id.btn_action_empty);
        tintView = searchLayout.findViewById(R.id.transparent_view);
//
        edtSearchSrc.setOnClickListener(onClickListener);
        btnBack.setOnClickListener(onClickListener);
        btnEmpty.setOnClickListener(onClickListener);
        tintView.setOnClickListener(onClickListener);

        initSearchView();
//
        suggestionsListView.setVisibility(GONE);
        setAnimationDuration(AnimationUtil.ANIMATION_DURATION_MEDIUM);
    }


/////스타일 적용. attrs.xml 에 선언해둔 attribute를 이용하여 이를 각각의 View에 설정
    private void initStyle(TypedArray attrs){
        //R.styleable 은 res/attr.xml에서 정의한 것을 자바에서 구현
        if(attrs != null){
            if(attrs.hasValue(R.styleable.SearchView_searchBackground)){
                setBackground(attrs.getDrawable(R.styleable.SearchView_searchBackground));
            }

            if(attrs.hasValue(R.styleable.SearchView_searchSuggestionBackground)){
                setSuggestionBackground(attrs.getDrawable(R.styleable.SearchView_searchSuggestionBackground));
            }

            if(attrs.hasValue(R.styleable.SearchView_searchCloseIcon)){
                setCloseIcon(attrs.getDrawable(R.styleable.SearchView_searchCloseIcon));
            }

            if(attrs.hasValue(R.styleable.SearchView_searchBackIcon)){
                setBackIcon(attrs.getDrawable(R.styleable.SearchView_searchBackIcon));
            }

           /* if(attrs.hasValue(R.styleable.ContentSearchView_searchSuggestionIcon)){
                setSuggstionIcon(attrs.getDrawable(R.styleable.ContentSearchView_searchSuggestionIcon));
            }*/

            if(attrs.hasValue(R.styleable.SearchView_android_textColor)){
                setTextColor(attrs.getColor(R.styleable.SearchView_android_textColor, 0));
            }

            if(attrs.hasValue(R.styleable.SearchView_android_hint)){
                setHint(attrs.getString(R.styleable.SearchView_android_hint));
            }

            if(attrs.hasValue(R.styleable.SearchView_android_textColorHint)){
                setHintTextColor(attrs.getColor(R.styleable.SearchView_android_textColorHint, 0));
            }

            if(attrs.hasValue(R.styleable.SearchView_android_inputType)){
                setInputType(attrs.getInt(R.styleable.SearchView_android_inputType, EditorInfo.TYPE_NULL));
            }


        }

    }

///////////////////initStyle에서 호출하는 메소드들
@Override
public void setBackground(Drawable background) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        searchView.setBackground(background);
    } else {
        searchView.setBackgroundDrawable(background);
    }
}

    @Override
    public void setBackgroundColor(int color) {
        searchView.setBackgroundColor(color);
    }

    public void setTextColor(int color) {
        edtSearchSrc.setTextColor(color);
    }

    public void setHintTextColor(int color) {
        edtSearchSrc.setHintTextColor(color);
    }

    public void setHint(CharSequence hint) {
        edtSearchSrc.setHint(hint);
    }


    public void setCloseIcon(Drawable drawable) {
        btnEmpty.setImageDrawable(drawable);
    }

    public void setBackIcon(Drawable drawable) {
        btnBack.setImageDrawable(drawable);
    }

    /*public void setSuggestionIcon(Drawable drawable) {
        suggestionIcon = drawable;
    }*/

    public void setInputType(int inputType) {
        edtSearchSrc.setInputType(inputType);
    }

    public void setSuggestionBackground(Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            suggestionsListView.setBackground(background);
        } else {
            suggestionsListView.setBackgroundDrawable(background);
        }
    }
////////////////////////////////////////////////


    //searchView 실행
    private void initSearchView() {
        //enter key 클릭시 호출. 쿼리 submit
        edtSearchSrc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onSubmitQuery();
                return true;
            }
        });

        //EditText의 text가 변화할 때
        edtSearchSrc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //사용자가 입력한 쿼리
                usersQuey = s;
                //
                startFilter(s);
                SearchView.this.onTextChanged(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //searchView의 포커스가 변화할 때
        edtSearchSrc.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showKeyboard(edtSearchSrc);
                    showSuggestions();
                }
            }
        });
    }


    //클릭 리스너
    private final OnClickListener onClickListener = new OnClickListener() {

        public void onClick(View v) {
            if (v == btnBack) {
                closeSearch();
            } else if (v == btnEmpty) {
                edtSearchSrc.setText(null);
            } else if (v == edtSearchSrc) {
                showSuggestions();
            } else if (v == tintView) {
                closeSearch();
            }
        }
    };


    //EditText의 문자가 변할 때
    private void onTextChanged(CharSequence newText) {
        CharSequence text = edtSearchSrc.getText();
        usersQuey = text;
        //EditText가 비어있지 않은 경우
        boolean hasText = !TextUtils.isEmpty(text);
        if (hasText) {
            //텍스트가 입력되면 비우기 버튼 보여줌
            btnEmpty.setVisibility(VISIBLE);
        } else {
            //텍스트가 없으면 비우기 버튼 사라짐
            btnEmpty.setVisibility(GONE);
        }

        //텍스트 감지가 null이 아니고 새로운 쿼리와 기존의 쿠리가 같지 않을 경우
        if (onQueryTextListener != null && !TextUtils.equals(newText, oldQuery)) {
            //false -> SearchView가 가능한 제안을 보여준 경우, true -> 리스너가 handle 한 경우
            onQueryTextListener.onQueryTextChange(newText.toString());
        }
        oldQuery = newText.toString();
    }

    //query submit
    private void onSubmitQuery() {
        CharSequence query = edtSearchSrc.getText();
        //EditText에 입력된 쿼리가 null이 아니고 query 문자열의 길이가 1 이상일 때
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            //query text listener가 null이거나, 아직 사용자가 query를 submit하지 않았을 경우
            if (onQueryTextListener == null || !onQueryTextListener.onQueryTextSubmit(query.toString())) {
                //searchView 닫기
                closeSearch();
                //텍스트 비우기
                edtSearchSrc.setText(null);
            }
        }
    }

    //SearchView 클래스가 원래 가지는 setSuggestionAdapter 대신 구현
    public void setSuggestions(List<Content> suggestions, MatrixCursor matrixCursor, boolean flag) {
        if (suggestions != null && suggestions.size() > 0) {
            Log.i(TAG, "setSuggestions의 suggestions=" + suggestions);
            tintView.setVisibility(VISIBLE);
            searchListAdapter = new SearchListAdapter(context, matrixCursor, flag);
            setAdapter(searchListAdapter);

            setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    setQuery((String) searchListAdapter.getItem(position), submit);
                }
            });
        } else {
            Log.i(TAG, "setSuggestions의 suggestions 없음");
            tintView.setVisibility(GONE);
        }
    }

    //searchView가 포커스 됐을 때 검색 제안 목록 보이기
    public void showSuggestions() {
        //어댑터가 null이 아니고, 어댑터에 등록된 자료의 수가 1개 이상이고 검색 제안 목록의 visibility과 gone일 때
        if (searchListAdapter != null && searchListAdapter.getCount() > 0 && suggestionsListView.getVisibility() == GONE) {
            suggestionsListView.setVisibility(VISIBLE);
        }
    }

    //검색 제안 목록 없애기
    public void dismissSuggestions() {
        //검색 제안 목록의 visibility가 visible이면 gone으로 만든다
        if (suggestionsListView.getVisibility() == VISIBLE) {
            suggestionsListView.setVisibility(GONE);
        }
    }

    //검색 제안 목록에서 아이템 클릭할 경우
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        suggestionsListView.setOnItemClickListener(listener);
    }


    //쿼리 등록
    public void setQuery(CharSequence query, boolean submit) {
        edtSearchSrc.setText(query);
        if (query != null) {
            edtSearchSrc.setSelection(edtSearchSrc.length());
            Log.i(TAG, "setQuery의 query= " + query);
            usersQuey = query;
        }
        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery();
        }
    }


    //EditText뷰 객체 포커싱 변화될 때 requestFocus로 포커스 요청하고 입력 장치 보여줌
    public void showKeyboard(View view) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && view.hasFocus()) {
            view.clearFocus();
        }
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }



    /**
     * Submit the query as soon as the user clicks the item.
     *
     * @param submit submit state
     */
    public void setSubmitOnClick(boolean submit) {
        this.submit = submit;
    }


    //필터
    private void startFilter(CharSequence s) {
        if (searchListAdapter != null && searchListAdapter instanceof Filterable) {
//            ((Filterable) searchListAdapter).getFilter().filter(s, ContentSearchView.this);
            //이전에 실행되지 않은 필터 요청을 지우고 나중에 실행될 새로운 필터 요청
            (searchListAdapter).getFilter().filter(s, SearchView.this);
        }
    }




//////검색창 보이기, 닫기 및 검색 제안 목록 없애기
    //검색창 보이기
    public void showSearch() {
        showSearch(true);
    }

    public void showSearch(boolean animate) {
        if (isSearchOpen()) {
            return;
        }

        //Request Focus
        edtSearchSrc.setText(null);
        edtSearchSrc.requestFocus();

        if (animate) {
            setVisibleWithAnimation();

        } else {
            searchLayout.setVisibility(VISIBLE);
            if (searchViewListener != null) {
                searchViewListener.onSearchViewShown();
            }
        }
        //searchView가 열려있음 표시
        isSearchOpen = true;
    }


    //searchView 닫기
    public void closeSearch() {
        if (!isSearchOpen()) {
            return;
        }

        //EditText 비우기
        edtSearchSrc.setText(null);
        //검색 제안 목록 접기
        dismissSuggestions();
        //searchView의 focus 잃기
        clearFocus();

        //searchLayout 안보이게 하기
        searchLayout.setVisibility(GONE);
        if (searchViewListener != null) {
            searchViewListener.onSearchViewClosed();
        }

        //searchView가 닫혔음 표시
        isSearchOpen = false;
    }




    //searchView가 열렸는지 여부
    public boolean isSearchOpen() {
        return isSearchOpen;
    }



//////리스너 등록
    /**
     * Set this listener to listen to Query Change events.
     *
     * @param listener
     */
    public void setOnQueryTextListener(OnQueryTextListener listener) {
        onQueryTextListener = listener;
    }

    /**
     * Set this listener to listen to Search View open and close events
     *
     * @param listener
     */
    public void setOnSearchViewListener(SearchViewListener listener) {
        searchViewListener = listener;
    }



    /////AppBar 클릭시 애니메이션들
    private void setVisibleWithAnimation() {
        AnimationUtil.AnimationListener animationListener = new AnimationUtil.AnimationListener() {
            @Override
            public boolean onAnimationStart(View view) {
                return false;
            }

            @Override
            public boolean onAnimationEnd(View view) {
                if (searchViewListener != null) {
                    searchViewListener.onSearchViewShown();
                }
                return false;
            }

            @Override
            public boolean onAnimationCancel(View view) {
                return false;
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchLayout.setVisibility(View.VISIBLE);
            AnimationUtil.reveal(searchView, animationListener);

        } else {
            AnimationUtil.fadeInView(searchLayout, animationDuration, animationListener);
        }
    }





    //애니메이션 시간 설정
    public void setAnimationDuration(int duration) {
        animationDuration = duration;
    }

    public interface OnQueryTextListener {

        /**
         * Called when the user submits the query. This could be due to a key press on the
         * keyboard or due to pressing a submit button.
         * The listener can override the standard behavior by returning true
         * to indicate that it has handled the submit request. Otherwise return false to
         * let the SearchView handle the submission by launching any associated intent.
         *
         * @param query the query text that is to be submitted
         * @return true if the query has been handled by the listener, false to let the
         * SearchView perform the default action.
         */
        boolean onQueryTextSubmit(String query);

        /**
         * Called when the query text is changed by the user.
         *
         * @param newText the new content of the query text field.
         * @return false if the SearchView should perform the default action of showing any
         * suggestions if available, true if the action was handled by the listener.
         */
        boolean onQueryTextChange(String newText);
    }
}

