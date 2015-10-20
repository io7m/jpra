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

package com.io7m.jpra.model;

import com.io7m.jnull.NullCheck;

/**
 * An {@code unsigned} integer type expression.
 */

public final class TypeExprIntegerUnsigned implements TypeExprIntegerType
{
  private final SizeExprType<SizeUnitBitsType> size;

  /**
   * Construct a type expression.
   *
   * @param in_size An expression denoting the size of the integer in bits
   */

  public TypeExprIntegerUnsigned(final SizeExprType<SizeUnitBitsType> in_size)
  {
    this.size = NullCheck.notNull(in_size);
  }

  @Override public <A, E extends Exception> A matchTypeExpression(
    final TypeExprMatcherType<A, E> m)
    throws E
  {
    return m.matchInteger(this);
  }

  @Override public <A, E extends Exception> A matchTypeIntegerExpression(
    final TypeExprIntegerMatcherType<A, E> m)
    throws E
  {
    return m.matchIntegerUnsigned(this);
  }

  @Override public <A, E extends Exception> A matchTypeScalarExpression(
    final TypeExprScalarMatcherType<A, E> m)
    throws E
  {
    return m.matchScalarInteger(this);
  }
}
