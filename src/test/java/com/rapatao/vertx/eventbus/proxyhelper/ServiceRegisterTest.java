package com.rapatao.vertx.eventbus.proxyhelper;

import com.rapatao.vertx.eventbus.proxyhelper.helper.InvalidTestServiceImpl;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by rapatao on 13/09/16.
 */
@RunWith(VertxUnitRunner.class)
public class ServiceRegisterTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close();
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailToRegistryServiceWithMultiplesInterfaces(TestContext context) {
        ServiceRegister.of(vertx).withPrefix("").to(new InvalidTestServiceImpl()).register();
    }

}

interface Service {
    Future<String> someMethod(String argument);
}

class ServiceImpl implements Service {
    @Override
    public Future<String> someMethod(String argument) {
        Future<String> future = Future.future();
        future.complete("future complete: test 1");
        return future;
    }
}