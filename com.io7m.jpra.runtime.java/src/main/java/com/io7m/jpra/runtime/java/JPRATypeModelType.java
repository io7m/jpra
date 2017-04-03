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

package com.io7m.jpra.runtime.java;

import org.immutables.value.Value;

/**
 * The run-time {@code jpra} type model.
 */

@Value.Enclosing public interface JPRATypeModelType
{
  /**
   * The type of {@code jpra} scalar types.
   */

  interface JPRAScalarType extends JPRATypeType
  {
    @Value.Parameter int sizeInBits();

    <A, E extends Exception> A matchScalar(
      final JPRAScalarMatcherType<A, E> m)
      throws E;
  }

  /**
   * The type of {@code jpra} scalar type matchers.
   *
   * @param <A> The type of returned values
   * @param <E> The type of raised exceptions
   */

  interface JPRAScalarMatcherType<A, E extends Exception>
  {
    /**
     * Match a float type.
     *
     * @param t The type
     *
     * @return A value of {@code A}
     *
     * @throws E If required
     */

    A onFloat(JPRAFloatType t)
      throws E;

    /**
     * Match a signed integer type.
     *
     * @param t The type
     *
     * @return A value of {@code A}
     *
     * @throws E If required
     */

    A onIntegerSigned(JPRAIntegerSignedType t)
      throws E;

    /**
     * Match an unsigned integer type.
     *
     * @param t The type
     *
     * @return A value of {@code A}
     *
     * @throws E If required
     */

    A onIntegerUnsigned(JPRAIntegerUnsignedType t)
      throws E;

    /**
     * Match a signed normalized integer type.
     *
     * @param t The type
     *
     * @return A value of {@code A}
     *
     * @throws E If required
     */

    A onIntegerSignedNormalized(JPRAIntegerSignedNormalizedType t)
      throws E;

    /**
     * Match an unsigned normalized integer type.
     *
     * @param t The type
     *
     * @return A value of {@code A}
     *
     * @throws E If required
     */

    A onIntegerUnsignedNormalized(JPRAIntegerUnsignedNormalizedType t)
      throws E;
  }

  /**
   * The type of {@code jpra} types.
   */

  interface JPRATypeType
  {
    <A, E extends Exception> A matchType(
      final JPRATypeMatcherType<A, E> m)
      throws E;
  }

  /**
   * The type of {@code jpra} type matchers.
   *
   * @param <A> The type of returned values
   * @param <E> The type of raised exceptions
   */

  interface JPRATypeMatcherType<A, E extends Exception>
  {
    /**
     * Match a scalar type.
     *
     * @param t The type
     *
     * @return A value of {@code A}
     *
     * @throws E If required
     */

    A onScalar(JPRAScalarType t)
      throws E;

    /**
     * Match a vector type.
     *
     * @param t The type
     *
     * @return A value of {@code A}
     *
     * @throws E If required
     */

    A onVector(JPRAVectorType t)
      throws E;

    /**
     * Match a matrix type.
     *
     * @param t The type
     *
     * @return A value of {@code A}
     *
     * @throws E If required
     */

    A onMatrix(JPRAMatrixType t)
      throws E;

    /**
     * Match a string type.
     *
     * @param t The type
     *
     * @return A value of {@code A}
     *
     * @throws E If required
     */

    A onString(JPRAStringType t)
      throws E;

    /**
     * Match a user-defined type.
     *
     * @param t The type
     *
     * @return A value of {@code A}
     *
     * @throws E If required
     */

    A onUserDefined(JPRAUserDefinedType t)
      throws E;

    /**
     * Match a {@code boolean-set} type.
     *
     * @param t The type
     *
     * @return A value of {@code A}
     *
     * @throws E If required
     */

    A onBooleanSet(JPRABooleanSetType t)
      throws E;

    /**
     * Match an {@code array} type.
     *
     * @param t The type
     *
     * @return A value of {@code A}
     *
     * @throws E If required
     */

    A onArray(JPRAArrayType t)
      throws E;
  }

  /**
   * The type of {@code jpra} {@code float} types.
   */

  @Value.Immutable(builder = false, prehash = true) abstract class JPRAFloatType
    implements JPRAScalarType
  {
    @Override public final <A, E extends Exception> A matchScalar(
      final JPRAScalarMatcherType<A, E> m)
      throws E
    {
      return m.onFloat(this);
    }

    @Override public final <A, E extends Exception> A matchType(
      final JPRATypeMatcherType<A, E> m)
      throws E
    {
      return m.onScalar(this);
    }
  }

  /**
   * The type of {@code jpra} {@code signed integer} types.
   */

  @Value.Immutable(builder = false, prehash = true)
  abstract class JPRAIntegerSignedType
    implements JPRAScalarType
  {
    @Override public final <A, E extends Exception> A matchScalar(
      final JPRAScalarMatcherType<A, E> m)
      throws E
    {
      return m.onIntegerSigned(this);
    }

    @Override public final <A, E extends Exception> A matchType(
      final JPRATypeMatcherType<A, E> m)
      throws E
    {
      return m.onScalar(this);
    }
  }

  /**
   * The type of {@code jpra} {@code unsigned integer} types.
   */

  @Value.Immutable(builder = false, prehash = true)
  abstract class JPRAIntegerUnsignedType
    implements JPRAScalarType
  {
    @Override public final <A, E extends Exception> A matchScalar(
      final JPRAScalarMatcherType<A, E> m)
      throws E
    {
      return m.onIntegerUnsigned(this);
    }

    @Override public final <A, E extends Exception> A matchType(
      final JPRATypeMatcherType<A, E> m)
      throws E
    {
      return m.onScalar(this);
    }
  }

  /**
   * The type of {@code jpra} {@code signed normalized integer} types.
   */

  @Value.Immutable(builder = false, prehash = true)
  abstract class JPRAIntegerSignedNormalizedType
    implements JPRAScalarType
  {
    @Override public final <A, E extends Exception> A matchScalar(
      final JPRAScalarMatcherType<A, E> m)
      throws E
    {
      return m.onIntegerSignedNormalized(this);
    }

    @Override public final <A, E extends Exception> A matchType(
      final JPRATypeMatcherType<A, E> m)
      throws E
    {
      return m.onScalar(this);
    }
  }

  /**
   * The type of {@code jpra} {@code unsigned normalized integer} types.
   */

  @Value.Immutable(builder = false, prehash = true)
  abstract class JPRAIntegerUnsignedNormalizedType
    implements JPRAScalarType
  {
    @Override public final <A, E extends Exception> A matchScalar(
      final JPRAScalarMatcherType<A, E> m)
      throws E
    {
      return m.onIntegerUnsignedNormalized(this);
    }

    @Override public final <A, E extends Exception> A matchType(
      final JPRATypeMatcherType<A, E> m)
      throws E
    {
      return m.onScalar(this);
    }
  }

  /**
   * The type of {@code jpra} {@code matrix} types.
   */

  @Value.Immutable(builder = false, prehash = true)
  abstract class JPRAMatrixType
    implements JPRATypeType
  {
    @Value.Parameter abstract int width();

    @Value.Parameter abstract int height();

    @Value.Parameter abstract JPRAScalarType elementType();

    @Override public final <A, E extends Exception> A matchType(
      final JPRATypeMatcherType<A, E> m)
      throws E
    {
      return m.onMatrix(this);
    }
  }

  /**
   * The type of {@code jpra} {@code vector} types.
   */

  @Value.Immutable(builder = false, prehash = true)
  abstract class JPRAVectorType implements JPRATypeType
  {
    @Value.Parameter abstract int length();

    @Value.Parameter abstract JPRAScalarType elementType();

    @Override public final <A, E extends Exception> A matchType(
      final JPRATypeMatcherType<A, E> m)
      throws E
    {
      return m.onVector(this);
    }
  }

  /**
   * The type of {@code jpra} {@code string} types.
   */

  @Value.Immutable(builder = false, prehash = true)
  abstract class JPRAStringType implements JPRATypeType
  {
    @Value.Parameter abstract int length();

    @Value.Parameter abstract String encoding();

    @Override public final <A, E extends Exception> A matchType(
      final JPRATypeMatcherType<A, E> m)
      throws E
    {
      return m.onString(this);
    }
  }

  /**
   * The type of {@code jpra} {@code string} types.
   */

  @Value.Immutable(builder = false, prehash = true)
  abstract class JPRABooleanSetType implements JPRATypeType
  {
    @Value.Parameter abstract int sizeInOctets();

    @Override public final <A, E extends Exception> A matchType(
      final JPRATypeMatcherType<A, E> m)
      throws E
    {
      return m.onBooleanSet(this);
    }
  }

  /**
   * The type of {@code jpra} user-defined types.
   */

  @Value.Immutable(builder = false, prehash = true)
  abstract class JPRAUserDefinedType implements JPRATypeType
  {
    @Value.Parameter abstract Class<? extends JPRAValueType> type();

    @Override public final <A, E extends Exception> A matchType(
      final JPRATypeMatcherType<A, E> m)
      throws E
    {
      return m.onUserDefined(this);
    }
  }

  /**
   * The type of {@code jpra} {@code array} types.
   */

  @Value.Immutable(builder = false, prehash = true)
  abstract class JPRAArrayType implements JPRATypeType
  {
    @Value.Parameter abstract int length();

    @Value.Parameter abstract JPRATypeType elementType();

    @Override public final <A, E extends Exception> A matchType(
      final JPRATypeMatcherType<A, E> m)
      throws E
    {
      return m.onArray(this);
    }
  }
}
