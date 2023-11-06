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

import io.grpc.Status;

/**
 * An observer of server-side stream events.<br>
 * 服务器端流事件的观察者。
 * */
public interface ServerStreamListener extends StreamListener {
  /**
   * Called when the remote side of the transport gracefully closed, indicating the client had no
   * more data to send. No further messages will be received on the stream.<br>
   * 当传输的远程端正常关闭时调用，指示客户端没有更多的数据要发送。流上不会收到更多消息。
   *
   * <p>This method should return quickly, as the same thread may be used to process other streams.
   *
   * <p>此方法应快速返回，因为同一线程可能用于处理其他流。
   */
  void halfClosed();

  /**
   * Called when the stream is fully closed. A status code of {@link
   * io.grpc.Status.Code#OK} implies normal termination of the stream.
   * Any other value implies abnormal termination. Since clients cannot send status, the passed
   * status is always library-generated and only is concerned with transport-level stream shutdown
   * (the call itself may have had a failing status, but if the stream terminated cleanly with the
   * status appearing to have been sent, then the passed status here would be {@code OK}). This is
   * guaranteed to always be the final call on a listener. No further callbacks will be issued.<br>
   * 在流完全关闭时调用。状态代码 {@link io.grpc.Status.Code#OK} 表示流的正常终止。
   * 任何其他值都意味着异常终止。由于客户端无法发送状态，因此传递的状态始终是库生成的，并且仅与传输级流关闭有关 (调用本身可能具有失败状态，但是如果流完全终止，
   * 并且状态似乎已发送，那么这里传递的状态将是 {@code OK})。保证这始终是侦听器上的最终调用。将不会发出进一步的回调。
   *
   * <p>This method should return quickly, as the same thread may be used to process other streams.
   *
   * <p>此方法应快速返回，因为同一线程可用于处理其他流。
   *
   * @param status details about the remote closure 有关远程关闭的详细信息
   */
  void closed(Status status);
}
