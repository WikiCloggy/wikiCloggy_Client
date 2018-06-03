package gg.soc.wikicloggy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by userp on 2018-05-14.
 */

public class HttpInterface {
    private static final String TAG = "HttpInterface";

    //private String serverUrl = "http://ec2-13-125-187-247.ap-northeast-2.compute.amazonaws.com";
    private String serverUrl = "http://35.200.17.32";
    //private String serverUrl = "http://172.20.10.3"; // for local test
    private String port = ":3000/";
    private String apiPath;
    private String requestURL = null;
    private long user_code;

    public HttpInterface() { // this is for download image
        user_code = LoginActivity.currentUserID;
    };

    public HttpInterface(String type) {
        user_code = LoginActivity.currentUserID;
       this.setUrl(type);
    }

    public String setUrl(String type) {
        switch (type) {
            case "createUser": // user create
                apiPath = "api/user/";
                break;
            case "getUser" : // get user info
                apiPath = "api/user/details/" +user_code;
                break;
            case "profile": // edit profile, name
                apiPath = "api/user/profile/" + user_code;
                break;
            case "avatar": // upload file
                apiPath = "api/user/profile/files/" + user_code;
                break;
            case "getLog": // get user query log
                apiPath = "api/log/list/" +user_code;
                break;
            case "log" : // upload photos to analysis
                apiPath = "api/log/";
                break;
            case "analysis" :
                apiPath = "api/log/files/"; // + db id
                break;
            case "createPost" :
                apiPath = "api/board/";
                break;
            case "sendPostFile" :
                apiPath = "api/board/files/"; //+ db id
                break;
        }
        requestURL = serverUrl + port + apiPath;
        Log.d("hyeon", requestURL);
        return requestURL;
    }

    public String addToUrl (String string) {
        requestURL += string;
        return requestURL;
    }

    public String getUrl() {
        return requestURL;
    }

    public Bitmap getBitmapImage (String imageUrl) {
        try {
            URL url = new URL(imageUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            return BitmapFactory.decodeStream(is);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getUser () {
        RequestHttpURLConnection urlConnection=null;
        String response;

        try {
            urlConnection = new RequestHttpURLConnection();
            response = urlConnection.requestHttpGet(this.setUrl("getUser"));

            JSONArray json = new JSONArray(response);
            JSONObject userData = json.getJSONObject(0);
            return userData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String postJson(JSONObject formdata) {
        String response;
        RequestHttpURLConnection urlConnection = new RequestHttpURLConnection();
        response = urlConnection.requestHttpPost(this.getUrl(),formdata);
        return response;
    }

    public JSONArray getJson (){
        RequestHttpURLConnection urlConnection=null;
        String response;

        try {
            urlConnection = new RequestHttpURLConnection();
            response = urlConnection.requestHttpGet(this.getUrl());

            JSONArray jsonArray = new JSONArray(response);
            //JSONObject json = new JSONObject(response);
            //Log.d(TAG, json.toString());
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String postFile(String fileField,String filepath) {
        if (requestURL != null) {
            try {
                HttpURLConnection connection = null;
                DataOutputStream outputStream = null;
                InputStream inputStream = null;
                String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                File file = new File(filepath);
                FileInputStream fileInputStream = new FileInputStream(file);
                URL url = new URL(requestURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes("--" + boundary + "\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fileField + "\"; filename=\"" + file.getName() + "\"" + "\r\n");
                outputStream.writeBytes("Content-Type: image/jpeg" + "\r\n");
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + "\r\n");
                outputStream.writeBytes("\r\n");
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, 1048576);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, 1048576);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                outputStream.writeBytes("\r\n");
                outputStream.writeBytes("--" + boundary + "--" + "\r\n");

                inputStream = connection.getInputStream();

                int status = connection.getResponseCode();
                if (status == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    inputStream.close();
                    connection.disconnect();
                    fileInputStream.close();
                    outputStream.flush();
                    outputStream.close();
                    return response.toString();
                }
            } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

