package gg.soc.wikicloggy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class NotFoundActivity extends Activity {
    private static final String TAG = "NotFoundActivity";

    private Button cameraBtn;
    private ImageView userImageview;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_found);

        Intent intent = getIntent();
        imagePath = intent.getStringExtra("userImage");
        cameraBtn = (Button) findViewById(R.id.notFoundCameraBtn);
        userImageview = (ImageView) findViewById(R.id.notFountImageView);

        File file = new File(imagePath);
        if(file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            userImageview.setImageBitmap(bitmap);
        }
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
