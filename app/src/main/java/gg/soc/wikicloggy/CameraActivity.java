package gg.soc.wikicloggy;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.icu.text.LocaleDisplayNames;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManagerNonConfig;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class CameraActivity extends Activity {

    private TextureView mCameraTextureView;
    private Preview mPreview;

    private static final int PICK_FROM_ALBUM = 1;

    Activity cameraActivity = this;

    ListView listView = null;

    private static final String TAG = "CameraActivity";

    static final int REQUEST_CAMERA = 1;

    Button takePictureBtn;
    ImageButton getFromAlbumBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        setCustomActionbar();
        setNavigationbar();

        mCameraTextureView = (TextureView) findViewById(R.id.cameraTextureView);
        mPreview = new Preview(this, mCameraTextureView);

        takePictureBtn = (Button) findViewById(R.id.takePictureBtn);
        getFromAlbumBtn = (ImageButton) findViewById(R.id.getFromAlbumBtn);

        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPreview.takePicture();
                //startActivity(new Intent(CameraActivity.this, WaitingResultActivity.class));
            }
        });
        getFromAlbumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

    }
    public class leaveLog extends AsyncTask<String, String, String> {
        HttpInterface postJson;
        String response;
        String DBid;
        String imgPath;
        JSONObject jsonObject = new JSONObject();
        public leaveLog(String imgPath) {
            this.imgPath= imgPath;
            postJson = new HttpInterface("log");
        }
        @Override
        protected String doInBackground(String... strings) {

            try {
                jsonObject.put("user_code", LoginActivity.currentUserID);
                response = postJson.postJson(jsonObject);
                JSONObject log = new JSONObject(response);
                DBid = log.getString("_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return DBid;
        }

        @Override
        protected void onPostExecute(String result){
            askAnalysis analysis = new askAnalysis(imgPath, result);
            analysis.execute();
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
    public class askAnalysis extends AsyncTask <Void, Void, Void> {
        HttpInterface postFile;
        String response;
        String realPath;
        public askAnalysis(String imgPath, String DB_id) {
            this.postFile = new HttpInterface("analysis");
            postFile.addToUrl(DB_id);
            this.realPath = imgPath;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                response = postFile.postFile("logFile",realPath);
                Log.d(TAG,  response.toString()); // here key word return ******************************************************
                Intent intent = new Intent(CameraActivity.this, ResultActivity.class);
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                JSONArray imageJsonArray = new JSONArray(jsonObject.get("ref").toString());
                JSONObject imageJsonObject0 = new JSONObject(imageJsonArray.getJSONObject(0).toString());
                JSONObject imageJsonObject1 = new JSONObject(imageJsonArray.getJSONObject(1).toString());
                JSONObject imageJsonObject2 = new JSONObject(imageJsonArray.getJSONObject(2).toString());

                intent.putExtra("keyword", jsonObject.get("keyword").toString());
                intent.putExtra("analysis", jsonObject.get("analysis").toString());
                intent.putExtra("image0", imageJsonObject0.get("img_path").toString());
                intent.putExtra("image1", imageJsonObject1.get("img_path").toString());
                intent.putExtra("image2", imageJsonObject2.get("img_path").toString());
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                //Log.d(TAG,"send Avatar fail");
            }
            return null;
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK) return;
        switch (requestCode) {
            case PICK_FROM_ALBUM:
                Uri imageUri = data.getData();
//                try {
//                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
//                    Bitmap seletedImage = BitmapFactory.decodeStream(inputStream);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
                leaveLog leaveLog = new leaveLog(getPath(imageUri));
                leaveLog.execute();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CAMERA:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.CAMERA)) {
                        if(grantResult == PackageManager.PERMISSION_GRANTED) {
                            mCameraTextureView = (TextureView) findViewById(R.id.cameraTextureView);
                            //mPreview = new Preview(cameraActivity, mCameraTextureView);
                            Log.d(TAG,"mPreview set");
                        } else {
                            Toast.makeText(this,"Should have camera permission to run", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPreview.onResume();
    }
    @Override
    protected  void onPause() {
        super.onPause();
        //mPreview.onPause();
    }


    //initializing navigationbar
    private void setNavigationbar() {
        final String[] items = {"Logout", "지식견", "Profile", "Map", "Log"};
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);
        listView = (ListView)findViewById(R.id.drawer_menulist);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        UserManagement.requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CameraActivity.this, "로그아웃 성공", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        break;
                    case 1:
                        startActivity(new Intent(CameraActivity.this, BoardActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(CameraActivity.this, ProfileActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(CameraActivity.this, MapActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(CameraActivity.this, UserLogActivity.class));
                }
                DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer);
                drawer.closeDrawer(Gravity.LEFT);
            }
        });
    }
    //actionbar customizing

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