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


        Drawable drawable = getDrawable(R.drawable.sample_image);
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

        commentItemArrayList.add(new commentItem("현정", "저희 강아지도 그래서 병원 찾아갔었는데 항문낭염이라고 하더라구요! ㅠ.ㅠ 병원 한 번 데려가 보세요!", true, "항문낭염"));
        commentItemArrayList.add(new commentItem("영기", "맞아요! 항문낭염! 위험할 수 있으니 병원가시는 걸 추천드려요!", false, "질병"));
        commentItemArrayList.add(new commentItem("윤경", "저희 강아지도 바닥에 자꾸 엉덩이 끌고 다니길래 왜 그런가 했더니 항문에 문제가 있어서 그렇다네요...", false, "항문낭염"));
        commentAdapter = new CommentAdapter(PostActivity.this, commentItemArrayList);
        commentListView.setAdapter(commentAdapter);



    }
}
