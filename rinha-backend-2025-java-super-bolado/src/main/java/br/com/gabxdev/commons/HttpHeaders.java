package br.com.gabxdev.commons;

public enum HttpHeaders {
    CONTENT_TYPE("Content-Type");

    private final String value;

    HttpHeaders(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
