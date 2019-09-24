package com.dgimatov.flickrsearchdemo.search.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Api client which retrieves {@link Page}s using Flickr Search API
 */
public class GiphySearchApiClient implements ImagesSearchRepository {

    private static final String API_KEY = "32rweBKqUGkDEfVaVpb6F2U5KFFPSf8e";
    private static final String GIPHY_SEARCH_API_SCHEMA = "http://api.giphy.com/v1/gifs/search?api_key=%s&q=%s&limit=%s&offset=%s";
    private static final int CONNECT_TIMEOUT_MILLIS = 1000;
    private static final int perPage = 20;

    private final ThreadPoolExecutor threadPoolExecutor;
    private final GiphyDeserializer pageDeserializer;

    private final Map<String, Listener<Page>> listeners = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Runnable> tasks = Collections.synchronizedMap(new HashMap<>());

    public GiphySearchApiClient(ThreadPoolExecutor threadPoolExecutor, GiphyDeserializer pageDeserializer) {
        this.threadPoolExecutor = threadPoolExecutor;
        this.pageDeserializer = pageDeserializer;
    }

    private void makeSearchRequest(String requestUrl) {
        Runnable runnable = () -> {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(requestUrl);
                Log.i("test_", "url:" + requestUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                urlConnection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    sb.append(line);
                    line = reader.readLine();
                }

                Listener<Page> listener = listeners.get(requestUrl);
                if (listener != null) {
                    listener.onNext(pageDeserializer.deserialize(sb.toString()));
                }
            } catch (Exception e) {
                Listener<Page> listener = listeners.get(requestUrl);
                if (listener != null) {
                    listener.onError(e);
                }
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        };
        tasks.put(requestUrl, runnable);
        threadPoolExecutor.execute(runnable);
    }

    private int calculateOffset(int perPage, int currentPage) {
        return perPage * currentPage;
    }

    @Override
    public void subscribe(String text, int page, Listener<Page> listener) {
        unsubscribeAll();
        String requestUrl = buildRequestUrl(text, page, calculateOffset(perPage, page));
        listeners.put(requestUrl, listener);
        makeSearchRequest(requestUrl);
    }

    private String buildRequestUrl(String text, int limit, int offset) {
        return String.format(GIPHY_SEARCH_API_SCHEMA, API_KEY, text, limit, offset);
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
}
