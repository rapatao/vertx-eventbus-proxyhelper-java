package com.rapatao.vertx.eventbus.proxyhelper;

import com.rapatao.vertx.eventbus.proxyhelper.handler.CustomMessageFailHandler;
import com.rapatao.vertx.eventbus.proxyhelper.handler.MessageFailHandler;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by rapatao on 15/09/16.
 */
class EventBusConsumerHandler {

    private final static Logger logger = LoggerFactory.getLogger(EventBusConsumerHandler.class);

    private final Method method;
    private final Class<?>[] attributes;
    private final Object instance;

    public EventBusConsumerHandler(final Method method, final Object instance) {
        this.method = method;
        this.attributes = method.getParameterTypes();
        this.instance = instance;
    }

    public void handle(Message<String> handler) {
        try {
            Future<Object> future = (Future<Object>) method.invoke(instance, Json.decodeValue(handler.body(), attributes[0]));
            future.setHandler(futureHandler -> {
                if (futureHandler.succeeded()) {
                    handler.reply(Json.encode(futureHandler.result()));
                } else {
                    handleFail(method, instance, handler, future);
                }
            });
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private <T> void handleFail(Method method, T instance, Message<String> handler, Future<Object> future) {
        final CustomMessageFailHandler customMessageFailHandler = getCustomMessageFailHandler(method, instance);
        if (customMessageFailHandler != null) {
            customMessageFailHandler.handle(handler, future.cause());
        } else {
            handler.fail(-1, future.cause().getMessage());
        }
    }

    private <T> CustomMessageFailHandler getCustomMessageFailHandler(Method method, T instance) {
        try {
            MessageFailHandler messageFailHandlerAnnotation =
                    instance.getClass().getMethod(method.getName(), method.getParameterTypes()).getAnnotation(MessageFailHandler.class);
            if (messageFailHandlerAnnotation != null) {
                final Class<? extends CustomMessageFailHandler> messageFailHandlerClass = messageFailHandlerAnnotation.value();
                return messageFailHandlerClass.getConstructor().newInstance();
            }
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}
