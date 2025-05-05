package com.cc.trade;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MqTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Test
    public void test() {
        String exchangeName = "trade.direct";
        String routingKey = "order.query";
        String message = "Hello World";
        //100 个线程
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
    }
}
