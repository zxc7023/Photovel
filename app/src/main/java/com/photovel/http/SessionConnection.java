package com.photovel.http;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by junki on 2017-07-05.
 */

public class SessionConnection {
    private static String TAG = "SessionConnectionTest";
    private static int sequnece=0;

    public static String compareSession(String jSessionValue) {
        URL url;
        HttpURLConnection conn = null;
        OutputStream os;
        BufferedWriter bw;
        InputStream is = null;
        ByteArrayOutputStream baos;
        String responseResult="0";

        try {
            String compareURL = Value.userCompareURL;
            url = new URL(compareURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            //conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Cookie", jSessionValue);
            int responseCode = conn.getResponseCode();
            Log.i("여기까지됨", responseCode + "");
            switch (responseCode) {
                case HttpURLConnection.HTTP_OK:
                    is = conn.getInputStream();
                    baos = new ByteArrayOutputStream();
                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData = null;
                    int nLength = 0;
                    while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                        baos.write(byteBuffer, 0, nLength);
                    }
                    byteData = baos.toByteArray();
                    responseResult = new String(byteData);
                    Log.i(TAG + sequnece++ +"세션 존재여부 true:1 / false :0", responseResult);
                    break;
                default:
                    Log.i(TAG + sequnece++ +"비정상 코드 ", "responseCode=" + responseCode);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseResult;
    }
}
