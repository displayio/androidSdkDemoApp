package io.displayio.sdk;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by mark on 8/21/16.
 */
public class DioImageView extends ImageView {

    public void setRoundFrame( ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            GradientDrawable shape =  new GradientDrawable();
            shape.setCornerRadius( 18 );
            shape.setColor(Color.WHITE);
            shape.setStroke(5, Color.DKGRAY);
            setBackground(shape);
            setPadding(15,15,15,15);
        }
    }

    public void rotate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {

            ObjectAnimator animation = ObjectAnimator.ofFloat(this, "rotationY", 270f, 360f);
            animation.setDuration(1000);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.start();
        }

    }

    public DioImageView(Context context) {
        super(context);
    }


}
