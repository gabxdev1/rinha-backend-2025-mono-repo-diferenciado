package br.com.gabxdev.mapper;

public class JsonParse {
    private final static String key = "\"correlationId\":";

    public static String extractUUIDFromRequest(String json) {
        var start = json.indexOf('"', json.indexOf(key) + key.length()) + 1;
        var end = json.indexOf('"', start);

        return json.substring(start, end);
    }
}
