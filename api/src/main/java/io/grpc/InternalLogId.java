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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nullable;

/**
 * An internal class. Do not use.<br>
 * 一个内部类。不要使用。
 *
 *<p>An object that has an ID that is unique within the JVM, primarily for debug logging.
 *
 *<p>具有在JVM中唯一的ID的对象，主要用于调试日志记录。
 */
@Internal
public final class InternalLogId {

  /**
   * id分配.
   */
  private static final AtomicLong idAlloc = new AtomicLong();

  /**
   * Creates a log id. <br>
   * 创建日志id
   *
   * @param type the "Type" to be used when logging this id.   The short name of this class will be
   *     used, or else a default if the class is anonymous. 记录此id时要使用的 “类型”。
   *             将使用此类的短名称，或者如果类是匿名的，则使用默认值。
   * @param details a short, human readable string that describes the object the id is attached to.
   *     Typically this will be an address or target. 一个简短的可读字符串，用于描述id附加到的对象。通常，这将是地址或目标。
   */
  public static InternalLogId allocate(Class<?> type, @Nullable String details) {
    return allocate(getClassName(type), details);
  }

  /**
   * Creates a log id.<br>
   * 创建日志id。
   *
   * @param typeName the "Type" to be used when logging this id.    记录此id时要使用的 “类型”。
   * @param details a short, human readable string that describes the object the id is attached to.
   *     Typically this will be an address or target. 一个简短的可读字符串，用于描述id附加到的对象。通常，这将是地址或目标。
   */
  public static InternalLogId allocate(String typeName, @Nullable String details) {
    return new InternalLogId(typeName, details, getNextId());
  }

  /**
   * 分配一个id.
   */
  static long getNextId() {
    return idAlloc.incrementAndGet();
  }

  private final String typeName;
  @Nullable
  private final String details;
  private final long id;

  InternalLogId(String typeName, String details, long id) {
    checkNotNull(typeName, "typeName");
    checkArgument(!typeName.isEmpty(), "empty type");
    this.typeName = typeName;
    this.details = details;
    this.id = id;
  }

  public String getTypeName() {
    return typeName;
  }

  @Nullable
  public String getDetails() {
    return details;
  }

  public long getId() {
    return id;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(shortName());
    if (details != null) {
      sb.append(": (");
      sb.append(details);
      sb.append(')');
    }
    return sb.toString();
  }

  private static String getClassName(Class<?> type) {
    String className = checkNotNull(type, "type").getSimpleName();
    if (!className.isEmpty()) {
      return className;
    }
    // + 1 removes the separating '.'
    // 貌似是删除包名
    return type.getName().substring(type.getPackage().getName().length() + 1);
  }

  public String shortName() {
    return typeName + "<" + id + ">";
  }
}
