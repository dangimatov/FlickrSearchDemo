package com.dgimatov.flickrsearchdemo.search.model;

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
public class FlickrSearchApiClient implements ImagesSearchRepository {

    private static final String API_KEY = "3e7cc266ae2b0e0d78e279ce8e361736";
    private static final String FLICKR_SEARCH_API_SCHEMA = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=%s&format=json&nojsoncallback=1&safe_search=1&text=%s&page=%s";
    private static final int CONNECT_TIMEOUT_MILLIS = 1000;

    private final ThreadPoolExecutor threadPoolExecutor;
    private final PageDeserializer pageDeserializer;

    private final Map<String, Listener<Page>> listeners = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Runnable> tasks = Collections.synchronizedMap(new HashMap<>());

    public FlickrSearchApiClient(ThreadPoolExecutor threadPoolExecutor, PageDeserializer pageDeserializer) {
        this.threadPoolExecutor = threadPoolExecutor;
        this.pageDeserializer = pageDeserializer;
    }

    private void makeSearchRequest(String requestUrl) {
        Runnable runnable = () -> {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(requestUrl);
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

    @Override
    public void subscribe(String text, int page, Listener<Page> listener) {
        unsubscribeAll();
        String requestUrl = buildRequestUrl(text, page);
        listeners.put(requestUrl, listener);
        makeSearchRequest(requestUrl);
    }

    private String buildRequestUrl(String text, int page) {
        return String.format(FLICKR_SEARCH_API_SCHEMA, API_KEY, text, page);
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
