package gg.soc.wikicloggy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class SelectFaceActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "SelectFaceActivity";
    private ImageView imageView;
    private String userImage;

    private Button rightBtn;
    private Button leftBtn;
    private String id;

    private FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_face);

        imageView = (ImageView) findViewById(R.id.selectFaceImageView);
        imageView.setImageResource(R.drawable.main_cloggy);

        rightBtn = (Button) findViewById(R.id.selectFaceRigjtBtn);
        leftBtn = (Button) findViewById(R.id.selectFaceLeftBtn);
        rightBtn.setOnClickListener(this);
        leftBtn.setOnClickListener(this);

        frameLayout = (FrameLayout) findViewById(R.id.selectFaceWaitingFrameLayout);
        frameLayout.setVisibility(View.GONE);

        Intent intent = getIntent();
        userImage = intent.getStringExtra("userImage");
        id = intent.getStringExtra("id");

        Log.d(TAG, "id is "+id);
        File file = new File(userImage);
        if(file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onClick(View view) {
        String direction = null;

        if(view.getId() == R.id.selectFaceLeftBtn) {
            direction = "left";
            GetResult getResult = new GetResult(id, direction);
            getResult.execute();
        } else if (view.getId() == R.id.selectFaceRigjtBtn) {
            //Toast.makeText(this, "오른쪽", Toast.LENGTH_SHORT).show();
            direction = "right";
            GetResult getResult = new GetResult(id, direction);
            getResult.execute();
        }

    }

    class GetResult extends AsyncTask<Void,Void,JSONObject> {
        HttpInterface httpInterface;
        String result;
        public GetResult(String id, String direction) {
            httpInterface = new HttpInterface("selectFace");
            httpInterface.addToUrl(id);
            httpInterface.addToUrl("/"+direction);
            Log.d(TAG, httpInterface.getUrl());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            frameLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            return httpInterface.getJsonObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Log.d(TAG, jsonObject.toString());
            frameLayout.setVisibility(View.INVISIBLE);
            try {
                if(jsonObject.getString("result").equals("success")) {
                    Intent intent = new Intent(SelectFaceActivity.this, ResultActivity.class);

                    JSONArray keywordJsonArray = jsonObject.getJSONArray("percentage");
                    JSONArray keyImageJsonArray = jsonObject.getJSONArray("path");
                    String state = jsonObject.getString("state");

                    JSONObject keywordJsonObject0 = keywordJsonArray.getJSONObject(0);
                    JSONObject keywordJsonObject1 = keywordJsonArray.getJSONObject(1);
                    JSONObject keywordJsonObject2 = keywordJsonArray.getJSONObject(2);

                    JSONObject imageJsonObject0 = keyImageJsonArray.getJSONObject(0);
                    JSONObject imageJsonObject1 = keyImageJsonArray.getJSONObject(1);
                    JSONObject imageJsonObject2 = keyImageJsonArray.getJSONObject(2);

                    //if(jsonObject.get("keyword").toString() == "") {
                    //    intent = new Intent(CameraActivity.this, ResultFailActivity.class);
                    //} else {
                    String keywordString = keywordJsonObject0.getString("keyword")+" "+keywordJsonObject0.getString("probability")
                            +" "+keywordJsonObject1.getString("keyword")+" "+keywordJsonObject1.getString("probability")
                            +" "+keywordJsonObject2.getString("keyword")+" "+keywordJsonObject2.getString("probability");

                    intent.putExtra("keyword", keywordString);
                    intent.putExtra("analysis", state);
                    intent.putExtra("image0", imageJsonObject0.get("img_path").toString());
                    intent.putExtra("image1", imageJsonObject1.get("img_path").toString());
                    intent.putExtra("image2", imageJsonObject2.get("img_path").toString());
                    intent.putExtra("userImage", userImage);

                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
