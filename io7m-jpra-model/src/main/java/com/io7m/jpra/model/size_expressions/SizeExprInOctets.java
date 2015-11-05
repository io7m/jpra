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

import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.type_expressions.TypeExprType;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A size function denoting the size in octets of a given type expression.
 */

@Immutable public final class SizeExprInOctets<I> implements SizeExprType<I>
{
  private final TypeExprType<I> expression;

  /**
   * Construct an expression.
   *
   * @param in_expression The type expression
   */

  public SizeExprInOctets(
    final TypeExprType<I> in_expression)
  {
    this.expression = NullCheck.notNull(in_expression);
  }

  /**
   * @return The type expression
   */

  public TypeExprType<I> getTypeExpression()
  {
    return this.expression;
  }

  @Override public <A, E extends Exception> A matchSizeExpression(
    final SizeExprMatcherType<I, A, E> m)
    throws E
  {
    return m.matchInOctets(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.expression.getLexicalInformation();
  }
}
