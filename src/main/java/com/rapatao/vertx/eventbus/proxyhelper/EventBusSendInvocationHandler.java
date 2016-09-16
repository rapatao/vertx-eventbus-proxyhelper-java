package com.rapatao.vertx.eventbus.proxyhelper;

import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by rapatao on 15/09/16.
 */
@RequiredArgsConstructor
class EventBusSendInvocationHandler implements InvocationHandler {

    private final EventBus eventBus;
    private final String prefix;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final Type returnType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
        Future<Object> future = Future.future();
        final String address = prefix + "#" + method.getDeclaringClass().getName() + "#" + method.getName();
        eventBus.<String>send(address, Json.encode(args[0]), handler -> {
            if (handler.succeeded()) {
                future.complete(Json.decodeValue(handler.result().body(), ((Class) returnType)));
            } else {
                future.fail(handler.cause());
            }
        });
        return future;
    }

}
