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

package com.io7m.jpra.model.type_declarations;

/**
 * A type declaration matcher.
 *
 * @param <A> The type of returned values
 * @param <E> The type of raised exceptions
 * @param <I> The type of identifiers
 * @param <T> The type of type information
 */

public interface TypeDeclMatcherType<I, T, A, E extends Exception>
{
  /**
   * Match a type declaration.
   *
   * @param t The declaration
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchRecord(TypeDeclRecord<I, T> t)
    throws E;

  /**
   * Match a type declaration.
   *
   * @param t The declaration
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchPacked(TypeDeclPacked<I, T> t)
    throws E;
}
