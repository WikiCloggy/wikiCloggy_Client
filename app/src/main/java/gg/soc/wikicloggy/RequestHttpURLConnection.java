package gg.soc.wikicloggy;

import android.content.ContentValues;
import android.icu.util.Output;
import android.renderscript.ScriptGroup;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

/**
 * Created by userp on 2018-05-14.
 * 서버와 통신하기 위한 HTTP GET, POST class
 */

public class RequestHttpURLConnection {
    public String requestHttpGet(String url) {
        try {
            URL reqURL = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) reqURL.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept", "*/*");

            int resCode = httpURLConnection.getResponseCode();
            if(resCode != HttpURLConnection.HTTP_OK) return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

            String input;
            StringBuffer stringBuffer = new StringBuffer();

            while((input = reader.readLine())!= null) {
                stringBuffer.append(input);
            }
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String requestHttpPost(String url, JSONObject jsonObject) {
        String result = "";

        try {
            URL reqURL = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) reqURL.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            String json = jsonObject.toString();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(json.getBytes("euc-kr"));
            outputStream.flush();
            outputStream.close();

            int retCode = httpURLConnection.getResponseCode();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = reader.readLine())!= null) {
                response.append(line);
                response.append('\r');
            }
            reader.close();

            String res = response.toString();
            return res;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
