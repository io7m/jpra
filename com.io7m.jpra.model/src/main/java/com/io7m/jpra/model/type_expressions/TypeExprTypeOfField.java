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
import com.io7m.jpra.model.names.FieldPath;
import com.io7m.jpra.model.names.FieldReference;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@code type-of} type expression.
 *
 * @param <I> The type of identifiers
 * @param <T> The type of evaluated types
 */

public final class TypeExprTypeOfField<I, T> implements TypeExprType<I, T>
{
  private final I identifier;
  private final FieldReference field_reference;
  private final T type;

  /**
   * Construct an expression.
   *
   * @param in_identifier      The identifier
   * @param in_type            The expression type
   * @param in_field_reference The reference to the field
   */

  public TypeExprTypeOfField(
    final I in_identifier,
    final T in_type,
    final FieldReference in_field_reference)
  {
    this.field_reference = Objects.requireNonNull(
      in_field_reference,
      "Field reference");
    this.identifier = Objects.requireNonNull(in_identifier, "Identifier");
    this.type = Objects.requireNonNull(in_type, "Type");
  }

  @Override
  public T getType()
  {
    return this.type;
  }

  /**
   * @return The field reference
   */

  public FieldReference getFieldReference()
  {
    return this.field_reference;
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
    return m.matchTypeOfField(this);
  }

  @Override
  public Optional<LexicalPosition<Path>> lexical()
  {
    final FieldPath path = this.field_reference.getFieldPath();
    return path.getElements().get(0).lexical();
  }
}
