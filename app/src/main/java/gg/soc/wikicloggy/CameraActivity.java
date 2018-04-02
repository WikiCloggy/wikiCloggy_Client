package gg.soc.wikicloggy;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class CameraActivity extends Activity {
    private TextureView mCameraTextureView;
    private CameraAPI mPreview;

    Activity cameraActivity = this;

    private static final String TAG = "CAMERAACTIVITY";

    static final int REQUEST_CAMERA = 1;

    ListView listView = null;

    Button takePictureButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //For fullscreen with actionbar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        setCustomActionbar();
        setNavigationbar();

        mCameraTextureView = (TextureView) findViewById(R.id.cameraTextureView);
        mPreview = new CameraAPI(this, mCameraTextureView);

        takePictureButton = (Button) findViewById(R.id.takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPreview.takePicture();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mPreview.onResume();
    }
    @Override
    protected  void onPause() {
        super.onPause();
        mPreview.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case REQUEST_CAMERA:
                for(int i=0; i<permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if(grantResult == PackageManager.PERMISSION_GRANTED) {
                        mCameraTextureView = (TextureView) findViewById(R.id.cameraTextureView);
                        mPreview = new CameraAPI(cameraActivity, mCameraTextureView);
                        Log.d(TAG, "mPreview set");
                    } else {
                        Toast.makeText(this, "Should have camera permission to run", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                break;
        }
    }

    //initializing navigationbar
    private void setNavigationbar() {
        final String[] items = {"Logout", "B"};
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
                        break;
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