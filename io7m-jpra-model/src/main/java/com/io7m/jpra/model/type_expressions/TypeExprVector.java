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

import java.nio.file.Path;
import java.util.Optional;

/**
 * @param <I>  The type of identifiers
 * @param <T>  The type of types
 * @param <S>  The type of sizes (in bits)
 */

public final class TypeExprVector<I, T, S>
  implements TypeExprType<I, T, S>
{
  private final Optional<ImmutableLexicalPositionType<Path>> lex;
  private final T                                            type;
  private final S                                            element_count;
  private final TypeExprType<I, T, S>                element_type;

  public TypeExprVector(
    final Optional<ImmutableLexicalPositionType<Path>> in_lex,
    final T in_type,
    final S in_element_count,
    final TypeExprType<I, T, S> in_element_type)
  {
    this.lex = NullCheck.notNull(in_lex);
    this.type = NullCheck.notNull(in_type);
    this.element_count = NullCheck.notNull(in_element_count);
    this.element_type = NullCheck.notNull(in_element_type);
  }

  @Override public <A, E extends Exception> A matchType(
    final TypeExprMatcherType<I, T, S, A, E> m)
    throws E
  {
    return m.matchExprVector(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.lex;
  }
}
