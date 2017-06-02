package io.displayio.showcase;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import com.jaredrummler.android.widget.AnimatedSvgView;

public class SplashDisplay extends AppCompatActivity {
    private AnimatedSvgView mLogoView;
    private View imgTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_display);

        imgTitle = findViewById(R.id.display_title);
        mLogoView = (AnimatedSvgView)findViewById(R.id.animated_svg_view);
        mLogoView.setTraceColor(Color.WHITE);
        mLogoView.postDelayed(new Runnable() {
            @Override public void run() {
                mLogoView.start();
            }
        }, 500);

        imgTitle.setAlpha(0);

        mLogoView.setOnStateChangeListener(new AnimatedSvgView.OnStateChangeListener() {
            @Override
            public void onStateChange(@AnimatedSvgView.State int state) {
                if (state == AnimatedSvgView.STATE_FILL_STARTED) {

                    imgTitle.setVisibility(View.VISIBLE);
                    imgTitle.setTranslationY(-imgTitle.getHeight());

                    AnimatorSet set = new AnimatorSet();
                    Interpolator interpolator = new OvershootInterpolator();
                    ObjectAnimator a1 = ObjectAnimator.ofFloat(mLogoView, View.TRANSLATION_Y, 0);
                    ObjectAnimator a2 = ObjectAnimator.ofFloat(imgTitle, View.TRANSLATION_Y, 0);
                    ObjectAnimator a3 = ObjectAnimator.ofFloat(imgTitle, View.ALPHA, 1);
                    a1.setInterpolator(interpolator);
                    a2.setInterpolator(interpolator);
                    set.setDuration(500).playTogether(a1, a2, a3);
                    set.start();
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashDisplay.this, MainActivity.class));
            }
        }, 3000);
    }
}
