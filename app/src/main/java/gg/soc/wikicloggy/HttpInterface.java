package gg.soc.wikicloggy;

import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * Created by userp on 2018-05-14.
 */

public class HttpInterface extends AsyncTask<Void, Void, String>{

    private final static String TAG = "HttpInterface";

//    private final static String url = "http://ec2-13-125-187-247.ap-northeast-2.compute.amazonaws.com:3000";
    private final static String url = "http://192.168.0.58:3000/";
    //특정 user_code의 정보 보기
//    private final static String get_user_url = "/api/user/detail/"; //+user_code
//    //해당 user_code의 profile 정보 수정
//    private final static String post_user_profile_url = "/api/user/profile/"; //+user_code
//    //user_code profile 사진 path 설정
//    private final static String post_user_profile_image_url = "/api/user/profile/files/"; //+user_code
//    //user create
//    private final static String post_user_create_url = "/api/user/";

//    RequestHttpURLConnection urlConnection = new RequestHttpURLConnection();

    private long userCode;

    public HttpInterface (User user) {
        this.userCode = user.getId();
    }

    @Override
    protected String doInBackground(Void... voids) {
        return null;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
    }
}
