package com.kitri.photovel.content;

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
import com.kitri.vo.Photo;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.kitri.photovel.R;

/**
 * Created by Junki on 2017-05-31.
 */

public class ClusterTest extends FragmentActivity
        implements OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<Photo>,
        ClusterManager.OnClusterInfoWindowClickListener<Photo>,
        ClusterManager.OnClusterItemClickListener<Photo>,
        ClusterManager.OnClusterItemInfoWindowClickListener<Photo> {



    private ClusterManager<Photo> cm;
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
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.50111,-0.122777775), 9.5f));
        startDemo();


    }

    //클러스터된 아이템을 클릭되었을때의 이벤트
    @Override
    public boolean onClusterClick(Cluster<Photo> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().getPhotoFileName();
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

        cm = new ClusterManager<Photo>(this, mMap);
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
    public void onClusterInfoWindowClick(Cluster<Photo> cluster) {
        // Does nothing, but you could go to a list of the users.

    }

    @Override
    public boolean onClusterItemClick(final Photo photo) {
        // Does nothing, but you could go into the user's profile page, for example.
        Toast.makeText(this, photo.getPhotoFileName()+"이 선택되었습니다.", Toast.LENGTH_SHORT).show();
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View testView = getLayoutInflater().inflate(R.layout.multi_photo_detail, null);
                ((ImageView)testView.findViewById(R.id.photo_detail)).setImageBitmap(photo.getBitmap());
                return testView;
            }
        });
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Photo photo) {
        // Does nothing, but you could go into the user's profile page, for example.

    }

    private void addItems(final ClusterManager cm) {
        final String[] strArray = {"1_1.jpg", "1_2.jpg", "1_3.jpg","1_4.jpg","1_5.jpg"};
        final Bitmap[] bitmapArray = new Bitmap[strArray.length];
        final LatLng []latLngs = {new LatLng(51.50111,-0.122777775),new LatLng(51.524025,-0.1584639),new LatLng(51.50083,-0.122777775),new LatLng(51.531944,-0.12416667),new LatLng(51.503887,-0.07638889)};
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");

        final Date[] photoDates = {};

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
                                cm.addItem(new Photo(bitmapArray[i], latLngs[i].latitude , latLngs[i].longitude, strArray[i]));
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


    private class MarkerRenderer extends DefaultClusterRenderer<Photo> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext()); //마커의 아이콘을 Generator해줌
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext()); // 클러스트된 아이콘을 Generator해줌
        private final ImageView mImageView; // 클러스트가 되어진 마커가 아닌 일반 마커를 의미
        private final ImageView mClusterImageView; // 클러스트가 된 마커를 의미

        private final int mDimension;
        View multiPhotoView;
        ImageView proFileInImageView;
        TextView rankTextView;
        TextView amu_text;


        public MarkerRenderer() {
            super(getApplicationContext(), mMap, cm);
            Drawable TRANSPARENT_DRAWABLE = new ColorDrawable(Color.TRANSPARENT);
            multiPhotoView = getLayoutInflater().inflate(R.layout.multi_photo, null);
            multiPhotoView.findViewById(R.id.rankTextView).setVisibility(View.GONE);
            mClusterIconGenerator.setContentView(multiPhotoView); // 인플레이터한 전체레이아웃을 아이콘으로 만들어준다.
            mClusterImageView = (ImageView) multiPhotoView.findViewById(R.id.image); //multiPhotoView안의 이미지뷰를 찾아줌

            //새로 만든 이미지뷰를 설정해줌
            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);

            mIconGenerator.setBackground(TRANSPARENT_DRAWABLE);
        }

        @Override
        protected void onBeforeClusterItemRendered(Photo photo, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            /*
            원본입니다.
            mImageView.setImageBitmap(photo.getPhoto());
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(photo.name);
            */
            View newMultiPhotoView = getLayoutInflater().inflate(R.layout.multi_photo, null);
            //시도중입니다.
            proFileInImageView = (ImageView)newMultiPhotoView.findViewById(R.id.image);
            proFileInImageView.setImageBitmap(photo.getBitmap());
            rankTextView = (TextView)newMultiPhotoView.findViewById(R.id.rankTextView);
//            rankTextView.setText(photo.getRank()+"");
            amu_text = (TextView)newMultiPhotoView.findViewById(R.id.amu_text);
            amu_text.setVisibility(View.GONE);

            mIconGenerator.setContentView(newMultiPhotoView);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
            //.title(photo.getName());



        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Photo> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> photos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (Photo p : cluster.getItems()) {
                // Draw 4 at most.
                if (photos.size() == 4) break;
                Drawable bitmapDrawable = new BitmapDrawable(getResources(), p.getBitmap());
                bitmapDrawable.setBounds(0, 0, width, height);
                photos.add(bitmapDrawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(photos);
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
