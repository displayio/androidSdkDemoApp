package io.display.displayioshowcase;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new EasySplashScreen(SplashDisplay.this)
            .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(3000)
                .withLogo(R.drawable.display_io_logo_white)
                .withBackgroundColor(ContextCompat.getColor(this, R.color.dark_background))
                .create()
        );
    }
}
