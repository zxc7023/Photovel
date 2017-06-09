package com.kitri.photovel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap;
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
import com.kitri.vo.MarkerInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Junki on 2017-05-31.
 */

public class ClusterTest extends FragmentActivity
        implements OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<MarkerInfo>,
        ClusterManager.OnClusterInfoWindowClickListener<MarkerInfo>,
        ClusterManager.OnClusterItemClickListener<MarkerInfo>,
        ClusterManager.OnClusterItemInfoWindowClickListener<MarkerInfo> {



    private ClusterManager<MarkerInfo> cm;
    private Random mRandom = new Random(1984);
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_test);

        //프래그먼트에 지도를 보여주기위해 싱크
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap =googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.50111,-0.122777775), 9.5f));
        startDemo();


    }

    //클러스터된 아이템을 클릭되었을때의 이벤트
    @Override
    public boolean onClusterClick(Cluster<MarkerInfo> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().name;
        Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    protected void startDemo() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.50111,-0.122777775), 9.5f));

        cm = new ClusterManager<MarkerInfo>(this, mMap);
        cm.setRenderer(new MarkerRenderer());

        mMap.setOnCameraIdleListener(cm);
        mMap.setOnMarkerClickListener(cm);
        mMap.setOnInfoWindowClickListener(cm);

        cm.setOnClusterClickListener(this);
        cm.setOnClusterInfoWindowClickListener(this);
        cm.setOnClusterItemClickListener(this);
        cm.setOnClusterItemInfoWindowClickListener(this);
        addItems(cm);

    }

    @Override
    public void onClusterInfoWindowClick(Cluster<MarkerInfo> cluster) {
        // Does nothing, but you could go to a list of the users.

    }

    @Override
    public boolean onClusterItemClick(final MarkerInfo markerInfo) {
        // Does nothing, but you could go into the user's profile page, for example.
        Toast.makeText(this, markerInfo.getName()+"이 선택되었습니다.", Toast.LENGTH_SHORT).show();
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View testView = getLayoutInflater().inflate(R.layout.multi_photo_detail, null);
                ((ImageView)testView.findViewById(R.id.photo_detail)).setImageBitmap(markerInfo.photo);
                return testView;
            }
        });
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(MarkerInfo markerInfo) {
        // Does nothing, but you could go into the user's profile page, for example.

    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }

    private void addItems(final ClusterManager cm) {
        final String[] strArray = {"1_1.jpg", "1_2.jpg", "1_3.jpg","1_4.jpg","1_5.jpg"};
        final Bitmap[] bitmapArray = new Bitmap[strArray.length];
        final LatLng []latLngs = {new LatLng(51.50111,-0.122777775),new LatLng(51.524025,-0.1584639),new LatLng(51.50083,-0.122777775),new LatLng(51.531944,-0.12416667),new LatLng(51.503887,-0.07638889)};
        final int []rank = {1,2,3,4,5};

        new Thread(){
            public void run() {
                super.run();
                URL url;
                HttpURLConnection conn;
                try {
                    for(int i = 0; i<strArray.length; i++){
                        bitmapArray[i] = BitmapFactory.decodeStream((InputStream)new URL("http://photovel.com/upload/1/"+strArray[i]).getContent());
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i=0;i<strArray.length;i++) {
                                cm.addItem(new MarkerInfo(latLngs[i], strArray[i], bitmapArray[i],rank[i]));
                            }
                            cm.cluster();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    private class MarkerRenderer extends DefaultClusterRenderer<MarkerInfo> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext()); //마커의 아이콘을 Generator해줌
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext()); // 클러스트된 아이콘을 Generator해줌
        private final ImageView mImageView; // 클러스트가 되어진 마커가 아닌 일반 마커를 의미
        private final ImageView mClusterImageView; // 클러스트가 된 마커를 의미

        private final int mDimension;
        View multiProfile; //클러스터가 아닌 일반 마커의 전체 뷰
        ImageView proFileInImageView;
        TextView rankTextView;
        TextView amu_text;


        public MarkerRenderer() {
            super(getApplicationContext(), mMap, cm);
            multiProfile = getLayoutInflater().inflate(R.layout.multi_photo, null);
            multiProfile.findViewById(R.id.rankTextView).setVisibility(View.GONE);

            mClusterIconGenerator.setContentView(multiProfile); // 인플레이터한 전체레이아웃을 아이콘으로 만들어준다.
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image); //multiprofile안의 이미지뷰를 찾아줌

            //새로 만든 이미지뷰를 설정해줌
            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding,  padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
            Drawable TRANSPARENT_DRAWABLE = new ColorDrawable(Color.TRANSPARENT);
            mIconGenerator.setBackground(TRANSPARENT_DRAWABLE);



        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerInfo markerInfo, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            /*
            원본입니다.
            mImageView.setImageBitmap(markerInfo.getPhoto());
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(markerInfo.name);
            */
            View newMultiProfile = getLayoutInflater().inflate(R.layout.multi_photo, null);
            //시도중입니다.
            proFileInImageView = (ImageView)newMultiProfile.findViewById(R.id.image);
            proFileInImageView.setImageBitmap(markerInfo.getPhoto());
            rankTextView = (TextView)newMultiProfile.findViewById(R.id.rankTextView);
            rankTextView.setText(markerInfo.getRank()+"");
            amu_text = (TextView)newMultiProfile.findViewById(R.id.amu_text);
            amu_text.setVisibility(View.GONE);

            mIconGenerator.setContentView(newMultiProfile);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(markerInfo.name);



        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MarkerInfo> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (MarkerInfo p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable bitmapDrawable = new BitmapDrawable(getResources(), p.photo);
                bitmapDrawable.setBounds(0, 0, width, height);
                profilePhotos.add(bitmapDrawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);


/*            for(Bitmap bitmap :profilePhotos){
                mClusterImageView.setImageBitmap(bitmap);
            }*/
            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

}
