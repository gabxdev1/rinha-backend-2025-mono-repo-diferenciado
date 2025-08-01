package br.com.gabxdev.properties;

public enum PropertiesKey {
    WORKERS_THREAD_POOL_SIZE("workers.thread.pool.size"),
    IO_THREAD_POOL_SIZE("io.thread.pool.size"),
    BACK_END_1_URL("api.url.back-end-1"),
    BACK_END_2_URL("api.url.back-end-2"),
    BACK_END_1_PORT("port.back-end-1"),
    BACK_END_2_PORT("port.back-end-2");

    private final String key;

    PropertiesKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
