package com.fuzzproductions.fuzzapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.FileObserver;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

public class ImageManager {
    private HashMap imageMap = new HashMap();
    private File cacheDir;

    private ImageQueue imageQueue;

    Thread imageLoaderThread;

    public ImageManager(Context context) {
        imageLoaderThread = new Thread(new ImageQueueManager());

        imageQueue = new ImageQueue();
// Make background thread low priority, to avoid affecting UI performance
        imageLoaderThread.setPriority(Thread.NORM_PRIORITY-1);

// Find the dir to save cached images
        String sdState = android.os.Environment.getExternalStorageState();
        if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
            File sdDir = android.os.Environment.getExternalStorageDirectory();
            cacheDir = new File(sdDir,"data/codehenge");
        }
        else
            cacheDir = context.getCacheDir();

        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public void displayImage(String url, Activity activity, ImageView imageView) {
        if(imageMap.containsKey(url))
            imageView.setImageBitmap((Bitmap)imageMap.get(url));
        else {
            queueImage(url, imageView);
            imageView.setImageResource(R.drawable.stub);
        }
    }

    private void queueImage(String url, ImageView imageView) {
// This ImageView might have been used for other images, so we clear
// the queue of old tasks before starting.
        imageQueue.Clean(imageView);
        ImageRef p=new ImageRef(url, imageView);
        synchronized(imageQueue.imageRefs) {
            imageQueue.imageRefs.push(p);
            imageQueue.imageRefs.notifyAll();
        }

// Start thread if it's not started yet
        if(imageLoaderThread.getState() == Thread.State.NEW)
            imageLoaderThread.start();
    }

    private class ImageRef {
        public String url;
        public ImageView imageView;
        public ImageRef(String u, ImageView i) {
            url=u;
            imageView=i;
        }
    }

    private class ImageQueue {
        private Stack imageRefs = new Stack();
        //removes all instances of this ImageView
        public void Clean(ImageView view) {
            for(int i = 0 ;i < imageRefs.size();) {
                if(((ImageRef)imageRefs.get(i)).imageView == view)
                    imageRefs.remove(i);
                else ++i;
            }
        }
    }

    private class ImageQueueManager implements Runnable {
        @Override
        public void run() {
            try {
                while(true) {
// Thread waits until there are images in the
// queue to be retrieved
                    if(imageQueue.imageRefs.size() == 0) {
                        synchronized(imageQueue.imageRefs) {
                            imageQueue.imageRefs.wait();
                        }
                    }
// When we have images to be loaded
                    if(imageQueue.imageRefs.size() != 0) {
                        ImageRef imageToLoad;

                        synchronized(imageQueue.imageRefs) {
                            imageToLoad = (ImageRef)imageQueue.imageRefs.pop();
                        }

                        Bitmap bmp = getBitmap(imageToLoad);
                        imageMap.put(imageToLoad.url, bmp);
                        Object tag = imageToLoad.imageView.getTag();
                        Log.d("..........", "tag: " + tag );
                        Log.d("..........", "imageToLoad.url: " + imageToLoad.url );
                        // Make sure we have the right view - thread safety defender
                        if(tag != null && ((String)tag).equals(imageToLoad.url)) {
                            Log.d("..........", "BitmapDisplayer display" );
                            BitmapDisplayer bmpDisplayer =
                                    new BitmapDisplayer(bmp, imageToLoad.imageView);
                            Activity a =
                                    (Activity)imageToLoad.imageView.getContext();

                            a.runOnUiThread(bmpDisplayer);
                        }


                    }

                    if(Thread.interrupted())
                        break;
                }
            } catch (InterruptedException e) {}
        }

        private Bitmap getBitmap(final ImageRef imageToLoad) {
            final String urlString = imageToLoad.url;
            String filename = String.valueOf(urlString.hashCode());
            File f = new File(cacheDir, filename);
// Is the bitmap in our cache?
            Bitmap bitmap = BitmapFactory.decodeFile(f.getPath());
            if(bitmap != null) return bitmap;

// Nope, have to download it
            try {
                URL url = new URL(urlString);

                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                InputStream input = connection.getInputStream();

                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inSampleSize = 5;


                bitmap = BitmapFactory.decodeStream(input,null,options);

                if (bitmap.getWidth() > 400){
                    bitmap = Bitmap.createScaledBitmap(bitmap, 400,bitmap.getHeight() * ((bitmap.getWidth() - 400)/bitmap.getWidth()) , false);
                }

                if (bitmap.getWidth() < 200){
                    bitmap = Bitmap.createScaledBitmap(bitmap, 200,bitmap.getHeight() * (200/bitmap.getWidth()) , false);
                }

                Log.d("..........", "gotBitmap null?: " + (bitmap == null));

// save bitmap to cache for later
                writeFile(bitmap, f);

                return bitmap;
            } catch (Exception ex) { ex.printStackTrace(); return null; }
        }

        private void writeFile(Bitmap bmp, File f) {
            FileOutputStream out = null;

            try {
                out = new FileOutputStream(f);
                bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
            } catch (Exception e) { e.printStackTrace(); }
            finally {
                try {
                    if (out != null ) out.close();
                } catch(Exception ex) {}
            }
        }
    }

    //Used to display bitmap in the UI thread
    private class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        ImageView imageView;
        public BitmapDisplayer(Bitmap b, ImageView i) {
            bitmap=b;
            imageView=i;
        }

        public void run() {
            Log.d("...........", "BidmapDisplayer");
            if(bitmap != null)
                imageView.setImageBitmap(bitmap);
            else
                imageView.setVisibility(View.GONE);
        }
    }

}