package com.dgimatov.flickrsearchdemo.search.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Deserializer for {@link Page}
 */
public class PageDeserializer {

    private static final String IMAGE_URL_SCHEMA_BASE_URL = "https://farm%s.staticflickr.com/%s/%s_%s";

    /**
     * @param json json
     * @return Deserialized {@link Page} object from json string
     * @throws JSONException
     */
    Page deserialize(String json) throws JSONException {
        JSONObject pageContent = new JSONObject(json).getJSONObject("photos");
        int currentPage = pageContent.getInt("page");
        int totalPages = pageContent.getInt("pages");
        JSONArray images = pageContent.getJSONArray("photo");
        List<ImageUrl> imageUrls = new ArrayList<>();
        for (int i = 0; i < images.length(); i++) {
            JSONObject image = images.getJSONObject(i);
            String id = image.getString("id");
            String secret = image.getString("secret");
            String server = image.getString("server");
            int farm = image.getInt("farm");
            String baseImageUrl = String.format(IMAGE_URL_SCHEMA_BASE_URL, farm, server, id, secret);
            imageUrls.add(new ImageUrl(baseImageUrl, ".jpg"));
        }
        return new Page(currentPage, totalPages, imageUrls);
    }
}
