/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.grpc.netty;

import io.grpc.internal.ObjectPool;
import io.netty.channel.ChannelHandler;
import io.netty.util.AsciiString;
import java.util.concurrent.Executor;

/**
 * An class that provides a Netty handler to control protocol negotiation.
 * <br>
 * 提供Netty处理程序来控制协议协商的类。
 */
interface ProtocolNegotiator {

  /**
   * The HTTP/2 scheme to be used when sending {@code HEADERS}.
   * <br>
   * 发送 {@code HEADERS} 时要使用的HTTP2方案。
   */
  AsciiString scheme();

  /**
   * Creates a new handler to control the protocol negotiation. Once the negotiation has completed
   * successfully, the provided handler is installed. Must call {@code
   * grpcHandler.onHandleProtocolNegotiationCompleted()} at certain point if the negotiation has
   * completed successfully.
   * <br>
   * 创建一个新的处理程序来控制协议协商。协商成功完成后，将安装提供的处理程序。
   * 如果协商已成功完成，则必须在某些时候调用 {@code grpcHandler.onHandleProtocolNegotiationCompleted()}。
   */
  ChannelHandler newHandler(GrpcHttp2ConnectionHandler grpcHandler);

  /**
   * Releases resources held by this negotiator. Called when the Channel transitions to terminated
   * or when InternalServer is shutdown (depending on client or server). That means handlers
   * returned by {@link #newHandler} can outlive their parent negotiator on server-side, but not
   * on client-side.
   * <br>
   * 释放这位谈判者持有的资源。当通道过渡到终止或InternalServer关闭时 (取决于客户端或服务器) 调用。
   * 这意味着 {@link #newHandler} 返回的处理程序在服务器端可以超过其父谈判者，但在客户端则不能。
   */
  void close();

  interface ClientFactory {
    /**
     * Creates a new negotiator.
     * 创建一个新的谈判者。??
     * 谈判者是什么？
     * */
    ProtocolNegotiator newNegotiator();

    /**
     * Returns the implicit port to use if no port was specified explicitly by the user.
     * 如果用户未显式指定任何端口，则返回要使用的隐式端口。
     * */
    int getDefaultPort();
  }

  interface ServerFactory {
    /**
     * Creates a new negotiator.
     * <br>
     * 创建一个新的谈判者。
     *
     * @param offloadExecutorPool an executor pool for time-consuming tasks
     */
    ProtocolNegotiator newNegotiator(ObjectPool<? extends Executor> offloadExecutorPool);
  }
}
