package com.photovel.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.vo.Comment;
import com.vo.Content;
import com.vo.ContentDetail;
import com.vo.MainImage;
import com.vo.User;

import org.json.JSONObject;

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
 * Created by Eundi on 2017-06-26.
 */

public class JsonConnection {
    private static final String TAG = "JsonConnection";

    public static String getConnection(final String url, final  String method, final JSONObject json){

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
                Log.i(TAG, "1.2 getConnection json= " + json);
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

    public static void setBitmap(final List imgs, final String url){
        Thread setBitmap = new Thread(){
            @Override
            public void run() {
                super.run();
                try {

                    //MainImage 타입일 때
                    if (imgs.get(0) instanceof MainImage) {
                        for (int i = 0; i < imgs.size(); i++) {
                            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)
                                    new URL(url + "/" + ((MainImage) imgs.get(i)).getImage_file_name()).getContent());
                            ((MainImage) imgs.get(i)).setBitmap(bitmap);
                            Log.i(TAG, "4. getBitmap (MainImage)bitmap= " + bitmap);
                        }

                        //Content 타입일 때
                    }else if(imgs.get(0) instanceof Content){
                        for (int i = 0; i < imgs.size(); i++) {
                            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)
                                    new URL(url+"/"+((Content)imgs.get(i)).getContent_id()+"/"+((Content)imgs.get(i)).getPhoto_file_name()).getContent());
                            ((Content)imgs.get(i)).setBitmap(bitmap);
                            Log.i(TAG, "4. getBitmap (Content)bitmap= " + bitmap);
                        }
                        //글쓴이 profile 가져오기
                        for (int i = 0; i < imgs.size(); i++) {
                            Bitmap bitmap = null;
                            if(((Content)imgs.get(i)).getUser().getUser_profile_photo() != null){
                                if("O".equals(((Content)imgs.get(i)).getUser().getUser_sns_status())) {
                                    bitmap = BitmapFactory.decodeStream((InputStream)
                                            new URL(url + "/profile/"+((Content)imgs.get(i)).getUser().getUser_profile_photo()).getContent());
                                }else{
                                    bitmap = BitmapFactory.decodeStream((InputStream)
                                            new URL(((Content)imgs.get(i)).getUser().getUser_profile_photo()).getContent());
                                }
                            }
                            ((Content)imgs.get(i)).getUser().setBitmap(bitmap);
                            Log.i(TAG, "4. getBitmap (profile)bitmap= " + bitmap);

                        }

                        //ContentDetail 타입일 때
                    } else if (imgs.get(0) instanceof ContentDetail) {
                        for (int i = 0; i < imgs.size(); i++) {
                            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)
                                    new URL(url+"/"+((ContentDetail)imgs.get(i)).getContent_id()+"/"+((ContentDetail)imgs.get(i)).getPhoto().getPhoto_file_name()).getContent());
                            ((ContentDetail)imgs.get(i)).getPhoto().setBitmap(bitmap);
                            ((ContentDetail)imgs.get(i)).getPhoto().setRank(String.valueOf(i+1));
                            Log.i(TAG, "4. getBitmap (ContentDetail)bitmap= " + bitmap);
                        }
                        //Comment 타입일 때
                    } else if (imgs.get(0) instanceof Comment) {
                        for (int i = 0; i < imgs.size(); i++) {
                            Bitmap bitmap = null;
                            if(((Comment)imgs.get(i)).getUser().getUser_profile_photo() != null){
                                if("O".equals(((Comment)imgs.get(i)).getUser().getUser_sns_status())) {
                                    bitmap = BitmapFactory.decodeStream((InputStream)
                                            new URL(url + "/profile/"+((Comment)imgs.get(i)).getUser().getUser_profile_photo()).getContent());
                                }else{
                                    bitmap = BitmapFactory.decodeStream((InputStream)
                                            new URL(((Comment)imgs.get(i)).getUser().getUser_profile_photo()).getContent());
                                }
                            }
                            ((Comment)imgs.get(i)).getUser().setBitmap(bitmap);
                            Log.i(TAG, "4. getBitmap (Comment)bitmap= " + bitmap);
                        }
                        //User 타입일 때
                    } else if (imgs.get(0) instanceof User) {
                        for (int i = 0; i < imgs.size(); i++) {
                            Bitmap bitmap = null;
                            if(((User)imgs.get(i)).getUser_profile_photo() != null){
                                if("O".equals(((User)imgs.get(i)).getUser_sns_status())){
                                    bitmap = BitmapFactory.decodeStream((InputStream)
                                            new URL(url + "/profile/"+((User)imgs.get(i)).getUser_profile_photo()).getContent());
                                }else{
                                    bitmap = BitmapFactory.decodeStream((InputStream)
                                            new URL(((User)imgs.get(i)).getUser_profile_photo()).getContent());
                                }
                            }

                            ((User)imgs.get(i)).setBitmap(bitmap);
                            Log.i(TAG, "4. getBitmap (User)bitmap= " + bitmap);
                        }
                    }else{
                        Log.i(TAG, "MainImage, Content, ContentDetail, Comment, User Type이 아닙니다");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        setBitmap.start();
        try {
            setBitmap.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static String setHeaderConnection(final String url, final  String method, final JSONObject json,String jSessionValue){

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
            conn.setRequestProperty("Cookie", jSessionValue);

            //method가 post면
            if (method.toUpperCase().equals("POST") && json != null) {
                Log.i(TAG, "1.2 getConnection json= " + json);
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
}
