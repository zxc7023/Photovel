package com.photovel.content;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.photovel.R;
import com.photovel.FontActivity;
import com.photovel.http.JsonConnection;
import com.photovel.http.MultipartConnection;
import com.photovel.http.Value;
import com.vo.Content;
import com.vo.ContentDetail;
import com.vo.Photo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ContentUpdateMain extends FontActivity {
    private Button btnSort, btnPhotoSave;
    private TextView btnBack;
    private TextView tvTitle;
    private FloatingActionButton  btnAddPhots, btnTop;
    private String path;
    private ExifInterface exif;
    private static final String TAG = "AppPermission";
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;

    private RecyclerView mRecyclerView;
    private ContentUpdateAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ContentDetail> myDataset, originData, sumData;
    private String address;

    private EditText contentSubject, contentText;
    private Switch swPrivate;
    private boolean flagSwitch;
    private int content_id=-1;

    private Content content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_insert_main);
        checkPermission();

        Intent intent = getIntent();
        content_id = intent.getIntExtra("content_id",-1);
        if(content_id==-1){
            Log.i("content_id","content_id 못받아옴!!!");
        }else{
            Log.i("content_id","update_content_id : "+content_id);
        }

        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvTitle.setText("여행 편집");

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        contentSubject = (EditText) findViewById(R.id.contentSubject);
        contentText = (EditText) findViewById(R.id.contentText);
        contentText.addTextChangedListener(new TextWatcher() {  //5줄로 제한하기
            String previousString = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                previousString= s.toString();
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (contentText.getLineCount() >= 6)
                {
                    contentText.setText(previousString);
                    contentText.setSelection(contentText.length());
                }
            }
        });

        btnBack = (TextView)findViewById(R.id.btnBack);

        //imageView를 font로 바꿔주기
        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        btnBack.setTypeface(fontAwesomeFont);

        //Content, ContentDetail 객체 받아오기
        Thread contentList = new Thread(){
            @Override
            public void run() {
                super.run();
                String responseData = JsonConnection.getConnection(Value.contentURL+"/"+content_id, "GET", null);
                content = JSON.parseObject(responseData, Content.class);
                myDataset = content.getDetails();
            }
        };
        contentList.start();
        try {
            contentList.join();  //모든처리 thread처리 기다리기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //ContentDetail bitmap 받아오기
        JsonConnection.setBitmap(myDataset, Value.contentPhotoURL);
        JsonConnection.setBitmap(originData, Value.contentPhotoURL);

        //content정보 추가하기
        contentSubject.setText(content.getContent_subject());
        contentText.setText(content.getContent());

        //recycleview사용선언
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new ContentUpdateAdapter(myDataset, ContentUpdateMain.this);
        mRecyclerView.setAdapter(mAdapter);

        if(myDataset.size()==0) {
            mRecyclerView.setBackgroundResource(R.drawable.bg_content_insert_main);
        }

        //top버튼
        btnTop = (FloatingActionButton) findViewById(R.id.btnTop);
        final NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nestedScrollView);
        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {   //스크롤내리면 보이게
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(oldScrollY > 200){
                    btnTop.show();
                }else{
                    btnTop.hide();
                }
            }
        });
        btnTop.setOnClickListener(new View.OnClickListener() {  //top으로 이동
            @Override
            public void onClick(View view) {
                nsv.fullScroll(View.FOCUS_UP);
            }
        });

        //취소버튼
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //스위치버튼
        swPrivate = (Switch)findViewById(R.id.swPrivate);
        if(content.getContent_private_flag().equals("F")){
            swPrivate.setChecked(true);
        }else{
            swPrivate.setChecked(false);
        }
        swPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Log.i("ddd",""+isChecked);  //true,false
                flagSwitch = isChecked;
            }
        });

        //갤러리에서 사진받아와서 그 사진 add
        btnAddPhots = (FloatingActionButton) findViewById(R.id.btnAddPhots);
        btnAddPhots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                //사진을 여러개 선택할수 있도록 한다
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                //공통 갤러리이기 때문에 바꾸면 안됨!!
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Toast.makeText(getApplicationContext(),"복수 선택은 길게 꾹눌러 주숑",Toast.LENGTH_LONG).show();
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
            }
        });

        //정렬버튼
        btnSort = (Button)findViewById(R.id.btnSort);
        btnSort.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Collections.sort(myDataset);    //정렬해줘야함
                mAdapter.notifyDataSetChanged();
            }
        });

        //저장버튼
        btnPhotoSave = (Button)findViewById(R.id.btnPhotoSave);
        btnPhotoSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(ContentUpdateMain.this);
                alert_confirm.setMessage("글을 올리시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //라디오버튼 선택안했을때
                                if(mAdapter.pa2==null || mAdapter.pa2.getHolder().radioG.getCheckedRadioButtonId()==-1){
                                    Toast.makeText(getApplicationContext(),"대표사진을 다시 선택해주세요!",Toast.LENGTH_LONG).show();
                                    return;
                                }

                                //완료되는 Content처리
                                Content resultContent = content;
                                resultContent.setContent_subject(contentSubject.getText().toString());
                                if(contentText.getText().toString().equals("")){
                                    resultContent.setContent("");
                                }else{
                                    resultContent.setContent(contentText.getText().toString());
                                }
                                resultContent.setContent_written_date(new Date());
                                if(flagSwitch==true){
                                    resultContent.setContent_private_flag("T");
                                }else{
                                    resultContent.setContent_private_flag("F");
                                }

                                //완료되는 Content_detail처리
                                for(int j=0; j<originData.size(); j++){
                                    //원재 DB에있는것인데 지워진것인지 아닌지 판단
                                    originData.get(j).setDetail_delete_status("T");
                                }
                                for(int i=0;i<myDataset.size();i++){
                                    if(myDataset.get(i).getPhoto().getPhoto_latitude()==0 && myDataset.get(i).getPhoto().getPhoto_longitude()==0){
                                        //위치 없을때
                                        Toast.makeText(getApplicationContext(),"위치가 지정되어있지 않은 사진이 있습니다 다시 확인해 주세요!",Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    if(myDataset.get(i).getDetail_content()==null){
                                        myDataset.get(i).setDetail_content("");
                                    }
                                    if(myDataset.get(i).getContent_detail_id()==null){
                                        myDataset.get(i).setContent_detail_id("");
                                    }else{
                                        //원래 DB에있는것인데 지워진것인지 아닌지 판단2
                                        myDataset.get(i).setDetail_delete_status("F");
                                        for(int j=0; j<originData.size(); j++){
                                            if(myDataset.get(i).getContent_detail_id() == originData.get(j).getContent_detail_id()){
                                                originData.get(j).setDetail_delete_status("F");
                                            }
                                        }
                                    }
                                    if(myDataset.get(i).getPhoto().getPhoto_top_flag()==1){ //원래 photo_top_flag없애기
                                        myDataset.get(i).getPhoto().setPhoto_top_flag(0);
                                    }
                                }
                                myDataset.get(mAdapter.pa2.getPosition()).getPhoto().setPhoto_top_flag(1);  //바뀐 photo_top_flag설정

                                //sumData에 합치기
                               sumData = new ArrayList<ContentDetail>();
                                for(int i=0; i<myDataset.size(); i++){
                                    sumData.add(myDataset.get(i));
                                }
                                for(int j=0; j<originData.size(); j++){
                                    if(originData.get(j).getDetail_delete_status().equals("T")){
                                        sumData.add(originData.get(j));
                                    }
                                }

                                resultContent.setDetails(sumData);

                                //json 처리
                                final JSONObject obj = new JSONObject();  //결과 json
                                try {
                                    JSONArray jArray = new JSONArray();
                                    for(int i=0; i<resultContent.getDetails().size(); i++){
                                        JSONObject sObject = new JSONObject();  //배열 내에 들어갈 json
                                        sObject.put("content_id",resultContent.getDetails().get(i).getContent_id());
                                        sObject.put("content_detail_id",resultContent.getDetails().get(i).getContent_detail_id());
                                        sObject.put("detail_content",resultContent.getDetails().get(i).getDetail_content());
                                        sObject.put("detail_delete_status",resultContent.getDetails().get(i).getDetail_delete_status());
                                        JSONObject sbObject = new JSONObject();
                                        sbObject.put("content_id",resultContent.getDetails().get(i).getPhoto().getContent_id());
                                        sbObject.put("content_detail_id",resultContent.getDetails().get(i).getPhoto().getContent_detail_id());
                                        sbObject.put("photo_date",new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(resultContent.getDetails().get(i).getPhoto().getPhoto_date()));
                                        sbObject.put("photo_latitude",resultContent.getDetails().get(i).getPhoto().getPhoto_latitude());
                                        sbObject.put("photo_longitude",resultContent.getDetails().get(i).getPhoto().getPhoto_longitude());
                                        sbObject.put("photo_top_flag",resultContent.getDetails().get(i).getPhoto().getPhoto_top_flag());
                                        sObject.put("photo",sbObject);
                                        jArray.put(sObject);
                                    }
                                    obj.put("content_id",resultContent.getContent_id());
                                    obj.put("content_subject",resultContent.getContent_subject());
                                    obj.put("content",resultContent.getContent());
                                    obj.put("content_written_date",new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(resultContent.getContent_written_date()));
                                    obj.put("content_private_flag",resultContent.getContent_private_flag());
                                    obj.put("details",jArray);

                                    Log.i("ddd",obj.toString());

                                    //Bitmap처리
                                    final List<Bitmap> tmp = new ArrayList<Bitmap>();
                                    for(int i=0; i<sumData.size(); i++){
                                        tmp.add(sumData.get(i).getPhoto().getBitmap());
                                    }

                                    Thread contentUpdate =new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String responseData = MultipartConnection.getConnection(Value.contentURL+"/"+content_id, obj, tmp);
                                            content_id = Integer.parseInt(responseData);
                                        }
                                    });
                                    contentUpdate.start();
                                    try {
                                        contentUpdate.join();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Intent intent = new Intent(getApplicationContext(), ContentDetailListMain.class);
                                Log.i("content_id","insert_content_id : " + content_id);
                                intent.putExtra("content_id", content_id);
                                getApplicationContext().startActivity(intent);
                                finish();

                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });
    }

    //requestCode == 1 -> 갤러리에서 사진불러오기
    //requestCode == 2 -> googlemap에서받아온 주소 apapter로 전달
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && resultCode == RESULT_OK) {
            if (requestCode == 1) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {     //갤러리에서 사진 여러개 클릭시
                    int  cnt=clipData.getItemCount();
                    if(clipData.getItemCount()>20){     //20이상 클릭시 제한주기
                        Toast.makeText(getApplicationContext(),"사진은 20개까지만 선택할 수 있습니다.",Toast.LENGTH_SHORT).show();
                        cnt=20;
                    }
                    Photo[] photos = new Photo[cnt];

                    for (int i = 0; i < cnt; i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        Log.i("uri", uri.toString());
                        photos[i] = selectPhoto(uri);
                    }

                    //사진cnt 만큼 recyclerview추가
                    for (int i = 0; i < cnt; i++) {
                        photos[i].getPhoto_file_name();
                        myDataset.add(new ContentDetail(photos[i]));
                        Collections.sort(myDataset);    //정렬해줘야함
                        mAdapter.notifyDataSetChanged();
                    }

                }else{  //갤러리에서 사진 한개 클릭시
                    Uri uri = data.getData();   //갤러리에서 선택한 dataUri 받아오기
                    Photo photo = selectPhoto(uri);

                    myDataset.add(new ContentDetail(photo));
                    Collections.sort(myDataset);
                    mAdapter.notifyDataSetChanged();
                }

            }else if(requestCode==2){
                mAdapter.photoGoogleMapResult(data);
            }
        }
    }

    //사진선택
    private Photo selectPhoto(Uri uri){
        mRecyclerView.setBackgroundResource(R.color.bgGrey);
        path = PhotoRealPathUtil.getRealPath(this, uri);
        //사진의 정보 받받
        ExifInterface orig = null;
        Photo photo = new Photo();
        try {
            orig = new ExifInterface(path);
            if(orig.getAttribute(ExifInterface.TAG_GPS_LATITUDE)!=null){ //위도, 경도
                PhotoGeoDegree geo = new PhotoGeoDegree(orig);
                photo.setPhoto_latitude(geo.getLatitude());
                photo.setPhoto_longitude(geo.getLongitude());
                GetCurrentAddress getAddress = new GetCurrentAddress();
                address = getAddress.getAddress(geo.getLatitude(), geo.getLongitude()); //주소로 바꿔주기
                photo.setAddress(address);
            }else {
                photo.setAddress("주소 미확인");
            }

            if(orig.getAttribute(ExifInterface.TAG_DATETIME) !=null){ //시간날짜
                String datetime = orig.getAttribute(ExifInterface.TAG_DATETIME);
                Date date = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").parse(datetime);
                photo.setPhoto_date(date);
            }else{
                photo.setPhoto_date(new Date());
            }

            exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED); //회전률
            //사진보여주기 전에 용량처리해주기
            Bitmap bitmap = resizeBitmap(path);

            //사진보여주기 전에 회전처리해주기
            photo.setBitmap(rotateBitmap(bitmap,orientation));
            return photo;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    //사진 용량 줄이기
    private Bitmap resizeBitmap(String path) {
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 200000; //고정됨!!!!수정금지!!!!
            in = new FileInputStream(path);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }

            Bitmap b = null;
            in = new FileInputStream(path);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();

                double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            return b;
        } catch (IOException e) {
            Log.e("TAG", e.getMessage(), e);
            return null;
        }

    }

    //사진 회전
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    //권한 설정
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        Log.i(TAG, "CheckPermission : " + checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_STORAGE);

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

        } else {
            Log.e(TAG, "permission deny");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED
                        && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission always deny");
                    finish();

                }
                break;
        }
    }

    //back버튼 설정
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(ContentUpdateMain.this);
        alert_confirm.setMessage("작성하신 모든 작업을 취소 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
        //super.onBackPressed();
    }
}