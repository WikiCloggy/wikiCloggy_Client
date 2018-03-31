package gg.soc.wikicloggy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import de.hdodenhof.circleimageview.CircleImageView;


public class LoginActivity extends Activity {
    private SessionCallback callback;
    TextView user_nickname, user_email;
    CircleImageView user_img;
    LoginButton loginButton;

    AQuery aQuery;

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
}
