package io.displayio.sdk.ads.tools;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import io.displayio.sdk.Controller;
import io.displayio.sdk.R;

public class FileLoader {
    boolean isFileLoaded = false;
    OnLoadedListener listener;
    String filePath;
    public int fileRes;
    String url;
    PreloadFileTask loadTask;

    public FileLoader(String url){
        this.url = url;
//        filePath = Controller.getInstance().getContext().getFilesDir() + File.separator + url.split("/")[url.split("/").length - 1];
        filePath = "android.resource://io.display.sdk/" + url;
    }

    public FileLoader(int fileRes){
//        this.url = url;
//        filePath = Controller.getInstance().getContext().getFilesDir() + File.separator + url.split("/")[url.split("/").length - 1];
        this.fileRes = fileRes;
    }

    public void setListener(OnLoadedListener listener) {
        this.listener = listener;
    }

    public void exec() {
//        File file = new File(filePath);
//        if(!file.exists() || file.length() == 0) {
//            setIsFileLoaded(false);
//           loadTask = new PreloadFileTask();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                loadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, filePath);
//            } else {
//                loadTask.execute(url, filePath);
//            }
//        }else{
//            if(!file.canRead()) {
//                file.setReadable(true);
//            }
//            if(file.canRead()) {
                setIsFileLoaded(true);
                if (listener != null)
                    listener.onLoaded();
//            }
//        }
    }


    public static abstract class OnLoadedListener {
        public abstract void onLoaded();

        public abstract void onLoadError();
    }
    protected void setIsFileLoaded(boolean loaded) {
        isFileLoaded = loaded;
    }

    public boolean isFileLoaded() {
        return isFileLoaded;
    }
    public Uri getUri() {
//        Uri uri;
//        String mFilePath = Controller.getInstance().getContext().getFilesDir() + File.separator + url.split("/")[url.split("/").length - 1];
//        File mFile = new File(mFilePath);
//        if(!mFile.canRead()) {
//            mFile.setReadable(true);
//        }
//        if (isFileLoaded() && mFile.exists() && mFile.length() > 0)
//            uri = Uri.parse(mFilePath);
//        else
//            uri = Uri.parse(url);

//        return uri;
        return Uri.parse("android.resource://" + Controller.getInstance().getContext().getPackageName() + "/" + R.raw.interstitial_video_1_landscape_no_landing_card);
    }

    public Uri getResUri(){
        return Uri.parse("android.resource://" + Controller.getInstance().getContext().getPackageName() + "/" + fileRes);
    }
    class PreloadFileTask extends AsyncTask<String, String, Boolean> {


        @Override
        protected Boolean doInBackground(String[] params) {
            URL aURL;
            String mUrl = params[0];
            String mFilePath = params[1];
            if(!new File(mFilePath).exists()) {
                try {
                    aURL = new URL(mUrl);

                    URLConnection conn = null;

                    conn = aURL.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();

                    BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
                    FileOutputStream outStream = new FileOutputStream(mFilePath);
                    byte[] buff = new byte[5 * 1024];

                    //Read bytes (and store them) until there is nothing more to read(-1)
                    int len;
                    while ((len = inStream.read(buff)) != -1)
                        outStream.write(buff, 0, len);

                    outStream.flush();
                    outStream.close();
                    inStream.close();
                    is.close();
                    setIsFileLoaded(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    if(listener != null)
                        listener.onLoadError();
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(listener != null)
                listener.onLoaded();
        }
    }
}
