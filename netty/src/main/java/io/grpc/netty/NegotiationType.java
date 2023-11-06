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

package io.grpc.netty;

import io.grpc.ExperimentalApi;

/**
 * Identifies the negotiation used for starting up HTTP/2.
 * <br>
 * 标识用于启动http2的协商。
 */
@ExperimentalApi("https://github.com/grpc/grpc-java/issues/1784")
public enum NegotiationType {
  /**
   * Uses TLS ALPN/NPN negotiation, assumes an SSL connection.
   * <br>
   * 使用TLS ALPNNPN协商，假设SSL连接。
   */
  TLS,

  /**
   * Use the HTTP UPGRADE protocol for a plaintext (non-SSL) upgrade from HTTP/1.1 to HTTP/2.
   * <br>
   * 使用HTTP升级协议进行从HTTP1.1到http2的明文 (非SSL) 升级。
   */
  PLAINTEXT_UPGRADE,

  /**
   * Just assume the connection is plaintext (non-SSL) and the remote endpoint supports HTTP/2
   * directly without an upgrade.
   * <br>
   * 只需假设连接是明文 (非SSL)，并且远程端点直接支持HTTP2而无需升级。
   */
  PLAINTEXT
}
