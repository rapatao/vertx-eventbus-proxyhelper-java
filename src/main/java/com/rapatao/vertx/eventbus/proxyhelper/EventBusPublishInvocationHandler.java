package com.rapatao.vertx.eventbus.proxyhelper;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by rapatao on 15/09/16.
 */
@RequiredArgsConstructor
class EventBusPublishInvocationHandler implements InvocationHandler {

    private final EventBus eventBus;
    private final String prefix;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final String address = prefix + "#" + method.getDeclaringClass().getName() + "#" + method.getName();
        eventBus.publish(address, Json.encode(args[0]));
        return null;
    }

}
