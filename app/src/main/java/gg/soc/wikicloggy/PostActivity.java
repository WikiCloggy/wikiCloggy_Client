package gg.soc.wikicloggy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class PostActivity extends Activity {
    ImageView imageView;
    ImageView commentImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();

        Drawable drawable = getDrawable(R.drawable.main_cloggy);
        imageView = (ImageView) findViewById(R.id.postImageView);
        imageView.setImageDrawable(drawable);
        commentImageView = (ImageView)findViewById(R.id.commentImageView);
        commentImageView.setImageDrawable(drawable);

        //String id = intent.getStringExtra("id"); 인텐트로 스트링 받아오는 법
    }
}
