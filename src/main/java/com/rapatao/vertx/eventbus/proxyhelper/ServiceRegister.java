package com.rapatao.vertx.eventbus.proxyhelper;

import com.rapatao.vertx.eventbus.proxyhelper.handler.CustomMessageFailHandler;
import com.rapatao.vertx.eventbus.proxyhelper.handler.MessageFailHandler;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by rapatao on 13/09/16
 */
@AllArgsConstructor
public final class ServiceRegister {

    private final static Logger logger = LoggerFactory.getLogger(ServiceRegister.class);

    private final Vertx vertx;

    private <T> Class<?> getInterfaceClass(T instance) {
        final Class<?>[] interfaces = instance.getClass().getInterfaces();
        if (interfaces == null) {
            throw new RuntimeException("Class must have an interface");
        }
        if (interfaces.length > 1) {
            throw new RuntimeException("The class must have only one interface");
        }
        return interfaces[0];
    }

    public <T> void registry(final String prefix, final T instance) {
        final Class<?> anInterface = getInterfaceClass(instance);

        for (Method method : anInterface.getMethods()) {
            final String address = prefix + "#" + anInterface.getName() + "#" + method.getName();
            logger.info("Registering consumer for " + address);

            final Class<?> attributeClass = method.getParameterTypes()[0];

            vertx.eventBus().<String>consumer(address, handler -> {
                try {
                    Future<Object> future = (Future<Object>) method.invoke(instance, Json.decodeValue(handler.body(), attributeClass));
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
            });

        }
    }

    public <T> void registry(final T instance) {
        registry("", instance);
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
