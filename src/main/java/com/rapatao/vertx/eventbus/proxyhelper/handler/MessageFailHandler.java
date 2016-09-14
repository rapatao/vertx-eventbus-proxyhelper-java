package com.rapatao.vertx.eventbus.proxyhelper.handler;

import java.lang.annotation.*;

/**
 * Created by rapatao on 13/09/16.
 */

@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageFailHandler {

    Class<? extends CustomMessageFailHandler> value();
}
