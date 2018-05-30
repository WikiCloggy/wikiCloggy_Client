package gg.soc.wikicloggy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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


public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";

    private SessionCallback callback;
    LoginButton loginButton;
    DBController dbController;
    AQuery aQuery;
    public static User currentUser;
    public static long currentUserID = 0;
    private User kakaoUser;
///////////////////////////// To do 로그인할 때 서버 데이터 가져와서 user local db update., 카카오 연동 코드랑 같이 보기!
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

        dbController = new DBController(this);

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
                    currentUserID = result.getId();
                    currentUser = new User(result.getId());

                    kakaoUser = new User(currentUserID,result.getNickname(),result.getThumbnailImagePath());

                    CreateUserTask createUserTask = new CreateUserTask();
                    createUserTask.execute();
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
    URL의 파일을 비동기적으로 다운로드 받아 Bitmap 이미지로 변환. (Profile Image)
    변환 후의 Bitmap 이미지는 로컬 DB에 저장.

    private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            Log.d(TAG, "Get XML Task");
            Bitmap bitmap = null;
            HttpInterface downloadImage = new HttpInterface();
            if(currentUser.getAvatarPath()!=null)
             bitmap = downloadImage.getBitmapImage(currentUser.getAvatarPath());


            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            currentUser.setBitmapImg(result);
            dbController.updateProfileImg(new User(currentUserID, result));
        }
    }
    */

    public class CreateUserTask extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObject = new JSONObject();
        HttpInterface postJson;
        public CreateUserTask() {
            postJson = new HttpInterface("createUser");
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                jsonObject.put("user_code", kakaoUser.getId());
                jsonObject.put("name", kakaoUser.getName());
                jsonObject.put("avatar_path", kakaoUser.getAvatarPath());
                postJson.postJson(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //서버에서 유저 생성/확인 후 서버에서 유저 정보를 가져옴
            //From server and save local db
            getUserTask getUserTask = new getUserTask();
            getUserTask.execute();
        }
    }

    public class getUserTask extends AsyncTask<Void, Void, Void> {

        public getUserTask() {}

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject json ;
                HttpInterface getUser = new HttpInterface();
                json = getUser.getUser();
                currentUser.setName(json.getString("name"));
                currentUser.setAvatarPath(json.getString("avatar_path"));

                if(dbController.getUser(currentUserID) == null){ //Local DB에 사용자가 등록되어있지 않은 경우
                    if(currentUser.getAvatarPath()!=null) {
                        dbController.addUser(new User(currentUserID, currentUser.getName(),currentUser.getAvatarPath()));
                    }
                    else  {
                        dbController.addUser(new User(currentUserID, currentUser.getName()));
                    }
                } else {
                    dbController.updateUser(new User(currentUserID, currentUser.getName(), currentUser.getAvatarPath()));
                }

            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

    }
}
