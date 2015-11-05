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

public final class TypeExprVector<I> implements TypeExprType<I>
{
  private final Optional<ImmutableLexicalPositionType<Path>> lex;
  private final SizeExprType<I>                              element_count;
  private final TypeExprType<I>                              element_type;
  private final I                                            data;

  public TypeExprVector(
    final I in_data,
    final Optional<ImmutableLexicalPositionType<Path>> in_lex,
    final SizeExprType<I> in_element_count,
    final TypeExprType<I> in_element_type)
  {
    this.lex = NullCheck.notNull(in_lex);
    this.element_count = NullCheck.notNull(in_element_count);
    this.element_type = NullCheck.notNull(in_element_type);
    this.data = NullCheck.notNull(in_data);
  }

  @Override public I getData()
  {
    return this.data;
  }

  @Override public <A, E extends Exception> A matchType(
    final TypeExprMatcherType<I, A, E> m)
    throws E
  {
    return m.matchExprVector(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.lex;
  }

  public SizeExprType<I> getElementCount()
  {
    return this.element_count;
  }

  public TypeExprType<I> getElementType()
  {
    return this.element_type;
  }
}
