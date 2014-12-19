package com.godaddy.domains;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Verticle;

/**
 * Created by agupta on 12/18/2014
 * Package : ${package}
 * Purpose :
 */
public class AsyncHandlerEventBusWithFailure extends Verticle {

    @Override
    public void start(){
        vertx.eventBus().registerHandler("test.failure", new Handler<Message<String>>() {
            public void handle(Message<String> message) {
                message.fail(123, "Not enough aardvarks");
            }
        });
    }
}
