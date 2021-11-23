package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.model.Withdrawal;
import com.payprovider.withdrawal.model.WithdrawalScheduled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.amqp.core.Binding;

@Slf4j
@Service
public class EventsService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Binding binding;

    @Async
    void send(Withdrawal withdrawal) {
        log.info("Sending message to the queue");
        rabbitTemplate.convertAndSend(binding.getExchange(), binding.getRoutingKey(), withdrawal);
        log.info("Message sent successfully to the queue");
    }

    @Async
    void send(WithdrawalScheduled withdrawal) {
        log.info("Sending message to the queue");
        rabbitTemplate.convertAndSend(binding.getExchange(), binding.getRoutingKey(), withdrawal);
        log.info("Message sent successfully to the queue");
    }
}
