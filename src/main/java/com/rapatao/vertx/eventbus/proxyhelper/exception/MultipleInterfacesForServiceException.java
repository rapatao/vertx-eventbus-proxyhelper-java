package com.rapatao.vertx.eventbus.proxyhelper.exception;

/**
 * Created by rapatao on 27/10/16.
 */
public class MultipleInterfacesForServiceException extends RuntimeException {

  public MultipleInterfacesForServiceException() {
    super("The class must have only one interface");
  }

}
