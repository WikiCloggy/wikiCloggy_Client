package gg.soc.wikicloggy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by userp on 2018-05-14.
 */

public class HttpInterface {

//    private String serverUrl = "http://ec2-13-125-187-247.ap-northeast-2.compute.amazonaws.com";
    private String serverUrl = "http://192.168.10.162";

    private String port =":3000/";
    private String apiPath;

    public HttpInterface(String type, User user){
        switch(type)
        {
            case "user" : // user create
                apiPath ="api/user/";
                break;
            case "profile" :
                apiPath ="api/profile/"+user.getId();
                break;
            case "avatar" :
                apiPath="api/prifile/files/"+user.getId();
                break;
            case "board" :
                break;
            case "log" :
                break;
        }

    }

    public String getserverUrl () {
        return serverUrl + port + apiPath;
    }
}
