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

package com.io7m.jpra.model.type_expressions;

import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.names.TypeName;

import java.nio.file.Path;
import java.util.Optional;

/**
 * An unqualified reference to a type.
 *
 * Example: {@code T}
 */

public final class TypeExprNameT implements TypeExprNameType
{
  private final TypeName name;

  /**
   * Construct a reference.
   *
   * @param in_name The field name
   */

  public TypeExprNameT(
    final TypeName in_name)
  {
    this.name = NullCheck.notNull(in_name);
  }

  /**
   * @return The type name
   */

  public TypeName getName()
  {
    return this.name;
  }

  @Override public <A, E extends Exception> A matchTypeExpression(
    final TypeExprMatcherType<A, E> m)
    throws E
  {
    return m.matchReference(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.name.getLexicalInformation();
  }

  @Override public <A, E extends Exception> A matchTypeNameExpression(
    final TypeExprNameMatcherType<A, E> m)
    throws E
  {
    return m.matchNameT(this);
  }
}
