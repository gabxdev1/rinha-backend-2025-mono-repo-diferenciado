package br.com.gabxdev.client;

import br.com.gabxdev.config.UnixSocketConfig;
import org.newsclub.net.unix.AFUNIXDatagramSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpClient {
    private static final UdpClient INSTANCE = new UdpClient();

    private UdpClient() {
    }

    public static UdpClient getInstance() {
        return INSTANCE;
    }

    public void send(DatagramPacket datagramPacket, DatagramSocket socket) {
        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(DatagramPacket datagramPacket, AFUNIXDatagramSocket socket) {
        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
