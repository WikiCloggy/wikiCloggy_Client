package gg.soc.wikicloggy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    private Uri profileImageUri;
    private Button profileImageBtn;
    private ImageView profileImageView;
    private String absoultePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileImageBtn = (Button)findViewById(R.id.profileImageBtn);
        profileImageView = (ImageView)findViewById(R.id.profileImageView);
        profileImageBtn.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode!= RESULT_OK) return;
        switch (requestCode) {
             case PICK_FROM_ALBUM:
                 profileImageUri = data.getData();
                 Log.e("CROP", "pick from album");
                 Intent intent = new Intent("com.android.camera.action.CROP");
                 intent.setDataAndType(profileImageUri, "image/*");

                 intent.putExtra("outputX", 200);
                 intent.putExtra("outputY", 200);
                 intent.putExtra("aspectX", 1);
                 intent.putExtra("aspectY", 1);
                 intent.putExtra("scale", true);
                 intent.putExtra("return-data", true);
                 startActivityForResult(intent, CROP_FROM_IMAGE);

                 break;
             case CROP_FROM_IMAGE:
                 if(resultCode!=RESULT_OK) {
                     return;
                 }
                 final Bundle extras = data.getExtras();
                 String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Wikicloggy/"+ System.currentTimeMillis()+".jpg";

                 if(extras!=null) {
                     Bitmap photo = extras.getParcelable("data");
                     profileImageView.setImageBitmap(photo);

                     storeCropImage(photo, filePath);
                     absoultePath = filePath;
                     break;
                 }
                 File file = new File(profileImageUri.getPath());
                 if(file.exists()) {
                     file.delete();
                 }
         }

    }

    // 앨범에서 이미지 가져오기
    public void doTakeAlbumPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
    private void storeCropImage(Bitmap bitmap, String filePath) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Wikicloggy";
        File directoryWikicloggy = new File(dirPath);

        if(!directoryWikicloggy.exists()) {
            directoryWikicloggy.mkdir();
            File copyFile = new File(filePath);
            BufferedOutputStream out = null;

            try {
                copyFile.createNewFile();
                out = new BufferedOutputStream(new FileOutputStream(copyFile));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.profileImageBtn) {
            doTakeAlbumPhoto();
        }
    }
}
