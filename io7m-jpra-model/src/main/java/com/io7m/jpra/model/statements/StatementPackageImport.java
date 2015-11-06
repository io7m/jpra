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

import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.ModelElementType;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;

import java.nio.file.Path;
import java.util.Optional;

/**
 * The {@code import} statement.
 *
 * @param <I> The type of identifiers
 * @param <T> The type of type information
 */

public final class StatementPackageImport<I, T>
  implements ModelElementType, StatementType<I, T>
{
  private final PackageNameQualified   pack;
  private final PackageNameUnqualified using;

  /**
   * Construct a statement.
   *
   * @param in_pack  The imported package
   * @param in_using The name that will be used to refer to the package
   */

  public StatementPackageImport(
    final PackageNameQualified in_pack,
    final PackageNameUnqualified in_using)
  {
    this.pack = NullCheck.notNull(in_pack);
    this.using = NullCheck.notNull(in_using);
  }

  /**
   * @return The short name
   */

  public PackageNameUnqualified getUsing()
  {
    return this.using;
  }

  /**
   * @return The target package name
   */

  public PackageNameQualified getPackageName()
  {
    return this.pack;
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.pack.getLexicalInformation();
  }

  @Override public <A, E extends Exception> A matchStatement(
    final StatementMatcherType<I, T, A, E> m)
    throws E
  {
    return m.matchPackageImport(this);
  }
}
