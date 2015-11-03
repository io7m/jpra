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

final class TypeDemo0
{
  //@formatter:off
  interface QTypeExprType<
    I, T,
    TE extends QTypeExprType<I, T, TE, SE>,
    SE extends QSizeExprType<I, T, SE, TE>>
  {
    //@formatter:on

    String getType();
  }

  //@formatter:off
  interface QSizeExprType<
    I, T,
    SE extends QSizeExprType<I, T, SE, TE>,
    TE extends QTypeExprType<I, T, TE, SE>>
  {
    //@formatter:on
  }

  //@formatter:off
  static final class QSizeOctetsOf<
    I, T,
    TE extends QTypeExprType<I, T, TE, SE>,
    SE extends QSizeExprType<I, T, SE, TE>>
    implements QSizeExprType<I, T, SE, TE>
  {
    //@formatter:on

    private final T type;

    QSizeOctetsOf(final T in_type)
    {
      this.type = NullCheck.notNull(in_type);
    }
  }

  //@formatter:off
  static final class QSizeConstant<
    I, T,
    TE extends QTypeExprType<I, T, TE, SE>,
    SE extends QSizeExprType<I, T, SE, TE>>
    implements QSizeExprType<I, T, SE, TE>
  {
    //@formatter:on

    private final BigInteger size;

    QSizeConstant(final BigInteger in_size)
    {
      this.size = NullCheck.notNull(in_size);
    }
  }

  //@formatter:off
  static final class QTInteger<
    I, T,
    TE extends QTypeExprType<I, T, TE, SE>,
    SE extends QSizeExprType<I, T, SE, TE>>
    implements QTypeExprType<I, T, TE, SE>
  {
    //@formatter:on

    private final SE size;

    QTInteger(final SE in_size)
    {
      this.size = NullCheck.notNull(in_size);
    }

    @Override public String getType()
    {
      return "INTEGER";
    }
  }
}
