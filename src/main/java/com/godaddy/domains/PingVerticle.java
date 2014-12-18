package com.godaddy.domains;
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

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.platform.Verticle;

/*
This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
public class PingVerticle extends Verticle {

  public void start() {


    vertx.eventBus().registerHandler("ping-address", new Handler<Message<String>>() {
      @Override
      public void handle(final Message<String> message) {
          final Buffer body = new Buffer();
            String req = message.body();
          HttpClient client = vertx.createHttpClient().setPort(8080).setHost("localhost");
         HttpClientRequest request= client.get("/v1/api/domains/"+req + "/" + req, new Handler<HttpClientResponse>() {
             @Override
             public void handle(HttpClientResponse response) {
                 System.out.println("response status " + response.statusCode());

                 response.dataHandler(new Handler<Buffer>() {
                     @Override
                     public void handle(Buffer buffer) {
                         body.appendBuffer(buffer);
                     }
                 });

                 response.endHandler(new Handler<Void>() {

                     @Override
                     public void handle(Void aVoid) {
                         container.logger().info("In the end handler ");
                         System.out.println("The total body received was " + body.length() + " bytes");
                         System.out.println("The total body received was " + body.toString() + " vals");

                         if(body.length()==0)
                             try {
                                 throw new Exception("No response found");
                             } catch (Exception e) {
                                 e.printStackTrace();
                             }
                         message.reply(body.toString());
                         container.logger().info("Sent back pong");
                     }
                 });

             }
         });
          request.putHeader("Content-type", "application/json");
          request.end();
          client.close();

      }

    });

    container.logger().info("PingVerticle started");

  }
}
