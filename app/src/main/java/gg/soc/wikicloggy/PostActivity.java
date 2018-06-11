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
import android.widget.AdapterView;
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

    private String author;

    private ImageView boneImageView;
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

        boneImageView = (ImageView) findViewById(R.id.commentItemBoneImageView);
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

        commentAdapter = new CommentAdapter(PostActivity.this, commentItemArrayList);

        commentListView.setAdapter(commentAdapter);

        commentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Log.d(TAG, commentItemArrayList.get(i).getName());
                if(author.equals(String.valueOf(LoginActivity.currentUserID))) { //본인의 게시글인 경우
                    //Log.d(TAG, "author is "+author);
                    Log.d(TAG, "adopted is "+commentItemArrayList.get(i).isAdopted());
                    if(commentItemArrayList.get(i).isAdopted()) { //채택 받은 경우
                        commentItemArrayList.get(i).setAdopted(false);
                        Log.d(TAG, commentItemArrayList.get(i).isAdopted()+"");
                    } else { //채택받지 못한 경우
                        commentItemArrayList.get(i).setAdopted(true);
                    }
                    PostComment postComment = new PostComment(postID, commentItemArrayList.get(i).getCommentID(), commentItemArrayList.get(i).isAdopted());
                    postComment.execute();
                }
            }
        });
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
            String author_name;
            String createdAt;
            String img_path;
            JSONArray commentJSONArray;
            JSONObject commentJSONObject;
            String commentName;
            String commentDate;
            String commentKeyword;
            boolean commentAdopted;
            String commentBody;
            String commentID = "";

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                title = jsonObject.getString("title");
                content = jsonObject.getString("content");
                author_name = jsonObject.getString("author_name");
                createdAt = jsonObject.getString("createdAt");
                author = jsonObject.getString("author");

                if(!jsonObject.isNull("comments")) {
                    commentJSONArray = jsonObject.getJSONArray("comments");
                    Log.d(TAG, "length is "+commentJSONArray.length());

                    for(int i = 0; i< commentJSONArray.length(); i++) {
                        commentJSONObject = commentJSONArray.getJSONObject(i);
                        if(!commentJSONObject.isNull("name") && !commentJSONObject.isNull("body") && !commentJSONObject.isNull("adopted") && !commentJSONObject.isNull("createdAt") && !commentJSONObject.isNull("keyword") && !commentJSONObject.isNull("_id")) {
                            commentName = commentJSONObject.getString("name");
                            commentBody = commentJSONObject.getString("body");
                            commentAdopted = commentJSONObject.getBoolean("adopted");
                            commentDate = commentJSONObject.getString("createdAt");
                            commentKeyword = commentJSONObject.getString("keyword");
                            commentID = commentJSONObject.getString("_id");

                            commentItemArrayList.add(new commentItem(commentName, commentBody, commentAdopted, commentKeyword, commentID));
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
                nameTextView.setText(author_name);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(PostActivity.this, PostActivity.class);
        intent.putExtra("id", postID);
        startActivity(intent);
        finish();
    }

    class PostComment extends AsyncTask<Void, Void, String> {
        HttpInterface httpInterface;
        String postID;
        String commentID;
        JSONObject jsonObject = new JSONObject();
        boolean adopted;

        String result;
        public PostComment(String postID, String commentID, boolean adopted) {
            httpInterface = new HttpInterface("updateComment");
            this.postID = postID;
            this.commentID = commentID;
            this.adopted = adopted;
            //Log.d(TAG, "post id is "+postID+" and commentID is "+commentID);
            httpInterface.addToUrl(postID);
            httpInterface.addToUrl("/"+commentID);
            Log.d(TAG, httpInterface.getUrl());

            try {
                jsonObject.put("adopted", adopted);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(Void... voids) {
            Log.d(TAG, jsonObject.toString());
            result = httpInterface.postJson(jsonObject);
            Log.d(TAG, result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                result  = (new JSONObject(result)).getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(result.equals(null)) {

            } else if(result.equals("ok")) {
                if(adopted) {
                    Toast.makeText(getApplicationContext(), "선택하신 댓글이 채택되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "댓글 채택이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(PostActivity.this, PostActivity.class);
                intent.putExtra("id", postID);
                startActivity(intent);
                finish();

            }
        }
    }
}
