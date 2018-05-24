package gg.soc.wikicloggy;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

public class SplashActivity extends Activity {

    //로딩 화면이 떠있는 시간(밀리초단위)
    private final int SPLASH_DISPLAY_LENGTH =1000;
    private ImageView runningcloggy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screan);

        runningcloggy = (ImageView)findViewById(R.id.running_wikicloggy);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(runningcloggy);
        Glide.with(this).load(R.drawable.running_cloggy).into(gifImage);

        //Intent intent = new Intent(this, LoginActivity.class);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 2000);
    }
}
