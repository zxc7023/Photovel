package com.photovel.http;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by EunD on 2017-06-26.
 */

public class MultipartConnection {
    private static final String TAG = "MultipartConnection";

    public String getConnection(String url, JSONObject json, List<Bitmap> bitmaps){
        String responseData = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        DataOutputStream dos = null;
        ByteArrayOutputStream baos;

        Log.i(TAG, "1. getConnection url= " + url);
        try {
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "**##**";

            URL connectURL = new URL(url);

            conn = (HttpURLConnection) connectURL.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"content\"" + lineEnd + lineEnd + URLEncoder.encode(json.toString(), "UTF-8"));

            for (int i = 0; i < bitmaps.size(); i++) {
                dos.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadFile\"; filename=\"uploadFile\"" + lineEnd);
                dos.writeBytes("Content-Type: image/jpg" + lineEnd + lineEnd);

                ByteArrayOutputStream outPutStream = new ByteArrayOutputStream();
                bitmaps.get(i).compress(Bitmap.CompressFormat.JPEG, 100, outPutStream);
                byte[] byteArray = outPutStream.toByteArray();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
                int bytesAvailable = inputStream.available();
                int maxBufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                int bytesRead = inputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = inputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = inputStream.read(buffer, 0, bufferSize);
                }
                inputStream.close();
            }

            dos.writeBytes(lineEnd + twoHyphens + boundary + twoHyphens + lineEnd);
            dos.flush();

            int responseCode = conn.getResponseCode();
            Log.i("responseCode", responseCode + "");

            if(responseCode == HttpURLConnection.HTTP_OK){
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] byteBuffer = new byte[1024];
                byte[] byteData = null;
                int nLength = 0;
                while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                    baos.write(byteBuffer, 0, nLength);
                }
                byteData = baos.toByteArray();
                responseData = new String(byteData);
                Log.i("isSucess",responseData);
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dos.close();
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return responseData;
    }
}
