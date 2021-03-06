# Vertx EventBus Proxy Helper for Java
[![Build Status](https://travis-ci.org/rapatao/vertx-eventbus-proxyhelper-java.svg?branch=master)](https://travis-ci.org/rapatao/vertx-eventbus-proxyhelper-java) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/b86ce732e6e741b1b7e8dafb9deb3bbb)](https://www.codacy.com/app/rapatao/vertx-eventbus-proxyhelper-java?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=rapatao/vertx-eventbus-proxyhelper-java&amp;utm_campaign=Badge_Grade)

Provides a simple and useful way to encapsulate the Vertx EvenBus.

## Setup

Add the dependency to your project and use the provided methods to register and consume the service.
```xml
<dependency>
    <groupId>com.rapatao</groupId>
    <artifactId>vertx-eventbus-proxyhelper-java</artifactId>
    <version>0.0.4</version>
</dependency>
```

The following repository allows you to access the dependency in OSSRH directly, just add the configuration in your "pom.xml" to get the wanted version.
```xml
<repositories>
    <repository>
        <id>sonatype-public-repository</id>
        <name>oss.sonatype.org public repository</name>
        <url>https://oss.sonatype.org/content/groups/public/</url>
    </repository>
</repositories>
```

## How to create and use a service?
The ProxyServiceCreator create an instance of given Service that will call the Vertx EventBus using the "send" method and will return a Future<T> with the handler result.
```java
interface Service {
    Future<String> someMethod(String argument);
}
```

```java
final Service service = ProxyCreator.toEventBus(vertx.eventBus()).asSend(Service.class);

final Future<String> stringFuture = service.someMethod("test");
stringFuture.setHandler(handler -> {
    context.assertEquals("future complete: test 1", handler.result());
    async.complete();
});
```

## How to register a service?
The ServiceRegister class get all methods of an interface provided by the given instance and will create a Vertx EventBus Consumer for each method.

```java
public class ServiceImpl implements Service {
    @Override
    public Future<String> someMethod(String argument) {
        final Future<String> future = Future.future();
        future.complete("future complete: test 1");
        return future;
    }
}
```

```java
ServiceRegistry.toEventBus(vertx.eventBus()).withPrefix("").to(new ServiceImpl()).registry();
```

## Limitations

- The service implementation must have only one interface
- You cannot override any service methods.
- All methods arguments must be decoded with io.vertx.core.json.Json#decode
- When using:
    - "asSend": The service method does need to return a "Future<T>".
    - "AsPublish": The service method does not need to have a return statement.
