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

import java.io.InputStream;
import javax.annotation.Nullable;

/**
 * An observer of {@link Stream} events. It is guaranteed to only have one concurrent callback at a
 * time.
 * {@link Stream} 事件的观察者。它保证一次只有一个并发回调
 */
public interface StreamListener {
  /**
   * Called upon receiving a message from the remote end-point.<br>
   * 在接收到来自远程端点的消息时调用。
   *
   * <p>Implementations must eventually drain the provided {@code producer} {@link MessageProducer}
   * completely by invoking {@link MessageProducer#next()} to obtain deframed messages until the
   * producer returns null.
   *
   * <p>实现最终必须通过调用 {@link MessageProducer#next()} 来完全耗尽提供的 {@code producer}
   * {@link MessageProducer#next()} 来获取经过反帧的消息，直到生产者返回null。
   *
   * <p>This method should return quickly, as the same thread may be used to process other streams.
   *
   * <p>此方法应快速返回，因为同一线程可用于处理其他流。
   *
   * @param producer supplier of deframed messages. 解封邮件的供应商。
   */
  void messagesAvailable(MessageProducer producer);

  /**
   * This indicates that the transport is now capable of sending additional messages
   * without requiring excessive buffering internally. This event is
   * just a suggestion and the application is free to ignore it, however doing so may
   * result in excessive buffering within the transport.<br>
   * 这表明传输现在能够发送额外的消息，而不需要过多的内部缓冲。此事件只是一个建议，应用程序可以随意忽略它，但是这样做可能会导致传输中过多的缓冲。
   */
  void onReady();

  /**
   * A producer for deframed gRPC messages.<br>
   * 解帧gRPC消息的生产者。
   */
  interface MessageProducer {
    /**
     * Returns the next gRPC message, if the data has been received by the deframer and the
     * application has requested another message.<br>
     * 如果解帧器已接收数据并且应用程序已请求另一条消息，则返回下一个gRPC消息。
     *
     * <p>The provided {@code message} {@link InputStream} must be closed by the listener.
     *
     * <p>监听器必须关闭提供的 {@code message} {@link InputStream}。
     *
     * <p>This is intended to be used similar to an iterator, invoking {@code next()} to obtain
     * messages until the producer returns null, at which point the producer may be discarded.
     *
     * <p>这旨在类似于迭代器使用，调用 {@code next()} 来获取消息，直到生产者返回null，此时生产者可能会被丢弃。
     */
    @Nullable
    InputStream next();
  }
}
