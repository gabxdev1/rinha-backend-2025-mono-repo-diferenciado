package br.com.gabxdev.consumer;

import br.com.gabxdev.config.UnixSocketConfig;
import br.com.gabxdev.model.Payment;
import br.com.gabxdev.worker.PaymentWorker;
import org.newsclub.net.unix.AFUNIXDatagramSocket;

import java.io.IOException;
import java.net.DatagramPacket;

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

        while (true) {
            var buffer = new byte[160];

            var packet = new DatagramPacket(buffer, buffer.length);

            socket.receive(packet);

            paymentWorker.enqueue(new Payment(System.currentTimeMillis(), packet.getData(), packet.getLength()));
        }
    }
}
