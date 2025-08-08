package br.com.gabxdev.config;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public final class LoudBalanceChannelConfig {

    private final static LoudBalanceChannelConfig INSTANCE = new LoudBalanceChannelConfig();

    private DatagramSocket socket;

    private LoudBalanceChannelConfig() {
        var properties = ApplicationProperties.getInstance();

        var isMaster = Boolean.parseBoolean(properties.getProperty(PropertiesKey.API_MASTER));

        if (isMaster) {
            this.socket = createSocketUdp(properties);

            connect(properties);
        }

    }

    public static LoudBalanceChannelConfig getInstance() {
        return INSTANCE;
    }

    private DatagramSocket createSocketUdp(ApplicationProperties properties) {
        var apiPort = Integer.parseInt(properties.getProperty(PropertiesKey.API_UDP_PORT));

        try {
            var socket = new DatagramSocket(apiPort);
            socket.setBroadcast(false);
            System.out.println("[LoudBalanceChannelConfig] Created UDP socket, port: " + apiPort);
            return socket;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar socket UDP, " + e.getMessage());
        }
    }

    private void connect(ApplicationProperties properties) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        var lbHost = properties.getProperty(PropertiesKey.LB_UDP_HOST);
        var lbPort = Integer.parseInt(properties.getProperty(PropertiesKey.LB_UDP_PORT));

        try {
            socket.connect(InetAddress.getByName(lbHost), lbPort);
            System.out.println("[LoudBalanceChannelConfig] Connected to UDP socket, host: " + lbHost + ", port: " + lbPort);
        } catch (UnknownHostException e) {
            System.out.println("Erro ao tentar se conectar ao lb, tentando novamente: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public DatagramSocket getSocket() {
        return socket;
    }
}
