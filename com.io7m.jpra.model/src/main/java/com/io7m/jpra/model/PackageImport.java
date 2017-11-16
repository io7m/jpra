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

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.model.names.PackageNameQualified;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * A package import declaration.
 */

@Immutable
public final class PackageImport implements ModelElementType
{
  private final PackageNameQualified from;
  private final PackageNameQualified to;

  /**
   * Construct a package import declaration.
   *
   * @param in_from The importing package
   * @param in_to   The imported package
   */

  public PackageImport(
    final PackageNameQualified in_from,
    final PackageNameQualified in_to)
  {
    this.from = Objects.requireNonNull(in_from, "From");
    this.to = Objects.requireNonNull(in_to, "To");
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

  @Override
  public boolean equals(final Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    final PackageImport that = (PackageImport) o;
    return Objects.equals(this.from, that.from)
      && Objects.equals(this.to, that.to);
  }

  @Override
  public int hashCode()
  {
    int result = this.from.hashCode();
    result = 31 * result + this.to.hashCode();
    return result;
  }

  @Override
  public Optional<LexicalPosition<Path>> getLexicalInformation()
  {
    return this.from.getLexicalInformation();
  }
}
