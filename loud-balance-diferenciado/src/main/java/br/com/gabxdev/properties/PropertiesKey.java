package br.com.gabxdev.properties;

public enum PropertiesKey {
    BACK_END_1_URL("api.url.back-end-1"),
    BACK_END_1_PORT("port.back-end-1"),
    WORKERS_THREAD_POOL_SIZE("workers.thread.pool.size"),
    IO_THREAD_POOL_SIZE("io.thread.pool.size"),
    UDP_PORT("udp.port");

    private final String key;

    PropertiesKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
