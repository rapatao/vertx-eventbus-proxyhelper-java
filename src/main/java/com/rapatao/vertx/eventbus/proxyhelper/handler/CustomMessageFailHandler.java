package com.rapatao.vertx.eventbus.proxyhelper.handler;

import io.vertx.core.eventbus.Message;

/**
 * Created by rapatao on 13/09/16.
 */
public interface CustomMessageFailHandler<T> {

    void handle(Message<T> message, Throwable throwable);

}
