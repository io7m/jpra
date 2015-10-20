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

import com.io7m.jnull.NullCheck;
import net.jcip.annotations.Immutable;

import java.util.Optional;

/**
 * A type reference.
 */

@Immutable public final class TypeExprReference implements TypeExprType
{
  private final TypeName                         name;
  private final Optional<PackageNameUnqualified> package_name;

  /**
   * Construct a type reference.
   *
   * @param in_package_name A package qualifier, if any
   * @param in_name         The type name
   */

  public TypeExprReference(
    final Optional<PackageNameUnqualified> in_package_name,
    final TypeName in_name)
  {
    this.name = NullCheck.notNull(in_name);
    this.package_name = NullCheck.notNull(in_package_name);
  }

  /**
   * @return The type name
   */

  public TypeName getName()
  {
    return this.name;
  }

  /**
   * @return The package qualifier, if any
   */

  public Optional<PackageNameUnqualified> getPackageName()
  {
    return this.package_name;
  }

  @Override public <A, E extends Exception> A matchTypeExpression(
    final TypeExprMatcherType<A, E> m)
    throws E
  {
    return m.matchReference(this);
  }
}
