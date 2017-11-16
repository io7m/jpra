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

import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.set.ImmutableSet;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.compiler.core.JPRACompilerException;
import com.io7m.jpra.model.Untyped;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.type_declarations.PackedFieldDeclValue;
import com.io7m.jpra.model.type_expressions.TypeExprBooleanSet;
import com.io7m.jpra.model.type_expressions.TypeExprFloat;
import com.io7m.jpra.model.type_expressions.TypeExprMatrix;
import com.io7m.jpra.model.type_expressions.TypeExprString;
import com.io7m.jpra.model.type_expressions.TypeExprType;
import com.io7m.jpra.model.type_expressions.TypeExprVector;
import com.io7m.jpra.model.types.Size;
import com.io7m.jpra.model.types.SizeUnitBitsType;
import com.io7m.jpra.model.types.SizeUnitType;
import com.io7m.jpra.model.types.TFloat;
import com.io7m.jpra.model.types.TIntegerType;
import com.io7m.jpra.model.types.TType;
import com.io7m.jranges.RangeInclusiveB;
import io.vavr.Tuple2;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * The type of exceptions raised during checking.
 */

public final class JPRACompilerCheckerException extends JPRACompilerException
{
  private final JPRACheckerErrorCode code;

  /**
   * Construct an exception.
   *
   * @param in_lex  Lexical information, if any
   * @param in_code The error code
   * @param message The exception message
   */

  public JPRACompilerCheckerException(
    final Optional<LexicalPosition<Path>> in_lex,
    final JPRACheckerErrorCode in_code,
    final String message)
  {
    super(in_lex, message);
    this.code = Objects.requireNonNull(in_code, "Code");
  }

  /**
   * Construct an exception.
   *
   * @param in_lex  Lexical information, if any
   * @param in_code The error code
   * @param e       The cause
   */

  public JPRACompilerCheckerException(
    final Optional<LexicalPosition<Path>> in_lex,
    final JPRACheckerErrorCode in_code,
    final Exception e)
  {
    super(in_lex, e);
    this.code = Objects.requireNonNull(in_code, "Code");
  }

  /**
   * @param e     The expression
   * @param size  The size
   * @param sizes The supported sizes
   * @param <T>   The precise type of size units
   *
   * @return An exception
   *
   * @see JPRACheckerErrorCode#RECORD_INTEGER_SIZE_UNSUPPORTED
   */

  public static <T extends SizeUnitType> JPRACompilerCheckerException
  integerSizeNotSupported(
    final TypeExprType<IdentifierType, Untyped> e,
    final Size<T> size,
    final ImmutableList<RangeInclusiveB> sizes)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Integer size not supported for record fields.");
    sb.append(System.lineSeparator());
    sb.append("  Size: ");
    sb.append(size.getValue());
    sb.append(System.lineSeparator());
    sb.append("  Supported sizes (bits): ");

    final int max = sizes.size();
    for (int index = 0; index < max; ++index) {
      final RangeInclusiveB r = sizes.get(index);
      final BigInteger lo = r.getLower();
      final BigInteger hi = r.getUpper();
      if (Objects.equals(lo, hi)) {
        sb.append(lo);
      } else {
        sb.append("[");
        sb.append(lo);
        sb.append(", ");
        sb.append(hi);
        sb.append("]");
      }

      if (index + 1 < max) {
        sb.append(", ");
      }
    }

