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

import java.math.BigInteger;

public class TypeDemo2
{
  static {

  }

  interface QTypeExprType<I, T>
  {
    interface QSizeExprType
    {

    }
  }

  static final class QTypeExprInteger<I, T>
  {
    private final QTypeExprType.QSizeExprType size;

    QTypeExprInteger(final QTypeExprType.QSizeExprType in_size)
    {
      this.size = NullCheck.notNull(in_size);
    }
  }

  static final class QSizeConstant<I, T, TE>
    implements QTypeExprType.QSizeExprType
  {
    private final BigInteger size;

    QSizeConstant(final BigInteger in_size)
    {
      this.size = NullCheck.notNull(in_size);
    }
  }

  static final class QSizeOctetsOf<I, T, TE>
    implements QTypeExprType.QSizeExprType
  {
    private final TE type;

    QSizeOctetsOf(final TE in_type)
    {
      this.type = NullCheck.notNull(in_type);
    }
  }
}
