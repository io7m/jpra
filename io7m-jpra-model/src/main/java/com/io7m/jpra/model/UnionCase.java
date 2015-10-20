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
 * A case in a {@code union} declaration.
 */

public final class UnionCase implements ModelElementType
{
  private final UnionCaseName name;
  private final TypeExprType  type;

  /**
   * Construct a case.
   *
   * @param in_name The name of the union case
   * @param in_type The type of the union case
   */

  public UnionCase(
    final UnionCaseName in_name,
    final TypeExprType in_type)
  {
    this.name = NullCheck.notNull(in_name);
    this.type = NullCheck.notNull(in_type);
  }

  /**
   * @return The name of the union case
   */

  public UnionCaseName getName()
  {
    return this.name;
  }
}
