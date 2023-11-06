/*
 * Copyright 2014 The gRPC Authors
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

package io.grpc;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A virtual connection to a conceptual endpoint, to perform RPCs. A channel is free to have zero or
 * many actual connections to the endpoint based on configuration, load, etc. A channel is also free
 * to determine which actual endpoints to use and may change it every RPC, permitting client-side
 * load balancing. Applications are generally expected to use stubs instead of calling this class
 * directly.
 * <br>
 * 到概念端点的虚拟连接，以执行rpc。根据配置，负载等，通道可以自由地具有到端点的零个或许多实际连接。
 * 通道也可以自由确定要使用的实际端点，并且可以在每个RPC上对其进行更改，从而允许客户端负载平衡。应用程序通常被期望使用存根，而不是直接调用这个类。
 *
 * <p>Applications can add common cross-cutting behaviors to stubs by decorating Channel
 * implementations using {@link ClientInterceptor}. It is expected that most application
 * code will not use this class directly but rather work with stubs that have been bound to a
 * Channel that was decorated during application initialization.
 * <br>
 * 应用程序可以通过使用 {@link ClientInterceptor} 装饰通道实现来向存根添加常见的横切行为。
 * 预计大多数应用程序代码将不会直接使用此类，而是使用已绑定到在应用程序初始化期间修饰的通道的存根。
 */
@ThreadSafe
public abstract class Channel {
  /**
   * Create a {@link ClientCall} to the remote operation specified by the given
   * {@link MethodDescriptor}. The returned {@link ClientCall} does not trigger any remote
   * behavior until {@link ClientCall#start(ClientCall.Listener, Metadata)} is
   * invoked.
   * <br>
   * 为给定的 {@link MethodDescriptor} 指定的远程操作创建一个 {@link ClientCall}。
   * 返回的 {@link ClientCall} 不会触发任何远程行为，
   * 直到 {@link ClientCall#start(ClientCall.Listener, Metadata)} 被调用。
   *
   * @param methodDescriptor describes the name and parameter types of the operation to call.
   *                         描述要调用的操作的名称和参数类型。
   * @param callOptions runtime options to be applied to this call.要应用于此调用的运行时选项。
   * @return a {@link ClientCall} bound to the specified method. 绑定到指定方法的 {@link ClientCall}。
   * @since 1.0.0
   */
  public abstract <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(
      MethodDescriptor<RequestT, ResponseT> methodDescriptor, CallOptions callOptions);

  /**
   * The authority of the destination this channel connects to. Typically this is in the format
   * {@code host:port}.
   * <br>
   * 此通道连接到的目的地的权限。通常，格式为 {@code host:port}。
   *
   * @since 1.0.0
   */
  public abstract String authority();
}
