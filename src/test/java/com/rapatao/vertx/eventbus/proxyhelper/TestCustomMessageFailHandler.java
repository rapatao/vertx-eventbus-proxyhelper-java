package com.rapatao.vertx.eventbus.proxyhelper;

import com.rapatao.vertx.eventbus.proxyhelper.handler.CustomMessageFailHandler;
import io.vertx.core.eventbus.Message;

public class TestCustomMessageFailHandler implements CustomMessageFailHandler<String> {

    @Override
    public void handle(Message<String> message, Throwable throwable) {
        message.fail(-1, throwable.getMessage() + " - custom handler");
    }

}