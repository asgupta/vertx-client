package com.godaddy.domains.integration.java;
/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import com.godaddy.domains.HttpClientWorker;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.testtools.TestVerticle;

import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertNotNull;
import static org.vertx.testtools.VertxAssert.assertTrue;
import static org.vertx.testtools.VertxAssert.testComplete;

/**
 * Example Java integration test that deploys the module that this project builds.
 *
 * Quite often in integration tests you want to deploy the same module for all tests and you don't want tests
 * to start before the module has been deployed.
 *
 * This test demonstrates how to do that.
 */

public class AsyncHTTPClientCallsTest extends TestVerticle {


    @Test
  public void testPing() {
        int index = 0;
        final int[] failCount = {0};
        container.deployVerticle(HttpClientWorker.class.getName(),10, new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> asyncResult) {
                // Deployment is asynchronous and this this handler will be called when it's complete (or failed)
                assertTrue(asyncResult.succeeded());
                assertNotNull("deploymentID should not be null", asyncResult.result());
                // If deployed correctly then start the tests!

            }
        });
        for (;index < 50; index++) {

      container.logger().info("in testPing() " + index);
            final String finalI = String.valueOf(index);
            final int finalIndex = index;
            vertx.eventBus().sendWithTimeout("get-address", finalI, 10000, new Handler<AsyncResult<Message<String>>>() {
                public void handle(AsyncResult<Message<String>> result) {
                    if (result.succeeded()) {
                       assertTrue(result.result().body().contains(finalI));
                        //    assertEquals("pong!", reply.body());
                    }
                    else
                    {
                        failCount[0] = failCount[0] +1;
                        fail();

                    }
        /*
        If we get here, the test is complete
        You must always call `testComplete()` at the end. Remember that testing is *asynchronous* so
        we cannot assume the test is complete by the time the test method has finished executing like
        in standard synchronous tests
        */

                    container.logger().info("Completed the tests :: " + finalIndex);
                    System.out.println("Failed tests total " + failCount[0]);
                    testComplete();


                }
            });

        }


    }




}
