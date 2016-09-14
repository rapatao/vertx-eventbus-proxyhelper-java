package com.rapatao.vertx.eventbus.proxyhelper;

import com.rapatao.vertx.eventbus.proxyhelper.helper.TestService;
import com.rapatao.vertx.eventbus.proxyhelper.helper.ValidTestServiceImpl;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by rapatao on 13/09/16.
 */
@RunWith(VertxUnitRunner.class)
public class ServiceRegisterConsumerTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        new ServiceRegister(vertx).registry(new ValidTestServiceImpl());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close();
    }

    @Test
    public void shouldConsumeAndReturnExpectedValue(TestContext context) throws InterruptedException {
        final Async async = context.async();

        TestService testService = new ProxyServiceCreator(vertx).create(TestService.class);

        Future<String> stringFuture = testService.stringMethod("test 1");
        stringFuture.setHandler(handler -> {
            context.assertEquals("future complete: test 1", handler.result());
            async.complete();
        });
    }

    @Test
    public void shouldReturnFailWithCustomHandler(TestContext context) {
        final Async async = context.async();

        TestService testService = new ProxyServiceCreator(vertx).create(TestService.class);

        Future<String> stringFuture = testService.throwMethodWithCustomFailMessageHandler("test");
        stringFuture.setHandler(handler -> {
            Assert.assertTrue(handler.failed());
            Assert.assertNotNull(handler.cause());
            Assert.assertEquals("test - custom handler", handler.cause().getMessage());
            async.complete();
        });
    }

    @Test
    public void shouldReturnFailWithoutCustomHandler(TestContext context) {
        final Async async = context.async();

        TestService testService = new ProxyServiceCreator(vertx).create(TestService.class);

        Future<String> stringFuture = testService.throwMethodWithoutCustomFailMessageHandler("test");
        stringFuture.setHandler(handler -> {
            Assert.assertTrue(handler.failed());
            Assert.assertNotNull(handler.cause());
            Assert.assertEquals("test", handler.cause().getMessage());
            async.complete();
        });
    }

}