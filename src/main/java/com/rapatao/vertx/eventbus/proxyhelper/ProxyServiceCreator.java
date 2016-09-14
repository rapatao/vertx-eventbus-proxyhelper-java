package com.rapatao.vertx.eventbus.proxyhelper;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/**
 * Created by rapatao on 13/09/16.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProxyServiceCreator {

    private final Vertx vertx;

    private String prefix = "";

    public static ProxyServiceCreator of(final Vertx vertx) {
        return new ProxyServiceCreator(vertx);
    }

    public ProxyServiceCreator withPrefix(@NonNull final String prefix) {
        this.prefix = prefix;
        return this;
    }

    public <T> T create(@NonNull final Class<T> service) {
        final Class<T>[] classes = new Class[]{service};
        final InvocationHandler invocationHandler = getInvocationHandler();
        return (T) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), classes, getInvocationHandler());
    }

    private InvocationHandler getInvocationHandler() {
        return (proxy, method, args) -> {
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
    }

}
