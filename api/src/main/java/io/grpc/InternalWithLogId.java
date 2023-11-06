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

/**
 * An internal class. Do not use.
 * 一个内部类。不要使用。
 *
 * <p>A loggable ID, unique for the duration of the program.
 * 可登录ID，在程序期间唯一。
 */
@Internal
public interface InternalWithLogId {
  /**
   * Returns an ID that is primarily used in debug logs. It usually contains the class name and a
   * numeric ID that is unique among the instances.
   * <br>
   * 返回主要用于调试日志的ID。它通常包含类名和实例之间唯一的数字ID。
   *
   * <p>The subclasses of this interface usually want to include the log ID in their {@link
   * Object#toString} results.
   * 此接口的子类通常希望在其 {@link Object#toString} 结果中包含日志ID。
   */
  InternalLogId getLogId();
}
