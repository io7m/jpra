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
 * A {@code string} type expression.
 */

@Immutable public final class TypeExprString implements TypeExprType
{
  private final String                           encoding;
  private final SizeExprType<SizeUnitOctetsType> size;

  /**
   * Construct an exception.
   *
   * @param in_encoding The string encoding
   * @param in_size     An expression denoting the maximum size of the string in
   *                    octets
   */

  public TypeExprString(
    final String in_encoding,
    final SizeExprType<SizeUnitOctetsType> in_size)
  {
    this.encoding = NullCheck.notNull(in_encoding);
    this.size = NullCheck.notNull(in_size);
  }

  /**
   * @return The string encoding
   */

  public String getEncoding()
  {
    return this.encoding;
  }

  /**
   * @return The expression denoting the maximum size of the string in octets
   */

  public SizeExprType<SizeUnitOctetsType> getSizeExpression()
  {
    return this.size;
  }

  @Override public <A, E extends Exception> A matchTypeExpression(
    final TypeExprMatcherType<A, E> m)
    throws E
  {
    return m.matchString(this);
  }
}
