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


import com.io7m.jranges.RangeInclusiveB;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Set;

import java.math.BigInteger;

/**
 * The capabilities that the checker is responsible for enforcing.
 */

public interface JPRACheckerCapabilitiesType
{
  /**
   * @return The set of supported {@code integer} sizes in records
   */

  List<RangeInclusiveB> getRecordIntegerSizeBitsSupported();

  /**
   * @param size The size in bits
   *
   * @return {@code true} iff {@code integer} types of the given size are
   * supported in records
   */

  boolean isRecordIntegerSizeBitsSupported(BigInteger size);

  /**
   * @param size The size in bits
   *
   * @return {@code true} iff {@code float} types of the given size are
   * supported in records
   */

  boolean isRecordFloatSizeBitsSupported(BigInteger size);

  /**
   * @param size The size in elements
   *
   * @return {@code true} iff vectors of the given size are supported
   */

  boolean isVectorSizeElementsSupported(BigInteger size);

  /**
   * @param width  The number of matrix columns
   * @param height The number of matrix rows
   *
   * @return {@code true} iff matrices of the given size are supported in
   * records
   */

  boolean isMatrixSizeElementsSupported(
    BigInteger width,
    BigInteger height);

  /**
   * @return The set of supported {@code float} sizes in records
   */

  List<RangeInclusiveB> getRecordFloatSizeBitsSupported();

  /**
   * @param encoding The encoding
   *
   * @return {@code true} iff the given string encoding is supported
   */

  boolean isStringEncodingSupported(
    String encoding);

  /**
   * @return The set of supported string encodings
   */

  Set<String> getStringEncodingsSupported();

  /**
   * @return The set of supported vector sizes
   */

  List<RangeInclusiveB> getVectorSizeSupported();

  /**
   * @param size The size in bits
   *
   * @return {@code true} iff {@code integer} types of the given size are
   * supported in vectors
   */

  boolean isVectorIntegerSizeSupported(BigInteger size);

  /**
   * @return The set of supported {@code integer} sizes in vectors
   */

  List<RangeInclusiveB> getVectorIntegerSizeSupported();

  /**
   * @param size The size in bits
   *
   * @return {@code true} iff {@code float} types of the given size are
   * supported in vectors
   */

  boolean isVectorFloatSizeSupported(BigInteger size);

  /**
   * @return The set of supported {@code float} sizes in vectors
   */

  List<RangeInclusiveB> getVectorFloatSizeSupported();

  /**
   * @return The set of supported matrix sizes
   */

  List<Tuple2<RangeInclusiveB, RangeInclusiveB>>
  getMatrixSizeElementsSupported();

  /**
   * @param size The size in bits
   *
   * @return {@code true} iff {@code integer} types of the given size are
   * supported in matrices
   */

  boolean isMatrixIntegerSizeSupported(BigInteger size);

  /**
   * @return The set of supported {@code integer} sizes in matrices
   */

  List<RangeInclusiveB> getMatrixIntegerSizeSupported();

  /**
   * @param size The size in bits
   *
   * @return {@code true} iff {@code float} types of the given size are
   * supported in matrices
   */

  boolean isMatrixFloatSizeSupported(BigInteger size);

  /**
   * @return The set of supported {@code float} sizes in matrices
   */

  List<RangeInclusiveB> getMatrixFloatSizeSupported();

  /**
   * @return The set of supported {@code integer} sizes in {@code packed} types
   */

  List<RangeInclusiveB> getPackedIntegerSizeBitsSupported();

  /**
   * @param size The size in bits
   *
   * @return {@code true} iff {@code integer} types of the given size are
   * supported in {@code packed} types
   */

  boolean isPackedIntegerSizeBitsSupported(BigInteger size);

  /**
   * @return The set of supported {@code packed} type sizes
   */

  List<RangeInclusiveB> getPackedSizeBitsSupported();

  /**
   * @param size The size in bits
   *
   * @return {@code true} iff {@code packed} types of the given size are
   * supported
   */

  boolean isPackedSizeBitsSupported(BigInteger size);
}
