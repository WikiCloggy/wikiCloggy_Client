package gg.soc.wikicloggy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class PostActivity extends Activity {
    ImageView imageView;
    Button writeCommentBtn;

    ListView commentListView;
    ArrayList<commentItem> commentItemArrayList;
    CommentAdapter commentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();


        Drawable drawable = getDrawable(R.drawable.main_cloggy);
        imageView = (ImageView) findViewById(R.id.postImageView);
        imageView.setImageDrawable(drawable);

        writeCommentBtn = (Button) findViewById(R.id.writeCommentBtn);
        //String id = intent.getStringExtra("id"); 인텐트로 스트링 받아오는 법

        writeCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this, CreateCommentActivity.class));
            }
        });

        commentListView = (ListView) findViewById(R.id.postCommentListView);
        commentItemArrayList = new ArrayList<commentItem>();

        commentItemArrayList.add(new commentItem("현정", "배고파", true, "배고픔"));
        commentItemArrayList.add(new commentItem("현정", "배고파", false, "배고픔"));
        commentAdapter = new CommentAdapter(PostActivity.this, commentItemArrayList);
        commentListView.setAdapter(commentAdapter);



    }
}
