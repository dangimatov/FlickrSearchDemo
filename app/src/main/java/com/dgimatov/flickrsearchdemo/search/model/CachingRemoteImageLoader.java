package com.dgimatov.flickrsearchdemo.search.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Loads remote images and caches them using LRU cache
 */
public class CachingRemoteImageLoader implements ImageLoader {
    private final ThreadPoolExecutor threadPoolExecutor;

    private final int MAX_CACHE_SIZE = 10 * 1024 * 1024;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private final Map<String, Listener<Bitmap>> listeners = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Runnable> tasks = Collections.synchronizedMap(new HashMap<>());

    private LruCache<String, Bitmap> bitmapCache = new LruCache<String, Bitmap>(MAX_CACHE_SIZE) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    public CachingRemoteImageLoader(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
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

    private Runnable fetchRemoteRunnable(String url) {
        return () -> {
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                httpURLConnection.setConnectTimeout(500);
                httpURLConnection.setReadTimeout(1000);
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
}
