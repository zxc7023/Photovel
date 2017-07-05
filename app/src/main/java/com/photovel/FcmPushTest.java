package com.photovel;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by junki on 2017-07-05.
 */

public class FcmPushTest {

    // Method to send Notifications from server to client end.

    public final static String AUTH_KEY_FCM = "AAAAaBiACs8:APA91bGBr5bByonwY6UL_k2VLbh0mLEAhjIGCNcTpND-5bWF6nO2CHVfC7yfrSrC6FS9BSN75bchZtiUbE-TgBWLK2_PlJD3Q02cgEIi6lcjwhgdYoNo6FbDGjrph_XKO-cvZ7nE1y6O";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

    // userDeviceIdKey is the device id you will query from your database

    public static void pushFCMNotification(String token ,HashMap<String,String> hashMap) {
        String authKey = AUTH_KEY_FCM; // You FCM AUTH key
        String FMCurl = API_URL_FCM;

        URL url = null;
        try {
            url = new URL(FMCurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + authKey);
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject json = new JSONObject();
            /*JSONObject info = new JSONObject();
            info.put("body", "푸쉬 발송 테스트 입니다."); // Notification body
            json.put("notification", info);*/
            JSONObject data = new JSONObject();

            Set<String> keySet =hashMap.keySet();
            for(String key : keySet){
                Log.i("FcmPushTest key" , key);
                data.put(key,hashMap.get(key));
            }
            json.put("to", token.trim()); // deviceID
            json.put("data",data);
            Log.i("FcmPushTest",json.toString());

            try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(),"UTF-8")) {
//혹시나 한글 깨짐이 발생하면
//try(OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")){ 인코딩을 변경해준다.

                wr.write(json.toString());
                wr.flush();
            } catch (Exception e) {
            }

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                Log.i("outputStream",output);
            }

            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

