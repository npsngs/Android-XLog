package com.forthe.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private boolean isDebug = true;
    private void log(String log){
        if(isDebug && !TextUtils.isEmpty(log)){
            Log.d("[ImageLoader]", log);
        }
    }


    private static ImageLoader instance;
    public static ImageLoader getInstance(Context context){
        if(Looper.getMainLooper() != Looper.myLooper()){
            throw new IllegalThreadStateException("please call from main thread");
        }
        if(instance == null){
            instance = new ImageLoader(context.getApplicationContext());
        }
        return instance;
    }




    private WeakReference<Context> contextRef;
    private ExecutorService executorService;
    private LruMemoryCache memoryCache;
    private Map<String, LoadTask> tasks;
    private SparseArray<String> L;
    private Handler handler;
    public ImageLoader(Context context) {
        this.contextRef = new WeakReference<>(context);
        this.executorService = Executors.newFixedThreadPool(2);
        L = new SparseArray<>();
        tasks = new HashMap<>();
        memoryCache = new LruMemoryCache(1024*1024*10);
        handler = new Handler(Looper.getMainLooper());
    }

    public void loadImage(String uri, ImageView iv, OnLoadFinishListener loadFinishListener){
        if(Looper.getMainLooper() != Looper.myLooper()){
            throw new IllegalThreadStateException("please call from main thread");
        }

        if(TextUtils.isEmpty(uri) || iv == null){
            return;
        }
        String lastUri = L.get(iv.hashCode());
        if(lastUri != null){
            LoadTask lastTask = tasks.get(lastUri);
            if(lastTask != null && LoadTask.STATUS_WAIT == lastTask.getStatus()){
                if(lastUri.equals(uri)){
                    return;
                }else{
                    lastTask.cancel();
                    log("cancel task");
                }
            }
        }

        Size size = DecodeInfo.getSize(iv);
        Bitmap bitmap = memoryCache.get(uri+size.w+"*"+size.h);
        if(bitmap != null && !bitmap.isRecycled()){
            iv.setImageBitmap(bitmap);
            if(loadFinishListener != null){
                loadFinishListener.onLoadFinish(bitmap, iv);
            }
            log("hit cache");
            return;
        }

        L.put(iv.hashCode(), uri);
        LoadTask task = new LoadTask(uri, iv, size, loadFinishListener);
        tasks.put(uri, task);
        executorService.execute(task);
    }

    public void loadImage(String uri, ImageView iv){
        loadImage(uri, iv, null);
    }

    class LoadTask implements Runnable{
        static final int STATUS_WAIT = 1;
        static final int STATUS_LOADING = 2;
        static final int STATUS_LOADED = 3;
        static final int STATUS_CANCEL = 4;
        private int status;
        private boolean isCancel = false;
        private String uri;
        private WeakReference<ImageView> ivRef;
        private OnLoadFinishListener loadFinishListener;
        private Size size;
        public LoadTask(String uri, ImageView iv, Size size, OnLoadFinishListener loadFinishListener) {
            this.uri = uri;
            this.size = size;
            this.loadFinishListener = loadFinishListener;
            status = STATUS_WAIT;
            ivRef = new WeakReference<>(iv);
        }

        @Override
        public void run() {
            ImageView iv = ivRef.get();
            if(iv == null || isCancel){
                status = STATUS_CANCEL;
                tasks.remove(uri);
                return;
            }

            status = STATUS_LOADING;
            try {

                Stream stream = loadFrom(uri);
                InputStream is  = stream.is;
                DecodeInfo decodeInfo = DecodeInfo.caculate(is, uri, size);
                try {
                    is.reset();
                } catch (IOException e) {
                    close(stream);
                    stream = loadFrom(uri);
                    is  = stream.is;
                }

                final Bitmap bitmap = decodeFrom(is, decodeInfo);

                close(stream);

                if(bitmap != null){
                    memoryCache.put(uri+size.w+"*"+size.h, bitmap);
                    iv = ivRef.get();
                    if(iv != null){
                        String lastUri = L.get(iv.hashCode());
                        if(uri.equals(lastUri)){
                            handler.post(new UpdateAction(bitmap, iv, loadFinishListener));
                        }
                    }
                }
            }catch (Throwable e){
                e.printStackTrace();
            }

            status = STATUS_LOADED;
            tasks.remove(uri);
        }

        private void close(Stream stream){
            try {
                stream.is.close();
                if(stream.extra instanceof HttpURLConnection){
                    ((HttpURLConnection)stream.extra).disconnect();
                }
            } catch (Exception e) {
            }
        }

        public int getStatus() {
            return status;
        }

        public void cancel() {
            isCancel = true;
        }
    }

    public interface OnLoadFinishListener{
        void onLoadFinish(Bitmap bitmap, ImageView iv);
    }

    class UpdateAction implements Runnable{
        private Bitmap bitmap;
        private ImageView iv;
        private OnLoadFinishListener loadFinishListener;
        public UpdateAction(Bitmap bitmap, ImageView iv, OnLoadFinishListener loadFinishListener) {
            this.loadFinishListener = loadFinishListener;
            this.bitmap = bitmap;
            this.iv = iv;
        }
        @Override
        public void run() {
            iv.setImageBitmap(bitmap);
            if(loadFinishListener != null){
                loadFinishListener.onLoadFinish(bitmap, iv);
            }
        }
    }

    class LruMemoryCache{
        private int maxSize;
        private int size;
        private LinkedHashMap<String, Bitmap> cache;
        public LruMemoryCache(int maxSize) {
            this.maxSize = maxSize;
            this.cache = new LinkedHashMap<>(0, 0.75f, true);
            size = 0;
        }

        public Bitmap get(String key){
            if (key == null) {
                return null;
            }
            synchronized (this) {
                return cache.get(key);
            }
        }

        public void put(String key, Bitmap bitmap){
            if (key == null || bitmap == null) {
                return;
            }
            synchronized (this) {
                size += getSize(bitmap);
                Bitmap previous = cache.put(key, bitmap);
                if (previous != null) {
                    size -= getSize(previous);
                }
            }
            if(size > 2*maxSize){
                int preSize = size;
                trimToSize();
                log(String.format("cache trim size：%d", preSize - size));
            }
        }

        private void trimToSize(){
            while (true) {
                String key;
                Bitmap value;
                synchronized (this) {
                    if (size < 0 || (cache.isEmpty() && size != 0)) {
                        throw new IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");
                    }

                    if (size <= maxSize || cache.isEmpty()) {
                        break;
                    }

                    Map.Entry<String, Bitmap> toEvict = cache.entrySet().iterator().next();
                    if (toEvict == null) {
                        break;
                    }
                    key = toEvict.getKey();
                    value = toEvict.getValue();
                    cache.remove(key);
                    size -= getSize(value);
                }
            }
        }

        private int getSize(Bitmap bitmap) {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    static class DecodeInfo{
        private float rotateDegrees;
        private int sampleSize;
        private boolean flip;

        public DecodeInfo(float rotateDegrees, int sampleSize, boolean flip) {
            this.rotateDegrees = rotateDegrees;
            this.sampleSize = sampleSize;
            this.flip = flip;
        }
        private static Size getSize(View v){
            Size size = new Size(0, 0);
            ViewGroup.LayoutParams lp = v.getLayoutParams();
            size.w = v.getWidth();
            size.h = v.getHeight();
            DisplayMetrics displayMetrics = null;
            Context context = v.getContext();
            if(context != null){
                displayMetrics = context.getResources().getDisplayMetrics();
            }

            if(size.w <= 0){
                size.w = lp.width;
                if(size.w <= 0){
                    if(displayMetrics != null){
                        size.w = displayMetrics.widthPixels;
                    }
                }
            }
            if(size.h <= 0){
                size.h = lp.height;
                if(size.h <= 0){
                    if(displayMetrics != null){
                        size.h = displayMetrics.heightPixels;
                    }
                }
            }
            return size;
        }

        public static DecodeInfo caculate(InputStream is, String uri, Size size){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            float rotation = 0f;
            int sampleSize = 1;
            boolean flip = false;
            if("image/jpeg".equalsIgnoreCase(options.outMimeType) && uri.startsWith("file://")){
                try {
                    String file = uri.substring(7);
                    ExifInterface exif = new ExifInterface(file);
                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    switch (exifOrientation) {
                        case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                            flip = true;
                        case ExifInterface.ORIENTATION_NORMAL:
                            rotation = 0;
                            break;
                        case ExifInterface.ORIENTATION_TRANSVERSE:
                            flip = true;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotation = 90;
                            break;
                        case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                            flip = true;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotation = 180;
                            break;
                        case ExifInterface.ORIENTATION_TRANSPOSE:
                            flip = true;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotation = 270;
                            break;
                    }
                } catch (IOException e) {
                   e.printStackTrace();
                }
            }

            if(size.isvalid()){
                int sw = 2*size.w;
                int sh = 2*size.h;
                while (sw < options.outWidth || sh < options.outHeight){
                    sampleSize *= 2;
                    if(sw < options.outWidth)
                        sw *= 2;
                    if(sh < options.outHeight)
                        sh *= 2;
                }
            }

            return new DecodeInfo(rotation, sampleSize, flip);
        }
    }

    static class Size{
        int w,h;
        public Size(int w, int h) {
            this.w = w;
            this.h = h;
        }
        public boolean isvalid(){
            return w>0 && h>0;
        }
    }

    private Bitmap decodeFrom(InputStream is, DecodeInfo decodeInfo) throws Throwable {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig =  Bitmap.Config.RGB_565;
        options.inSampleSize = decodeInfo.sampleSize;
        Bitmap bitmap = null;
        boolean isOom = false;
        try{
            bitmap = BitmapFactory.decodeStream(is, null, options);
        }catch (Throwable t){
            if(t instanceof OutOfMemoryError){
                log("out of memory error");
                isOom = true;
            }
        }

        if(bitmap == null && isOom && decodeInfo.sampleSize < 32){
            decodeInfo.sampleSize *= 2;
            return decodeFrom(is, decodeInfo);
        }

        if(bitmap != null && decodeInfo.rotateDegrees != 0f){
            Matrix m = new Matrix();
            if(decodeInfo.flip){
                m.postScale(-1, 1);
            }
            m.postRotate(decodeInfo.rotateDegrees);
            Bitmap finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap
                    .getHeight(), m, true);
            if (finalBitmap != bitmap) {
                bitmap.recycle();
            }
            bitmap = finalBitmap;
        }
        return bitmap;
    }

    class Stream{
        private InputStream is;
        private Object extra;

        public Stream(InputStream is) {
            this.is = is;
            this.extra = null;
        }

        public Stream(InputStream is, Object extra) {
            this.is = is;
            this.extra = extra;
        }
    }

    private Stream loadFrom(String uri) throws Throwable {
        if(uri.startsWith("http://") || uri.startsWith("https://")){
            HttpURLConnection conn = createConnection(uri);
            for(int redirectCount = 0; conn.getResponseCode() / 100 == 3 && redirectCount < 5; ++redirectCount) {
                conn.disconnect();
                conn = this.createConnection(conn.getHeaderField("Location"));
            }

            if(200 == conn.getResponseCode()) {
                return new Stream(new BufferedInputStream(conn.getInputStream(), 0x8000), conn);
            }else{
                conn.disconnect();
            }
            return null;
        }else if(uri.startsWith("file://")){
            return new Stream(new BufferedInputStream(new FileInputStream(uri.substring(7)), 0x8000));
        }else if(uri.startsWith("drawable://")){
            String drawableIdStr = uri.substring(11);
            int drawableId = Integer.parseInt(drawableIdStr);
            if(contextRef.get() != null){
                return new Stream(contextRef.get().getResources().openRawResource(drawableId));
            }
        }
        return null;
    }

    protected HttpURLConnection createConnection(String url) throws IOException {
        String encodedUrl = Uri.encode(url, "@#&=*+-_.,:!?()/~\'%");
        HttpURLConnection conn = (HttpURLConnection)(new URL(encodedUrl)).openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(20000);
        return conn;
    }
}
