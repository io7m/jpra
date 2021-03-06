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
import com.io7m.jpra.model.size_expressions.SizeExprType;

import java.net.URI;
import java.util.Objects;

/**
 * A {@code matrix} type expression.
 *
 * @param <I> The type of identifiers
 * @param <T> The type of evaluated types
 */

public final class TypeExprMatrix<I, T> implements TypeExprType<I, T>
{
  private final LexicalPosition<URI> lex;
  private final SizeExprType<I, T> width;
  private final SizeExprType<I, T> height;
  private final TypeExprType<I, T> element_type;
  private final T type;

  /**
   * Construct an expression.
   *
   * @param in_type         The expression type
   * @param in_lex          Lexical information
   * @param in_width        The number of columns
   * @param in_height       The number of rows
   * @param in_element_type The type of elements
   */

  public TypeExprMatrix(
    final T in_type,
    final LexicalPosition<URI> in_lex,
    final SizeExprType<I, T> in_width,
    final SizeExprType<I, T> in_height,
    final TypeExprType<I, T> in_element_type)
  {
    this.type = Objects.requireNonNull(in_type, "Type");
    this.lex = Objects.requireNonNull(in_lex, "Lexical information");
    this.width = Objects.requireNonNull(in_width, "Width");
    this.height = Objects.requireNonNull(in_height, "Height");
    this.element_type = Objects.requireNonNull(in_element_type, "Element type");
  }

  @Override
  public T getType()
  {
    return this.type;
  }

  /**
   * @return A size expression denoting the number of rows
   */

  public SizeExprType<I, T> getHeight()
  {
    return this.height;
  }

  /**
   * @return A type expression denoting the type of elements
   */

  public TypeExprType<I, T> getElementType()
  {
    return this.element_type;
  }

  /**
   * @return A size expression denoting the number of columns
   */

  public SizeExprType<I, T> getWidth()
  {
    return this.width;
  }

  @Override
  public <A, E extends Exception> A matchType(
    final TypeExprMatcherType<I, T, A, E> m)
    throws E
  {
    return m.matchExprMatrix(this);
  }

  @Override
  public LexicalPosition<URI> lexical()
  {
    return this.lex;
  }
}
