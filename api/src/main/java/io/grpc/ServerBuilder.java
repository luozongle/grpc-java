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

package io.grpc;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Preconditions;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

/**
 * A builder for {@link Server} instances.<br>
 * {@link Server} 实例的生成器。
 *
 * @param <T> The concrete type of this builder. 这个建筑商的混凝土类型。
 * @since 1.0.0
 */
public abstract class ServerBuilder<T extends ServerBuilder<T>> {

  /**
   * Static factory for creating a new ServerBuilder.
   * 用于创建新ServerBuilder的静态工厂。
   *
   * @param port the port to listen on 要侦听的端口
   * @since 1.0.0
   */
  public static ServerBuilder<?> forPort(int port) {
    return ServerProvider.provider().builderForPort(port);
  }

  /**
   * Execute application code directly in the transport thread.<br>
   * 直接在传输线程中执行应用程序代码。
   *
   * <p>Depending on the underlying transport, using a direct executor may lead to substantial
   * performance improvements. However, it also requires the application to not block under
   * any circumstances.
   *
   * <p>根据底层传输，使用直接执行器可能会带来实质性的性能改进。但是，它还要求应用程序在任何情况下都不能阻止。
   *
   * <p>Calling this method is semantically equivalent to calling {@link #executor(Executor)} and
   * passing in a direct executor. However, this is the preferred way as it may allow the transport
   * to perform special optimizations.
   *
   * <p>调用此方法在语义上等同于调用 {@link #executor(Executor)} 并传入直接执行器。然而，这是优选的方式，因为它可以允许传输执行特殊的优化。
   *
   * @return this
   * @since 1.0.0
   */
  public abstract T directExecutor();

  /**
   * Provides a custom executor. 提供自定义执行器。
   *
   * <p>It's an optional parameter. If the user has not provided an executor when the server is
   * built, the builder will use a static cached thread pool.
   *
   * <p>这是一个可选参数。如果用户在构建服务器时未提供执行器，则构建器将使用静态缓存线程池。
   *
   * <p>The server won't take ownership of the given executor. It's caller's responsibility to
   * shut down the executor when it's desired.
   *
   * <p>服务器不会获得给定执行器的所有权。这是调用者的责任关闭执行者时，它的需要。
   *
   * @return this
   * @since 1.0.0
   */
  public abstract T executor(@Nullable Executor executor);


  /**
   * Allows for defining a way to provide a custom executor to handle the server call.
   * This executor is the result of calling
   * {@link ServerCallExecutorSupplier#getExecutor(ServerCall, Metadata)} per RPC.<br>
   * 允许定义一种方法来提供自定义执行器来处理服务器调用。此执行器是对每个RPC调用
   * {@link ServerCallExecutorSupplier#getExecutor(ServerCall, Metadata)}的结果。
   *
   * <p>It's an optional parameter. If it is provided, the {@link #executor(Executor)} would still
   * run necessary tasks before the {@link ServerCallExecutorSupplier} is ready to be called, then
   * it switches over. But if calling {@link ServerCallExecutorSupplier} returns null, the server
   * call is still handled by the default {@link #executor(Executor)} as a fallback.
   *
   * @param executorSupplier the server call executor provider  服务器调用执行器提供程序
   * @return this
   * @since 1.39.0
   *
   * */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/8274")
  public T callExecutor(ServerCallExecutorSupplier executorSupplier) {
    return thisT();
  }

  /**
   * Adds a service implementation to the handler registry.<br>
   * 将服务实现添加到处理程序注册表。
   *
   * @param service ServerServiceDefinition object
   * @return this
   * @since 1.0.0
   */
  public abstract T addService(ServerServiceDefinition service);

  /**
   * Adds a service implementation to the handler registry.
   * <br>
   * 将服务实现添加到处理程序注册表中。
   *
   * @param bindableService BindableService object
   * @return this
   * @since 1.0.0
   */
  public abstract T addService(BindableService bindableService);

