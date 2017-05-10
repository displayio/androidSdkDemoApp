package io.display.sdk.ads.supers;

import org.json.JSONObject;

import io.display.sdk.ads.Ad;
import io.display.sdk.ads.components.Banner;

public abstract class BannerAd extends Ad {
    protected Banner banner;
    public BannerAd(String id, JSONObject data, JSONObject offering) {
        super(id, data, offering);
        banner = new Banner();
    }

    public void preload() {
        try {
            banner.setUrl(data.getString("ctv"));
            banner.setResource(data.getString("ctv"));
            banner.setOnPreloadErrorListener(new Banner.OnPreloadErrorListener() {
                @Override
                public void onPreloadError() {
                    broadcCastPreloadError();
                }
            });
            banner.setOnPreloadListener(new Banner.OnPreloadListener() {
                @Override
                public void onPreload() {
                    broadcastPreloadSuccess();
                }
            });

            banner.preload();
        } catch (Exception e) {
            broadcCastPreloadError();
        }
    }

}
