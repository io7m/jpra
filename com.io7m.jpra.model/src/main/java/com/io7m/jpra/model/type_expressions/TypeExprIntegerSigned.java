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
 * An {@code integer signed} type expression.
 *
 * @param <I> The type of identifiers
 * @param <T> The type of type information
 */

public final class TypeExprIntegerSigned<I, T> implements TypeExprType<I, T>
{
  private final LexicalPosition<URI> lex;
  private final SizeExprType<I, T> size;
  private final T type;

  /**
   * Construct an expression.
   *
   * @param in_type The expression type
   * @param in_lex  Lexical information
   * @param in_size The size in bits
   */

  public TypeExprIntegerSigned(
    final T in_type,
    final LexicalPosition<URI> in_lex,
    final SizeExprType<I, T> in_size)
  {
    this.lex = Objects.requireNonNull(in_lex, "Lexical information");
    this.size = Objects.requireNonNull(in_size, "Size");
    this.type = Objects.requireNonNull(in_type, "Type");
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
    return m.matchExprIntegerSigned(this);
  }

  @Override
  public LexicalPosition<URI> lexical()
  {
    return this.lex;
  }

  /**
   * @return The size expression denoting the size in bits
   */

  public SizeExprType<I, T> getSize()
  {
    return this.size;
  }
}