  /**
   * Adds a list of service implementations to the handler registry together.<br>
   * 将服务实现的列表一起添加到处理程序注册表中。
   *
   * @param services the list of ServerServiceDefinition objects
   * @return this
   * @since 1.37.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/7925")
  public final T addServices(List<ServerServiceDefinition> services) {
    checkNotNull(services, "services");
    for (ServerServiceDefinition service : services) {
      addService(service);
    }
    return thisT();
  }

  /**
   * Adds a {@link ServerInterceptor} that is run for all services on the server.  Interceptors
   * added through this method always run before per-service interceptors added through {@link
   * ServerInterceptors}.  Interceptors run in the reverse order in which they are added, just as
   * with consecutive calls to {@code ServerInterceptors.intercept()}.<br>
   * 添加为服务器上的所有服务运行的 {@link ServerInterceptor}。
   * 通过此方法添加的拦截器始终在通过 {@link ServerInterceptors} 添加的每服务拦截器之前运行。
   * 拦截器的运行顺序与添加顺序相反，就像连续调用 {@code serverinterceptors.intercept ()} 一样。
   *
   * @param interceptor the all-service interceptor
   * @return this
   * @since 1.5.0
   */
  public T intercept(ServerInterceptor interceptor) {
    throw new UnsupportedOperationException();
  }

  /**
   * Adds a {@link ServerTransportFilter}. The order of filters being added is the order they will
   * be executed.<br>
   * 添加 {@link ServerTransportFilter}。被添加的过滤器的顺序是它们将被执行的顺序。
   *
   * @return this
   * @since 1.2.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/2132")
  public T addTransportFilter(ServerTransportFilter filter) {
    throw new UnsupportedOperationException();
  }

  /**
   * Adds a {@link ServerStreamTracer.Factory} to measure server-side traffic.  The order of
   * factories being added is the order they will be executed.<br>
   * 添加 {@link ServerStreamTracer.Factory} 以测量服务器端流量。正在添加的工厂的顺序是它们将被执行的顺序。
   *
   * @return this
   * @since 1.3.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/2861")
  public T addStreamTracerFactory(ServerStreamTracer.Factory factory) {
    throw new UnsupportedOperationException();
  }

  /**
   * Sets a fallback handler registry that will be looked up in if a method is not found in the
   * primary registry. The primary registry (configured via {@code addService()}) is faster but
   * immutable. The fallback registry is more flexible and allows implementations to mutate over
   * time and load services on-demand.<br>
   * 设置一个回退处理程序注册表，如果在主注册表中找不到方法，将在该注册表中进行查找。主注册表 (通过 {@code addService()} 配置) 速度更快，但不可变。
   * 回退注册表更灵活，并允许实现随时间变化并按需加载服务。
   *
   * @return this
   * @since 1.0.0
   */
  public abstract T fallbackHandlerRegistry(@Nullable HandlerRegistry fallbackRegistry);

  /**
   * Makes the server use TLS.<br>
   * 使服务器使用TLS。
   *
   * @param certChain file containing the full certificate chain
   * @param privateKey file containing the private key
   *
   * @return this
   * @throws UnsupportedOperationException if the server does not support TLS.
   * @since 1.0.0
   */
  public abstract T useTransportSecurity(File certChain, File privateKey);

  /**
   * Makes the server use TLS.<br>
   * 使服务器使用TLS。
   *
   * @param certChain InputStream containing the full certificate chain
   * @param privateKey InputStream containing the private key
   *
   * @return this
   * @throws UnsupportedOperationException if the server does not support TLS, or does not support
   *         reading these files from an InputStream.
   * @since 1.12.0
   */
  public T useTransportSecurity(InputStream certChain, InputStream privateKey) {
    throw new UnsupportedOperationException();
  }


