package br.com.gabxdev.producer;

import br.com.gabxdev.config.ChannelConfig;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class EventProducer {

    private final static EventProducer INSTANCE = new EventProducer();

    private final DatagramSocket client = ChannelConfig.getInstance().getClientUdp();

    private EventProducer() {
    }

    public static EventProducer getInstance() {
        return INSTANCE;
    }

    public void sendEvent(byte[] event) {
        try {
            client.send(new DatagramPacket(event, event.length));
        } catch (Exception e) {
            System.out.println("[EventProducer] Error ao enviar evento");
        }
    }
}
