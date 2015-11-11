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
import com.gs.collections.impl.factory.Lists;
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.ModelElementType;
import net.jcip.annotations.Immutable;
import org.valid4j.Assertive;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A fully qualified package name.
 */

@Immutable public final class PackageNameQualified implements ModelElementType
{
  private final ImmutableList<PackageNameUnqualified> value;
  private final String                                image;

  /**
   * Construct a name.
   *
   * @param in_value The list of segments making up the full name
   */

  public PackageNameQualified(
    final ImmutableList<PackageNameUnqualified> in_value)
  {
    this.value = NullCheck.notNull(in_value);

    Assertive.require(
      in_value.size() > 0,
      "Name length %d must be > 0",
      Integer.valueOf(in_value.size()));

    final StringBuilder sb = new StringBuilder(this.value.size() * 8);
    for (int index = 0; index < this.value.size(); ++index) {
      sb.append(this.value.get(index));
      if (index + 1 < this.value.size()) {
        sb.append(".");
      }
    }
    this.image = sb.toString();
  }

  /**
   * Construct a name from the given segments.
   *
   * @param p The segments
   *
   * @return A new name
   */

  public static PackageNameQualified of(final PackageNameUnqualified... p)
  {
    return new PackageNameQualified(Lists.immutable.of(p));
  }

  @Override public String toString()
  {
    return this.image;
  }

  /**
   * @return The raw name segments
   */

  public ImmutableList<PackageNameUnqualified> getValue()
  {
    return this.value;
  }

  @Override public boolean equals(final Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    final PackageNameQualified that = (PackageNameQualified) o;
    return this.image.equals(that.image);
  }

  @Override public int hashCode()
  {
    return this.image.hashCode();
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.value.get(0).getLexicalInformation();
  }
}
