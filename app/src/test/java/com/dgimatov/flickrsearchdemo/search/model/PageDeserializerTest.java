package com.dgimatov.flickrsearchdemo.search.model;

import org.json.JSONException;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class PageDeserializerTest {

    private final String JSON = "{\n" +
            "\"photos\": {\n" +
            "\"page\": 1,\n" +
            "\"pages\": 32,\n" +
            "\"perpage\": 100,\n" +
            "\"total\": \"7\",\n" +
            "\"photo\": [\n" +
            "{\n" +
            "\"id\": \"43052905061\",\n" +
            "\"owner\": \"164374504@N04\",\n" +
            "\"secret\": \"ae07ea2755\",\n" +
            "\"server\": \"1801\",\n" +
            "\"farm\": 2,\n" +
            "\"title\": \"WOW! New Toyota Supra Likely UK Release Date And Price\",\n" +
            "\"ispublic\": 1,\n" +
            "\"isfriend\": 0,\n" +
            "\"isfamily\": 0\n" +
            "},\n" +
            "{\n" +
            "\"id\": \"26405063166\",\n" +
            "\"owner\": \"124179203@N07\",\n" +
            "\"secret\": \"085f53c4d0\",\n" +
            "\"server\": \"1638\",\n" +
            "\"farm\": 2,\n" +
            "\"title\": \"Levin game strong!! Love how random queen square can be! You literally get every type of car down there, all bases covered!!  #queensquarebreakfastmeet #avenuedriversclub #bristol #bristolcarsandcoffee #toyota #toyotacorolla #corolla #corollaae86 #toyotaa\",\n" +
            "\"ispublic\": 1,\n" +
            "\"isfriend\": 0,\n" +
            "\"isfamily\": 0\n" +
            "}]}}";

    @Test
    public void deserializePage() throws JSONException {
        Page expectedPage = new Page(1, 32, Arrays.asList(
                new ImageUrl("https://farm2.staticflickr.com/1801/43052905061_ae07ea2755", ".jpg"),
                new ImageUrl("https://farm2.staticflickr.com/1638/26405063166_085f53c4d0", ".jpg")));
        Page page = new PageDeserializer().deserialize(JSON);
        assertEquals(expectedPage, page);

    }
}