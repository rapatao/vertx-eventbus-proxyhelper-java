package com.rapatao.vertx.eventbus.proxyhelper.helper;

import io.vertx.core.Future;

import java.io.Serializable;

/**
 * Created by rapatao on 13/09/16.
 */
public class InvalidTestServiceImpl implements TestService, Serializable {

    @Override
    public Future<String> stringMethod(String value) {
        return null;
    }

    @Override
    public Future<String> throwMethodWithCustomFailMessageHandler(String value) {
        return null;
    }

    @Override
    public Future<String> throwMethodWithoutCustomFailMessageHandler(String value) {
        return null;
    }
}
