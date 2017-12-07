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

package com.io7m.jpra.model.statements;

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.model.ModelElementType;
import com.io7m.jpra.model.names.PackageNameQualified;

import java.net.URI;
import java.util.Objects;

/**
 * A {@code package-begin} statement.
 *
 * @param <I> The type of identifiers
 * @param <T> The type of type information
 */

public final class StatementPackageBegin<I, T>
  implements ModelElementType, StatementType<I, T>
{
  private final PackageNameQualified pack;

  /**
   * Construct a statement.
   *
   * @param in_pack The package name
   */

  public StatementPackageBegin(
    final PackageNameQualified in_pack)
  {
    this.pack = Objects.requireNonNull(in_pack, "Pack");
  }

  /**
   * @return The package name
   */

  public PackageNameQualified getPackageName()
  {
    return this.pack;
  }

  @Override
  public LexicalPosition<URI> lexical()
  {
    return this.pack.lexical();
  }

  @Override
  public <A, E extends Exception> A matchStatement(
    final StatementMatcherType<I, T, A, E> m)
    throws E
  {
    return m.matchPackageBegin(this);
  }
}
