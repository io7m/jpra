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
import net.jcip.annotations.Immutable;

/**
 * A {@code vector} type expression.
 */

@Immutable public final class TypeExprVector implements TypeExprType
{
  private final TypeExprScalarType type;
  private final SizeExprType<?>    size;

  /**
   * Construct a type expression.
   *
   * @param in_size The number of elements in the vector
   * @param in_type The type of vector elements
   */

  public TypeExprVector(
    final SizeExprType<?> in_size,
    final TypeExprScalarType in_type)
  {
    this.size = NullCheck.notNull(in_size);
    this.type = NullCheck.notNull(in_type);
  }

  /**
   * @return The number of elements in the vector
   */

  public SizeExprType<?> getElementCountExpression()
  {
    return this.size;
  }

  /**
   * @return The type of elements
   */

  public TypeExprScalarType getElementType()
  {
    return this.type;
  }

  @Override public <A, E extends Exception> A matchTypeExpression(
    final TypeExprMatcherType<A, E> m)
    throws E
  {
    return m.matchVector(this);
  }
}
