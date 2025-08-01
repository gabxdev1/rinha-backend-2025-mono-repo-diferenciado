package br.com.gabxdev.config;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConfig {

    private final static ServerConfig INSTANCE = new ServerConfig();

    private final int ioThreadPoolSize;

    private final int workersThreadPoolSize;

    private final ExecutorService workersThreadPool;

    private ServerConfig() {
        var applicationProperties = ApplicationProperties.getInstance();

        ioThreadPoolSize = Integer.parseInt(applicationProperties.getProperty(PropertiesKey.IO_THREAD_POOL_SIZE));
        workersThreadPoolSize = Integer.parseInt(applicationProperties.getProperty(PropertiesKey.WORKERS_THREAD_POOL_SIZE));

        workersThreadPool = Executors.newFixedThreadPool(workersThreadPoolSize, Thread.ofVirtual().factory());
    }

    public static ServerConfig getInstance() {
        return INSTANCE;
    }

    public ExecutorService getWorkersThreadPool() {
        return workersThreadPool;
    }

    public int getWorkersThreadPoolSize() {
        return workersThreadPoolSize;
    }

    public int getIoThreadPoolSize() {
        return ioThreadPoolSize;
    }
}
