package gg.soc.wikicloggy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

    private String serverUrl = "http://ec2-13-125-187-247.ap-northeast-2.compute.amazonaws.com";
//    private String serverUrl = "http://192.168.10.162"; // for local test
    private String port = ":3000/";
    private String apiPath;
    private String requestURL = null;

    public HttpInterface(String type, User user) {
        switch (type) {
            case "user": // user create
                apiPath = "api/user/";
                break;
            case "profile": // edit profile, name
                apiPath = "api/user/profile/" + user.getId();
                break;
            case "avatar": // upload file
                apiPath = "api/user/profile/files/" + user.getId();
                break;
            case "board":
                break;
            case "log":
                break;
        }
        requestURL = serverUrl + port + apiPath;

    }

    public HttpInterface(){};

    public String getserverUrl() {
        return requestURL;
    }

    public Bitmap getBitmapImage (String URL) {
        this.requestURL = URL;
        try {
            URL url = new URL(requestURL);

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

    public String postFile(String filepath) {
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
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "avatarFile" + "\"; filename=\"" + file.getName() + "\"" + "\r\n");
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

