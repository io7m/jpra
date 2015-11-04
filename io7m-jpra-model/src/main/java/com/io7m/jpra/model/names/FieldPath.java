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

package com.io7m.jpra.model.names;

import com.gs.collections.api.list.ImmutableList;
import com.io7m.jnull.NullCheck;
import org.valid4j.Assertive;

public final class FieldPath
{
  private final ImmutableList<FieldName> path;

  private FieldPath(
    final ImmutableList<FieldName> in_path)
  {
    this.path = NullCheck.notNull(in_path);
    Assertive.require(!in_path.isEmpty(), "Field path cannot be empty");
  }

  public static FieldPath ofList(
    final ImmutableList<FieldName> in_path)
  {
    return new FieldPath(in_path);
  }

  public ImmutableList<FieldName> getElements()
  {
    return this.path;
  }
}
