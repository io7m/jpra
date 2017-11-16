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

import java.util.Objects;
import java.util.Optional;

/**
 * A reference to a field.
 */

public final class FieldReference
{
  private final Optional<PackageNameUnqualified> pack;
  private final Optional<TypeName> type;
  private final FieldPath field_path;

  /**
   * Construct a reference.
   *
   * @param in_pack       The package name, if any
   * @param in_type       The type name, if any
   * @param in_field_path The field path
   */

  public FieldReference(
    final Optional<PackageNameUnqualified> in_pack,
    final Optional<TypeName> in_type,
    final FieldPath in_field_path)
  {
    this.pack = Objects.requireNonNull(in_pack, "Package");
    this.type = Objects.requireNonNull(in_type, "Type");
    this.field_path = Objects.requireNonNull(in_field_path, "Field path");
  }

  /**
   * @return The field path
   */

  public FieldPath getFieldPath()
  {
    return this.field_path;
  }

  /**
   * @return The package name, if any
   */

  public Optional<PackageNameUnqualified> getPackage()
  {
    return this.pack;
  }

  /**
   * @return The type name, if any
   */

  public Optional<TypeName> getType()
  {
    return this.type;
  }
}
