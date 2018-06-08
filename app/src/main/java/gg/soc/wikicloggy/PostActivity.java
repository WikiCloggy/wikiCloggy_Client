package gg.soc.wikicloggy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostActivity extends Activity {
    private static final String TAG = "PostActivity";

    private Button writeCommentBtn;

    private ListView commentListView;
    private ArrayList<commentItem> commentItemArrayList;
    private CommentAdapter commentAdapter;

    private String postID;

    private Drawable drawable;

    private TextView  titleTextView;
    private TextView dateTextView;
    private ImageView imageView;
    private TextView bodyTextView;
    private TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();

        postID = intent.getStringExtra("id");

        drawable = getDrawable(R.drawable.main_cloggy);
        imageView = (ImageView) findViewById(R.id.postImageView);
        imageView.setImageDrawable(drawable);

        writeCommentBtn = (Button) findViewById(R.id.writeCommentBtn);

        titleTextView = (TextView) findViewById(R.id.postTitleTextView);
        dateTextView = (TextView) findViewById(R.id.postTimeTextView);
        bodyTextView = (TextView) findViewById(R.id.postBodyTextView);
        nameTextView = (TextView) findViewById(R.id.postNameTextView);

        //String id = intent.getStringExtra("id"); 인텐트로 스트링 받아오는 법

        GetDetailPost getDetailPost = new GetDetailPost(postID);
        getDetailPost.execute();

        writeCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(PostActivity.this, CreateCommentActivity.class);
                commentIntent.putExtra("postID", postID);
                startActivity(commentIntent);
            }
        });

        commentListView = (ListView) findViewById(R.id.postCommentListView);
        commentItemArrayList = new ArrayList<commentItem>();

        //commentItemArrayList.add(new commentItem("현정", "저희 강아지도 그래서 병원 찾아갔었는데 항문낭염이라고 하더라구요! ㅠ.ㅠ 병원 한 번 데려가 보세요!", true, "항문낭염"));
        //commentItemArrayList.add(new commentItem("영기", "맞아요! 항문낭염! 위험할 수 있으니 병원가시는 걸 추천드려요!", false, "질병"));
        //commentItemArrayList.add(new commentItem("윤경", "저희 강아지도 바닥에 자꾸 엉덩이 끌고 다니길래 왜 그런가 했더니 항문에 문제가 있어서 그렇다네요...", false, "항문낭염"));

        commentAdapter = new CommentAdapter(PostActivity.this, commentItemArrayList);

        commentListView.setAdapter(commentAdapter);
    }
    class GetDetailPost extends AsyncTask<Void, Void, JSONArray> {
        HttpInterface httpInterface = new HttpInterface("getPostDetails");
        String dbID;
        public GetDetailPost(String dbID) {
            this.dbID = dbID;
            httpInterface.addToUrl(dbID);
        }
        @Override
        protected JSONArray doInBackground(Void... voids) {
            //httpInterface.getJson();
            return httpInterface.getJson();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            Log.d(TAG, jsonArray.toString());
            String title;
            String content;
            String author;
            String createdAt;
            String img_path;
            JSONArray commentJSONArray;
            JSONObject commentJSONObject;
            String commentName;
            String commentDate;
            String commentKeyword;
            boolean commentAdopted;
            String commentBody;

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                title = jsonObject.getString("title");
                content = jsonObject.getString("content");
                author = jsonObject.getString("author_name");
                createdAt = jsonObject.getString("createdAt");

                if(!jsonObject.isNull("comments")) {
                    commentJSONArray = jsonObject.getJSONArray("comments");
                    Log.d(TAG, "length is "+commentJSONArray.length());

                    for(int i = 0; i< commentJSONArray.length(); i++) {
                        commentJSONObject = commentJSONArray.getJSONObject(i);
                        if(!commentJSONObject.isNull("name") && !commentJSONObject.isNull("body") && !commentJSONObject.isNull("adopted") && !commentJSONObject.isNull("createdAt") && !commentJSONObject.isNull("keyword")) {
                            commentName = commentJSONObject.getString("name");
                            commentBody = commentJSONObject.getString("body");
                            commentAdopted = commentJSONObject.getBoolean("adopted");
                            commentDate = commentJSONObject.getString("createdAt");
                            commentKeyword = commentJSONObject.getString("keyword");

                            commentItemArrayList.add(new commentItem(commentName, commentBody, commentAdopted, commentKeyword));
                        }
                    }
                }

                if(jsonObject.isNull("img_path")) {
                    Log.d(TAG, "img_path is null");
                    imageView.setImageDrawable(drawable);
                } else {
                    img_path = jsonObject.getString("img_path");
                    GetImageFromServer getImageFromServer = new GetImageFromServer(img_path, imageView);
                    getImageFromServer.execute();
                }
                //img_path = jsonObject.getString("img_path");
                //Log.d(TAG, "title is "+title+" content is "+content+" author is "+author+" createdAt is "+createdAt+" isNull is "+jsonObject.isNull("img_path"));
                titleTextView.setText(title);
                bodyTextView.setText(content);
                nameTextView.setText(author);
                dateTextView.setText(createdAt);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    class GetImageFromServer extends AsyncTask<Void, Void, Bitmap> {
        HttpInterface httpInterface;
        String url;
        ImageView imageView;

        public GetImageFromServer(String url, ImageView imageView) {
            this.url = url;
            httpInterface = new HttpInterface();
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return httpInterface.getBitmapImage(url);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }
}
