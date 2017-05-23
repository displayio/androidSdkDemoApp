package io.displayio.sdk.device;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import io.displayio.sdk.Controller;

import static io.displayio.sdk.Controller.REQUEST_CODE_ASK_GEO_PERMISSIONS;

public class PermissionsHandler extends Activity {
    private Controller ctrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout rl = new RelativeLayout(this);
        rl.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rl.setLayoutParams(layoutParams);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(rl);

        ctrl = Controller.getInstance();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_GEO_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_GEO_PERMISSIONS:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission granted
                        ctrl.getLocation();
                    }
                }

                if(ctrl.deviceDescriptor != null && ctrl.deviceDescriptor.mDeviceEventsListener != null)
                    ctrl.deviceDescriptor.mDeviceEventsListener.onGeoPermissionRequestResult();

                finish();
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                if(ctrl.deviceDescriptor != null && ctrl.deviceDescriptor.mDeviceEventsListener != null)
                    ctrl.deviceDescriptor.mDeviceEventsListener.onGeoPermissionRequestResult();
                finish();
        }
    }
}
