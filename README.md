# Vertx EventBus Proxy Helper for Java

Provides a simple and useful way to encapsulate the Vertx EvenBus.

## Setup

```xml
<groupId>com.rapatao</groupId>
<artifactId>vertx-eventbus-proxyhelper-java</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>jar</packaging>
```

## How to register a service?
The ServiceRegister class get all methods of an interface provided by the given instance and will create a Vertx EventBus Consumer for each method.

```java
new ServiceRegister(vertx).registry(new ServiceImplementationInstance());
```

## Consuming a service
The ProxyServiceCreator create an instance of given Service that will call the Vertx EventBus using the "send" method and will return a Future<T> with the handler result.

```java
ServiceInterface service = new ProxyServiceCreator(vertx).create(ServiceInterface.class);
...
Future<String> stringFuture = service.someMethod("test");
stringFuture.setHandler(handler -> {
    context.assertEquals("future complete: test 1", handler.result());
    async.complete();
});
```

## Limitations

- All methods must return a Future<T>.
- The service implementation must have only one interface
- The service methods must have only one argument (to be improved).