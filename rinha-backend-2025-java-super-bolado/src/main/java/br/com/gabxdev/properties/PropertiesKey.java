package br.com.gabxdev.properties;

public enum PropertiesKey {
    UDP_CHANNEL_PORT("udp.channel.port"),
    UDP_CHANNEL_INTERNAL_PORT("udp.channel.internal.port"),
    EXTERNAL_UDP_HOST("external.udp.host"),
    EXTERNAL_UDP_PORT("external.udp.port"),
    WORKER_POOL_SIZE("worker.pool.size"),
    QUEUE_BUFFER("queue.buffer"),
    RETRY_API_DEFAULT("retry.api.default"),
    URL_PROCESSOR_DEFAULT("url.processor.default"),
    URL_PROCESSOR_FALLBACK("url.processor.fallback"),
    HANDLER_UDP_POOL_SIZE("handler.udp.pool-size");

    private final String key;

    PropertiesKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
