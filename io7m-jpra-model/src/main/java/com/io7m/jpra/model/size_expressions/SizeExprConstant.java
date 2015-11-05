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
import net.jcip.annotations.Immutable;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Optional;

/**
 * A constant size.
 */

@Immutable public final class SizeExprConstant<I> implements SizeExprType<I>
{
  private final BigInteger                                   value;
  private final Optional<ImmutableLexicalPositionType<Path>> lex;

  /**
   * Construct a size expression.
   *
   * @param in_lex  The lexical information for the name
   * @param in_size The size expression
   *
   * @return A new size expression
   */

  public SizeExprConstant(
    final Optional<ImmutableLexicalPositionType<Path>> in_lex,
    final BigInteger in_size)
  {
    this.lex = NullCheck.notNull(in_lex);
    this.value = NullCheck.notNull(in_size);
  }

  /**
   * @return The size value
   */

  public BigInteger getValue()
  {
    return this.value;
  }

  @Override public <A, E extends Exception> A matchSizeExpression(
    final SizeExprMatcherType<I, A, E> m)
    throws E
  {
    return m.matchConstant(this);
  }

  @Override public String toString()
  {
    return this.value.toString();
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.lex;
  }
}
