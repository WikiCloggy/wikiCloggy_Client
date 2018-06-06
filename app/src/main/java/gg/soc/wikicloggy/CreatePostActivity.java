package gg.soc.wikicloggy;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;

public class CreatePostActivity extends Activity {
    private static final String TAG = "CreatePostActivity";
    private static final int PICK_FROM_ALBUM = 1;
    ImageView userImageView;
    String userImage = null;

    Button getImageBtn;

    private EditText titleEditText;
    private EditText bodyEditText;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        setCustomActionbar();

        Intent intent = getIntent();

        Drawable drawable = getDrawable(R.drawable.main_cloggy);
        userImageView = (ImageView) findViewById(R.id.createPostImageView);
        userImageView.setImageDrawable(drawable);

        getImageBtn = (Button) findViewById(R.id.createPostGetImageBtn);
        saveBtn = (Button) findViewById(R.id.createPostSaveBtn);

        titleEditText = (EditText) findViewById(R.id.createPostTitleEditText);
        bodyEditText = (EditText) findViewById(R.id.createPostBodyEditText);

        userImage = intent.getStringExtra("userImage");
        if(userImage != null) {
            File file = new File(userImage);
            if(file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                userImageView.setImageBitmap(bitmap);
            }
        }

        getImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, titleEditText.getText().toString());
                //Log.d(TAG, bodyEditText.getText().toString());
                if(userImage == null) {
                    Toast.makeText(getApplicationContext(), "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else if(titleEditText.getText().toString().equals("")) {
                    Toast.makeText(CreatePostActivity.this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(bodyEditText.getText().toString().equals("")) {
                    Toast.makeText(CreatePostActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    //Log.d(TAG, "edit text check is "+titleEditText.getText().toString()+" "+bodyEditText.getText().toString());
                    Log.d(TAG, "user image is "+userImage);
                    createPost createPost = new createPost(titleEditText.getText().toString(), bodyEditText.getText().toString(), userImage);
                    createPost.execute();
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK) return;
        switch (requestCode) {
            case PICK_FROM_ALBUM:
                Uri imageUri = data.getData();
                userImage = getPath(imageUri);
                Log.d(TAG, "image path is "+userImage);
                File file = new File(userImage);
                if(file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Log.d(TAG, "bitmap is "+bitmap);
                    userImageView.setImageBitmap(bitmap);
                }
        }
    }
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    class createPost extends AsyncTask<Void, Void, String> {
        HttpInterface httpInterface;
        String title;
        String body;
        String image;
        JSONObject jsonObject;
        String response = null;
        public createPost (String title, String body, String image) {
            httpInterface = new HttpInterface("createPost");
            jsonObject = new JSONObject();
            this.title = title;
            this.body = body;
            this.image = image;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            SendImageFile sendImageFile = new SendImageFile(image, response);
            sendImageFile.execute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                jsonObject.put("title", title);
                jsonObject.put("content", body);
                jsonObject.put("author", String.valueOf(LoginActivity.currentUserID));
                jsonObject.put("createdAt", String.valueOf(new Date(System.currentTimeMillis())));

                response = httpInterface.postJson(jsonObject);
                JSONObject jsonObject = new JSONObject(response);
                response = jsonObject.getString("_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return response;
        }
    }
    class SendImageFile extends AsyncTask<Void, Void, String> {
        HttpInterface httpInterface;
        String image;
        String response;
        String dbID;
        JSONObject jsonObject;
        public SendImageFile (String image, String dbID) {
            httpInterface = new HttpInterface("sendPostFile");
            this.image = image;
            this.dbID = dbID;
        }
        @Override
        protected String doInBackground(Void... voids) {
            httpInterface.addToUrl(dbID);
            Log.d(TAG, httpInterface.getUrl());
            response  = httpInterface.postFile("postFile", image);
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try {
                jsonObject = new JSONObject(response);
                response = jsonObject.getString("result");

                if(response.equals("ok")) {
                    Toast.makeText(getApplicationContext(), "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void setCustomActionbar() {
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
