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
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.size_expressions.SizeExprType;
import io.vavr.collection.List;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@code boolean-set} type expression.
 *
 * @param <I> The type of identifiers
 * @param <T> The type of type information
 */

public final class TypeExprBooleanSet<I, T> implements TypeExprType<I, T>
{
  private final SizeExprType<I, T> size;
  private final List<FieldName> fields;
  private final Optional<LexicalPosition<Path>> lex;
  private final T type;

  /**
   * Construct a type expression.
   *
   * @param in_type   The type
   * @param in_lex    Lexical information
   * @param in_fields The fields of the set
   * @param in_size   The size in octets that will be used
   */

  public TypeExprBooleanSet(
    final T in_type,
    final Optional<LexicalPosition<Path>> in_lex,
    final List<FieldName> in_fields,
    final SizeExprType<I, T> in_size)
  {
    this.type = Objects.requireNonNull(in_type, "Type");
    this.lex = Objects.requireNonNull(in_lex, "Lexical information");
    this.fields = Objects.requireNonNull(in_fields, "Fields");
    this.size = Objects.requireNonNull(in_size, "Size");
  }

  /**
   * @return The fields in declaration order
   */

  public List<FieldName> getFieldsInDeclarationOrder()
  {
    return this.fields;
  }

  /**
   * @return The size expression denoting the size in octets
   */

  public SizeExprType<I, T> getSizeExpression()
  {
    return this.size;
  }

  @Override
  public T getType()
  {
    return this.type;
  }

  @Override
  public <A, E extends Exception> A matchType(
    final TypeExprMatcherType<I, T, A, E> m)
    throws E
  {
    return m.matchBooleanSet(this);
  }

  @Override
  public Optional<LexicalPosition<Path>> lexical()
  {
    return this.lex;
  }
}
