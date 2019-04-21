package com.dgimatov.flickrsearchdemo.search.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Loads remote images and caches them using LRU cache
 */
public final class CachingRemoteImageLoader implements ImageLoaderRepository {

    private static final int MAX_CACHE_SIZE_BYTES = 10 * 1024 * 1024; //~60 images
    private static final int CONNECT_TIMEOUT_MILLIS = 500;
    private static final int LOAD_TIMEOUT_MILLIS = 1500;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private final Map<String, Listener<Bitmap>> listeners = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Runnable> tasks = Collections.synchronizedMap(new HashMap<>());

    private static final ThreadPoolExecutor threadPoolExecutor =
            (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static final LruCache<String, Bitmap> bitmapCache = new LruCache<String, Bitmap>(MAX_CACHE_SIZE_BYTES) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return value.getAllocationByteCount();
            } else {
                return value.getByteCount();
            }
        }
    };

    private CachingRemoteImageLoader() {
    }

    @Override
    public void subscribe(String url, Listener<Bitmap> callback) {
        listeners.put(url, callback);
        loadImage(url);
    }

    @Override
    public void unsubscribe(String url) {
        listeners.remove(url);
        threadPoolExecutor.remove(tasks.get(url));
        tasks.remove(url);
    }

    @Override
    public void unsubscribeAll() {
        listeners.clear();
        Set<String> urls = tasks.keySet();
        synchronized (tasks) {
            for (String url : urls) {
                threadPoolExecutor.remove(tasks.get(url));
            }
        }
        tasks.clear();
    }

    private Runnable fetchRemoteRunnable(String url) {
        return () -> {
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
                httpURLConnection.setReadTimeout(LOAD_TIMEOUT_MILLIS);
                InputStream is = httpURLConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                bitmapCache.put(url, bitmap);
                uiHandler.post(() -> {
                    Listener<Bitmap> listener = listeners.get(url);
                    if (listener != null) {
                        listener.onNext(bitmap);
                        unsubscribe(url);
                    }
                });
            } catch (Exception e) {
                uiHandler.post(() -> {
                    Listener<Bitmap> listener = listeners.get(url);
                    if (listener != null) {
                        listener.onError(e);
                        unsubscribe(url);
                    }
                });
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        };
    }

    private void loadImage(String url) {
        Bitmap cachedBitmap = bitmapCache.get(url);
        if (cachedBitmap != null) {
            Listener<Bitmap> listener = listeners.get(url);
            if (listener != null) {
                listener.onNext(cachedBitmap);
                unsubscribe(url);
            }
        } else {
            Runnable fetchRemoteRunnable = fetchRemoteRunnable(url);
            tasks.put(url, fetchRemoteRunnable);
            threadPoolExecutor.execute(fetchRemoteRunnable);
        }
    }

    private static class InstanceHolder {
        static final CachingRemoteImageLoader INSTANCE = new CachingRemoteImageLoader();
    }

    /**
     * @return instance of the class
     */
    public static CachingRemoteImageLoader getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
