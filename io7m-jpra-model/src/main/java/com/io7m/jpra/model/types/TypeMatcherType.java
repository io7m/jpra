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

package com.io7m.jpra.model.types;

/**
 * A type matcher.
 *
 * @param <A> The type of returned values
 * @param <E> The type of raised exceptions
 */

public interface TypeMatcherType<A, E extends Exception>
{
  /**
   * Match an {@code array} type.
   *
   * @param t The type
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchArray(TArray t)
    throws E;

  /**
   * Match a {@code string} type.
   *
   * @param t The type
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchString(TString t)
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

  A matchBooleanSet(TBooleanSet t)
    throws E;

  /**
   * Match an {@code integer} type.
   *
   * @param t The type
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchInteger(TIntegerType t)
    throws E;

  /**
   * Match a {@code float} type.
   *
   * @param t The type
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchFloat(TFloat t)
    throws E;

  /**
   * Match a {@code vector} type.
   *
   * @param t The type
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchVector(TVector t)
    throws E;

  /**
   * Match a {@code matrix} type.
   *
   * @param t The type
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchMatrix(TMatrix t)
    throws E;

  /**
   * Match a {@code record} type.
   *
   * @param t The type
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchRecord(TRecord t)
    throws E;

  /**
   * Match a {@code packed} type.
   *
   * @param t The type
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchPacked(TPacked t)
    throws E;
}
