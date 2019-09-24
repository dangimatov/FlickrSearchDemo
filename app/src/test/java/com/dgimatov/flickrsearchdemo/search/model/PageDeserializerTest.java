package com.dgimatov.flickrsearchdemo.search.model;

import org.json.JSONException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class PageDeserializerTest {

    private final String JSON = "{\n" +
            "\"data\": [\n" +
            "{\n" +
            "\"images\": {\n" +
            "\n" +
            "\"downsized\": {\n" +
            "\"url\": \"https://media2.giphy.com/media/IvtWZCzn61hmM/giphy-downsized.gif\",\n" +
            "\"width\": \"500\",\n" +
            "\"height\": \"313\",\n" +
            "\"size\": \"417223\"\n" +
            "}\n" +
            "}\n" +
            "}\n" +
            "],\n" +
            "\"pagination\": {\n" +
            "\"total_count\": 26428,\n" +
            "\"count\": 1,\n" +
            "\"offset\": 0\n" +
            "}\n" +
            "}";

    @Test
    public void deserializePage() throws JSONException {
        Page expectedPage = new Page(1, 26428, Collections.singletonList(
                new ImageUrl("https://media2.giphy.com/media/IvtWZCzn61hmM/giphy-downsized.gif")));
        Page page = new GiphyDeserializer().deserialize(JSON);
        assertEquals(expectedPage, page);

    }
}