  /**
   * Set the decompression registry for use in the channel.  This is an advanced API call and
   * shouldn't be used unless you are using custom message encoding.   The default supported
   * decompressors are in {@code DecompressorRegistry.getDefaultInstance}.<br>
   * 设置要在通道中使用的解压缩注册表。这是一个高级API调用，除非您使用自定义消息编码，否则不应使用。
   * 受支持的默认解压缩程序位于 {@code DecompressorRegistry.getDefaultInstance} 中。
   *
   * @return this
   * @since 1.0.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/1704")
  public abstract T decompressorRegistry(@Nullable DecompressorRegistry registry);

  /**
   * Set the compression registry for use in the channel.  This is an advanced API call and
   * shouldn't be used unless you are using custom message encoding.   The default supported
   * compressors are in {@code CompressorRegistry.getDefaultInstance}. <br>
   * 设置在通道中使用的压缩注册表。这是一个高级API调用，除非您使用自定义消息编码，否则不应使用。
   * 受支持的默认压缩器位于 {@code CompressorRegistry.getDefaultInstance} 中。
   *
   * @return this
   * @since 1.0.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/1704")
  public abstract T compressorRegistry(@Nullable CompressorRegistry registry);

  /**
   * Sets the permitted time for new connections to complete negotiation handshakes before being
   * killed. <br>
   * 设置新连接在被杀死之前完成协商握手的允许时间。
   *
   * @return this
   * @throws IllegalArgumentException if timeout is negative
   * @throws UnsupportedOperationException if unsupported
   * @since 1.8.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/3706")
  public T handshakeTimeout(long timeout, TimeUnit unit) {
    throw new UnsupportedOperationException();
  }

  /**
   * Sets the time without read activity before sending a keepalive ping. An unreasonably small
   * value might be increased, and {@code Long.MAX_VALUE} nano seconds or an unreasonably large
   * value will disable keepalive. The typical default is two hours when supported. <br>
   * 设置在发送keepalive ping之前没有读取活动的时间。可能会增加不合理的小值，{@code Long.MAX_VALUE}
   * nano seconds或不合理的大值将禁用keepalive。支持时，典型的默认值为两个小时。
   *
   * @throws IllegalArgumentException if time is not positive
   * @throws UnsupportedOperationException if unsupported
   * @since 1.47.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/9009")
  public T keepAliveTime(long keepAliveTime, TimeUnit timeUnit) {
    throw new UnsupportedOperationException();
  }

  /**
   * Sets a time waiting for read activity after sending a keepalive ping. If the time expires
   * without any read activity on the connection, the connection is considered dead. An unreasonably
   * small value might be increased. Defaults to 20 seconds when supported.<br>
   * 设置发送keepalive ping后等待读取活动的时间。如果时间到期而连接上没有任何读取活动，则连接被认为是死的。
   * 一个不合理的小值可能会增加。支持时，默认为20秒。
   *
   * <p>This value should be at least multiple times the RTT to allow for lost packets.
   * 此值应至少是RTT的多倍，以允许丢失的数据包。
   *
   * @throws IllegalArgumentException if timeout is not positive
   * @throws UnsupportedOperationException if unsupported
   * @since 1.47.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/9009")
  public T keepAliveTimeout(long keepAliveTimeout, TimeUnit timeUnit) {
    throw new UnsupportedOperationException();
  }

  /**
   * Sets the maximum connection idle time, connections being idle for longer than which will be
   * gracefully terminated. Idleness duration is defined since the most recent time the number of
   * outstanding RPCs became zero or the connection establishment. An unreasonably small value might
   * be increased. {@code Long.MAX_VALUE} nano seconds or an unreasonably large value will disable
   * max connection idle.<br>
   * 设置最大连接空闲时间，连接空闲的时间要比正常终止的时间长。
   * 自最近一次未完成rpc的数量变为零或连接建立以来，定义了空闲持续时间。
   * 一个不合理的小值可能会增加。{@code Long.MAX_VALUE} nano秒或不合理的大值将禁用最大连接空闲。
   *
   * @throws IllegalArgumentException if idle is not positive
   * @throws UnsupportedOperationException if unsupported
   * @since 1.47.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/9009")
  public T maxConnectionIdle(long maxConnectionIdle, TimeUnit timeUnit) {
    throw new UnsupportedOperationException();
  }

  /**
   * Sets the maximum connection age, connections lasting longer than which will be gracefully
   * terminated. An unreasonably small value might be increased. A random jitter of +/-10% will be
   * added to it. {@code Long.MAX_VALUE} nano seconds or an unreasonably large value will disable
   * max connection age.<br>
   * 设置最长连接期限，持续时间超过该期限的连接将被正常终止。一个不合理的小值可能会增加。+-10% 的随机抖动将被添加到它。
   * {@code Long.MAX_VALUE} nano seconds或过大的值将禁用最长连接时间。
   *
   * @throws IllegalArgumentException if age is not positive
   * @throws UnsupportedOperationException if unsupported
   * @since 1.47.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/9009")
  public T maxConnectionAge(long maxConnectionAge, TimeUnit timeUnit) {
    throw new UnsupportedOperationException();
  }

  /**
   * Sets the grace time for the graceful connection termination. Once the max connection age
   * is reached, RPCs have the grace time to complete. RPCs that do not complete in time will be
   * cancelled, allowing the connection to terminate. {@code Long.MAX_VALUE} nano seconds or an
   * unreasonably large value are considered infinite.<br>
   * 设置正常连接终止的宽限期。一旦达到最大连接年龄，rpc就有宽限期来完成。
   * 没有及时完成的rpc将被取消，从而允许连接终止。{@code Long.MAX_VALUE} 纳米秒或不合理的大值被认为是无限的。
   *
   * @throws IllegalArgumentException if grace is negative
   * @throws UnsupportedOperationException if unsupported
   * @see #maxConnectionAge(long, TimeUnit)
   * @since 1.47.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/9009")
  public T maxConnectionAgeGrace(long maxConnectionAgeGrace, TimeUnit timeUnit) {
    throw new UnsupportedOperationException();
  }

  /**
   * Specify the most aggressive keep-alive time clients are permitted to configure. The server will
   * try to detect clients exceeding this rate and when detected will forcefully close the
   * connection. The typical default is 5 minutes when supported. <br>
   * 指定允许客户端配置的最积极的保持活动时间。服务器将尝试检测超过此速率的客户端，并且在检测到时将强制关闭连接。支持时，典型的默认值为5分钟。
   *
   * <p>Even though a default is defined that allows some keep-alives, clients must not use
   * keep-alive without approval from the service owner. Otherwise, they may experience failures in
   * the future if the service becomes more restrictive. When unthrottled, keep-alives can cause a
   * significant amount of traffic and CPU usage, so clients and servers should be conservative in
   * what they use and accept.
   *
   * <p>即使定义了允许某些保活的默认值，客户端也不得在未经服务所有者批准的情况下使用保活。否则，如果服务变得更加严格，他们将来可能会遇到故障。
   * 当不限制时，保持活动可能会导致大量的流量和CPU使用率，因此客户端和服务器应该保守使用和接受。
   *
   * @throws IllegalArgumentException if time is negative
   * @throws UnsupportedOperationException if unsupported
   * @see #permitKeepAliveWithoutCalls(boolean)
   * @since 1.47.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/9009")
  public T permitKeepAliveTime(long keepAliveTime, TimeUnit timeUnit) {
    throw new UnsupportedOperationException();
  }

  /**
   * Sets whether to allow clients to send keep-alive HTTP/2 PINGs even if there are no outstanding
   * RPCs on the connection. Defaults to {@code false} when supported. <br>
   * 设置是否允许客户端发送keep-alive HTTP2 ping，即使连接上没有未完成的rpc。支持时，默认为 {@code false}。
   *
   * @throws UnsupportedOperationException if unsupported
   * @see #permitKeepAliveTime(long, TimeUnit)
   * @since 1.47.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/9009")
  public T permitKeepAliveWithoutCalls(boolean permit) {
    throw new UnsupportedOperationException();
  }

  /**
   * Sets the maximum message size allowed to be received on the server. If not called,
   * defaults to 4 MiB. The default provides protection to servers who haven't considered the
   * possibility of receiving large messages while trying to be large enough to not be hit in normal
   * usage. <br>
   * 设置允许在服务器上接收的最大邮件大小。如果未调用，则默认为4 MiB。
   * 默认值为未考虑接收大型邮件的可能性的服务器提供保护，同时尝试使其足够大而不会在正常使用中被击中。
   *
   * <p>This method is advisory, and implementations may decide to not enforce this.  Currently,
   * the only known transport to not enforce this is {@code InProcessServer}.
   *
   * <p>此方法是建议性的，实现可能会决定不强制执行此方法。
   * 目前，唯一不强制执行此操作的已知传输是 {@code InProcessServer}。
   *
   * @param bytes the maximum number of bytes a single message can be. 单个消息的最大字节数。
   * @return this
   * @throws IllegalArgumentException if bytes is negative.
   * @throws UnsupportedOperationException if unsupported.
   * @since 1.13.0
   */
  public T maxInboundMessageSize(int bytes) {
    // intentional noop rather than throw, this method is only advisory.
    Preconditions.checkArgument(bytes >= 0, "bytes must be >= 0");
    return thisT();
  }

