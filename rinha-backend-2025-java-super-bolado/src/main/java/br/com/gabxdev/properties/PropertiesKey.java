package br.com.gabxdev.properties;

public enum PropertiesKey {
    UDP_CHANNEL_INTERNAL_PORT("udp.channel.internal.port"),
    EXTERNAL_UDP_HOST("external.udp.host"),
    EXTERNAL_UDP_PORT("external.udp.port"),
    WORKER_POOL_SIZE("worker.pool.size"),
    RETRY_API_DEFAULT("retry.api.default"),
    URL_PROCESSOR_DEFAULT("url.processor.default"),
    URL_PROCESSOR_FALLBACK("url.processor.fallback"),
    HANDLER_UDP_POOL_SIZE("handler.udp.pool-size"),
    TIMEOUT_PROFESSOR_DEFAULT("timeout.processor.default"),
    SOCKET_PATH("socket.path"),
    LB_UDP_HOST("lb.udp.host"),
    LB_UDP_PORT("lb.udp.port"),
    API_MASTER("api.master"),
    API_UDP_PORT("api.udp.port"),;

    private final String key;

    PropertiesKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
