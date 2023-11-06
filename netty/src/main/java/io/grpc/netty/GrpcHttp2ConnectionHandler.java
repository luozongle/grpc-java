/*
 * Copyright 2016 The gRPC Authors
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

import static com.google.common.base.Preconditions.checkState;

import io.grpc.Attributes;
import io.grpc.ChannelLogger;
import io.grpc.Internal;
import io.grpc.InternalChannelz;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2Settings;
import javax.annotation.Nullable;

/**
 * gRPC wrapper for {@link Http2ConnectionHandler}.
 * <br>
 * 用于 {@link Http2ConnectionHandler} 的gRPC包装器。
 * 看起来主要就是包装了一下Http2ConnectionHandler
 */
@Internal
public abstract class GrpcHttp2ConnectionHandler extends Http2ConnectionHandler {

  /**
   * 自适应累积器组成最小大小默认值.
   */
  static final int ADAPTIVE_CUMULATOR_COMPOSE_MIN_SIZE_DEFAULT = 1024;
  static final Cumulator ADAPTIVE_CUMULATOR =
      new NettyAdaptiveCumulator(ADAPTIVE_CUMULATOR_COMPOSE_MIN_SIZE_DEFAULT);

  @Nullable
  protected final ChannelPromise channelUnused;
  private final ChannelLogger negotiationLogger;

  protected GrpcHttp2ConnectionHandler(
      ChannelPromise channelUnused,
      Http2ConnectionDecoder decoder,
      Http2ConnectionEncoder encoder,
      Http2Settings initialSettings,
      ChannelLogger negotiationLogger) {
    super(decoder, encoder, initialSettings);
    this.channelUnused = channelUnused;
    this.negotiationLogger = negotiationLogger;
    setCumulator(ADAPTIVE_CUMULATOR);
  }

  /**
   * Same as {@link #handleProtocolNegotiationCompleted(
   *   Attributes, io.grpc.InternalChannelz.Security)}
   * but with no {@link io.grpc.InternalChannelz.Security}.
   * 与{@link #handleProtocolNegotiationCompleted(Attributes, io.grpc.InternalChannelz.Security)}相同，
   * 但没有 {@link io.grpc.InternalChannelz.Security}。
   *
   * @deprecated Use the two argument method instead.
   */
  @Deprecated
  @SuppressWarnings("InlineMeSuggester")
  // the caller should consider providing securityInfo调用者应考虑提供securityInfo
  public void handleProtocolNegotiationCompleted(Attributes attrs) {
    handleProtocolNegotiationCompleted(attrs, /*securityInfo=*/ null);
  }

  /**
   * Triggered on protocol negotiation completion.
   * <br>
   * 协议协商完成时触发。
   *
   * <p>It must me called after negotiation is completed but before given handler is added to the
   * channel.
   * <br>
   * 它必须在协商完成后但在给定的处理程序添加到通道之前调用。
   *
   * @param attrs arbitrary attributes passed after protocol negotiation (eg. SSLSession).
   * @param securityInfo informs channelz about the security protocol.
   */
  public void handleProtocolNegotiationCompleted(
      Attributes attrs, InternalChannelz.Security securityInfo) {
  }

  /**
   * Returns the channel logger for the given channel context.
   * <br>
   * 返回给定通道上下文的通道记录器。
   */
  public ChannelLogger getNegotiationLogger() {
    checkState(negotiationLogger != null, "NegotiationLogger must not be null");
    return negotiationLogger;
  }

  /**
   * Calling this method indicates that the channel will no longer be used.  This method is roughly
   * the same as calling {@link #close} on the channel, but leaving the channel alive.  This is
   * useful if the channel will soon be deregistered from the executor and used in a non-Netty
   * context.
   * <br>
   * 调用此方法表示将不再使用该通道。
   * 此方法与在频道上调用 {@link #close} 大致相同，但使频道保持活动状态。如果通道将很快从执行器中注销并在非Netty上下文中使用，则这很有用。
   */
  @SuppressWarnings("FutureReturnValueIgnored")
  public void notifyUnused() {
    channelUnused.setSuccess(null);
  }

  /**
   * Get the attributes of the EquivalentAddressGroup used to create this transport.
   * <br>
   * 获取用于创建此传输的EquivalentAddressGroup的属性。
   * */
  public Attributes getEagAttributes() {
    return Attributes.EMPTY;
  }

  /**
   * Returns the authority of the server. Only available on the client-side.
   * <br>
   * 返回服务器的权限。仅在客户端可用。
   *
   * @throws UnsupportedOperationException if on server-side 如果在服务器端
   */
  public String getAuthority() {
    throw new UnsupportedOperationException();
  }
}
