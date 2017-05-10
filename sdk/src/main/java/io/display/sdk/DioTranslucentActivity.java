package io.display.sdk;

import android.os.Build;
import android.transition.Explode;

public class DioTranslucentActivity extends DioGenericActivity {
    protected void preCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Explode());
        }
    }
    protected void postCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());
        }
    }
}
