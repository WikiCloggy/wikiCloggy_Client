package gg.soc.wikicloggy;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Drawable drawable = getDrawable(R.drawable.main_cloggy);

        intent = getIntent();
        keyword = intent.getStringExtra("keyword");
        analysis = intent.getStringExtra("analysis");

        userImageView = (ImageView) findViewById(R.id.resultUserImageView);
        exampleImageView1 = (ImageView) findViewById(R.id.resultExampleImageView1);
        exampleImageView2 = (ImageView) findViewById(R.id.resultExampleImageView2);
        exampleImageView3 = (ImageView) findViewById(R.id.resultExampleImageView3);

        keywordTextView = (TextView) findViewById(R.id.resultKeywordTextView);
        analysisTextView = (TextView) findViewById(R.id.resultAnalysisTextView);

        setCustomActionbar();
        userImageView.setImageDrawable(drawable);
        exampleImageView1.setImageDrawable(drawable);
        exampleImageView2.setImageDrawable(drawable);
        exampleImageView3.setImageDrawable(drawable);

        keywordTextView.setText(keyword);
        analysisTextView.setText(analysis);
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
