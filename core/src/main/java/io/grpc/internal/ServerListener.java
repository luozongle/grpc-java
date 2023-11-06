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

package io.grpc.internal;

/**
 * A listener to a server for transport creation events. The listener need not be thread-safe, so
 * notifications must be properly synchronized externally.<br>
 * 用于传输创建事件的服务器侦听器。侦听器不需要是线程安全的，因此通知必须在外部正确同步。
 */
public interface ServerListener {

  /**
   * Called upon the establishment of a new client connection.<br>
   * 在建立新客户端连接时调用。
   *
   * @param transport the new transport to be observed. 要观察的新运输。
   * @return a listener for stream creation events on the transport.
   */
  ServerTransportListener transportCreated(ServerTransport transport);

  /**
   * The server is shutting down. No new transports will be processed, but existing transports may
   * continue. Shutdown is only caused by a call to {@link InternalServer#shutdown()}. All
   * resources have been released.<br>
   * 服务器正在关闭。不会处理新的传输，但现有的传输可能会继续。Shutdown仅由调用 {@link InternalServer#shutdown()} 引起。已释放所有资源
   */
  void serverShutdown();
}
