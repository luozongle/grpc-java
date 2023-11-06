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

import io.grpc.Attributes;
import io.grpc.Metadata;

/**
 * A observer of a server-side transport for stream creation events. Notifications must occur from
 * the transport thread.<br>
 * 流创建事件的服务器端传输的观察者。必须从传输线程发出通知。
 */
public interface ServerTransportListener {
  /**
   * Called when a new stream was created by the remote client.<br>
   * 在远程客户端创建新流时调用。
   *
   * @param stream the newly created stream. 新创建的流。
   * @param method the fully qualified method name being called on the server. 在服务器上调用的完全限定方法名称。
   * @param headers containing metadata for the call. 包含调用的元数据的。
   */
  void streamCreated(ServerStream stream, String method, Metadata headers);

  /**
   * The transport has finished all handshakes and is ready to process streams.<br>
   * 传输已完成所有握手，并准备处理流。
   *
   * @param attributes transport attributes 传输属性
   *
   * @return the effective transport attributes that is used as the basis of call attributes
   *     用作呼叫属性基础的有效传输属性
   */
  Attributes transportReady(Attributes attributes);

  /**
   * The transport completed shutting down. All resources have been released.<br>
   * 传输已完成关闭。已释放所有资源。
   */
  void transportTerminated();
}
