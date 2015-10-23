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

package com.io7m.jpra.compiler.core.checker;

import java.math.BigInteger;

/**
 * The capabilities that the checker is responsible for enforcing.
 */

public interface CheckerCapabilitiesType
{
  /**
   * @param size The size in bits
   *
   * @return {@code true} iff integers of the given size are supported in
   * records
   */

  boolean isRecordIntegerSizeBitsSupported(BigInteger size);

  /**
   * @param size The size in bits
   *
   * @return {@code true} iff floating point types of the given size are
   * supported in records
   */

  boolean isRecordFloatSizeBitsSupported(BigInteger size);

  /**
   * @param size The size in elements
   *
   * @return {@code true} iff vectors of the given size are supported in records
   */

  boolean isRecordVectorSizeElementsSupported(BigInteger size);

  /**
   * @param width  The number of matrix columns
   * @param height The number of matrix rows
   *
   * @return {@code true} iff matrices of the given size are supported in
   * records
   */

  boolean isRecordMatrixSizeElementsSupported(
    BigInteger width,
    BigInteger height);
}
