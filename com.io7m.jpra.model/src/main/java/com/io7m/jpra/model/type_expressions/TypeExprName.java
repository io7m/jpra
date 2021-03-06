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

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.model.names.TypeReference;

import java.net.URI;
import java.util.Objects;

/**
 * A name.
 *
 * @param <I> The type of identifiers
 * @param <T> The type of evaluated types
 */

public final class TypeExprName<I, T> implements TypeExprType<I, T>
{
  private final I identifier;
  private final TypeReference ref;
  private final T type;

  /**
   * Construct an expression.
   *
   * @param in_identifier The identifier
   * @param in_type       The expression type
   * @param in_ref        The type reference
   */

  public TypeExprName(
    final I in_identifier,
    final T in_type,
    final TypeReference in_ref)
  {
    this.type = Objects.requireNonNull(in_type, "Type");
    this.identifier = Objects.requireNonNull(in_identifier, "Identifier");
    this.ref = Objects.requireNonNull(in_ref, "Ref");
  }

  @Override
  public T getType()
  {
    return this.type;
  }

  /**
   * @return The type reference
   */

  public TypeReference getReference()
  {
    return this.ref;
  }

  /**
   * @return The identifier
   */

  public I getIdentifier()
  {
    return this.identifier;
  }

  @Override
  public <A, E extends Exception> A matchType(
    final TypeExprMatcherType<I, T, A, E> m)
    throws E
  {
    return m.matchName(this);
  }

  @Override
  public LexicalPosition<URI> lexical()
  {
    return this.ref.type().lexical();
  }
}
