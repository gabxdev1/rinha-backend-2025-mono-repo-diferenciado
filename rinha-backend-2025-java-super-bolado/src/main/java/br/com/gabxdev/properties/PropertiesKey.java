package br.com.gabxdev.properties;

public enum PropertiesKey {
    UDP_CHANNEL_INTERNAL_PORT("udp.channel.internal.port"),
    EXTERNAL_UDP_HOST("external.udp.host"),
    EXTERNAL_UDP_PORT("external.udp.port"),
    WORKER_POOL_SIZE("worker.pool.size"),
    RETRY_API_DEFAULT("retry.api.default"),
    URL_PROCESSOR_DEFAULT("url.processor.default"),
    URL_PROCESSOR_FALLBACK("url.processor.fallback"),
    TIMEOUT_PROFESSOR_DEFAULT("timeout.processor.default"),
    SOCKET_PATH("socket.path"),
    CLIENT_BACKOFF("client.backoff"),
    WORKERS_THREAD_POOL_SIZE("workers.thread.pool.size"),
    IO_THREAD_POOL_SIZE("io.thread.pool.size");

    private final String key;

    PropertiesKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
