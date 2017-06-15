package com.kitri.photovel.content;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.kitri.photovel.R;
import com.kitri.vo.Content;
import com.kitri.vo.ContentDetail;
import com.kitri.vo.Photo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;

public class PhotoMain extends Activity {
    private Button btnSort, btnAllDelete, btnPhotoSave;
    private FloatingActionButton  btnAddPhots, btnTop;
    private String path;
    private ExifInterface exif;
    private static final String TAG = "AppPermission";
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;

    private RecyclerView mRecyclerView;
    private PhotoAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ContentDetail> myDataset;
    private String address;

    private EditText contentSubject, contentText;
    private int flag = 0;
    private Switch swPrivate;
    private boolean flagSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_main);
        checkPermission();

        Intent intent = new Intent(this.getIntent());
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        contentSubject = (EditText) findViewById(R.id.contentSubject);
        contentText = (EditText) findViewById(R.id.contentText);

        //키보드 컨트롤
        /*CoordinatorLayout mainLayout = (CoordinatorLayout)findViewById(R.id.main_content);
        InputMethodManager im = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        final SoftKeyboard softKeyboardDecector = new SoftKeyboard(this);
        addContentView(softKeyboardDecector, new FrameLayout.LayoutParams(-1, -1));
        softKeyboardDecector.setOnShownKeyboard(new SoftKeyboard.OnShownKeyboardListener() {
            @Override
            public void onShowSoftKeyboard() {
                btnTop.hide();
                //키보드 등장할 때
            }
        });
        softKeyboardDecector.setOnHiddenKeyboard(new SoftKeyboard.OnHiddenKeyboardListener() {
            @Override
            public void onHiddenSoftKeyboard() {
                // 키보드 사라질 때
                btnTop.show();
            }
        });*/

        //recycleview사용선언
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        myDataset = new ArrayList<>();
        mAdapter = new PhotoAdapter(myDataset, PhotoMain.this);
        mRecyclerView.setAdapter(mAdapter);


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
        btnAllDelete = (Button)findViewById(R.id.btnAllDelete);
        btnAllDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //스위치버튼
        swPrivate = (Switch)findViewById(R.id.swPrivate);
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
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(PhotoMain.this);
                alert_confirm.setMessage("글을 올리시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Log.i("ddd","-------content-------");
                                Log.i("ddd","content_id : "+1);
                                Log.i("ddd","content_subject : "+contentSubject.getText());
                                Log.i("ddd","content : "+contentText.getText());
                                Log.i("ddd","content_written_date : "+new Date().toString());
                                Log.i("ddd","content_private_flag : "+flagSwitch);  //true,false
                                for(int i=0;i<myDataset.size();i++){
                                    Log.i("ddd","-------content_detail-------");
                                    Log.i("ddd","content_detail_id : "+i+1);
                                    Log.i("ddd","datail_content : "+myDataset.get(i).getDetail_content());
                                    Log.i("ddd","-------photo-------");
                                    Log.i("ddd","photo_file_name : "+1+"_"+(i+1));
                                    Log.i("ddd","photo_date : "+myDataset.get(i).getPhoto().getPhoto_date());
                                    Log.i("ddd","photo_latitude : "+myDataset.get(i).getPhoto().getPhoto_latitude());
                                    Log.i("ddd","photo_longitude : "+myDataset.get(i).getPhoto().getPhoto_longitude());
                                    myDataset.get(mAdapter.pa2.getPosition()).getPhoto().setPhoto_top_flag(1);
                                    Log.i("ddd","photo_top_flag : "+myDataset.get(i).getPhoto().getPhoto_top_flag());
                                    Log.i("ddd","-------------------");
                                }

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

                    setSubject(photos[0].getAddress().toString());

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

                    setSubject(photo.getAddress().toString());

                    myDataset.add(new ContentDetail(photo));
                    Collections.sort(myDataset);
                    mAdapter.notifyDataSetChanged();
                }

            }else if(requestCode==2){
                mAdapter.photoGoogleMapResult(data);
            }
        }
    }

    //처음일때 한번만 제목 정해주기
    private void setSubject(String address){
        if(flag == 0){
            flag = 1;
            //address중에서 도시만 빼서 넣어줘야함
            if(address.equals("주소미발견")){
                contentSubject.setText("땡땡으로의 여행");
            }else{
                contentSubject.setText(address+"로의 여행");
            }
        }
    }

    //사진선택
    private Photo selectPhoto(Uri uri){
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
                address = getCurrentAddress(photo); //주소로 바꿔주기
                photo.setAddress(address);
            }else {
                photo.setAddress("주소미발견");
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
            Bitmap bitmap = getBitmap(path);

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
    private Bitmap getBitmap(String path) {
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 700000; //고정됨!!!!수정금지!!!!
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

    //위도,경도 -> address
    public String getCurrentAddress(Photo photo){
        // GPS를 주소로 변환후 반환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(photo.getPhoto_latitude(), photo.getPhoto_longitude(), 1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    //back버튼 설정
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(PhotoMain.this);
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

    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
        }
    }*/
}