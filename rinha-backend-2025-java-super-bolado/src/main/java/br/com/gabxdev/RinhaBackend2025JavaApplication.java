package br.com.gabxdev;

import br.com.gabxdev.mapper.PaymentMapper;
import br.com.gabxdev.ws.Event;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

//@SpringBootApplication
//@RegisterReflectionForBinding()
public class RinhaBackend2025JavaApplication {
    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        PaymentMapper.toPayment(UUID.randomUUID().toString());

        long end = System.currentTimeMillis();


        System.out.println(end - start);


        //39ms - 40ms

//        SpringApplication.run(RinhaBackend2025JavaApplication.class, args);
    }
}
