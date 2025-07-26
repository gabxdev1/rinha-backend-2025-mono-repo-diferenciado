package br.com.gabxdev.commons;

public enum MediaType {
    APPLICATION_JSON("application/json");

    private final String value;

    MediaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
