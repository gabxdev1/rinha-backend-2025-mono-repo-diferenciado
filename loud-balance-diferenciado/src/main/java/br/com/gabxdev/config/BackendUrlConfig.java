package br.com.gabxdev.config;

import br.com.gabxdev.socket.BackendAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class BackendUrlConfig {

    @Value("${rinha.api.back-end-1-url}")
    private String backEndUrl1;

    private int backEndPort1;

    @Value("${rinha.api.back-end-2-url}")
    private String backEndUrl2;

    private int backEndPort2;


    public List<BackendAddress> getBackendsAddresses() {
        var socketAddress1 = new BackendAddress(backEndUrl1, backEndPort1);
        var socketAddress2 = new BackendAddress(backEndUrl2, backEndPort2);

        return List.of(socketAddress1, socketAddress2);
    }
}
