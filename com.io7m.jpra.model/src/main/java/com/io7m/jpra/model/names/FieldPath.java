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
import io.vavr.collection.List;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A path to a field.
 */

public final class FieldPath
{
  private final List<FieldName> path;
  private final String image;

  private FieldPath(
    final List<FieldName> in_path)
  {
    this.path = Objects.requireNonNull(in_path, "Path");

    Preconditions.checkPrecondition(
      !in_path.isEmpty(), "Field path cannot be empty");

    this.image =
      in_path.map(FieldName::value).collect(Collectors.joining("."));
  }

  /**
   * Construct a field path from a non-empty list of path elements.
   *
   * @param in_elements The elements
   *
   * @return A field path
   */

  public static FieldPath ofList(
    final List<FieldName> in_elements)
  {
    return new FieldPath(in_elements);
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

    final FieldPath fp = (FieldPath) o;
    return Objects.equals(this.image, fp.image);
  }

  @Override
  public int hashCode()
  {
    return this.image.hashCode();
  }

  @Override
  public String toString()
  {
    return this.image;
  }

  /**
   * @return The path elements
   */

  public List<FieldName> getElements()
  {
    return this.path;
  }
}
