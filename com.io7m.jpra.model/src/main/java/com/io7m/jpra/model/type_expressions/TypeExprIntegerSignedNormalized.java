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
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.size_expressions.SizeExprType;

import java.nio.file.Path;
import java.util.Optional;

/**
 * An {@code integer signed-normalized} type expression.
 *
 * @param <I> The type of identifiers
 * @param <T> The type of type information
 */

public final class TypeExprIntegerSignedNormalized<I, T>
  implements TypeExprType<I, T>
{
  private final Optional<LexicalPosition<Path>> lex;
  private final SizeExprType<I, T> size;
  private final T type;

  /**
   * Construct an expression.
   *
   * @param in_type The expression type
   * @param in_lex  Lexical information
   * @param in_size The size in bits
   */

  public TypeExprIntegerSignedNormalized(
    final T in_type,
    final Optional<LexicalPosition<Path>> in_lex,
    final SizeExprType<I, T> in_size)
  {
    this.type = NullCheck.notNull(in_type);
    this.lex = NullCheck.notNull(in_lex);
    this.size = NullCheck.notNull(in_size);
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
    return m.matchExprIntegerSignedNormalized(this);
  }

  @Override
  public Optional<LexicalPosition<Path>> getLexicalInformation()
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
