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
 * A type expression matcher.
 *
 * @param <A> The type of returned values
 * @param <E> The type of raised exceptions
 */

public interface TypeExprMatcherType<A, E extends Exception>
{
  /**
   * Match an {@code array} type expression.
   *
   * @param t The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchArray(TypeExprArray t)
    throws E;

  /**
   * Match a {@code string} type expression.
   *
   * @param t The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchString(TypeExprString t)
    throws E;

  /**
   * Match a {@code boolean-set} type expression.
   *
   * @param t The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchBooleanSet(TypeExprBooleanSet t)
    throws E;

  /**
   * Match a type reference.
   *
   * @param t The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchReference(TypeExprNameType t)
    throws E;

  /**
   * Match an {@code integer} type expression.
   *
   * @param t The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchInteger(TypeExprIntegerType t)
    throws E;

  /**
   * Match a {@code float} type expression.
   *
   * @param t The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchFloat(TypeExprFloat t)
    throws E;

  /**
   * Match a {@code vector} type expression.
   *
   * @param t The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchVector(TypeExprVector t)
    throws E;

  /**
   * Match a {@code matrix} type expression.
   *
   * @param t The expression
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchMatrix(TypeExprMatrix t)
    throws E;
}
