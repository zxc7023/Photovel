package com.photovel.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by EunD on 2017-06-26.
 */

public class JsonConnection {
    private static final String TAG = "JsonConnection";
    private String url;
    private String method;
    private JSONObject json;


    public String getConnection(final String url, final  String method, final JSONObject json){

        String responseData = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        OutputStream dos = null;
        ByteArrayOutputStream baos;

        Log.i(TAG, "1. getConnection url= " + url);

        try {
            URL connectURL = new URL(url);
            conn = (HttpURLConnection) connectURL.openConnection();

            //서버로부터 결과값을 응답받음
            conn.setDoInput(true);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestMethod(method);

            //method가 post면
            if (method.toUpperCase().equals("POST") && json != null) {
                conn.setDoOutput(true);
                dos = conn.getOutputStream();
                dos.write(json.toString().getBytes());
                dos.flush();
            }

            //정상인 경우 200번, 그 외 오류있는 경우 오류 번호 반환
            final int responseCode = conn.getResponseCode();
            Log.i(TAG, "2. getConnection responseCode= " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
                Reader reader = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(reader);

                responseData = br.readLine();
                Log.i(TAG, "3. getConnection responseData= " + responseData);

                br.close();
                reader.close();
                is.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return responseData;
    }

    public List<Bitmap> getBitmap(final List imgs, final String url){
        final List<Bitmap> bitmaps = null;
        Thread thread2 = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    for (int i = 0; i < imgs.size(); i++) {
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                        bitmaps.add(bitmap);
                        Log.i(TAG, "4. getBitmap bitmap= " + bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread2.start();
        try {
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bitmaps;
    }
}
