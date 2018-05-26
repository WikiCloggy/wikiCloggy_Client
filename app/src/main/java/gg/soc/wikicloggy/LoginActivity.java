package gg.soc.wikicloggy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";

    private SessionCallback callback;
    TextView user_nickname, user_email;
    CircleImageView user_img;
    LoginButton loginButton;
    DBController dbController;
    AQuery aQuery;

    public static long currentUserID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Drawable drawable = getResources().getDrawable(R.drawable.main_cloggy);
        ImageView imageView = (ImageView)findViewById(R.id.imageView2);
        imageView.setImageDrawable(drawable);

        aQuery = new AQuery(this);
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();

        dbController = new DBController(getApplicationContext());

        //Kakao login buttion
        loginButton = (LoginButton)findViewById(R.id.com_kakao_login);
        loginButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if(!isConnected()) {
                        Toast.makeText(LoginActivity.this, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
                if(isConnected()) {
                    return false;
                } else {
                    return true;
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    //check for internet connection
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            //access token을 성공적으로 발급 받아 valid access token을 가지고 있는 상태
            //일반적으로 로그인 후의 다음
            if(Session.getCurrentSession().isOpened()) { //Session Check
                redirectSignipActivity();
            }

            //카카오 사용자 정보 요청
            UserManagement.requestMe(new MeResponseCallback() {
                //세션이 닫혀 실패한 경우로 에러 결과를 받음
                @Override
                public void onSessionClosed(ErrorResult errorResult) {

                }
                //사용자가 가입된 상태가 아니여서 실패한 경우
                @Override
                public void onNotSignedUp() {

                }
                //사용자 정보 요청이 성공한 경우로 사용자 정보 객체를 받음
                @Override
                public void onSuccess(UserProfile result) {
                    GetXMLTask getXMLTask = new GetXMLTask();
                    CreateUserTask createUserTask = new CreateUserTask(new User(result.getId(), result.getNickname()), result.getThumbnailImagePath());
                    createUserTask.execute();

                    if(dbController.getUser(result.getId())==null){ //Local DB에 사용자가 등록되어있지 않은 경우
                        if(result.getThumbnailImagePath() != null) {
                            getXMLTask.execute(new String[] {result.getThumbnailImagePath()});
                        }
                        dbController.addUser(new User(result.getId(), result.getNickname()));
                    }
                    currentUserID = result.getId();
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null)  {
                Logger.e(exception);
            }
        }
    }
    protected void redirectSignipActivity() {
        final Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
        finish();
    }

    /*
    * 카카오 API가 제공하는 프로필 사진 url를 통해서
    * 사진을 비동기적으로 다운 받을 수 있게 해주는 class
    * */
    private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            for (String url : strings) {
                bitmap = downloadImage(url);
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            dbController.updateProfileImg(new User(currentUserID, result));
        }
    }

    public class CreateUserTask extends AsyncTask<Void, Void, Void> {
        HttpInterface server;
        User user;
        String url;
        String imgPath;
        JSONObject jsonObject = new JSONObject();
        RequestHttpURLConnection urlConnection = new RequestHttpURLConnection();
        public CreateUserTask(User user, String imgPath) {
            this.user = user;
            this.imgPath = imgPath;
            server = new HttpInterface("user", user);
            url = server.getserverUrl();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                jsonObject.put("user_code", user.getId());
                jsonObject.put("name", user.getName());
                jsonObject.put("avatar_path", imgPath);
                urlConnection.requestHttpPost(url, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //Creates Bitmap from InputStream and resturns it
    private Bitmap downloadImage(String url) {
        Bitmap bitmap = null;
        InputStream stream = null;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = 1;

        try {
            stream = getHttpConnection(url);
            bitmap = BitmapFactory.decodeStream(stream, null, bitmapOptions);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //Makes HttpURLConnction and returns InputStream
    private InputStream getHttpConnection(String urlString) throws IOException {
        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpURLConnection.getInputStream();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }
}
