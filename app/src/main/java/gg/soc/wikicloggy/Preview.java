package gg.soc.wikicloggy;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 * Created by userp on 2018-04-10.
 */

public class Preview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";

    SurfaceView mSurfaceview;
    SurfaceHolder mHolder;
    Camera.Size mPreviewSize;
    List<Camera.Size> mSupportedPreviewSizes;
    Camera mCamera;

    Preview(Context context, SurfaceView sv) {
        super(context);

        mSurfaceview = sv;
        mHolder = mSurfaceview.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        if(mCamera != null) {
            mCamera.stopPreview();

            mCamera.release();
            mCamera = null;
        }
        mCamera = camera;
        if(mCamera != null) {
            List<Camera.Size> localSizes = mCamera.getParameters().getSupportedPictureSizes();
            mSupportedPreviewSizes = localSizes;
            requestLayout();

            Camera.Parameters params = mCamera.getParameters();

            List<String> focusModes = params.getSupportedFocusModes();
            if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                mCamera.setParameters(params);
            }

            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mCamera.startPreview();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumWidth(), heightMeasureSpec);

        setMeasuredDimension(width, height);

        if(mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed && getChildCount() > 0) {
            final View child =getChildAt(0);

            final int width = r - l;
            final int height = b - t;
            int previewWidth = width;
            int previewheight = height;
            if(mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewheight = mPreviewSize.height;
            }

            if(width*previewheight >  height*previewWidth) {
                final int scaledChildWidth = previewWidth*height/previewheight;
                child.layout((width-scaledChildWidth)/2, 0, (width+scaledChildWidth)/2, height);
            } else {
                final int scaledChiledHeight = previewheight*width/previewWidth;
                child.layout(0, (height-scaledChiledHeight)/2, width, (height+scaledChiledHeight)/2);
            }
        }
    }
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w/h;
        if(sizes == null) return null;

        double minDiff =Double.MAX_VALUE;
        int targetHeight= h;

        Camera.Size optimalSize = null;

        for(Camera.Size size:sizes) {
            double ratio = (double)size.width/size.height;
            if(Math.abs(ratio=targetRatio)>ASPECT_TOLERANCE) continue;
        }
        return optimalSize;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d("@@@", "surfaceCreated");

        try {
            if(mCamera != null) {
                mCamera.setPreviewDisplay(surfaceHolder);
            }
        } catch (IOException e) {
            Log.e("TAG", "IOException caued by setPreviewDisplay()", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(mCamera != null) {
            mCamera.stopPreview();
        }
    }
}
