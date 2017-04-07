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

package com.io7m.jpra.model.size_expressions;

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.type_expressions.TypeExprType;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A size function denoting the size in bits of a given type expression.
 *
 * @param <I> The type of identifiers
 * @param <T> The type of type information
 */

@Immutable
public final class SizeExprInBits<I, T> implements SizeExprType<I, T>
{
  private final TypeExprType<I, T> expression;

  /**
   * Construct an expression.
   *
   * @param in_expression The type expression
   */

  public SizeExprInBits(
    final TypeExprType<I, T> in_expression)
  {
    this.expression = NullCheck.notNull(in_expression);
  }

  /**
   * @return The type expression
   */

  public TypeExprType<I, T> getTypeExpression()
  {
    return this.expression;
  }

  @Override
  public <A, E extends Exception> A matchSizeExpression(
    final SizeExprMatcherType<I, T, A, E> m)
    throws E
  {
    return m.matchInBits(this);
  }

  @Override
  public Optional<LexicalPosition<Path>> getLexicalInformation()
  {
    return this.expression.getLexicalInformation();
  }
}
