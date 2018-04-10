package gg.soc.wikicloggy;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Camera;

/**
 * Created by userp on 2018-04-09.
 */

public class CameraAdapter {

    //Check if this device has a camera
    private boolean checkCameraHardware(Context context) {
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            //this device has a camera
            return true;
        } else {
            //no camera on this device
            return false;
        }
    }
    // A safe way to get an instance of the Camera object
    public static android.hardware.Camera getCameraInstance() {
        android.hardware.Camera c = null;
        try {
            c = android.hardware.Camera.open();
        } catch (Exception e) {

        }
        return c;
    }
}
