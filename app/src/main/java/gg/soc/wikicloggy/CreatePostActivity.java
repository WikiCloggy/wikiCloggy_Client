package gg.soc.wikicloggy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class CreatePostActivity extends Activity {

    ImageView userImageView;
    String userImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        Intent intent = getIntent();

        Drawable drawable = getDrawable(R.drawable.main_cloggy);
        userImageView = (ImageView) findViewById(R.id.createPostImageView);
        userImageView.setImageDrawable(drawable);

        userImage = intent.getStringExtra("userImage");
        if(userImage != null) {
            File file = new File(userImage);
            if(file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                userImageView.setImageBitmap(bitmap);
            }
        }
    }
}