  /**
   * Sets the maximum size of metadata allowed to be received. {@code Integer.MAX_VALUE} disables
   * the enforcement. The default is implementation-dependent, but is not generally less than 8 KiB
   * and may be unlimited. <br>
   * 设置允许接收的元数据的最大大小。{@code Integer.MAX_VALUE} 禁用强制执行。默认值取决于实现，但通常不小于8 KiB，并且可能不受限制。
   *
   * <p>This is cumulative size of the metadata. The precise calculation is
   * implementation-dependent, but implementations are encouraged to follow the calculation used for
   * <a href="http://httpwg.org/specs/rfc7540.html#rfc.section.6.5.2">
   * HTTP/2's SETTINGS_MAX_HEADER_LIST_SIZE</a>. It sums the bytes from each entry's key and value,
   * plus 32 bytes of overhead per entry.
   *
   * <p>这是元数据的累积大小。精确计算取决于实现，但鼓励实现遵循用于 <a href = "http:httpwg.orgspecsrfc7540.htmlrfc.section.6.5.2">
   *     HTTP2的SETTINGS_MAX_HEADER_LIST_SIZE 的计算</a>。它将每个条目的键和值的字节相加，再加上每个条目的32字节开销。
   *
   * @param bytes the maximum size of received metadata  接收到的元数据的最大大小
   * @return this
   * @throws IllegalArgumentException if bytes is non-positive
   * @since 1.17.0
   */
  public T maxInboundMetadataSize(int bytes) {
    Preconditions.checkArgument(bytes > 0, "maxInboundMetadataSize must be > 0");
    // intentional noop rather than throw, this method is only advisory.
    return thisT();
  }

  /**
   * Sets the BinaryLog object that this server should log to. The server does not take
   * ownership of the object, and users are responsible for calling {@link BinaryLog#close()}.
   *
   * <br>设置此服务器应记录到的BinaryLog对象。服务器不取得对象的所有权，用户负责调用{@link BinaryLog#close()}。
   *
   * @param binaryLog the object to provide logging. 要提供日志记录的对象。
   * @return this
   * @since 1.13.0
   */
  @ExperimentalApi("https://github.com/grpc/grpc-java/issues/4017")
  public T setBinaryLog(BinaryLog binaryLog) {
    throw new UnsupportedOperationException();
  }

  /**
   * Builds a server using the given parameters.<br>
   * 使用给定的参数构建服务器。
   *
   * <p>The returned service will not been started or be bound a port. You will need to start it
   * with {@link Server#start()}.
   *
   * <p>返回的服务将不会启动或绑定端口。您需要使用{@link Server#start()}启动它。
   *
   * @return a new Server
   * @since 1.0.0
   */
  public abstract Server build();

  /**
   * Returns the correctly typed version of the builder.
   * 返回生成器的正确类型版本。
   */
  private T thisT() {
    @SuppressWarnings("unchecked")
    T thisT = (T) this;
    return thisT;
  }
}
