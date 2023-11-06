/*
 * Copyright 2017 The gRPC Authors
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

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Listens to events on a stream to collect metrics.<br>
 * 侦听流上的事件以收集度量。
 */
@ExperimentalApi("https://github.com/grpc/grpc-java/issues/2861")
@ThreadSafe
public abstract class ServerStreamTracer extends StreamTracer {
  /**
   * Called before the interceptors and the call handlers and make changes to the Context object
   * if needed.<br>
   * 在拦截器和调用处理程序之前调用，并根据需要对上下文对象进行更改。
   */
  public Context filterContext(Context context) {
    return context;
  }

  /**
   * Called when {@link ServerCall} is created.  This is for the tracer to access information about
   * the {@code ServerCall}.  Called after {@link #filterContext} and before the application call
   * handler.<br>
   * 在创建 {@link ServerCall} 时调用。这是为了让跟踪程序访问有关 {@code ServerCall} 的信息。
   * 在 {@link #filterContext}之后和应用程序调用处理程序之前调用。
   */
  @SuppressWarnings("deprecation")
  public void serverCallStarted(ServerCallInfo<?, ?> callInfo) {
    serverCallStarted(ReadOnlyServerCall.create(callInfo));
  }

  /**
   * Called when {@link ServerCall} is created.  This is for the tracer to access information about
   * the {@code ServerCall}.  Called after {@link #filterContext} and before the application call
   * handler.
   *
   * <br>在创建 {@link ServerCall} 时调用。这是为了让跟踪程序访问有关 {@code ServerCall} 的信息。
   * 在{@link #filterContext}之后和应用程序调用处理程序之前调用。
   *
   * @deprecated Implement {@link #serverCallStarted(ServerCallInfo)} instead. This method will be
   *     removed in a future release of gRPC.
   *     改为实现{@link #serverCallStarted(ServerCallInfo)}。此方法将在gRPC的未来版本中删除。
   */
  @Deprecated
  public void serverCallStarted(ServerCall<?, ?> call) {
  }

  public abstract static class Factory {
    /**
     * Creates a {@link ServerStreamTracer} for a new server stream.
     *
     * <br>为新服务器流创建 {@link ServerStreamTracer}。
     *
     * <p>Called right before the stream is created
     *
     * <p>在创建流之前调用
     *
     * @param fullMethodName the fully qualified method name  完全限定的方法名称
     * @param headers the received request headers.  It can be safely mutated within this method.
     *        It should not be saved because it is not safe for read or write after the method
     *        returns.  收到的请求标头。它可以在这种方法中安全地突变。它不应该被保存，因为它是不安全的读或写方法返回后。
     */
    public abstract ServerStreamTracer newServerStreamTracer(
        String fullMethodName, Metadata headers);
  }

  /**
   * A data class with info about the started {@link ServerCall}.<br>
   * 包含有关已启动的 {@link ServerCall} 的信息的数据类。
   */
  public abstract static class ServerCallInfo<ReqT, RespT> {
    public abstract MethodDescriptor<ReqT, RespT> getMethodDescriptor();

    public abstract Attributes getAttributes();

    @Nullable
    public abstract String getAuthority();
  }

  /**
   * This class exists solely to help transition to the {@link ServerCallInfo} based API.
   *
   * <br>这个类的存在仅仅是为了帮助转换到基于 {@link ServerCallInfo} 的API。
   *
   * @deprecated Will be deleted when {@link #serverCallStarted(ServerCall)} is removed.
   *     将在删除{@link #serverCallStarted(ServerCall)}时删除。
   */
  @Deprecated
  private static final class ReadOnlyServerCall<ReqT, RespT>
      extends ForwardingServerCall<ReqT, RespT> {
    private final ServerCallInfo<ReqT, RespT> callInfo;

    private static <ReqT, RespT> ReadOnlyServerCall<ReqT, RespT> create(
        ServerCallInfo<ReqT, RespT> callInfo) {
      return new ReadOnlyServerCall<>(callInfo);
    }

    private ReadOnlyServerCall(ServerCallInfo<ReqT, RespT> callInfo) {
      this.callInfo = callInfo;
    }

    @Override
    public MethodDescriptor<ReqT, RespT> getMethodDescriptor() {
      return callInfo.getMethodDescriptor();
    }

    @Override
    public Attributes getAttributes() {
      return callInfo.getAttributes();
    }

    @Override
    public boolean isReady() {
      // a dummy value
      return false;
    }

    @Override
    public boolean isCancelled() {
      // a dummy value
      return false;
    }

    @Override
    public String getAuthority() {
      return callInfo.getAuthority();
    }

    @Override
    protected ServerCall<ReqT, RespT> delegate() {
      throw new UnsupportedOperationException();
    }
  }
}
