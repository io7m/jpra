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

package com.io7m.jpra.model.type_expressions;

/**
 * The type of type expression matchers.
 *
 * @param <A> The type of returned values
 * @param <E> The type of raised exceptions
 * @param <I> The type of identifiers
 * @param <T> The type of type information
 */

public interface TypeExprMatcherType<I, T, A, E extends Exception>
{
  /**
   * Match an {@code integer signed} expression.
   *
   * @param e The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchExprIntegerSigned(
    TypeExprIntegerSigned<I, T> e)
    throws E;

  /**
   * Match an {@code integer signed-normalized} expression.
   *
   * @param e The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchExprIntegerSignedNormalized(
    TypeExprIntegerSignedNormalized<I, T> e)
    throws E;

  /**
   * Match an {@code integer unsigned} expression.
   *
   * @param e The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchExprIntegerUnsigned(
    TypeExprIntegerUnsigned<I, T> e)
    throws E;

  /**
   * Match an {@code integer unsigned-normalized} expression.
   *
   * @param e The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchExprIntegerUnsignedNormalized(
    TypeExprIntegerUnsignedNormalized<I, T> e)
    throws E;

  /**
   * Match an {@code array} expression.
   *
   * @param e The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchExprArray(
    TypeExprArray<I, T> e)
    throws E;

  /**
   * Match a {@code float} expression.
   *
   * @param e The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchExprFloat(
    TypeExprFloat<I, T> e)
    throws E;

  /**
   * Match a {@code vector} expression.
   *
   * @param e The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchExprVector(
    TypeExprVector<I, T> e)
    throws E;

  /**
   * Match a {@code matrix} expression.
   *
   * @param e The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchExprMatrix(TypeExprMatrix<I, T> e)
    throws E;

  /**
   * Match a {@code string} expression.
   *
   * @param e The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchExprString(TypeExprString<I, T> e)
    throws E;

  /**
   * Match a name expression.
   *
   * @param e The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchName(TypeExprName<I, T> e)
    throws E;

  /**
   * Match a {@code type-of} expression.
   *
   * @param e The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchTypeOfField(TypeExprTypeOfField<I, T> e)
    throws E;

  /**
   * Match a {@code boolean-set} expression.
   *
   * @param e The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchBooleanSet(
    TypeExprBooleanSet<I, T> e)
    throws E;
}
