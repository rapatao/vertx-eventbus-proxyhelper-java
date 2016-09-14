package com.rapatao.vertx.eventbus.proxyhelper;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/**
 * Created by rapatao on 13/09/16.
 */
@AllArgsConstructor
public class ProxyServiceCreator {

    private final Vertx vertx;

    public <T> T create(final String prefix, final Class<T> serviceInterface) {
        final Class<T>[] classes = new Class[]{serviceInterface};
        final InvocationHandler invocationHandler = (proxy, method, args) -> {

            final Type returnType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];

            Future<Object> future = Future.future();
            final String address = prefix + "#" + method.getDeclaringClass().getName() + "#" + method.getName();

            vertx.eventBus().<String>send(address, Json.encode(args[0]), handler -> {
                if (handler.succeeded()) {
                    future.complete(Json.decodeValue(handler.result().body(), ((Class) returnType)));
                } else {
                    future.fail(handler.cause());
                }
            });

            return future;
        };

        return (T) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), classes, invocationHandler);
    }

    public <T> T create(Class<T> serviceInterface) {
        return create("", serviceInterface);
    }

}
