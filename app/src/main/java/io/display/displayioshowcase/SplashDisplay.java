package io.display.displayioshowcase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new EasySplashScreen(SplashDisplay.this)
            .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(3000)
                .withHeaderText("Display")
                .withFooterText("(C) Display.io")
                .withBeforeLogoText("Display.io")
                .withLogo(R.drawable.ic_displayio)
                .withAfterLogoText("Some more details")
                .create()
        );
    }
}
