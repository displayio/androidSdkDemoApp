package io.display.displayioshowcase;

/**
 * Created by Nicolae on 11.05.2017.
 */

public class InterstitialAd {
    private String landingResName;
    private String videoResName;
    private int tileRes;
    private String placementId;
    private String width;
    private String height;

    public String getLandingResName() {
        return landingResName;
    }

    public void setLandingResName(String landingResName) {
        this.landingResName = landingResName;
    }

    public String getVideoResName() {
        return videoResName;
    }

    public void setVideoResName(String videoResName) {
        this.videoResName = videoResName;
    }

    public int getTileRes() {
        return tileRes;
    }

    public void setTileRes(int tileRes) {
        this.tileRes = tileRes;
    }

    public String getPlacementId() {
        return placementId;
    }

    public void setPlacementId(String placementId) {
        this.placementId = placementId;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public static InterstitialAd from(String landingResName, String placementId, int tileRes, String videoResName, String width, String height) {
        InterstitialAd interstitialAd = new InterstitialAd();
        interstitialAd.setLandingResName(landingResName);
        interstitialAd.setPlacementId(placementId);
        interstitialAd.setTileRes(tileRes);
        interstitialAd.setVideoResName(videoResName);
        interstitialAd.setWidth(width);
        interstitialAd.setHeight(height);
        return interstitialAd;
    }
}
