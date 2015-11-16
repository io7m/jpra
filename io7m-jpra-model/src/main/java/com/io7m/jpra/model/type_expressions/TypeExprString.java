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
import com.io7m.jpra.model.size_expressions.SizeExprType;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A {@code string} type expression.
 *
 * @param <I> The type of identifiers
 * @param <T> The type of evaluated types
 */

public final class TypeExprString<I, T> implements TypeExprType<I, T>
{
  private final Optional<ImmutableLexicalPositionType<Path>> lex;
  private final SizeExprType<I, T>                           size;
  private final String                                       encoding;
  private final T                                            type;

  /**
   * Construct an expression.
   *
   * @param in_type     The expression type
   * @param in_lex      Lexical information
   * @param in_size     The number of octets
   * @param in_encoding The string encoding
   */

  public TypeExprString(
    final T in_type,
    final Optional<ImmutableLexicalPositionType<Path>> in_lex,
    final SizeExprType<I, T> in_size,
    final String in_encoding)
  {
    this.type = NullCheck.notNull(in_type);
    this.lex = NullCheck.notNull(in_lex);
    this.size = NullCheck.notNull(in_size);
    this.encoding = NullCheck.notNull(in_encoding);
  }

  @Override public T getType()
  {
    return this.type;
  }

  /**
   * @return The size expression denoting the maximum length of the string in
   * octets
   */

  public SizeExprType<I, T> getSize()
  {
    return this.size;
  }

  /**
   * @return The string encoding
   */

  public String getEncoding()
  {
    return this.encoding;
  }

  @Override public <A, E extends Exception> A matchType(
    final TypeExprMatcherType<I, T, A, E> m)
    throws E
  {
    return m.matchExprString(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.lex;
  }
}
