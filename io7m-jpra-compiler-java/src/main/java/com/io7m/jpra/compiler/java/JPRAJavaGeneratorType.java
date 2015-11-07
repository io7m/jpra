/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jpra.compiler.java;

import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.types.TPacked;
import com.io7m.jpra.model.types.TRecord;

import java.io.IOException;
import java.io.OutputStream;

/**
 * The type of Java source code generators.
 */

public interface JPRAJavaGeneratorType
{
  /**
   * @param t The type name
   *
   * @return The class name that will result for a byte-buffered implementation
   * of {@code t}
   */

  String getRecordImplementationByteBufferedName(TypeName t);

  /**
   * @param t The type name
   *
   * @return The class name that will result for the readable interface of
   * {@code t}
   */

  String getRecordInterfaceReadableName(TypeName t);

  /**
   * @param t The type name
   *
   * @return The class name that will result for the writable interface of
   * {@code t}
   */

  String getRecordInterfaceWritableName(TypeName t);

  /**
   * @param t The type name
   *
   * @return The class name that will result for the readable and writable
   * interface of {@code t}
   */

  String getRecordInterfaceName(TypeName t);

  /**
   * Generate Java source code for the implementation of a {@code record}
   * definition.
   *
   * @param t  The type
   * @param os The output stream
   *
   * @throws IOException On I/O errors
   */

  void generateRecordImplementation(
    TRecord t,
    OutputStream os)
    throws IOException;

  /**
   * Generate Java source code for the readable interface of a {@code record}
   * definition.
   *
   * @param t  The type
   * @param os The output stream
   *
   * @throws IOException On I/O errors
   */

  void generateRecordInterfaceReadable(
    TRecord t,
    OutputStream os)
    throws IOException;

  /**
   * Generate Java source code for the writable interface of a {@code record}
   * definition.
   *
   * @param t  The type
   * @param os The output stream
   *
   * @throws IOException On I/O errors
   */

  void generateRecordInterfaceWritable(
    TRecord t,
    OutputStream os)
    throws IOException;

  /**
   * Generate Java source code for the readable and writable interface of a
   * {@code record} definition.
   *
   * @param t  The type
   * @param os The output stream
   *
   * @throws IOException On I/O errors
   */

  void generateRecordInterface(
    TRecord t,
    OutputStream os)
    throws IOException;

  /**
   * Generate Java source code for a {@code packed} definition.
   *
   * @param t  The type
   * @param os The output stream
   *
   * @throws IOException On I/O errors
   */

  void generatePacked(
    TPacked t,
    OutputStream os)
    throws IOException;
}
