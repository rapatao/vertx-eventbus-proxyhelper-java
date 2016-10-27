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

interface Service {
  Future<String> someMethod(String argument);
}

/**
 * Created by rapatao on 13/09/16.
 */
@RunWith(VertxUnitRunner.class)
public class ServiceRegistryTest {

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
    ServiceRegistry.toEventBus(vertx.eventBus()).withPrefix("").to(new InvalidTestServiceImpl()).registry();
  }

}
