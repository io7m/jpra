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

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.model.ModelElementType;
import io.vavr.collection.List;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * A fully qualified package name.
 */

@Immutable
public final class PackageNameQualified implements ModelElementType
{
  private final List<PackageNameUnqualified> value;
  private final String image;

  /**
   * Construct a name.
   *
   * @param in_value The list of segments making up the full name
   */

  public PackageNameQualified(
    final List<PackageNameUnqualified> in_value)
  {
    this.value = Objects.requireNonNull(in_value, "Value");

    Preconditions.checkPreconditionV(
      Integer.valueOf(in_value.size()),
      in_value.size() > 0,
      "Name length %d must be > 0",
      Integer.valueOf(in_value.size()));

    final StringBuilder sb = new StringBuilder(this.value.size() * 8);
    for (int index = 0; index < this.value.size(); ++index) {
      sb.append(this.value.get(index).value());
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
    return new PackageNameQualified(List.of(p));
  }

  /**
   * Parse a string, yielding a qualified package name.
   *
   * @param text The raw text
   *
   * @return A package name
   */

  public static PackageNameQualified valueOf(final String text)
  {
    final String[] segments = text.split("\\.");
    return new PackageNameQualified(
      List.of(segments)
        .map(raw -> PackageNameUnqualified.of(Optional.empty(), raw)));
  }

  @Override
  public String toString()
  {
    return this.image;
  }

  /**
   * @return The raw name segments
   */

  public List<PackageNameUnqualified> getValue()
  {
    return this.value;
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

    final PackageNameQualified that = (PackageNameQualified) o;
    return Objects.equals(this.image, that.image);
  }

  @Override
  public int hashCode()
  {
    return this.image.hashCode();
  }

  @Override
  public Optional<LexicalPosition<Path>> lexical()
  {
    return this.value.get(0).lexical();
  }
}
