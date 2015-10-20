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
 * A {@code matrix} type expression.
 */

public final class TypeExprMatrix implements TypeExprType
{
  private final TypeExprScalarType type;
  private final SizeExprType<?>    size_width;
  private final SizeExprType<?>    size_height;

  /**
   * Construct a {@code matrix} type expression.
   *
   * @param in_size_width  An expression denoting the number of columns in the
   *                       matrix
   * @param in_size_height An expression denoting the number of rows in the
   *                       matrix
   * @param in_type        The type of matrix elements
   */

  public TypeExprMatrix(
    final SizeExprType<?> in_size_width,
    final SizeExprType<?> in_size_height,
    final TypeExprScalarType in_type)
  {
    this.size_width = NullCheck.notNull(in_size_width);
    this.size_height = NullCheck.notNull(in_size_height);
    this.type = NullCheck.notNull(in_type);
  }

  /**
   * @return The expression denoting the number of rows in the matrix
   */

  public SizeExprType<?> getHeightExpression()
  {
    return this.size_height;
  }

  /**
   * @return The expression denoting the number of columns in the matrix
   */

  public SizeExprType<?> getWidthExpression()
  {
    return this.size_width;
  }

  /**
   * @return The type of matrix elements
   */

  public TypeExprScalarType getElementType()
  {
    return this.type;
  }

  @Override public <A, E extends Exception> A matchTypeExpression(
    final TypeExprMatcherType<A, E> m)
    throws E
  {
    return m.matchMatrix(this);
  }
}
