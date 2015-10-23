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

import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A package import declaration.
 */

@Immutable public final class PackageImport implements ModelElementType
{
  private final PackageNameQualified   from;
  private final PackageNameQualified   to;
  private final PackageNameUnqualified using;

  /**
   * Construct a package import declaration.
   *
   * @param in_from  The importing package
   * @param in_to    The imported package
   * @param in_using The unqualified name used to refer to {@code in_to}
   */

  public PackageImport(
    final PackageNameQualified in_from,
    final PackageNameQualified in_to,
    final PackageNameUnqualified in_using)
  {
    this.from = NullCheck.notNull(in_from);
    this.to = NullCheck.notNull(in_to);
    this.using = NullCheck.notNull(in_using);
  }

  /**
   * @return The importing package
   */

  public PackageNameQualified getFrom()
  {
    return this.from;
  }

  /**
   * @return The imported package
   */

  public PackageNameQualified getTo()
  {
    return this.to;
  }

  /**
   * @return The unqualified name used to refer to the imported package
   */

  public PackageNameUnqualified getUsing()
  {
    return this.using;
  }

  @Override public boolean equals(final Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    final PackageImport that = (PackageImport) o;
    return this.from.equals(that.from)
           && this.to.equals(that.to)
           && this.using.equals(that.using);
  }

  @Override public int hashCode()
  {
    int result = this.from.hashCode();
    result = 31 * result + this.to.hashCode();
    result = 31 * result + this.using.hashCode();
    return result;
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.using.getLexicalInformation();
  }
}
