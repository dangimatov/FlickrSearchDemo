package com.dgimatov.flickrsearchdemo.search.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GiphyDeserializer {

    /**
     * @param json json
     * @return Deserialized {@link Page} object from json string
     * @throws JSONException
     */
    Page deserialize(String json) throws JSONException {
        JSONObject root = new JSONObject(json);
        JSONArray data = root.getJSONArray("data");
        JSONObject pagination = root.getJSONObject("pagination");

        int currentPage = pagination.getInt("count");
        int totalPages = pagination.getInt("total_count");


        List<ImageUrl> imageUrls = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject imageWrapper = data.getJSONObject(i);
            JSONObject images = imageWrapper.getJSONObject("images");
            JSONObject image = images.getJSONObject("downsized");

            String url = image.getString("url");
            imageUrls.add(new ImageUrl(url));
        }
        return new Page(currentPage, totalPages, imageUrls);
    }
}
