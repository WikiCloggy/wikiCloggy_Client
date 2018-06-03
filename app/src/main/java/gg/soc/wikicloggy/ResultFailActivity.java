package gg.soc.wikicloggy;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class ResultFailActivity extends Activity {

    private static final String TAG = "ResultFailActivity";
    ImageView resultFailImageView;
    Button resultFailGoQuestionBtn;

    String userImage;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_fail);

        setCustomActionbar();
        intent = getIntent();
        userImage = intent.getStringExtra("userImage");

        Drawable drawable = getDrawable(R.drawable.main_cloggy);
        resultFailImageView = (ImageView) findViewById(R.id.resultFailImageView);
        resultFailImageView.setImageDrawable(drawable);

        File file = new File(userImage);
        if(file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            resultFailImageView.setImageBitmap(bitmap);
        }

        resultFailGoQuestionBtn = (Button) findViewById(R.id.resultFailGoQuestionBtn);
        resultFailGoQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ResultFailActivity.this, CreatePostActivity.class);
                intent.putExtra("userImage", userImage);
                startActivity(intent);
                finish();
            }
        });
    }
    private void setCustomActionbar() {
        ActionBar actionBar = getActionBar();

        //for custom actionbar, set customEnabled true
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.layout_actionbar, null);

        actionBar.setCustomView(actionbar);
    }
}
