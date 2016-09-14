package com.rapatao.vertx.eventbus.proxyhelper.helper;

import com.rapatao.vertx.eventbus.proxyhelper.TestCustomMessageFailHandler;
import com.rapatao.vertx.eventbus.proxyhelper.handler.MessageFailHandler;
import io.vertx.core.Future;

/**
 * Created by rapatao on 13/09/16.
 */
public interface TestService {

    Future<String> stringMethod(String value);

    Future<String> throwMethodWithCustomFailMessageHandler(String value);

    Future<String> throwMethodWithoutCustomFailMessageHandler(String value);

}

