package br.com.gabxdev.consumer;

import br.com.gabxdev.config.UnixSocketConfig;
import br.com.gabxdev.mapper.PaymentMapper;
import br.com.gabxdev.worker.PaymentWorker;
import org.newsclub.net.unix.AFUNIXDatagramSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

public class PaymentPostConsumer {
    private final static PaymentPostConsumer INSTANCE = new PaymentPostConsumer();

    private PaymentPostConsumer() {
        UnixSocketConfig.getInstance().getSockets().forEach(socket -> {
            Thread.startVirtualThread(() -> {
                try {
                    packetsHandler(socket);
                } catch (Exception e) {
                    System.out.println("Error handler consumer payment post: " + e.getMessage());
                }
            });
        });
    }

    public static PaymentPostConsumer getInstance() {
        return INSTANCE;
    }

    private void packetsHandler(AFUNIXDatagramSocket socket) throws IOException {
        var paymentWorker = PaymentWorker.getInstance();

        var buffer = new byte[140];

        var packet = new DatagramPacket(buffer, buffer.length);

        while (true) {
            socket.receive(packet);

            var data = new byte[packet.getLength()];
            System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());

            paymentWorker.enqueue(PaymentMapper.toPayment(data));
        }
    }
}
