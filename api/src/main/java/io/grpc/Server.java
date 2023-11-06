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

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Server for listening for and dispatching incoming calls. It is not expected to be implemented by
 * application code or interceptors.
 * 用于侦听和调度传入呼叫的服务器。它不会被应用程序代码或拦截器实现。
 */
@ThreadSafe
public abstract class Server {

  /**
   * Key for accessing the {@link Server} instance inside server RPC {@link Context}. It's
   * unclear to us what users would need. If you think you need to use this, please file an
   * issue for us to discuss a public API.<br>
   * 用于访问服务器RPC {@link Context} 内的 {@link Server} 实例的键。
   * 我们不清楚用户需要什么。如果你认为你需要使用这个，请提交一个问题，我们讨论一个公共的API。
   */
  static final Context.Key<Server> SERVER_CONTEXT_KEY =
      Context.key("io.grpc.Server");

  /**
   * Bind and start the server.  After this call returns, clients may begin connecting to the
   * listening socket(s).<br>
   * 绑定并启动服务器。在该调用返回之后，客户端可以开始连接到监听套接字。
   *
   * @return {@code this} object
   * @throws IllegalStateException if already started or shut down
   * @throws IOException if unable to bind
   * @since 1.0.0
   */
  public abstract Server start() throws IOException;

  /**
   * Returns the port number the server is listening on.  This can return -1 if there is no actual
   * port or the result otherwise does not make sense.  Result is undefined after the server is
   * terminated.  If there are multiple possible ports, this will return one arbitrarily.
   * Implementations are encouraged to return the same port on each call.
   * <br>
   * 返回服务器正在侦听的端口号。这可以返回-1，如果没有实际的端口或结果，否则没有意义。服务器终止后，结果未定义。如果有多个可能的端口，这将返回一个任意。鼓励实现在每次调用时返回相同的端口。
   *
   * @see #getListenSockets()
   * @throws IllegalStateException if the server has not yet been started.
   * @since 1.0.0
   */
  public int getPort() {
    return -1;
  }

  /**
   * Returns a list of listening sockets for this server.  May be different than the originally
   * requested sockets (e.g. listening on port '0' may end up listening on a different port).
   * The list is unmodifiable.<br>
   * 返回此服务器的侦听套接字列表。可能不同于最初请求的套接字 (例如，在端口 “0” 上侦听可能最终在不同端口上侦听)。该列表是不可修改的。
   *
   * @throws IllegalStateException if the server has not yet been started. 如果服务器尚未启动。
   * @since 1.19.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/5332")
  public List<? extends SocketAddress> getListenSockets() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns all services registered with the server, or an empty list if not supported by the
   * implementation. <br>
   * 返回向服务器注册的所有服务，如果实现不支持，则返回空列表。
   *
   * @since 1.1.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/2222")
  public List<ServerServiceDefinition> getServices() {
    return Collections.emptyList();
  }

  /**
   * Returns immutable services registered with the server, or an empty list if not supported by the
   * implementation.<br>
   * 返回在服务器中注册的不可变服务，如果实现不支持，则返回空列表。
   *
   * @since 1.1.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/2222")
  public List<ServerServiceDefinition> getImmutableServices() {
    return Collections.emptyList();
  }


  /**
   * Returns mutable services registered with the server, or an empty list if not supported by the
   * implementation.<br>
   * 返回向服务器注册的可变服务，如果实现不支持，则返回空列表。
   *
   * @since 1.1.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/2222")
  public List<ServerServiceDefinition> getMutableServices() {
    return Collections.emptyList();
  }

  /**
   * Initiates an orderly shutdown in which preexisting calls continue but new calls are rejected.
   * After this call returns, this server has released the listening socket(s) and may be reused by
   * another server.<br>
   * 启动有序关闭，其中先前存在的呼叫继续，但新呼叫被拒绝。在这个调用返回之后，这个服务器已经释放了监听套接字并且可以被另一个服务器重用。
   *
   * <p>Note that this method will not wait for preexisting calls to finish before returning.
   * {@link #awaitTermination()} or {@link #awaitTermination(long, TimeUnit)} needs to be called to
   * wait for existing calls to finish.
   *
   * <p>请注意，此方法不会在返回之前等待预先存在的调用完成。需要调用
   * {@link #awaitTermination()} 或 {@link #awaitTermination(long, TimeUnit)} 以等待现有调用完成。
   *
   * <p>Calling this method before {@code start()} will shut down and terminate the server like
   * normal, but prevents starting the server in the future.
   *
   * <p>在 {@code start()} 之前调用此方法将像正常情况一样关闭并终止服务器，但会阻止将来启动服务器。
   *
   * @return {@code this} object
   * @since 1.0.0
   */
  public abstract Server shutdown();

  /**
   * Initiates a forceful shutdown in which preexisting and new calls are rejected. Although
   * forceful, the shutdown process is still not instantaneous; {@link #isTerminated()} will likely
   * return {@code false} immediately after this method returns. After this call returns, this
   * server has released the listening socket(s) and may be reused by another server.
   * 启动强制关闭，拒绝先前存在的和新的呼叫。虽然强大，但关闭过程仍然不是即时的;
   * {@link #isTerminated()} 可能会在此方法返回后立即返回 {@code false}。
   * 在这个调用返回之后，这个服务器已经释放了监听套接字并且可以被另一个服务器重用。
   *
   * <p>Calling this method before {@code start()} will shut down and terminate the server like
   * normal, but prevents starting the server in the future.
   *
   * <p>在 {@code start()} 之前调用此方法将像正常情况一样关闭并终止服务器，但会阻止将来启动服务器。
   *
   * @return {@code this} object
   * @since 1.0.0
   */
  public abstract Server shutdownNow();

  /**
   * Returns whether the server is shutdown. Shutdown servers reject any new calls, but may still
   * have some calls being processed.<br>
   * 返回服务器是否已关闭。关闭服务器会拒绝任何新呼叫，但可能仍有一些正在处理的呼叫。
   *
   * @see #shutdown()
   * @see #isTerminated()
   * @since 1.0.0
   */
  public abstract boolean isShutdown();

  /**
   * Returns whether the server is terminated. Terminated servers have no running calls and
   * relevant resources released (like TCP connections).<br>
   * 返回服务器是否已终止。终止的服务器没有正在运行的调用和释放的相关资源 (如TCP连接)。
   *
   * @see #isShutdown()
   * @since 1.0.0
   */
  public abstract boolean isTerminated();

  /**
   * Waits for the server to become terminated, giving up if the timeout is reached.<br>
   * 等待服务器终止，如果达到超时则放弃。
   *
   * <p>Calling this method before {@code start()} or {@code shutdown()} is permitted and does not
   * change its behavior.
   *
   * <p>允许在 {@code start()} 或 {@code shutdown()} 之前调用此方法，并且不会更改其行为。
   *
   * @return whether the server is terminated, as would be done by {@link #isTerminated()}.
   *     服务器是否终止，就像 {@link #isTerminated()} 所做的那样。
   */
  public abstract boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

  /**
   * Waits for the server to become terminated.<br>
   * 等待服务器终止。
   *
   * <p>Calling this method before {@code start()} or {@code shutdown()} is permitted and does not
   * change its behavior.
   *
   * <p>允许在 {@code start()} 或 {@code shutdown()} 之前调用此方法，并且不会更改其行为。
   *
   * @since 1.0.0
   */
  public abstract void awaitTermination() throws InterruptedException;
}
