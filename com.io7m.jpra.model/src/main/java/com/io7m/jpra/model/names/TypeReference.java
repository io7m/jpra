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

import com.io7m.jnull.NullCheck;

import java.util.Optional;

/**
 * A reference to a type by name.
 */

public final class TypeReference
{
  private final Optional<PackageNameUnqualified> pack;
  private final TypeName type;

  /**
   * Construct a reference.
   *
   * @param in_pack The package name, if any
   * @param in_type The type name
   */

  public TypeReference(
    final Optional<PackageNameUnqualified> in_pack,
    final TypeName in_type)
  {
    this.type = NullCheck.notNull(in_type, "Type");
    this.pack = NullCheck.notNull(in_pack, "Pack");
  }

  /**
   * @return The package name, if any
   */

  public Optional<PackageNameUnqualified> getPackage()
  {
    return this.pack;
  }

  /**
   * @return The type name
   */

  public TypeName getType()
  {
    return this.type;
  }
}
