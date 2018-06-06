package gg.soc.wikicloggy;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by userp on 2018-04-10.
 * Camera2 API를 사용하여 화면에 카메라 프리뷰를 띄우고 사진을 찍는 기능까지 담당하는 class
 */

public class Preview extends Thread {
    private final static String TAG = "Preview";

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private Size mPreviewSize;
    private Context mContext;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mPreviewSession;
    private TextureView mTextureView;

    private StreamConfigurationMap map;

    public Preview(Context context, TextureView textureView) {
        mContext = context;
        mTextureView = textureView;
    }

    private String getBackFacingCameraId(CameraManager cManager) {
        try {
            for (final String cameraId : cManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cManager.getCameraCharacteristics(cameraId);
                int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cOrientation == CameraCharacteristics.LENS_FACING_BACK) return cameraId;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void openCamera() {
        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "openCamera E");
        try {
            String cameraId = getBackFacingCameraId(manager);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];

            int permissionCamera = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);

            if(permissionCamera == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CAMERA}, CameraActivity.REQUEST_CAMERA);
            } else {
                manager.openCamera(cameraId, mStateCallback, null);
            }

        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    //카메라를 시작하기 위해서 선언
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener(){

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                              int width, int height) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onSurfaceTextureAvailable, width="+width+",height="+height);
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                                int width, int height) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            // TODO Auto-generated method stub
        }
    };

    //카메라가 켜진다면 시작하는 콜백함수
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onOpened");
            mCameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onDisconnected");
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onError");
        }

    };

    //실직적으로 동작하는 부분
    protected void startPreview() {
        // TODO Auto-generated method stub
        if(null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            Log.e(TAG, "startPreview fail, return");
        }

        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        if(null == texture) {
            Log.e(TAG,"texture is null, return");
            return;
        }

        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        //카메라 출력이 저장됨
        Surface surface = new Surface(texture);

        try {
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mPreviewBuilder.addTarget(surface);

        try {
            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(CameraCaptureSession session) {
                    // TODO Auto-generated method stub
                    mPreviewSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    // TODO Auto-generated method stub
                    Toast.makeText(mContext, "onConfigureFailed", Toast.LENGTH_LONG).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void updatePreview() {
        // TODO Auto-generated method stub
        if(null == mCameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }

        mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        Handler backgroundHandler = new Handler(thread.getLooper());

        try {
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setSurfaceTextureListener()
    {
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
    }

    public void onResume() {
        Log.d(TAG, "onResume");
        setSurfaceTextureListener();
    }

    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    public void onPause() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onPause");
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
                Log.d(TAG, "CameraDevice Close");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
    }
    /*
    * 카메라 Preview 이후에 Preview에 띄워진 화면을 캡쳐하기 위해
    * */

    protected void takePicture() {
        if(null == mCameraDevice) {
            Log.e(TAG, "mCameraDevice is null, return");
            return;
        }
        CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(mCameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            Log.d(TAG, "width is "+width +" and height is "+height);

            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(mTextureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            // Orientation
            int rotation = ((Activity)mContext).getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            final String filepath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Wikicloggy/"+System.currentTimeMillis()+".jpg";

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes, filepath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                            reader.close();
                        }
                    }
                }

                private void save(byte[] bytes, String filePath) throws IOException {

                    Log.d(TAG, "Save image");
                    String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Wikicloggy";
                    File directoryWikicloggy = new File(dirPath);
                    if(!directoryWikicloggy.exists()) {
                        directoryWikicloggy.mkdir();
                    }

                    File copyFile = new File(filePath);

                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    BufferedOutputStream out = null;

                    out = new BufferedOutputStream(new FileOutputStream(copyFile));
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, out);

                    //저장된 사진을 서버로 전송
                    leaveLog leaveLog = new leaveLog(filePath);
                    leaveLog.execute();//

                }
            };

            HandlerThread thread = new HandlerThread("CameraPicture");
            thread.start();
            final Handler backgroudHandler = new Handler(thread.getLooper());
            reader.setOnImageAvailableListener(readerListener, backgroudHandler);

            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session,
                                               CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    startPreview();
                }

            };

            mCameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, backgroudHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, backgroudHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
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
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                response = postFile.postFile("logFile",realPath);
                Log.d(TAG,  response.toString()); // here key word return ******************************************************
                Intent intent;
                JSONObject jsonObject = new JSONObject(response);
                String result = jsonObject.getString("result");
                if(result.equals("fail")) {
                    intent = new Intent(mContext, ResultFailActivity.class);
                    intent.putExtra("userImage", realPath);
                    mContext.startActivity(intent);
                } else if(result.equals("success")) {
                    intent = new Intent(mContext, ResultActivity.class);

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
                    intent.putExtra("userImage", realPath);

                    mContext.startActivity(intent);

                }
            } catch (Exception e) {
                e.printStackTrace();
                //Log.d(TAG,"send Avatar fail");
            }
            return null;
        }

    }

}
