package br.com.gabxdev.producer;

import br.com.gabxdev.config.ChannelConfig;
import br.com.gabxdev.lb.LoudBalance;
import io.undertow.util.StatusCodes;
import org.newsclub.net.unix.AFUNIXDatagramSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.List;

public class PaymentPostProducer {

    private static PaymentPostProducer INSTANCE = new PaymentPostProducer();

    private final List<AFUNIXDatagramSocket> sockets = ChannelConfig.getInstance().getDatagramSockets();

    private final LoudBalance loudBalance = LoudBalance.getInstance();

    private PaymentPostProducer() {
    }

    public static PaymentPostProducer getInstance() {
        return INSTANCE;
    }

    public int callAnyApi(byte[] event) {
        var client = loudBalance.selectBackEnd(sockets);

        try {
            client.send(new DatagramPacket(event, event.length));

            return StatusCodes.OK;
        } catch (IOException e) {
            return StatusCodes.INTERNAL_SERVER_ERROR;
        }
    }
}
