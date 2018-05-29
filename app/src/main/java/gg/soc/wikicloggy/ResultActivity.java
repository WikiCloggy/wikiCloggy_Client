package gg.soc.wikicloggy;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class ResultActivity extends Activity {

    private static final String TAG = "ResultActivity";

    ImageView userImageView;
    ImageView exampleImageView1;
    ImageView exampleImageView2;
    ImageView exampleImageView3;

    TextView keywordTextView;
    TextView analysisTextView;

    Intent intent;

    String keyword;
    String analysis;
    String imgPath0;
    String imgPath1;
    String imgPath2;

    String userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Drawable drawable = getDrawable(R.drawable.main_cloggy);

        intent = getIntent();
        keyword = intent.getStringExtra("keyword");
        analysis = intent.getStringExtra("analysis");
        imgPath0 = intent.getStringExtra("image0");
        imgPath1 = intent.getStringExtra("image1");
        imgPath2 = intent.getStringExtra("image2");
        userImage = intent.getStringExtra("userImage");

        //Log.d(TAG, userImage);

        userImageView = (ImageView) findViewById(R.id.resultUserImageView);
        exampleImageView1 = (ImageView) findViewById(R.id.resultExampleImageView1);
        exampleImageView2 = (ImageView) findViewById(R.id.resultExampleImageView2);
        exampleImageView3 = (ImageView) findViewById(R.id.resultExampleImageView3);

        keywordTextView = (TextView) findViewById(R.id.resultKeywordTextView);
        analysisTextView = (TextView) findViewById(R.id.resultAnalysisTextView);

        setCustomActionbar();
        userImageView.setImageDrawable(drawable);
        //exampleImageView1.setImageDrawable(drawable);
        exampleImageView2.setImageDrawable(drawable);
        exampleImageView3.setImageDrawable(drawable);

        File file = new File(userImage);
        if(file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Log.d(TAG, bitmap.toString());
            userImageView.setImageBitmap(bitmap);
        }

        keywordTextView.setText(keyword);
        analysisTextView.setText(analysis);
        GetImageFromURL getImageFromURL0 = new GetImageFromURL(imgPath0,0);
        GetImageFromURL getImageFromURL1 = new GetImageFromURL(imgPath1,1);
        GetImageFromURL getImageFromURL2 = new GetImageFromURL(imgPath2,2);
        getImageFromURL0.execute();
        getImageFromURL1.execute();
        getImageFromURL2.execute();
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
    class GetImageFromURL extends AsyncTask<Void, Void, Bitmap> {
        HttpInterface httpInterface = new HttpInterface();
        String url;
        int num;
        public GetImageFromURL (String url, int num) {
            this.url = url;
            this.num = num;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            httpInterface.getBitmapImage(url);
            return httpInterface.getBitmapImage(url);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(num == 0) {
                exampleImageView1.setImageBitmap(bitmap);
            } else if(num == 1) {
                exampleImageView2.setImageBitmap(bitmap);
            } else if(num == 2) {
                exampleImageView3.setImageBitmap(bitmap);
            }
        }
    }
}
