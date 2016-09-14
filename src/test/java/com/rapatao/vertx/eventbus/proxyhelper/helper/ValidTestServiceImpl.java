package com.rapatao.vertx.eventbus.proxyhelper.helper;

import com.rapatao.vertx.eventbus.proxyhelper.TestCustomMessageFailHandler;
import com.rapatao.vertx.eventbus.proxyhelper.handler.MessageFailHandler;
import io.vertx.core.Future;

/**
 * Created by rapatao on 13/09/16.
 */
public class ValidTestServiceImpl implements TestService {

    @Override
    public Future<String> stringMethod(String value) {
        Future<String> future = Future.future();

        future.complete("future complete: " + value);

        return future;
    }

    @Override
    @MessageFailHandler(TestCustomMessageFailHandler.class)
    public Future<String> throwMethodWithCustomFailMessageHandler(String value) {
        Future<String> future = Future.future();
        future.fail(new RuntimeException(value));
        return future;
    }

    @Override
    public Future<String> throwMethodWithoutCustomFailMessageHandler(String value) {
        Future<String> future = Future.future();
        future.fail(new RuntimeException(value));
        return future;
    }

}
