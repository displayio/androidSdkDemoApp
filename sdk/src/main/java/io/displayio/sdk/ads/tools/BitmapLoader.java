package io.displayio.sdk.ads.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by jynx on 25/12/16.
 */

public class BitmapLoader {
    String url;
    Bitmap bitmap;
    boolean loaded = false;
    ArrayList<BitmapLoadListener> listeners = new ArrayList<>();
    public BitmapLoader( String url) {
        this.url = url;
    }
    public void addListener(BitmapLoadListener listener) {
        this.listeners.add( listener );
    }
    public void exec() {

        PreloadImageTask task = new PreloadImageTask();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        } else {
            task.execute(url);
        }
    }

    protected class PreloadImageTask extends AsyncTask<String, String, Bitmap> {
        @Override
        protected Bitmap doInBackground(String[] params) {
            try {
                String url = params[0];
                return getImageBitmap(url);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap retrievedBitmap){
            bitmap = retrievedBitmap;
            if (bitmap == null) {
                for(BitmapLoadListener listener: listeners) {
                    listener.onError();
                }

            } else {
                loaded = true;
                for(BitmapLoadListener listener: listeners) {
                    listener.onSuccess();
                }
            }

        }

        private Bitmap getImageBitmap(String url) throws IOException {
            Bitmap bm = null;
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            try {
                bm = BitmapFactory.decodeStream(bis);
            } catch (Exception e) {
                for(BitmapLoadListener listener: listeners) {
                    listener.onError();
                }
                e.printStackTrace();
            }
            bis.close();
            is.close();
            return bm;
        }
    }
    public static abstract class BitmapLoadListener {
        public abstract void onSuccess();
        public abstract void onError();
    }
    public Bitmap getBitmap() {
        return bitmap;
    }
    public boolean isLoaded() {
        return loaded;
    }
}
