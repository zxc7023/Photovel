package com.photovel.setting;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.photovel.FontActivity;
import com.photovel.FontActivity2;
import com.photovel.MainActivity;
import com.photovel.NavigationItemSelected;
import com.photovel.R;
import com.photovel.content.CommentAdapter;
import com.photovel.content.ContentDetailListMain;
import com.photovel.content.ContentInsertMain;
import com.photovel.content.ContentSlideShowMain;
import com.photovel.content.ContentUpdateMain;
import com.photovel.content.GetCurrentAddress;
import com.photovel.content.MultiDrawable;
import com.photovel.content.MySupportMapFragment;
import com.photovel.content.PhotoGeoDegree;
import com.photovel.content.PhotoRealPathUtil;
import com.photovel.friend.FriendListMain;
import com.photovel.http.JsonConnection;
import com.photovel.http.MultipartConnection;
import com.photovel.http.Value;
import com.photovel.user.UserBitmapEncoding;
import com.vo.Comment;
import com.vo.Content;
import com.vo.ContentDetail;
import com.vo.Permission;
import com.vo.Photo;
import com.vo.User;

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

public class SettingMain extends FontActivity2 implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar;
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    private static final String TAG = "AppPermission";
    private String user_id, user_nick_name, user_password, user_phone, user_profile, path;
    private int user_friend_count;
    private TextView tvpen1, tvpen2, tvpen3, tvUserID, tvUserFriendCount;
    private LinearLayout userProfileUpdate;
    private EditText etUserNickName, etUserPassword, etUserPhone;
    private Button btnSettingUpdate, btnUserFriendGo;
    private Switch userfriendRecommendflag, userfriendSearchflag, userfeedflag;
    private boolean userfriendRecommendflagSwitch, userfriendSearchflagSwitch, userfeedflagSwitch;
    private CircularImageView userProfile;
    private Permission permission;
    private Bitmap bitmap2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main);
        checkPermission();

        // Adding Toolbar to the activity
        toolbar = (Toolbar) findViewById(R.id.settingToolbar);
        setSupportActionBar(toolbar);

        SharedPreferences get_to_eat = getSharedPreferences("loginInfo", MODE_PRIVATE);
        user_id = get_to_eat.getString("user_id","notFound");
        user_nick_name = get_to_eat.getString("user_nick_name","notFound");
        user_password = get_to_eat.getString("user_password","notFound");
        user_phone = get_to_eat.getString("user_phone","notFound");
        user_profile = get_to_eat.getString("user_profile","notFound");
        user_friend_count = get_to_eat.getInt("user_friend_count",0);

        tvpen1 = (TextView)findViewById(R.id.tvpen1);
        tvpen2 = (TextView)findViewById(R.id.tvpen2);
        tvpen3 = (TextView)findViewById(R.id.tvpen3);
        userProfileUpdate = (LinearLayout)findViewById(R.id.userProfileUpdate);
        tvUserID = (TextView)findViewById(R.id.tvUserID);
        tvUserFriendCount = (TextView)findViewById(R.id.tvUserFriendCount);
        etUserNickName = (EditText)findViewById(R.id.etUserNickName);
        etUserPassword = (EditText)findViewById(R.id.etUserPassword);
        etUserPhone = (EditText)findViewById(R.id.etUserPhone);
        btnSettingUpdate = (Button)findViewById(R.id.btnSettingUpdate);
        btnUserFriendGo = (Button)findViewById(R.id.btnUserFriendGo);

        userProfile = (CircularImageView)findViewById(R.id.userProfile);
        userfriendRecommendflag = (Switch)findViewById(R.id.userfriendRecommendflag);
        userfriendSearchflag = (Switch)findViewById(R.id.userfriendSearchflag);
        userfeedflag = (Switch)findViewById(R.id.userfeedflag);

        //imageView를 font로 바꿔주기
        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        tvpen1.setTypeface(fontAwesomeFont);
        tvpen2.setTypeface(fontAwesomeFont);
        tvpen3.setTypeface(fontAwesomeFont);

        tvUserID.setText(user_id);
        etUserNickName.setText(user_nick_name);
        etUserPassword.setText(user_password);
        etUserPhone.setText(user_phone);
        tvUserFriendCount.setText(String.valueOf(user_friend_count));
        if(!user_profile.equals("notFound")){
            UserBitmapEncoding ub = new UserBitmapEncoding();
            bitmap2 = ub.StringToBitMap(user_profile);
        }else{
            bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile_circle);
        }
        userProfile.setImageBitmap(bitmap2);

        //permission 객체 받아오기
        Thread permissionList = new Thread(){
            @Override
            public void run() {
                super.run();
                String responseData = JsonConnection.getConnection(Value.photovelURL+"/common/user/setting/"+user_id, "GET", null);
                permission = JSON.parseObject(responseData, Permission.class);
            }
        };
        permissionList.start();
        try {
            permissionList.join();  //모든처리 thread처리 기다리기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //친구추천 허용 스위치
        if(permission.getFriend_recom_flag()==1){
            userfriendRecommendflagSwitch = true;
            userfriendRecommendflag.setChecked(true);
        }else{
            userfriendRecommendflagSwitch = false;
            userfriendRecommendflag.setChecked(false);
        }
        userfriendRecommendflag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Log.i("ddd",""+isChecked);  //true,false
                userfriendRecommendflagSwitch = isChecked;
            }
        });

        //친구검색 허용 스위치
        if(permission.getFriend_search_flag()==1){
            userfriendSearchflagSwitch = true;
            userfriendSearchflag.setChecked(true);
        }else{
            userfriendSearchflagSwitch = false;
            userfriendSearchflag.setChecked(false);
        }
        userfriendSearchflag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userfriendSearchflagSwitch = isChecked;
            }
        });

        //피드백알림 허용 스위치
        if(permission.getFeed_flag()==1){
            userfeedflagSwitch = true;
            userfeedflag.setChecked(true);
        }else{
            userfeedflagSwitch = false;
            userfeedflag.setChecked(false);
        }
        userfeedflag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userfeedflagSwitch = isChecked;
            }
        });

        //toolbar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //메뉴 navigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        TextView btnContentInsert = (TextView)hView.findViewById(R.id.btnContentInsert);
        btnContentInsert.setTypeface(fontAwesomeFont);
        btnContentInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ContentInsertMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //준기오빠의 낮은 버전을 위해 인텐트할때 넣어주기
                getApplicationContext().startActivity(intent);
            }
        });
        TextView tvUserName = (TextView)hView.findViewById(R.id.tvUserName);
        tvUserName.setText(user_nick_name);
        CircularImageView userProfile = (CircularImageView)hView.findViewById(R.id.userProfile);
        if(!user_profile.equals("notFound")){
            UserBitmapEncoding ub = new UserBitmapEncoding();
            userProfile.setImageBitmap(ub.StringToBitMap(user_profile));
        }
        LinearLayout tvProfileUpdate = (LinearLayout)hView.findViewById(R.id.tvProfileUpdate);
        tvProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        userProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        btnSettingUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(SettingMain.this);
                alert_confirm.setMessage("정보를 수정하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                final JSONObject userObj = new JSONObject();
                                final JSONObject perObj = new JSONObject();
                                try {
                                    userObj.put("user_id",user_id);
                                    userObj.put("user_nick_name",etUserNickName.getText().toString());
                                    userObj.put("user_password",etUserPassword.getText().toString());
                                    userObj.put("user_phone",etUserPhone.getText().toString());
                                    Log.i("ddd",userObj.toString());

                                    perObj.put("user_id",user_id);
                                    if(userfriendRecommendflagSwitch==true) perObj.put("friend_recom_flag",1);
                                    else perObj.put("friend_recom_flag",0);
                                    if(userfriendSearchflagSwitch==true) perObj.put("friend_search_flag",1);
                                    else perObj.put("friend_search_flag",0);
                                    if(userfeedflagSwitch==true) perObj.put("feed_flag",1);
                                    else perObj.put("feed_flag",0);
                                    Log.i("ddd",perObj.toString());

                                    Thread settingUpdate =new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            MultipartConnection.getConnection(Value.photovelURL+"/common/user/setting", userObj, perObj, bitmap2);
                                        }
                                    });
                                    settingUpdate.start();
                                    try {
                                        settingUpdate.join();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //user정보값 수정해주기
                                SharedPreferences loginInfo = getSharedPreferences("loginInfo", MODE_PRIVATE);
                                SharedPreferences.Editor editor = loginInfo.edit();
                                editor.putString("user_nick_name",etUserNickName.getText().toString());
                                editor.putString("user_password",etUserPassword.getText().toString());
                                editor.putString("user_phone",etUserPhone.getText().toString());
                                UserBitmapEncoding ub = new UserBitmapEncoding();
                                String user_profile = ub.BitMapToString(bitmap2);
                                editor.putString("user_profile",user_profile);
                                editor.commit();

                                Toast.makeText(getApplicationContext(),"정보수정 성공",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), SettingMain.class);
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

        btnUserFriendGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FriendListMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                finish();
            }
        });
    }

    //requestCode == 1 -> 갤러리에서 사진불러오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();   //갤러리에서 선택한 dataUri 받아오기
                path = PhotoRealPathUtil.getRealPath(this, uri);
                Bitmap bitmap = resizeBitmap(path);
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                bitmap2 = rotateBitmap(bitmap, orientation);

                userProfile.setImageBitmap(bitmap2);
            }
        }
    }

    //사진 용량 줄이기
    private Bitmap resizeBitmap(String path) {
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 50000; //고정됨!!!!수정금지!!!!
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
        } catch (OutOfMemoryError e) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    //툴바 메뉴 클릭 시
    @Override
    @NonNull
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        NavigationItemSelected ns = new NavigationItemSelected();
        ns.selected(id, getApplicationContext());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.other_toolbar, menu);
        return true;
    }

    //Android BackButton EventListener
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            super.onBackPressed();
        }
    }
}