    return new JPRACompilerCheckerException(
      e.getLexicalInformation(),
      JPRACheckerErrorCode.RECORD_INTEGER_SIZE_UNSUPPORTED,
      sb.toString());
  }

  /**
   * @param e     The expression
   * @param size  The size
   * @param sizes The supported sizes
   *
   * @return An exception
   *
   * @see JPRACheckerErrorCode#RECORD_FLOAT_SIZE_UNSUPPORTED
   */

  public static JPRACompilerCheckerException floatSizeNotSupported(
    final TypeExprFloat<IdentifierType, Untyped> e,
    final Size<SizeUnitBitsType> size,
    final ImmutableList<RangeInclusiveB> sizes)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Float size not supported for record fields.");
    sb.append(System.lineSeparator());
    sb.append("  Size: ");
    sb.append(size.getValue());
    sb.append(System.lineSeparator());
    sb.append("  Supported sizes (bits): ");

    final int max = sizes.size();
    for (int index = 0; index < max; ++index) {
      final RangeInclusiveB r = sizes.get(index);
      final BigInteger lo = r.getLower();
      final BigInteger hi = r.getUpper();
      if (Objects.equals(lo, hi)) {
        sb.append(lo);
      } else {
        sb.append("[");
        sb.append(lo);
        sb.append(", ");
        sb.append(hi);
        sb.append("]");
      }

      if (index + 1 < max) {
        sb.append(", ");
      }
    }

    return new JPRACompilerCheckerException(
      e.getLexicalInformation(),
      JPRACheckerErrorCode.RECORD_FLOAT_SIZE_UNSUPPORTED,
      sb.toString());
  }

  /**
   * @param e         The expression
   * @param encoding  The string encoding
   * @param encodings The supported encodings
   *
   * @return An exception
   *
   * @see JPRACheckerErrorCode#STRING_ENCODING_UNSUPPORTED
   */

  public static JPRACompilerCheckerException stringEncodingNotSupported(
    final TypeExprString<IdentifierType, Untyped> e,
    final String encoding,
    final ImmutableSet<String> encodings)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("String encoding not supported.");
    sb.append(System.lineSeparator());
    sb.append("  Encoding: ");
    sb.append(encoding);
    sb.append(System.lineSeparator());
    sb.append("  Supported encodings: ");

    for (final String en : encodings) {
      sb.append(en);
      sb.append(" ");
    }

    return new JPRACompilerCheckerException(
      e.getLexicalInformation(),
      JPRACheckerErrorCode.STRING_ENCODING_UNSUPPORTED,
      sb.toString());
  }

  /**
   * @param e     The expression
   * @param range The valid size range
   * @param value The size
   *
   * @return An exception
   *
   * @see JPRACheckerErrorCode#BOOLEAN_SET_SIZE_INVALID
   */

  public static JPRACompilerCheckerException booleanSetSizeInvalid(
    final TypeExprBooleanSet<IdentifierType, Untyped> e,
    final RangeInclusiveB range,
    final BigInteger value)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Invalid boolean set size.");
    sb.append(System.lineSeparator());
    sb.append("  Size: ");
    sb.append(value);
    sb.append(System.lineSeparator());
    sb.append("  Valid sizes: [");
    sb.append(range.getLower());
    sb.append(", ");
    sb.append(range.getUpper());
    sb.append("]");

    return new JPRACompilerCheckerException(
      e.getLexicalInformation(),
      JPRACheckerErrorCode.BOOLEAN_SET_SIZE_INVALID,
      sb.toString());
  }

  /**
   * @param e        The expression
   * @param required The required space
   * @param value    The size
   *
   * @return An exception
   *
   * @see JPRACheckerErrorCode#BOOLEAN_SET_SIZE_TOO_SMALL
   */

  public static JPRACompilerCheckerException booleanSetSizeLessThanRequired(
    final TypeExprBooleanSet<IdentifierType, Untyped> e,
    final BigInteger required,
    final BigInteger value)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Not enough space allocated for boolean set.");
    sb.append(System.lineSeparator());
    sb.append("  Required size (bits): ");
    sb.append(required);
    sb.append(System.lineSeparator());
    sb.append("  Specified size (bits): ");
    sb.append(value);

    return new JPRACompilerCheckerException(
      e.getLexicalInformation(),
      JPRACheckerErrorCode.BOOLEAN_SET_SIZE_TOO_SMALL,
      sb.toString());
  }

  /**
   * @param e         The expression
   * @param size      The size
   * @param supported The supported sizes
   *
   * @return An exception
   *
   * @see JPRACheckerErrorCode#VECTOR_SIZE_UNSUPPORTED
   */

  public static JPRACompilerCheckerException vectorSizeNotSupported(
    final TypeExprVector<IdentifierType, Untyped> e,
    final BigInteger size,
    final ImmutableList<RangeInclusiveB> supported)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Vector size not supported.");
    sb.append(System.lineSeparator());
    sb.append("  Size: ");
    sb.append(size);
    sb.append(System.lineSeparator());
    sb.append("  Supported sizes (elements): ");

    final int max = supported.size();
    for (int index = 0; index < max; ++index) {
      final RangeInclusiveB r = supported.get(index);
      final BigInteger lo = r.getLower();
      final BigInteger hi = r.getUpper();
      if (Objects.equals(lo, hi)) {
        sb.append(lo);
      } else {
        sb.append("[");
        sb.append(lo);
        sb.append(", ");
        sb.append(hi);
        sb.append("]");
      }

      if (index + 1 < max) {
        sb.append(", ");
      }
    }

    return new JPRACompilerCheckerException(
      e.getLexicalInformation(),
      JPRACheckerErrorCode.VECTOR_SIZE_UNSUPPORTED,
      sb.toString());
  }

  /**
   * @param e The expression
   *
   * @return An exception
   *
   * @see JPRACheckerErrorCode#VECTOR_NON_SCALAR_TYPE
   */

  public static JPRACompilerCheckerException vectorNonScalarElement(
    final TypeExprVector<IdentifierType, Untyped> e)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Vectors must only have elements of scalar types.");

    return new JPRACompilerCheckerException(
      e.getLexicalInformation(),
      JPRACheckerErrorCode.VECTOR_NON_SCALAR_TYPE,
      sb.toString());
  }

  /**
   * @param e         The expression
   * @param size      The size
   * @param supported The supported sizes
   *
   * @return An exception
   *
   * @see JPRACheckerErrorCode#VECTOR_SIZE_INTEGER_UNSUPPORTED
   */

  public static JPRACompilerCheckerException vectorIntegerSizeNotSupported(
    final TIntegerType e,
    final BigInteger size,
    final ImmutableList<RangeInclusiveB> supported)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Vector integer element size not supported.");
    sb.append(System.lineSeparator());
    sb.append("  Size: ");
    sb.append(size);
    sb.append(System.lineSeparator());
    sb.append("  Supported sizes (bits): ");

    final int max = supported.size();
    for (int index = 0; index < max; ++index) {
      final RangeInclusiveB r = supported.get(index);
      final BigInteger lo = r.getLower();
      final BigInteger hi = r.getUpper();
      if (Objects.equals(lo, hi)) {
        sb.append(lo);
      } else {
        sb.append("[");
        sb.append(lo);
        sb.append(", ");
        sb.append(hi);
        sb.append("]");
      }

      if (index + 1 < max) {
        sb.append(", ");
      }
    }

    return new JPRACompilerCheckerException(
      e.getLexicalInformation(),
      JPRACheckerErrorCode.VECTOR_SIZE_INTEGER_UNSUPPORTED,
      sb.toString());
  }

  /**
   * @param e         The expression
   * @param size      The size
   * @param supported The supported sizes
   *
   * @return An exception
   *
   * @see JPRACheckerErrorCode#VECTOR_SIZE_FLOAT_UNSUPPORTED
   */

  public static JPRACompilerCheckerException vectorFloatSizeNotSupported(
    final TFloat e,
    final BigInteger size,
    final ImmutableList<RangeInclusiveB> supported)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Vector float element size not supported.");
    sb.append(System.lineSeparator());
    sb.append("  Size: ");
    sb.append(size);
    sb.append(System.lineSeparator());
    sb.append("  Supported sizes (bits): ");

    final int max = supported.size();
    for (int index = 0; index < max; ++index) {
      final RangeInclusiveB r = supported.get(index);
      final BigInteger lo = r.getLower();
      final BigInteger hi = r.getUpper();
      if (Objects.equals(lo, hi)) {
        sb.append(lo);
      } else {
        sb.append("[");
        sb.append(lo);
        sb.append(", ");
        sb.append(hi);
        sb.append("]");
      }

      if (index + 1 < max) {
        sb.append(", ");
      }
    }

    return new JPRACompilerCheckerException(
      e.getLexicalInformation(),
      JPRACheckerErrorCode.VECTOR_SIZE_FLOAT_UNSUPPORTED,
      sb.toString());
  }

  /**
   * @param e         The expression
   * @param tw        The width
   * @param th        The height
   * @param supported The supported sizes
   *
   * @return An exception
   *
   * @see JPRACheckerErrorCode#MATRIX_SIZE_UNSUPPORTED
   */

  public static JPRACompilerCheckerException matrixNotSupported(
    final TypeExprMatrix<IdentifierType, Untyped> e,
    final BigInteger tw,
    final BigInteger th,
    final ImmutableList<Tuple2<RangeInclusiveB, RangeInclusiveB>> supported)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Matrix size not supported.");
    sb.append(System.lineSeparator());
    sb.append("  Size: ");
    sb.append(tw);
    sb.append(" x ");
    sb.append(th);
    sb.append(System.lineSeparator());
    sb.append("  Supported sizes: ");

    final int max = supported.size();
    for (int index = 0; index < max; ++index) {
      final Tuple2<RangeInclusiveB, RangeInclusiveB> rp = supported.get(index);
      final RangeInclusiveB l = rp._1();
      final RangeInclusiveB r = rp._2();

      {
        final BigInteger lo = l.getLower();
        final BigInteger hi = l.getUpper();
        if (Objects.equals(lo, hi)) {
          sb.append(lo);
        } else {
          sb.append("[");
          sb.append(lo);
          sb.append(", ");
          sb.append(hi);
          sb.append("]");
        }
      }

      sb.append(" x ");

      {
        final BigInteger lo = r.getLower();
        final BigInteger hi = r.getUpper();
        if (Objects.equals(lo, hi)) {
          sb.append(lo);
        } else {
          sb.append("[");
          sb.append(lo);
          sb.append(", ");
          sb.append(hi);
          sb.append("]");
        }
      }

      if (index + 1 < max) {
        sb.append(", ");
      }
    }

    return new JPRACompilerCheckerException(
      e.getLexicalInformation(),
      JPRACheckerErrorCode.MATRIX_SIZE_UNSUPPORTED,
      sb.toString());
  }

  /**
   * @param e The expression
   *
   * @return An exception
   *
   * @see JPRACheckerErrorCode#MATRIX_NON_SCALAR_TYPE
   */

  public static JPRACompilerCheckerException matrixNonScalarElement(
    final TypeExprMatrix<IdentifierType, Untyped> e)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Matrices must only have elements of scalar types.");

    return new JPRACompilerCheckerException(
      e.getLexicalInformation(),
      JPRACheckerErrorCode.MATRIX_NON_SCALAR_TYPE,
      sb.toString());
  }

  /**
   * @param e         The expression
   * @param size      The size
   * @param supported The supported sizes
   *
   * @return An exception
   *
   * @see JPRACheckerErrorCode#MATRIX_SIZE_FLOAT_UNSUPPORTED
   */

  public static JPRACompilerCheckerException matrixFloatSizeNotSupported(
    final TFloat e,
    final BigInteger size,
    final ImmutableList<RangeInclusiveB> supported)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Matrix float element size not supported.");
    sb.append(System.lineSeparator());
    sb.append("  Size: ");
    sb.append(size);
    sb.append(System.lineSeparator());
    sb.append("  Supported sizes (bits): ");

    final int max = supported.size();
    for (int index = 0; index < max; ++index) {
      final RangeInclusiveB r = supported.get(index);
      final BigInteger lo = r.getLower();
      final BigInteger hi = r.getUpper();
      if (Objects.equals(lo, hi)) {
        sb.append(lo);
      } else {
        sb.append("[");
        sb.append(lo);
        sb.append(", ");
        sb.append(hi);
        sb.append("]");
      }

      if (index + 1 < max) {
        sb.append(", ");
      }
    }

    return new JPRACompilerCheckerException(
      e.getLexicalInformation(),
      JPRACheckerErrorCode.MATRIX_SIZE_FLOAT_UNSUPPORTED,
      sb.toString());
  }

  /**
   * @param e         The expression
   * @param size      The size
   * @param supported The supported sizes
   *
   * @return An exception
   *
   * @see JPRACheckerErrorCode#MATRIX_SIZE_INTEGER_UNSUPPORTED
   */

  public static JPRACompilerCheckerException matrixIntegerSizeNotSupported(
    final TIntegerType e,
    final BigInteger size,
    final ImmutableList<RangeInclusiveB> supported)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Matrix integer element size not supported.");
    sb.append(System.lineSeparator());
    sb.append("  Size: ");
    sb.append(size);
    sb.append(System.lineSeparator());
    sb.append("  Supported sizes (bits): ");

    final int max = supported.size();
    for (int index = 0; index < max; ++index) {
      final RangeInclusiveB r = supported.get(index);
      final BigInteger lo = r.getLower();
      final BigInteger hi = r.getUpper();
      if (Objects.equals(lo, hi)) {
        sb.append(lo);
      } else {
        sb.append("[");
        sb.append(lo);
        sb.append(", ");
        sb.append(hi);
        sb.append("]");
      }

      if (index + 1 < max) {
        sb.append(", ");
      }
    }

    return new JPRACompilerCheckerException(
      e.getLexicalInformation(),
      JPRACheckerErrorCode.MATRIX_SIZE_INTEGER_UNSUPPORTED,
      sb.toString());
  }

  /**
   * The size of a padding field was invalid.
   *
   * @param lex   Lexical information
   * @param value The size value
   *
   * @return An exception
   *
   * @see JPRACheckerErrorCode#PADDING_SIZE_INVALID
   */

  public static JPRACompilerCheckerException paddingSizeInvalid(
    final Optional<LexicalPosition<Path>> lex,
    final BigInteger value)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Padding size must be positive.");
    sb.append(System.lineSeparator());
    sb.append("  Size: ");
    sb.append(value);

    return new JPRACompilerCheckerException(
      lex, JPRACheckerErrorCode.PADDING_SIZE_INVALID, sb.toString());
  }

  /**
   * @param r   The field declaration
   * @param rvt The type expression
   *
   * @return An exception
   *
   * @see JPRACheckerErrorCode#PACKED_NON_INTEGER
   */

  public static JPRACompilerCheckerException packedNonIntegerType(
    final PackedFieldDeclValue<IdentifierType, Untyped> r,
    final TypeExprType<IdentifierType, TType> rvt)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Packed types may only contain integer fields.");
    sb.append(System.lineSeparator());
    sb.append("  Field name: ");
    sb.append(r.getName());
    sb.append(System.lineSeparator());
    sb.append("  Field type: ");
    sb.append(rvt.getType());

    return new JPRACompilerCheckerException(
      rvt.getLexicalInformation(),
      JPRACheckerErrorCode.PACKED_NON_INTEGER,
      sb.toString());
  }

  /**
   * @param name      The type name
   * @param size      The size in bits
   * @param supported The supported sizes
   *
   * @return An exception
   *
   * @see JPRACheckerErrorCode#PACKED_SIZE_NOT_SUPPORTED
   */

  public static JPRACompilerCheckerException packedSizeNotSupported(
    final TypeName name,
    final BigInteger size,
    final ImmutableList<RangeInclusiveB> supported)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Packed type size not supported.");
    sb.append(System.lineSeparator());
    sb.append("  Size: ");
    sb.append(size);
    sb.append(System.lineSeparator());
    sb.append("  Supported sizes (bits): ");

    final int max = supported.size();
    for (int index = 0; index < max; ++index) {
      final RangeInclusiveB r = supported.get(index);
      final BigInteger lo = r.getLower();
      final BigInteger hi = r.getUpper();
      if (Objects.equals(lo, hi)) {
        sb.append(lo);
      } else {
        sb.append("[");
        sb.append(lo);
        sb.append(", ");
        sb.append(hi);
        sb.append("]");
      }

      if (index + 1 < max) {
        sb.append(", ");
      }
    }

    return new JPRACompilerCheckerException(
      name.getLexicalInformation(),
      JPRACheckerErrorCode.PACKED_SIZE_NOT_SUPPORTED,
      sb.toString());
  }

  /**
   * @return The error code
   */

  public JPRACheckerErrorCode getErrorCode()
  {
    return this.code;
  }
}
