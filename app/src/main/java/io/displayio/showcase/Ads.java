package io.displayio.showcase;

import java.util.ArrayList;

/**
 * Created by Nicolae on 15.05.2017.
 */

public class Ads {

    public static ArrayList<InterstitialAd> getInterstitialVideoItems(ArrayList<InterstitialAd> rvListItems) {
        rvListItems.add(InterstitialAd.from("ic_landing_1", "https://play.google.com/store/apps/details?id=com.nousguide.android.rbtv", "1121", R.drawable.tile_1, "video_1", "480", "320"));
        rvListItems.add(InterstitialAd.from("ic_landing_2", "https://play.google.com/store/apps/details?id=com.audible.application", "1122", R.drawable.tile_2, "video_2", "480", "320"));
        rvListItems.add(InterstitialAd.from("ic_landing_3", "https://play.google.com/store/apps/details?id=com.ss.android.article.tbvideo.cn", "1123", R.drawable.tile_3, "video_3", "480", "320"));
        rvListItems.add(InterstitialAd.from("ic_landing_4", "https://play.google.com/store/apps/details?id=com.cmplay.tiles2", "1124", R.drawable.tile_4, "video_4", "480", "320"));
        rvListItems.add(InterstitialAd.from("ic_landing_5", "https://play.google.com/store/apps/details?id=com.d3p.mpq", "1125", R.drawable.tile_5, "video_5", "480", "320"));
        rvListItems.add(InterstitialAd.from("ic_landing_6", "https://play.google.com/store/apps/details?id=com.craftsvilla.app", "1126", R.drawable.tile_6, "video_6", "480", "320"));
        rvListItems.add(InterstitialAd.from("ic_landing_7", "https://play.google.com/store/apps/details?id=com.playrix.fishdomdd.gplay", "1127", R.drawable.tile_7, "video_7", "480", "320"));
        rvListItems.add(InterstitialAd.from("ic_landing_8", "https://play.google.com/store/apps/details?id=com.uc.browser.en", "1128", R.drawable.tile_8, "video_8", "480", "320"));
        return rvListItems;
    }

    public static ArrayList<InterstitialAd> getInterstitialStaticItems(ArrayList<InterstitialAd> rvListItems) {
        rvListItems.add(InterstitialAd.from("ic_inter_1", "https://play.google.com/store/apps/details?id=com.bitmango.go.make7hexapuzzle", "1111", R.drawable.ic_inter_1, "ic_inter_1", "320", "480"));
        rvListItems.add(InterstitialAd.from("ic_inter_2", "https://play.google.com/store/apps/details?id=com.heroesofchaos.ggplay.koramgame.ru", "1112", R.drawable.ic_inter_2, "ic_inter_2", "320", "480"));
        rvListItems.add(InterstitialAd.from("ic_inter_3", "https://play.google.com/store/apps/details?id=com.bigfishgames.fairwaysolitaireuniversalf2pgoogle", "1113", R.drawable.ic_inter_3, "ic_inter_3", "320", "480"));
        rvListItems.add(InterstitialAd.from("ic_inter_4", "https://play.google.com/store/apps/details?id=com.snailgameusa.swordofshadow", "1114", R.drawable.ic_inter_4, "ic_inter_4", "320", "480"));
        rvListItems.add(InterstitialAd.from("ic_inter_5", "https://play.google.com/store/apps/details?id=com.supercell.boombeach", "1115", R.drawable.ic_inter_5, "ic_inter_5", "320", "480"));
        rvListItems.add(InterstitialAd.from("ic_inter_6", "https://play.google.com/store/apps/details?id=com.supercell.clashroyale", "1116", R.drawable.ic_inter_6, "ic_inter_6", "320", "480"));
        return rvListItems;
    }

    public static ArrayList<Object[]> getInfeedItemsList(ArrayList<Object[]> mFeedItems) {
        mFeedItems.add(new Object[]{"US Unemployment Falls to Pre-Crisis Low", "Tories Set for Biggest Local Election Win in Decades", R.drawable.img_1});
        mFeedItems.add(new Object[]{"Tories Set for Biggest Local Election Win in Decades", "Tories Set for Biggest Local Election Win in Decades", R.drawable.img_2});
        mFeedItems.add(new Object[]{"Traders Vote Macron as Le Pen Vows to Wipe the Smiles Off Their Faces", "Traders Vote Macron as Le Pen Vows to Wipe the Smiles Off Their Faces", R.drawable.img_3});
        mFeedItems.add(new Object[]{"Brexit bickering: EU’s Juncker says he’ll stop ‘speaking English’ because it’s losing importance", "Brexit bickering: EU’s Juncker says he’ll stop ‘speaking English’ because it’s losing importance", R.drawable.img_4});
        mFeedItems.add(new Object[]{"1st large Chinese-made passenger jet C919 takes flight, seeks to rival Boeing & Airbus", "1st large Chinese-made passenger jet C919 takes flight, seeks to rival Boeing & Airbus", R.drawable.img_5});

        mFeedItems.add(new Object[]{"US Unemployment Falls to Pre-Crisis Low", "Tories Set for Biggest Local Election Win in Decades", R.drawable.img_1});
        mFeedItems.add(new Object[]{"Tories Set for Biggest Local Election Win in Decades", "Tories Set for Biggest Local Election Win in Decades", R.drawable.img_2});
        mFeedItems.add(new Object[]{"Traders Vote Macron as Le Pen Vows to Wipe the Smiles Off Their Faces", "Traders Vote Macron as Le Pen Vows to Wipe the Smiles Off Their Faces", R.drawable.img_3});
        mFeedItems.add(new Object[]{"Brexit bickering: EU’s Juncker says he’ll stop ‘speaking English’ because it’s losing importance", "Brexit bickering: EU’s Juncker says he’ll stop ‘speaking English’ because it’s losing importance", R.drawable.img_4});
        mFeedItems.add(new Object[]{"1st large Chinese-made passenger jet C919 takes flight, seeks to rival Boeing & Airbus", "1st large Chinese-made passenger jet C919 takes flight, seeks to rival Boeing & Airbus", R.drawable.img_5});

        mFeedItems.add(new Object[]{"US Unemployment Falls to Pre-Crisis Low", "Tories Set for Biggest Local Election Win in Decades", R.drawable.img_1});
        mFeedItems.add(new Object[]{"Tories Set for Biggest Local Election Win in Decades", "Tories Set for Biggest Local Election Win in Decades", R.drawable.img_2});
        mFeedItems.add(new Object[]{"Traders Vote Macron as Le Pen Vows to Wipe the Smiles Off Their Faces", "Traders Vote Macron as Le Pen Vows to Wipe the Smiles Off Their Faces", R.drawable.img_3});
        mFeedItems.add(new Object[]{"Brexit bickering: EU’s Juncker says he’ll stop ‘speaking English’ because it’s losing importance", "Brexit bickering: EU’s Juncker says he’ll stop ‘speaking English’ because it’s losing importance", R.drawable.img_4});
        mFeedItems.add(new Object[]{"1st large Chinese-made passenger jet C919 takes flight, seeks to rival Boeing & Airbus", "1st large Chinese-made passenger jet C919 takes flight, seeks to rival Boeing & Airbus", R.drawable.img_5});

        return mFeedItems;
    }
}
