package com.godaddy.domains.integration.java;

import com.godaddy.domains.AsyncHandlerEventBusWithFailure;
import com.godaddy.domains.AsyncHandlerEventBusWithSuccess;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.eventbus.ReplyException;
import org.vertx.testtools.TestVerticle;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertNotNull;
import static org.vertx.testtools.VertxAssert.assertTrue;
import static org.vertx.testtools.VertxAssert.testComplete;

/**
 * Created by agupta on 12/18/2014
 * Package : ${package}
 * Purpose :
 */
public class AsyncHandlerEventBusTest extends TestVerticle {
    @Override
    public void start() {
        // Make sure we call initialize() - this sets up the assert stuff so assert functionality works correctly
        initialize();

        // Deploy the module - the System property `vertx.modulename` will contain the name of the module so you
        // don't have to hardecode it in your tests

        container.deployVerticle(AsyncHandlerEventBusWithFailure.class.getName(), new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> asyncResult) {
                // Deployment is asynchronous and this this handler will be called when it's complete (or failed)
                assertTrue(asyncResult.succeeded());
                assertNotNull("deploymentID should not be null", asyncResult.result());

            }
        });

        container.deployVerticle(AsyncHandlerEventBusWithSuccess.class.getName(), new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> asyncResult) {
                // Deployment is asynchronous and this this handler will be called when it's complete (or failed)
                assertTrue(asyncResult.succeeded());
                assertNotNull("deploymentID should not be null", asyncResult.result());
                // If deployed correctly then start the tests!
                startTests();
            }
        });
    }

    @Test
      public void testAsyncMessageCallSucess()
    {
        vertx.eventBus().sendWithTimeout("test.success", "This is a message", 1000, new Handler<AsyncResult<Message<String>>>() {
            public void handle(AsyncResult<Message<String>> result) {
                if (result.succeeded()) {
                    System.out.println("I received a reply " + result.result().body());
                    assertEquals("WOOT WOOT WOOT INDEED", result.result().body());
                    testComplete();
                } else {
                    ReplyException ex = (ReplyException) result.cause();
                    System.err.println("Failure type: " + ex.failureType());
                    System.err.println("Failure code: " + ex.failureCode());
                    System.err.println("Failure message: " + ex.getMessage());
                    fail();
                    testComplete();
                }
            }
        });
    }

    @Test
    public void testAsyncMessageCall()
    {
        vertx.eventBus().sendWithTimeout("test.failure", "This is a message", 1000, new Handler<AsyncResult<Message<String>>>() {
            public void handle(AsyncResult<Message<String>> result) {
                if (result.succeeded()) {

                    System.out.println("I received a reply " + result.result().body());
                    fail();
                    testComplete();
                } else {
                    ReplyException ex = (ReplyException) result.cause();
                    System.err.println("Failure type: " + ex.failureType());
                    System.err.println("Failure code: " + ex.failureCode());
                    System.err.println("Failure message: " + ex.getMessage());
                    assertEquals(123, ex.failureCode());
                    testComplete();
                }
            }
        });
    }
}
